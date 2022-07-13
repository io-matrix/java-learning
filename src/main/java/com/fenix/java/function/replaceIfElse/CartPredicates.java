package com.fenix.java.function.replaceIfElse;

import java.util.function.Predicate;

public class CartPredicates {

    public static final Predicate<Cart> WHEN_CART_VALUE_IS_SIX = cart -> cart.getValue() == 6;
    public static final Predicate<Cart> WHEN_CART_VALUE_IS_SEVEN = cart -> cart.getValue() == 7;

}
