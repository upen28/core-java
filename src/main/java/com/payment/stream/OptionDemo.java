

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
	public static void main(String... args) throws Exception {
		Employer employer = new Employer();
		Optional<Employer> emp = Optional.of(employer);

		String city = emp.map(item -> item.getEmployee()).map(item -> item.getAdress()).map(item -> item.getCity()).orElse(null);
		if (city != null) {
			System.out.println("city " + city);
		}
		else {
			System.out.println("city is " + city);
		}

	}
}
