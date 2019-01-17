package com.david.hibernate.retrieve.classLevel.entity;

public class ReOrder {
	private Integer orderId;
	private String orderName;
	private ReCustomer customer;
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public String getOrderName() {
		return orderName;
	}
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}
	public ReCustomer getCustomer() {
		return customer;
	}
	public void setCustomer(ReCustomer customer) {
		this.customer = customer;
	}
	
}
