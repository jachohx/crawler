package cn.jachohx.crawler.util;

public class StringUtils {
	/**
	 * hashCode,û�и���,����Ǹ����Ļ�,����10000000000l
	 * @param s
	 * @return
	 */
	public static long hashCode(String s) {
		int code = s.hashCode();
		long fieldHashCode = code < 0 ? (-code + 10000000000l) : code;
		return fieldHashCode;
	}
}
