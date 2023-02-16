package com.interview.shoppingbasket;

import java.util.List;

public class PromotionCheckoutStep implements CheckoutStep {

    private PromotionsService promotionsService;

    public PromotionCheckoutStep(PromotionsService promotionsService) {
        this.promotionsService = promotionsService;
    }

    @Override
    public void execute(CheckoutContext checkoutContext) {
        List<Promotion> promotions = promotionsService.getPromotions(checkoutContext.getBasket());

        checkoutContext.setRetailPriceTotal(
                promotions.stream().mapToDouble(p ->
                        checkoutContext.getBasket().getItems().stream().mapToDouble(
                                it -> p.calculate(it)).sum()).sum());
    }

}
