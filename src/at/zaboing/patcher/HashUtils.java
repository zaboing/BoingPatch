package at.zaboing.patcher;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

public class HashUtils
{

	private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

	public static String toHexString(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes)
		{
			sb.append(HEX_CHARS[(b & 0xF0) >> 4]);
			sb.append(HEX_CHARS[b & 0x0F]);
		}
		return sb.toString();
	}

	public static byte[] hash(byte[] bytes)
	{
		return Hashing.murmur3_128().hashBytes(bytes).asBytes();
	}

	public static Hasher hasher()
	{
		return Hashing.murmur3_128().newHasher();
	}
}
