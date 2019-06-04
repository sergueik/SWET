package com.github.sergueik.swet;

import java.awt.event.KeyEvent;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.win32.W32APIOptions;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

// based on: https://toster.ru/q/636535
public class JavaHotkeyManager extends Thread {
	public static void register() {
		User32.RegisterHotKey(null, 1, 0x000, KeyEvent.VK_F);
		new JavaHotkeyManager().start();
	}

	public JavaHotkeyManager() {

	}

	public static void main(String[] args) {
		register();
		// run();
	}

	@Override
	public void run() {
		JavaHotkeyManager.MSG msg = new JavaHotkeyManager.MSG();
		System.err.println("Running " + this.toString());
		while (true) {
			// register the key on the same thread as listening
			User32.RegisterHotKey(null, 1, 0x000, KeyEvent.VK_F);
			while (User32.PeekMessage(msg, null, 0, 0, User32.PM_REMOVE)) {
				if (msg.message == User32.WM_HOTKEY) {
					System.out.println("Hotkey pressed with id: " + msg.wParam);
				}
			}

			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static class User32 {
		static {
			Native.register(
					NativeLibrary.getInstance("user32", W32APIOptions.DEFAULT_OPTIONS));
		}

		public static final int MOD_ALT = 0x0001;
		public static final int MOD_CONTROL = 0x0002;
		public static final int MOD_SHIFT = 0x0004;
		public static final int MOD_WIN = 0x0008;
		public static final int WM_HOTKEY = 0x0312;
		public static final int PM_REMOVE = 0x0001;

		public static native boolean RegisterHotKey(Pointer hWnd, int id,
				int fsModifiers, int vk);

		public static native boolean UnregisterHotKey(Pointer hWnd, int id);

		public static native boolean PeekMessage(JavaHotkeyManager.MSG lpMsg,
				Pointer hWnd, int wMsgFilterMin, int wMsgFilterMax, int wRemoveMsg);

	}

	public static class MSG extends Structure {
		public Pointer hWnd;
		public int lParam;
		public int message;
		public int time;
		public int wParam;
		public int x;
		public int y;

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "hWnd", "lParam", "message", "time",
					"wParam", "x", "y" });
		}
	}

}