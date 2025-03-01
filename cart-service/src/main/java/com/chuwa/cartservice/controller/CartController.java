package com.chuwa.cartservice.controller;

import com.chuwa.cartservice.entity.CartItem;
import com.chuwa.cartservice.payload.CartDTO;
import com.chuwa.cartservice.payload.CartItemRequest;
import com.chuwa.cartservice.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Add items to the cart. ",
            description = "Pass in itemId and quantity in the request body. Cart will call item service to retrieve item details. Support CircuitBreaker fallback. "
                    + "For guest with no sessionId, sessionId will be generated and returned when you add a first item to cart; "
                    + "copy and paste it in the request body of the subsequent requests."
                   + "For logged-in users, use Authorize button at the top right page to pass in jwt.")
    public ResponseEntity<Map<String, String>> addItemToCart(@RequestBody CartItemRequest request, HttpServletRequest httpRequest) {
        return cartService.addItem(request, httpRequest);
    }

    @DeleteMapping("/remove")
    @Operation(summary = "Remove items from the cart.",
            description = "Pass in itemId in the request body." + "For guest pass in sessionId.")
    public ResponseEntity<String> removeItemFromCart(@RequestBody CartItemRequest request, HttpServletRequest httpRequest) {
        cartService.removeItem(request, httpRequest);
        return ResponseEntity.ok("Item removed from cart");
    }

    @PostMapping("/list")
    @Operation(summary = "List all added items from the cart.",
            description = "For guest pass in sessionId in the request body.")
    public ResponseEntity<CartDTO> getCart(@RequestBody CartItemRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(cartService.getCart(request, httpRequest));
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear the cart.",
            description = "For guest pass in sessionId in the request body.")
    public ResponseEntity<String> clearCart(@RequestBody CartItemRequest request, HttpServletRequest httpRequest) {
        cartService.clearCart(request, httpRequest);
        return ResponseEntity.ok("Cart cleared");
    }

    @PostMapping("/migrate")
    @Operation(summary = "Migrate cart items from the guest's session to the user's account when the user signs in.",
            description = "Migrate the current cart items from the guest's sessionId key to the logged-in user's userId key in Redis.")
    public ResponseEntity<String> migrateCart(@RequestBody CartItemRequest request, HttpServletRequest httpRequest) {
        cartService.migrateCart(request,httpRequest);
        return ResponseEntity.ok("Cart migrated successfully");
    }
}
