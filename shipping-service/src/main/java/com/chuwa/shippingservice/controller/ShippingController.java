package com.chuwa.shippingservice.controller;

import com.chuwa.shippingservice.enums.ShippingEventType;
import com.chuwa.shippingservice.payload.OrderEvent;
import com.chuwa.shippingservice.payload.ShippingEvent;
import com.chuwa.shippingservice.producer.ShippingEventProducer;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipping")
public class ShippingController {

    private final ShippingEventProducer shippingEventProducer;

    public ShippingController(ShippingEventProducer shippingEventProducer) {
        this.shippingEventProducer = shippingEventProducer;
    }

    @GetMapping("/ready-to-ship")
    @Operation(summary = "Simulate the process of sending ready-to-ship message to payment service.")
    public ResponseEntity<String> readyToShip(@RequestParam("orderId") UUID orderId) {
        shippingEventProducer.sendToPayment(new ShippingEvent(ShippingEventType.READY_TO_SHIP, null, orderId));
        return ResponseEntity.ok("Ready to ship!");
    }

    @GetMapping("/delivered")
    @Operation(summary = "Simulate the process of sending order delivered message to order service.")
    public ResponseEntity<String> onDelivery(@RequestParam("orderId") UUID orderId) {
        shippingEventProducer.sendToOrder(new ShippingEvent(ShippingEventType.DELIVERED, null, orderId));
        return ResponseEntity.ok("Order delivered!");
    }

}
