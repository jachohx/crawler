package cn.jachohx.crawler.xml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import cn.jachohx.crawler.domain.Crawler;

public class NextPage extends CrawlerNode{
	
	public NextPage(Crawler crawler) {
		super(crawler);
	}

	@Override
	public void analyze(Node node) {
		NamedNodeMap attrs = node.getAttributes();
		Node replaceNode = attrs.getNamedItem("replace");
		Node temp = null;
		if (replaceNode != null && "true".equals(replaceNode.getNodeValue())) {
			crawler.setNextPageReplace(true);
			Node splitMarkNode = attrs.getNamedItem("splitMark");
			Node firstPageNoNode = attrs.getNamedItem("firstPageNo");
			Node pageStrNode = attrs.getNamedItem("pageStr");
			crawler.setNextPageSplitMark(splitMarkNode.getNodeValue());
			crawler.setFirstPageNo(Integer.parseInt(firstPageNoNode.getNodeValue()));
			crawler.setPageStr(pageStrNode.getNodeValue());
		} else if ((temp = attrs.getNamedItem("xpath")) != null){
			crawler.setNextPath(temp.getNodeValue());
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
