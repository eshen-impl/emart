package com.chuwa.orderservice.service.impl;

import com.chuwa.orderservice.dao.OrderByUserRepository;
import com.chuwa.orderservice.dao.OrderRepository;
import com.chuwa.orderservice.entity.*;
import com.chuwa.orderservice.exception.ResourceNotFoundException;
import com.chuwa.orderservice.payload.CartItem;
import com.chuwa.orderservice.payload.OrderDTO;
import com.chuwa.orderservice.service.OrderService;
import com.chuwa.orderservice.util.CartRedisUtil;
import com.chuwa.orderservice.util.JsonUtil;
import com.chuwa.orderservice.util.UUIDUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderByUserRepository orderByUserRepository;
    private final CartRedisUtil cartRedisUtil;

    public OrderServiceImpl(OrderRepository orderRepository, OrderByUserRepository orderByUserRepository, CartRedisUtil cartRedisUtil) {
        this.orderRepository = orderRepository;
        this.orderByUserRepository = orderByUserRepository;
        this.cartRedisUtil = cartRedisUtil;
    }

    public OrderDTO createOrder(OrderDTO orderDTO) {
        UUID orderId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        String cartKey = "cart:"+ UUIDUtil.encodeUUID(orderDTO.getUserId());
        List<CartItem> items = cartRedisUtil.getCartItems(cartKey);
        if (items.isEmpty()) throw new ResourceNotFoundException("Nothing in your cart yet. Please add something first!");
        String itemsJson = JsonUtil.toJson(items);
        double totalAmount = items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();

        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(orderDTO.getUserId());
        order.setOrderStatus("Created");
        order.setTotalAmount(BigDecimal.valueOf(totalAmount));
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        order.setItems(itemsJson);
        order.setPaymentStatus("Pending");


        orderRepository.save(order);
        orderByUserRepository.save(convertToOrderByUser(order));

        cartRedisUtil.clearCart(cartKey);
//        paymentServiceClient.initiatePayment(order, order.getTotalAmount());
//        orderRepository.save(order);

        return convertToDTO(order);
    }

    public OrderDTO updateOrder(UUID orderId, OrderDTO orderDTO) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setItems(orderDTO.getItems());
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);
        orderByUserRepository.save(convertToOrderByUser(order));

        return convertToDTO(order);
    }


    public OrderDTO cancelOrder(UUID orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));



//        if (order.getOrderStatus().equals(OrderStatus.PAID.toString())) {
            order.setOrderStatus(OrderStatus.CANCELED.toString());
            order.setPaymentStatus(PaymentStatus.PENDING.toString());
//            paymentServiceClient.initiateRefund(order, order.getTotalAmount());
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
//        } else {
//            throw new RuntimeException("Order cannot be canceled");
//        }


        orderRepository.save(order);
        orderByUserRepository.save(convertToOrderByUser(order));

        return convertToDTO(order);
    }

    public List<OrderDTO> getUserOrders(UUID userId) {
        List<OrderByUser> orders = orderByUserRepository.findByUserId(userId);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public OrderDTO getOrderById(UUID orderId) {
        return orderRepository.findByOrderId(orderId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public OrderDTO completeOrder(UUID orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!"Paid".equals(order.getPaymentStatus())) {
            throw new IllegalStateException("Order must be paid before completing.");
        }

        order.setOrderStatus("Completed");
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);
        orderByUserRepository.save(convertToOrderByUser(order));

        return convertToDTO(order);
    }

    @Override
    public void processPaymentResponse(PaymentEvent event) {
        UUID orderId = event.getOrderId();
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (orderOpt.isEmpty()) return;

        Order order = orderOpt.get();
        if (event.getEventType().equals("payment_succeed")) {
            order.setOrderStatus(OrderStatus.PAID.toString());
            order.setPaymentStatus(PaymentStatus.COMPLETED.toString());
        } else if (event.getEventType().equals("payment_authorization_failed")) {
            order.setOrderStatus(OrderStatus.FAILED.toString());
            order.setPaymentStatus(PaymentStatus.FAILED.toString());
        } else if (event.getEventType().equals("payment_refunded")) {
            order.setOrderStatus(OrderStatus.CANCELED.toString());
            order.setPaymentStatus(PaymentStatus.REFUNDED.toString());
        }
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }




    private OrderDTO convertToDTO(Order order) {
        return new OrderDTO(order.getOrderId(), order.getUserId(), order.getOrderStatus(),
                order.getTotalAmount(), order.getCreatedAt(), order.getUpdatedAt(),
                order.getItems(), order.getPaymentStatus());
    }

    private OrderDTO convertToDTO(OrderByUser order) {
        return new OrderDTO(order.getOrderId(), order.getUserId(), order.getOrderStatus(),
                order.getTotalAmount(), order.getCreatedAt(), order.getUpdatedAt(),
                order.getItems(), order.getPaymentStatus());
    }

    private OrderByUser convertToOrderByUser(Order order) {
        return new OrderByUser(order.getUserId(), order.getCreatedAt(), order.getOrderId(),
                order.getOrderStatus(), order.getTotalAmount(), order.getUpdatedAt(),
                order.getItems(), order.getPaymentStatus());
    }
}
