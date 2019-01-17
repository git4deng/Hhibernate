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
		 * 注意主键关联映射不受insert顺序的影响，必须先插入Manager,因为Manger的主键不可能为空，必须先插入主键实体
		 */
		session.save(dept);
		session.save(mgr);
		
	}
	
	@Test
	public void testOne2OneByPrimaryGet(){
		DepartmentPrimary dept=(DepartmentPrimary) session.get(DepartmentPrimary.class, 1);
		System.out.println(dept.getDeptName());
		//注意此处可能会发生懒加载异常 
		System.out.println(dept.getMgr().getClass());
	}
	@Test
	public void testOne2OneByPrimaryGet2(){
		/*
		 * 在查询没有外键的实体对象时，使用左外连接查询，一并查出其关联对象，并已经进行初始化.因为在manger表中无外键，所以一次性查出来了。
		 */
		ManagerPrimary mgr=(ManagerPrimary) session.get(ManagerPrimary.class, 1);
		System.out.println(mgr.getMgrName());
		
		
	}
} 
	