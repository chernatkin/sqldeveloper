package org.chernatkin.android.sqldeveloper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.chernatkin.android.sqldeveloper.dialects.AbstractExpandableListProvider;
import org.chernatkin.android.sqldeveloper.dialects.ExecutionBuilder;
import org.chernatkin.android.sqldeveloper.dialects.ExpandableListProvider;
import org.chernatkin.android.sqldeveloper.dialects.ResultSetTransformer;
import org.chernatkin.android.sqldeveloper.dialects.SQLDialect;
import org.chernatkin.android.sqldeveloper.dialects.SQLDialectManager;
import org.chernatkin.android.sqldeveloper.dialects.StatementBuilder;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class SchemaObjectsActivity extends Activity{
	
	public static String SCHEMA_PARAM_NAME = "schema";
	
	public static String DIALECT_PARAM_NAME = "dialect";

	private final ExpandableListProvider tablesProvider = new AbstractExpandableListProvider() {
		
		@Override
		protected Map<String, List<String>> createTree(final SQLDialect dialect, final String schema) throws SQLException{
			return SQLDialectManager.execute(dialect, "INFORMATION_SCHEMA", new ExecutionBuilder<Map<String, List<String>>>() {

				@Override
				public Map<String, List<String>> excecute(final Connection conn) throws SQLException {
					final ResultSet collsSet = conn.prepareStatement("SELECT cols.TABLE_NAME, cols.COLUMN_NAME, cols.DTD_IDENTIFIER, cols.IS_NULLABLE, cols.COLUMN_DEFAULT FROM COLUMNS cols "
							+ "INNER JOIN TABLES tbls ON cols.TABLE_SCHEMA = tbls.TABLE_SCHEMA AND cols.TABLE_NAME = tbls.TABLE_NAME "
							+ "WHERE TABLE_SCHEMA = '" + schema.toUpperCase() + "' and TABLE_TYPE = 'BASE TABLE' ORDER BY cols.TABLE_NAME, cols.COLUMN_NAME;").executeQuery();
					
					final Map<String, List<String>> tree = new LinkedHashMap<String, List<String>>();
					
					while(collsSet.next()){
						
						final String tableName = collsSet.getString("TABLE_NAME");
						List<String> values = tree.get(tableName);
						if(values == null){
							values = new ArrayList<String>();
							tree.put(tableName, values);
						}
						
						final String columnName = collsSet.getString("COLUMN_NAME");
						final String columnType = collsSet.getString("DTD_IDENTIFIER");
						final String nullable = collsSet.getString("IS_NULLABLE");
						final Object defaultValue = collsSet.getObject("COLUMN_DEFAULT");
						
						final String columnDef = "<b>" + columnName + "</b>" 
								+ "  <i>" + columnType.toLowerCase() 
								+ ((nullable != null && nullable.equalsIgnoreCase("NO")) ? " not null" : "") 
								+ (defaultValue != null ? "  default " + defaultValue : "")
								+ "</i>";
						
						values.add(columnDef);
					}
					
					final ResultSet pkSet = conn.prepareStatement("SELECT TABLE_NAME, COLUMN_NAME, PK_NAME FROM SYSTEM_PRIMARYKEYS WHERE TABLE_SCHEM = '" + schema.toUpperCase() + "';").executeQuery();
					
					while(pkSet.next()){
						final String tableName = pkSet.getString("TABLE_NAME");
						final List<String> values = tree.get(tableName);
						if(values == null) { continue; }
						
						final String pkDef = "<i>primary key:</i> " +
													"<b>" + pkSet.getString("PK_NAME") + "</b>, " +
													"<i>column</i> <b>" + pkSet.getString("COLUMN_NAME") + "</b>";
						values.add(pkDef);
					}
					
					final ResultSet fkSet = conn.prepareStatement("SELECT PKTABLE_NAME, PKCOLUMN_NAME, FKTABLE_NAME, FKCOLUMN_NAME, FK_NAME FROM SYSTEM_CROSSREFERENCE WHERE FKTABLE_SCHEM = '" + schema.toUpperCase() + "';").executeQuery();
					
					while(fkSet.next()){
						final String tableName = fkSet.getString("FKTABLE_NAME");
						final List<String> values = tree.get(tableName);
						if(values == null) { continue; }
						
						final String fkDef = "<i>foreign key:</i> " +
												"<b>" + fkSet.getString("FK_NAME") + "</b>, " +
												"<i>column</i> <b>" + fkSet.getString("FKCOLUMN_NAME") + "</b> " +
												"<i>references</i> <b>" + fkSet.getString("PKTABLE_NAME") +"(" + fkSet.getString("FKCOLUMN_NAME") + ")</b>";
						values.add(fkDef);
					}
					
					return tree;
				}
			});
		}
	};
	
	private String schema;
	
	private SQLDialect dialect;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		schema = getIntent().getStringExtra(SCHEMA_PARAM_NAME);
		dialect = SQLDialectManager.getDialect(getIntent().getIntExtra(DIALECT_PARAM_NAME, 0));
		
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		
		addTab("Tables", tablesProvider);
		addSQLSheetTab();
	}

	private void addTab(final String label, final ExpandableListProvider content){
		final ActionBar bar = getActionBar();
		
		final SchemaObjectsFragment fr = new SchemaObjectsFragment();
		fr.setProvider(content);
		fr.setSchema(schema);
		fr.setDialect(dialect);
		
		bar.addTab(bar.newTab().setText(label).setTabListener(new TabListener() {
			
			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				ft.remove(fr);
			}
			
			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				ft.replace(android.R.id.content, fr);
			}
			
			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {}
		}));
	}

	private void addSQLSheetTab(){
		final ActionBar bar = getActionBar();
		
		bar.addTab(bar.newTab().setText("SQL Sheets").setTabListener(new TabListener() {
			
			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
			
			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				startSQLActivity();
			}
			
			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				startSQLActivity();
			}
		}));
	}
	
	private void startSQLActivity(){
		final Intent sqlIntent = new Intent(SchemaObjectsActivity.this, SQLActivity.class);
		sqlIntent.putExtra(SQLActivity.SCHEMA_PARAM_NAME, schema);
		sqlIntent.putExtra(SQLActivity.DIALECT_PARAM_NAME, dialect.getTitleId());
		startActivity(sqlIntent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getActionBar().setSelectedNavigationItem(0);
	}
}
