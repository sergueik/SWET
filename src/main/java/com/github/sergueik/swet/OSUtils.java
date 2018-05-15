package com.github.sergueik.swet;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeMapped;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.VerRsrc.VS_FIXEDFILEINFO;
import com.sun.jna.platform.win32.WinReg;
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
			if (osName.startsWith("windows")) {
				osName = "windows";
			}
		}
		return osName;
	}

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

	// https://www.javaworld.com/article/2071275/core-java/when-runtime-exec---won-t.html?page=2
	public static void killProcess(String processName) {

		String command = String.format((osName.toLowerCase().startsWith("windows"))
				? "taskkill.exe /F /IM %s" : "killall %s", processName.trim());

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

	// would lead to an error:
	// "A copy of Firefox is already open.
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

	// TODO:
	public static boolean findAppInPath(String appName) {
		boolean status = false;
		String processName = null;
		String findCommand = null;
		if (osName.toLowerCase().matches("mac os x")) {
			findCommand = String.format("'kMDItemFSName = %s'", appName);
			processName = "/usr/bin/mdfind";
		} else if (!(osName.startsWith("windows"))) {
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
					// Failed to create query for: '' kMDItemFSName = Firefox ''.
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

}