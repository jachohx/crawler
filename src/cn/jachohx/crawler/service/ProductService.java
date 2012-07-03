package cn.jachohx.crawler.service;

import cn.jachohx.crawler.domain.*;
import cn.jachohx.crawler.jdbc.SimpleJdbcTemplate;
import cn.jachohx.crawler.repository.*;
import java.util.*;

public class ProductService{
	protected static ProductService productService;
	
	public static ProductService getInstance() {
		if (productService == null) {
			productService = new ProductService();
			ProductRepository repository = new ProductRepository();
			repository.setSimpleJdbcTemplate(SimpleJdbcTemplate.getInstance());
			productService.setRepository(repository);
			/*ProductPriceLogRepository priceLogRepository = new ProductPriceLogRepository();
			priceLogRepository.setSimpleJdbcTemplate(SimpleJdbcTemplate.getInstance());
			productService.setPriceLogRepository(priceLogRepository);*/
		}
		return productService;
	}
    ProductRepository repository;
    //ProductPriceLogRepository priceLogRepository;
    public void setRepository(ProductRepository repository) {
        this.repository = repository;
    }
    
    /*public void setPriceLogRepository(ProductPriceLogRepository priceLogRepository) {
		this.priceLogRepository = priceLogRepository;
	}*/

	public Product find(long id) {
        return repository.find(id);
    }
    
    public List<Product> listAll() {
        return repository.listAll();
    }
    
    public List<Product> list(String market, String type) {
    	return repository.list(market, type);
    }
    
    public Pager<Product> pager(int pageNo, int pageSize, String orderBy) {
        return repository.pager(pageNo, pageSize, orderBy);
    }
    
    public void create(Product product) {
        repository.create(product);
        ProductPriceLogService.getInstance().create(product);
    }
    
    public void update(Product product) {
        int count = repository.update(product);
        if(count > 0) {
        	ProductPriceLogService.getInstance().create(product);
//        	System.out.println("update count :" + count);
        }
    }
    public void updatePrice(Product product) {
    	int count = repository.updatePrice(product);
    	if(count > 0) {
    		ProductPriceLogService.getInstance().create(product);
//    		System.out.println("update count :" + count);
    	}
    }
    
    public void updateTitle(Product product) {
    	repository.updateTitle(product);
    }
    
    public void delete(long id) {
        repository.delete(id);
    }
    
    public void delete(Product product) {
        repository.delete(product);
    }
}