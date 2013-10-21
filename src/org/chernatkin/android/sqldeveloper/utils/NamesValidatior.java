package org.chernatkin.android.sqldeveloper.utils;

import java.util.Locale;

public class NamesValidatior {

	
	public static boolean isSchemaNameValid(String name){
		if(name == null){ return false; }
		
		name = name.trim().toLowerCase(Locale.ENGLISH);
		if(name.isEmpty()){ return false; }
		
		return name.matches("^[[a-z][A-Z]]+[[a-z][A-Z][_][0-9]]+$");
	}
}
