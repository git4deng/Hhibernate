package com.david.hibernate.joinedSubclass.entity;

public class JoinedStudent extends JoinedPerson {
	private String school;

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}
}
