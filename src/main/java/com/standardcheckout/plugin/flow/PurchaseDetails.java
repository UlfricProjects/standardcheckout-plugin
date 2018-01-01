package com.standardcheckout.plugin.flow;

import java.math.BigDecimal;

import com.standardcheckout.plugin.model.Cart;

public class PurchaseDetails {

	private BigDecimal price;
	private Cart cart;
	private String name;

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
