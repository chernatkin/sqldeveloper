package com.example.sqldeveloper.dialects;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BatchBuilderImpl implements BatchBuilder {

	private final List<String> queries = new ArrayList<String>();
	
	public BatchBuilderImpl(final String query) {
		queries.add(query);
	}

	@Override
	public Statement createBatch(final Connection conn) throws SQLException {
		final Statement st = conn.createStatement();
		
		for(String query : queries) {
			st.addBatch(query);
		}
		
		return st;
	}
	
	public BatchBuilderImpl addQuery(final String query){
		queries.add(query);
		return this;
	}
}
