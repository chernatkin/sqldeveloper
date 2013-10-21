package org.chernatkin.android.sqldeveloper.dialects;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetTransformer<T> {

	public T transformResultSet(ResultSet result, boolean resultIsResultSet) throws SQLException;
	
}
