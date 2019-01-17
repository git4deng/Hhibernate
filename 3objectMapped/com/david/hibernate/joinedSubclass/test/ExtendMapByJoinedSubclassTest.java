package com.david.hibernate.joinedSubclass.test;

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



@SuppressWarnings("deprecation")
public class ExtendMapByJoinedSubclassTest {

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
	 *  �����������������Ҫ���뵽�������ݱ���,�Ӻ͸���ֱ𱣴���2�ű���.����Ч����Ե�һЩ 
	 */
	@Test
	public void testExtendMapBySubclassSave(){
		/*
		 * SQL:
		 * 		Hibernate: insert into DAVID_JOINED_PERSON (NAME, AGE) values (?, ?)
		 */
		JoinedPerson p1=new JoinedPerson();
		p1.setAge(20);
		p1.setName("P-1");
		session.save(p1);
		
		/*
		 * SQL:
		 * 		Hibernate: insert into DAVID_JOINED_PERSON (NAME, AGE) values (?, ?)
		 *		Hibernate: insert into DAVID_JOINED_STUDENT (school, STUDENT_ID) values (?, ?)
		 */
		JoinedStudent stu1=new JoinedStudent();
		stu1.setAge(21);
		stu1.setName("STU-1");
		stu1.setSchool("school-1");
		session.save(stu1);
		
	}
	/**
	 * ��ѯ:
	 * 1. ��ѯ�����¼, ��һ���������Ӳ�ѯ(Ч�ʽϵ�)
	 * 2. ���������¼, ��һ�������Ӳ�ѯ(Ч�ʽϵ�)
	 * �ŵ�:
	 * 1. ����Ҫʹ���˱������.
	 * 2. ������е��ֶ�����ӷǿ�Լ��.
	 * 3. û��������ֶ�. 
	 */
	@Test
	public void testExtendMapBySubclassQuery(){
		/*
		 * SQL: 
		 * 		select joinedpers0_.ID as ID1_11_, joinedpers0_.NAME as NAME2_11_, joinedpers0_.AGE as AGE3_11_,
		 * 		joinedpers0_1_.school as school2_12_, case when joinedpers0_1_.STUDENT_ID is not null then 1 
		 * 		when joinedpers0_.ID is not null then 0 end as clazz_ from DAVID_JOINED_PERSON joinedpers0_ 
		 * 		left outer join DAVID_JOINED_STUDENT joinedpers0_1_ on joinedpers0_.ID=joinedpers0_1_.STUDENT_ID
		 */
		List<JoinedPerson> persons = session.createQuery("FROM JoinedPerson").list();
		System.out.println(persons.size());
		/*
		 *SQL: 
		 *	select joinedstud0_.STUDENT_ID as ID1_11_, joinedstud0_1_.NAME as NAME2_11_, joinedstud0_1_.AGE as AGE3_11_, 
		 *	joinedstud0_.school as school2_12_ from DAVID_JOINED_STUDENT joinedstud0_ inner join DAVID_JOINED_PERSON 
		 *	joinedstud0_1_ on joinedstud0_.STUDENT_ID=joinedstud0_1_.ID
		 */
		List<JoinedStudent> students = session.createQuery("FROM JoinedStudent").list();
		System.out.println(students.size());
		
	}
} 
	