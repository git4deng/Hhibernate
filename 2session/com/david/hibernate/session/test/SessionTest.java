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
	 * 测试session缓存
	 */
	@Test
	public void testSessionCache() {
		Article article = (Article) session.get(Article.class, 1);
		System.out.println(article);
		
		Article article2 = (Article) session.get(Article.class, 1);
		System.out.println(article2);
		System.out.println(article==article2);// true 说明取出的是同一个对象。
		/**
		 * 以上代码执行，控制台仅仅输出一次sql查询语句，原因是session的缓存，当程序查询一个实体对象时，首先会先在session中查找
		 * 若果存在该对象，直接取回，如果不存在再通过session从数据库中去取，因此session缓存可以减少程序访问数据库的次数。
		 * 只要session的生命周期没有结束或者没有清理缓存操作，则存放在session缓存中的对象也不会结束生命周期。
		 */
	}
	
	/**
	 * 测试session的flush()方法:使数据表中的数据和session缓存中的对象保持一致，则可能发送对应的update语句
	 */
	@Test
	public void testSessionFlush() {
		Article article1 = (Article) session.get(Article.class, 1);
		article1.setName("david_wei");
		/**
		 * 以上代码，修改实体对象的属性值，控制台包含查询和update的sql,数据库的数据相对应的发生了改变，原因是
		 * 在transaction提交事务之前，session会将缓存中的对象通过flush方法，将数据更新直数据库。
		 * transaction.commit()之前会判断session缓存的对象状态是否保持一致，即先flush()方法，再提交事务。
		 */
		
		//也可以显示的调用session的flush方法
//		session.flush();
		/**
		 * 需要注意的是，flush方法可能会发送sql语句，但是不会提交事务。
		 * 另外:在未提交事务和显示地调用flush方法的时候，也有可能会进行flush操作。
		 * 比如：
		 * （1）执行HQL和QBC(QBC(Query By Criteria) API提供了检索对象的另一种方式，它主要由Criteria接口、Criterion接口和Expresson类组成，它支持在运行时动态生成查询语句。)
		 * 	查询，会先进行flush操作，以保证得到的数据是最新的。
		 * （2）若记录的id是调用数据库底层自增的方式生成的，则在调用save方法后，会立即发送insert语句（调用flush)方法，并不是在等到commit的时候才调用
		 * 因为save方法之后必须保证对象的id是存在的。但是，如果id是hibernate生成的就不会发送insert语句，如hibernate的hilo策略（高低算法）。
		 * 
		 */
		//执行查询之前，会先flush方法，即会输出update语句，然会再执行select语句
		Article article2 = (Article) session.createCriteria(Article.class).uniqueResult();
		//注意，输出这条语句的时候，并没有提交事务，即数据库的数据并未更新，但是输出的数据却是修改之后的。
		System.out.println(article2);
	}
	/**
	 * 补充知识点：
	 * 1.事务特点：ACID,原子性（Atomicity，一致性（Consistency），隔离性（Isolation），持久性（Durability）
	 * 2.事务考虑的问题：
	 * 	1）.脏读：脏读是指在一个事务处理过程里读取了另一个未提交的事务中的数据
	 * 	2）.不可重读读：不可重复读是指在对于数据库中的某个数据，一个事务范围内多次查询却返回了不同的数据值，这是由于在查询间隔，被另一个事务修改并提交了
	 * 	3）.幻读:幻读是事务非独立执行时发生的一种现象。例如事务T1对一个表中所有的行的某个数据项做了从“1”修改为“2”的操作，这时事务T2又对这个表中插入了一行数据项，
	 * 	而这个数据项的数值还是为“1”并且提交给数据库。而操作事务T1的用户如果再查看刚刚修改的数据，会发现还有一行没有修改，其实这行是从事务T2中添加的，就好像产生幻
	 * 	觉一样，这就是发生了幻读.
	 * 3.数据库提供的4种事务隔离级别：
	 * Serializable (串行化)：可避免脏读、不可重复读、幻读的发生。
	 * Repeatable read (可重复读)：可避免脏读、不可重复读的发生。
	 * Read committed (读已提交)：可避免脏读的发生。
 	 * Read uncommitted (读未提交)：最低级别，任何情况都无法保证
	 * 
	 * 其中MySQL都支持这4种隔离级别，默认是repeatable read(可重复读),Oracle仅仅支持Read committed和Serializable，默认是读已提交Read committed
	 */

	/**
	 * refresh():会强制发送select语句，以使session缓存的数据和数据表中的数据保持一致。
	 */
	@Test
	public void testSessionRefresh() {
		Article article1 = (Article) session.get(Article.class, 1);
		System.out.println(article1);
		/**
		 * 假如在第一条打印和第二条打印之间数据库的数据发生变化了，此时调用refresh方法会将数据库的最新数据给同步到session缓存中，
		 * 即执行了一遍select操作。（但是与数据库的事务隔离级别有关，默认的事务隔离级别是：REPEATABLE READ（可重复读），可以在hibernate的配置文件种配置
		 * hibernate.connection.isolation属性来设置事务隔离级别，从低到高分别是1,2,4,8
		 */
		session.refresh(article1);
		
		System.out.println(article1);
	}
	/**
	 * clear()方法：清理缓存。
	 */
	@Test
	public void testSessionClear() {
		Article article = (Article) session.get(Article.class, 1);
		System.out.println(article);
		//清理缓存
		session.clear();
		
		Article article2 = (Article) session.get(Article.class, 1);
		System.out.println(article2);
		
		System.out.println(article==article2);
		/**
		 * 以上代码执行输出2此select语句，最后输出false,这就是clear方法的作用
		 */
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
