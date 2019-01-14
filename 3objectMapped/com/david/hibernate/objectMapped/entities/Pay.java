package com.david.hibernate.objectMapped.entities;

public class Pay {
	private int monthPay;
	private int yearPay;
	private int vocationWithPay;//��н��
	//<parent> Ԫ��ָ��������������������� name: ��������������е�������
	private Worker worker;
	
	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}

	public int getMonthPay() {
		return monthPay;
	}

	public void setMonthPay(int monthPay) {
		this.monthPay = monthPay;
	}

	public Pay() {
		
		
	}

	public Pay(int monthPay, int yearPay, int vocationWithPay) {
		
		this.monthPay = monthPay;
		this.yearPay = yearPay;
		this.vocationWithPay = vocationWithPay;
	}

	public int getYearPay() {
		return yearPay;
	}

	public void setYearPay(int yearPay) {
		this.yearPay = yearPay;
	}

	public int getVocationWithPay() {
		return vocationWithPay;
	}

	public void setVocationWithPay(int vocationWithPay) {
		this.vocationWithPay = vocationWithPay;
	}

	@Override
	public String toString() {
		return "Pay [monthPay=" + monthPay + ", yearPay=" + yearPay + ", vocationWithPay=" + vocationWithPay + "]";
	}
}
