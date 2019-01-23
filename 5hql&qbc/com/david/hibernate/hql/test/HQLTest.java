package com.david.hibernate.hql.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.david.hibernate.hql.entity.DepartmentHQL;
import com.david.hibernate.hql.entity.EmployeeHQL;

import javafx.scene.DepthTest;

@SuppressWarnings("deprecation")
public class HQLTest {

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
	 * ׼������
	 */
	@Test
	public void testhql() {
		DepartmentHQL d=new DepartmentHQL();
		d.setName("D-7");
		
		EmployeeHQL e=new EmployeeHQL();
		e.setEmail("david7.com");
		e.setName("E-7");
		e.setSalary(707);
		
		d.getEmps().add(e);
		e.setDept(d);
		
		session.save(d);
		session.save(e);
	}
	/**
	 * ռλ���ķ�ʽ
	 */
	@Test
	public void testHqlQuery() {
		//1.����query����
		String hql="FROM EmployeeHQL e where e.salary >? and e.email like ? ";
		Query query = session.createQuery(hql);
		//2.�󶨲�����֧�ֶ������setXXX�������ĵ��÷�ʽ
		query.setFloat(0, 400).setString(1, "%david%");
		//3.ִ�в�ѯ
		List<EmployeeHQL> list = query.list();
		
		System.out.println(list);
	}
	/**
	 * ָ����������������ʽ
	 */
	@Test
	public void testHqlQueryNamed() {
		//1.����query����
		String hql="FROM EmployeeHQL e where e.salary >:sal and e.email like :email order by e.id desc ";
		Query query = session.createQuery(hql);
		//2.�󶨲���
		query.setFloat("sal", 400).setString("email", "%david%");
		//3.ִ�в�ѯ
		List<EmployeeHQL> list = query.list();
		
		System.out.println(list);
	}
	/**
	 * ʹ�ö���ʵ�������ѯ
	 */
	@Test
	public void testHqlQueryEntityParmeter() {
		//1.����query����
		String hql="FROM EmployeeHQL e where e.salary >:sal and e.dept=:dept and e.email like :email ";
		Query query = session.createQuery(hql);
		//2.�󶨲���
		DepartmentHQL d=new DepartmentHQL();
		d.setId(5);
		query.setFloat("sal", 400).setString("email", "%david%").setEntity("dept", d);
		//3.ִ�в�ѯ
		List<EmployeeHQL> list = query.list();
		
		System.out.println(list);
		/*
		 * 	select
        		department0_.D_ID as D_ID1_9_0_,
        		department0_.D_NAME as D_NAME2_9_0_ 
    		from
        		DAVID_DEPARTMENT_HQL department0_ 
    		where
        		department0_.D_ID=?
		 */
	}
	/**
	 * HQL��ҳ��ѯ��hibernate���Զ���ѡ���ҳ�ķ�ʽ������MySQLʹ��limit��Oracleʹ�õ���rownumber
	 * select
        employeehq0_.E_ID as E_ID1_11_,
        employeehq0_.E_NAME as E_NAME2_11_,
        employeehq0_.E_SALARY as E_SALARY3_11_,
        employeehq0_.E_EMAIL as E_EMAIL4_11_,
        employeehq0_.D_ID as D_ID5_11_ 
      from
        DAVID_EMPLOYEE_HQL employeehq0_ limit ?
	 */
	@Test
	public void testHqlPageQuery() {
		String hql="FROM EmployeeHQL";
		Query query = session.createQuery(hql);
		int pageNo=1;
		int pageSize=5;
		
		List<EmployeeHQL> list = query.setFirstResult((pageNo-1)*pageSize).setMaxResults(pageSize).list();
		for(EmployeeHQL e:list)
			System.out.println(e);
		
	}
	/**
	 * ������ѯ��hql�����ӳ���ļ�������
	 * <query name="salaryQuery"><![CDATA[FROM EmployeeHQL e WHERE e.salary>:minSal and e.salary<:maxSal ]]></query>
	 */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void testHqlNameedQuery() {
		List<EmployeeHQL> list = session.getNamedQuery("salaryQuery")
				.setFloat("minSal", 100)
				.setFloat("maxSal", 303)
				.list();
		for(EmployeeHQL e:list)
			System.out.println(e);
	}
	/**
	 * ͶӰ��ѯ����ѯ���������ʵ��Ĳ�������. ͨ�� SELECT �ؼ���ʵ��.
	 * Query �� list() �������صļ����а��������������͵�Ԫ��, ÿ��������������ѯ�����һ����¼
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHqlFieldQuery() {
		String hql="select e.name,e.salary from EmployeeHQL e where e.dept=:dept";
		DepartmentHQL d=new DepartmentHQL();
		d.setId(5);
		List<Object[]> list = session.createQuery(hql).setEntity("dept", d).list();
		for(Object[] obj:list)
			System.out.println(Arrays.asList(obj));
	}
	/**
	 * ͶӰ��ѯ2�������ڳ־û����ж���һ������Ĺ���������װͶӰ��ѯ���صļ�¼, 
	 * ʹ�����������ȫ���������������������ʲ�ѯ�����
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHqlFieldQuery2() {
		//ע������ṩ��Ӧ�Ĺ�����
		String hql="select  new EmployeeHQL(e.salary, e.email, e.dept) from EmployeeHQL e where e.dept=:dept";
		DepartmentHQL d=new DepartmentHQL();
		d.setId(5);
		List<EmployeeHQL> list = session.createQuery(hql).setEntity("dept", d).list();
		for(EmployeeHQL e:list)
			System.out.println(Arrays.asList(e.getSalary()+","+e.getEmail()+","+e.getDept()));
		
	}
	/**
	 * �ۺϺ�����ѯ
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHqlGropuQuery() {
		String hql="SELECT max(e.salary),min(e.salary),e.dept from EmployeeHQL e "
				+ " GROUP BY e.dept "
				+ " HAVING min(e.salary)>:minsal ";
		Query query = session.createQuery(hql);
		List<Object[]> list = query.setFloat("minsal", 100).list();
		for(Object[] obj:list)
			System.out.println(Arrays.asList(obj));
	}
	/**
	 * ������������
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testLeftJoinFetch(){
		/*
		 * ��ѯ����п��ܻ�����ظ�Ԫ��, ����ͨ��һ�� DISTINCT�ؼ����������ظ�Ԫ��
		 */
		String hql="SELECT DISTINCT d FROM DepartmentHQL d LEFT JOIN FETCH d.emps";
		Query query = session.createQuery(hql);
		List<DepartmentHQL> list = query.list();
		for(DepartmentHQL d:list)
			System.out.println(d.getName()+"=="+d.getEmps().size());
		
