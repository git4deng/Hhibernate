package com.david.hibernate.hql.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.david.hibernate.hql.dao.DepartmentHQLDao;
import com.david.hibernate.hql.entity.DepartmentHQL;
import com.david.hibernate.hql.entity.EmployeeHQL;
import com.david.hibernate.hql.utils.HibernateUtil;

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
	 * 准备数据
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
	 * 占位符的方式
	 */
	@Test
	public void testHqlQuery() {
		//1.创建query对象
		String hql="FROM EmployeeHQL e where e.salary >? and e.email like ? ";
		Query query = session.createQuery(hql);
		//2.绑定参数，支持对象调用setXXX方法链的调用方式
		query.setFloat(0, 400).setString(1, "%david%");
		//3.执行查询
		List<EmployeeHQL> list = query.list();
		
		System.out.println(list);
	}
	/**
	 * 指定属性名的命名方式
	 */
	@Test
	public void testHqlQueryNamed() {
		//1.创建query对象
		String hql="FROM EmployeeHQL e where e.salary >:sal and e.email like :email order by e.id desc ";
		Query query = session.createQuery(hql);
		//2.绑定参数
		query.setFloat("sal", 400).setString("email", "%david%");
		//3.执行查询
		List<EmployeeHQL> list = query.list();
		
		System.out.println(list);
	}
	/**
	 * 使用对象实体参数查询
	 */
	@Test
	public void testHqlQueryEntityParmeter() {
		//1.创建query对象
		String hql="FROM EmployeeHQL e where e.salary >:sal and e.dept=:dept and e.email like :email ";
		Query query = session.createQuery(hql);
		//2.绑定参数
		DepartmentHQL d=new DepartmentHQL();
		d.setId(5);
		query.setFloat("sal", 400).setString("email", "%david%").setEntity("dept", d);
		//3.执行查询
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
	 * HQL分页查询：hibernate会自动的选择分页的方式，比如MySQL使用limit，Oracle使用的是rownumber
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
	 * 命名查询，hql语句在映射文件中配置
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
	 * 投影查询：查询结果仅包含实体的部分属性. 通过 SELECT 关键字实现.
	 * Query 的 list() 方法返回的集合中包含的是数组类型的元素, 每个对象数组代表查询结果的一条记录
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
	 * 投影查询2：可以在持久化类中定义一个对象的构造器来包装投影查询返回的记录, 
	 * 使程序代码能完全运用面向对象的语义来访问查询结果集
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHqlFieldQuery2() {
		//注意必须提供对应的构造器
		String hql="select  new EmployeeHQL(e.salary, e.email, e.dept) from EmployeeHQL e where e.dept=:dept";
		DepartmentHQL d=new DepartmentHQL();
		d.setId(5);
		List<EmployeeHQL> list = session.createQuery(hql).setEntity("dept", d).list();
		for(EmployeeHQL e:list)
			System.out.println(Arrays.asList(e.getSalary()+","+e.getEmail()+","+e.getDept()));
		
	}
	/**
	 * 聚合函数查询
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
	 * 迫切左外连接
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testLeftJoinFetch(){
		/*
		 * 查询结果中可能会包含重复元素, 可以通过一个 DISTINCT关键字来过滤重复元素
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
		 * 查询结果中可能会包含重复元素, 可以通过一个 HashSet 来过滤重复元素
		 */
		list=new ArrayList<>(new LinkedHashSet<>(list));
		for(DepartmentHQL d:list)
			System.out.println(d.getName()+"^^^^^^"+d.getEmps().size());
	}
	/**
	 * 如果在 HQL 中没有显式指定检索策略, 将使用映射文件配置的检索策略.
	 * HQL 会忽略映射文件中设置的迫切左外连接检索策略, 如果希望 HQL 采用迫切左外连接策略, 就必须在 HQL 查询语句中显式的指定它
	 * 若在 HQL 代码中显式指定了检索策略, 就会覆盖映射文件中配置的检索策略
	 */
	@Test
	public void testLeftJoin(){
		/*
		 * list() 方法返回的集合中存放的是对象数组类型,根据配置文件来决定 Employee 集合的检索策略
		 */
		String hql="FROM DepartmentHQL d LEFT JOIN  d.emps";
		Query query = session.createQuery(hql);
		List<Object[]> list = query.list();
		for(Object[] obj:list)
			System.out.println(Arrays.asList(obj));
		/*
		 * 如果希望 list() 方法返回的集合中仅包含 Department 对象, 可以在HQL 查询语句中使用 SELECT 关键字
		 * 注意此时 DepartmentHQL中的set是未被初始化的，即懒加载的检索
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
		//1.创建一个Criteria对象
		Criteria criteria = session.createCriteria(EmployeeHQL.class);
		//2.添加查询条件，在QBC中查询条件使用Criterion来表示，Criterion 可以通过 Restrictions 的静态方法得到
		criteria.add(Restrictions.eq("name", "E-7"));
		criteria.add(Restrictions.gt("salary", 200f));
		//uniqueResult 返回结果不唯一，抛出异常
//		EmployeeHQL result = (EmployeeHQL) criteria.uniqueResult();
//		System.out.println(result);
		List<EmployeeHQL> list = criteria.list();
		
		System.out.println(list);
		/*
		 * qbc的检索策略与映射文件中的检索策略保持一致
		 */
		for(EmployeeHQL e:list){
			System.out.println(e.getDept().getName());
		}
	}
	/**
	 * 组合条件查询
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
		/*sql效果
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
		/* 完整查询sql
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
	 * 统计查询
	 */
	@Test
	public void testQBC3(){
		Criteria criteria = session.createCriteria(EmployeeHQL.class);
		//统计查询: 使用 Projection 来表示: 可以由 Projections 的静态方法得到
		criteria.setProjection(Projections.max("salary"));
		System.out.println(criteria.uniqueResult());
	}
	/**
	 * 分页与排序
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testQBC4(){
		Criteria criteria = session.createCriteria(EmployeeHQL.class);
		criteria.addOrder(Order.desc("salary"));
		criteria.addOrder(Order.asc("id"));
		
		//2. 添加翻页方法
		int pageSize = 3;
		int pageNo = 3;
		List<EmployeeHQL> list = criteria.setFirstResult((pageNo - 1) * pageSize)
		        .setMaxResults(pageSize)
		        .list();
		System.out.println(list);
	}
	/*
	 * 原生sql insert操作
	 */
	@Test
	public void testNativeSQL1(){
		String sql="insert into DAVID_DEPARTMENT_HQL values(?,?)";
		SQLQuery query = session.createSQLQuery(sql);
		query.setInteger(0, 9)
		.setString(1, "D-5").executeUpdate();
		
	}
	/**
	 * 原生sql select 操作
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
	@Test
	public void testHibernateSecendLevelCache(){
		//1.回顾session一级缓存
		//一下获取两次DepartmentHQL对象，仅仅发送一次select语句，这就是session缓存的作用
		DepartmentHQL dept1 = (DepartmentHQL) session.get(DepartmentHQL.class, 2);
		System.out.println(dept1.getName());
		
		/*
		 * 但是当此时seesion关闭了，开启新的session过后，获取对象就会再次发送select语句，二级缓存主要解决的就是
		 * 这个问题
		 */
		transaction.commit();
		session.close();
		
		session=sessionFactory.openSession();
		transaction=session.beginTransaction();
		
		DepartmentHQL dept2 = (DepartmentHQL) session.get(DepartmentHQL.class, 2);
		System.out.println(dept2.getName());
	}
	@Test
	public void testCollectionSecendLevelCache(){
		
		DepartmentHQL dept1 = (DepartmentHQL) session.get(DepartmentHQL.class, 2);
		System.out.println(dept1.getName());
		System.out.println(dept1.getEmps().size());
		
		transaction.commit();
		session.close();
		
		session=sessionFactory.openSession();
		transaction=session.beginTransaction();
		
		DepartmentHQL dept2 = (DepartmentHQL) session.get(DepartmentHQL.class, 2);
		System.out.println(dept2.getName());
		System.out.println(dept2.getEmps().size());
	}
	@Test
	public void testQueryCache(){
		String hql="FROM EmployeeHQL";
		Query query = session.createQuery(hql);
		query.setCacheable(true);
		List<EmployeeHQL> list1 = query.list();
		System.out.println(list1.size());
		
		list1 = query.list();
		System.out.println(list1.size());
		
		Criteria criteria = session.createCriteria(EmployeeHQL.class);
		criteria.setCacheable(true);
		List list = criteria.list();
		System.out.println(list.size());
		list = criteria.list();
		System.out.println(list.size());
	}
	/**
	 * 	时间戳缓存区域存放了对于查询结果相关的表进行插入, 更新或删除操作的时间戳.  Hibernate 通过时间戳缓存区域来判断被缓存的查询结果是否过期, 其运行过程如下:
		T1 时刻执行查询操作, 把查询结果存放在 QueryCache 区域, 记录该区域的时间戳为 T1
		T2 时刻对查询结果相关的表进行更新操作, Hibernate 把 T2 时刻存放在 UpdateTimestampCache 区域.
		T3 时刻执行查询结果前, 先比较 QueryCache 区域的时间戳和 UpdateTimestampCache 区域的时间戳, 若 T2 >T1, 
		那么就丢弃原先存放在 QueryCache 区域的查询结果, 重新到数据库中查询数据, 再把结果存放到 QueryCache 区域; 若 T2 < T1, 直接从 QueryCache 中获得查询结果
	 */
	@Test
	public void testUpdateTimeStampCache(){
		Query query = session.createQuery("FROM EmployeeHQL");
		query.setCacheable(true);
		
		List<EmployeeHQL> emps = query.list();
		System.out.println(emps.size());
		
		EmployeeHQL employee = (EmployeeHQL) session.get(EmployeeHQL.class, 3);
		employee.setSalary(30000);
		
		emps = query.list();
		System.out.println(emps.size());
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testQueryItorator(){
		/*
		 * 如果没有这段select DepartmentHQL的代码，下面代码将会查询n条sql,因为iterate()方法回从一级或者二级缓存
		 * 中去寻找满足条件的对象，如果没有，再从数据库中获取。
		 */
		DepartmentHQL dept = (DepartmentHQL) session.get(DepartmentHQL.class, 1);
		System.out.println(dept.getEmps().size());
		
		String hql="FROM EmployeeHQL E WHERE E.dept.id= 1 ";
		Iterator<EmployeeHQL> iterate = session.createQuery(hql).iterate();
		while(iterate.hasNext()){
			System.out.println(iterate.next().getName());
		}
		/*
		 * 二级缓存中存在满足hql查询条件的对象时，控制台的输出结果
		  	Hibernate: select department0_.D_ID as D_ID1_9_0_, department0_.D_NAME as D_NAME2_9_0_ from DAVID_DEPARTMENT_HQL 
		  	department0_ where department0_.D_ID=?
			Hibernate: select emps0_.D_ID as D_ID5_9_0_, emps0_.E_ID as E_ID1_11_0_, emps0_.E_ID as E_ID1_11_1_, 
			emps0_.E_NAME as E_NAME2_11_1_, emps0_.E_SALARY as E_SALARY3_11_1_,
			emps0_.E_EMAIL as E_EMAIL4_11_1_, emps0_.D_ID as D_ID5_11_1_ from DAVID_EMPLOYEE_HQL emps0_ where emps0_.D_ID=?
			6
			Hibernate: select employeehq0_.E_ID as col_0_0_ from DAVID_EMPLOYEE_HQL employeehq0_ where employeehq0_.D_ID=1
			E-1
			E-3
			E-5
			E-7
			E-7
			E-9  这里将是连续输出
			
			如果缓存中不存在时控制台的输出：
			Hibernate: select department0_.D_ID as D_ID1_9_0_, department0_.D_NAME as D_NAME2_9_0_ from DAVID_DEPARTMENT_HQL 
			department0_ where department0_.D_ID=?
			Hibernate: select emps0_.D_ID as D_ID5_9_0_, emps0_.E_ID as E_ID1_11_0_, emps0_.E_ID as E_ID1_11_1_, 
			emps0_.E_NAME as E_NAME2_11_1_, emps0_.E_SALARY as E_SALARY3_11_1_, emps0_.E_EMAIL as E_EMAIL4_11_1_, 
			emps0_.D_ID as D_ID5_11_1_ from DAVID_EMPLOYEE_HQL emps0_ where emps0_.D_ID=?
			2
			Hibernate: select employeehq0_.E_ID as col_0_0_ from DAVID_EMPLOYEE_HQL employeehq0_ where employeehq0_.D_ID=1
			Hibernate: select employeehq0_.E_ID as E_ID1_11_0_, employeehq0_.E_NAME as E_NAME2_11_0_, employeehq0_.E_SALARY 
			as E_SALARY3_11_0_, employeehq0_.E_EMAIL as E_EMAIL4_11_0_, employeehq0_.D_ID as D_ID5_11_0_ from DAVID_EMPLOYEE_HQL 
			employeehq0_ where employeehq0_.E_ID=?
			E-1
			Hibernate: select employeehq0_.E_ID as E_ID1_11_0_, employeehq0_.E_NAME as E_NAME2_11_0_, employeehq0_.E_SALARY as 
			E_SALARY3_11_0_, employeehq0_.E_EMAIL as E_EMAIL4_11_0_, employeehq0_.D_ID as D_ID5_11_0_ from DAVID_EMPLOYEE_HQL 
			employeehq0_ where employeehq0_.E_ID=?
			E-3
			Hibernate: select employeehq0_.E_ID as E_ID1_11_0_, employeehq0_.E_NAME as E_NAME2_11_0_, employeehq0_.E_SALARY as 
			E_SALARY3_11_0_, employeehq0_.E_EMAIL as E_EMAIL4_11_0_, employeehq0_.D_ID as D_ID5_11_0_ from DAVID_EMPLOYEE_HQL 
			employeehq0_ where employeehq0_.E_ID=?
			E-5
			Hibernate: select employeehq0_.E_ID as E_ID1_11_0_, employeehq0_.E_NAME as E_NAME2_11_0_, employeehq0_.E_SALARY as 
			E_SALARY3_11_0_, employeehq0_.E_EMAIL as E_EMAIL4_11_0_, employeehq0_.D_ID as D_ID5_11_0_ from DAVID_EMPLOYEE_HQL 
			employeehq0_ where employeehq0_.E_ID=?
			E-7
			Hibernate: select employeehq0_.E_ID as E_ID1_11_0_, employeehq0_.E_NAME as E_NAME2_11_0_, employeehq0_.E_SALARY as 
			E_SALARY3_11_0_, employeehq0_.E_EMAIL as E_EMAIL4_11_0_, employeehq0_.D_ID as D_ID5_11_0_ from DAVID_EMPLOYEE_HQL 
			employeehq0_ where employeehq0_.E_ID=?
			E-7
			Hibernate: select employeehq0_.E_ID as E_ID1_11_0_, employeehq0_.E_NAME as E_NAME2_11_0_, employeehq0_.E_SALARY as 
			E_SALARY3_11_0_, employeehq0_.E_EMAIL as E_EMAIL4_11_0_, employeehq0_.D_ID as D_ID5_11_0_ from DAVID_EMPLOYEE_HQL 
			employeehq0_ where employeehq0_.E_ID=?
			E-9
			
		 */
		
	}
	@Test
	public void testSessionManager(){
		//获取 Session
		//开启事务
		Session session = HibernateUtil.getInstance().getsession();
		System.out.println("-->" + session.hashCode());
		Transaction transaction = session.beginTransaction();
		DepartmentHQLDao dao=new DepartmentHQLDao();
		DepartmentHQL dept=new DepartmentHQL();
		dept.setName("DD1");
		dao.save(dept);
		dao.save(dept);
		dao.save(dept);
		//若 Session 是由 thread 来管理的, 则在提交或回滚事务时, 已经关闭 Session 了. 
		transaction.commit();
		System.out.println(session.isOpen()); 
	}
	@Test
	public void testBatch(){
		session.doWork(new Work() {
			
			@Override
			public void execute(Connection connection) throws SQLException {
				
				
			}
		});
	}
}
