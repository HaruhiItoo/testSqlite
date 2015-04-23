package com.example.m04surfaceviewtest;

import java.util.Calendar;

public class Constant {
	final static int DIALOG_SET_SEARCH_RANGE=1;
	final static int DIALOG_SET_DATETIME=2;
	final static int DIALOG_SCH_DEL_CONFIRM=3;
	final static int DIALOG_CHECK=4;
	final static int DIALOG_ALL_DEL_CONFIRM=5;
	final static int DIALOG_ABOUT=6;
	final static int MENU_HELP=1;
	final static int MENU_ABOUT=2;
	
	public static enum WhoCall
	{
		SETTING_ALARM,
		SETTING_DATE,
		SETTING_RANGE,
		NEW,
		EDIT,
		SEARCH_RESULT
	}
	
	public static enum Layout
	{
		WELCOME_VIEW,
		MAIN,
		SETTING,
		TYPE_MANAGER,
		SEARCH,
		SEARCH_RESULT,
		HELP,
		ABOUT
	}
	
	public static String getNowDateString()
	{
		Calendar c = Calendar.getInstance();
		String nowDate=Schedule.toDateString(c.get(Calendar.YEAR), 
				c.get(Calendar.MONTH)+1, 
				c.get(Calendar.DAY_OF_MONTH));
		return nowDate;
	}
	
	public static String getNowTimeString()
	{
		Calendar c = Calendar.getInstance();
		int nowh = c.get(Calendar.HOUR_OF_DAY);
		int nowm=c.get(Calendar.MINUTE);
		String nowTime=(nowh<10?"0"+nowh:""+nowh)+ ":" 
				+ (nowm<10?"0"+nowm:""+nowm);
		return nowTime;
	}
}
