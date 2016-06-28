package com.jht.assistantmanager.model;

import java.sql.Timestamp;

/**
 * Apporderrecord entity. @author MyEclipse Persistence Tools
 */

public class Apporderrecord implements java.io.Serializable {

	// Fields

	private int id;
	private String uuid;
	private String orderid;
	private Short orderstate;
	private String khdm;
	private String codelist;
	private Double price;
	private String paydescribe;
	private String excdescribe;
	private String flowcode;
	private String pflowcod;
	private String paymemo;
	private String memo;
	private Short handlerstate;
	private String createtime;
	private String updatetime;

	// Constructors

	/** default constructor */
	public Apporderrecord() {
	}

	/** full constructor */
	public Apporderrecord(String uuid, String orderid, Short orderstate, String khdm, String codelist, Double price,
			String paydescribe, String excdescribe, String flowcode, String pflowcod, String paymemo, String memo,
			Short handlerstate, String createtime, String updatetime) {
		this.uuid = uuid;
		this.orderid = orderid;
		this.orderstate = orderstate;
		this.khdm = khdm;
		this.codelist = codelist;
		this.price = price;
		this.paydescribe = paydescribe;
		this.excdescribe = excdescribe;
		this.flowcode = flowcode;
		this.pflowcod = pflowcod;
		this.paymemo = paymemo;
		this.memo = memo;
		this.handlerstate = handlerstate;
		this.createtime = createtime;
		this.updatetime = updatetime;
	}

	// Property accessors

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getOrderid() {
		return this.orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public Short getOrderstate() {
		return this.orderstate;
	}

	public void setOrderstate(Short orderstate) {
		this.orderstate = orderstate;
	}

	public String getKhdm() {
		return this.khdm;
	}

	public void setKhdm(String khdm) {
		this.khdm = khdm;
	}

	public String getCodelist() {
		return this.codelist;
	}

	public void setCodelist(String codelist) {
		this.codelist = codelist;
	}

	public Double getPrice() {
		return this.price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getPaydescribe() {
		return this.paydescribe;
	}

	public void setPaydescribe(String paydescribe) {
		this.paydescribe = paydescribe;
	}

	public String getExcdescribe() {
		return this.excdescribe;
	}

	public void setExcdescribe(String excdescribe) {
		this.excdescribe = excdescribe;
	}

	public String getFlowcode() {
		return this.flowcode;
	}

	public void setFlowcode(String flowcode) {
		this.flowcode = flowcode;
	}

	public String getPflowcod() {
		return this.pflowcod;
	}

	public void setPflowcod(String pflowcod) {
		this.pflowcod = pflowcod;
	}

	public String getPaymemo() {
		return this.paymemo;
	}

	public void setPaymemo(String paymemo) {
		this.paymemo = paymemo;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Short getHandlerstate() {
		return this.handlerstate;
	}

	public void setHandlerstate(Short handlerstate) {
		this.handlerstate = handlerstate;
	}

	public String getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getUpdatetime() {
		return this.updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

}