package cn.jachohx.crawler.util;

public class StringUtils {
	/**
	 * hashCode,没有负数,如果是负数的话,加上10000000000l
	 * @param s
	 * @return
	 */
	public static long hashCode(String s) {
		int code = s.hashCode();
		long fieldHashCode = code < 0 ? (-code + 10000000000l) : code;
		return fieldHashCode;
	}
}
