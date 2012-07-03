package cn.jachohx.crawler.xml;

import org.w3c.dom.Node;

import cn.jachohx.crawler.domain.Crawler;

public class Urls extends CrawlerNode{
	
	public Urls(Crawler crawler) {
		super(crawler);
	}

	@Override
	public void childNode(Node child, String tagName) {
		if ("url".equals(tagName)) {
			crawler.addInitUrl(child.getTextContent());
		}
	}

}
