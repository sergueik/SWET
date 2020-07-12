package com.github.sergueik.swet;
/**
 * Copyright 2020 Serguei Kouzmine
 */

import com.sun.jna.Native;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinUser;

// origin: https://stackoverflow.com/questions/28538234/sending-a-keyboard-input-with-java-jna-and-sendinput
// see also: https://www.codeproject.com/Articles/5264831/How-to-Send-mouseInputs-using-Csharp

public class User32WakeupTest {

	private static final int WM_MOUSEMOVE = 512;
	private static final int WM_LBUTTONDOWN = 513;
	// https://docs.microsoft.com/en-us/windows/win32/inputdev/wm-lbuttonup
	private static final int WM_LBUTTONUP = 0x0202; // 514
	private static final int WM_RBUTTONDOWN = 516;

	// https://docs.microsoft.com/en-us/windows/win32/inputdev/wm-keyup
	private static final int WM_KEYDOWN = 0x0100; // 514
	private static final int WM_KEYUP = 0x0101;
	// String to locate target window by title starting with (downcase)
	// like ubiquitous "Untitled - Notepad"
	private static String title = "untitled";
	private static String message = "wakeup neo";
	private static WinUser.INPUT input;
	private static WinDef.RECT rect;
	private static WinDef.POINT point;
	private static int delay = 100;

	public static void main(String[] args) {
		// inspect all windows
		User32.INSTANCE.EnumWindows((hWnd, data) -> {
			char[] name = new char[512];

			User32.INSTANCE.GetWindowText(hWnd, name, name.length);

			if (Native.toString(name).toLowerCase().startsWith(title)) {
				System.err.println("Found target window");
				// Bring the window to the front
				User32.INSTANCE.SetForegroundWindow(hWnd);

				// instantiate keyboard input reference
				input = new WinUser.INPUT();
				input.type = new WinDef.DWORD(WinUser.INPUT.INPUT_KEYBOARD);
				input.input.setType("ki");
				// https://groups.google.com/d/msg/jna-users/NDBGwC1VZbU/cjYCQ1CjBwAJ
				input.input.ki.wScan = new WinDef.WORD(0);
				input.input.ki.time = new WinDef.DWORD(0);
				input.input.ki.dwExtraInfo = new BaseTSD.ULONG_PTR(0);

				char[] chars = message.toUpperCase().toCharArray();
				for (int index = 0; index < chars.length; index++) {
					char letter = chars[index];

					System.err.println(String.format("type: %c", letter));
					input.input.ki.wVk = new WinDef.WORD(letter);
					// 0x41 for 'a'etc.
					input.input.ki.dwFlags = new WinDef.DWORD(0);
					// keydown

					User32.INSTANCE.SendInput(new WinDef.DWORD(1),
							(WinUser.INPUT[]) input.toArray(1), input.size());

					// Release
					input.input.ki.wVk = new WinDef.WORD(letter);
					// keyup
					input.input.ki.dwFlags = new WinDef.DWORD(2);

					User32.INSTANCE.SendInput(new WinDef.DWORD(1),
							(WinUser.INPUT[]) input.toArray(1), input.size());
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
					}
				}
				// Prepare mouse button press event reference
				input = new WinUser.INPUT();

				input.type = new WinDef.DWORD(WinUser.INPUT.INPUT_MOUSE);
				input.input.setType("mi");

				// click on the center of the target window
				// https://www.codota.com/code/java/methods/com.sun.jna.platform.win32.User32/GetWindowRect
				rect = new WinDef.RECT();
				// https://www.codota.com/code/java/methods/com.sun.jna.platform.win32.Kernel32/GetLastError
				if (!User32.INSTANCE.GetWindowRect(hWnd, rect)) {
					throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
				}

				point = new WinDef.POINT();
				point.x = (rect.left + rect.right) / 2;
				point.y = (rect.top + rect.bottom) / 2;
				System.err.println(
						String.format("Drawing line from (%d, %d)", point.x, point.y));
				input.input.mi.dx = new WinDef.LONG(point.x);
				input.input.mi.dy = new WinDef.LONG(point.y);
				// https://www.programcreek.com/java-api-examples/?api=com.sun.jna.platform.win32.WinDef
				// https://www.programcreek.com/java-api-examples/?code=martin-lizner/trezor-ssh-agent/trezor-ssh-agent-master/src/main/java/com/trezoragent/mouselistener/JNIMouseHook.java

				User32.INSTANCE.SetCursorPos(point.x, point.y);
				input.input.mi.dwFlags = new WinDef.DWORD(
						WM_LBUTTONDOWN | WM_MOUSEMOVE);

				input.input.mi.time = new WinDef.DWORD(0);
				input.input.mi.dx = new WinDef.LONG(0);
				input.input.mi.dy = new WinDef.LONG(0);
				input.input.ki.dwExtraInfo = new BaseTSD.ULONG_PTR(0);
				User32.INSTANCE.SendInput(new WinDef.DWORD(1),
						(WinUser.INPUT[]) input.toArray(1), input.size());

				// Prepare mouse button release event reference
				// input = new WinUser.INPUT();

				// input.type = new WinDef.DWORD(WinUser.INPUT.INPUT_MOUSE);
				// input.input.setType("mi");

				input.input.mi.dx = new WinDef.LONG(200);
				input.input.mi.dy = new WinDef.LONG(200);

				input.input.mi.dwFlags = new WinDef.DWORD(WM_LBUTTONDOWN | WM_MOUSEMOVE);
				User32.INSTANCE.SendInput(new WinDef.DWORD(1),
						(WinUser.INPUT[]) input.toArray(1), input.size());


				input.input.mi.time = new WinDef.DWORD(0);
				input.input.ki.dwExtraInfo = new BaseTSD.ULONG_PTR(0);
				User32.INSTANCE.SendInput(new WinDef.DWORD(1),
						(WinUser.INPUT[]) input.toArray(1), input.size());
				input.input.mi.dx = new WinDef.LONG(0);
				input.input.mi.dy = new WinDef.LONG(0);

				input.input.mi.dwFlags = new WinDef.DWORD(WM_LBUTTONUP);

				input.input.mi.time = new WinDef.DWORD(0);
				input.input.ki.dwExtraInfo = new BaseTSD.ULONG_PTR(0);
				User32.INSTANCE.SendInput(new WinDef.DWORD(1),
						(WinUser.INPUT[]) input.toArray(1), input.size());

				User32.INSTANCE.GetCursorPos(point);
				System.err.println(String.format("Releasing mouse button at (%d, %d)",
						point.x, point.y));

				return false; // exit enum windows loop
			}

			return true; // Keep searching
		}, null);
	}
}
