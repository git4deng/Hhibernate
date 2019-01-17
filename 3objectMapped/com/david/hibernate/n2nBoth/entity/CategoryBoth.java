package com.david.hibernate.n2nBoth.entity;

import java.util.HashSet;
import java.util.Set;

public class CategoryBoth {
	private Integer  id;
	private String name;
	private Set<ItemBoth> items=new HashSet<ItemBoth>();
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
	public Set<ItemBoth> getItems() {
		return items;
	}
	public void setItems(Set<ItemBoth> items) {
		this.items = items;
	}
	@Override
	public String toString() {
		return "CategoryBoth [id=" + id + ", name=" + name + ", items=" + items + "]";
	}
	
	
}
