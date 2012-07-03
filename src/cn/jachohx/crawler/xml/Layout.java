package cn.jachohx.crawler.xml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import cn.jachohx.crawler.domain.Crawler;

public class Layout extends CrawlerNode{
	
	public Layout(Crawler crawler) {
		super(crawler);
	}

	@Override
	public void analyze(Node node) {
		NamedNodeMap attrs = node.getAttributes();
		Node xpathNode = attrs.getNamedItem("xpath");
		Node typeNode = attrs.getNamedItem("type");
		String type = null;
		if (typeNode != null) 
			type = typeNode.getNodeValue();
		if ("url".equals(type)) {
			crawler.setUrlXPath(xpathNode.getNodeValue());
		} else if ("price".equals(type)) {
			crawler.setPriceXPath(xpathNode.getNodeValue());
		} else if ("priceImg".equals(type)) {
			crawler.setImgXPath(xpathNode.getNodeValue());
		}
		super.analyze(node);
	}

	@Override
	public void analyzeChildren(Node node) {
	}

	@Override
	public void childNode(Node child, String tagName) {
	}

}
