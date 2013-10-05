package com.example.sqldeveloper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sqldeveloper.dialects.SQLDialect;
import com.example.sqldeveloper.dialects.SQLDialectManager;
import com.example.sqldeveloper.dialects.StatementBuilder;
import com.example.sqldeveloper.utils.NamesValidatior;

public class SchemasActivity extends FragmentActivity implements ActionBar.OnNavigationListener {
	
	private int lastSelectedDialect = -1;
	
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schemas);

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setTitle("Database type:");
		
		final List<SQLDialect> dialects = SQLDialectManager.getAllDialects();
		final String[] titles = new String[dialects.size()];
		
		for(int i = 0; i < dialects.size(); i++){
			titles[i] = getString(dialects.get(i).getTitleId());
		}
		
		actionBar.setListNavigationCallbacks(new ArrayAdapter<String>(actionBar.getThemedContext(),
										android.R.layout.simple_list_item_1,
										android.R.id.text1, titles), this);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.schemas, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		
		if(item.getItemId() == R.id.add_schema_item){ 
			
			final View popup = getLayoutInflater().inflate(R.layout.schema_add_popup, null);
			final EditText nameView = (EditText)popup.findViewById(R.id.schema_name_input);
			final TextView errorView = (TextView)popup.findViewById(R.id.schema_name_error);
			errorView.setVisibility(View.INVISIBLE);
			
			final AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setTitle(R.string.new_schema_popup_title)		
					.setView(popup);
			
			final AlertDialog dialog = builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					return;
				}
			})
			.setPositiveButton(R.string.ok, null)
			.create();
			dialog.show();
			
			dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					errorView.setVisibility(View.INVISIBLE);
					final String schemaName = nameView.getText().toString();
					if(!NamesValidatior.isSchemaNameValid(schemaName)){
						errorView.setText(R.string.invalid_schema_name_msg);
						errorView.setVisibility(View.VISIBLE);
						return;
					}
					
					final SQLDialect dialect = SQLDialectManager.getAllDialects().get(lastSelectedDialect);
					try {
						SQLDialectManager.execute(dialect, new StatementBuilder() {
							
							@Override
							public PreparedStatement prepareStatement(final Connection conn) throws SQLException {
								return conn.prepareStatement("CREATE SCHEMA "+ schemaName.trim().toLowerCase(Locale.ENGLISH) +" AUTHORIZATION DBA");
							}
						}, null);
						dialog.dismiss();
						refreshSchemasFragment();
					} catch (SQLException e) {
						errorView.setText(e.getMessage());
						errorView.setVisibility(View.VISIBLE);
					}
				}
			});
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(final int position, final long id) {
		if(lastSelectedDialect == position){ return false; }
		lastSelectedDialect = position;
		
		refreshSchemasFragment();
		return true;
	}
	
	private void refreshSchemasFragment(){
		final Fragment fragment = new SchemasSectionFragment();
		final Bundle args = new Bundle();
		args.putInt(SchemasSectionFragment.SQL_DIALECT_INDEX, getActionBar().getSelectedNavigationIndex());
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
	}
}