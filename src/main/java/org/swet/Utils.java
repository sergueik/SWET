package org.swet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Common utilities class for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class Utils {

	private static Utils instance = new Utils();

	private Utils() {
	}

	public static Utils getInstance() {
		return instance;
	}

	public String getScriptContent(String resourceFileName) {
		try {
			/* System.err.println("Script contents: " + getResourceURI(resourceFileName));		*/
			final InputStream stream = this.getClass().getClassLoader()
					.getResourceAsStream(resourceFileName);
			final byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			return new String(bytes, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// NOTE: getResourceURI does not work well with standalone app.
	public String getResourceURI(String resourceFileName) {
		try {
			URI uri = this.getClass().getClassLoader().getResource(resourceFileName)
					.toURI();
			System.err.println("Resource URI: " + uri.toString());
			return uri.toString();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream getResourceStream(String resourceFilePath) {
		return this.getClass().getClassLoader()
				.getResourceAsStream(resourceFilePath);
	}

	public String getResourcePath(String resourceFileName) {
		return String.format("%s/src/main/resources/%s",
				System.getProperty("user.dir"), resourceFileName);
	}

	public String writeDataJSON(Map<String, String> data, String defaultPayload) {
		String payload = defaultPayload;
		JSONObject json = new JSONObject();
		try {
			for (String key : data.keySet()) {
				json.put(key, data.get(key));
			}
			StringWriter wr = new StringWriter();
			json.write(wr);
			payload = wr.toString();
		} catch (JSONException e) {
			System.err.println("Exception (ignored): " + e);
		}
		return payload;
	}

	public String readData(Optional<Map<String, String>> parameters) {
		return readData(null, parameters);
	}

	public String readData(String payload,
			Optional<Map<String, String>> parameters) {

		Map<String, String> collector = (parameters.isPresent()) ? parameters.get()
				: new HashMap<>();

		String data = (payload == null)
				? "{ \"Url\": \"http://www.google.com\", \"ElementCodeName\": \"Name of the element\", \"CommandId\": \"d5be4ea9-c51f-4e61-aefc-e5c83ba00be8\", \"ElementCssSelector\": \"html div.home-logo_custom > img\", \"ElementId\": \"\", \"ElementXPath\": \"/html//img[1]\" }"
				: payload;
		try {
			JSONObject elementObj = new JSONObject(data);
			@SuppressWarnings("unchecked")
			Iterator<String> propIterator = elementObj.keys();
			while (propIterator.hasNext()) {
				String propertyKey = propIterator.next();

				String propertyVal = elementObj.getString(propertyKey);
				System.err.println(propertyKey + ": " + propertyVal);
				collector.put(propertyKey, propertyVal);
			}
		} catch (JSONException e) {
			System.err.println("Ignored exception: " + e.toString());
			return null;
		}
		return collector.get("ElementCodeName");
	}

	public static boolean createFolder(String path) throws Exception {
		File dir = new File(path.trim());
		if (!dir.exists()) {
			return dir.mkdir();
		} else
			return false;
	}

	/*
	public static String getDate() {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			return dateFormat.format(date);
		} catch (Exception e) {
		}
		return "";
	}
	
	public static String getTime() {
		try {
			DateFormat dateFormat = new SimpleDateFormat("HHmmss");
			Date date = new Date();
			return dateFormat.format(date);
		} catch (Exception e) {
		}
		return "";
	}
	*/

	public static void writeToFile(List<String> content, String filename,
			Boolean overwriteFlag) {
		File file = new File(filename);
		if (overwriteFlag) {

			try {
				file.createNewFile();
				FileWriter fw = null;
				try {
					fw = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					for (String line : content) {
						bw.write(line);
						bw.newLine();
					}
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Write content to " + filename + " succesfully!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static List<String> readFileLineByLine(String filename)
			throws IOException {
		FileInputStream fis = new FileInputStream(filename);
		// Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		List<String> res = new ArrayList<>();

		String line = null;
		while ((line = br.readLine()) != null) {
			res.add(line);
		}
		br.close();
		return res;
	}

	// origin:
	// https://github.com/TsvetomirSlavov/wdci/blob/master/code/src/main/java/com/seleniumsimplified/webdriver/manager/EnvironmentPropertyReader.java
	public static String getPropertyEnv(String name, String defaultValue) {
		String value = System.getProperty(name);
		if (value == null) {
			value = System.getenv(name);
			if (value == null) {
				value = defaultValue;
			}
		}
		return value;
	}

	public static String resolveEnvVars(String input) {
		if (null == input) {
			return null;
		}
		Pattern p = Pattern.compile("\\$(?:\\{(\\w+)\\}|(\\w+))");
		Matcher m = p.matcher(input);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
			String envVarValue = System.getenv(envVarName);
			m.appendReplacement(sb,
					null == envVarValue ? "" : envVarValue.replace("\\", "\\\\"));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public String readManifestVersion() {
		// https://stackoverflow.com/questions/1429172/how-do-i-list-the-files-inside-a-jar-file
		CodeSource src = this.getClass().getProtectionDomain().getCodeSource();
		String result = null;
		String manifestTag = "Implementation-Version: (0.0.8(?:\\-SNAPSHOT)*)";
		try {
			URL jar = src.getLocation();
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			ZipEntry ze = null;
			while ((ze = zip.getNextEntry()) != null) {
				String manifestResourcePath = ze.getName();
				if (manifestResourcePath.endsWith("MANIFEST.MF")) {
					InputStream inputStream = getResourceStream(manifestResourcePath);
					String manifestSource = IOUtils.toString(inputStream, "UTF8");

					Pattern pattern = Pattern.compile(manifestTag,
							Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(Pattern.quote(manifestSource));
					if (matcher.find()) {
						result = matcher.group(1);
						System.err.println("Discovered version: " + result
								+ " in manifest : " + manifestResourcePath);
					}
					IOUtils.closeQuietly(inputStream);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
