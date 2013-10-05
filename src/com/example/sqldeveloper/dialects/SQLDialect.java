package com.example.sqldeveloper.dialects;

import java.io.File;
import java.util.Properties;

import android.os.Environment;

public abstract class SQLDialect {

	private final Properties props = new Properties();

	private final int titleId;
	
	private final String dbName;
	
	public SQLDialect(final int title, final String dbname) {
		props.setProperty("user", "SA");
		props.setProperty("password", "");
		props.setProperty("jdbc.translate_tti_types", "false");
		
		this.titleId = title;
		this.dbName = dbname;
	}

	public Properties getProps() {
		return props;
	}

	public int getTitleId() {
		return titleId;
	}

	public String getDbName() {
		return dbName;
	}
	
	public static String getApplicationRoot(){
		return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sqldeveloper";
	}
}
