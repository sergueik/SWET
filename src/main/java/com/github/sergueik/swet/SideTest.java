package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2018 Serguei Kouzmine
 */

import static java.lang.String.format;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Side Test serializer class for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */
// TODO
final class SideTest {

	private String name;
	private String id;
	private List<SideCommand> commands;

	public String getName() {
		return name;
	}

	public void setName(String data) {
		this.name = data;
	}

	public String getId() {
		return id;
	}

	public void setId(String data) {
		this.id = data;
	}

	public void setCommands(List<SideCommand> data) {
		this.commands = data;
	}

	public List<SideCommand> getCommands() {
		return commands;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(format("\"id\": \"%s\"\n", id))
				.append(format("\"name\": \"%s\"\n", name))
				.append(format("\"commands\": %s\n", commands.toString())).toString();
	}
}
