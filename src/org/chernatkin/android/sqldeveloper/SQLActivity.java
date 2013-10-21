package org.chernatkin.android.sqldeveloper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.chernatkin.android.sqldeveloper.dialects.SQLDialect;
import org.chernatkin.android.sqldeveloper.dialects.SQLDialectManager;
import org.chernatkin.android.sqldeveloper.utils.UniqueIdGenerator;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;

import org.chernatkin.android.sqldeveloper.R;

public class SQLActivity extends Activity {

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	private static final String STATE_TAB_COUNTER = "tab_counter";
	
	public static String SCHEMA_PARAM_NAME = "schema";
	
	public static String DIALECT_PARAM_NAME = "dialect";
	
	public static String LOADED_SQL_PARAM_NAME = "loaded_sql";
	
	private String schema;
	
	private SQLDialect dialect;
	
	public final AtomicInteger tabCounter = new AtomicInteger(1);
	
	private final List<SQLFragment> fragments = new ArrayList<SQLFragment>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		schema = getIntent().getStringExtra(SCHEMA_PARAM_NAME);
		dialect = SQLDialectManager.getDialect(getIntent().getIntExtra(DIALECT_PARAM_NAME, 0));
		
		LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
			
			@Override
			public void onReceive(final Context context, final Intent intent) {
				addTab(getString(R.string.new_sqltab_title), null);
			}
		}, new IntentFilter(SQLTabListener.NEW_TAB_ACTION));
		
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		
		restorePrefs();
		addTab(getString(R.string.new_sqltab_title), null);
	}
	
	private void addTab(final String label, final String content){
		final ActionBar bar = getActionBar();
		
		final SQLFragment fr = new SQLFragment();
		fr.setDialect(dialect);
		fr.setSchema(schema);
		fr.setSqlContent(content);
		
		fragments.add(fr);
		
		bar.addTab(bar.newTab().setText(label).setTabListener(new SQLTabListener(fr, this)));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sql, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		
		if(item.getItemId() == R.id.delete_sqltab){ 
			getActionBar().removeTabAt(getActionBar().getSelectedNavigationIndex());
			fragments.remove(getActionBar().getSelectedNavigationIndex());
			return true;
		}
		if(item.getItemId() == R.id.delete_all_sqltabs){ 
			getActionBar().removeAllTabs();
			addTab(getString(R.string.new_sqltab_title), null);
			tabCounter.set(1);
			return true;
		}
		if(item.getItemId() == R.id.save_sqltab){
			final Intent filesIntent = new Intent(this, FilesActivity.class);
			filesIntent.putExtra(FilesActivity.FILES_ACTION_PARAM_NAME, FilesActivity.FILES_ACTION_SAVE);
			filesIntent.putExtra(FilesActivity.FILES_INITIAL_PATH, SQLDialect.getApplicationRoot() + File.separator + "sqlsheets");
			filesIntent.putExtra(FilesActivity.FILES_INITIAL_FILE_NAME, getActionBar().getSelectedTab().getText() + ".sql");
			filesIntent.putExtra(FilesActivity.FILES_SQL_TO_SAVE, fragments.get(getActionBar().getSelectedNavigationIndex()).getSqlContent());
			startActivityForResult(filesIntent , R.id.save_sqltab);
		}
		if(item.getItemId() == R.id.load_sqltab){
			final Intent filesIntent = new Intent(this, FilesActivity.class);
			filesIntent.putExtra(FilesActivity.FILES_ACTION_PARAM_NAME, FilesActivity.FILES_ACTION_LOAD);
			filesIntent.putExtra(FilesActivity.FILES_INITIAL_PATH, SQLDialect.getApplicationRoot() + File.separator + "sqlsheets");
			startActivityForResult(filesIntent , R.id.load_sqltab);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if(savedInstanceState == null){
			return;
		}
		
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
		if (savedInstanceState.containsKey(STATE_TAB_COUNTER)) {
			tabCounter.set(savedInstanceState.getInt(STATE_TAB_COUNTER));
		}
		if (savedInstanceState.containsKey(DIALECT_PARAM_NAME)) {
			dialect = SQLDialectManager.getDialect(savedInstanceState.getInt(DIALECT_PARAM_NAME, 0));
		}
		if (savedInstanceState.containsKey(SCHEMA_PARAM_NAME)) {
			schema = savedInstanceState.getString(SCHEMA_PARAM_NAME);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
		outState.putInt(STATE_TAB_COUNTER, tabCounter.get());
		outState.putInt(DIALECT_PARAM_NAME, dialect.getTitleId());
		outState.putString(SCHEMA_PARAM_NAME, schema);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		final Editor editor = getPreferences(MODE_PRIVATE).edit();
		editor.clear();
		
		final ActionBar bar = getActionBar();
		final String newLabel = getString(R.string.new_sqltab_title);
		
		boolean isSaved = false;
		
		for(int i = 0; i < bar.getTabCount(); i++){
			final Tab tab = bar.getTabAt(i);
			final String sqlText = fragments.get(i).getSqlContent();
			
			if(tab.getText().toString().equalsIgnoreCase(newLabel) || sqlText == null){
				continue;
			}
			
			final String trimmedSql = sqlText.trim();
			if(trimmedSql.isEmpty()){
				continue;
			}
			
			editor.putString(makeKey(tab.getText().toString()), trimmedSql);
			isSaved = true;
		}
		
		if(!isSaved){
			resetTabCounter();
		}
		
		editor.putInt(makeKey(STATE_TAB_COUNTER), tabCounter.get());
		editor.commit();
		
		super.onDestroy();
	}
	
	private String makeKey(final String key){
		return dialect.getTitleId() + ";" + schema + ';' + key;
	}
	
	private void restorePrefs(){
		final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		for(Map.Entry<String, ?> entry : prefs.getAll().entrySet()){
			final String[] keys = entry.getKey().split(";");
			final int titleId = Integer.parseInt(keys[0]);
			final String savedSchema = keys[1];
			final String key = keys[2];
			
			if(titleId != dialect.getTitleId() || !schema.equals(savedSchema)){
				continue;
			}
			if(key.equals(STATE_TAB_COUNTER)){
				tabCounter.set((Integer)entry.getValue());
				continue;
			}
			
			addTab(key, entry.getValue().toString());
		}
	}
	
	private void resetTabCounter(){
		tabCounter.set(1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == R.id.load_sqltab && resultCode == Activity.RESULT_OK){
			final String loadedSql = data.getStringExtra(LOADED_SQL_PARAM_NAME);
			
			final int tabNewIndex = getActionBar().getTabCount() - 1;
			final Tab tabNew = getActionBar().getTabAt(tabNewIndex);
			tabNew.setText(UniqueIdGenerator.generateSqlTabName(tabCounter.getAndIncrement()));
			fragments.get(tabNewIndex).setSqlContent(loadedSql);
			
			addTab(getString(R.string.new_sqltab_title), null);
			getActionBar().setSelectedNavigationItem(tabNewIndex);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}
	
	
}
