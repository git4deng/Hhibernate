package com.david.hibernate.session.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.david.hibernate.session.entities.Article;

@SuppressWarnings("deprecation")
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
	
	/**
	 * 知识点：
	 * 站在持久化的角度, Hibernate 把对象分为 4 种状态: 持久化状态, 临时状态, 游离状态, 删除状态. Session 的特定方法能使对象从一个状态转换到另一个状态
	 * 临时对象（Transient）:在使用代理主键的情况下, OID 通常为 null,不处于 Session 的缓存中,在数据库中没有对应的记录
	 * 持久化对象(也叫”托管”)（Persist）:OID 不为 null,位于 Session 缓存中,若在数据库中已经有和其对应的记录, 持久化对象和数据库中的相关记录对应,
	 * 	  Session 在 flush 缓存时, 会根据持久化对象的属性变化, 来同步更新数据库,在同一个 Session 实例的缓存中, 数据库表中的每条记录只对应唯一的持久化对象
	 * 删除对象(Removed):在数据库中没有和其 OID 对应的记录,不再处于 Session 缓存中,一般情况下, 应用程序不该再使用被删除的对象
	 * 游离对象(也叫”脱管”) （Detached）：OID 不为 null,不再处于 Session 缓存中,一般情况需下, 游离对象是由持久化对象转变过来的, 因此在数据库中可能还存在与它对应的记录
	 */
	
	/**
	 * save()方法：使一个对象从临时状态变成持久化对象，并为该对象分配id,在flush缓存时会发送一条insert语句
	 * 另外，在save方法之前设置id时无效的，而且save方法之后修改id会抛出异常，因为持久化对象的id是不能别修改的，这样会导致该对象与数据库记录无法对应起来。
	 */
	@Test
	public void testSessionSave() {
		Article article=new Article();
		article.setAuthor("david");
		article.setName("david's article!");
		article.setDate(new Date());
		//article.setId(1);//此时设置id是无效的
		//此时article对象是一个临时对象，id为null
		System.out.println(article);
		
		session.save(article);
		//article.setId(1);//抛异常
		//save方法之后，article对象转换成持久化对象了，其中id已经存在不为null
		System.out.println(article);
	}
	/**
	 * persist():也会执行insert操作，同样也会让一个临时对象变成持久化对象
	 * 和save()的区别是：在persist方法调用之前，如果该对象已经存在id,那么不会执行insert操作，而会抛出异常！
	 */
	@Test
	public void testSessionPersist() {
		Article article=new Article();
		article.setAuthor("david_1");
		article.setName("david_1's article!");
		article.setDate(new Date());
		//article.setId(100);//会抛出异常	
		session.persist(article);
	}
	/**
	 * get():会获取到一个持久化对象
	 * 注意get与load方法区别：
	 * 1.执行get方法会立即加载对象，而load方法，若使用该对象，则不会立即执行查询操作，而返回一个
	 * 	代理对象，即get是立即检索，load是延迟加载
	 * 2.若数据库没有对应的记录，get返回null,load抛出异常(如果不使用该对象不会抛出异常)
	 * 3. load方法可能会抛出一个懒加载的异常(org.hibernate.LazyInitializationException)
	 * 即在session关闭之后才使用load加载对象。
	 * 
	 */
	@Test
	public void testSessionGet() {
		Article article = (Article) session.get(Article.class, 2);
		System.out.println(article);
	}
	/**
	 * load():会获取到一个持久化对象
	 */
	@Test
	public void testSessionLoad() {
		Article article = (Article) session.load(Article.class, 2);
		System.out.println(article.getClass().getName());//代理对象
		//session.close();//此处关闭session，下面使用对象会抛出延迟加载的异常
		System.out.println(article);//不使用该对象就不会执行select操作
	}
	
	/**
	 * update:将一个游离对象变成一个持久化对象
	 * 1.持久化对象的更新，不需要显示地调用update方法，在transaction.commit方法时
	 * 	会先执行session的flush方法。
	 * 2. 更新一个游离对象，需要显式地调用session的update方法
	 * 注意：1)无论游离对象是否发生改变，都会发送update语句（session需要把该对象与数据库记录对应起来）
	 * 但是可以通过设置对象映射文件的属性 select-before-update=true即可（默认false),通常不需要设置该属性，仅仅在
	 * 与触发器使用防止update的误操作的时候会设置该属性。
	 * 2).若数据表种数据不存在，但是还是调用update方法会抛出异常（即在该对象是游离对象的时候把id改成数据库不存在
	 * 对应的id的时候，再执行update方法）
	 * 3).当 update() 方法关联一个游离对象时, 如果在 Session 的缓存中已经存在相同 OID 的持久化对象, 会抛出异常
	 */
	@Test
	public void testSessionUpdate() {
		Article article = (Article) session.get(Article.class, 2);
		//此处提交事务，关闭session使得article对象从持久化对象变成游离态对象
		transaction.commit();
		session.close();
		
		//article.setId(1000);//这个操作会导致session在数据库种找不到对应的记录而抛出异常
		
		session=sessionFactory.openSession();
		transaction=session.beginTransaction();
		//对于新获取到session和开启的事务而言，无法感知游离对象的存在，因此要想保存修改后的对象
		//必须使用update方法。
		article.setAuthor("DAVID");
		
		//此操作会导致session中先存在一个OID为2对象，此时再调用update方法关联OID也为2的article对象
		//执行跟新操作会抛出异常，因为session无法确定使用缓存中哪条数据去与数据表记录相对应。
		//Article article1 = (Article) session.get(Article.class, 2);
		session.update(article);
	}
	/**
	 * SaveOrUpdate():如果对象是临时对象，那么执行save方法，如果是游离对象执行update方法，其中判断依据主要是对象的OID是否为空
	 * 注意：1.如果对象的OID在数据库表中找不到对应的记录，执行此方法会抛异常
	 * 	   2.OID值等于id的usaved-value属性值的对象，也被认为是一个游离对象，在对象关系映射文件中设置(假如设置为8)
	 * 		<id name="id" type="java.lang.Integer" unsaved-value="8">
     *       	<column name="ID" />
     *       	<generator class="native" />
     *  	</id>
     *     这样这个实体就能正常地插入数据库了，但是数据的id并不是8，而是按照id的策略生成的
	 */
	@Test
	public void testSessionSaveOrUpdate() {
		
		Article article=new Article();
		article.setAuthor("david");
		article.setName("david's article!");
		article.setDate(new Date());
		article.setId(8);
		session.saveOrUpdate(article);
	}
	/**
	 * delete():执行删除操作，只要OID和数据表中对应一条记录，执行delte操作，若OID在数据库中找不到
	 * 对应记录，则抛出异常
	 */
	@Test
	public void testSessionDelete() {
		//游离对象
		Article article=new Article();
		article.setId(1);
		//session.delete(article);
		
		//删除持久化对象
		Article article1=(Article) session.get(Article.class, 4);
		session.delete(article1);
		//以下输出，表示只有在commit时调用flush方法的时候才会执行delete操作 
		System.out.println(article1);
		/*
		 * 将配置文件属性hibernate.use_identifier_rollback 设为 true, 将改变 
		 * delete() 方法的运行行为: delete() 方法会把持久化对象或游离对象的 OID 设置为 null, 
		 * 使它们变为临时对象,这样在删除过后就能执行save或则saveorupdate()方法了
		 * 关键点时 把持久化对象或者游离对象变成临时对象了
		 */
		session.saveOrUpdate(article1);
	}
	/**
	 * evict():从session缓存中把指定的持久化对象移除
	 */
	@Test
	public void testSessionEvict() {
		Article article5=(Article) session.get(Article.class, 5);
		Article article6=(Article) session.get(Article.class, 6);
		
		article5.setAuthor("AA");
		article6.setAuthor("BB");
		
		//若不使用evict()方法，那么在提交事务的时候，这2个持久化对象将更新到数据库中
		session.evict(article5);
		session.evict(article6);
	}
	
	@Test
	public void testSessionDoWork() {
		session.doWork(new Work() {
			
			@Override
			public void execute(Connection connection) throws SQLException {
				System.out.println(connection);
				//hibernate 未提供存储过程的api,需要通过jdbc的api来调用
			}
		});
	}

}
