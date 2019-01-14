package com.david.hibernate.n21both.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.david.hibernate.n21both.entity.CustomerBoth;
import com.david.hibernate.n21both.entity.OrderBoth;
/**
 * ˫��һ�Զ�
 * @author david
 *
 */
@SuppressWarnings("deprecation")
public class N21BothTest {

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
	public void testManyToOneBothSave(){
		CustomerBoth customer=new CustomerBoth();
		customer.setCustomerName("CC");
		
		OrderBoth order1=new OrderBoth();
		order1.setOrderName("O-5");
		OrderBoth order2=new OrderBoth();
		order2.setOrderName("O-6");
		//�趨������ϵ
		//�趨n�˵Ĺ�����ϵ
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		//�趨1�˵Ĺ�����ϵ
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		/*
		 *˫��һ�Զ��ȱ���1��ˣ�3��insert,2��update��update�Ȳ�������,��Ҫ��ά��1���
		 *��Ӧ����Ƕ˵Ĺ�ϵ������n�˴����ʱ��1�˵�oid�Ѿ������ˣ���˻���ȴ�n��n��update���
		 *�Ƽ�ʹ�ô˷�ʽ��
		 */
		/*
		 * ������1�˵�set�ڵ�ָ��inverse=true��ʹ1�˷���ά��������ϵ�������Ͳ���ִ��update
		 * 1�˵ı��ˣ���customer��������ˣ�������1�������������inverse=true���Ȳ���1�˺�
		 * ����n�ˣ��ô����ǲ�����update���
		 */
		session.save(customer);
		session.save(order1);
		session.save(order2);
		
		
		CustomerBoth customer1=new CustomerBoth();
		customer1.setCustomerName("DD");
		
		OrderBoth order3=new OrderBoth();
		order3.setOrderName("O-7");
		OrderBoth order4=new OrderBoth();
		order4.setOrderName("O-8");
		
		order3.setCustomer(customer1);
		order4.setCustomer(customer1);
		
		customer1.getOrders().add(order3);
		customer1.getOrders().add(order4);
		/*
		 *�ȱ���n�ˣ��󱣴�1�ˣ�����2*n��update��n+1��insert
		 *���ȴ�n����ȷ��1��oid,�����1�����Ż����n�Ĺ�����ϵ��ͬʱ�������1�˶�Ӧn�Ĺ�����ϵ
		 *��˻���2*n��update��� 
		 */
		session.save(order3);
		session.save(order4);
		session.save(customer1);	
	}
	
	@Test
	public void testManyToOneBothGet(){
		CustomerBoth customer=(CustomerBoth) session.get(CustomerBoth.class, 1);
		//��n��һ�˼���ʹ���ӳټ���
		System.out.println(customer.getCustomerName());
		//���ض��һ�˵ļ�����Hibernate�����ü��϶���org.hibernate.collection.internal.PersistentSet��
		//�����;����ӳټ��غʹ�Ŵ������Ĺ��ܣ�ͬ��Ҳ���ܻ��׳��������쳣
		System.out.println(customer.getOrders().getClass());
		//����Ҫʹ�ü���Ԫ�ص�ʱ����г�ʼ��
	}
	
	@Test
	public void testManyToOneBothUpdate(){
		CustomerBoth customer=(CustomerBoth) session.get(CustomerBoth.class, 1);
		customer.getOrders().iterator().next().setOrderName("XXXX");
	}
	/**
	 * cascade����Ϊdelete������ɾ��
	 * <set name="orders" table="DAVID_ORDER_BOTH" inverse="true" cascade="delete">
	 */
	@Test
	public void testManyToOneDelete(){
		/*
		 * ��û���趨������ϵ������£��Ҳ���ֱ��ɾ��1��һ�˵Ķ���1��һ�˶�����n�����������
		 * ������������������ֱ��ɾ��,������cascade��ֵΪdelete��ɾ��1��Ӧ�ļ�¼�Լ�������n�ļ�¼
		 */
		CustomerBoth customer2=(CustomerBoth) session.get(CustomerBoth.class, 2);
		session.delete(customer2);
	}
	/**
	 * cascade����Ϊdelete-orphan:ɾ�����к͵�ǰ��������ϵ�Ķ���
	 * <set name="orders" table="DAVID_ORDER_BOTH" inverse="true" cascade="delete-orphan">
	 */
	@Test
	public void testManyToOneDeleteOrphan(){
		
		CustomerBoth customer2=(CustomerBoth) session.get(CustomerBoth.class, 1);
		//ע����delete�Ƚϣ�����ֻ�ǽ����OIDΪ1��������Ĺ�����ϵ����������cascade=delete-orphan����ô
		//���set��ɾ�������Ӧorder�ļ�¼�����ǲ���ɾ��customer��¼
		customer2.getOrders().clear();
	}
	/**
	 * cascade����Ϊsave-update:�������棬��ͨ��session��save��update�Լ�saveOrUpdate����
	 * ��������߸��¶���ʱ�������������й������½�����ʱ���󣬲��Ҽ����������й������������
	 * <set name="orders" table="DAVID_ORDER_BOTH" inverse="true" cascade="save-update">
	 */
	@Test
	public void testManyToOneSaveOrUpdate(){
		//1.save
		CustomerBoth customer=new CustomerBoth();
		customer.setCustomerName("CASCADE");
		
		OrderBoth order1=new OrderBoth();
		order1.setOrderName("CA-1");
		OrderBoth order2=new OrderBoth();
		order2.setOrderName("CA-2");
		
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		session.save(customer);
		
		//2.update
		CustomerBoth customer6=(CustomerBoth) session.get(CustomerBoth.class, 6);
		OrderBoth order3=new OrderBoth();
		order3.setOrderName("CA-3");
		order3.setCustomer(customer6);
		customer6.getOrders().add(order3);
		//ע�� ��ʱ order3ֻ��һ����ʱ������Ϊcascade���õ���save-update,���Դ˴�����ʱ���������Ϊ�־û�������
		session.update(customer6);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
