package com.example.m04surfaceviewtest;

import java.util.List;

import com.example.m04surfaceviewtest.Constant.Layout;

import android.app.Activity;
import android.graphics.Color;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		setContentView(R.layout.main);		
		curr=Layout.MAIN;
		
		int sel = 0;		
		
		CheckBox bCheck = null;		
				
		bCheck.setEnabled(false);
		//bCheck.setOnClickListener();
		bCheck.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				// go to searching UI.
				showDialog(Constant.DIALOG_CHECK);
				
			}
		});
	
		ListView lv = (ListView)findViewById(R.id.lv);
		lv.setAdapter(new BaseAdapter() {
			List<Schedule> alSch;
			
			@Override
			public int getCount() {				
				return alSch.size();
			}
			
			@Override
			public Object getItem(int position) {
				return alSch.get(position);
			}
			
			@Override
			public long getItemId(int position) {				
				return 0;
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LinearLayout ll = new LinearLayout(RcActivity.this);
				ll.setOrientation(LinearLayout.VERTICAL);
				ll.setPadding(5, 5, 5, 5);
				LinearLayout llUp = new LinearLayout(RcActivity.this);
				llUp.setOrientation(LinearLayout.HORIZONTAL);				
				LinearLayout llDown = new LinearLayout(RcActivity.this);
				llDown.setOrientation(LinearLayout.HORIZONTAL);
				
				TextView tvDate = new TextView(RcActivity.this);				
				tvDate.setText(alSch.get(position).getDate1()+"    ");
				tvDate.setTextSize(17);
				tvDate.setTextColor(Color.parseColor("#129666"));
				llUp.addView(tvDate);
				
				TextView tvTime = new TextView(RcActivity.this);
				tvTime.setText(alSch.get(position).timeForListView());
				tvTime.setTextSize(17);
				tvTime.setTextColor(Color.parseColor("#925301"));
				llUp.addView(tvTime);
				
				if(alSch.get(position).isPassed())
				{
					//ToDo: add R to set colors.
//					tvDate.setTextColor(getResources().getColor(R.color.passedschtext));
//					tvTime.setTextColor(getResources().getColor(R.color.passedschtext));
//					ll.setBackgroundColor(getResources().getColor(R.color.passedschgb));
				}
				
				return null;
			}
		});
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	}

}
