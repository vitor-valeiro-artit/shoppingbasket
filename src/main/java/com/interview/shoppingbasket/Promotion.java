package com.interview.shoppingbasket;

import lombok.Data;

import java.util.function.Function;

@Data
public class Promotion {

    private String productCode;
    private Function<BasketItem, Double> calc;
    private Function<BasketItem, Boolean> additionalRuleApplicable;

    public Promotion(String productCode, Function<BasketItem, Double> calc, Function<BasketItem, Boolean> additionalRuleApplicable) {
        this.productCode = productCode;
        this.calc = calc;
        this.additionalRuleApplicable = additionalRuleApplicable;
    }

    public Promotion(String productCode, Function<BasketItem, Double> calc) {
        this(productCode, calc, (it -> Boolean.TRUE));
    }

    private Boolean applicable(BasketItem basketItem) {
        return basketItem.getProductCode().equals(this.productCode) && additionalRuleApplicable.apply(basketItem);
    }

    public Double calculate(BasketItem basketItem) {
        if (applicable(basketItem)) {
            return calc.apply(basketItem);
        }
        return 0d;
    }

}
