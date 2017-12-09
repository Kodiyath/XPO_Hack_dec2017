package com.xpo.alexaintegration.business;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

//FIXME: handle providing the orders
@Service
public class DummyLogisticService implements LogisticsService {

	@Override
	public List<Order> getOrders() {
		Order o1 = new Order("1", LocalDate.of(2017, 12, 2), OrderStatus.DELIVERED, LocalDate.of(2017, 12, 4), new Product("Sofa", "furniture"));
		Order o2 = new Order("2", LocalDate.of(2017, 12, 4), OrderStatus.IN_TRANSIT, LocalDate.of(2017, 12, 10), new Product("Sofa", "furniture"));
		Order o3 = new Order("3", LocalDate.of(2017, 12, 12), OrderStatus.PLACED, LocalDate.of(2017, 12, 20), new Product("Sofa", "furniture"));
		Order o4 = new Order("4", LocalDate.of(2017, 12, 2), OrderStatus.SHIPPED, LocalDate.of(2017, 12, 30), new Product("Sofa", "furniture"));
		
		return Arrays.asList(o1, o2, o3, o4);
	}

	@Override
	public List<Order> getOrders(List<Order> orders, OrderStatus status) {
		return orders.stream().filter(t -> t.getStatus() == status).collect(Collectors.toList());
	}

	@Override
	public List<Order> getOrders(List<Order> orders, LocalDate deliveryDate) {
		return orders.stream().filter(t -> t.getCurrentEta() == deliveryDate).collect(Collectors.toList());
	}

}
