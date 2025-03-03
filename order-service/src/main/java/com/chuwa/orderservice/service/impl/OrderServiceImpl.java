package com.chuwa.orderservice.service.impl;

import com.chuwa.orderservice.client.AccountClient;
import com.chuwa.orderservice.client.ItemClient;
import com.chuwa.orderservice.dao.OrderByUserRepository;
import com.chuwa.orderservice.dao.OrderRepository;
import com.chuwa.orderservice.entity.*;
import com.chuwa.orderservice.enums.OrderStatus;
import com.chuwa.orderservice.enums.PaymentStatus;
import com.chuwa.orderservice.exception.EmptyCartException;
import com.chuwa.orderservice.exception.InsufficientStockException;
import com.chuwa.orderservice.exception.ResourceNotFoundException;
import com.chuwa.orderservice.payload.*;
//import com.chuwa.orderservice.publisher.OrderEventPublisher;
import com.chuwa.orderservice.service.OrderService;
import com.chuwa.orderservice.util.CartRedisUtil;
import com.chuwa.orderservice.util.JsonUtil;
import com.chuwa.orderservice.util.UUIDUtil;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

//    private final OrderEventPublisher orderEventPublisher;
    private final OrderRepository orderRepository;
    private final OrderByUserRepository orderByUserRepository;
    private final CartRedisUtil cartRedisUtil;
    private final ItemClient itemClient;

    private final AccountClient accountClient;


    public OrderServiceImpl(OrderRepository orderRepository, OrderByUserRepository orderByUserRepository, CartRedisUtil cartRedisUtil, ItemClient itemClient, AccountClient accountClient) {
        this.orderRepository = orderRepository;
        this.orderByUserRepository = orderByUserRepository;
        this.cartRedisUtil = cartRedisUtil;
        this.itemClient = itemClient;
        this.accountClient = accountClient;
    }

    @CircuitBreaker(name = "createOrder", fallbackMethod = "fallbackForCreateOrder")
    public OrderDTO createOrder(UUID userId, CreateOrderRequestDTO createOrderRequestDTO) {
        UUID orderId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        String cartKey = "cart:"+ UUIDUtil.encodeUUID(userId);
        List<CartItem> items = cartRedisUtil.getCartItems(cartKey);
        if (items.isEmpty()) throw new EmptyCartException("Nothing in your cart yet. Please add something first!");
        validateOrderItems(items); // check requested units of each item in cart not exceeding available units

        AddressDTO billingAddress;
        AddressDTO shippingAddress;
        PaymentMethodDTO paymentMethod;

        try {
             billingAddress = accountClient.getAddress(createOrderRequestDTO.getBillingAddressId());
             shippingAddress = accountClient.getAddress(createOrderRequestDTO.getShippingAddressId());
             paymentMethod = accountClient.getPaymentMethod(createOrderRequestDTO.getPaymentMethodId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Address or Payment Method not found in Account Service");
        } catch (FeignException e) {
            throw new RuntimeException("Error calling Account Service", e);
        }

        String itemsJson = JsonUtil.toJson(items);
        double totalAmount = items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();

        PaymentMethodDTO paymentMethodForOrder = hideSensitivePaymentMethodInfo(paymentMethod);

        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setOrderStatus(OrderStatus.CREATED);
        order.setTotalAmount(BigDecimal.valueOf(totalAmount));
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        order.setItems(itemsJson);
        order.setBillingAddress(JsonUtil.toJson(billingAddress));
        order.setShippingAddress(JsonUtil.toJson(shippingAddress));
        order.setPaymentMethod(JsonUtil.toJson(paymentMethodForOrder));
        order.setPaymentStatus(PaymentStatus.PENDING);


        orderRepository.save(order);
        orderByUserRepository.save(convertToOrderByUser(order));
//        orderEventPublisher.sendOrderEvent(order); //to other microservices except payment
//        cartRedisUtil.clearCart(cartKey);
//        paymentServiceClient.initiatePayment(order, order.getTotalAmount());
//        orderRepository.save(order);

        return convertToDTO(order);
    }

    public void fallbackForCreateOrder(UUID userId, CreateOrderRequestDTO createOrderRequestDTO, Throwable throwable) {
        throw new RuntimeException("Service is unavailable, please try again later.\n" + throwable.getMessage());
    }

    public OrderDTO updateOrder(UUID orderId, OrderDTO orderDTO) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setItems(orderDTO.getItems());
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);
        orderByUserRepository.save(convertToOrderByUser(order));