		hql="FROM DepartmentHQL d LEFT JOIN FETCH d.emps";
		query=session.createQuery(hql);
		list=query.list();
		/*
		 * ��ѯ����п��ܻ�����ظ�Ԫ��, ����ͨ��һ�� HashSet �������ظ�Ԫ��
		 */
		list=new ArrayList<>(new LinkedHashSet<>(list));
		for(DepartmentHQL d:list)
			System.out.println(d.getName()+"^^^^^^"+d.getEmps().size());
	}
	/**
	 * ����� HQL ��û����ʽָ����������, ��ʹ��ӳ���ļ����õļ�������.
	 * HQL �����ӳ���ļ������õ������������Ӽ�������, ���ϣ�� HQL ���������������Ӳ���, �ͱ����� HQL ��ѯ�������ʽ��ָ����
	 * ���� HQL ��������ʽָ���˼�������, �ͻḲ��ӳ���ļ������õļ�������
	 */
	@Test
	public void testLeftJoin(){
		/*
		 * list() �������صļ����д�ŵ��Ƕ�����������,���������ļ������� Employee ���ϵļ�������
		 */
		String hql="FROM DepartmentHQL d LEFT JOIN  d.emps";
		Query query = session.createQuery(hql);
		List<Object[]> list = query.list();
		for(Object[] obj:list)
			System.out.println(Arrays.asList(obj));
		/*
		 * ���ϣ�� list() �������صļ����н����� Department ����, ������HQL ��ѯ�����ʹ�� SELECT �ؼ���
		 * ע���ʱ DepartmentHQL�е�set��δ����ʼ���ģ��������صļ���
		 */
		hql="SELECT DISTINCT d FROM DepartmentHQL d LEFT JOIN  d.emps";
		query = session.createQuery(hql);
		List<DepartmentHQL> depts = query.list();
		for(DepartmentHQL d:depts){
			System.out.println(d.getName()+","+d.getEmps().size());
		}
	}
	@SuppressWarnings({ "unchecked" })
	@Test
	public void testQBC1(){
		//1.����һ��Criteria����
		Criteria criteria = session.createCriteria(EmployeeHQL.class);
		//2.��Ӳ�ѯ��������QBC�в�ѯ����ʹ��Criterion����ʾ��Criterion ����ͨ�� Restrictions �ľ�̬�����õ�
		criteria.add(Restrictions.eq("name", "E-7"));
		criteria.add(Restrictions.gt("salary", 200f));
		//uniqueResult ���ؽ����Ψһ���׳��쳣
//		EmployeeHQL result = (EmployeeHQL) criteria.uniqueResult();
//		System.out.println(result);
		List<EmployeeHQL> list = criteria.list();
		
		System.out.println(list);
		/*
		 * qbc�ļ���������ӳ���ļ��еļ������Ա���һ��
		 */
		for(EmployeeHQL e:list){
			System.out.println(e.getDept().getName());
		}
	}
	/**
	 * ���������ѯ
	 */
	@Test
	public void testQBC2(){
		Criteria criteria = session.createCriteria(EmployeeHQL.class);
		//1.and
		Conjunction conjunction = Restrictions.conjunction();
		conjunction.add(Restrictions.like("name", "E",MatchMode.ANYWHERE));
		DepartmentHQL d=new DepartmentHQL();
		d.setId(2);
		conjunction.add(Restrictions.eq("dept", d));
		System.out.println(conjunction);
		/*sqlЧ��
		 * select
	        this_.E_ID as E_ID1_11_0_,
	        this_.E_NAME as E_NAME2_11_0_,
	        this_.E_SALARY as E_SALARY3_11_0_,
	        this_.E_EMAIL as E_EMAIL4_11_0_,
	        this_.D_ID as D_ID5_11_0_ 
	     from
	        DAVID_EMPLOYEE_HQL this_ 
	     where
	        (
	            this_.E_NAME like ? 
	            and this_.D_ID=?
	        )
		 */
		
		//2.OR
		Disjunction disjunction = Restrictions.disjunction();
		disjunction.add(Restrictions.gt("salary", 10f));
		disjunction.add(Restrictions.isNotNull("email"));
		System.out.println(disjunction);
		criteria.add(conjunction);
		criteria.add(disjunction);
		/* ������ѯsql
		 * select
	        this_.E_ID as E_ID1_11_0_,
	        this_.E_NAME as E_NAME2_11_0_,
	        this_.E_SALARY as E_SALARY3_11_0_,
	        this_.E_EMAIL as E_EMAIL4_11_0_,
	        this_.D_ID as D_ID5_11_0_ 
	      from
	        DAVID_EMPLOYEE_HQL this_ 
	      where
	        (
	            this_.E_NAME like ? 
	            and this_.D_ID=?
	        ) 
	        and (
	            this_.E_SALARY>? 
	            or this_.E_EMAIL is not null
	        )
		 */
		List<EmployeeHQL> list = criteria.list();
		System.out.println(list);
	}
	/**
	 * ͳ�Ʋ�ѯ
	 */
	@Test
	public void testQBC3(){
		Criteria criteria = session.createCriteria(EmployeeHQL.class);
		//ͳ�Ʋ�ѯ: ʹ�� Projection ����ʾ: ������ Projections �ľ�̬�����õ�
		criteria.setProjection(Projections.max("salary"));
		System.out.println(criteria.uniqueResult());
	}
	/**
	 * ��ҳ������
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testQBC4(){
		Criteria criteria = session.createCriteria(EmployeeHQL.class);
		criteria.addOrder(Order.desc("salary"));
		criteria.addOrder(Order.asc("id"));
		
		//2. ��ӷ�ҳ����
		int pageSize = 3;
		int pageNo = 3;
		List<EmployeeHQL> list = criteria.setFirstResult((pageNo - 1) * pageSize)
		        .setMaxResults(pageSize)
		        .list();
		System.out.println(list);
	}
	/*
	 * ԭ��sql insert����
	 */
	@Test
	public void testNativeSQL1(){
		String sql="insert into DAVID_DEPARTMENT_HQL values(?,?)";
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, 9)
		.setString(1, "D-5").executeUpdate();
		
	}
	/**
	 * ԭ��sql select ����
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testNativeSQL2(){
		String sql="select * from DAVID_DEPARTMENT_HQL ";
		
		SQLQuery query = session.createSQLQuery(sql);
		List<Object[]> list = query.list();
		System.out.println(list.size());
		for(Object[] o:list){
			System.out.println(o.length+":"+o[0]+","+o[1]);
		}
		
	}
	
}
