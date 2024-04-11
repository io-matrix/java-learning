package jav.fenix.function.replaceIfElse;

import java.util.function.Function;

public class CartFunctions {

    public static final Function<Cart, Double> MULTIPLY_WITH_ONE_POINT_FIVE = cart -> cart.getValue() * 1.5;
    public static final Function<Cart, Double> MULTIPLY_WITH_TWO_POINT_FIVE = cart -> cart.getValue() * 2.5;
    public static final Function<Cart, Double> DEFAULT = cart -> cart.getValue() * 1.0;

}
