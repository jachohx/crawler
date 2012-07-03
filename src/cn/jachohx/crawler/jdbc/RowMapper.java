package cn.jachohx.crawler.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract interface RowMapper {
	public abstract Object mapRow(ResultSet paramResultSet, int paramInt) throws SQLException;
}