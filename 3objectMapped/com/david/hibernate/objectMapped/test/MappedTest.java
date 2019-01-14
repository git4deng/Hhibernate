package com.david.hibernate.objectMapped.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.david.hibernate.objectMapped.entities.Pay;
import com.david.hibernate.objectMapped.entities.Worker;

@SuppressWarnings("deprecation")
public class MappedTest {

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
	public void testComponent(){
		Worker worker=new Worker();
		worker.setName("AA");
		Pay pay=new Pay();
		pay.setMonthPay(10000);
		pay.setYearPay(100000);
		pay.setVocationWithPay(5);
		
		worker.setPay(pay);
		
		session.save(worker);
	}
}
