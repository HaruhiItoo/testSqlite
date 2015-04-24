package com.example.m04surfaceviewtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		
		// Test2:
		//setContentView(new MySurfaceView(this));
		
		
		// Test1:
//		DrawTest drawTest = new DrawTest(this);
//		setContentView(drawTest);
	}
}
