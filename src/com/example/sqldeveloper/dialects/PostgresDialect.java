package com.example.sqldeveloper.dialects;

import java.io.File;

import com.example.sqldeveloper.R;

public class PostgresDialect extends SQLDialect {

	public PostgresDialect() {
		super(R.string.title_postgres, getApplicationRoot() + File.separator  + "postgresql" + File.separator + "postgresqldb");
		getProps().setProperty("sql.syntax_pgs", "true");
	}
	
}
