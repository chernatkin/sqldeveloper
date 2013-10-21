package org.chernatkin.android.sqldeveloper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.chernatkin.android.sqldeveloper.dialects.ExecutionBuilder;
import org.chernatkin.android.sqldeveloper.dialects.SQLDialect;
import org.chernatkin.android.sqldeveloper.dialects.SQLDialectManager;
import org.chernatkin.android.sqldeveloper.utils.UniqueIdGenerator;

import org.chernatkin.android.sqldeveloper.R;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class SQLFragment extends Fragment {

	private static final String STATE_DIALECT = "dialect";
	
	private static final String STATE_SCHEMA = "schema";
	
	public static final String STATE_SQL_CONTENT = "sql_content";
	
	private SQLDialect dialect;
	
	private String schema;
	
	private ScrollView scroll;
	
	private EditText sqlArea;
	
	private String sqlContent;
	
	public SQLFragment() {
	}
	
	public void setDialect(SQLDialect dialect) {
		this.dialect = dialect;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setSqlContent(String sqlContent) {
		this.sqlContent = sqlContent;
		if(sqlArea != null){
			sqlArea.setText(sqlContent);
		}
	}

	public String getSqlContent() {
		return sqlArea == null ? "" : sqlArea.getText().toString();
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(scroll != null) { return scroll; }
		
		parseSavedState(savedInstanceState);
		
		final Context context = inflater.getContext();
		scroll = new ScrollView(context);
		
		final HorizontalScrollView horizontalScroll = new HorizontalScrollView(context);
		scroll.addView(horizontalScroll);
		
		final RelativeLayout tabContent = new RelativeLayout(context);
		tabContent.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		horizontalScroll.addView(tabContent);
		
		final LinearLayout runButtons = new LinearLayout(context); 
		final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		runButtons.setLayoutParams(params);
		runButtons.setId(UniqueIdGenerator.generateId());
		runButtons.setOrientation(LinearLayout.HORIZONTAL);
		tabContent.addView(runButtons);
		
		final Button runButton = new Button(context);
		final LinearLayout.LayoutParams runParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		runButton.setLayoutParams(runParams);
		runButton.setText(R.string.run_all);
		runButtons.addView(runButton);
		
		sqlArea = new EditText(context);
		sqlArea.setId(UniqueIdGenerator.generateId());
		if(sqlContent != null){ sqlArea.setText(sqlContent); }
		RelativeLayout.LayoutParams sqlAreaParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		sqlAreaParams.addRule(RelativeLayout.BELOW, runButtons.getId());
		sqlArea.setLayoutParams(sqlAreaParams);
		sqlArea.setSingleLine(false);
		sqlArea.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
		sqlArea.setFilters(new InputFilter[] { new InputFilter.LengthFilter(1024) });
		tabContent.addView(sqlArea);
		
		
		final TextView resultMsg = new TextView(context);
		RelativeLayout.LayoutParams resultMsgParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		resultMsgParams.addRule(RelativeLayout.BELOW, sqlArea.getId());
		resultMsg.setLayoutParams(resultMsgParams);
		resultMsg.setRawInputType(InputType.TYPE_NULL);
		resultMsg.setKeyListener(null);
		resultMsg.setFilters(new InputFilter[] { new InputFilter.LengthFilter(1024) });
		resultMsg.setText(R.string.no_result);
		tabContent.addView(resultMsg);
		
		final TableLayout resultTable = new TableLayout(context);  
		RelativeLayout.LayoutParams resultTableParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		resultTableParams.addRule(RelativeLayout.BELOW, sqlArea.getId());
		resultTable.setLayoutParams(resultTableParams);
		resultTable.setBackgroundColor(Color.BLACK);
		resultTable.setVisibility(View.INVISIBLE);
		tabContent.addView(resultTable);
		
		runButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				
				resultTable.setVisibility(View.INVISIBLE);
				resultTable.removeAllViews();
				
				resultMsg.setText(R.string.no_result);
				resultMsg.setVisibility(View.VISIBLE);
				
				final String sqlScript = sqlArea.getText().toString().trim();
				if(sqlScript.isEmpty()){ return; }
				
				if(!sqlScript.contains(";")){
					resultMsg.setText("Expected \";\" after each statement");
					return;
				}
				
				final List<String> queries = splitQueries(sqlScript);
				
				final List<String> results = new ArrayList<String>(queries.size());
				
				try {
					SQLDialectManager.execute(dialect, schema, new ExecutionBuilder<List<String[]>>() {

						@Override
						public List<String[]> excecute(final Connection conn) throws SQLException{
							for(int i = 0; i < queries.size(); i++){
								final String sql = queries.get(i).trim();
								if(sql.isEmpty()){ continue; }
								
								PreparedStatement ps = conn.prepareStatement(sql);
								boolean isResultSet = ps.execute();
								if(!isResultSet){
									int count = ps.getUpdateCount();
									results.add(count + " rows changed");
								} else if(i == queries.size() - 1){
									final ResultSet set = ps.getResultSet();
									final ResultSetMetaData meta = set.getMetaData();
									
									final TableRow header = new TableRow(context);
									final TextView rowIdName = createTableCell(context);
									rowIdName.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
									rowIdName.setBackgroundColor(Color.GRAY);
									rowIdName.setText("ROWID");
									header.addView(rowIdName);
									
									for(int j = 1; j <= meta.getColumnCount(); j++){
										final TextView cell = createTableCell(context);
										cell.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
										cell.setBackgroundColor(Color.GRAY);
										
										cell.setText(meta.getColumnLabel(j));
										header.addView(cell);
									}
									
									resultTable.addView(header);
									
									while(set.next()){
										final TableRow row = new TableRow(context);
										final TextView rowId = createTableCell(context);
										rowId.setText(Integer.toString(set.getRow()));
										row.addView(rowId);
										
										for(int j = 1; j <= meta.getColumnCount(); j++){
											final Object obj = set.getObject(j);
											
											final TextView cell = createTableCell(context);
											cell.setText(obj == null ? "null" : obj.toString());
											row.addView(cell);
										}
										
										resultTable.addView(row);
									}
									
									results.clear();
									resultMsg.setText("");
									resultMsg.setVisibility(View.INVISIBLE);
									resultTable.setVisibility(View.VISIBLE);
								}
								
							}
							return null;
						}
					});
				} catch (SQLException sqle) {
					results.add(sqle.getMessage());
				}
				
				if(results.isEmpty()){
					return;
				}
				
				final StringBuilder result = new StringBuilder();
				for(String textMsg : results){
					result.append(textMsg).append("\n");
				}
				
				resultMsg.setText(result.toString());
			}
		});
		
		return scroll;
    }
	
	private List<String> splitQueries(final String script){
		final String[] queries = script.split(";");
		
		final List<String> sqls = new ArrayList<String>(queries.length);
		for(String query : queries){
			if(query == null){ continue; }
			final String sql = query.trim();
			if(!sql.isEmpty()){
				sqls.add(sql);
			}
		}
		
		return sqls;
	}
	
	private TextView createTableCell(final Context context){
		final TextView cell = new TextView(context);
		TableRow.LayoutParams tableCellParams = new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
		tableCellParams.setMargins(1, 1, 1, 1);
		cell.setLayoutParams(tableCellParams);
		cell.setPadding(2, 2, 2, 0);
		cell.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
		cell.setBackgroundColor(Color.WHITE);
		
		return cell;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseSavedState(savedInstanceState);
	}

	private void parseSavedState(Bundle savedInstanceState){
		if(savedInstanceState == null){ return; }
		
		if(savedInstanceState.containsKey(STATE_DIALECT)){
			dialect = SQLDialectManager.getDialect(savedInstanceState.getInt(STATE_DIALECT));
		}
		if(savedInstanceState.containsKey(STATE_SCHEMA)){
			schema = savedInstanceState.getString(STATE_SCHEMA);
		}
		if(savedInstanceState.containsKey(STATE_SQL_CONTENT)){
			sqlContent = savedInstanceState.getString(STATE_SQL_CONTENT);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_DIALECT, dialect.getTitleId());
		outState.putString(STATE_SCHEMA, schema);
		if(sqlArea != null){
			outState.putString(STATE_SQL_CONTENT, sqlArea.getText().toString());
		}
	}
	
	
}
