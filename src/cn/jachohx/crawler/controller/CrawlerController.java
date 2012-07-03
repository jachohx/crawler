package cn.jachohx.crawler.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.jachohx.crawler.domain.Crawler;
import cn.jachohx.crawler.domain.Product;
import cn.jachohx.crawler.fetcher.CrawlerFetcher;
import cn.jachohx.crawler.jdbc.SimpleJdbcTemplate;
import cn.jachohx.crawler.service.ProductService;
import cn.jachohx.crawler.xml.CrawlerFactory;

public class CrawlerController {
	Logger log = Logger.getLogger(CrawlerController.class);
	
	public void start() {
//		Crawler crawler = MarketConfig.getCrawler(6);
		CrawlerFactory crawlerFactory = CrawlerFactory.getInstance();
		List<Crawler> crawlers = crawlerFactory.getCrawler();
		if (crawlers != null && crawlers.size() > 0) {
			for (Crawler crawler : crawlers) {
				CrawlerFetcher fetcher = new CrawlerFetcher(crawler);
				fetcher.fetcher();
				log("--------------------- " + crawler.getMarket() + " - " + crawler.getType() + " ---------------------");
				analyse(crawler);
				log("------------------------------------------");
			}
		}
	}
	
	/**
	 * ������ȡ�õ�����Ʒ
	 * @param crawler
	 */
	protected void analyse(Crawler crawler) {
		List<Product> products = crawler.getProducts();
		if(products == null || products.size() == 0)return;
		Map<Long, Product> dbProductsMap =  getDBProducts(crawler);
		if (dbProductsMap != null) {
			ProductService productService = ProductService.getInstance();
			String market = crawler.getMarket();
			String type = crawler.getType();
			for (Product product : products) {
				Product dbProduct = dbProductsMap.get(product.getProductSid());
				//������Ʒ
				if (dbProduct == null) {
					product.setProductMarket(market);
					product.setProductType(type);
					productService.create(product);
					log("add:\t" + product);
					continue;
				}
				//���¼۸�
				if (dbProduct.getProductPrice() != product.getProductPrice()) {
					product.setProductId(dbProduct.getProductId());
					double price = product.getProductPrice() - dbProduct.getProductPrice();
					log("update:\t" + (price > 0 ? "+" + price + "\tU" : price + "\tD") + "\t" + product.getProductPrice() + "\t" + dbProduct.getProductPrice()
							+ "\t" + product.getProductTitle() + "\t" + product.getProductUrl());
//					log.info("update:\t" + product);
//					log.info("old:\t" + dbProduct);
					productService.updatePrice(product);
				}
				//���±���
				if (!dbProduct.getProductTitle().equals(product.getProductTitle())) {
					product.setProductId(dbProduct.getProductId());
					log("title:\t" + product);
					productService.updateTitle(product);
				}
			}
		}
		SimpleJdbcTemplate simpleJdbcTemplate = SimpleJdbcTemplate.getInstance();
		Connection con = simpleJdbcTemplate.getConnection();;
		try {
			if (!con.getAutoCommit()) {
				log("commit");
				con.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �����ݿ���õ���ǰ�����Ӧ�����ݣ�����sidΪ��������Map
	 * @param crawler
	 * @return
	 */
	protected Map<Long, Product> getDBProducts(Crawler crawler) {
		Map<Long, Product> result = new HashMap<Long, Product>(1);;
		ProductService productService = ProductService.getInstance();
		List<Product> dbProducts = productService.list(crawler.getMarket(), crawler.getType());
		if (dbProducts != null && dbProducts.size() > 0) {
			result = new HashMap<Long, Product>(dbProducts.size());
			for (Product product : dbProducts) {
				result.put(product.getProductSid(), product);
			}
		}
		return result;
	}
	
	private void log(String log){
		this.log.info(log);
		System.out.println(log);
	}
}