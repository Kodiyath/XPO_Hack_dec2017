package com.xpo.alexaintegration.business;

public class Product {

	private final String name;
	private final String category;
	public Product(String name, String category) {
		super();
		this.name = name;
		this.category = category;
	}
	public String getName() {
		return name;
	}
	public String getCategory() {
		return category;
	}
	
}
