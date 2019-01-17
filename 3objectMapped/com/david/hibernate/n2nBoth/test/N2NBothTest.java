package com.david.hibernate.n2nBoth.test;

import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.david.hibernate.n2nBoth.entity.CategoryBoth;
import com.david.hibernate.n2nBoth.entity.ItemBoth;
/**
 * 双向一对多
 * @author david
 *
 */
@SuppressWarnings("deprecation")
public class N2NBothTest {

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
	public void testManyToManyBothSave(){
		CategoryBoth category1=new CategoryBoth();
		category1.setName("C-1");
		
		CategoryBoth category2=new CategoryBoth();
		category2.setName("C-2");
		
		ItemBoth item1=new ItemBoth();
		item1.setName("I-1");
		
		ItemBoth item2=new ItemBoth();
		item2.setName("I-2");
		
		//设定关联关系
		category1.getItems().add(item1);
		category1.getItems().add(item2);
		
		category2.getItems().add(item1);
		category2.getItems().add(item2);
		
		item1.getCategories().add(category1);
		item1.getCategories().add(category2);
		
		item2.getCategories().add(category1);
		item2.getCategories().add(category2);
		
		session.save(category1);
		session.save(category2);
		
		session.save(item1);
		session.save(item2);
	}
	
	@Test
	public void testManyToManyBothGet(){
		CategoryBoth category1=(CategoryBoth) session.get(CategoryBoth.class,3);
		System.out.println(category1.getName());
		//查询Set<Category>需要连接中间表
		Set<ItemBoth> items = category1.getItems();
		System.out.println(items);
		
		ItemBoth item=(ItemBoth) session.get(ItemBoth.class, 3);
		System.out.println(item.getName());
		
		System.out.println(item.getCategories().size());
	}
}
