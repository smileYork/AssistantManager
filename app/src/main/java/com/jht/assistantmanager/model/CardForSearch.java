package com.jht.assistantmanager.model;

public class CardForSearch {

	private String code;

	private double cardprice;

	private double cardsmallprice;

	private String statusname;

	private String actdate;

	private int selnum;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getCardprice() {
		return cardprice;
	}

	public void setCardprice(double cardprice) {
		this.cardprice = cardprice;
	}

	public double getCardsmallprice() {
		return cardsmallprice;
	}

	public void setCardsmallprice(double cardsmallprice) {
		this.cardsmallprice = cardsmallprice;
	}

	public String getStatusname() {
		return statusname;
	}

	public void setStatusname(String statusname) {
		this.statusname = statusname;
	}

	public String getActdate() {
		return actdate;
	}

	public void setActdate(String actdate) {
		this.actdate = actdate;
	}

	public int getSelnum() {
		return selnum;
	}

	public void setSelnum(int selnum) {
		this.selnum = selnum;
	}

	@Override
	public String toString() {
		return "CardForSearch [code=" + code + ", cardprice=" + cardprice + ", cardsmallprice=" + cardsmallprice
				+ ", statusname=" + statusname + ", actdate=" + actdate + ", selnum=" + selnum + "]";
	}

}
