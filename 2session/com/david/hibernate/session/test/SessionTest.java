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
	
	/**
	 * ֪ʶ�㣺
	 * վ�ڳ־û��ĽǶ�, Hibernate �Ѷ����Ϊ 4 ��״̬: �־û�״̬, ��ʱ״̬, ����״̬, ɾ��״̬. Session ���ض�������ʹ�����һ��״̬ת������һ��״̬
	 * ��ʱ����Transient��:��ʹ�ô��������������, OID ͨ��Ϊ null,������ Session �Ļ�����,�����ݿ���û�ж�Ӧ�ļ�¼
	 * �־û�����(Ҳ�С��йܡ�)��Persist��:OID ��Ϊ null,λ�� Session ������,�������ݿ����Ѿ��к����Ӧ�ļ�¼, �־û���������ݿ��е���ؼ�¼��Ӧ,
	 * 	  Session �� flush ����ʱ, ����ݳ־û���������Ա仯, ��ͬ���������ݿ�,��ͬһ�� Session ʵ���Ļ�����, ���ݿ���е�ÿ����¼ֻ��ӦΨһ�ĳ־û�����
	 * ɾ������(Removed):�����ݿ���û�к��� OID ��Ӧ�ļ�¼,���ٴ��� Session ������,һ�������, Ӧ�ó��򲻸���ʹ�ñ�ɾ���Ķ���
	 * �������(Ҳ�С��ѹܡ�) ��Detached����OID ��Ϊ null,���ٴ��� Session ������,һ���������, ����������ɳ־û�����ת�������, ��������ݿ��п��ܻ�����������Ӧ�ļ�¼
	 */
	
	/**
	 * save()������ʹһ���������ʱ״̬��ɳ־û����󣬲�Ϊ�ö������id,��flush����ʱ�ᷢ��һ��insert���
	 * ���⣬��save����֮ǰ����idʱ��Ч�ģ�����save����֮���޸�id���׳��쳣����Ϊ�־û������id�ǲ��ܱ��޸ĵģ������ᵼ�¸ö��������ݿ��¼�޷���Ӧ������
	 */
	@Test
	public void testSessionSave() {
		Article article=new Article();
		article.setAuthor("david");
		article.setName("david's article!");
		article.setDate(new Date());
		//article.setId(1);//��ʱ����id����Ч��
		//��ʱarticle������һ����ʱ����idΪnull
		System.out.println(article);
		
		session.save(article);
		//article.setId(1);//���쳣
		//save����֮��article����ת���ɳ־û������ˣ�����id�Ѿ����ڲ�Ϊnull
		System.out.println(article);
	}
	/**
	 * persist():Ҳ��ִ��insert������ͬ��Ҳ����һ����ʱ�����ɳ־û�����
	 * ��save()�������ǣ���persist��������֮ǰ������ö����Ѿ�����id,��ô����ִ��insert�����������׳��쳣��
	 */
	@Test
	public void testSessionPersist() {
		Article article=new Article();
		article.setAuthor("david_1");
		article.setName("david_1's article!");
		article.setDate(new Date());
		//article.setId(100);//���׳��쳣	
		session.persist(article);
	}
	/**
	 * get():���ȡ��һ���־û�����
	 * ע��get��load��������
	 * 1.ִ��get�������������ض��󣬶�load��������ʹ�øö����򲻻�����ִ�в�ѯ������������һ��
	 * 	������󣬼�get������������load���ӳټ���
	 * 2.�����ݿ�û�ж�Ӧ�ļ�¼��get����null,load�׳��쳣(�����ʹ�øö��󲻻��׳��쳣)
	 * 3. load�������ܻ��׳�һ�������ص��쳣(org.hibernate.LazyInitializationException)
	 * ����session�ر�֮���ʹ��load���ض���
	 * 
	 */
	@Test
	public void testSessionGet() {
		Article article = (Article) session.get(Article.class, 2);
		System.out.println(article);
	}
	/**
	 * load():���ȡ��һ���־û�����
	 */
	@Test
	public void testSessionLoad() {
		Article article = (Article) session.load(Article.class, 2);
		System.out.println(article.getClass().getName());//�������
		//session.close();//�˴��ر�session������ʹ�ö�����׳��ӳټ��ص��쳣
		System.out.println(article);//��ʹ�øö���Ͳ���ִ��select����
	}
	
	/**
	 * update:��һ�����������һ���־û�����
	 * 1.�־û�����ĸ��£�����Ҫ��ʾ�ص���update��������transaction.commit����ʱ
	 * 	����ִ��session��flush������
	 * 2. ����һ�����������Ҫ��ʽ�ص���session��update����
	 * ע�⣺1)������������Ƿ����ı䣬���ᷢ��update��䣨session��Ҫ�Ѹö��������ݿ��¼��Ӧ������
	 * ���ǿ���ͨ�����ö���ӳ���ļ������� select-before-update=true���ɣ�Ĭ��false),ͨ������Ҫ���ø����ԣ�������
	 * �봥����ʹ�÷�ֹupdate���������ʱ������ø����ԡ�
	 * 2).�����ݱ������ݲ����ڣ����ǻ��ǵ���update�������׳��쳣�����ڸö�������������ʱ���id�ĳ����ݿⲻ����
	 * ��Ӧ��id��ʱ����ִ��update������
	 * 3).�� update() ��������һ���������ʱ, ����� Session �Ļ������Ѿ�������ͬ OID �ĳ־û�����, ���׳��쳣
	 */
	@Test
	public void testSessionUpdate() {
		Article article = (Article) session.get(Article.class, 2);
		//�˴��ύ���񣬹ر�sessionʹ��article����ӳ־û�����������̬����
		transaction.commit();
		session.close();
		
		//article.setId(1000);//��������ᵼ��session�����ݿ����Ҳ�����Ӧ�ļ�¼���׳��쳣
		
		session=sessionFactory.openSession();
		transaction=session.beginTransaction();
		//�����»�ȡ��session�Ϳ�����������ԣ��޷���֪�������Ĵ��ڣ����Ҫ�뱣���޸ĺ�Ķ���
		//����ʹ��update������
		article.setAuthor("DAVID");
		
		//�˲����ᵼ��session���ȴ���һ��OIDΪ2���󣬴�ʱ�ٵ���update��������OIDҲΪ2��article����
		//ִ�и��²������׳��쳣����Ϊsession�޷�ȷ��ʹ�û�������������ȥ�����ݱ��¼���Ӧ��
		//Article article1 = (Article) session.get(Article.class, 2);
		session.update(article);
	}
	/**
	 * SaveOrUpdate():�����������ʱ������ôִ��save������������������ִ��update�����������ж�������Ҫ�Ƕ����OID�Ƿ�Ϊ��
	 * ע�⣺1.��������OID�����ݿ�����Ҳ�����Ӧ�ļ�¼��ִ�д˷��������쳣
	 * 	   2.OIDֵ����id��usaved-value����ֵ�Ķ���Ҳ����Ϊ��һ����������ڶ����ϵӳ���ļ�������(��������Ϊ8)
	 * 		<id name="id" type="java.lang.Integer" unsaved-value="8">
     *       	<column name="ID" />
     *       	<generator class="native" />
     *  	</id>
     *     �������ʵ����������ز������ݿ��ˣ��������ݵ�id������8�����ǰ���id�Ĳ������ɵ�
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
	 * delete():ִ��ɾ��������ֻҪOID�����ݱ��ж�Ӧһ����¼��ִ��delte��������OID�����ݿ����Ҳ���
	 * ��Ӧ��¼�����׳��쳣
	 */
	@Test
	public void testSessionDelete() {
		//�������
		Article article=new Article();
		article.setId(1);
		//session.delete(article);
		
		//ɾ���־û�����
		Article article1=(Article) session.get(Article.class, 4);
		session.delete(article1);
		//�����������ʾֻ����commitʱ����flush������ʱ��Ż�ִ��delete���� 
		System.out.println(article1);
		/*
		 * �������ļ�����hibernate.use_identifier_rollback ��Ϊ true, ���ı� 
		 * delete() ������������Ϊ: delete() ������ѳ־û�������������� OID ����Ϊ null, 
		 * ʹ���Ǳ�Ϊ��ʱ����,������ɾ���������ִ��save����saveorupdate()������
		 * �ؼ���ʱ �ѳ־û�������������������ʱ������
		 */
		session.saveOrUpdate(article1);
	}
	/**
	 * evict():��session�����а�ָ���ĳ־û������Ƴ�
	 */
	@Test
	public void testSessionEvict() {
		Article article5=(Article) session.get(Article.class, 5);
		Article article6=(Article) session.get(Article.class, 6);
		
		article5.setAuthor("AA");
		article6.setAuthor("BB");
		
		//����ʹ��evict()��������ô���ύ�����ʱ����2���־û����󽫸��µ����ݿ���
		session.evict(article5);
		session.evict(article6);
	}
	
	@Test
	public void testSessionDoWork() {
		session.doWork(new Work() {
			
			@Override
			public void execute(Connection connection) throws SQLException {
				System.out.println(connection);
				//hibernate δ�ṩ�洢���̵�api,��Ҫͨ��jdbc��api������
			}
		});
	}

}
