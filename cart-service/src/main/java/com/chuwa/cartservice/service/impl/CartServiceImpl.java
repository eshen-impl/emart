package com.chuwa.cartservice.service.impl;


import com.chuwa.cartservice.client.ItemClient;
import com.chuwa.cartservice.entity.CartItem;
import com.chuwa.cartservice.exception.AccessDeniedException;
import com.chuwa.cartservice.exception.ResourceNotFoundException;
import com.chuwa.cartservice.payload.CartDTO;
import com.chuwa.cartservice.payload.CartItemRequest;
import com.chuwa.cartservice.payload.ItemDTO;
import com.chuwa.cartservice.repository.CartRepository;
import com.chuwa.cartservice.service.CartService;
import com.chuwa.cartservice.util.JwtUtil;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ItemClient itemClient;

    public CartServiceImpl(CartRepository cartRepository, ItemClient itemClient) {
        this.cartRepository = cartRepository;
        this.itemClient = itemClient;
    }

    @CircuitBreaker(name = "itemService", fallbackMethod = "fallbackForItemService")
    public ResponseEntity<Map<String, String>> addItem(CartItemRequest request, HttpServletRequest httpRequest) {
        String cartKey;
        String sessionId = null;
        String userId = getJwtToken(httpRequest);

        if (userId == null) {
            if (StringUtils.hasText(request.getSessionId()))  {
                cartKey = "cart:" + request.getSessionId(); //use existing session
            } else {
                sessionId = UUID.randomUUID().toString();
                cartKey =  "cart:" + sessionId; //generate new session id
            }
        } else {
            cartKey = "cart:" + userId;
        }

        try {
            ItemDTO itemDTO = itemClient.getItemById(request.getItemId());

            CartItem cartItem = new CartItem(
                    itemDTO.getItemId(),
                    itemDTO.getItemName(),
                    request.getQuantity(),
                    itemDTO.getUnitPrice()
            );

            cartRepository.addItemToCart(cartKey, cartItem);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Item not found in Item Service");
        } catch (FeignException e) {
            throw new RuntimeException("Error calling Item Service", e);
        }

        if (request.getSessionId() == null) { // If a new sessionId was generated, return it in the response
            return ResponseEntity.ok(Map.of("sessionId", sessionId));
        } else {
            return ResponseEntity.ok(Map.of("message", "Item added to cart"));
        }
    }

    public ResponseEntity<Map<String, String>> fallbackForItemService(CartItemRequest request, HttpServletRequest httpRequest, Throwable throwable) {
        throw new RuntimeException("Item Service is unavailable, please try again later.\n" + throwable.getMessage());
    }

    public void removeItem(CartItemRequest request, HttpServletRequest httpRequest) {
        String userId = getJwtToken(httpRequest);
        String sessionId = userId != null ? userId : request.getSessionId();
        String cartKey = "cart:" + sessionId;
        cartRepository.removeItemFromCart(cartKey, request.getItemId());
    }

    public CartDTO getCart(CartItemRequest request, HttpServletRequest httpRequest) {
        String userId = getJwtToken(httpRequest);
        String sessionId = userId != null ? userId : request.getSessionId();
        String cartKey = "cart:" + sessionId;
        List<CartItem> items = cartRepository.getCartItems(cartKey);
        return new CartDTO(userId, items);
    }

    public void clearCart(CartItemRequest request, HttpServletRequest httpRequest) {
        String userId = getJwtToken(httpRequest);
        String sessionId = userId != null ? userId : request.getSessionId();
        String cartKey = "cart:" + sessionId;
        cartRepository.clearCart(cartKey);
    }

    @Override
    public void migrateCart(CartItemRequest request, HttpServletRequest httpRequest) {
        String userId = getJwtToken(httpRequest);
        if (userId == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        String sessionCartKey = "cart:" + request.getSessionId();
        String userCartKey = "cart:" + userId;
        cartRepository.migrateCart(sessionCartKey, userCartKey);
    }

    private String getJwtToken(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (JwtUtil.validateToken(token)) {
                return JwtUtil.getUserIdFromToken(token); //if logged in, use encoded UUID as redis cart key
            }
        }
        return null; // Invalid token → Treat as guest user who has no token

    }
}

