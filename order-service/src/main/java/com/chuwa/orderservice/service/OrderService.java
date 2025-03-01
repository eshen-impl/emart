package com.chuwa.orderservice.service;

import com.chuwa.orderservice.entity.PaymentEvent;
import com.chuwa.orderservice.payload.OrderDTO;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDTO createOrder(UUID userId);
    OrderDTO updateOrder(UUID orderId, OrderDTO orderDTO);
    OrderDTO cancelOrder(UUID orderId);
    List<OrderDTO> getUserOrders(UUID userId);
    OrderDTO getOrderById(UUID orderId);
    OrderDTO completeOrder(UUID orderId);
    void processPaymentResponse(PaymentEvent response);
}
