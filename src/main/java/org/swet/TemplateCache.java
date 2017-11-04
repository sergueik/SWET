package org.swet;

import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

public class TemplateCache {
	private static Map<String, String> cache = Collections
			.synchronizedMap(new HashMap<String, String>());

	private static TemplateCache instance = new TemplateCache();

	private TemplateCache() {
	}

	public static TemplateCache getInstance() {
		return instance;
	}

	public void addItem(String tag, String path) {
		cache.put(tag, path);
	}

	public String getItem(String tag) {
		return cache.get(tag);
	}

	private String extractTag(String payload) {

		String templateTag = null;

		// find specially formatted twig comment in the template
		Matcher matcherTwigComment = Pattern
				.compile("\\{#(?:\\r?\\n)?(.*)(?:\\r?\\n)?#\\}", Pattern.MULTILINE)
				.matcher(payload);
		if (matcherTwigComment.find()) {
			String comment = matcherTwigComment.group(1);
			// extract template tag from the comment
			Matcher matcherTemplate = Pattern
					.compile("template: (.+)$", Pattern.MULTILINE).matcher(comment);
			if (matcherTemplate.find()) {
				templateTag = matcherTemplate.group(1);
				System.err
						.println(String.format("Discovered tag: \"%s\": ", templateTag));
			}
		}
		return templateTag;
	}

	public void fillCache() {
		// https://stackoverflow.com/questions/1429172/how-do-i-list-the-files-inside-a-jar-file
		CodeSource src = this.getClass().getProtectionDomain().getCodeSource();
		String note = "TODO";
		String templateTag = null;
		try {
			URL jar = src.getLocation();
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			ZipEntry ze = null;

			while ((ze = zip.getNextEntry()) != null) {
				String templateResourcePath = ze.getName();
				if (templateResourcePath.startsWith("templates")
						&& templateResourcePath.endsWith(".twig")) {
					InputStream inputStream = (new Utils())
							.getResourceStream(templateResourcePath);
					String templateContents = IOUtils.toString(inputStream, "UTF8");

					if (templateContents != null) {

						templateTag = extractTag(templateContents);
						if (templateTag != null) {
							String templateLabel = String.format("%s (%s)", templateTag,
									(note == null) ? "unknown" : note);
							System.err
									.println(String.format("Discovered template \"%s\": \"%s\": ",
											templateLabel, templateResourcePath));
							cache.put(templateTag, templateResourcePath);

						}
					}
					IOUtils.closeQuietly(inputStream);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String approxLookup(String templateTag) {
		Pattern pattern = Pattern.compile(Pattern.quote(templateTag),
				Pattern.CASE_INSENSITIVE);
		String matchedKey = null;
		for (String key : cache.keySet()) {
			Matcher matcher = pattern.matcher(key);
			if (matcher.find()) {
				matchedKey = key;
			}
		}
		return matchedKey;
	}

	public void clearCache() {
		cache.clear();
	}

	public Object removeEntry(String tag) {
		return cache.remove(tag);
	}

	public String toString() {
		StringBuffer result = new StringBuffer();
		for (String key : cache.keySet()) {
			result.append(key + ": " + cache.get(key));
			result.append("\n");
		}
		return result.toString();
	}
}
