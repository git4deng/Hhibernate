package com.david.hibernate.retrieve.classLevel.entity;

import java.util.HashSet;
import java.util.Set;

public class ReCustomer {
	private Integer customerId;
	private String customerName;
	private Set<ReOrder> orders=new  HashSet<ReOrder>();
	public Set<ReOrder> getOrders() {
		return orders;
	}

	public void setOrders(Set<ReOrder> orders) {
		this.orders = orders;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@Override
	public String toString() {
		return "ReCustomer [customerId=" + customerId + ", customerName=" + customerName + ", orders=" + orders + "]";
	}

}
