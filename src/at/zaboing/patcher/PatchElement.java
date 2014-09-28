package at.zaboing.patcher;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

public abstract class PatchElement
{

	public abstract String getName();

	public abstract String getHash(String rootDir);

	public abstract void zip(ZipOutputStream zipStream, String rootDir) throws IOException;

	public static String escapeString(String s)
	{
		char fileSep = '/';
		char escape = '%';
		int len = s.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
		{
			char ch = s.charAt(i);
			if (ch < ' ' || ch >= 0x7F || ch == fileSep || ch == '\\' || (ch == '.' && i == 0) || ch == escape)
			{
				sb.append(escape);
				if (ch < 0x10)
				{
					sb.append('0');
				}
				sb.append(Integer.toHexString(ch));
			}
			else
			{
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	public static String unescapeString(String escapedString)
	{
		char escape = '%';
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < escapedString.length(); i++)
		{
			char ch = escapedString.charAt(i);
			if (ch == escape && i + 2 < escapedString.length())
			{
				String hexString = escapedString.substring(i + 1, i + 3);
				i += 2;
				sb.append((char) Integer.parseInt(hexString, 16));
			}
			else
			{
				sb.append(ch);
			}
		}
		return sb.toString();
	}
}
