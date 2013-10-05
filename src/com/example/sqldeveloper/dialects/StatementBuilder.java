package com.example.sqldeveloper.dialects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementBuilder {

	public PreparedStatement prepareStatement(Connection conn) throws SQLException;
}
