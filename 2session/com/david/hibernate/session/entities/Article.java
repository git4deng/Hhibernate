package com.david.hibernate.session.entities;

import java.sql.Blob;
import java.util.Date;

public class Article {
	private Integer id;
	private String name;
	private String Author;
	private Date date;
	//��������ֶ���Ҫ���ڲ��� hibernate�������� �������������ֵ����Ϊ Author��name
	private String desc;
	//���ı�
	private String content;
	//ͼƬ������
	private Blob image;
	
	public Article() {
		super();
	}

	public Article(String name, String author, Date date) {
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "Article [id=" + id + ", name=" + name + ", Author=" + Author + ", date=" + date + ", desc=" + desc
				+ ", content=" + content + ", image=" + image + "]";
	}
}
