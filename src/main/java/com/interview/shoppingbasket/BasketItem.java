package com.interview.shoppingbasket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasketItem {
    private String productCode;
    private String productName;
    private int quantity;
    private double productRetailPrice;

}
