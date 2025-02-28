package com.chuwa.orderservice.controller;

import com.chuwa.orderservice.payload.OrderDTO;
import com.chuwa.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        orderDTO.setUserId(userId);
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @PutMapping("/update/{orderId}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable("orderId") UUID orderId, @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, orderDTO));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/complete/{orderId}")
    public ResponseEntity<OrderDTO> completeOrder(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity.ok(orderService.completeOrder(orderId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderDTO>> getUserOrders(Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }
}

