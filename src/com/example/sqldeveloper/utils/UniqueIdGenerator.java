package com.example.sqldeveloper.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class UniqueIdGenerator {

	private static final AtomicInteger COUNTER = new AtomicInteger(Integer.MAX_VALUE);
	
	
	public static int generateId(){
		return COUNTER.decrementAndGet();
	}
	
	public static String generateSqlTabName(final int tabPosition){
		return "Sheet" + tabPosition;
	}
}
