package com.jht.assistantmanager.model;

import java.io.Serializable;

public class Card implements Serializable {

	private static final long serialVersionUID = 1803960736595324512L;

	private int id;

	private int type;

	private double price;

	private String packageName;

	private boolean isVIP;

	private String changeTime;

	private String orderNumber;

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isVIP() {
		return isVIP;
	}

	public void setVIP(boolean isVIP) {
		this.isVIP = isVIP;
	}

	public String getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(String changeTime) {
		this.changeTime = changeTime;
	}

	public Card(int id, int type, double price, String packageName, boolean isVIP, String changeTime) {
		super();
		this.id = id;
		this.type = type;
		this.price = price;
		this.packageName = packageName;
		this.isVIP = isVIP;
		this.changeTime = changeTime;
	}

	public Card(int id, int type, double price, String packageName, boolean isVIP, String changeTime,
			String orderNumber) {
		super();
		this.id = id;
		this.type = type;
		this.price = price;
		this.packageName = packageName;
		this.isVIP = isVIP;
		this.changeTime = changeTime;
		this.orderNumber = orderNumber;
	}

}
