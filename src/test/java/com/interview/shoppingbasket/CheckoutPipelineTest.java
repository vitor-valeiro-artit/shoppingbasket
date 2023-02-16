package com.interview.shoppingbasket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

public class CheckoutPipelineTest {

    CheckoutPipeline checkoutPipeline;

    PricingService pricingService;

    PromotionsService promotionsService;

    @BeforeEach
    void setup() {
        promotionsService = Mockito.mock(PromotionsService.class);
        pricingService = Mockito.mock(PricingService.class);
        when(promotionsService.getPromotions(Mockito.any())).thenReturn(Arrays.asList(
                new Promotion("productCode",
                        (it -> ((int) it.getQuantity() / 2) * it.getProductRetailPrice()),
                        (it -> it.getQuantity() > 1)),
                new Promotion("productCode2",
                        (it -> it.getQuantity() * it.getProductRetailPrice() * 0.5)),
                new Promotion("productCode3",
                        (it -> it.getQuantity() * it.getProductRetailPrice() * 0.9))
        ));

        when(pricingService.getPrice("productCode")).thenReturn(3.99);
        when(pricingService.getPrice("productCode2")).thenReturn(2.0);
        when(pricingService.getPrice("productCode3")).thenReturn(5.0);

        checkoutPipeline = new CheckoutPipeline();
        checkoutPipeline.addStep(new BasketConsolidationCheckoutStep());
        checkoutPipeline.addStep(new RetailPriceCheckoutStep(pricingService));
        checkoutPipeline.addStep(new PromotionCheckoutStep(promotionsService));
    }

    @Test
    void returnZeroPaymentForEmptyPipeline() {
        Basket basket = new Basket();
        PaymentSummary paymentSummary = checkoutPipeline.checkout(basket);

        assertEquals(paymentSummary.getRetailTotal(), 0.0);
    }

    @Test
    void executeAllPassedCheckoutSteps() {
        Basket basket = new Basket();
        basket.add("productCode", "myProduct", 10);
        basket.add("productCode2", "myProduct2", 10);
        basket.add("productCode3", "myProduct3", 10);

        basket.add("productCode", "myProduct", 10);
        basket.add("productCode2", "myProduct2", 20);
        basket.add("productCode3", "myProduct3", 30);

        basket.add("productCode", "myProduct", 10);
        basket.add("productCode2", "myProduct2", 20);
        basket.add("productCode3", "myProduct3", 30);

        PaymentSummary paymentSummary = checkoutPipeline.checkout(basket);

        List<BasketItem> basketSize = basket.getItems();

        assertEquals(3, basketSize.size());
        assertEquals("productCode", basketSize.get(0).getProductCode());
        assertEquals("myProduct", basketSize.get(0).getProductName());
        assertEquals(30, basketSize.get(0).getQuantity());

        assertEquals("productCode2", basketSize.get(1).getProductCode());
        assertEquals("myProduct2", basketSize.get(1).getProductName());
        assertEquals(50, basketSize.get(1).getQuantity());

        assertEquals("productCode3", basketSize.get(2).getProductCode());
        assertEquals("myProduct3", basketSize.get(2).getProductName());
        assertEquals(70, basketSize.get(2).getQuantity());
        assertEquals(424.85, paymentSummary.getRetailTotal());
    }

}
