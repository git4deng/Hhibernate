package com.david.hibernate.n21both.entity;

import java.util.HashSet;
import java.util.Set;

public class CustomerBoth {
	private Integer customerId;
	private String customerName;
	/*1.申明集合类型时，需使用接口类型，因为hiberante在获取集合类型时返回的是一个
	 * hibernate的内置集合类型（该集合实现了Java集合接口），而不是Javase一个标准的集合实现
	 *2.需要对集合进行初始化操作，以防空指针异常 
	 */
	private Set<OrderBoth> orders=new  HashSet<>();
	
	public Set<OrderBoth> getOrders() {
		return orders;
	}

	public void setOrders(Set<OrderBoth> orders) {
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
		return "Customer [customerId=" + customerId + ", customerName=" + customerName + ", orders=" + orders + "]";
	}

}
