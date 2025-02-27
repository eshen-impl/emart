package com.chuwa.cartservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartItemRequest {
    private String sessionId; //if not logged in, use session id
    private String itemId;
    private int quantity;
}
