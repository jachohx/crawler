package cn.jachohx.crawler.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import cn.jachohx.crawler.util.PropertiesUtils;

public class SimpleJdbcTemplate {
	public final String DEFAULT_CONFIGURATION_FILE = "database.properties";
	static SimpleJdbcTemplate simpleJdbcTemplate;
	private Connection con;
	public static SimpleJdbcTemplate getInstance( ){
		if (simpleJdbcTemplate == null) {
			simpleJdbcTemplate = new SimpleJdbcTemplate();
			simpleJdbcTemplate.init();
		}
		return simpleJdbcTemplate;
	}
	
	private void init() {
		Properties config = PropertiesUtils.getProterties("database.properties");
		String url = config.getProperty("mysql.url");
		String user = config.getProperty("mysql.user");
		String password = config.getProperty("mysql.password");
		Mysql mysql = new Mysql(url, user, password);
		con = mysql.getConnection();
		try {
			con.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void shutdown() {
		try {
			if (!con.getAutoCommit()) 
				con.commit();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() {
		return con;
	}
	
	protected PreparedStatement getPreparedStatement(String sql, Object... args) throws SQLException {
		PreparedStatement ps = con.prepareStatement(sql);
		if (args != null && args.length > 0) {
			ArgPreparedStatementSetter argsPreparedStatementSetter = new ArgPreparedStatementSetter(args);
			argsPreparedStatementSetter.setValues(ps);
		}
		return ps;
	}
	
	protected ResultSet getResultSet(String sql, Object... args) throws SQLException {
		PreparedStatement ps = getPreparedStatement(sql, args);
		ResultSet rs = ps.executeQuery();
		return rs;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) 
		throws DataAccessException {
		ResultSet rs = null;
		try {
			rs = getResultSet(sql, args);
			while(rs.next()){
				return (T) rs.getObject(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new DataAccessException();
	}
	
	public <T> T queryForObject(String sql, ParameterizedRowMapper<T> rm, Object... args) 
			throws DataAccessException{
		ResultSet rs = null;
		try {
			rs = getResultSet(sql, args);
			while(rs.next()){
				return (T) rm.mapRow(rs, rs.getRow());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		throw new DataAccessException();
	}
	
	public int queryForInt(String sql, Object... args) {
		int result = 0;
		try {
			result = queryForObject(sql, int.class, args);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public <T> List<T> query(String sql, ParameterizedRowMapper<T> rm, Object... args) 
			throws DataAccessException {
		ResultSet rs = null;
		List<T> result = new ArrayList<T>();
		try {
			rs = getResultSet(sql, args);
			while(rs.next()){
				result.add(rm.mapRow(rs, rs.getRow()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(result.size() > 0)
			return result;
		throw new DataAccessException();
	}
	
	public int update(String sql, Object... args) {
		PreparedStatement ps = null;
		int count = 0;
		try {
			ps = getPreparedStatement(sql, args);
			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
}
