package com.jht.assistantmanager.model;

public class OrderRecord {

	private int id;

	private String statusname;
	
	private String cardprice;

	private String ordercontent;

	private String orderno;
	
	private String code;

	private String submitdate;

	private String fulladdr;

	private String buyermemo;

	private double orderamount;
	
	

	public String getCardprice() {
		return cardprice;
	}

	public void setCardprice(String cardprice) {
		this.cardprice = cardprice;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStatusname() {
		return statusname;
	}

	public void setStatusname(String statusname) {
		this.statusname = statusname;
	}

	public String getOrdercontent() {
		return ordercontent;
	}

	public void setOrdercontent(String ordercontent) {
		this.ordercontent = ordercontent;
	}

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public String getSubmitdate() {
		return submitdate;
	}

	public void setSubmitdate(String submitdate) {
		this.submitdate = submitdate;
	}

	public String getFulladdr() {
		return fulladdr;
	}

	public void setFulladdr(String fulladdr) {
		this.fulladdr = fulladdr;
	}

	public String getBuyermemo() {
		return buyermemo;
	}

	public void setBuyermemo(String buyermemo) {
		this.buyermemo = buyermemo;
	}

	public double getOrderamount() {
		return orderamount;
	}

	public void setOrderamount(double orderamount) {
		this.orderamount = orderamount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public OrderRecord(String statusname, String ordercontent, String orderno, String submitdate, String fulladdr,
			String buyermemo, double orderamount) {
		super();
		this.statusname = statusname;
		this.ordercontent = ordercontent;
		this.orderno = orderno;
		this.submitdate = submitdate;
		this.fulladdr = fulladdr;
		this.buyermemo = buyermemo;
		this.orderamount = orderamount;
	}

}
