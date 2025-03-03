package com.chuwa.paymentservice.controller;

import com.chuwa.paymentservice.entity.Payment;
import com.chuwa.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/validate")
    public ResponseEntity<Payment> validatePayment(@RequestBody ) {
        return ResponseEntity.ok(paymentService.validatePayment(orderId, userId, paymentMethodId, amount));
    }

    @PostMapping("/process/{orderId}")
    public ResponseEntity<Payment> processPayment(@PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentService.processPayment(orderId));
    }

    @PostMapping("/fail/{orderId}")
    public ResponseEntity<String> markPaymentFailed(@PathVariable UUID orderId) {
        paymentService.markPaymentFailed(orderId);
        return ResponseEntity.ok("Payment marked as failed");
    }

    @PostMapping("/refund/{orderId}")
    public ResponseEntity<Payment> initiateRefund(@PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentService.initiateRefund(orderId));
    }

    @PostMapping("/refund/complete/{orderId}")
    public ResponseEntity<Payment> completeRefund(@PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentService.completeRefund(orderId));
    }
}

