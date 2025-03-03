package com.chuwa.paymentservice.service;


import com.chuwa.paymentservice.entity.Payment;
import com.chuwa.paymentservice.entity.PaymentStatus;
import com.chuwa.paymentservice.dao.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment validatePayment(UUID orderId, UUID userId, String paymentMethodId, double amount, String transactionKey) {
        Optional<Payment> existingPayment = paymentRepository.findByTransactionKey(transactionKey);
        if (existingPayment.isPresent()) {
            return existingPayment.get();
        }
        boolean isValid = validateCard(paymentMethodId);

        Payment payment = Payment.builder()
                .orderId(orderId)
                .userId(userId)
                .paymentMethodId(paymentMethodId)
                .amount(amount)
                .status(isValid ? PaymentStatus.VALIDATED : PaymentStatus.FAILED)
                .transactionKey(transactionKey)
                .build();

        return paymentRepository.save(payment);
    }


    @Transactional
    public void markPaymentFailed(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment record not found for order " + orderId));

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }

    @Transactional
    public Payment processPayment(UUID orderId, String transactionKey) {

        Optional<Payment> existingPayment = paymentRepository.findByTransactionKey(transactionKey);
        if (existingPayment.isPresent()) {
            return existingPayment.get();
        }
        Optional<Payment> optionalPayment = paymentRepository.findByOrderId(orderId);

        if (optionalPayment.isEmpty()) {
            throw new RuntimeException("Payment not found for Order ID: " + orderId);
        }

        Payment payment = optionalPayment.get();

        if (payment.getStatus() == PaymentStatus.VALIDATED) {
            payment.setStatus(PaymentStatus.PAID);
            return paymentRepository.save(payment);
        } else {
            throw new RuntimeException("Cannot process payment, status: " + payment.getStatus());
        }
    }

    @Transactional
    public Payment initiateRefund(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment record not found for order " + orderId));

        if (payment.getStatus() == PaymentStatus.PAID) {
            payment.setStatus(PaymentStatus.REFUND_REQUESTED);
            return paymentRepository.save(payment);
        }

        throw new RuntimeException("Refund cannot be initiated from status: " + payment.getStatus());
    }


    @Transactional
    public Payment refundPayment(UUID orderId) {
        Optional<Payment> optionalPayment = paymentRepository.findByOrderId(orderId);

        if (optionalPayment.isEmpty()) {
            throw new RuntimeException("Payment not found for Order ID: " + orderId);
        }

        Payment payment = optionalPayment.get();

        if (payment.getStatus() == PaymentStatus.PAID) {
            payment.setStatus(PaymentStatus.REFUNDED);

            return paymentRepository.save(payment);
        } else {
            throw new RuntimeException("Cannot refund payment, status: " + payment.getStatus());
        }
    }

    private boolean validateCard(String paymentMethodId) {
        // Simulate external validation (e.g., call Stripe API)
        return paymentMethodId != null && paymentMethodId.startsWith("pm_");
    }
}

