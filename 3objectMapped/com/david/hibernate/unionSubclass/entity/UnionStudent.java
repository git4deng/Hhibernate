package com.david.hibernate.unionSubclass.entity;

public class UnionStudent extends UnionPerson {
	private String school;

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}
}
