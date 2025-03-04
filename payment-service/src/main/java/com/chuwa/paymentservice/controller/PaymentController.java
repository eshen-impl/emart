package com.chuwa.paymentservice.controller;

import com.chuwa.paymentservice.entity.Payment;
import com.chuwa.paymentservice.payload.RefundRequestDTO;
import com.chuwa.paymentservice.payload.ValidatePaymentRequestDTO;
import com.chuwa.paymentservice.service.PaymentService;
import com.chuwa.paymentservice.service.impl.PaymentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentServiceImpl paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate payment info for new and updated orders.",
            description = "Order service sync call this API to initiate payment. ")
    public ResponseEntity<Map<String, String>> validatePayment(@RequestBody ValidatePaymentRequestDTO validatePaymentRequestDTO, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        Map<String, String> res = paymentService.validatePayment(userId, validatePaymentRequestDTO);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/cancel")
    @Operation(summary = "Cancel a payment authorization before shipping. ",
            description = "Order service sync call this API to cancel payment. ")
    public ResponseEntity<String> cancelPayment(@RequestParam("transactionKey") UUID transactionKey) {
        paymentService.cancelAuthorization(transactionKey);
        return ResponseEntity.ok("Payment authorization canceled successfully!");
    }

    @PutMapping("/refund")
    @Operation(summary = "Initiate a refund process after delivery. ",
            description = "Order service sync call this API to request refund. ")
    public ResponseEntity<String> initiateRefund(@RequestBody RefundRequestDTO refundRequest) {
        paymentService.initiateRefund(refundRequest);
        return ResponseEntity.ok("Refund requested successfully!");
    }

    @GetMapping
    @Operation(summary = "Find a payment record for the specified order. ",
            description = "Return only the latest payment (not voided)")
    public ResponseEntity<Payment> findOrderPayment(@RequestParam("transactionKey") UUID transactionKey) {
        return ResponseEntity.ok(paymentService.findPayment(transactionKey));
    }


}

