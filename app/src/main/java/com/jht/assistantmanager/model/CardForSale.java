package com.jht.assistantmanager.model;

public class CardForSale {

	private String id;

	private String name;

	private double money;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public CardForSale(String id, String name, double d) {
		super();
		this.id = id;
		this.name = name;
		this.money = d;
	}

	public CardForSale() {
		super();
	}

}
