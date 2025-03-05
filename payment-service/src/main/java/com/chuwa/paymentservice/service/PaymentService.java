package com.chuwa.paymentservice.service;

import com.chuwa.paymentservice.entity.Payment;
import com.chuwa.paymentservice.payload.RefundRequestDTO;
import com.chuwa.paymentservice.payload.ShippingEvent;
import com.chuwa.paymentservice.payload.ValidatePaymentRequestDTO;

import java.util.Map;
import java.util.UUID;

public interface PaymentService {
    Map<String, String> validatePayment(UUID userId, ValidatePaymentRequestDTO validatePaymentRequestDTO);
    void cancelAuthorization(UUID transactionKey);

    void initiateRefund(RefundRequestDTO refundRequest);

    Payment findPayment(UUID transactionKey);
    void processShippingResponse(ShippingEvent event);
}
