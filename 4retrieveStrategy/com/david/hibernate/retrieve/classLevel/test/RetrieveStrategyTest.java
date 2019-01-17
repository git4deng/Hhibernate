package com.david.hibernate.retrieve.classLevel.test;

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

import com.david.hibernate.retrieve.classLevel.entity.ReCustomer;
import com.david.hibernate.retrieve.classLevel.entity.ReOrder;


@SuppressWarnings("deprecation")
public class RetrieveStrategyTest {

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
	public void testManyToOneSave(){
		ReCustomer ReCustomer=new ReCustomer();
		ReCustomer.setCustomerName("AA");
		
		ReOrder ReOrder1=new ReOrder();
		ReOrder1.setOrderName("O-1");
		ReOrder ReOrder2=new ReOrder();
		ReOrder2.setOrderName("O-2");
		
		ReOrder1.setCustomer(ReCustomer);
		ReOrder2.setCustomer(ReCustomer);
		
		session.save(ReCustomer);
		session.save(ReOrder1);
		session.save(ReOrder2);
		
		
		ReCustomer ReCustomer1=new ReCustomer();
		ReCustomer1.setCustomerName("BB");
		
		ReOrder ReOrder3=new ReOrder();
		ReOrder3.setOrderName("O-3");
		ReOrder ReOrder4=new ReOrder();
		ReOrder4.setOrderName("O-4");
		
		ReOrder3.setCustomer(ReCustomer1);
		ReOrder4.setCustomer(ReCustomer1);
		
		session.save(ReOrder3);
		session.save(ReOrder4);
		session.save(ReCustomer1);	
	}
	/**
	 * �༶��ļ�������
	 * 	��������: �������ؼ�������ָ���Ķ���
	 * 	�ӳټ���: �ӳټ��ؼ�������ָ���Ķ�����ʹ�þ��������ʱ���ٽ��м���
	 * ע�⣺���� <class> Ԫ�ص� lazy ������ true ���� false, Session �� get() ������ Query �� list() �������༶������ʹ��������������
	 * �� lazy ����ֻ��load������Ч
	 */
	@Test
	public void testClassLevelStrategy(){
		ReCustomer c=(ReCustomer) session.load(ReCustomer.class, 1);
		/* ��ӳ���ļ���class��ǩ lazy����δ����ʱ��load������ȡ�����Ǵ������
		 * class com.david.hibernate.retrieve.classLevel.entity.ReCustomer_$$_jvst38f_2
		 * ������lazy=false(Ĭ����true)���ȡ���Ƕ�����
		 * class com.david.hibernate.retrieve.classLevel.entity.ReCustomer
		 */
		System.out.println(c.getClass());
		/*
		 * �� Hibernate ������ʱ���� CGLIB ���߶�̬����Hibernate ����������ʵ��ʱ, ����ʼ���� OID ����
		 * ��ʹ��id�ǲ��ᷢ��select����
		 */
		System.out.println(c.getCustomerId());
		
	}
	/**
	 * һ�Զ��������
	 */
	@Test
	public void testOne2ManyLevelStrategy(){
		//1.set��lazy����
		ReCustomer c=(ReCustomer) session.get(ReCustomer.class, 1);
		System.out.println(c.getCustomerName());
		/*
		 * 1-n,n-n�ļ�������Ĭ��ʹ�õ��������صļ�������,����ͨ������set ��lazy�������޸�Ĭ�ϵļ������ԣ�Ĭ��Ϊtrue
		 * ���������޸�lazy����Ϊfalse,���������ᷢ��2��select��䣬Ӱ�����ܡ�����lazy����ֵ����������Ϊextra,��ǿ��
		 * �ӳټ���,��ȡֵ�ᾡ���ܵ��ӳٳ�ʼ����ʱ������������ ��ȡorder��size,����������һ��
		 * select count(ORDER_ID) from DAVID_ORDER_RETRIEVE where CUSTOMER_ID =?
		 * �����ǰ����й�����order�����
		 */
		System.out.println(c.getOrders().size());
		
		
	}
	/**
	 * <set> Ԫ�ص� batch-size ����,����Ϊ�ӳټ������Ի��������������趨��������������. 
	 * ���������ܼ��� SELECT ������Ŀ, ����ӳټ�����������������������
	 */
	@Test
	public void testSetBatchSize(){
		List<ReCustomer> customers=session.createQuery("FROM ReCustomer").list();
		System.out.println(customers.size());
		/*
		 * set Ԫ�ص�batch-size��������ָ��һ�γ�ʼ��set���ϵ����������������ֵ����ôset�е�ÿ��Ԫ�صĳ�ʼ������
		 * ����һ��select��䣬��������˸����ԣ���ô����set.size/n��select��䣬����in(�����ķ�ʽ)����ʼ�����Ԫ��
		 * 
		 * select orders0_.CUSTOMER_ID as CUSTOMER3_7_1_, orders0_.ORDER_ID as ORDER_ID1_18_1_, 
		 * orders0_.ORDER_ID as ORDER_ID1_18_0_, orders0_.ORDER_NAME as ORDER_NA2_18_0_, 
		 * orders0_.CUSTOMER_ID as CUSTOMER3_18_0_ from DAVID_ORDER_RETRIEVE orders0_ 
		 * where orders0_.CUSTOMER_ID in (?, ?, ?, ?)
		 * 
		 */
		for(ReCustomer c:customers){
			if(c.getOrders()!=null)
				System.out.println(c.getOrders().size());
		}
	}
	/**
	 * fetch: ȡֵΪ ��select�� �� ��subselect�� ʱ, ������ʼ�� orders �Ĳ�ѯ������ʽ;  ��ȡֵΪ��join��, ����� orders ���ϱ���ʼ����ʱ��
	 */
	@Test
	public void testSetFetch(){
		/*
		 * fetch����ȡֵ��
		 * select:Ĭ��ȡֵ��������ʽ��ѯ��ʼ��set����Ԫ��
		 * subselect:�����Ӳ�ѯ�ķ�ʽ������bitch-size����ֵ����Ϊ�Ӳ�ѯ��Ϊwhere�Ӿ�in���������֣�
		 * ����ѯ��1�˵�����id,����bitch-size��Ч������lazy��Ч
		 * 	select
		 *       orders0_.CUSTOMER_ID as CUSTOMER3_7_1_,
		 *       orders0_.ORDER_ID as ORDER_ID1_18_1_,
		 *       orders0_.ORDER_ID as ORDER_ID1_18_0_,
		 *       orders0_.ORDER_NAME as ORDER_NA2_18_0_,
		 *       orders0_.CUSTOMER_ID as CUSTOMER3_18_0_ 
		 *   from
		 *       DAVID_ORDER_RETRIEVE orders0_ 
		 *   where
		 *       orders0_.CUSTOMER_ID in (
		 *           select
		 *               recustomer0_.CUSTOMER_ID 
		 *           from
		 *               DAVID_CUSTOMER_RETRIEVE recustomer0_
		 *       )
		 *  join:�����������������(ͨ���������Ӽ��������ָ���Ķ�������Ķ���)�������������й����� Order ����,lazy���Ա�����
		 *  ����Query ��list() ���������ӳ���ļ������õ������������Ӽ�������, �����ɲ����ӳټ��ز���     
		 *  
		 */
		List<ReCustomer> customers=session.createQuery("FROM ReCustomer").list();
		System.out.println(customers.size());
		for(ReCustomer c:customers){
			if(c.getOrders()!=null)
				System.out.println(c.getOrders().size());
		}
	}
	@Test
	public void testSetFetch2(){
		ReCustomer c=(ReCustomer) session.get(ReCustomer.class, 1);
		/*
		 * fetch:joinʱ���ڻ�ȡcustomer��ʱ��ͳ�ʼ����set���ϣ�ͨ��left join�ķ�ʽ
			select
			    recustomer0_.CUSTOMER_ID as CUSTOMER1_7_0_,
			    recustomer0_.CUSTOMER_NAME as CUSTOMER2_7_0_,
			    orders1_.CUSTOMER_ID as CUSTOMER3_7_1_,
			    orders1_.ORDER_ID as ORDER_ID1_18_1_,
			    orders1_.ORDER_ID as ORDER_ID1_18_2_,
			    orders1_.ORDER_NAME as ORDER_NA2_18_2_,
			    orders1_.CUSTOMER_ID as CUSTOMER3_18_2_ 
			from
			    DAVID_CUSTOMER_RETRIEVE recustomer0_ 
			left outer join
			    DAVID_ORDER_RETRIEVE orders1_ 
			        on recustomer0_.CUSTOMER_ID=orders1_.CUSTOMER_ID 
			where
			    recustomer0_.CUSTOMER_ID=? 
		 */
	}
	/**
	 * ���һ�ļ�������
	 */
	@Test
	public void testMany2One(){
		/*
		 * Ĭ�������δ��ʼ��customer����,������lazy="false",�ͻ�������ʼ��customer
		 * <many-to-one name="customer" class="ReCustomer" column="CUSTOMER_ID" lazy="false"></many-to-one>
		 * 
		 */
		ReOrder order =(ReOrder) session.get(ReOrder.class, 1);
		System.out.println(order.getOrderName());
		
		/*
		 * fetch��������δjoinʱ����������customer���󣬲��������������ӵķ�ʽ
		 * <many-to-one name="customer" class="ReCustomer" column="CUSTOMER_ID"  fetch="join"/>
		 * ����Query ��list() ���������ӳ���ļ������õ������������Ӽ�������, �����ɲ����ӳټ��ز���     
		 */
		List<ReOrder> orders=session.createQuery("from ReOrder o").list();
		for(ReOrder o:orders){
			if(o.getCustomer()!=null)
				System.out.println(o.getCustomer().getCustomerName());
		}
		/*
		 *�����ʼ��costomer�����batch-size������Ч�����ǵ���class��ǩ��������
		 *<class name="ReCustomer" table="DAVID_CUSTOMER_RETRIEVE" lazy="true" batch-size="5">
		 */
	}
}
