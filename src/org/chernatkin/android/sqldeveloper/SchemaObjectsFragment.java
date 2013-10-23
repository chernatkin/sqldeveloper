package org.chernatkin.android.sqldeveloper;

import org.chernatkin.android.sqldeveloper.dialects.ExpandableListProvider;
import org.chernatkin.android.sqldeveloper.dialects.SQLDialect;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class SchemaObjectsFragment extends Fragment{

	private ExpandableListProvider provider;

	private String schema;
	
	private SQLDialect dialect;
	
	public void setProvider(ExpandableListProvider provider) {
		this.provider = provider;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	public void setDialect(SQLDialect dialect) {
		this.dialect = dialect;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		final Context context = inflater.getContext();
		
		final LinearLayout tabContent = new LinearLayout(context);
		tabContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		tabContent.setOrientation(LinearLayout.VERTICAL);
		
		final ExpandableListView listView = new ExpandableListView(context);
		listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		listView.setAdapter(provider.createAdapter(context, dialect, schema));
		tabContent.addView(listView);
		
		return tabContent;
	}
	
	
}
