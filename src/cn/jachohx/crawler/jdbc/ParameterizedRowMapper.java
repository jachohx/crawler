package cn.jachohx.crawler.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract interface ParameterizedRowMapper<T> extends RowMapper
{
  public abstract T mapRow(ResultSet paramResultSet, int paramInt)
    throws SQLException;
}
