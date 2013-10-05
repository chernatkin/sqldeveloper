package com.example.sqldeveloper;

import com.example.sqldeveloper.utils.UniqueIdGenerator;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class SQLTabListener implements TabListener {

	public static final String NEW_TAB_ACTION = "org.my.android.sqldeveloper.action.newsqltab"; 
	
	private final Fragment fragment;
	
	private final SQLActivity context;
	
	public SQLTabListener(final Fragment fragment, final SQLActivity context) {
		this.fragment = fragment;
		this.context = context;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		return;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		ft.replace(android.R.id.content, fragment);
		
		final int pos = tab.getPosition();
		final int tabCount = context.getActionBar().getTabCount();
		if(!tab.getText().toString().equalsIgnoreCase(context.getString(R.string.new_sqltab_title)) || pos + 1 != tabCount) { return; }
		
		tab.setText(UniqueIdGenerator.generateSqlTabName(context.tabCounter.getAndIncrement()));
		
		final Intent newTabIntent = new Intent(NEW_TAB_ACTION);
		LocalBroadcastManager.getInstance(context).sendBroadcast(newTabIntent);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(fragment);
	}

}
