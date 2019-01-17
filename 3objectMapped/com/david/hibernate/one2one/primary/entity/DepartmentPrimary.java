package com.david.hibernate.one2one.primary.entity;

public class DepartmentPrimary {
	
	private Integer deptId;
	private String deptName;
	
	private ManagerPrimary mgr;

	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public ManagerPrimary getMgr() {
		return mgr;
	}

	public void setMgr(ManagerPrimary mgr) {
		this.mgr = mgr;
	}

	@Override
	public String toString() {
		return "Department [deptId=" + deptId + ", deptName=" + deptName + ", mgr=" + mgr + "]";
	}
	
}