//        orderEventPublisher.sendOrderEvent(order);
        return convertToDTO(order);
    }


    public OrderDTO cancelOrder(UUID orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));



//        if (order.getOrderStatus().equals(OrderStatus.PAID.toString())) {
            order.setOrderStatus(OrderStatus.CANCELED);
            order.setPaymentStatus(PaymentStatus.PENDING);
//            paymentServiceClient.initiateRefund(order, order.getTotalAmount());
            order.setUpdatedAt(LocalDateTime.now());

//        } else {
//            throw new RuntimeException("Order cannot be canceled");
//        }


        orderRepository.save(order);
        orderByUserRepository.save(convertToOrderByUser(order));
//        orderEventPublisher.sendOrderEvent(order);
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

        order.setOrderStatus(OrderStatus.DELIVERED);
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
            order.setOrderStatus(OrderStatus.SHIPPED);
            order.setPaymentStatus(PaymentStatus.PAID);
        } else if (event.getEventType().equals("payment_authorization_failed")) {
            order.setOrderStatus(OrderStatus.SUSPENDED);
            order.setPaymentStatus(PaymentStatus.FAILED);
        } else if (event.getEventType().equals("payment_refunded")) {
            order.setOrderStatus(OrderStatus.REFUNDED);
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }


    private void validateOrderItems(List<CartItem> orderItems) {
        List<String> itemIds = orderItems.stream().map(CartItem::getItemId).collect(Collectors.toList());

        Map<String, Integer> availableUnits = itemClient.getAvailableUnits(itemIds);

        List<String> insufficientItems = new ArrayList<>();

        for (CartItem item : orderItems) {
            int requestedQty = item.getQuantity();
            int availableQty = availableUnits.getOrDefault(item.getItemId(), 0);

            if (requestedQty > availableQty) {
                insufficientItems.add("Item: " + item.getItemId() + " (Requested: "
                        + requestedQty + ", Available: " + availableQty + ")");
            }
        }

        if (!insufficientItems.isEmpty()) {
            throw new InsufficientStockException("Insufficient stock for the following items: " + String.join("; ", insufficientItems));
        }
    }


    private PaymentMethodDTO hideSensitivePaymentMethodInfo(PaymentMethodDTO paymentMethodDTO) {
        PaymentMethodDTO paymentMethodForOrder = new PaymentMethodDTO();
        paymentMethodForOrder.setPaymentMethodId(paymentMethodDTO.getPaymentMethodId());
        paymentMethodForOrder.setType(paymentMethodDTO.getType());
        String cardNumber = paymentMethodDTO.getCardNumber();
        if (cardNumber != null && cardNumber.length() > 4) {
            paymentMethodForOrder.setCardNumber("**** **** **** " + cardNumber.substring(cardNumber.length() - 4));
        }
        return paymentMethodForOrder;
    }

    private OrderDTO convertToDTO(Order order) {
        return new OrderDTO(order.getOrderId(), order.getUserId(), order.getOrderStatus(),
                order.getTotalAmount(), order.getCreatedAt(), order.getUpdatedAt(),
                order.getItems(), order.getPaymentStatus(), order.getShippingAddress(),
                order.getBillingAddress(), order.getPaymentMethod());

    }

    private OrderDTO convertToDTO(OrderByUser order) {
        return new OrderDTO(order.getOrderId(), order.getUserId(), order.getOrderStatus(),
                order.getTotalAmount(), order.getCreatedAt(), order.getUpdatedAt(),
                order.getItems(), order.getPaymentStatus(), order.getShippingAddress(),
                order.getBillingAddress(), order.getPaymentMethod());
    }

    private OrderByUser convertToOrderByUser(Order order) {
        return new OrderByUser(order.getUserId(), order.getCreatedAt(), order.getOrderId(),
                order.getOrderStatus(), order.getTotalAmount(), order.getUpdatedAt(),
                order.getItems(), order.getPaymentStatus(), order.getShippingAddress(),
                order.getBillingAddress(), order.getPaymentMethod());
    }
}
