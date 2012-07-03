package cn.jachohx.crawler.service;

import cn.jachohx.crawler.domain.*;
import cn.jachohx.crawler.jdbc.SimpleJdbcTemplate;
import cn.jachohx.crawler.repository.*;

import java.util.*;

public class ProductPriceLogService{
	
	protected static ProductPriceLogService productPriceLogService;
	
	public static ProductPriceLogService getInstance() {
		if (productPriceLogService == null) {
			productPriceLogService = new ProductPriceLogService();
			ProductPriceLogRepository repository = new ProductPriceLogRepository();
			repository.setSimpleJdbcTemplate(SimpleJdbcTemplate.getInstance());
			productPriceLogService.setRepository(repository);
		}
		return productPriceLogService;
	}
    ProductPriceLogRepository repository;
    public void setRepository(ProductPriceLogRepository repository) {
        this.repository = repository;
    }
    
    public ProductPriceLog find(long id) {
        return repository.find(id);
    }
    
    public List<ProductPriceLog> listAll() {
        return repository.listAll();
    }
    
    public Pager<ProductPriceLog> pager(int pageNo, int pageSize, String orderBy) {
        return repository.pager(pageNo, pageSize, orderBy);
    }
    
    public void create(ProductPriceLog productPriceLog) {
        repository.create(productPriceLog);
    }
    
    public void create(Product product) {
        repository.create(new ProductPriceLog(product));
    }
    
    public void update(ProductPriceLog productPriceLog) {
        repository.update(productPriceLog);
    }
    
    public void delete(long id) {
        repository.delete(id);
    }
    
    public void delete(ProductPriceLog productPriceLog) {
        repository.delete(productPriceLog);
    }
}