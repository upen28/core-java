package com.payment.uml;

import java.util.Date;
import java.util.GregorianCalendar;

public class Employee {

	private String name;
	private double salary;
	private Date hireDay;

	private PhoneNumber number;
	private Adress[] adress = new Adress[5];

	public Employee(String name, double salary, int year, int month, int day) {
		this.name = name;
		this.salary = salary;
		GregorianCalendar cal = new GregorianCalendar(year, month, day);
		hireDay = cal.getTime();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public Date getHireDay() {
		return hireDay;
	}

	public void setHireDay(Date hireDay) {
		this.hireDay = hireDay;
	}

	public PhoneNumber getNumber() {
		return number;
	}

	public void setNumber(PhoneNumber number) {
		this.number = number;
	}

	public Adress[] getAdress() {
		return adress;
	}

	public void setAdress(Adress[] adress) {
		this.adress = adress;
	}

}
