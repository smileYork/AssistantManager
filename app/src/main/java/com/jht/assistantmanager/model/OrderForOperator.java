package com.jht.assistantmanager.model;

public class OrderForOperator {

	private String id;

	private String packageName;

	private String operator_type;

	private String operator_time;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getOperator_type() {
		return operator_type;
	}

	public void setOperator_type(String operator_type) {
		this.operator_type = operator_type;
	}

	public String getOperator_time() {
		return operator_time;
	}

	public void setOperator_time(String operator_time) {
		this.operator_time = operator_time;
	}

	public OrderForOperator(String id, String packageName, String operator_type, String operator_time) {
		super();
		this.id = id;
		this.packageName = packageName;
		this.operator_type = operator_type;
		this.operator_time = operator_time;
	}

}
