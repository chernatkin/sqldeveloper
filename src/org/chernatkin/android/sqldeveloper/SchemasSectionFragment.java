package org.chernatkin.android.sqldeveloper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.chernatkin.android.sqldeveloper.dialects.ResultSetTransformer;
import org.chernatkin.android.sqldeveloper.dialects.SQLDialect;
import org.chernatkin.android.sqldeveloper.dialects.SQLDialectManager;
import org.chernatkin.android.sqldeveloper.dialects.StatementBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.TextView;

public class SchemasSectionFragment extends Fragment {

	public static final String SQL_DIALECT_INDEX = "sql_dialect_index";
	
	public static final String DELETE_SCHEMA_ACTION = "delete_schema_action";
	
	public static final String SCHEMA_NAME = "schema_name";

	private int dialectPosition = 0;
	
	private View rootView;
	
	public SchemasSectionFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(rootView != null){ return rootView; }
		
		rootView = inflater.inflate(R.layout.fragment_schemas, container, false);
		
		if(savedInstanceState != null && savedInstanceState.getInt(SQL_DIALECT_INDEX) > 0){
			dialectPosition = savedInstanceState.getInt(SQL_DIALECT_INDEX);
		}
		else if(getArguments() != null && getArguments().getInt(SQL_DIALECT_INDEX) > 0){
			dialectPosition = getArguments().getInt(SQL_DIALECT_INDEX);
		}
		else{
			dialectPosition = 0;
		}
		
		registerForContextMenu(buildSchemasTree());
		
		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(SQL_DIALECT_INDEX, dialectPosition);
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		
		final ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
		
		if(item.getItemId() == R.id.delete_schema_item){
			final String schemaName = ((TextView)info.targetView).getText().toString();
			
			final SQLDialect selectedDialect = SQLDialectManager.getAllDialects().get(dialectPosition);
			try{
				SQLDialectManager.execute(selectedDialect, null, new StatementBuilder() {
					
					@Override
					public PreparedStatement prepareStatement(final Connection conn) throws SQLException {
						return conn.prepareStatement("DROP SCHEMA " + schemaName + " CASCADE;");
					}
				}, null);
				
			}catch(SQLException sqle){
			}

			buildSchemasTree();
			return true;
		}
		
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(R.string.context_menu_title);
		getActivity().getMenuInflater().inflate(R.menu.schemas_context_menu, menu);
	}
	
	private ExpandableListView buildSchemasTree(){
		final SQLDialect selectedDialect = SQLDialectManager.getAllDialects().get(dialectPosition);
		
		final List<String> localSchemas = new ArrayList<String>();
		try {
			SQLDialectManager.execute(selectedDialect, "INFORMATION_SCHEMA", new StatementBuilder() {
				@Override
				public PreparedStatement prepareStatement(final Connection conn) throws SQLException{
					return conn.prepareStatement("SELECT schema_name FROM schemata WHERE schema_owner = 'DBA';");
				}
			},
			new ResultSetTransformer<List<String>>() {
				@Override
				public List<String> transformResultSet(final ResultSet result, final boolean resultIsResultSet) throws SQLException{
					while(result.next()){
						localSchemas.add(result.getString("SCHEMA_NAME"));
					}
					return localSchemas;
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		final Map<String, List<String>> tree = new LinkedHashMap<String, List<String>>();
		tree.put(getString(R.string.local_schemas_title), localSchemas);
		
		final ExpandableListView treeView = (ExpandableListView) rootView.findViewById(R.id.schemas_tree);
		treeView.setAdapter(new SimpleExpandableListAdapter(treeView.getContext(), Collections.unmodifiableMap(tree)));
		treeView.expandGroup(0);
		
		treeView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				final Intent sqlIntent = new Intent(parent.getContext(), SchemaObjectsActivity.class);
				sqlIntent.putExtra(SchemaObjectsActivity.SCHEMA_PARAM_NAME, parent.getExpandableListAdapter().getChild(groupPosition, childPosition).toString());
				sqlIntent.putExtra(SchemaObjectsActivity.DIALECT_PARAM_NAME, selectedDialect.getTitleId());
				startActivity(sqlIntent);
				return true;
			}
		});
		
		return treeView;
	}
}
