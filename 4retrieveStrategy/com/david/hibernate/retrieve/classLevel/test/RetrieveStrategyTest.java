package com.david.hibernate.retrieve.classLevel.test;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.david.hibernate.retrieve.classLevel.entity.ReCustomer;
import com.david.hibernate.retrieve.classLevel.entity.ReOrder;


@SuppressWarnings("deprecation")
public class RetrieveStrategyTest {

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
		ReCustomer ReCustomer=new ReCustomer();
		ReCustomer.setCustomerName("AA");
		
		ReOrder ReOrder1=new ReOrder();
		ReOrder1.setOrderName("O-1");
		ReOrder ReOrder2=new ReOrder();
		ReOrder2.setOrderName("O-2");
		
		ReOrder1.setCustomer(ReCustomer);
		ReOrder2.setCustomer(ReCustomer);
		
		session.save(ReCustomer);
		session.save(ReOrder1);
		session.save(ReOrder2);
		
		
		ReCustomer ReCustomer1=new ReCustomer();
		ReCustomer1.setCustomerName("BB");
		
		ReOrder ReOrder3=new ReOrder();
		ReOrder3.setOrderName("O-3");
		ReOrder ReOrder4=new ReOrder();
		ReOrder4.setOrderName("O-4");
		
		ReOrder3.setCustomer(ReCustomer1);
		ReOrder4.setCustomer(ReCustomer1);
		
