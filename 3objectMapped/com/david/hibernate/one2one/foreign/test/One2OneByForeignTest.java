package com.david.hibernate.one2one.foreign.test;

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
import com.david.hibernate.one2one.foreign.entity.Department;
import com.david.hibernate.one2one.foreign.entity.Manager;

@SuppressWarnings("deprecation")
public class One2OneByForeignTest {

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
	public void testOne2OneByForeignSave(){
		Manager mgr=new Manager();
		mgr.setMgrName("MGR-1");
		
		Department dept=new Department();
		dept.setDeptName("DEPT-1");
		
		dept.setMgr(mgr);
		/*
		 * ע��˴���˳�������ȱ��汻��������Ķ��������һ��update��䡣
		 */
		session.save(mgr);
		session.save(dept);
	}
	
	@Test
	public void testOne2OneByForeignGet(){
		Department dept=(Department) session.get(Department.class, 1);
		System.out.println(dept.getDeptName());
		//ע��˴����ܻᷢ���������쳣 
		System.out.println(dept.getMgr().getMgrName());
	}
	@Test
	public void testOne2OneByForeignGet2(){
		/*
		 * �ڲ�ѯû�������ʵ�����ʱ��ʹ���������Ӳ�ѯ��һ�������������󣬲��Ѿ����г�ʼ��
		 */
		Manager mgr=(Manager) session.get(Manager.class, 1);
		System.out.println(mgr.getMgrName());
		
		
	}
} 
	