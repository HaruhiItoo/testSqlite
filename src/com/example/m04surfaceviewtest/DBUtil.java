package com.example.m04surfaceviewtest;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class DBUtil {
	static SQLiteDatabase sld;
	static String MY_DB_PATH = "/data/data/com.example.m04surfaceviewtest/myDb"; 
	
	private static List<String> alType;
	
	public static void loadType(Activity father)
	{
		try
		{
			sld = SQLiteDatabase.openDatabase(MY_DB_PATH, null, 
					SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY);
			String sql = "create table if not exists type(tno integer primary key,  tname varchar2(20); ";
			sld.execSQL(sql);
			Cursor cursor = sld.query("type", null, null, null, null, null, "tno");
			int count = cursor.getCount();
			if(count == 0)
			{
				//for(int i=0; i<father.default)
				for(int i =0; i< 5; i++)
				{
					sql = "insert into type values(" + i + ",'" 
							//+ father.defaultType[i]
							+ "type01"
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
	
	public static boolean insertType(Activity father, String newType)
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
			cursor.close();
			sld.close();
		}
		
		return true;
	}
	
	public static int getSNFromPrefs(Activity father)
	{
		SharedPreferences sp = father.getSharedPreferences("SN", Context.MODE_PRIVATE);
		int sn = sp.getInt("sn", 0);
		
		Editor editor = sp.edit();
		editor.putInt("sn", sn+1);
		editor.commit();
		
		return sn;
	}

}
