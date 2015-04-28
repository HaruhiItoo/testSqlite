package com.example.m04surfaceviewtest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.*;
import android.content.SharedPreferences.Editor;
import android.database.*;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;

public class DBUtil {
	static SQLiteDatabase sld;
	//static String MY_DB_PATH = "/data/data/com.example.m04surfaceviewtest/myDb";
	static String MY_DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/com.example.m04surfaceviewtest/myDb";
	
	public static List<String> alType = new ArrayList<String>();	
	public static List<Schedule> alSch = new ArrayList<Schedule>();		
	//public static Map<Integer, Boolean> alIsSelected = new HashMap<Integer, Boolean>();
	public static List<Boolean> alIsSelected = new ArrayList<Boolean>();
	
	//Ln:7
	public static void loadType(RcActivity father)
	{
		try
		{
			sld = SQLiteDatabase.openDatabase(MY_DB_PATH, null, 
					SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY);
			String sql = "create table if not exists type(tno integer primary key,  tname varchar2(20)); ";
			sld.execSQL(sql);
			Cursor cursor = sld.query("type", null, null, null, null, null, "tno");
			int count = cursor.getCount();
			if(count == 0)
			{
				for(int i=0; i<father.defaultType.length; i++)
				{
					sql = "insert into type values(" + i + ",'" 
							+ father.defaultType[i]							
							+ "')";
					sld.execSQL(sql);
				}
				
				cursor = sld.query("type", null, null, null, null, null, "tno");
				count = cursor.getCount();
			}
			
			alType.clear();
			while(cursor.moveToNext())
			{
				alType.add(cursor.getString(1));
			}
			
			sld.close();
			cursor.close();
			
		}catch (Exception e)
		{
			Toast.makeText(father, "Create DB fail: " + e.toString(), Toast.LENGTH_LONG).show();
		}
		
		
	}
	
