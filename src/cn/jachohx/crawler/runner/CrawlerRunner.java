package cn.jachohx.crawler.runner;
import cn.jachohx.crawler.controller.CrawlerController;


public class CrawlerRunner {
	public static void main(String[] args) {
		CrawlerController controller = new CrawlerController();
		controller.start();
	}
}
