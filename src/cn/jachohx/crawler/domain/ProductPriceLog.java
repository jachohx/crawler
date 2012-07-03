package cn.jachohx.crawler.domain;
import java.util.Date;

public class ProductPriceLog{
	private long logId;
	private long productId;
	private double productPrice;
	private Date productUpdateAt;
	
	public ProductPriceLog() {}
	public ProductPriceLog(Product product) {
		this.productId = product.getProductId();
		this.productPrice = product.getProductPrice();
	}
	
	public void setLogId(long logId){
		this.logId = logId;
	}
	public long getLogId(){
		return this.logId;
	}
	public void setProductId(long productId){
		this.productId = productId;
	}
	public long getProductId(){
		return this.productId;
	}
	public void setProductPrice(double productPrice){
		this.productPrice = productPrice;
	}
	public double getProductPrice(){
		return this.productPrice;
	}
	public void setProductUpdateAt(Date productUpdateAt){
		this.productUpdateAt = productUpdateAt;
	}
	public Date getProductUpdateAt(){
		return this.productUpdateAt;
	}
}