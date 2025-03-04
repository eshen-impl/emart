package com.chuwa.paymentservice.service.impl;


import com.chuwa.paymentservice.client.AccountClient;
import com.chuwa.paymentservice.entity.Payment;
import com.chuwa.paymentservice.entity.ShippingEvent;
import com.chuwa.paymentservice.enums.PaymentRefundStatus;
import com.chuwa.paymentservice.enums.PaymentStatus;
import com.chuwa.paymentservice.dao.PaymentRepository;
import com.chuwa.paymentservice.enums.ShippingEventType;
import com.chuwa.paymentservice.exception.ResourceNotFoundException;
import com.chuwa.paymentservice.payload.PaymentMethodDTO;
import com.chuwa.paymentservice.payload.RefundRequestDTO;
import com.chuwa.paymentservice.payload.ValidatePaymentRequestDTO;
import com.chuwa.paymentservice.service.PaymentService;
import com.chuwa.paymentservice.util.JsonUtil;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final AccountClient accountClient;
    private final ThreadPoolTaskScheduler taskScheduler;

    public PaymentServiceImpl(PaymentRepository paymentRepository, AccountClient accountClient, ThreadPoolTaskScheduler taskScheduler) {
        this.paymentRepository = paymentRepository;
        this.accountClient = accountClient;
        this.taskScheduler = taskScheduler;
    }

    @Transactional
    @CircuitBreaker(name = "validatePayment", fallbackMethod = "fallbackForValidatePayment")
    public Map<String, String> validatePayment(UUID userId, ValidatePaymentRequestDTO validateRequest) {
        Map<String, String> res = new HashMap<>();
        Optional<Payment> existingPayment = paymentRepository.findByTransactionKey(validateRequest.getTransactionKey());
        if (existingPayment.isPresent()) {
            res.put("isValid", existingPayment.get().getStatus() == PaymentStatus.VALIDATED ? "true" : "false");
            res.put("reason", "existing transaction");
            return res;
        }

        //new transaction key but existing order -> updating order scenario
        paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(validateRequest.getOrderId())
                .ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.VOIDED); //void original payment record
                    paymentRepository.save(payment);}
                );

        Payment payment = new Payment();
        PaymentMethodDTO paymentMethod;
        try {
            paymentMethod = accountClient.getPaymentMethod(validateRequest.getPaymentMethodId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Payment Method not found in Account Service");
        } catch (FeignException e) {
            throw new RuntimeException("Error calling Account Service", e);
        }

        boolean isValid = paymentMethod.getExpirationDate().after(new Date()); //Add deep validation in future

        payment.setStatus(isValid ? PaymentStatus.VALIDATED : PaymentStatus.FAILED);
        payment.setOrderId(validateRequest.getOrderId());
        payment.setUserId(userId);
        payment.setPaymentMethodId(validateRequest.getPaymentMethodId());
        payment.setPaymentMethod(JsonUtil.toJson(paymentMethod));
        payment.setAmount(validateRequest.getAmount());
        payment.setCurrency(Currency.getInstance(validateRequest.getCurrency()));
        payment.setTransactionKey(validateRequest.getTransactionKey());

        paymentRepository.save(payment);

        res.put("isValid", String.valueOf(isValid));
        res.put("reason", isValid ? "" : "invalid card");
        return res;
    }

    public Map<String, String> fallbackForValidatePayment(UUID userId, ValidatePaymentRequestDTO validatePaymentRequestDTO, Throwable throwable) {
        throw new RuntimeException("Service is unavailable, please try again later.\n" + throwable.getMessage());
    }

    @Transactional
    public void cancelAuthorization(UUID transactionKey) {
        Payment authorization = paymentRepository.findByTransactionKey(transactionKey).orElseThrow(
                () -> new ResourceNotFoundException("Payment authorization not found"));

        if (authorization.getStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Payment cannot be canceled in status: " + authorization.getStatus());
        }
        authorization.setStatus(PaymentStatus.CANCELED);
    }


    @Transactional
    public void processPayment(UUID transactionKey) {
        paymentRepository.findByTransactionKey(transactionKey)
                .ifPresent((payment) -> {
                    if (payment.getStatus() == PaymentStatus.VALIDATED) {
                        payment.setStatus(PaymentStatus.PAID);

                        //kafka: send payment success
                        paymentRepository.save(payment);
                        return;
                    }
                });

        //payment not found || payment failure
        //kafka send payment failure
    }

    @Transactional
    public void initiateRefund(RefundRequestDTO refundRequest) {
        Payment payment = paymentRepository.findByTransactionKey(refundRequest.getTransactionKey())
                .orElseThrow(() -> new RuntimeException("Payment record not found. "));

        if (payment.getStatus() == PaymentStatus.PAID) {
            payment.setRefundStatus(PaymentRefundStatus.REFUND_REQUESTED);
            paymentRepository.save(payment);
        } else {
            throw new IllegalStateException("Refund cannot be requested in payment status: " + payment.getStatus());
        }

        LOGGER.info("Refund initiated for transactionKey={}, scheduling refund in 10 seconds.", refundRequest.getTransactionKey());

        taskScheduler.schedule(() -> refundPayment(refundRequest), Instant.now().plusSeconds(10));

    }

    @Transactional
    public void refundPayment(RefundRequestDTO refundRequest) {
        paymentRepository.findByTransactionKey(refundRequest.getTransactionKey())
                .ifPresent((payment) -> {
                    if (payment.getStatus() == PaymentStatus.PAID
                            && payment.getRefundStatus() == PaymentRefundStatus.REFUND_REQUESTED) {
                        payment.setRefundStatus(PaymentRefundStatus.REFUNDED);
                        payment.setRefundedAmount(refundRequest.getRequestRefundAmount());
                        //kafka: send refund success
                        paymentRepository.save(payment);
                        return;
                    }
                });

        //payment not found || refund not approved
        //kafka send refund failure
    }

    @Override
    public Payment findPayment(UUID transactionKey) {
        return paymentRepository.findByTransactionKey(transactionKey).orElseThrow(
                () -> new ResourceNotFoundException("Payment not found"));
    }

    public void processShippingResponse(ShippingEvent event) {
        Optional<Payment> paymentOpt = paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(event.getOrderId());
        if (paymentOpt.isEmpty()) return;

        Payment payment = paymentOpt.get();
        if (event.getEventType() == ShippingEventType.READY_TO_SHIP) {
            processPayment(payment.getTransactionKey());
        }

    }

}

