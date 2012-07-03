package cn.jachohx.crawler.domain;
import java.util.Date;

public class Product{
	private long productId;
	private long productSid;
	private String productMarket;
	private String productType;
	private String productTitle;
	private String productUrl;
	private double productPrice;
	private Date productCreateAt;
	private Date productUpdateAt;
	
	public void setProductId(long productId){
		this.productId = productId;
	}
	public long getProductId(){
		return this.productId;
	}
	public void setProductSid(long productSid){
		this.productSid = productSid;
	}
	public long getProductSid(){
		return this.productSid;
	}
	public void setProductMarket(String productMarket){
		this.productMarket = productMarket;
	}
	public String getProductMarket(){
		return this.productMarket;
	}
	public void setProductType(String productType){
		this.productType = productType;
	}
	public String getProductType(){
		return this.productType;
	}
	public void setProductTitle(String productTitle){
		this.productTitle = productTitle;
	}
	public String getProductTitle(){
		return this.productTitle;
	}
	public void setProductUrl(String productUrl){
		this.productUrl = productUrl;
	}
	public String getProductUrl(){
		return this.productUrl;
	}
	public void setProductPrice(double productPrice){
		this.productPrice = productPrice;
	}
	public void setProductPrice(String productPrice){
		if (productPrice != null && !productPrice.isEmpty())
			this.productPrice = Double.parseDouble(productPrice);
	}
	public double getProductPrice(){
		return this.productPrice;
	}
	public void setProductCreateAt(Date productCreateAt){
		this.productCreateAt = productCreateAt;
	}
	public Date getProductCreateAt(){
		return this.productCreateAt;
	}
	public void setProductUpdateAt(Date productUpdateAt){
		this.productUpdateAt = productUpdateAt;
	}
	public Date getProductUpdateAt(){
		return this.productUpdateAt;
	}
	
	public String toString(){
		return productPrice + "\t" + productSid + "\t" + productTitle + "\t" + productUrl;
	}
}