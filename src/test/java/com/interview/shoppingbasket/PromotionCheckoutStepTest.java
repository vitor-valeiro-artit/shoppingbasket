package com.interview.shoppingbasket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PromotionCheckoutStepTest {

    PromotionsService promotionsService;

    PromotionCheckoutStep promotionCheckoutStep;

    CheckoutContext checkoutContext;

    Basket basket;

    @BeforeEach
    void setup() {
        promotionsService = Mockito.mock(PromotionsService.class);
        promotionCheckoutStep = new PromotionCheckoutStep(promotionsService);
        when(promotionsService.getPromotions(Mockito.any())).thenReturn(Arrays.asList(
                new Promotion("productCode",
                        (it -> ((int) it.getQuantity() / 2) * it.getProductRetailPrice()),
                        (it -> it.getQuantity() > 1)),
                new Promotion("productCode2",
                        (it -> it.getQuantity() * it.getProductRetailPrice() * 0.5)),
                new Promotion("productCode3",
                        (it -> it.getQuantity() * it.getProductRetailPrice() * 0.9))
        ));

        basket = new Basket();
        checkoutContext = new CheckoutContext(basket);
    }

    @Test
    void executePromotionCheckoutSteps() {
        basket.add("productCode", "myProduct", 10);
        basket.getItems().get(0).setProductRetailPrice(4);

        basket.add("productCode2", "myProduct2", 10);
        basket.getItems().get(1).setProductRetailPrice(2);

        basket.add("productCode3", "myProduct3", 10);
        basket.getItems().get(2).setProductRetailPrice(5);

        promotionCheckoutStep.execute(checkoutContext);

        List<BasketItem> basketSize = basket.getItems();

        assertEquals(3, basketSize.size());
        assertEquals("productCode", basketSize.get(0).getProductCode());
        assertEquals("myProduct", basketSize.get(0).getProductName());
        assertEquals(10, basketSize.get(0).getQuantity());
        assertEquals(4, basketSize.get(0).getProductRetailPrice());

        assertEquals("productCode2", basketSize.get(1).getProductCode());
        assertEquals("myProduct2", basketSize.get(1).getProductName());
        assertEquals(10, basketSize.get(1).getQuantity());
        assertEquals(2, basketSize.get(1).getProductRetailPrice());

        assertEquals("productCode3", basketSize.get(2).getProductCode());
        assertEquals("myProduct3", basketSize.get(2).getProductName());
        assertEquals(10, basketSize.get(2).getQuantity());
        assertEquals(5, basketSize.get(2).getProductRetailPrice());

        assertEquals(75, checkoutContext.paymentSummary().getRetailTotal());
    }

}
