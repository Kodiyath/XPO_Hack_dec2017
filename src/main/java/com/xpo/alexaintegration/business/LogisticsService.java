package com.xpo.alexaintegration.business;

import java.time.LocalDate;
import java.util.List;

public interface LogisticsService {

	/**
	 * Get all the orders by the user.
	 * 
	 * @return List of orders
	 */
	List<Order> getOrders();
	
	List<Order> getOrders(final List<Order>orders, final OrderStatus status);
	
	List<Order> getOrders(final List<Order>orders, final LocalDate deliveryDate);
}
