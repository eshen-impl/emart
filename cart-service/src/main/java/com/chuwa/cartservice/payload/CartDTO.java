package com.chuwa.cartservice.payload;

import com.chuwa.cartservice.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartDTO {
    private String userId;
    private List<CartItem> items;


}

