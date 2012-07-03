package cn.jachohx.crawler.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.jachohx.crawler.domain.Crawler;

public abstract class CrawlerNode {
	protected Crawler crawler;
	
	public CrawlerNode(Crawler crawler) {
		this.crawler = crawler;
	}
	
	public void analyze(Node node) {
		analyzeChildren(node);
	}
	public void analyzeChildren(Node node) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String tagName = child.getNodeName();
			childNode(child, tagName);
		}
	}
	
	public abstract void childNode(Node child, String tagName);
}
