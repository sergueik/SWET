package common.extensions;

public final class BitExtension {
	public static int clearBits(int value, int bits) {
		return value & ~bits;
	}
}
