package com.example.sqldeveloper.dialects;

import java.io.File;

import com.example.sqldeveloper.R;

public class OracleDialect extends SQLDialect {

	public OracleDialect() {
		super(R.string.title_oracle, getApplicationRoot() + File.separator + "oracle" + File.separator + "oracledb");
		getProps().setProperty("sql.syntax_ora", "true");
	}

	
	
}
