package com.github.sergueik.swet;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.prefs.Preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// https://en.wikipedia.org/wiki/Java_Native_Access
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeMapped;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
// https://github.com/java-native-access/jna/blob/master/contrib/platform/src/com/sun/jna/platform/win32/Advapi32Util.java
import com.sun.jna.platform.win32.Advapi32Util;

import com.sun.jna.platform.win32.VerRsrc.VS_FIXEDFILEINFO;
// https://github.com/java-native-access/jna/blob/master/contrib/platform/src/com/sun/jna/platform/win32/WinReg.java
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

// origin: http://stackoverflow.com/questions/585534/what-is-the-best-way-to-find-the-users-home-directory-in-java 
// see also:https://github.com/java-native-access/jna/blob/master/contrib/ntservice/src/jnacontrib/win32/Win32Service.java
public class OSUtils {

	private static String osName = null;
	private static boolean is64bit = false;
	private static Map<String, String> installedBrowsers;

	public static String getOsName() {
		if (osName == null) {
			osName = System.getProperty("os.name").toLowerCase();
			System.err.println("OS Name: " + osName);
			if (osName.startsWith("windows")) {
				osName = "windows";
			}
		}
		return osName;
	}

	// origin:
	// http://stackoverflow.com/questions/585534/what-is-the-best-way-to-find-the-users-home-directory-in-java
	public static String getDesktopPath() {
		HWND hwndOwner = null;
		int nFolder = Shell32.CSIDL_DESKTOPDIRECTORY;
		HANDLE hToken = null;
		int dwFlags = Shell32.SHGFP_TYPE_CURRENT;
		char[] pszPath = new char[Shell32.MAX_PATH];
		int hResult = Shell32.INSTANCE.SHGetFolderPath(hwndOwner, nFolder, hToken,
				dwFlags, pszPath);
		if (Shell32.S_OK == hResult) {
			String path = new String(pszPath);
			return (path.substring(0, path.indexOf('\0')));
		} else {
			return String.format("%s\\Desktop", System.getProperty("user.home"));
		}
	}

	static class HANDLE extends PointerType implements NativeMapped {
	}

	static class HWND extends HANDLE {
	}

