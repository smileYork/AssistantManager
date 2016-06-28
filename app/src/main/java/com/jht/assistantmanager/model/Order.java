package com.jht.assistantmanager.model;

public class Order {

	private int id;

	private String orderNumber;

	private int cardNumber;

	private double price;

	private String operatorId;

	private long createTime;

	private int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Order(int id, String orderNumber, int cardNumber, double price, String operatorId, long createTime,
			int type) {
		super();
		this.id = id;
		this.orderNumber = orderNumber;
		this.cardNumber = cardNumber;
		this.price = price;
		this.operatorId = operatorId;
		this.createTime = createTime;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public int getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(int cardNumber) {
		this.cardNumber = cardNumber;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}
