package org.chernatkin.android.sqldeveloper.dialects;

import android.content.Context;
import android.widget.ExpandableListAdapter;

public interface ExpandableListProvider {

	
	public ExpandableListAdapter createAdapter(final Context context, final SQLDialect dialect, final String schema);
}
