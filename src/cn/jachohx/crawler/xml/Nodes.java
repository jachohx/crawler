package cn.jachohx.crawler.xml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import cn.jachohx.crawler.domain.Crawler;

public class Nodes extends CrawlerNode{
	
	public Nodes(Crawler crawler) {
		super(crawler);
	}

	@Override
	public void analyze(Node node) {
		NamedNodeMap attrs = node.getAttributes();
		Node xpath = attrs.getNamedItem("xpath");
		crawler.setListXPath(xpath.getNodeValue());
		super.analyze(node);
	}

	@Override
	public void childNode(Node child, String tagName) {
		if ("link".equals(tagName)) {
			new Link(crawler).analyze(child);
		} else if ("layout".equals(tagName)) {
			new Layout(crawler).analyze(child);
		}
	}

}
