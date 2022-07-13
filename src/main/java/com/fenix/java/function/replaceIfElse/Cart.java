package com.fenix.java.function.replaceIfElse;

public class Cart {

    private int value;

    public static void main(String[] args) {
        Cart cart6 = new Cart(6);
        Cart cart7 = new Cart(7);
        Cart cart8 = new Cart(8);

        System.out.println("cart 6 -> " + RuleMatrix.getRule(cart6).apply(cart6));
        System.out.println("cart 7 -> " + RuleMatrix.getRule(cart7).apply(cart7));
        System.out.println("cart 8 -> " + RuleMatrix.getRule(cart8).apply(cart8));

    }

    public Cart(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
