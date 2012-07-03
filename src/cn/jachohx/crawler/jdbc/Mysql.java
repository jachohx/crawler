package cn.jachohx.crawler.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author huangxiang
 *
 */
public class Mysql {
	private String url;
	private String user = "root";
	private String password = "";
	private Connection con;
	private void init() {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		} catch (java.lang.ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
		}
		try {
			con = DriverManager.getConnection(url,user ,password); // /”√ªß√˚£¨√‹¬Î
		} catch (SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}
	}
	
	public Mysql(){
	}
	
	public Mysql(String url){
		this.url = url;
		init();
	}
	
	public Mysql(String url,String password){
		this.url = url;
		this.password = password;
		init();
	}
	
	public Mysql(String url,String user,String password){
		this.url = url;
		this.user = user;
		this.password = password;
		init();
	}
	
	/**
	 * must init
	 * @return Connection
	 */
	public Connection getConnection(){
		return this.con;
	}
	
	/**
	 * contain init
	 * @param url
	 * @param root
	 * @param password
	 * @return
	 */
	public Connection getConnection(String url,String root,String password){
		this.url = url;
		this.user = root;
		this.password = password;
		init();
		return this.con;
	}
	
	/**
	 * close connection
	 */
	public void closeConnection(){
		try {
			con.close();
		} catch (SQLException e) {
			System.err.println("SQLException: " + e.getMessage());
		}
	}
}
