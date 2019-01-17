package com.david.hibernate.n2n.test;

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
import com.david.hibernate.n2n.entity.Category;
import com.david.hibernate.n2n.entity.Item;
/**
 * 双向一对多
 * @author david
 *
 */
@SuppressWarnings("deprecation")
public class N2NTest {

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
	public void testManyToManySave(){
		Category category1=new Category();
		category1.setName("C-1");
		
		Category category2=new Category();
		category2.setName("C-2");
		
		Item item1=new Item();
		item1.setName("I-1");
		
		Item item2=new Item();
		item2.setName("I-2");
		
		//设定关联关系
		category1.getItems().add(item1);
		category1.getItems().add(item2);
		
		category2.getItems().add(item1);
		category2.getItems().add(item2);
		
		session.save(category1);
		session.save(category2);
		
		session.save(item1);
		session.save(item2);
	}
	
	@Test
	public void testManyToManyGet(){
		Category category1=(Category) session.get(Category.class, 1);
		System.out.println(category1.getName());
		//查询Set<Category>需要连接中间表
		System.out.println(category1.getItems());
	}
}
