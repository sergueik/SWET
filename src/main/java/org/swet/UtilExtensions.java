package org.swet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.RuntimeException;

import java.net.URI;
import java.net.URISyntaxException;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

// generic JSONobject (de-)serializer
// origin: https://www.codeproject.com/Tips/709552/Google-App-Engine-JAVA

public class UtilExtensions {
	public static JSONObject getJsonObject(String post) throws JSONException {
		JSONObject jsonObject = new JSONObject(post);
		return jsonObject;
	}

	public static JSONArray getJsonArray(String post) throws JSONException {
		JSONArray jsonArray = new JSONArray(post);
		return jsonArray;
	}

	public static JSONObject modelToJSON(Model model, Mapper mapper)
			throws NoSuchFieldException, IllegalAccessException, JSONException,
			NoSuchMethodException, InvocationTargetException {
		JSONObject jsonObject = new JSONObject();
		if (mapper.size() > 0) {
			for (Entry entry : mapper.getEntrySet()) {
				String value = entry.getValue().toString();
				String key = entry.getKey().toString();
				jsonObject.put(value, model.getProperty(key));
			}
			return jsonObject;
		} else {
			for (String property : model.getProperties()) {
				jsonObject.put(property, model.getProperty(property));
			}
			return jsonObject;
		}
	}

	public static JSONArray modelsToJSON(List models, Mapper mapper)
			throws IllegalAccessException, NoSuchFieldException, JSONException,
			NoSuchMethodException, InvocationTargetException {
		JSONArray jsonArray = new JSONArray();
		for (Object model : models) {
			jsonArray.put(modelToJSON((Model) model, mapper));
		}
		return jsonArray;
	}

	public static Model jsonObjectToModel(JSONObject jsonObject, Class model,
			Mapper mapper) throws IllegalAccessException, InstantiationException,
			JSONException, NoSuchFieldException {
		Model m = null;
		if (mapper.size() > 0) {
			m = (Model) model.newInstance();
			for (Entry entry : mapper.getEntrySet()) {
				String value = entry.getValue().toString();
				String key = entry.getKey().toString();
				String jValue = jsonObject.get(value).toString();
				m.setProperty(key, jValue);
			}
			return m;
		} else {
			m = (Model) model.newInstance();
			for (String property : m.getProperties()) {
				String jValue = jsonObject.get(property).toString();
				m.setProperty(property, jValue);
			}
			return m;
		}
	}

	public static List<Model> jsonArrayToModel(JSONArray jsonArray, Class model,
			Mapper mapper) throws JSONException, IllegalAccessException,
			NoSuchFieldException, InstantiationException {
		List<Model> list = new ArrayList<Model>();
		int length = jsonArray.length();
		for (int index = 0; index < length; index++) {
			list.add(
					jsonObjectToModel(jsonArray.getJSONObject(index), model, mapper));
		}
		return list;
	}

	private static abstract class Mapper {

		protected HashMap<String, String> mapper = new HashMap<String, String>();

		abstract public void init();

		public Mapper() {
			init();
		}

		public Set<Entry<String, String>> getEntrySet() {
			return this.mapper.entrySet();
		}

		public int size() {
			return this.mapper.size();
		}

		public String get(String key) {
			return this.mapper.get(key);
		}

	}

	private static abstract class Model {

		abstract public String keyToString();

		public List<String> getProperties() {
			List<String> list = new ArrayList<String>();
			for (Field field : this.getClass().getDeclaredFields()) {
				list.add(field.getName());
			}
			return list;
		}

		public Object getProperty(String property)
				throws NoSuchFieldException, IllegalAccessException,
				NoSuchMethodException, InvocationTargetException {
			Field f = this.getClass().getDeclaredField(property);
			f.setAccessible(true);
			/*
				if (f.getType() == Key.class) {
					Method method = this.getClass().getDeclaredMethod("keyToString");
					return method.invoke(this);
				}
			*/
			return f.get(this);
		}

		public void setProperty(String property, Object value)
				throws IllegalAccessException, NoSuchFieldException {
			Field f = this.getClass().getDeclaredField(property);
			f.setAccessible(true);
			f.set(this, value);
		}
	}
}
