package com.david.hibernate.one2one.foreign.entity;

public class Manager {
	private Integer mgrId;
	private String mgrName;
	
	private Department dept;

	public Integer getMgrId() {
		return mgrId;
	}

	public void setMgrId(Integer mgrId) {
		this.mgrId = mgrId;
	}

	public String getMgrName() {
		return mgrName;
	}

	public void setMgrName(String mgrName) {
		this.mgrName = mgrName;
	}

	public Department getDept() {
		return dept;
	}

	public void setDept(Department dept) {
		this.dept = dept;
	}

	@Override
	public String toString() {
		return "Manager [mgrId=" + mgrId + ", mgrName=" + mgrName + ", dept=" + dept + "]";
	}
	
}
