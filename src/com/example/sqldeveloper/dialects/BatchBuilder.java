package com.example.sqldeveloper.dialects;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public interface BatchBuilder {

	public Statement createBatch(Connection conn) throws SQLException;
}
