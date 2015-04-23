package com.example.m04surfaceviewtest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.util.Log;

public class Schedule {
	// Schedule primary key
	private int sn;
	// Have alarm?
	private boolean alarmSet;
	private String date1;
	private String time1;
	private String date2;
	private String time2;
	private String title;
	private String note;
	private String type;
	private boolean timeSet;
	
	public Schedule(int y, int m, int d)
	{
		sn=0;
		date1=toDateString(y,m,d);
		// Default 8 o'clock.
		time1=toTimeString(8, 0);
		date2=null;
		time2=null;
		title="";
		note="";
		type="";
		timeSet=true;
		alarmSet=false;
	}

	public Schedule(int sn, String date1, String time1, String date2, String time2, 
			String title, String note, String type, String timeSet, String alarmSet)
	{
		//...
	}

	public static String toDateString(int y, int m, int d) {
		// format: YYYY/MM/DD
		StringBuffer sb = new StringBuffer();
		sb.append(y);
		sb.append("/");
		sb.append(m<10?"0"+m:""+m);
		sb.append("/");
		sb.append(d<10?"0"+d:""+d);
		return sb.toString();		
	}
	
	public String toTimeString(int h, int m) {
		// format: HH:MM
		StringBuffer sb = new StringBuffer();		
		sb.append(h<10?"0"+h:""+h);
		sb.append(":");
		sb.append(m<10?"0"+m:""+m);
		return sb.toString();		
	}
	
	public String typeForListView()
	{
		StringBuffer sb = new StringBuffer();		
		sb.append("[");
		sb.append(type);
		sb.append("]");
		return sb.toString();
	}
	
	public String dateForListView()
	{
		StringBuffer sb = new StringBuffer();				
		sb.append(date1);
		sb.append("    ");
		return sb.toString();
	}
	
	public String timeForListView()
	{
		if(!timeSet)
		{
			return "- -:- -    ";
		}
		
		StringBuffer sb = new StringBuffer();				
		sb.append(time1);
		sb.append("    ");
		return sb.toString();
	}
	
	public boolean isPassed()
	{
		// Check if out of date.
		
		String nowDate = getNowDateString();
		String nowTime = getNowTimeString();
		String schDate = date1;
		String schTime = timeSet?time1:"23:59";
		// Pass if current time is later than scheduled time.
		if(nowDate.compareTo(schDate)>0
				|| (nowDate.compareTo(schDate)==0&&nowTime.compareTo(schTime)>0))		
		{
			return true;
		}
		
		return false;
	}

	public String toInsertSql(Activity father)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("insert into schedule values('')");
		//ToDo:
		//sn=getSNFromPrefs(father);
		//....
		Log.d("toInsertSql", sb.toString());
		return sb.toString();
	}
	
	public String toUpdateSql(Activity father)
	{
		int preSn=sn;
		StringBuffer sb = new StringBuffer();
		sb.append("update schedule set sn=");
		//ToDo:
		//sn=getSNFromPrefs(father);
		//....
		Log.d("toUpdateSql", sb.toString());
		return sb.toString();
	}
	
	private String getNowDateString() {
		Date aDate= new Date();		
		return new SimpleDateFormat("YYYY/MM/dd", Locale.getDefault()).format(aDate);		
	}
	
	private String getNowTimeString() {
		Date aDate= new Date();		
		return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(aDate);		
	}

	
}
