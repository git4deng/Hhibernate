package com.david.hibernate.one2one.primary.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.david.hibernate.one2one.primary.entity.DepartmentPrimary;
import com.david.hibernate.one2one.primary.entity.ManagerPrimary;



@SuppressWarnings("deprecation")
public class One2OneByPrimaryTest {

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
	public void testOne2OneByPrimarySave(){
		ManagerPrimary mgr=new ManagerPrimary();
		mgr.setMgrName("MGR-2");
		
		DepartmentPrimary dept=new DepartmentPrimary();
		dept.setDeptName("DEPT-2");
		
		dept.setMgr(mgr);
		/*
		 * ע����������ӳ�䲻��insert˳���Ӱ�죬�����Ȳ���Manager,��ΪManger������������Ϊ�գ������Ȳ�������ʵ��
		 */
		session.save(dept);
		session.save(mgr);
		
	}
	
	@Test
	public void testOne2OneByPrimaryGet(){
		DepartmentPrimary dept=(DepartmentPrimary) session.get(DepartmentPrimary.class, 1);
		System.out.println(dept.getDeptName());
		//ע��˴����ܻᷢ���������쳣 
		System.out.println(dept.getMgr().getClass());
	}
	@Test
	public void testOne2OneByPrimaryGet2(){
		/*
		 * �ڲ�ѯû�������ʵ�����ʱ��ʹ���������Ӳ�ѯ��һ�������������󣬲��Ѿ����г�ʼ��.��Ϊ��manger���������������һ���Բ�����ˡ�
		 */
		ManagerPrimary mgr=(ManagerPrimary) session.get(ManagerPrimary.class, 1);
		System.out.println(mgr.getMgrName());
		
		
	}
} 
	