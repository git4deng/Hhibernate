package com.david.hibernate.helloworld.entity;

import java.sql.Date;

public class News {
	private Integer id;
	private String name;
	private String Author;
	private Date date;
	
	public News() {
	}

	public News(String name, String author, Date date) {
		super();
		this.name = name;
		Author = author;
		this.date = date;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return Author;
	}

	public void setAuthor(String author) {
		Author = author;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "News [id=" + id + ", name=" + name + ", Author=" + Author + ", date=" + date + "]";
	}
	
}
