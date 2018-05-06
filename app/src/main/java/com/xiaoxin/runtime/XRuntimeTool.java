package com.xiaoxin.runtime;
import com.xiaoxin.util.XDigest;
public class XRuntimeTool
{
	public static String toCommandHex(byte[] bytes)
	{
		StringBuffer buf = new StringBuffer();
		String hex = XDigest.Hex.encode(bytes);
		int length = hex.length();
		for (int i = 0;i < length / 2;i++)
		{
			buf.append('\\');
			buf.append('x');
			buf.append(hex.charAt(i * 2));
			buf.append(hex.charAt(i * 2 + 1));
		}
		return buf.toString();
	}
}

