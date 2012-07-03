package cn.jachohx.crawler.fetcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import cn.jachohx.crawler.config.CrawlConfig;
import cn.jachohx.crawler.domain.Crawler;
import cn.jachohx.crawler.domain.Product;
import cn.jachohx.crawler.url.WebURL;
import cn.jachohx.crawler.util.EntityUtils;
import cn.jachohx.crawler.util.PriceUtils;
import cn.jachohx.crawler.util.StringUtils;
import cn.jachohx.crawler.util.UrlUtils;

public class CrawlerFetcher {
	Crawler crawler = null;
	boolean completed = false;
	PageFetcher pageFetcher = null;
	List<Product> products = null;
	
	boolean nextPageReplace;
	String pageStr;
	String nextPageSplitMark;
	public CrawlerFetcher(Crawler crawler) {
		this.crawler = crawler;
		CrawlConfig config = new CrawlConfig();
		config.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.11 " +
				"(KHTML, like Gecko) Chrome/20.0.1132.8 Safari/536.11");
		pageFetcher = new PageFetcher(config);
		nextPageReplace = crawler.isNextPageReplace();
		pageStr = crawler.getPageStr();
		nextPageSplitMark = crawler.getNextPageSplitMark();
	}
	
	public int fetcher() {
		List<String> urls = crawler.getInitUrl();
        for(String url : urls)
        	fetcher(url);
		return crawler.getTotal();
	}
	
	protected void fetcher(String url) {
        int pageNo = crawler.getFirstPageNo();
    	while(url != null && !completed) {
    		String _url = null;
    		try {
    			_url = analyse(url);
    		} catch (Exception e) {
    			e.printStackTrace(System.out);
    			return ;
    		}
    		if(_url != null && !_url.contains("http://")){
    			_url = UrlUtils.getAbsoluteUrl(url, _url);
    		} else if(_url == null && nextPageReplace){
    			_url = url.replaceAll(pageStr + nextPageSplitMark + (pageNo++), 
    					pageStr + nextPageSplitMark + (pageNo));
    		}
    		url = _url;
    	};
	}
	
	/**
	 * 分析页面商品列表，并将数据存放到crawler里。
	 * @param url
	 * @return 返回下一页的url
	 * @throws Exception
	 */
	protected String analyse(String url) throws Exception{
		System.out.println(url);
    	HtmlCleaner cleaner = new HtmlCleaner();  
    	PageFetchResult result = pageFetcher(url);
    	InputStream in = result.getEntity().getContent();
    	//charset
    	String charset = crawler.getCharset();
    	if (charset == null) {
    		charset = result.getCharset();
    	}
    	String source = new String(EntityUtils.toByteArray(in), charset);
    	
        TagNode node = null;
       	node = cleaner.clean(source);
        if(node == null)return null;
        //按tag取.  
        Object[] ns = null;
        //按xpath取  
        ns = node.evaluateXPath(crawler.getListXPath());
        if(ns.length < 1){
        	completed = true;
        	return null;
        }
        System.out.println(ns.length);
        for(Object on : ns) { 
        	Product product = new Product();
            TagNode n = (TagNode) on;
            Object[] _ns = n.evaluateXPath(crawler.getUrlXPath());
            TagNode link = (TagNode)_ns[0];
            //title
            String title = link.getText().toString();
            title = StringEscapeUtils.unescapeXml(title);
            title = title.replaceAll("[\r\t\n]", "");
        	title = title.trim();
        	product.setProductTitle(title);
        	//url
        	String productUrl = link.getAttributeByName("href");
            product.setProductUrl(productUrl);
            //sid
            String productSidStr = null;
            Pattern pattern = Pattern.compile(crawler.getProductSidRegex());
			Matcher matcher = pattern.matcher(productUrl);
			if(matcher.find()){
				productSidStr = matcher.group(matcher.groupCount());
				if (crawler.isFilter())
					product.setProductUrl(matcher.group(0));
			}
            product.setProductSid(StringUtils.hashCode(productSidStr));
            //price
            if(crawler.getImgXPath() != null){
            	_ns = n.evaluateXPath(crawler.getImgXPath());
	            TagNode img = (TagNode)_ns[0];
	            String imgUrl = img.getAttributeByName("src");
	            product.setProductPrice(PriceUtils.getPriceForUrl(imgUrl));
            } else if(crawler.getPriceXPath() != null){
            	_ns = n.evaluateXPath(crawler.getPriceXPath());
            	if(_ns.length > 0){
            		TagNode price = (TagNode)_ns[0];
            		product.setProductPrice(PriceUtils.getPrice(price.getText().toString()));
            	}
            }
            crawler.addProduct(product);
        }
        //nextPageUrl
        if(crawler.getNextPath() != null){
        	ns = node.evaluateXPath(crawler.getNextPath());
	        if(ns != null && ns.length > 0){
	        	TagNode next = (TagNode)ns[0];
	        	return next.getAttributeByName("href");
	        }
        }
        in.close();
        return null;
	}
	
	protected PageFetchResult pageFetcher(String url) throws IllegalStateException, IOException{
    	WebURL webUrl = new WebURL();
    	webUrl.setURL(url);
    	PageFetchResult result = pageFetcher.fetchHeader(webUrl);
		return result;
    }
}
