package com.example.m04surfaceviewtest;

import com.example.m04surfaceviewtest.Constant.Layout;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

public class RcActivity extends Activity {
	
	private Layout curr;

	// Override the method of handling messages.
	Handler hd = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			// If msg is 0, go to main UI.
			case 0:
				gotoMain();
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		goToWelcomeView();
	}


	private void goToWelcomeView() {
		MySurfaceView mview = new MySurfaceView(this);
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(mview);
		curr=Layout.WELCOME_VIEW;
		
	}
	
	protected void gotoMain() {
		//ToDo: main layout::
		//setContentView(R.layout.main);
		curr=Layout.MAIN;
		
	}

}
