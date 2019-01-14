package com.david.hibernate.n21.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.david.hibernate.n21.entity.Customer;
import com.david.hibernate.n21.entity.Order;

@SuppressWarnings("deprecation")
public class N21Test {

	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transaction;

	@Before
	public void initSession() {
		Configuration configuration = new Configuration().configure();
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties())
				.buildServiceRegistry();

		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
	}

	@After
	public void destroy() {
		transaction.commit();
		session.close();
		sessionFactory.close();
	}
	
	@Test
	public void testManyToOneSave(){
		Customer customer=new Customer();
		customer.setCustomerName("AA");
		
		Order order1=new Order();
		order1.setOrderName("O-1");
		Order order2=new Order();
		order1.setOrderName("O-2");
		
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		/*
		 * �ȱ���1��ˣ��󱣴����һ�Σ�����̨���3��insert���
		 */
		session.save(customer);
		session.save(order1);
		session.save(order2);
		
		
		Customer customer1=new Customer();
		customer1.setCustomerName("BB");
		
		Order order3=new Order();
		order3.setOrderName("O-3");
		Order order4=new Order();
		order4.setOrderName("O-4");
		
		order3.setCustomer(customer1);
		order4.setCustomer(customer1);
		/*
		 * �ȱ������һ�ˣ������3��insert��䣬2��update��䣬��Ϊ�Ȳ���n��ʱ��1�����������
		 * ��ô����������ÿգ���1���insert֮���ٽ��и��²�������������n��update��䣬�����Ƽ�
		 * �Ȳ���1��һ�ˣ����Խ�ʡ����
		 */
		session.save(order3);
		session.save(order4);
		session.save(customer1);	
	}
	
	@Test
	public void testManyToOneGet(){
		// ����ѯ���һ��������Ĭ������£�ֻ��ѯ�˶��һ�˶��󣬶�û�в�ѯ������1����һ�˵Ķ���
		//��Ĭ�����ӳټ��ص�
		Order order=(Order) session.get(Order.class, 1);
		System.out.println(order.getOrderName());
		/*
		 * ��ʹ�ù�����1�Ƕ˵����Ե�ʱ���ٴ����ݿ��в�ѯ�����������ʹ�ù����Ķ���ʱ��session�Ѿ�
		 * �رգ����׳��������쳣����Ĭ������£�������customer������һ���������
		 */
//		session.close();//��ʱ�ر�session������ʹ��customer���Ծͻᷢ���������쳣
		System.out.println(order.getCustomer());
		
	}
	
	@Test
	public void testManyToOneUpdate(){
		Order order=(Order) session.get(Order.class, 1);
		order.setOrderName("AAA");
		//1.����customerΪ��������ʱ��,���׳��쳣��ֻ��ʹ��session.save��������
		//��������ɳ־û������ܸ��³ɹ�
		Customer customer1=new Customer();
		customer1.setCustomerName("CC");
		order.setCustomer(customer1);
		
		session.save(customer1);
		session.update(order);
		
		//2.����customerΪ�־û�����,����ֱ��update
		Customer customer2=(Customer) session.get(Customer.class, 2);
		Order order2=(Order) session.get(Order.class, 2);
		order2.setCustomer(customer2);
		order2.setOrderName("O-2");
		session.update(order2);	
	}
	@Test
	public void testManyToOneDelete(){
		/*
		 * ��û���趨������ϵ������£��Ҳ���ֱ��ɾ��1��һ�˵Ķ���1��һ�˶�����n�����������
		 * ������������������ֱ��ɾ��
		 */
		Customer customer2=(Customer) session.get(Customer.class, 2);
		session.delete(customer2);
	}
}
