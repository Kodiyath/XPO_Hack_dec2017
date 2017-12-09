package com.xpo.alexaintegration.business;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public enum OrderStatus {

	PLACED("placed"),
	SHIPPED("shipped"),
	IN_TRANSIT("in transit"),
	DELIVERED("delivered");
	
	private String status;

	private OrderStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
	
	public static OrderStatus getStatus(final String status) {
		return Arrays.stream(OrderStatus.values()).filter(t -> StringUtils.equalsIgnoreCase(t.getStatus(), status)).findFirst().get() ;
	}
}