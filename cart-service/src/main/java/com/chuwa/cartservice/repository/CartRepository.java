package com.chuwa.cartservice.repository;

import com.chuwa.cartservice.entity.CartItem;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Repository
public class CartRepository {

    private final HashOperations<String, String, CartItem> hashOperations;

    public CartRepository(RedisTemplate<String, Object> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void addItemToCart(String cartKey, CartItem cartItem) {
        hashOperations.put(cartKey, cartItem.getItemId(), cartItem);
    }

    public void removeItemFromCart(String cartKey, String itemId) {
        hashOperations.delete(cartKey, itemId);
    }

    public List<CartItem> getCartItems(String cartKey) {
        Map<String, CartItem> cartMap = hashOperations.entries(cartKey);
        return new ArrayList<>(cartMap.values());
    }

    public void clearCart(String cartKey) {
        hashOperations.getOperations().delete(cartKey);
    }

    public void migrateCart(String sessionCartKey, String userCartKey) {
        // Retrieve all cart items from guest cart
        Map<String, CartItem> guestCartItems = hashOperations.entries(sessionCartKey);

        if (!guestCartItems.isEmpty()) {
            // Move each item from guest cart to user cart
            for (Map.Entry<String, CartItem> entry : guestCartItems.entrySet()) {
                hashOperations.put(userCartKey, entry.getKey(), entry.getValue());
            }

            // Delete guest cart after migration
            hashOperations.getOperations().delete(sessionCartKey);
        }
    }
}
