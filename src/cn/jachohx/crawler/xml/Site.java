package cn.jachohx.crawler.xml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import cn.jachohx.crawler.domain.Crawler;

public class Site extends CrawlerNode{
	
	public Site(Crawler crawler) {
		super(crawler);
	}

	@Override
	public void analyze(Node node) {
		NamedNodeMap attrs = node.getAttributes();
		Node name = attrs.getNamedItem("name");
		Node type = attrs.getNamedItem("type");
		Node charsetNode = attrs.getNamedItem("charset");
		System.out.println(name.getNodeValue() + " : " + type.getNodeValue());
		crawler.setMarket(name.getNodeValue());
		crawler.setType(type.getNodeValue());
		if (charsetNode != null) {
			String charset = charsetNode.getNodeValue();
			crawler.setCharset(charset);
			System.out.println("Charset : " + charset);
		}
		super.analyze(node);
	}

	@Override
	public void childNode(Node child, String tagName) {
		if ("urls".equals(tagName)) {
			new Urls(crawler).analyze(child);
		} else if ("nodes".equals(tagName)) {
			new Nodes(crawler).analyze(child);
		} else if ("nextPage".equals(tagName)) {
			new NextPage(crawler).analyze(child);
		}
	}

}
