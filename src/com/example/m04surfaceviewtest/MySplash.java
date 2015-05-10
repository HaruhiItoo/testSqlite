package com.example.m04surfaceviewtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

public class MySplash extends Activity {

	public Handler hd = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case 0:
					Intent openMainAct = new Intent(MySplash.this, RcActivity.class);
					startActivity(openMainAct);
					finish();
					break;
			}			
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
				
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setTheme(R.style.NoTitle);
		
		super.onCreate(savedInstanceState);
		
		MySurfaceView2 mview = new MySurfaceView2(this);
		setContentView(mview);
		
	}

}
