package com.github.sergueik.swet;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
	private Utils utils = Utils.getInstance();
	private static Map<String, String> cache = Collections
			.synchronizedMap(new HashMap<String, String>());

	private static TemplateCache instance = new TemplateCache();

	private TemplateCache() {
	}

	public static TemplateCache getInstance() {
		return instance;
	}

	public static Map<String, String> getCache() {
		return cache;
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

	public void fillTemplateDirectoryCache(final File dir, String note,
			Map<String, String> templates) {
		FileReader fileReader = null;
		String contents = null;
		if (dir.listFiles().length == 0) {
			return;
		}
		for (final File fileEntry : dir.listFiles()) {
			contents = null;
			if (fileEntry.getName().endsWith(".twig")) {
				if (fileEntry.isFile()) {
					try {
						fileReader = new FileReader(fileEntry);
						char[] template = new char[(int) fileEntry.length()];
						fileReader.read(template);
						contents = new String(template);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (fileReader != null) {
							try {
								fileReader.close();
							} catch (IOException e) {
							}
						}
					}
				}
				if (contents != null) {

					String templateName = extractTag(contents);
					if (templateName != null) {
						String templateLabel = String.format("%s (%s)", templateName,
								(note == null) ? "unknown" : note);
						String templateAbsolutePath = fileEntry.getAbsolutePath();
						System.out.println(String.format("Make option for \"%s\": \"%s\"",
								templateAbsolutePath, templateLabel));
						if (templates.containsKey(templateLabel)) {
							templates.replace(templateLabel, templateAbsolutePath);
						} else {
							templates.put(templateLabel, templateAbsolutePath);
						}
						System.out.println(String.format("Data for option \"%s\": \"%s\"",
								templateLabel, templates.get(templateLabel)));
					} else {
						System.out
								.println(String.format("no tag: %s", fileEntry.getName()));
					}
				}
			}
		}
	}

	public void fillEmbeddedTemplateCache() {
		// https://stackoverflow.com/questions/1429172/how-do-i-list-the-files-inside-a-jar-file
		CodeSource src = this.getClass().getProtectionDomain().getCodeSource();
		String note = "embedded";
		String templateTag = null;
		try {
			URL jar = src.getLocation();
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			ZipEntry ze = null;

			while ((ze = zip.getNextEntry()) != null) {
				String templateResourcePath = ze.getName();
				if (templateResourcePath.startsWith("templates")
						&& templateResourcePath.endsWith(".twig")) {
					InputStream inputStream = utils
							.getResourceStream(templateResourcePath);
					String templateContents = IOUtils.toString(inputStream, "UTF8");

					if (templateContents != null) {

						templateTag = extractTag(templateContents);
						if (templateTag != null) {
							String templateLabel = String.format("%s (embedded)",
									templateTag);
							System.err
									.println(String.format("Discovered template \"%s\": \"%s\": ",
											templateLabel, templateResourcePath));
							synchronized (cache) {
								if (cache.containsKey(templateLabel)) {
									cache.replace(templateLabel, templateResourcePath);
								} else {
									cache.put(templateLabel, templateResourcePath);
								}
							}

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
