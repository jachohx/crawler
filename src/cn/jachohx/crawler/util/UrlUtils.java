package cn.jachohx.crawler.util;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtils {
	
	/**
	 * ����contentType�õ�HTML�ַ�����
	 * @param contentType
	 * @return
	 */
	public static String getCharsetFromContentTypeString(String contentType){
		if (contentType != null) {
            String pattern = "charset=([a-z\\d\\-]*)";
            Matcher matcher = Pattern.compile(pattern,  Pattern.CASE_INSENSITIVE).matcher(contentType);
            if (matcher.find()) {
                String charset = matcher.group(1);
                if (Charset.isSupported(charset)) {
                    return charset;
                }
            }
        }
        return null;
	}
	
	/**
	 * �õ����Ե�ַ
	 * @param initUrl ��ʼ�����Ե�ַ
	 * @param url ��Ե�ַ
	 * @return
	 */
	public static String getAbsoluteUrl(String initUrl, String url){
		String _url = url;
		if (_url != null && !_url.contains("http://")) {
			if (_url.startsWith("/")) {
				_url = initUrl.substring(0, initUrl.indexOf("/", 8)) + _url;
			} else {
				_url = initUrl.substring(0, initUrl.lastIndexOf("/")) + "/" + _url;
			}
		}
		return _url;
	}
	
	public static void main(String[] args) {
		System.out.println(getAbsoluteUrl("http://www.coo8.com/products/600-0-0-0-0.html", 
				"/products/600-0-0-0-2-0-101101.html"));
	}
}
