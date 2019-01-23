package com.david.hibernate.hql.entity;

public class EmployeeHQL {
	private Integer id;
	private String name;
	private float salary;
	private String email;

	private DepartmentHQL dept;

	public EmployeeHQL() {
		
	}
	
	public EmployeeHQL(float salary, String email, DepartmentHQL dept) {
		super();
		this.salary = salary;
		this.email = email;
		this.dept = dept;
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

	public float getSalary() {
		return salary;
	}

	public void setSalary(float salary) {
		this.salary = salary;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public DepartmentHQL getDept() {
		return dept;
	}

	public void setDept(DepartmentHQL dept) {
		this.dept = dept;
	}

	@Override
	public String toString() {
		return "EmployeeHQL [id=" + id + ", name=" + name + ", salary=" + salary + ", email=" + email + "]";
	}
	
}
