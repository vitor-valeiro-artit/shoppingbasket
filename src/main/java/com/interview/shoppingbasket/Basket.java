package com.interview.shoppingbasket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Basket {

    private List<BasketItem> items = new ArrayList<>();

    public void add(String productCode, String productName, int quantity) {
        BasketItem basketItem = new BasketItem();
        basketItem.setProductCode(productCode);
        basketItem.setProductName(productName);
        basketItem.setQuantity(quantity);

        items.add(basketItem);
    }

    public List<BasketItem> getItems() {
        return items;
    }

    public void consolidateItems() {
        items = items.stream().collect(
                Collectors.groupingBy(BasketItem::getProductCode,
                        Collectors.reducing(new BasketItem(), (it1, it2) ->
                                new BasketItem(
                                        it2.getProductCode(),
                                        it2.getProductName(),
                                        it2.getQuantity() + it1.getQuantity(),
                                        0d
                                )
                        )
                )
        ).entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        Basket basket = new Basket();
        basket.add("productCode1", "productName1", 3);
        basket.add("productCode1", "productName1", 1);
        basket.add("productCode2",
                "productName2",
                2
        );
        basket.add("productCode2",
                "productName2",
                5
        );
        basket.add("productCode3",
                "productName3",
                1
        );
        basket.consolidateItems();
        System.out.println(basket.items);
    }
}
