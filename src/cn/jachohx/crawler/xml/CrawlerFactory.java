package cn.jachohx.crawler.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cn.jachohx.crawler.domain.Crawler;

public class CrawlerFactory {
	public final static String CRAWLER_CONFIG = "crawler.xml";
	public static CrawlerFactory factory;
	protected List<Crawler> list;
	public static CrawlerFactory getInstance() {
		if (factory == null) {
			factory = new CrawlerFactory();
		}
		return factory;
	}
	
	public List<Crawler> getCrawler() {
		if (list == null) {
			init();
		}
		return list;
	}
	
	protected void init() {
		analyzeFile(null);
	}
	
	public void rebuild() {
		init();
	}
	
	protected void analyzeFile(String path) {
		Document doc = path != null ? getDocument(path) : getDocument();
		if (doc == null) return ;
		if (list == null)
			list = new ArrayList<Crawler>();
		parse(doc);
	}
	
	protected Document getDocument() {
		return getDocument(CRAWLER_CONFIG);
	}
	
	protected Document getDocument(String path1) {
		ClassLoader classLoader = CrawlerFactory.class.getClassLoader();
		URL path = classLoader.getResource(path1);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Leave off validation, and turn off namespaces
        factory.setValidating(false);
        factory.setNamespaceAware(false);

        DocumentBuilder builder;
        Document doc = null;
        try {
        	builder = factory.newDocumentBuilder();
        	System.out.println(path.getPath());
			doc = builder.parse(new FileInputStream(path.getPath()));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return doc;
	}
	
	protected void parse(Document doc) {
		analyzeDocument(doc);
	}
	
	private void analyzeDocument(Node node)  {
        switch (node.getNodeType()) {
            case Node.DOCUMENT_NODE:
            	analyzeChildNode(node);
                break;
                
            case Node.ELEMENT_NODE:
                String name = node.getNodeName();
                //site
                if ("site".equals(name)) {
                	Crawler crawler = new Crawler();
                	Site site = new Site(crawler);
                	site.analyze(node);
                	list.add(crawler);
                } else if ("import".equals(name)) {
                	NamedNodeMap attrs = node.getAttributes();
                	Node importFilePath = attrs.getNamedItem("file");
                	if(importFilePath != null) {
                		analyzeFile(importFilePath.getNodeValue());
                	}
                } else {
                	analyzeChildNode(node);
                }
                break;
        }
    }
	
	private void analyzeChildNode(Node father) {
		NodeList children = father.getChildNodes();
        if (children != null) {
            for (int i=0; i<children.getLength(); i++) {
                analyzeDocument(children.item(i));
            }
        }
	}
}