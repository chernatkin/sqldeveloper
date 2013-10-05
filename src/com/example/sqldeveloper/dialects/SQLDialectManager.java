package com.example.sqldeveloper.dialects;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SQLDialectManager {
	
	private static final List<SQLDialect> DIALECTS;
	
	static{
		try{
			final Class<?> jdbcClass = Class.forName("org.hsqldb.jdbc.JDBCDriver");
		}catch(ClassNotFoundException cle){
			throw new IllegalStateException("Driver not found");
		}
		
		final List<SQLDialect> tmp = new ArrayList<SQLDialect>(8);
		tmp.add(new PostgresDialect());
		tmp.add(new OracleDialect());
		
		DIALECTS = Collections.unmodifiableList(tmp);
	}
	
	public static List<SQLDialect> getAllDialects(){
		return DIALECTS;
	}
	
	public static SQLDialect getDialect(final int titleId){
		for(SQLDialect dialect : DIALECTS){
			if(dialect.getTitleId() == titleId){ return dialect; }
		}
		
		return null;
	}
	
	public static <T> T execute(final SQLDialect dialect, final StatementBuilder psBuilder, final ResultSetTransformer<T> transformer) throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try{
			conn = DriverManager.getConnection("jdbc:hsqldb:file:" + dialect.getDbName(), dialect.getProps());
			ps = psBuilder.prepareStatement(conn);
			final boolean hasResultSet =  ps.execute();
			resultSet = ps.getResultSet();
			
			if(transformer == null){ return null; }
			return transformer.transformResultSet(resultSet, hasResultSet);
		}catch(SQLException sqle){
			sqle.printStackTrace();
			throw sqle;
		}
		finally{
			try{
				if(resultSet != null && !resultSet.isClosed()){
					resultSet.close();
				}
				if(ps != null && !ps.isClosed()){
					ps.close();
				}
				if(conn != null && !conn.isClosed()){
					conn.close();
				}
			}catch(Throwable th){}
		}
	}
	
	public static <T> T execute(final SQLDialect dialect, final String schema, final ExecutionBuilder<T> execBuilder) throws SQLException{
		
		Connection conn = null;
		try{
			conn = DriverManager.getConnection("jdbc:hsqldb:file:" + dialect.getDbName(), dialect.getProps());
			conn.createStatement().execute("SET INITIAL SCHEMA " + schema + ';');
			return execBuilder.excecute(conn);
		}catch(SQLException sqle){
			sqle.printStackTrace();
			throw sqle;
		}
		finally{
			try{
				if(conn != null && !conn.isClosed()){
					conn.close();
				}
			}catch(Throwable th){}
		}
		
	}
		
}
