package com.payment.uml;

public class Manager extends Employee {

	private double bonus;

	public Manager(String name, double salary, int year, int month, int day, PhoneNumber number) {
		super(name, salary, year, month, day);
	}

	public double getBonus() {
		return bonus;
	}

	public void setBonus(double bonus) {
		this.bonus = bonus;
	}

	@Override
	public double getSalary() {
		return super.getSalary() + 100;
	}

}
