package com.david.hibernate.helloworld.test;

import java.sql.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Test;

import com.david.hibernate.helloworld.entity.News;

public class HelloWorldTest {

	@Test
	public void test() {
		
		System.out.println("test...");
		
		//1. 创建一个 SessionFactory 对象
		/**
		 * SessionFactory接口，生成session对象的工厂，线程安全的。SessionFactory非常耗费资源，一般初始化
		 * 一个SessionFactory对象。
		 */
		SessionFactory sessionFactory = null;
		
		//1). 创建 Configuration 对象: 对应 hibernate 的基本配置信息和 对象关系映射信息
		/** 
		 * Configuration负责管理hibernate的配置信息，包括数据库连接的配置信息，对象关系映射等
		 * 创建Configuration对象有2种方式：
		 * 方式一：new Configuration().configure()，这种方式采用默认加载hiberante.cfg.xml配置文件
		 * 方式二：显示地指定配置文件的位置，通过文件File引入 File file=new File(path);利用configure()
		 * 的带参方法 new Configuration().configure(file);创建
		 */
		Configuration configuration = new Configuration().configure();
		
		//4.0 之前这样创建
		/**
		 * SessionFactory对象创建的方式一：由configuration对象创建configuration.buildSessionFactory();
		 */
		//sessionFactory = configuration.buildSessionFactory();
		
		//2). 创建一个 ServiceRegistry 对象: hibernate 4.x 新添加的对象
		/**
		 * hibernate 的任何配置和服务都需要在该对象中注册后才能有效.
		 */
		ServiceRegistry serviceRegistry = 
				new ServiceRegistryBuilder().applySettings(configuration.getProperties())
				                            .buildServiceRegistry();
		//3).
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		
		//2. 创建一个 Session 对象
		/**
		 * Session接口：是数据库与应用程序之间交互的一个单线程对象，是hibernate核心运作中心。
		 * 所有持久化操作必须在session对象管理下才能进行。session生命周期很短。
		 * session对象是hibernate的一级缓存，执行flush方法之前，持久化对象都保存在session缓存中
		 */
		Session session = sessionFactory.openSession();
		
		//3. 开启事务
		Transaction transaction = session.beginTransaction();
		
		//4. 执行保存操作
		News news = new News("David's first Hibernate program!", "David", new Date(new java.util.Date().getTime()));
		session.save(news);
		
		//5. 提交事务 
		transaction.commit();
		
		//6. 关闭 Session
		session.close();
		
		//7. 关闭 SessionFactory 对象
		sessionFactory.close();
	}
	
}
