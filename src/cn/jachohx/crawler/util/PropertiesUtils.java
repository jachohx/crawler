package cn.jachohx.crawler.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class PropertiesUtils {
	public static Properties getProterties(String name) {
		Properties config = new Properties();
		ClassLoader classLoader = PropertiesUtils.class.getClassLoader();
	    URL path = classLoader.getResource(name);
		try {
			config.load(new FileInputStream(path.getPath()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return config;
	}
}