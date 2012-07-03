package cn.jachohx.crawler.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

public class IdTableGenerator {
	DataSource idGenDataSource;
	Connection con;
	
	public void setIdDataSource(DataSource idGenDataSource) {
		this.idGenDataSource = idGenDataSource;
	}
	
	public void setIdConnection(Connection con) {
		this.con = con;
	}
	
	static int size = 10;
	static long start_id = 0;
	
	Map<String, IdHolder> holderMap = new java.util.concurrent.ConcurrentHashMap<String, IdHolder>();
	
	public long generate(String tableName, String columnName) {
		IdHolder holder = holderMap.get(tableName);
		if (holder == null) {
			holder = new IdHolder();
			holderMap.put(tableName, holder);
		}
		synchronized (holder) {
			if (holder.needAlloc()) {
				long lastUsedId = alloc(tableName, columnName);
				holder.currentId = lastUsedId + 1;
				holder.limit = lastUsedId + size;
			} else {
				holder.currentId ++;
			}
			return holder.currentId;
		}
	}
	
	static class IdHolder {
		long currentId;
		long limit;
		boolean needAlloc() {return currentId >= limit; }
	}
	
	public long alloc(String tableName, String columnName) {
		long result = 0;
		boolean oldAutoCommit = false;
		try {
			if (con == null) con = idGenDataSource.getConnection();
			oldAutoCommit = con.getAutoCommit();
			con.setAutoCommit(false);
			result = getLastUsedId(con, tableName);
			if(result == 0) {
				result = initIdTable(con, tableName, columnName);
			}
			updateLastUsedId(con, tableName, columnName);
			
			/*if (updateCount == 0) {
				initIdTable(con, tableName, columnName);
//				updateLastUsedId(con, tableName, columnName);
			}*/
			
			
			con.commit();
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			throw new RuntimeException(e);
		} finally {
			if (con != null) {
				try {
					con.setAutoCommit(oldAutoCommit);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		
		return result;
	}

	static long getLastUsedId(Connection con, String tableName) throws SQLException {
		PreparedStatement ps = con.prepareStatement("select last_used_id from crawler_keygen where table_name = ?");
		ps.setString(1, tableName);
		ResultSet rs = ps.executeQuery();
		long result = 0;
		if (rs.next()) {
			result = rs.getLong(1);
		}
		rs.close();
		ps.close();
		return result;
	}
	
	static int updateLastUsedId(Connection con, String tableName, String columnName) throws SQLException  {
		PreparedStatement ps = con.prepareStatement("update crawler_keygen set last_used_id = last_used_id + ?" +
		" where table_name = ?");

		ps.setInt(1, size);
		ps.setString(2, tableName);
		
		int result = ps.executeUpdate();
		ps.close();
		return result;
	}
	
	static long initIdTable(Connection con, String tableName, String columnName) throws SQLException {
		/*PreparedStatement ps = con.prepareStatement("select max(" + columnName + ") from " + tableName);
		ResultSet rs = ps.executeQuery();
		rs.next();
		long maxId = rs.getLong(1);
		rs.close();
		ps.close();
		*/
		PreparedStatement ps = con.prepareStatement("insert into crawler_keygen (table_name, last_used_id) values (?, ?)");
		ps.setString(1, tableName);
		ps.setLong(2, start_id);
		ps.executeUpdate();
		ps.close();
		return start_id;
	}
	
}
