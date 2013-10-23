package org.chernatkin.android.sqldeveloper.dialects;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.chernatkin.android.sqldeveloper.SimpleExpandableListAdapter;

import android.content.Context;
import android.widget.ExpandableListAdapter;

public abstract class AbstractExpandableListProvider implements ExpandableListProvider {
	
	@Override
	public ExpandableListAdapter createAdapter(final Context context, final SQLDialect dialect, final String schema) {
		try{
			return new SimpleExpandableListAdapter(context, createTree(dialect, schema));
		}
		catch(SQLException sqle){
			return new SimpleExpandableListAdapter(context, Collections.<String, List<String>>emptyMap());
		}
	}

	protected abstract Map<String, List<String>> createTree(final SQLDialect dialect, final String schema) throws SQLException;
}
