package common.extensions;

public final class StringExtension {
	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}
}