	private static Map<String, Object> OPTIONS = new HashMap<>();
	static {
		OPTIONS.put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
		OPTIONS.put(Library.OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
	}

	static interface Shell32 extends Library {

		public static final int MAX_PATH = 260;
		// https://sourceforge.net/u/cstrauss/w32api/ci/7805df8efec130f582b131b8c0d75e1b6ce0993b/tree/include/shlobj.h?format=raw
		public static final int CSIDL_DESKTOPDIRECTORY = 0x0010;
		public static final int SHGFP_TYPE_CURRENT = 0;
		public static final int SHGFP_TYPE_DEFAULT = 1;
		public static final int S_OK = 0;

		static Shell32 INSTANCE = Native.loadLibrary("shell32", Shell32.class,
				OPTIONS);

		/**
		 * see http://msdn.microsoft.com/en-us/library/bb762181(VS.85).aspx
		 * 
		 * HRESULT SHGetFolderPath( HWND hwndOwner, int nFolder, HANDLE hToken,
		 * DWORD dwFlags, LPTSTR pszPath);
		 */
		public int SHGetFolderPath(HWND hwndOwner, int nFolder, HANDLE hToken,
				int dwFlags, char[] pszPath);

	}

	// based on: https://github.com/AnarSultanov/InstalledBrowsers
	public static List<String> getInstalledBrowsers() {
		if (installedBrowsers == null) {
			findInstalledBrowsers();
		}
		List<String> browsersList = new ArrayList<>();
		installedBrowsers.keySet().forEach(o -> browsersList.add(o));
		return browsersList;
	}

	public static String getPath(String browserName) {
		if (installedBrowsers == null) {
			findInstalledBrowsers();
		}
		return installedBrowsers.containsKey(browserName)
				? installedBrowsers.get(browserName) : null;
	}

	public static boolean isInstalled(String browserName) {
		if (installedBrowsers == null) {
			findInstalledBrowsers();
		}
		return installedBrowsers.containsKey(browserName);
	}

	public static String getVersion(String browserName) {
		if (!isInstalled(browserName))
			return null;
		int[] version = getVersionInfo(installedBrowsers.get(browserName));
		return String.valueOf(version[0]) + "." + String.valueOf(version[1]) + "."
				+ String.valueOf(version[2]) + "." + String.valueOf(version[3]);
	}

	public static int getMajorVersion(String browserName) {
		return isInstalled(browserName)
				? getVersionInfo(installedBrowsers.get(browserName))[0] : 0;
	}

	public static int getMinorVersion(String browserName) {
		return isInstalled(browserName)
				? getVersionInfo(installedBrowsers.get(browserName))[1] : 0;
	}

	public static int getBuildVersion(String browserName) {
		return isInstalled(browserName)
				? getVersionInfo(installedBrowsers.get(browserName))[2] : 0;
	}

	public static int getRevisionVersion(String browserName) {
		return isInstalled(browserName)
				? getVersionInfo(installedBrowsers.get(browserName))[3] : 0;
	}

	public static List<String> findInstalledBrowsers() {
		if (System.getProperty("os.arch").contains("64")) {
			is64bit = true;
		} else {
			is64bit = false;
		}
		installedBrowsers = new HashMap<>();
		Set<String> pathsList = new HashSet<>();
		pathsList.addAll(findBrowsersInProgramFiles());
		pathsList.addAll(findBrowsersInRegistry());
		for (String path : pathsList) {
			String[] tmp = (path.split("\\\\"));
			String browser = tmp[tmp.length - 1];
			installedBrowsers.put(browser, path);
		}
		return new ArrayList(installedBrowsers.keySet());
	}

	@SuppressWarnings("unused")
	private static int[] getVersionInfo(String path) {
		if (installedBrowsers == null) {
			findInstalledBrowsers();
		}
		IntByReference dwDummy = new IntByReference();
		dwDummy.setValue(0);

		int versionlength = com.sun.jna.platform.win32.Version.INSTANCE
				.GetFileVersionInfoSize(path, dwDummy);

		byte[] bufferarray = new byte[versionlength];
		Pointer lpData = new Memory(bufferarray.length);
		PointerByReference lplpBuffer = new PointerByReference();
		IntByReference puLen = new IntByReference();
		boolean fileInfoResult = com.sun.jna.platform.win32.Version.INSTANCE
				.GetFileVersionInfo(path, 0, versionlength, lpData);
		boolean verQueryVal = com.sun.jna.platform.win32.Version.INSTANCE
				.VerQueryValue(lpData, "\\", lplpBuffer, puLen);

		VS_FIXEDFILEINFO lplpBufStructure = new VS_FIXEDFILEINFO(
				lplpBuffer.getValue());
		lplpBufStructure.read();

		int v1 = (lplpBufStructure.dwFileVersionMS).intValue() >> 16;
		int v2 = (lplpBufStructure.dwFileVersionMS).intValue() & 0xffff;
		int v3 = (lplpBufStructure.dwFileVersionLS).intValue() >> 16;
		int v4 = (lplpBufStructure.dwFileVersionLS).intValue() & 0xffff;
		return new int[] { v1, v2, v3, v4 };
	}

	public static List<String> findBrowsersInProgramFiles() {
		// find possible root
		File[] rootPaths = File.listRoots();
		List<String> browsers = new ArrayList<>();
		String[] defaultPath = (is64bit)
				? new String[] {
						"Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
						"Program Files (x86)\\Internet Explorer\\iexplore.exe",
						"Program Files (x86)\\Mozilla Firefox\\firefox.exe" }
				: new String[] {
						"Program Files\\Google\\Chrome\\Application\\chrome.exe",
						"Program Files\\Internet Explorer\\iexplore.exe",
						"Program Files\\Mozilla Firefox\\firefox.exe" };

		// check file existence
		for (File rootPath : rootPaths) {
			for (String defPath : defaultPath) {
				File exe = new File(rootPath + defPath);
				System.err.println("Inspecting browser path: " + rootPath + defPath);
				if (exe.exists()) {
					// browsers.add(exe.toString());
					String browser = exe.toString().replaceAll("\\\\", "/")
							.replaceAll("^(?:.+)/([^/]+)(.exe)$", "$1$2");
					System.err.println("Found browser: " + browser);
					browsers.add(browser);
				}
			}
		}
		return browsers;
	}

	// http://www.programcreek.com/java-api-examples/index.php?api=com.sun.jna.platform.win32.Advapi32Util
	// https://java-native-access.github.io/jna/4.2.0/com/sun/jna/platform/win32/Advapi32Util.html
	private static List<String> findBrowsersInRegistry() {
		// String regPath = "SOFTWARE\\Clients\\StartMenuInternet\\";
		String regPath = is64bit
				? "SOFTWARE\\Wow6432Node\\Clients\\StartMenuInternet\\"
				: "SOFTWARE\\Clients\\StartMenuInternet\\";

		List<String> browsers = new ArrayList<>();
		String path = null;
		try {
			for (String browserName : Advapi32Util
					.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, regPath)) {
				path = Advapi32Util
						.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE,
								regPath + "\\" + browserName + "\\shell\\open\\command", "")
						.replace("\"", "");
				if (path != null && new File(path).exists()) {
					// System.err.println("Browser path: " + path);
					// browsers.add(exe.toString());
					String browser = path.replaceAll("\\\\", "/")
							.replaceAll("^(?:.+)/([^/]+)(.exe)$", "$1$2");
					System.err.println("Found browser: " + browser);
					browsers.add(browser);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return browsers;
	}

	// https://github.com/java-native-access/jna/blob/master/contrib/platform/src/com/sun/jna/platform/win32/Advapi32Util.java
	// see also:
	// https://www.javatips.net/api/Wiring-master/IDE/processing/app/windows/Registry.java
	public static int getZoom() {
		int value = -1;
		try {
			value = Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER,
					"Software\\Microsoft\\Internet Explorer\\Zoom", "ZoomFactor");
		} catch (com.sun.jna.platform.win32.Win32Exception e) {
			System.err.println("Exception (ignored):  " + e.toString());
			// The system cannot find the file specified
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public static int getAdvancedOptionsZoom() {
		int value = -1;
		try {
			value = Advapi32Util.registryGetIntValue(WinReg.HKEY_LOCAL_MACHINE,
					"SOFTWARE\\Microsoft\\Internet Explorer\\AdvancedOptions\\ACCESSIBILITY\\ZOOMLEVEL",
					"CheckedValue");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	// https://www.javaworld.com/article/2071275/core-java/when-runtime-exec---won-t.html?page=2
	// see
	// also:https://github.com/java-native-access/jna/blob/master/contrib/ntservice/src/jnacontrib/win32/Win32Service.java

	public static void killProcess(String processName) {

		String command = String.format(
				(osName.equals("windows")) ? "taskkill.exe /F /IM %s" : "killall %s",
				processName.trim());

		try {
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(command);
			// process.redirectErrorStream( true);

			BufferedReader stdoutBufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			BufferedReader stderrBufferedReader = new BufferedReader(
					new InputStreamReader(process.getErrorStream()));
			String line = null;

			StringBuffer processOutput = new StringBuffer();
			while ((line = stdoutBufferedReader.readLine()) != null) {
				processOutput.append(line);
			}
			StringBuffer processError = new StringBuffer();
			while ((line = stderrBufferedReader.readLine()) != null) {
				processError.append(line);
			}
			int exitCode = process.waitFor();
			// ignore exit code 128: the process "<browser driver>" not found.
			if (exitCode != 0 && (exitCode ^ 128) != 0) {
				System.out.println("Process exit code: " + exitCode);
				if (processOutput.length() > 0) {
					System.out.println("<OUTPUT>" + processOutput + "</OUTPUT>");
				}
				if (processError.length() > 0) {
					// e.g.
					// The process "chromedriver.exe"
					// with PID 5540 could not be terminated.
					// Reason: Access is denied.
					System.out.println("<ERROR>" + processError + "</ERROR>");
				}
			}
		} catch (Exception e) {
			System.err.println("Exception (ignored): " + e.getMessage());
		}
	}

	// on osx to open the url in selected browser, use open
	// https://coderanch.com/t/111494/os/launching-Safari-Java-App
	// one can find the absolute path to the application via spotlight
	// /usr/bin/mdfind "kMDItemFSName = Firefox.app"
	// but running application directly via like
	// /Applications/Firefox.app/Contents/MacOS/firefox http://ya.ru

	// would likely lead to an error:
	// "A copy of Firefox is already open. Only one copy of Firefox can be open at
	// a time."
	// Only one copy of Firefox can be open at a time."
	public static void runAppCommand(String browserAppName, String url) {
		try {

			Runtime runtime = Runtime.getRuntime();
			String processName = null;
			String[] processArgs = new String[] {};
			if (osName.matches("mac os x")) {

				processName = "/usr/bin/open";
				processArgs = new String[] { processName, "-a", browserAppName, url };
				/* String.format("\\\"%s\\\"", browserAppName) */
				// TODO: quote handling
				// Running: open -a "Firefox" http://ya.ru
				// Unable to find application named '"Firefox"'
				// Running: open -a \"Google Chrome\" http://ya.ru
				// The file /Users/sergueik/src/Chrome\" does not exist.
			} else if (osName.matches("windows")) {
				processName = "C:\\Windows\\System32\\cmd.exe";
				processArgs = new String[] { processName, "/c", "start", browserAppName,
						url };
			} else {
				// TODO: on Linux need to compose the command with bash
				// to launch browser in the background
				// or make event handler operate on separate thread
				processName = "/usr/bin/env";
				processArgs = new String[] { processName, browserAppName, url };
			}
			System.err.println("Running: " + String.join(" ", processArgs));
			// Process process = runtime.exec(String.join(" ", processArgs));
			Process process = runtime.exec(processArgs);

			int exitCode = process.waitFor();
			BufferedReader stdoutBufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			BufferedReader stderrBufferedReader = new BufferedReader(
					new InputStreamReader(process.getErrorStream()));
			String line = null;
			StringBuffer processOutput = new StringBuffer();
			while ((line = stdoutBufferedReader.readLine()) != null) {
				processOutput.append(line);
			}
			StringBuffer processError = new StringBuffer();
			while ((line = stderrBufferedReader.readLine()) != null) {
				processError.append(line);
			}
			if (exitCode != 0 && (exitCode ^ 128) != 0) {
				System.out.println("Process exit code: " + exitCode);
				if (processOutput.length() > 0) {
					System.out.println("<OUTPUT>" + processOutput + "</OUTPUT>");
				}
				if (processError.length() > 0) {
					System.out.println("<ERROR>" + processError + "</ERROR>");
				}
			}
		} catch (Exception e) {
			System.err.println("Exception (ignored): " + e.getMessage());
		}
	}

	// TODO: on mac os x mdfind status is 0 regardless of whether search was
	// successful or failed.
	// one needs to examine the details of its output
	// NOTE: few code modifications to support linking with older version of
	// Selenium on OSX are not checked in
	public static boolean findAppInPath(String appName) {
		boolean status = false;
		String processName = null;
		String findCommand = null;
		if (osName.matches("mac os x")) {
			// NOTE: for cached saved spotlight search a.k.a. smart folder, use
			// findCommand = String.format("'kMDItemFSName = %s'", appName);
			// Could not find smart folder /Users/sergueik/Library/Saved
			// Searches/kMDItemFSName = 'Google Chrome'.savedSearch
			findCommand = String.format("-onlyin /Applications -name '%s'", appName);
			processName = "/usr/bin/mdfind";
		} else if (!(osName.equals("windows"))) {
			processName = "/usr/bin/which";
			findCommand = appName;
		}
		String[] processArgs = new String[] { processName, findCommand };
		System.err.println("Running: " + String.join(" ", processArgs));
		try {
			Runtime runtime = Runtime.getRuntime();

			Process process = runtime.exec(String.join(" ", processArgs));
			// process.redirectErrorStream( true);

			BufferedReader stdoutBufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			BufferedReader stderrBufferedReader = new BufferedReader(
					new InputStreamReader(process.getErrorStream()));
			String line = null;

			StringBuffer processOutput = new StringBuffer();
			while ((line = stdoutBufferedReader.readLine()) != null) {
				processOutput.append(line);
			}
			StringBuffer processError = new StringBuffer();
			while ((line = stderrBufferedReader.readLine()) != null) {
				processError.append(line);
			}
			int exitCode = process.waitFor();
			// ignore exit code 128: the process "<browser driver>" not found.
			if (exitCode != 0 && (exitCode ^ 128) != 0) {
				status = false;
				System.out.println("Process exit code: " + exitCode);
				if (processOutput.length() > 0) {
					System.out.println("<OUTPUT>" + processOutput + "</OUTPUT>");
					// Failed to create query for: '' kMDItemFSName = Firefox
					// ''.
				}
				if (processError.length() > 0) {
					// e.g.
					// The process "chromedriver.exe"
					// with PID 5540 could not be terminated.
					// Reason: Access is denied.
					System.out.println("<ERROR>" + processError + "</ERROR>");
				}
			} else {
				status = true;
			}
		} catch (Exception e) {
			System.err.println("Exception (ignored): " + e.getMessage());
		}
		return status;
	}

	// based on:
	// https://stackoverflow.com/questions/62289/read-write-to-windows-registry-using-java
	public static class WinRegistry {
		public static final int HKEY_CURRENT_USER = 0x80000001;
		public static final int HKEY_LOCAL_MACHINE = 0x80000002;
		public static final int REG_SUCCESS = 0;
		public static final int REG_NOTFOUND = 2;
		public static final int REG_ACCESSDENIED = 5;

		private static final int KEY_ALL_ACCESS = 0xf003f;
		private static final int KEY_READ = 0x20019;
		private static final Preferences userRoot = Preferences.userRoot();
		private static final Preferences systemRoot = Preferences.systemRoot();
		private static final Class<? extends Preferences> userClass = userRoot
				.getClass();
		private static final Method regOpenKey;
		private static final Method regCloseKey;
		private static final Method regQueryValueEx;
		private static final Method regEnumValue;
		private static final Method regQueryInfoKey;
		private static final Method regEnumKeyEx;

		// NOTE: commenting the wrapper for "RegCreateKeyEx"
		// does not solve the exception
		// WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at
		// root 0x80000002. Windows RegCreateKeyEx(...) returned error code 5.
		/*
		private static final Method regCreateKeyEx;
		private static final Method regSetValueEx;
		private static final Method regDeleteKey;
		private static final Method regDeleteValue;
		*/
		static {
			try {
				regOpenKey = userClass.getDeclaredMethod("WindowsRegOpenKey",
						new Class[] { int.class, byte[].class, int.class });
				regOpenKey.setAccessible(true);
				regCloseKey = userClass.getDeclaredMethod("WindowsRegCloseKey",
						new Class[] { int.class });
				regCloseKey.setAccessible(true);
				regQueryValueEx = userClass.getDeclaredMethod("WindowsRegQueryValueEx",
						new Class[] { int.class, byte[].class });
				regQueryValueEx.setAccessible(true);
				regEnumValue = userClass.getDeclaredMethod("WindowsRegEnumValue",
						new Class[] { int.class, int.class, int.class });
				regEnumValue.setAccessible(true);
				regQueryInfoKey = userClass.getDeclaredMethod("WindowsRegQueryInfoKey1",
						new Class[] { int.class });
				regQueryInfoKey.setAccessible(true);
				regEnumKeyEx = userClass.getDeclaredMethod("WindowsRegEnumKeyEx",
						new Class[] { int.class, int.class, int.class });
				regEnumKeyEx.setAccessible(true);
				/*
				regCreateKeyEx = userClass.getDeclaredMethod("WindowsRegCreateKeyEx",
						new Class[] { int.class, byte[].class });
				regCreateKeyEx.setAccessible(true);
				*/
				/*
				regSetValueEx = userClass.getDeclaredMethod("WindowsRegSetValueEx",
						new Class[] { int.class, byte[].class, byte[].class });
				regSetValueEx.setAccessible(true);
				*/
				/*
				regDeleteValue = userClass.getDeclaredMethod("WindowsRegDeleteValue",
						new Class[] { int.class, byte[].class });
				regDeleteValue.setAccessible(true);
				*/
				/*
				regDeleteKey = userClass.getDeclaredMethod("WindowsRegDeleteKey",
						new Class[] { int.class, byte[].class });
				regDeleteKey.setAccessible(true);
				*/
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	private WinRegistry() {
		}

	/**
	 * Read a value from key and value name
	 * @param hkey   HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
	 * @param key
	 * @param valueName
	 * @return the value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static String readString(int hkey, String key, String valueName)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		if (hkey == HKEY_LOCAL_MACHINE) {
			return readString(systemRoot, hkey, key, valueName);
		} else if (hkey == HKEY_CURRENT_USER) {
			return readString(userRoot, hkey, key, valueName);
		} else {
			throw new IllegalArgumentException("hkey=" + hkey);
		}
	}

	/**		 * Read value(s) and value name(s) form given key 
	 * @param hkey  HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
	 * @param key
	 * @return the value name(s) plus the value(s)
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Map<String, String> readStringValues(int hkey, String key)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		if (hkey == HKEY_LOCAL_MACHINE) {
			return readStringValues(systemRoot, hkey, key);
		} else if (hkey == HKEY_CURRENT_USER) {
			return readStringValues(userRoot, hkey, key);
		} else {
			throw new IllegalArgumentException("hkey=" + hkey);
		}
	}

	/**
	 * Read the value name(s) from a given key
	 * @param hkey  HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
	 * @param key
	 * @return the value name(s)
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static List<String> readStringSubKeys(int hkey, String key)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		if (hkey == HKEY_LOCAL_MACHINE) {
			return readStringSubKeys(systemRoot, hkey, key);
		} else if (hkey == HKEY_CURRENT_USER) {
			return readStringSubKeys(userRoot, hkey, key);
		} else {
			throw new IllegalArgumentException("hkey=" + hkey);
		}
	}

	/**
	 * Create a key
	 * @param hkey  HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
	 * @param key
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	/*
	public static void createKey(int hkey, String key)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		int[] ret;
		if (hkey == HKEY_LOCAL_MACHINE) {
			ret = createKey(systemRoot, hkey, key);
			regCloseKey.invoke(systemRoot, new Object[] { new Integer(ret[0]) });
		} else if (hkey == HKEY_CURRENT_USER) {
			ret = createKey(userRoot, hkey, key);
			regCloseKey.invoke(userRoot, new Object[] { new Integer(ret[0]) });
		} else {
			throw new IllegalArgumentException("hkey=" + hkey);
		}
		if (ret[1] != REG_SUCCESS) {
			throw new IllegalArgumentException("rc=" + ret[1] + "  key=" + key);
		}
	}
	*/

	/**
	 * Write a value in a given key/value name
	 * @param hkey
	 * @param key
	 * @param valueName
	 * @param value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	/*
	public static void writeStringValue(int hkey, String key, String valueName,
			String value) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		if (hkey == HKEY_LOCAL_MACHINE) {
			writeStringValue(systemRoot, hkey, key, valueName, value);
		} else if (hkey == HKEY_CURRENT_USER) {
			writeStringValue(userRoot, hkey, key, valueName, value);
		} else {
			throw new IllegalArgumentException("hkey=" + hkey);
		}
	}
	*/
	/**
	 * Delete a given key
	 * @param hkey
	 * @param key
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */

	/*
	public static void deleteKey(int hkey, String key)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		int rc = -1;
		if (hkey == HKEY_LOCAL_MACHINE) {
			rc = deleteKey(systemRoot, hkey, key);
		} else if (hkey == HKEY_CURRENT_USER) {
			rc = deleteKey(userRoot, hkey, key);
		}
		if (rc != REG_SUCCESS) {
			throw new IllegalArgumentException("rc=" + rc + "  key=" + key);
		}
	}
	*/
	/**
	 * delete a value from a given key/value name
	 * @param hkey
	 * @param key
	 * @param value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	/*
	public static void deleteValue(int hkey, String key, String value)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		int rc = -1;
		if (hkey == HKEY_LOCAL_MACHINE) {
			rc = deleteValue(systemRoot, hkey, key, value);
		} else if (hkey == HKEY_CURRENT_USER) {
			rc = deleteValue(userRoot, hkey, key, value);
		}
		if (rc != REG_SUCCESS) {
			throw new IllegalArgumentException(
					"rc=" + rc + "  key=" + key + "  value=" + value);
		}
	}
	*/
	// =====================
	/*
			private static int deleteValue(Preferences root, int hkey, String key,
					String value) throws IllegalArgumentException, IllegalAccessException,
					InvocationTargetException {
				int[] handles = (int[]) regOpenKey.invoke(root, new Object[] {
						new Integer(hkey), toCstr(key), new Integer(KEY_ALL_ACCESS) });
				if (handles[1] != REG_SUCCESS) {
					return handles[1]; // can be REG_NOTFOUND, REG_ACCESSDENIED
				}
				int rc = ((Integer) regDeleteValue.invoke(root,
						new Object[] { new Integer(handles[0]), toCstr(value) })).intValue();
				regCloseKey.invoke(root, new Object[] { new Integer(handles[0]) });
				return rc;
			}
	*/
	/*
	private static int deleteKey(Preferences root, int hkey, String key)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		int rc = ((Integer) regDeleteKey.invoke(root,
				new Object[] { new Integer(hkey), toCstr(key) })).intValue();
		return rc; // can REG_NOTFOUND, REG_ACCESSDENIED, REG_SUCCESS
	}
	*/

	private static String readString(Preferences root, int hkey, String key,
			String value) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		int[] handles = (int[]) regOpenKey.invoke(root,
				new Object[] { new Integer(hkey), toCstr(key), new Integer(KEY_READ) });
		if (handles[1] != REG_SUCCESS) {
			return null;
		}
		byte[] valb = (byte[]) regQueryValueEx.invoke(root,
				new Object[] { new Integer(handles[0]), toCstr(value) });
		regCloseKey.invoke(root, new Object[] { new Integer(handles[0]) });
		return (valb != null ? new String(valb).trim() : null);
	}

	private static Map<String, String> readStringValues(Preferences root,
			int hkey, String key) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		HashMap<String, String> results = new HashMap<String, String>();
		int[] handles = (int[]) regOpenKey.invoke(root,
				new Object[] { new Integer(hkey), toCstr(key), new Integer(KEY_READ) });
		if (handles[1] != REG_SUCCESS) {
			return null;
		}
		int[] info = (int[]) regQueryInfoKey.invoke(root,
				new Object[] { new Integer(handles[0]) });

		int count = info[0]; // count
		int maxlen = info[3]; // value length max
		for (int index = 0; index < count; index++) {
			byte[] name = (byte[]) regEnumValue.invoke(root,
					new Object[] { new Integer(handles[0]), new Integer(index),
							new Integer(maxlen + 1) });
			String value = readString(hkey, key, new String(name));
			results.put(new String(name).trim(), value);
		}
		regCloseKey.invoke(root, new Object[] { new Integer(handles[0]) });
		return results;
	}

	private static List<String> readStringSubKeys(Preferences root, int hkey,
			String key) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		List<String> results = new ArrayList<String>();
		int[] handles = (int[]) regOpenKey.invoke(root,
				new Object[] { new Integer(hkey), toCstr(key), new Integer(KEY_READ) });
		if (handles[1] != REG_SUCCESS) {
			return null;
		}
		int[] info = (int[]) regQueryInfoKey.invoke(root,
				new Object[] { new Integer(handles[0]) });

		int count = info[0]; // Fix: info[2] was being used here with wrong
													// results. Suggested by davenpcj, confirmed by
													// Petrucio
		int maxlen = info[3]; // value length max
		for (int index = 0; index < count; index++) {
			byte[] name = (byte[]) regEnumKeyEx.invoke(root,
					new Object[] { new Integer(handles[0]), new Integer(index),
							new Integer(maxlen + 1) });
			results.add(new String(name).trim());
		}
		regCloseKey.invoke(root, new Object[] { new Integer(handles[0]) });
		return results;
	}

	/*
			private static int[] createKey(Preferences root, int hkey, String key)
					throws IllegalArgumentException, IllegalAccessException,
					InvocationTargetException {
				return (int[]) regCreateKeyEx.invoke(root,
						new Object[] { new Integer(hkey), toCstr(key) });
			}
	*/
	/*
	private static void writeStringValue(Preferences root, int hkey, String key,
			String valueName, String value) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		int[] handles = (int[]) regOpenKey.invoke(root, new Object[] {
				new Integer(hkey), toCstr(key), new Integer(KEY_ALL_ACCESS) });
	
		regSetValueEx.invoke(root, new Object[] { new Integer(handles[0]),
				toCstr(valueName), toCstr(value) });
		regCloseKey.invoke(root, new Object[] { new Integer(handles[0]) });
	}
	*/
	// utility
	private static byte[] toCstr(String str) {
		byte[] result = new byte[str.length() + 1];

		for (int i = 0; i < str.length(); i++) {
			result[i] = (byte) str.charAt(i);
		}
		result[str.length()] = 0;
		return result;
	}
}}
