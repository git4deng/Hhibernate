package com.david.hibernate.n2nBoth.entity;

import java.util.HashSet;
import java.util.Set;

public class ItemBoth {
	private Integer  id;
	private String name;
	
	private Set<CategoryBoth> categories=new HashSet<CategoryBoth>();
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
	
	public Set<CategoryBoth> getCategories() {
		return categories;
	}
	public void setCategories(Set<CategoryBoth> categories) {
		this.categories = categories;
	}
	@Override
	public String toString() {
		return "ItemBoth [id=" + id + ", name=" + name + ", categories=" + categories + "]";
	}
	
	
}
