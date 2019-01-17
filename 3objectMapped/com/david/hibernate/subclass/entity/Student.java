package com.david.hibernate.subclass.entity;

public class Student extends Person {
	private String school;

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}
}
