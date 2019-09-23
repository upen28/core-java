
package com.payment.stream;

import java.util.Optional;

class Address {
	private String city;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}

class Employee {
	private String name;

	private Address adress;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getAdress() {
		return adress;
	}

	public void setAdress(Address adress) {
		this.adress = adress;
	}
}

class Employer {
	private String name;
	private Employee employee;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

}

public class OptionDemo {

	public static void checkBasicValidtaion(String opt) {
		if (opt.length() != 7) {
			throw new RuntimeException("invalid length required {} find  {} ");
		}
	}

	public static void main(String... args) throws Exception {
		Employer employer = new Employer();
		Employee em = new Employee();
		Address ad = new Address();
		ad.setCity("roorke");
		
		em.setAdress(ad);
		employer.setEmployee(em);
		Optional<Employer> emp = Optional.of(employer);

		emp.map(item -> item.getEmployee()).map(item -> item.getAdress()).map(item -> item.getCity())
				.ifPresentOrElse(fld -> {
					System.out.println("basic validation");
					checkBasicValidtaion(fld);
				}, () -> {
					System.out.println("throwing runtime exception");
					throw new RuntimeException("fld city is required");
				});
	}
}
