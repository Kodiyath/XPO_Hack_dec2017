package com.xpo.alexaintegration.business;

import java.time.LocalDate;

public class Order {

	private final String orderId;
	private final LocalDate orderDate;
	private final OrderStatus status;
	private final LocalDate currentEta;
	private final Product product;

	// TODO: delivery window
	public Order(String orderId, LocalDate orderDate, OrderStatus status, LocalDate currentEta, final Product product) {
		super();
		this.orderId = orderId;
		this.orderDate = orderDate;
		this.status = status;
		this.currentEta = currentEta;
		this.product = product;
	}

	public String getOrderId() {
		return orderId;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public LocalDate getCurrentEta() {
		return currentEta;
	}

	public Product getProduct() {
		return product;
	}
}
