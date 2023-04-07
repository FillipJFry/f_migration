package com.goit.fry.migration.entities;

public class Client {

	private final Long id;
	private final String name;

	public Client(Long id, String name) {

		this.id = id;
		this.name = name;
	}

	public Long getId() {

		return id;
	}

	public String getName() {

		return name;
	}
}
