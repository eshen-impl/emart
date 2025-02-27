package com.chuwa.cartservice.service;

import com.chuwa.cartservice.payload.CartDTO;
import com.chuwa.cartservice.payload.CartItemRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface CartService {
    ResponseEntity<Map<String, String>> addItem(CartItemRequest request, HttpServletRequest httpRequest);
    void removeItem(CartItemRequest request, HttpServletRequest httpRequest);
    CartDTO getCart(CartItemRequest request, HttpServletRequest httpRequest);
    void clearCart(CartItemRequest request, HttpServletRequest httpRequest);

    void migrateCart(CartItemRequest request, HttpServletRequest httpRequest);
}
