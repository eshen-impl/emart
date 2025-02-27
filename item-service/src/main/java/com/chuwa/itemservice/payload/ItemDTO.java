package com.chuwa.itemservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private String itemId;
    private String itemName;
    private String upc;
    private double unitPrice;
    private String[] pictureUrls;
    private int availableUnits;
}
