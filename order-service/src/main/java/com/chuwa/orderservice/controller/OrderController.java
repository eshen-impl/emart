package com.chuwa.orderservice.controller;

import com.chuwa.orderservice.payload.CreateOrderRequestDTO;
import com.chuwa.orderservice.payload.OrderDTO;
import com.chuwa.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Submit a new order for all the added items in cart. ",
            description = "Return details of this order. " + "Required to be authenticated (have signed in) ")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequestDTO createOrderRequestDTO, Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        return ResponseEntity.ok(orderService.createOrder(userId, createOrderRequestDTO));
    }

    @PutMapping("/cancel/{orderId}")
    @PostMapping("/create")
    @Operation(summary = "Cancel an existing order.",
            description = "Order status changed to be cancelled. Payment refund functionality WIP. "
                    + "Required to be authenticated (have signed in)")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @PutMapping("/update/{orderId}")
    @Operation(summary = "Update an existing order's total amount and items. ",
            description = "Currently items need to be in JSON string format. "
                    + "Required to be authenticated (have signed in)")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable("orderId") UUID orderId, @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, orderDTO));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/complete/{orderId}")
    @Operation(summary = "Set status to completed for an existing order. ",
            description = "Required to have role 'ROLE_ADMIN'")
    public ResponseEntity<OrderDTO> completeOrder(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity.ok(orderService.completeOrder(orderId));
    }

    @GetMapping("/history")
    @Operation(summary = "Get all history orders and their details for the current user. ",
            description =  "Required to be authenticated (have signed in) ")
    public ResponseEntity<List<OrderDTO>> getUserOrders(Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get a specific order and its details for the current user.",
            description =  "Required to be authenticated (have signed in) ")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }
}

