package com.chuwa.cartservice.repository;

import com.chuwa.cartservice.entity.CartItem;
import com.chuwa.cartservice.util.JsonUtil;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Repository
public class CartRepository {

    private final HashOperations<String, String, String> hashOperations;

    public CartRepository(RedisTemplate<String, Object> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void addItemToCart(String cartKey, CartItem cartItem) {
        try {
            // Serialize CartItem to JSON and store it as a String
            String jsonCartItem = JsonUtil.toJson(cartItem);
            hashOperations.put(cartKey, cartItem.getItemId(), jsonCartItem);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing CartItem", e);
        }
    }

    public void removeItemFromCart(String cartKey, String itemId) {
        hashOperations.delete(cartKey, itemId);
    }

    public List<CartItem> getCartItems(String cartKey) {
        Map<String, String> cartMap = hashOperations.entries(cartKey);
        List<CartItem> cartItems = new ArrayList<>();

        // Deserialize JSON strings back into CartItem objects
        for (String json : cartMap.values()) {
            CartItem cartItem = JsonUtil.fromJsonToCartItem(json);
            cartItems.add(cartItem);
        }

        return cartItems;
    }

    public void clearCart(String cartKey) {
        hashOperations.getOperations().delete(cartKey);
    }

    public void migrateCart(String sessionCartKey, String userCartKey) {
        Map<String, String> guestCartItems = hashOperations.entries(sessionCartKey);

        if (!guestCartItems.isEmpty()) {
            // Migrate each item by deserializing it and serializing it again into the user cart
            for (Map.Entry<String, String> entry : guestCartItems.entrySet()) {
                CartItem cartItem = JsonUtil.fromJsonToCartItem(entry.getValue());
                addItemToCart(userCartKey, cartItem);  // This will serialize and save it as JSON
            }

            // Delete guest cart after migration
            hashOperations.getOperations().delete(sessionCartKey);
        }
    }
}
