package cn.jachohx.crawler.xml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import cn.jachohx.crawler.domain.Crawler;

public class Link extends CrawlerNode{
	
	public Link(Crawler crawler) {
		super(crawler);
	}

	@Override
	public void analyze(Node node) {
		NamedNodeMap attrs = node.getAttributes();
		Node regex = attrs.getNamedItem("regex");
		Node filter = attrs.getNamedItem("filter");
		crawler.setProductSidRegex(regex.getNodeValue());
		if (filter != null)
			crawler.setFilter(true);
		super.analyze(node);
	}

	@Override
	public void childNode(Node child, String tagName) {
		if ("param".equals(tagName)) {
			
		}
	}

}
