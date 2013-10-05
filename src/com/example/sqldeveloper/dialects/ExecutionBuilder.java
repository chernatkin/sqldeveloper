package com.example.sqldeveloper.dialects;

import java.sql.Connection;
import java.sql.SQLException;

public interface ExecutionBuilder<T> {

	public T excecute(Connection conn) throws SQLException;
	
}
