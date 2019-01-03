package com.david.hibernate.session.test;

import static org.junit.Assert.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.david.hibernate.session.entities.Article;

public class SessionTest {

	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transaction;
	
	@Before
	public void initSession(){
		Configuration configuration=new Configuration().configure();
		ServiceRegistry serviceRegistry=new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		
		sessionFactory=configuration.buildSessionFactory(serviceRegistry);
		session=sessionFactory.openSession();
		transaction=session.beginTransaction();
	}
	
	@After
	public void destroy(){
		transaction.commit();
		session.close();
		sessionFactory.close();
	}
	
	/**
	 * ����session����
	 */
	@Test
	public void testSessionCache() {
		Article article = (Article) session.get(Article.class, 1);
		System.out.println(article);
		
		Article article2 = (Article) session.get(Article.class, 1);
		System.out.println(article2);
		System.out.println(article==article2);// true ˵��ȡ������ͬһ������
		/**
		 * ���ϴ���ִ�У�����̨�������һ��sql��ѯ��䣬ԭ����session�Ļ��棬�������ѯһ��ʵ�����ʱ�����Ȼ�����session�в���
		 * �������ڸö���ֱ��ȡ�أ������������ͨ��session�����ݿ���ȥȡ�����session������Լ��ٳ���������ݿ�Ĵ�����
		 * ֻҪsession����������û�н�������û�������������������session�����еĶ���Ҳ��������������ڡ�
		 */
	}
	
	/**
	 * ����session��flush()����:ʹ���ݱ��е����ݺ�session�����еĶ��󱣳�һ�£�����ܷ��Ͷ�Ӧ��update���
	 */
	@Test
	public void testSessionFlush() {
		Article article1 = (Article) session.get(Article.class, 1);
		article1.setName("david_wei");
		/**
		 * ���ϴ��룬�޸�ʵ����������ֵ������̨������ѯ��update��sql,���ݿ���������Ӧ�ķ����˸ı䣬ԭ����
		 * ��transaction�ύ����֮ǰ��session�Ὣ�����еĶ���ͨ��flush�����������ݸ���ֱ���ݿ⡣
		 * transaction.commit()֮ǰ���ж�session����Ķ���״̬�Ƿ񱣳�һ�£�����flush()���������ύ����
		 */
		
		//Ҳ������ʾ�ĵ���session��flush����
//		session.flush();
		/**
		 * ��Ҫע����ǣ�flush�������ܻᷢ��sql��䣬���ǲ����ύ����
		 * ����:��δ�ύ�������ʾ�ص���flush������ʱ��Ҳ�п��ܻ����flush������
		 * ���磺
		 * ��1��ִ��HQL��QBC(QBC(Query By Criteria) API�ṩ�˼����������һ�ַ�ʽ������Ҫ��Criteria�ӿڡ�Criterion�ӿں�Expresson����ɣ���֧��������ʱ��̬���ɲ�ѯ��䡣)
		 * 	��ѯ�����Ƚ���flush�������Ա�֤�õ������������µġ�
		 * ��2������¼��id�ǵ������ݿ�ײ������ķ�ʽ���ɵģ����ڵ���save�����󣬻���������insert��䣨����flush)�������������ڵȵ�commit��ʱ��ŵ���
		 * ��Ϊsave����֮����뱣֤�����id�Ǵ��ڵġ����ǣ����id��hibernate���ɵľͲ��ᷢ��insert��䣬��hibernate��hilo���ԣ��ߵ��㷨����
		 * 
		 */
		//ִ�в�ѯ֮ǰ������flush�������������update��䣬Ȼ����ִ��select���
		Article article2 = (Article) session.createCriteria(Article.class).uniqueResult();
		//ע�⣬�����������ʱ�򣬲�û���ύ���񣬼����ݿ�����ݲ�δ���£��������������ȴ���޸�֮��ġ�
		System.out.println(article2);
	}
	/**
	 * ����֪ʶ�㣺
	 * 1.�����ص㣺ACID,ԭ���ԣ�Atomicity��һ���ԣ�Consistency���������ԣ�Isolation�����־��ԣ�Durability��
	 * 2.�����ǵ����⣺
	 * 	1��.����������ָ��һ��������������ȡ����һ��δ�ύ�������е�����
	 * 	2��.�����ض����������ظ�����ָ�ڶ������ݿ��е�ĳ�����ݣ�һ������Χ�ڶ�β�ѯȴ�����˲�ͬ������ֵ�����������ڲ�ѯ���������һ�������޸Ĳ��ύ��
	 * 	3��.�ö�:�ö�������Ƕ���ִ��ʱ������һ��������������T1��һ���������е��е�ĳ�����������˴ӡ�1���޸�Ϊ��2���Ĳ�������ʱ����T2�ֶ�������в�����һ�������
	 * 	��������������ֵ����Ϊ��1�������ύ�����ݿ⡣����������T1���û�����ٲ鿴�ո��޸ĵ����ݣ��ᷢ�ֻ���һ��û���޸ģ���ʵ�����Ǵ�����T2����ӵģ��ͺ��������
	 * 	��һ��������Ƿ����˻ö�.
	 * 3.���ݿ��ṩ��4��������뼶��
	 * Serializable (���л�)���ɱ�������������ظ������ö��ķ�����
	 * Repeatable read (���ظ���)���ɱ�������������ظ����ķ�����
	 * Read committed (�����ύ)���ɱ�������ķ�����
 	 * Read uncommitted (��δ�ύ)����ͼ����κ�������޷���֤
	 * 
	 * ����MySQL��֧����4�ָ��뼶��Ĭ����repeatable read(���ظ���),Oracle����֧��Read committed��Serializable��Ĭ���Ƕ����ύRead committed
	 */

	/**
	 * refresh():��ǿ�Ʒ���select��䣬��ʹsession��������ݺ����ݱ��е����ݱ���һ�¡�
	 */
	@Test
	public void testSessionRefresh() {
		Article article1 = (Article) session.get(Article.class, 1);
		System.out.println(article1);
		/**
		 * �����ڵ�һ����ӡ�͵ڶ�����ӡ֮�����ݿ�����ݷ����仯�ˣ���ʱ����refresh�����Ὣ���ݿ���������ݸ�ͬ����session�����У�
		 * ��ִ����һ��select�����������������ݿ��������뼶���йأ�Ĭ�ϵ�������뼶���ǣ�REPEATABLE READ�����ظ�������������hibernate�������ļ�������
		 * hibernate.connection.isolation����������������뼶�𣬴ӵ͵��߷ֱ���1,2,4,8
		 */
		session.refresh(article1);
		
		System.out.println(article1);
	}
	/**
	 * clear()�����������档
	 */
	@Test
	public void testSessionClear() {
		Article article = (Article) session.get(Article.class, 1);
		System.out.println(article);
		//������
		session.clear();
		
		Article article2 = (Article) session.get(Article.class, 1);
		System.out.println(article2);
		
		System.out.println(article==article2);
		/**
		 * ���ϴ���ִ�����2��select��䣬������false,�����clear����������
		 */
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
