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
		 * 注意此处的顺序，这里先保存被外键关联的对象可以少一条update语句。
		 */
		session.save(mgr);
		session.save(dept);
	}
	
	@Test
	public void testOne2OneByForeignGet(){
		Department dept=(Department) session.get(Department.class, 1);
		System.out.println(dept.getDeptName());
		//注意此处可能会发生懒加载异常 
		System.out.println(dept.getMgr().getMgrName());
	}
	@Test
	public void testOne2OneByForeignGet2(){
		/*
		 * 在查询没有外键的实体对象时，使用左外连接查询，一并查出其关联对象，并已经进行初始化
		 */
		Manager mgr=(Manager) session.get(Manager.class, 1);
		System.out.println(mgr.getMgrName());
		
		
	}
} 
	