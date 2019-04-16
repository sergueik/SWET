package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2019 Serguei Kouzmine
 */

import java.io.File;
import java.util.HashMap;

import org.junit.Test;

import com.github.sergueik.swet.Utils;
import com.github.sergueik.swet.RenderTemplate;

public class RenderTemplateTest {

	private String templateAbsolutePath = System.getProperty("user.dir")
			+ File.separator + "src\\main\\resources\\templates"	;

	@Test
	public void renderTest() {
		String templatePath = System.getProperty("user.dir") + File.separator
				+ "src\\main\\resources\\templates\\core_selenium_csharp.twig";
		RenderTemplate renderTemplate = new RenderTemplate();
		System.err
				.println(String.format("Reading template from %s ...", templatePath));
		renderTemplate.setTemplateAbsolutePath(
				templatePath.replace("\\\\", "\\").replace("\\", "/"));
		String output = renderTemplate.renderTest();
		System.err.println("Rendered: " + output);
	}
}
