package com.jht.assistantmanager.model;

public class OrderForList {

	private String orderNumber;

	private String packageName;

	private String address;

	private float money;

	private String orderTime;

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public float getMoney() {
		return money;
	}

	public void setMoney(float money) {
		this.money = money;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public OrderForList(String orderNumber, String packageName, String address, float money, String orderTime) {
		super();
		this.orderNumber = orderNumber;
		this.packageName = packageName;
		this.address = address;
		this.money = money;
		this.orderTime = orderTime;
	}

}
