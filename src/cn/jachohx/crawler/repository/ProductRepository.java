package cn.jachohx.crawler.repository;

import cn.jachohx.crawler.domain.*;
import cn.jachohx.crawler.jdbc.DataAccessException;
import cn.jachohx.crawler.jdbc.ParameterizedRowMapper;
import cn.jachohx.crawler.jdbc.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;

public class ProductRepository{
	public final static String TABLE_NAME = "crawler_product";
	SimpleJdbcTemplate simpleJdbcTemplate;
	IdTableGenerator idGenerator;
	static enum Field {
		product_id,
		product_sid,
		product_market,
		product_type,
		product_title,
		product_url,
		product_price,
		product_create_at,
		product_update_at	
	}
	
	public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
		this.simpleJdbcTemplate = simpleJdbcTemplate;
		this.idGenerator = new IdTableGenerator();
		this.idGenerator.setIdConnection(simpleJdbcTemplate.getConnection());
	}
	
	static ParameterizedRowMapper<Product> productRowMapper = new ParameterizedRowMapper<Product> () {
        @Override
        public Product mapRow(ResultSet rs, int index) throws SQLException {
        		Product product = new Product();
                product.setProductId(rs.getLong(Field.product_id.name()));
                product.setProductSid(rs.getLong(Field.product_sid.name()));
                product.setProductMarket(rs.getString(Field.product_market.name()));
                product.setProductType(rs.getString(Field.product_type.name()));
                product.setProductTitle(rs.getString(Field.product_title.name()));
                product.setProductUrl(rs.getString(Field.product_url.name()));
                product.setProductPrice(rs.getDouble(Field.product_price.name()));
                product.setProductCreateAt(rs.getTimestamp(Field.product_create_at.name()));
                product.setProductUpdateAt(rs.getTimestamp(Field.product_update_at.name()));
                return product;
        }
    };

    public Product find(long id) {
        Product product = null;
        try {
            product = simpleJdbcTemplate.queryForObject("select * from " + TABLE_NAME + 
            		" where product_id = ?", productRowMapper, id);
        } catch(DataAccessException e){
        }
        return product;
    }

    public List<Product> listAll(){
        List<Product> result = null;
        String sql = "select * from " + TABLE_NAME;
        try{
            result = simpleJdbcTemplate.query(sql, productRowMapper);
        }catch (Exception e) {}
        return result;
    }
    
    public List<Product> list(String market, String type) {
    	if ((market == null || market.isEmpty()) && (type == null || type.isEmpty())) 
    		return listAll();
    	List<Product> result = null;
        String sql = "select * from " + TABLE_NAME + " where ";
        if(market != null) 
        	sql += " product_market = '" + market + "'" + (type != null ? " and " : "");
        if (type != null) 
        	sql += " product_type = '" + type + "'";
        try{
            result = simpleJdbcTemplate.query(sql, productRowMapper);
        }catch (Exception e) {}
        return result;
    }
    
    public Pager<Product> pager(int pageNo, int pageSize, String orderBy){
        List<Product> result = null;
        if(pageNo < 1 || pageSize < 0)return null;
        String sqlCount = "select count(*) from " + TABLE_NAME;
        int count  = simpleJdbcTemplate.queryForInt(sqlCount);
        if(count <= 0)return null;
        String sql = "select * from " + TABLE_NAME + 
            ((orderBy == null || "".equals(orderBy)) ? " " : (" order by " + orderBy )) + 
            	" limit " + (pageNo - 1) * pageSize +" ," + pageSize;
        try{
            result = simpleJdbcTemplate.query(sql, productRowMapper);
        }catch (Exception e) {}
        Pager<Product> pager = new Pager<Product>();
        pager.setPageNo(pageNo);
        pager.setPageSize(pageSize);
        pager.setTotal(count);
        pager.setResultList(result);
        return pager;
    }

    public void create(Product product){
    	long id = idGenerator.generate(TABLE_NAME, "id");
    	product.setProductId(id);
    	simpleJdbcTemplate.update("insert into " + TABLE_NAME + "("+
    		"product_id,"+
            "product_sid,"+	
            "product_market,"+	
            "product_type,"+	
            "product_title,"+	
            "product_url,"+	
            "product_price,"+	
            "product_create_at,"+	
            "product_update_at"+
			") values(?,?,?,?,?,?,?,now(),now())",
			product.getProductId(),
            product.getProductSid(),
            product.getProductMarket(),
            product.getProductType(),
            product.getProductTitle(),
            product.getProductUrl(),
            product.getProductPrice()
		); 	
    }
    
    public int update(Product product){
    	return simpleJdbcTemplate.update("update " + TABLE_NAME + " set "+
				"product_sid = ?,"+	
				"product_market = ?,"+	
				"product_type = ?,"+	
				"product_title = ?,"+	
				"product_url = ?,"+	
				"product_price = ?,"+	
				"product_update_at = now() "+
			"where product_id = ? and product_price != ?",
			product.getProductSid(),
			product.getProductMarket(),
			product.getProductType(),
			product.getProductTitle(),
			product.getProductUrl(),
			product.getProductPrice(),
			product.getProductId(),
			product.getProductPrice()
		); 
    }
    
    public int updatePrice(Product product){
    	return simpleJdbcTemplate.update("update " + TABLE_NAME + " set "+
    			"product_price = ?,"+	
    			"product_update_at = now() "+
    			"where product_id = ? and product_price != ?",
    			product.getProductPrice(),
    			product.getProductId(),
    			product.getProductPrice()
    			); 
    }
    
    public int updateTitle(Product product){
    	return simpleJdbcTemplate.update("update " + TABLE_NAME + " set "+
				"product_title = ?"+	
			"where product_id = ? ",
			product.getProductTitle(),
			product.getProductId()
		); 
    }
    
    public void delete(long product_id){
        simpleJdbcTemplate.update("delete from " + TABLE_NAME + " where product_id = ?", product_id);
    }
    
    public void delete(Product product){
        delete(product.getProductId());
    }

}