	public static boolean insertType(RcActivity father, String newType)
	{
		Cursor cursor=null;
		boolean duplicate = false;
		try
		{
			sld = SQLiteDatabase.openDatabase(MY_DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
			cursor = sld.query("type", null, null, null, null, null, "tno");
			
			alType.clear();
			while(cursor.moveToNext())
			{
				// Check whether the type is duplicate. 
				if(newType.equals(cursor.getString(1)))
					duplicate = true;
				
				alType.add(cursor.getString(1));
			}
			
			if(!duplicate)
			{
				// Actual a new type.
				alType.add(newType);
				
				// Delete all types from DB, and then add them back.
				String sql = "delete from type";				
				sld.execSQL(sql);
				for(int i=0; i<alType.size(); i++)
				{
					sql = "insert into type values (" + i + ", '" + alType.get(i) + "')";
					sld.execSQL(sql);					
				}
				
				Toast.makeText(father, "Success to add new type: " + newType, Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(father, "Duplicate type: " + newType, Toast.LENGTH_SHORT).show();
			}
			
		}catch(Exception e)
		{
			Toast.makeText(father, "Fail to update types: " + e.toString(), Toast.LENGTH_SHORT).show();
			return false;
		}finally
		{
			// Note!! To close cursor and DB.
			cursor.close();
			sld.close();
		}
		
		return true;
	}
	
	// Line:14
	public static void deleteType(RcActivity father, String s)
	{
		try
		{
			sld = SQLiteDatabase.openDatabase(MY_DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
			String sql = "delete from type where tname='" + s + "'";
			sld.execSQL(sql);
			Toast.makeText(father, "Success to delete type.", Toast.LENGTH_SHORT).show();
		}catch (Exception e)
		{
			Toast.makeText(father, "Faile to delete type: " + e.toString(), Toast.LENGTH_SHORT).show();
		}finally
		{
			sld.close();
		}
		
	}
	
	//Ln:19
	public static void loadSchedule(RcActivity father)
	{
		try
		{		
			// Create a folder.
			File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/com.example.m04surfaceviewtest");
			if(!f.exists())
			{
				f.mkdir();
			}
			
			sld=SQLiteDatabase.openDatabase(MY_DB_PATH, null, 
					SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY);
			String sql = "create table if not exists schedule" 
					+"(sn integer primary key, date1 char(10), time1 char(5),"
					+" date2 char(10), time2 char(5), title varchar2(40),"
					+" note varchar2(120), type varchar2(20)," 
					+" timeset boolean, alarmset boolean)";
			sld.execSQL(sql);
			
			// Order by time.
			Cursor cursor =sld.query("schedule", null, null, null, null, null, 
					"date1 desc,time1 desc");
			while(cursor.moveToNext())
			{
				int sn = cursor.getInt(0);
				String date1=cursor.getString(1);
				String time1=cursor.getString(2);
				String date2=cursor.getString(3);
				String time2=cursor.getString(4);
				String title=cursor.getString(5);
				String note=cursor.getString(6);
				String type=cursor.getString(7);
				String timeSet=cursor.getString(8);
				String alarmSet=cursor.getString(9);
				Schedule schTemp=new Schedule(sn, date1, time1, date2, time2, title, 
						note, type, timeSet, alarmSet);
				alSch.add(schTemp);
				Log.d("schdata", ""+cursor.getPosition()+":sn="+sn+":"
				+date1+","+time1+","+date2+","+timeSet);
			}
			
			sld.close();
			cursor.close();
			
		}catch(Exception e)
		{
			Toast.makeText(father, "Fail to load schedule: "+e.toString(), 
					Toast.LENGTH_LONG).show();
		}
	}
	
	//Ln:22
	public static void insertSchedule(RcActivity father)
	{
		try
		{
			sld=SQLiteDatabase.openDatabase(MY_DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
			String sql =father.schTemp.toInsertSql(father);
			sld.execSQL(sql);
			sld.close();
		}catch(Exception e)
		{
			Toast.makeText(father, 
					"Fail to insert schedule: " + e.toString(), Toast.LENGTH_LONG).show();
		}
		
	}
	
	//Ln:25
	public static void updateSchedule(RcActivity father)
	{
		try
		{
			sld=SQLiteDatabase.openDatabase(MY_DB_PATH, null, 
					SQLiteDatabase.OPEN_READWRITE);
			String sql=father.schTemp.toUpdateSql(father);
			sld.execSQL(sql);
			sld.close();
		}catch(Exception e)
		{
			Toast.makeText(father, 
					"Fail to update schedule: " + e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	//Ln:28
	public static void deleteSchedule(RcActivity father)
	{
		try
		{
			sld=SQLiteDatabase.openDatabase(MY_DB_PATH, null, 
					SQLiteDatabase.OPEN_READWRITE);
			int sn=father.schTemp.getSn();
			String sql="delete from schedule where sn="+sn;
			sld.execSQL(sql);
			sld.close();
			Toast.makeText(father, "Success to delete.", Toast.LENGTH_SHORT).show();
		}catch(Exception e)
		{
			Toast.makeText(father, 
					"Fail to delete schedule: " + e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	public static void deleteAllSchedule(RcActivity father)
	{
		try
		{
			sld=SQLiteDatabase.openDatabase(MY_DB_PATH, null, 
					SQLiteDatabase.OPEN_READWRITE);
			String sql="delete from schedule";
			sld.execSQL(sql);
			sld.close();
			Toast.makeText(father, "Success to delete all schedules.", Toast.LENGTH_SHORT).show();
		}catch(Exception e)
		{
			Toast.makeText(father, 
					"Fail to delete all schedules: " + e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	//Ln:37
	public static int getSNFromPrefs(Activity father)
	{
		SharedPreferences sp = father.getSharedPreferences("SN", Context.MODE_PRIVATE);
		// Get SN from 2 because there have been a schedule in DB(test).
		int sn = sp.getInt("sn", 2);
		
		Editor editor = sp.edit();
		editor.putInt("sn", sn+1);
		editor.commit();
		
		return sn;
	}

}
