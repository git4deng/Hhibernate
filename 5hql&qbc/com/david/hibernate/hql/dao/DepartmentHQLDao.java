package com.david.hibernate.hql.dao;

import org.hibernate.Session;

import com.david.hibernate.hql.entity.DepartmentHQL;
import com.david.hibernate.hql.utils.HibernateUtil;
/**
 * 主要学习session管理办法
 * @author ghca
 *
 */
public class DepartmentHQLDao {
	/**方案一
	 * 此种方式需要传入一个session对象，导致上一层（service）需要获取到session对象，使得
	 * service与hibernate api紧密耦合的，所以不推荐使用此方式
	 */
	public void save(Session session,DepartmentHQL dept){
		session.save(dept);
	}
	/**方案二：获取和当前线程绑定的session对象
	 * 优点1：不需要从外部传入session对象
	 * 2.多个dao也可以使用一个事务。
	 * 
	 * @param dept
	 */
	public void save(DepartmentHQL dept){
		Session session = HibernateUtil.getInstance().getsession();
		System.out.println(session.hashCode());
		session.save(dept);
	}
}
