package com.david.hibernate.n21both.entity;

public class OrderBoth {
	@Override
	public String toString() {
		return "OrderBoth [orderId=" + orderId + ", orderName=" + orderName + ", customer=" + customer + "]";
	}
	private Integer orderId;
	private String orderName;
	private CustomerBoth customer;
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
	public CustomerBoth getCustomer() {
		return customer;
	}
	public void setCustomer(CustomerBoth customer) {
		this.customer = customer;
	}
	
}
