package com.david.hibernate.hql.dao;

import org.hibernate.Session;

import com.david.hibernate.hql.entity.DepartmentHQL;
import com.david.hibernate.hql.utils.HibernateUtil;
/**
 * ��Ҫѧϰsession����취
 * @author ghca
 *
 */
public class DepartmentHQLDao {
	/**����һ
	 * ���ַ�ʽ��Ҫ����һ��session���󣬵�����һ�㣨service����Ҫ��ȡ��session����ʹ��
	 * service��hibernate api������ϵģ����Բ��Ƽ�ʹ�ô˷�ʽ
	 */
	public void save(Session session,DepartmentHQL dept){
		session.save(dept);
	}
	/**����������ȡ�͵�ǰ�̰߳󶨵�session����
	 * �ŵ�1������Ҫ���ⲿ����session����
	 * 2.���daoҲ����ʹ��һ������
	 * 
	 * @param dept
	 */
	public void save(DepartmentHQL dept){
		Session session = HibernateUtil.getInstance().getsession();
		System.out.println(session.hashCode());
		session.save(dept);
	}
}