		session.save(ReOrder3);
		session.save(ReOrder4);
		session.save(ReCustomer1);	
	}
	/**
	 * 类级别的检索策略
	 * 	立即检索: 立即加载检索方法指定的对象
	 * 	延迟检索: 延迟加载检索方法指定的对象。在使用具体的属性时，再进行加载
	 * 注意：无论 <class> 元素的 lazy 属性是 true 还是 false, Session 的 get() 方法及 Query 的 list() 方法在类级别总是使用立即检索策略
	 * 即 lazy 属性只对load方法有效
	 */
	@Test
	public void testClassLevelStrategy(){
		ReCustomer c=(ReCustomer) session.load(ReCustomer.class, 1);
		/* 当映射文件的class标签 lazy属性未配置时，load方法获取到的是代理对象
		 * class com.david.hibernate.retrieve.classLevel.entity.ReCustomer_$$_jvst38f_2
		 * 当配置lazy=false(默认是true)后获取到是对象本身
		 * class com.david.hibernate.retrieve.classLevel.entity.ReCustomer
		 */
		System.out.println(c.getClass());
		/*
		 * 由 Hibernate 在运行时采用 CGLIB 工具动态生成Hibernate 创建代理类实例时, 仅初始化其 OID 属性
		 * 即使用id是不会发送select语句的
		 */
		System.out.println(c.getCustomerId());
		
	}
	/**
	 * 一对多检索策略
	 */
	@Test
	public void testOne2ManyLevelStrategy(){
		//1.set的lazy属性
		ReCustomer c=(ReCustomer) session.get(ReCustomer.class, 1);
		System.out.println(c.getCustomerName());
		/*
		 * 1-n,n-n的集合属性默认使用的是懒加载的检索策略,可以通过设置set 的lazy属性来修改默认的检索策略，默认为true
		 * 并不建议修改lazy属性为false,立即检索会发送2条select语句，影响性能。另外lazy属性值还可以设置为extra,增强的
		 * 延迟检索,该取值会尽可能的延迟初始化的时机，例如如下 获取order的size,仅仅发送了一句
		 * select count(ORDER_ID) from DAVID_ORDER_RETRIEVE where CUSTOMER_ID =?
		 * 而不是把所有关联的order查出来
		 */
		System.out.println(c.getOrders().size());
		
		
	}
	/**
	 * <set> 元素的 batch-size 属性,用来为延迟检索策略或立即检索策略设定批量检索的数量. 
	 * 批量检索能减少 SELECT 语句的数目, 提高延迟检索或立即检索的运行性能
	 */
	@Test
	public void testSetBatchSize(){
		List<ReCustomer> customers=session.createQuery("FROM ReCustomer").list();
		System.out.println(customers.size());
		/*
		 * set 元素的batch-size属性用来指定一次初始化set集合的数量，如果不设置值，那么set中的每个元素的初始化都将
		 * 发送一次select语句，如果设置了该属性，那么发送set.size/n条select语句，采用in(条件的方式)来初始化多个元素
		 * 
		 * select orders0_.CUSTOMER_ID as CUSTOMER3_7_1_, orders0_.ORDER_ID as ORDER_ID1_18_1_, 
		 * orders0_.ORDER_ID as ORDER_ID1_18_0_, orders0_.ORDER_NAME as ORDER_NA2_18_0_, 
		 * orders0_.CUSTOMER_ID as CUSTOMER3_18_0_ from DAVID_ORDER_RETRIEVE orders0_ 
		 * where orders0_.CUSTOMER_ID in (?, ?, ?, ?)
		 * 
		 */
		for(ReCustomer c:customers){
			if(c.getOrders()!=null)
				System.out.println(c.getOrders().size());
		}
	}
	/**
	 * fetch: 取值为 “select” 或 “subselect” 时, 决定初始化 orders 的查询语句的形式;  若取值为”join”, 则决定 orders 集合被初始化的时机
	 */
	@Test
	public void testSetFetch(){
		/*
		 * fetch属性取值：
		 * select:默认取值，正常方式查询初始化set集合元素
		 * subselect:采用子查询的方式，忽略bitch-size属性值，因为子查询作为where子句in的条件出现，
		 * 即查询了1端的所有id,所以bitch-size无效，但是lazy有效
		 * 	select
		 *       orders0_.CUSTOMER_ID as CUSTOMER3_7_1_,
		 *       orders0_.ORDER_ID as ORDER_ID1_18_1_,
		 *       orders0_.ORDER_ID as ORDER_ID1_18_0_,
		 *       orders0_.ORDER_NAME as ORDER_NA2_18_0_,
		 *       orders0_.CUSTOMER_ID as CUSTOMER3_18_0_ 
		 *   from
		 *       DAVID_ORDER_RETRIEVE orders0_ 
		 *   where
		 *       orders0_.CUSTOMER_ID in (
		 *           select
		 *               recustomer0_.CUSTOMER_ID 
		 *           from
		 *               DAVID_CUSTOMER_RETRIEVE recustomer0_
		 *       )
		 *  join:会采用迫切左外连接(通过左外连接加载与检索指定的对象关联的对象)策略来检索所有关联的 Order 对象,lazy属性被忽略
		 *  但是Query 的list() 方法会忽略映射文件中配置的迫切左外连接检索策略, 而依旧采用延迟加载策略     
		 *  
		 */
		List<ReCustomer> customers=session.createQuery("FROM ReCustomer").list();
		System.out.println(customers.size());
		for(ReCustomer c:customers){
			if(c.getOrders()!=null)
				System.out.println(c.getOrders().size());
		}
	}
	@Test
	public void testSetFetch2(){
		ReCustomer c=(ReCustomer) session.get(ReCustomer.class, 1);
		/*
		 * fetch:join时，在获取customer的时候就初始化了set集合，通过left join的方式
			select
			    recustomer0_.CUSTOMER_ID as CUSTOMER1_7_0_,
			    recustomer0_.CUSTOMER_NAME as CUSTOMER2_7_0_,
			    orders1_.CUSTOMER_ID as CUSTOMER3_7_1_,
			    orders1_.ORDER_ID as ORDER_ID1_18_1_,
			    orders1_.ORDER_ID as ORDER_ID1_18_2_,
			    orders1_.ORDER_NAME as ORDER_NA2_18_2_,
			    orders1_.CUSTOMER_ID as CUSTOMER3_18_2_ 
			from
			    DAVID_CUSTOMER_RETRIEVE recustomer0_ 
			left outer join
			    DAVID_ORDER_RETRIEVE orders1_ 
			        on recustomer0_.CUSTOMER_ID=orders1_.CUSTOMER_ID 
			where
			    recustomer0_.CUSTOMER_ID=? 
		 */
	}
	/**
	 * 多对一的检索策略
	 */
	@Test
	public void testMany2One(){
		/*
		 * 默认情况下未初始化customer对象,若设置lazy="false",就会立即初始化customer
		 * <many-to-one name="customer" class="ReCustomer" column="CUSTOMER_ID" lazy="false"></many-to-one>
		 * 
		 */
		ReOrder order =(ReOrder) session.get(ReOrder.class, 1);
		System.out.println(order.getOrderName());
		
		/*
		 * fetch属性设置未join时会立即检索customer对象，采用迫切左外连接的方式
		 * <many-to-one name="customer" class="ReCustomer" column="CUSTOMER_ID"  fetch="join"/>
		 * 但是Query 的list() 方法会忽略映射文件中配置的迫切左外连接检索策略, 而依旧采用延迟加载策略     
		 */
		List<ReOrder> orders=session.createQuery("from ReOrder o").list();
		for(ReOrder o:orders){
			if(o.getCustomer()!=null)
				System.out.println(o.getCustomer().getCustomerName());
		}
		/*
		 *上面初始化costomer对象的batch-size属性有效，但是得在class标签上面设置
		 *<class name="ReCustomer" table="DAVID_CUSTOMER_RETRIEVE" lazy="true" batch-size="5">
		 */
	}
}
