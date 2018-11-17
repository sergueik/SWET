package com.github.sergueik.swet;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

// https://stackoverflow.com/questions/11038553/serialize-java-object-with-gson
public class ConfigDataSerializer implements JsonSerializer<ConfigData> {
	@Override
	public JsonElement serialize(final ConfigData configData, final Type type,
			final JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		String id = configData.getId();
		if (id != null && !id.isEmpty()) {
			result.add("id", new JsonPrimitive(id));
		}
		result.add("browser", new JsonPrimitive(configData.getBrowser()));
		result.add("templateName", new JsonPrimitive(configData.getTemplateName()));
		String templateDirectory = configData.getTemplateDirectory();
		if (templateDirectory != null && !templateDirectory.isEmpty()) {
			result.add("templateDirectory", new JsonPrimitive(templateDirectory));
		}
		String templatePath = configData.getTemplatePath();
		if (templatePath != null && !templatePath.isEmpty()) {
			result.add("templatePath", new JsonPrimitive(templatePath));
		}
		/*
		Template template = configData.getTemplate();
		if (template != null) {
		    result.add("template", new JsonPrimitive(template.getId()));
		}
		*/
		return result;
	}
}