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
		
		//1. ����һ�� SessionFactory ����
		/**
		 * SessionFactory�ӿڣ�����session����Ĺ������̰߳�ȫ�ġ�SessionFactory�ǳ��ķ���Դ��һ���ʼ��
		 * һ��SessionFactory����
		 */
		SessionFactory sessionFactory = null;
		
		//1). ���� Configuration ����: ��Ӧ hibernate �Ļ���������Ϣ�� �����ϵӳ����Ϣ
		/** 
		 * Configuration�������hibernate��������Ϣ���������ݿ����ӵ�������Ϣ�������ϵӳ���
		 * ����Configuration������2�ַ�ʽ��
		 * ��ʽһ��new Configuration().configure()�����ַ�ʽ����Ĭ�ϼ���hiberante.cfg.xml�����ļ�
		 * ��ʽ������ʾ��ָ�������ļ���λ�ã�ͨ���ļ�File���� File file=new File(path);����configure()
		 * �Ĵ��η��� new Configuration().configure(file);����
		 */
		Configuration configuration = new Configuration().configure();
		
		//4.0 ֮ǰ��������
		/**
		 * SessionFactory���󴴽��ķ�ʽһ����configuration���󴴽�configuration.buildSessionFactory();
		 */
		//sessionFactory = configuration.buildSessionFactory();
		
		//2). ����һ�� ServiceRegistry ����: hibernate 4.x ����ӵĶ���
		/**
		 * hibernate ���κ����úͷ�����Ҫ�ڸö�����ע��������Ч.
		 */
		ServiceRegistry serviceRegistry = 
				new ServiceRegistryBuilder().applySettings(configuration.getProperties())
				                            .buildServiceRegistry();
		//3).
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		
		//2. ����һ�� Session ����
		/**
		 * Session�ӿڣ������ݿ���Ӧ�ó���֮�佻����һ�����̶߳�����hibernate�����������ġ�
		 * ���г־û�����������session��������²��ܽ��С�session�������ں̡ܶ�
		 * session������hibernate��һ�����棬ִ��flush����֮ǰ���־û����󶼱�����session������
		 */
		Session session = sessionFactory.openSession();
		
		//3. ��������
		Transaction transaction = session.beginTransaction();
		
		//4. ִ�б������
		News news = new News("David's first Hibernate program!", "David", new Date(new java.util.Date().getTime()));
		session.save(news);
		
		//5. �ύ���� 
		transaction.commit();
		
		//6. �ر� Session
		session.close();
		
		//7. �ر� SessionFactory ����
		sessionFactory.close();
	}
	
}
