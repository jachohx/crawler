package cn.jachohx.crawler.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jachohx.crawler.PictureAnalyzer;


public class PriceUtils {
	/**
	 * 过滤价格，去除多余的字符
	 * @param str ￥1,000.00
	 * @return 1000.00
	 */
	public static String getPrice(String str){
		if(str == null || str.isEmpty())return str;
		String price = "";
		Pattern pricePattern = Pattern.compile("([[0-9]{1,}\\,{0,1}]{1,}\\.{0,1}[0-9]{0,2})");
		Matcher matcher = pricePattern.matcher(str);
		if(matcher.find() && matcher.groupCount() > 0){
			price = matcher.group(0);
			price = price.replace(",", "");
		}
		return price;
	}
	
	/**
	 * 根据图片得到价格
	 * @param imgUrl
	 * @return
	 * @throws IOException
	 */
	public static String getPriceForUrl(String imgUrl) throws IOException{
		PictureAnalyzer picture = new PictureAnalyzer();
		picture.deleteOtherPixelMark();
		URL url = new URL(imgUrl);
		InputStream inputStream = url.openConnection().getInputStream();
		picture.monitorLogBuilder.append(imgUrl +"\n");
		picture.initImage(inputStream);
		String price = picture.analyseImage();
		picture.clear();
		return price;
	}
}
