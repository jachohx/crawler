package cn.jachohx.crawler.config;

import cn.jachohx.crawler.domain.Crawler;

public class MarketConfig {
	/**
     * @param type 1为京东，3为亚马逊，6为22shop
     */
    public static Crawler getCrawler(int type){
    	Crawler crawler = new Crawler();
    	switch(type){
    		case 1 :
    			crawler.addInitUrl("http://www.360buy.com/products/670-671-672.html");
    			crawler.setListXPath("//div[@id='plist']/ul/li");
    			crawler.setUrlXPath("/div[2]/a");
    		    crawler.setImgXPath("/div[3]/img");
    		    crawler.setNextPath("//div/div/a[@class='next']");
    			break;
    		case 2 :
    			crawler.addInitUrl("http://www.coo8.com/products/600-0-0-0-0.html");
    			crawler.setListXPath("//div[@class='srchContent']/ul/li");
    		    crawler.setUrlXPath("/p[@class='name']/a");
    		    crawler.setImgXPath("/p[@class='price']/img");
    		    crawler.setNextPath("//div/p/a[@class='pageNext']");
    			break;
    		case 3 :
    			crawler.addInitUrl("http://www.amazon.cn/s/ref=sv_pc_2?ie=UTF8&rh=n%3A888483051&page=1");
//    			http://www.amazon.cn/s/ref=sr_abn_pp?ie=UTF8&bbn=665002051&rh=n%3A665002051
    			crawler.setListXPath("//div[@class='data']");
    			crawler.setUrlXPath("/h3/a");
    			crawler.setPriceXPath("/div/span");
//    			nextPath("//div/div/a[@class='next']");
    			crawler.setNextPageReplace(true);
    			crawler.setFirstPageNo(1);
    			crawler.setPageStr("page");
    			break;
    		case 4 :
    			crawler.addInitUrl("http://www.suning.com/emall/thirdSearchNewCmd?storeId=10052&catalogId=10051&categoryId=258004&currentPage=0");
    			crawler.setListXPath("//div[@id='proShow']/ul/li");
    			crawler.setUrlXPath("/div/span/a");
    			crawler.setImgXPath("/div/p[@class='price']/img");
    			crawler.setNextPageReplace(true);
    			crawler.setFirstPageNo(0);
    			crawler.setPageStr("currentPage");
    			break;
    		case 6:
    			crawler.addInitUrl("http://www.22shop.com/select-0-0-0-0-0-0-0-1-0-0-0-0-D.html");
    			crawler.setListXPath("//div[@class='mobile_tu']/div[@class='mobile_product']");
    			crawler.setUrlXPath("/span[@class='mobile_wen']/a");
    			crawler.setPriceXPath("/span[@class='mobile_wen']/strong");
    			crawler.setNextPageReplace(true);
    			crawler.setNextPageSplitMark("");
    			crawler.setFirstPageNo(1);
    			crawler.setPageStr("select-0-0-0-0-0-0-0-");
    			crawler.setProductSidRegex("http://www.22shop.com/(.*).html");
    			crawler.setMarket("22shop");
    			crawler.setType("mobile");
    			break;
    			
    	}
    	return crawler;
    }
}
