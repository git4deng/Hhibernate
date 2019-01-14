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
		 * 先保存1这端，后保存多这一段，控制台输出3条insert语句
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
		 * 先保存多这一端，会出现3条insert语句，2条update语句，因为先插入n的时候，1的这个不存在
		 * 那么关联外键先置空，等1这端insert之后再进行更新操作。这样会多出n条update语句，所有推荐
		 * 先插入1这一端，可以节省性能
		 */
		session.save(order3);
		session.save(order4);
		session.save(customer1);	
	}
	
	@Test
	public void testManyToOneGet(){
		// 若查询多的一个对象，则默认情况下，只查询了多得一端对象，而没有查询关联的1的那一端的对象
		//即默认是延迟加载的
		Order order=(Order) session.get(Order.class, 1);
		System.out.println(order.getOrderName());
		/*
		 * 当使用关联的1那端的属性的时候，再从数据库中查询。但是如果在使用关联的对象时候session已经
		 * 关闭，会抛出懒加载异常。即默认情况下，关联的customer对象试一个代理对象
		 */
//		session.close();//此时关闭session，后续使用customer属性就会发生懒加载异常
		System.out.println(order.getCustomer());
		
	}
	
	@Test
	public void testManyToOneUpdate(){
		Order order=(Order) session.get(Order.class, 1);
		order.setOrderName("AAA");
		//1.更新customer为游离对象的时候,会抛出异常。只有使用session.save方法将该
		//游离对象变成持久化对象方能更新成功
		Customer customer1=new Customer();
		customer1.setCustomerName("CC");
		order.setCustomer(customer1);
		
		session.save(customer1);
		session.update(order);
		
		//2.更新customer为持久化对象,可以直接update
		Customer customer2=(Customer) session.get(Customer.class, 2);
		Order order2=(Order) session.get(Order.class, 2);
		order2.setCustomer(customer2);
		order2.setOrderName("O-2");
		session.update(order2);	
	}
	@Test
	public void testManyToOneDelete(){
		/*
		 * 在没有设定级联关系的情况下，且不能直接删除1这一端的对象，1这一端对象在n的这端有引用
		 * 如果不存在引用则可以直接删除
		 */
		Customer customer2=(Customer) session.get(Customer.class, 2);
		session.delete(customer2);
	}
}
