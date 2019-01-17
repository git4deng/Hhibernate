package com.david.hibernate.one2one.primary.entity;

public class ManagerPrimary {
	private Integer mgrId;
	private String mgrName;
	
	private DepartmentPrimary dept;

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

	public DepartmentPrimary getDept() {
		return dept;
	}

	public void setDept(DepartmentPrimary dept) {
		this.dept = dept;
	}

	@Override
	public String toString() {
		return "Manager [mgrId=" + mgrId + ", mgrName=" + mgrName + ", dept=" + dept + "]";
	}
	
}
