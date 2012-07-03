package cn.jachohx.crawler.repository;

import cn.jachohx.crawler.domain.*;
import cn.jachohx.crawler.jdbc.DataAccessException;
import cn.jachohx.crawler.jdbc.ParameterizedRowMapper;
import cn.jachohx.crawler.jdbc.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;

public class ProductPriceLogRepository {
	public final static String TABLE_NAME = "crawler_product_price_log";
	SimpleJdbcTemplate simpleJdbcTemplate;

	static enum Field {
		log_id, 
		product_id, 
		product_price, 
		product_update_at
	}

	public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
		this.simpleJdbcTemplate = simpleJdbcTemplate;
	}
	static ParameterizedRowMapper<ProductPriceLog> productPriceLogRowMapper = new ParameterizedRowMapper<ProductPriceLog> () {
        @Override
		public ProductPriceLog mapRow(ResultSet rs, int index) throws SQLException {
			ProductPriceLog productPriceLog = new ProductPriceLog();
			productPriceLog.setLogId(rs.getLong(Field.log_id.name()));
			productPriceLog.setProductId(rs.getLong(Field.product_id.name()));
			productPriceLog.setProductPrice(rs.getDouble(Field.product_price.name()));
			productPriceLog.setProductUpdateAt(rs.getTimestamp(Field.product_update_at.name()));
			return productPriceLog;
		}
	};

	public ProductPriceLog find(long id) {
		ProductPriceLog productPriceLog = null;
		try {
			productPriceLog = simpleJdbcTemplate.queryForObject("select * from " + TABLE_NAME + " where log_id = ?", 
					productPriceLogRowMapper, id);
		} catch (DataAccessException e) {
		} 
		return productPriceLog;
	}

	public List<ProductPriceLog> listAll() {
		List<ProductPriceLog> result = null;
		String sql = "select * from " + TABLE_NAME;
		try {
			result = simpleJdbcTemplate.query(sql, productPriceLogRowMapper);
		} catch (Exception e) {
		}
		return result;
	}

	public Pager<ProductPriceLog> pager(int pageNo, int pageSize, String orderBy) {
		List<ProductPriceLog> result = null;
		if (pageNo < 1 || pageSize < 0)
			return null;
		String sqlCount = "select count(*) from " + TABLE_NAME;
		int count = simpleJdbcTemplate.queryForInt(sqlCount);
		if (count <= 0)
			return null;
		String sql = "select * from " + TABLE_NAME + ((orderBy == null || "".equals(orderBy)) ? " " : 
			(" order by " + orderBy)) + " limit " + (pageNo - 1) * pageSize + " ," + pageSize;
		try {
			result = simpleJdbcTemplate.query(sql, productPriceLogRowMapper);
		} catch (Exception e) {
		}
		Pager<ProductPriceLog> pager = new Pager<ProductPriceLog>();
		pager.setPageNo(pageNo);
		pager.setPageSize(pageSize);
		pager.setTotal(count);
		pager.setResultList(result);
		return pager;
	}

	public void create(ProductPriceLog productPriceLog) {
		simpleJdbcTemplate.update("insert into " + TABLE_NAME + "(" + 
				"product_id," + 
				"product_price," + 
				"product_update_at" + 
				") values(?,?,now())",
				productPriceLog.getProductId(), 
				productPriceLog.getProductPrice());
	}

	public void update(ProductPriceLog productPriceLog) {
		simpleJdbcTemplate.update("update " + TABLE_NAME + " set " + 
				"product_id = ?," + 
				"product_price = ?," + 
				"product_update_at = now() " + 
				"where log_id = ?",
				productPriceLog.getProductId(), 
				productPriceLog.getProductPrice(), 
				productPriceLog.getLogId());

	}

	public void delete(long log_id) {
		simpleJdbcTemplate.update("delete from " + TABLE_NAME + " where log_id = ?", log_id);
	}

	public void delete(ProductPriceLog productPriceLog) {
		delete(productPriceLog.getLogId());
	}

}