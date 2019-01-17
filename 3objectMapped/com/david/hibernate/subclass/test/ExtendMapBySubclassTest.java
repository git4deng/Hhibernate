package com.david.hibernate.subclass.test;

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

import com.david.hibernate.one2one.primary.entity.DepartmentPrimary;
import com.david.hibernate.one2one.primary.entity.ManagerPrimary;
import com.david.hibernate.subclass.entity.Person;
import com.david.hibernate.subclass.entity.Student;



@SuppressWarnings("deprecation")
public class ExtendMapBySubclassTest {

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
	 * 1. �����������ֻ��Ѽ�¼���뵽һ�����ݱ���.
	 * 2. ��������� Hibernate �Զ�ά��. ��type)
	 */
	@Test
	public void testExtendMapBySubclassSave(){
		/*
		 * SQL:
		 * 		insert into DAVID_PERSON (NAME, AGE, type) values (?, ?, 'person')
		 */
		Person p1=new Person();
		p1.setAge(20);
		p1.setName("P-1");
		session.save(p1);
		
		/*
		 * SQL:
		 * 		insert into DAVID_PERSON (NAME, AGE, school, type) values (?, ?, ?, 'student')
		 */
		Student stu1=new Student();
		stu1.setAge(21);
		stu1.setName("STU-1");
		stu1.setSchool("school-1");
		session.save(stu1);
		
	}
	/**
	 * ��ѯ:
	 * 1. ��ѯ�����¼, ֻ��Ҫ��ѯһ�����ݱ�
	 * 2. ���������¼, Ҳֻ��Ҫ��ѯһ�����ݱ�
	 * ȱ��:
	 * 1. ʹ���˱������.
	 * 2. ������е��ֶβ�����ӷǿ�Լ��.
	 * 3. ���̳в�ν���, �����ݱ���ֶ�Ҳ��϶�. 
	 */
	@Test
	public void testExtendMapBySubclassQuery(){
		/*
		 * Hibernate: select person0_.ID as ID1_15_, person0_.NAME as NAME3_15_, person0_.AGE as AGE4_15_, 
		 * person0_.school as school5_15_, person0_.type as type2_15_ from DAVID_PERSON person0_
		 */
		List<Person> persons = session.createQuery("FROM Person").list();
		System.out.println(persons.size());
		/*
		 * Hibernate: select student0_.ID as ID1_15_, student0_.NAME as NAME3_15_, student0_.AGE as AGE4_15_,
		 *  student0_.school as school5_15_ from DAVID_PERSON student0_ where student0_.type='student'
		 */
		List<Student> students = session.createQuery("FROM Student").list();
		System.out.println(students.size());
		
	}
} 
	