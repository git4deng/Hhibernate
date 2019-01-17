package com.david.hibernate.unionSubclass.test;

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

import com.david.hibernate.joinedSubclass.entity.JoinedPerson;
import com.david.hibernate.joinedSubclass.entity.JoinedStudent;
import com.david.hibernate.one2one.primary.entity.DepartmentPrimary;
import com.david.hibernate.one2one.primary.entity.ManagerPrimary;
import com.david.hibernate.subclass.entity.Person;
import com.david.hibernate.subclass.entity.Student;
import com.david.hibernate.unionSubclass.entity.UnionPerson;
import com.david.hibernate.unionSubclass.entity.UnionStudent;



@SuppressWarnings("deprecation")
public class ExtendMapByUnionSubclassTest {

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
	
	/**
	 * �������: 
	 * �����������ֻ��Ѽ�¼���뵽һ�����ݱ���.
	 */
	@Test
	public void testExtendMapBySubclassSave(){
		/*
		 * SQL:	select next_hi from hibernate_unique_key for update
		 * 		update hibernate_unique_key set next_hi = ? where next_hi = ?
		 * 		insert into DAVID_UNION_PERSON (NAME, AGE, ID) values (?, ?, ?)
		 */
		UnionPerson p1=new UnionPerson();
		p1.setAge(20);
		p1.setName("P-1");
		session.save(p1);
		
		/*
		 * SQL:
		 * 		insert into DAVID_UNION_STUDENT (NAME, AGE, school, ID) values (?, ?, ?, ?)
		 */
		UnionStudent stu1=new UnionStudent();
		stu1.setAge(21);
		stu1.setName("STU-1");
		stu1.setSchool("school-1");
		session.save(stu1);
		
	}
	/** 
	 * ��ѯ:
	 * 1. ��ѯ�����¼, ��Ѹ�����ӱ��¼���ܵ�һ��������ѯ. �����Բ�. 
	 * 2. ���������¼, Ҳֻ��Ҫ��ѯһ�����ݱ�
	 * �ŵ�:
	 * 1. ����ʹ�ñ������.
	 * 2. ������е��ֶ�����ӷǿ�Լ��.
	 * ȱ��:
	 * 1. ����������ֶ�
	 * 2. �����¸�����ֶ�, ����µ�Ч�ʽϵ�
	 */
	@Test
	public void testExtendMapBySubclassQuery(){
		/*
		 * SQL: 
		 * 		select unionperso0_.ID as ID1_18_, unionperso0_.NAME as NAME2_18_, unionperso0_.AGE as AGE3_18_, 
		 * 		unionperso0_.school as school1_19_, unionperso0_.clazz_ as clazz_ from ( select ID, NAME, AGE, null as 
		 * 		school, 0 as clazz_ from DAVID_UNION_PERSON union select ID, NAME, AGE, school, 1 as clazz_ from 
		 * 		DAVID_UNION_STUDENT ) unionperso0_
		 */
		List<UnionPerson> persons = session.createQuery("FROM UnionPerson").list();
		System.out.println(persons.size());
		/*
		 *SQL: 
		 *	select unionstude0_.ID as ID1_18_, unionstude0_.NAME as NAME2_18_, unionstude0_.AGE as AGE3_18_, 
		 *	unionstude0_.school as school1_19_ from DAVID_UNION_STUDENT unionstude0_
		 */
		List<UnionStudent> students = session.createQuery("FROM UnionStudent").list();
		System.out.println(students.size());
		
	}
} 
	