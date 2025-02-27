package com.chuwa.cartservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * used to retrieve necessary item info from item service via feign
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemDTO {
    private String itemId;
    private String itemName;
    private double unitPrice;
}
