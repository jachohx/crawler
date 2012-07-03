package cn.jachohx.crawler.domain;

import java.util.ArrayList;
import java.util.List;

public class Crawler {
	private List<String> initUrl = null;
	private String listXPath = null;
	private String urlXPath = null;
	private String imgXPath = null;
	private String priceXPath = null;
	
	private String nextPath = null;
	 
	private boolean nextPageReplace = false;
	private String nextPageSplitMark = "=";
	private int firstPageNo = 0;
	private String pageStr = null;
	private List<Product> products = null;
	private String productSidRegex = null;
	
	private String market = null;
	private String type = null;
	private String charset = null;
	
	//ÊÇ·ñ¹ýÂËurl
	private boolean filter = false;
	
	public List<String> getInitUrl() {
		return initUrl;
	}
	public void setInitUrl(List<String> initUrl) {
		this.initUrl = initUrl;
	}
	public void addInitUrl(String url) {
		if (initUrl == null)
			initUrl = new ArrayList<String>();
		initUrl.add(url);
	}
	public String getListXPath() {
		return listXPath;
	}
	public void setListXPath(String listXPath) {
		this.listXPath = listXPath;
	}
	public String getUrlXPath() {
		return urlXPath;
	}
	public void setUrlXPath(String urlXPath) {
		this.urlXPath = urlXPath;
	}
	public String getImgXPath() {
		return imgXPath;
	}
	public void setImgXPath(String imgXPath) {
		this.imgXPath = imgXPath;
	}
	public String getPriceXPath() {
		return priceXPath;
	}
	public void setPriceXPath(String priceXPath) {
		this.priceXPath = priceXPath;
	}
	public String getNextPath() {
		return nextPath;
	}
	public void setNextPath(String nextPath) {
		this.nextPath = nextPath;
	}
	public boolean isNextPageReplace() {
		return nextPageReplace;
	}
	public void setNextPageReplace(boolean nextPageReplace) {
		this.nextPageReplace = nextPageReplace;
	}
	public String getNextPageSplitMark() {
		return nextPageSplitMark;
	}
	public void setNextPageSplitMark(String nextPageSplitMark) {
		this.nextPageSplitMark = nextPageSplitMark;
	}
	public int getFirstPageNo() {
		return firstPageNo;
	}
	public void setFirstPageNo(int firstPageNo) {
		this.firstPageNo = firstPageNo;
	}
	public String getPageStr() {
		return pageStr;
	}
	public void setPageStr(String pageStr) {
		this.pageStr = pageStr;
	}
	public List<Product> getProducts() {
		return products;
	}
	public void setProducts(List<Product> products) {
		this.products = products;
	}
	public void addProduct(Product product) {
		if (products == null)
			products = new ArrayList<Product>();
		this.products.add(product);
	}
	public void addProducts(List<Product> products) {
		if (products == null)
			products = new ArrayList<Product>();
		this.products.addAll(products);
	}
	public int getTotal() {
		if (products != null)
			return products.size();
		return 0;
	}
	public String getProductSidRegex() {
		return productSidRegex;
	}
	public void setProductSidRegex(String productSidRegex) {
		this.productSidRegex = productSidRegex;
	}
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isFilter() {
		return filter;
	}
	public void setFilter(boolean filter) {
		this.filter = filter;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
}
