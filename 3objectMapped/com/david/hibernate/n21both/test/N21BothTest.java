package com.david.hibernate.n21both.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.david.hibernate.n21both.entity.CustomerBoth;
import com.david.hibernate.n21both.entity.OrderBoth;
/**
 * 双向一对多
 * @author david
 *
 */
@SuppressWarnings("deprecation")
public class N21BothTest {

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
	public void testManyToOneBothSave(){
		CustomerBoth customer=new CustomerBoth();
		customer.setCustomerName("CC");
		
		OrderBoth order1=new OrderBoth();
		order1.setOrderName("O-5");
		OrderBoth order2=new OrderBoth();
		order2.setOrderName("O-6");
		//设定关联关系
		//设定n端的关联关系
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		//设定1端的关联关系
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		/*
		 *双向一对多先保存1这端，3条insert,2条update，update先插入的这端,主要是维护1这段
		 *对应多的那端的关系，因在n端存入的时候1端的oid已经存在了，因此会比先存n少n条update语句
		 *推荐使用此方式。
		 */
		/*
		 * 可以在1端的set节点指定inverse=true，使1端放弃维护关联关系，这样就不会执行update
		 * 1端的表了，即customer不会更新了，建议在1的这段设置设置inverse=true，先插入1端后
		 * 插入n端，好处就是不会多出update语句
		 */
		session.save(customer);
		session.save(order1);
		session.save(order2);
		
		
		CustomerBoth customer1=new CustomerBoth();
		customer1.setCustomerName("DD");
		
		OrderBoth order3=new OrderBoth();
		order3.setOrderName("O-7");
		OrderBoth order4=new OrderBoth();
		order4.setOrderName("O-8");
		
		order3.setCustomer(customer1);
		order4.setCustomer(customer1);
		
		customer1.getOrders().add(order3);
		customer1.getOrders().add(order4);
		/*
		 *先保存n端，后保存1端，会多出2*n条update和n+1条insert
		 *因先存n，不确定1的oid,因此在1保存后才会更新n的关联关系，同时还会关联1端对应n的关联关系
		 *因此会有2*n条update语句 
		 */
		session.save(order3);
		session.save(order4);
		session.save(customer1);	
	}
	
	@Test
	public void testManyToOneBothGet(){
		CustomerBoth customer=(CustomerBoth) session.get(CustomerBoth.class, 1);
		//对n的一端集合使用延迟加载
		System.out.println(customer.getCustomerName());
		//返回多的一端的集合是Hibernate的内置集合对象（org.hibernate.collection.internal.PersistentSet）
		//该类型具有延迟加载和存放代理对象的功能，同理也可能会抛出懒加载异常
		System.out.println(customer.getOrders().getClass());
		//在需要使用集合元素的时候进行初始化
	}
	
	@Test
	public void testManyToOneBothUpdate(){
		CustomerBoth customer=(CustomerBoth) session.get(CustomerBoth.class, 1);
		customer.getOrders().iterator().next().setOrderName("XXXX");
	}
	/**
	 * cascade设置为delete，级联删除
	 * <set name="orders" table="DAVID_ORDER_BOTH" inverse="true" cascade="delete">
	 */
	@Test
	public void testManyToOneDelete(){
		/*
		 * 在没有设定级联关系的情况下，且不能直接删除1这一端的对象，1这一端对象在n的这端有引用
		 * 如果不存在引用则可以直接删除,当设置cascade的值为delete就删除1对应的记录以及所关联n的记录
		 */
		CustomerBoth customer2=(CustomerBoth) session.get(CustomerBoth.class, 2);
		session.delete(customer2);
	}
	/**
	 * cascade设置为delete-orphan:删除所有和当前对象解除关系的对象
	 * <set name="orders" table="DAVID_ORDER_BOTH" inverse="true" cascade="delete-orphan">
	 */
	@Test
	public void testManyToOneDeleteOrphan(){
		
		CustomerBoth customer2=(CustomerBoth) session.get(CustomerBoth.class, 1);
		//注意与delete比较，这里只是解除与OID为1关联对象的关联关系，若配置了cascade=delete-orphan，那么
		//清空set将删除与其对应order的记录，但是不会删除customer记录
		customer2.getOrders().clear();
	}
	/**
	 * cascade设置为save-update:级联保存，当通过session的save、update以及saveOrUpdate方法
	 * 来保存或者更新对象时，级联保存所有关联的新建的临时对象，并且级联更新所有关联的游离对象
	 * <set name="orders" table="DAVID_ORDER_BOTH" inverse="true" cascade="save-update">
	 */
	@Test
	public void testManyToOneSaveOrUpdate(){
		//1.save
		CustomerBoth customer=new CustomerBoth();
		customer.setCustomerName("CASCADE");
		
		OrderBoth order1=new OrderBoth();
		order1.setOrderName("CA-1");
		OrderBoth order2=new OrderBoth();
		order2.setOrderName("CA-2");
		
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		session.save(customer);
		
		//2.update
		CustomerBoth customer6=(CustomerBoth) session.get(CustomerBoth.class, 6);
		OrderBoth order3=new OrderBoth();
		order3.setOrderName("CA-3");
		order3.setCustomer(customer6);
		customer6.getOrders().add(order3);
		//注意 此时 order3只是一个临时对象，因为cascade配置的是save-update,所以此处将临时对象给保存为持久化对象了
		session.update(customer6);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
