package org.chernatkin.android.sqldeveloper.expandablelist;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.chernatkin.android.sqldeveloper.dialects.SQLDialect;

import android.content.Context;
import android.widget.ExpandableListAdapter;

public abstract class AbstractEventExpandableListProvider implements ExpandableListProvider{

	@Override
	public ExpandableListAdapter createAdapter(final Context context, final SQLDialect dialect, final String schema) {
		try{
			return new EventExpandableListAdapter(context, createTree(dialect, schema));
		}
		catch(SQLException sqle){
			return new EventExpandableListAdapter(context, Collections.<ExpandableListItem, List<ExpandableListItem>>emptyMap());
		}
	}

	protected abstract Map<ExpandableListItem, List<ExpandableListItem>> createTree(final SQLDialect dialect, final String schema) throws SQLException;
}
