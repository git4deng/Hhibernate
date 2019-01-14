package com.david.hibernate.n21both.entity;

import java.util.HashSet;
import java.util.Set;

public class CustomerBoth {
	private Integer customerId;
	private String customerName;
	/*1.������������ʱ����ʹ�ýӿ����ͣ���Ϊhiberante�ڻ�ȡ��������ʱ���ص���һ��
	 * hibernate�����ü������ͣ��ü���ʵ����Java���Ͻӿڣ���������Javaseһ����׼�ļ���ʵ��
	 *2.��Ҫ�Լ��Ͻ��г�ʼ���������Է���ָ���쳣 
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
