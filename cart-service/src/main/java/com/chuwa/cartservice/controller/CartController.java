package com.chuwa.cartservice.controller;

import com.chuwa.cartservice.entity.CartItem;
import com.chuwa.cartservice.payload.CartDTO;
import com.chuwa.cartservice.payload.CartItemRequest;
import com.chuwa.cartservice.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addItemToCart(@RequestBody CartItemRequest request, HttpServletRequest httpRequest) {
        return cartService.addItem(request, httpRequest);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeItemFromCart(@RequestBody CartItemRequest request, HttpServletRequest httpRequest) {
        cartService.removeItem(request, httpRequest);
        return ResponseEntity.ok("Item removed from cart");
    }

    @PostMapping("/list")
    public ResponseEntity<CartDTO> getCart(@RequestBody CartItemRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(cartService.getCart(request, httpRequest));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@RequestBody CartItemRequest request, HttpServletRequest httpRequest) {
        cartService.clearCart(request, httpRequest);
        return ResponseEntity.ok("Cart cleared");
    }

    @PostMapping("/migrate")
    public ResponseEntity<String> migrateCart(@RequestBody CartItemRequest request, HttpServletRequest httpRequest) {
        cartService.migrateCart(request,httpRequest);
        return ResponseEntity.ok("Cart migrated successfully");
    }
}
