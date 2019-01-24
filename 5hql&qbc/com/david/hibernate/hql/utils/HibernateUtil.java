package com.david.hibernate.hql.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

@SuppressWarnings("deprecation")
public class HibernateUtil {
	private static HibernateUtil instance=new HibernateUtil();
	private HibernateUtil(){}
	
	public static HibernateUtil getInstance() {
		return instance;
	}
	private SessionFactory sessionFactory;
	public SessionFactory getSessionFactory() {
		if(sessionFactory==null){
			Configuration configuration=new Configuration().configure();
			ServiceRegistry serviceRegistry=new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
			sessionFactory=configuration.buildSessionFactory(serviceRegistry);
		}
		return sessionFactory;
	}
	@SuppressWarnings("unused")
	public Session getsession() {
		return getSessionFactory().getCurrentSession();

	}
	
}
