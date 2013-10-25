package org.chernatkin.android.sqldeveloper.utils;

import org.chernatkin.android.sqldeveloper.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.widget.ScrollView;
import android.widget.TextView;

public class DialogUtils {

	public static AlertDialog buildMessageDialog(final Context context, final String title, final String text){
		return buildMessageDialog(context, title, text, null);
	}
	
	public static AlertDialog buildMessageDialog(final Context context, final String title, final String text, final OnClickListener okListener){
		final TextView textView = new TextView(context);
		textView.setPadding(10, 0, 10, 0);
		textView.setText(text);
		
		final ScrollView scroll = new ScrollView(context);
		scroll.addView(textView);
		
		return new AlertDialog.Builder(context)
					.setTitle(title)
					.setView(scroll)
					.setPositiveButton(R.string.ok, okListener)
					.create();
	}
	
	public static AlertDialog buildQuestionDialog(final Context context, final String title, final String text, final OnClickListener okListener){
		final TextView textView = new TextView(context);
		textView.setPadding(10, 0, 10, 0);
		textView.setText(text);
		
		return new AlertDialog.Builder(context)
					.setTitle(title)
					.setView(textView)
					.setPositiveButton(R.string.ok, okListener)
					.setNegativeButton(R.string.cancel, null)
					.create();
	}
	
}
