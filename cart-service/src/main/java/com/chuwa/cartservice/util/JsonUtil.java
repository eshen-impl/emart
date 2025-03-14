package com.chuwa.cartservice.util;

import com.chuwa.cartservice.entity.CartItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object items) {
        try {
            return objectMapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing CartItem list to JSON", e);
        }
    }

    public static Object fromJson(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing JSON to CartItem list", e);
        }
    }

    public static CartItem fromJsonToCartItem(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, CartItem.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing CartItem", e);
        }
    }


}
