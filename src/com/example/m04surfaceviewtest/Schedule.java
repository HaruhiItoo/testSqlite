package com.example.m04surfaceviewtest;

import static com.example.m04surfaceviewtest.DBUtil.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.util.Log;

public class Schedule {
	// Schedule primary key
	private int sn;
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
		this.sn=sn;
		this.date1=date1;
		this.time1=time1;
		this.date2=date2;
		this.time2=time2;
		this.title=title;
		this.note=note;
		this.type=type;
		// 0 (false) and 1 (true).
		this.timeSet=(timeSet.equals("1"));
		this.alarmSet=(alarmSet.equals("1"));
	}

	//
	// Getters
	//	
	public int getSn() {
		return sn;
	}
	public String getDate1() {		
		return date1;
	}
	
	public boolean getAlarmSet() {		
		return alarmSet;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getNote() {
		return note;
	}

	public String getTime1() {
		return time1;
	}

	public boolean getTimeSet() {		
		return timeSet;
	}

	public String getDate2() {
		return date2;
	}

	public String getTime2() {
		return time2;
	}
	
	public String getType() {
		return type;
	}

	//
	// Setters
	//
	public void setDate1(String date1)
	{
		this.date1=date1;
		this.timeSet=true;
	}
	public void setDate2(String date2)
	{
		this.date2=date2;
		this.alarmSet=true;
	}
	public void setTitle(String title) {
		this.title=title;		
	}

	public void setNote(String note) {
		this.note=note;		
	}

	public void setType(String type) {
		this.type=type;		
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

	public String toInsertSql(RcActivity father)
	{
		Schedule sch = father.schTemp;
		sn=getSNFromPrefs(father);
		StringBuffer sb = new StringBuffer();
		sb.append("insert into schedule values(");	
		sb.append(sn);
		sb.append(",'");
		sb.append(sch.date1);
		sb.append("','");
		sb.append(sch.time1);
		sb.append("','");
		sb.append(sch.date2);
		sb.append("','");
		sb.append(sch.time2);
		sb.append("','");
		sb.append(sch.title);
		sb.append("','");
		sb.append(sch.note);
		sb.append("','");
		sb.append(sch.type);
		sb.append("',");
		sb.append(sch.timeSet?"1":"0");
		sb.append(",");
		sb.append(sch.alarmSet?"1":"0");
		sb.append(")");
		
		Log.d("toInsertSql", sb.toString());
		return sb.toString();
	}
	
	//Ln42:
	public String toUpdateSql(RcActivity father)
	{		
		Schedule sch = father.schTemp;		
		StringBuffer sb = new StringBuffer();
		
		//使用schedule本身的SN, 不需更新SN.
		//int preSn=sn;
		//sb.append("update schedule set sn=");
		//sn=getSNFromPrefs(father);
		sb.append("update schedule set date1='");
		sb.append(sch.date1);
		sb.append("',time1='");
		sb.append(sch.time1);
		sb.append("',date2='");
		sb.append(sch.date2);
		sb.append("',time2='");
		sb.append(sch.time2);
		sb.append("',title='");
		sb.append(sch.title);
		sb.append("',note='");
		sb.append(sch.note);
		sb.append("',type='");
		sb.append(sch.type);
		sb.append("',timeSet=");
		sb.append(sch.timeSet?"1":"0");
		sb.append(",alarmSet=");
		sb.append(sch.alarmSet?"1":"0");
		sb.append(" where sn=");
		sb.append(sch.sn);
		
		Log.d("toUpdateSql", sb.toString());
		return sb.toString();
	}
	
	private String getNowDateString() {
		Date aDate= new Date();		
		return new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(aDate);		
	}
	
	private String getNowTimeString() {
		Date aDate= new Date();		
		return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(aDate);		
	}

	
}
