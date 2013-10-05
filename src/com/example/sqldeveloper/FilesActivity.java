package com.example.sqldeveloper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class FilesActivity extends Activity {

	public static final String FILES_ACTION_PARAM_NAME = "action";
	
	public static final String FILES_ACTION_LOAD = "load";
	
	public static final String FILES_ACTION_SAVE = "save";
	
	public static final String FILES_INITIAL_PATH = "initial_path";
	
	public static final String FILES_INITIAL_FILE_NAME = "initial_file_name";
	
	public static final String FILES_SQL_TO_SAVE = "sql_to_save";
	
	private static final String PARENT_DIR_ALIAS = "..";
	
	private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	private String action;
	
	private String sqlForSaving;
	
	private File currentDir;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_files);
		
		final Intent intent = getIntent();
		action = intent.getStringExtra(FILES_ACTION_PARAM_NAME);
		
		final String initPath = intent.getStringExtra(FILES_INITIAL_PATH);
		final String initFileName = intent.getStringExtra(FILES_INITIAL_FILE_NAME);
		sqlForSaving = intent.getStringExtra(FILES_SQL_TO_SAVE);
		
		final Button button = (Button)findViewById(R.id.file_action_button);
		final EditText fileNameView = (EditText)findViewById(R.id.new_file_name);
		
		if(action.equals(FILES_ACTION_SAVE)){
			button.setVisibility(View.VISIBLE);
			fileNameView.setText(initFileName);
			fileNameView.setVisibility(View.VISIBLE);
		}
		else{
			button.setVisibility(View.INVISIBLE);
			fileNameView.setVisibility(View.INVISIBLE);
			fileNameView.setText("");
		}
		
		currentDir = new File(initPath);
		if(!currentDir.exists()){
			currentDir.mkdir();
		}
		openDir(null);
	}

	public void onSave(final View view){
		final EditText fileNameView = (EditText)findViewById(R.id.new_file_name);
		saveTextToFile(fileNameView.getText().toString(), sqlForSaving);
		
		setResult(RESULT_OK);
		finish();
	}
	
	private void openDir(final File file){
		if(file != null){
			if(!file.isDirectory()){
				return;
			}
			currentDir = file;
		}
		
		final ListView listView = (ListView)findViewById(R.id.files_list);
		
		final List<String> filesList = new ArrayList<String>();
		if(!ROOT_PATH.equals(currentDir.getAbsolutePath())){
			filesList.add(PARENT_DIR_ALIAS);
		}
		final String[] childFiles = currentDir.list();
		if(childFiles != null && childFiles.length != 0){
			filesList.addAll(Arrays.asList(childFiles));
		}
		
		listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, android.R.id.text1, filesList));
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
				final String fileName = ((TextView)item).getText().toString();
				final File file = getChildFile(fileName);
				
				if(file.isDirectory()){
					openDir(file);
				}
				else if(action.equals(FILES_ACTION_LOAD)){
					if(file.length() > 1024){
						buildFailDialog("Too large", "File size should be less than 1024 bytes").show();
						return;
					}
					buildQuestionDialog("SQL import", "File \"" + file.getName() + "\" will be imported", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							final Intent sqlIntent = new Intent("");
							sqlIntent.putExtra(SQLActivity.LOADED_SQL_PARAM_NAME, readTextFromFile(file));
							setResult(RESULT_OK, sqlIntent);
							FilesActivity.this.finish();
						}
					}).show();
				}
			}
		});
		
		final String path = currentDir.getAbsolutePath().substring(ROOT_PATH.length());
		final TextView currentPath = (TextView)findViewById(R.id.current_files_path);
		currentPath.setText(path.isEmpty() ? "/" : path);
	}
	
	private File getChildFile(final String name){
		if(name == null){
			return currentDir;
		}
		if(name.equals(PARENT_DIR_ALIAS)){
			return currentDir.getParentFile();
		}
		
		return new File(currentDir.getAbsolutePath() + File.separator + name);
	}
	
	private AlertDialog buildFailDialog(final String title, final String text){
		final TextView textView = new TextView(this);
		textView.setPadding(10, 0, 10, 0);
		textView.setText(text);
		
		return new AlertDialog.Builder(this)
					.setTitle(title)
					.setView(textView)
					.setPositiveButton(R.string.ok, null)
					.create();
	}
	
	private AlertDialog buildQuestionDialog(final String title, final String text, final OnClickListener okListener){
		final TextView textView = new TextView(this);
		textView.setPadding(10, 0, 10, 0);
		textView.setText(text);
		
		return new AlertDialog.Builder(this)
					.setTitle(title)
					.setView(textView)
					.setPositiveButton(R.string.ok, okListener)
					.setNegativeButton(R.string.cancel, null)
					.create();
	}
	
	private String readTextFromFile(final File file){
		
		FileReader in = null;
		try{
			in = new FileReader(file);
			
			final StringBuilder text = new StringBuilder();
			final char[] buf = new char[256];
			
			int length = 0;
			while((length = in.read(buf)) != -1){
				text.append(buf, 0, length);
			}
			return text.toString();
		}
		catch(Exception e){
			return "";
		}
		finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {}
			}
		}
		
	}
	
	private void saveTextToFile(final String fileName, final String text){
		final File file = new File(currentDir.getAbsolutePath() + File.separator + fileName);
		
		FileWriter out = null;
		try{
			out = new FileWriter(file);
			out.write(text);
		} catch(Exception e){
			return;
		}
		finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {}
			}
		}
	}
}
