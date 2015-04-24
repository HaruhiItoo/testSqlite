package com.example.m04surfaceviewtest;

// Note!! To impoart DBUtil's static methods.
import static com.example.m04surfaceviewtest.DBUtil.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.m04surfaceviewtest.Constant.Layout;

import android.app.Activity;
import android.graphics.Color;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RcActivity extends Activity {
	
	private Layout curr;	

	public String[] defaultType = {"Private", "Work", "ToBuy"};
	
	// Override the method of handling messages.
	public Handler hd = new Handler(){

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
//		CheckBox bCheck = (CheckBox)findViewById(R.id.check);				
		//final Map<Integer, Boolean> alIsSelected = new HashMap<Integer, Boolean>();
		
		// Ln9: UI settings.
//		bCheck.setEnabled(false);
//		bEdit.setEnabled(false);
//		bDel.setEnabled(false);
		alSch.clear();
		
		// Read data from DB. 		
		loadSchedule(this);
		loadType(this);
		

//		bCheck.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {	
//				// go to searching UI.
//				showDialog(Constant.DIALOG_CHECK);
//				
//			}
//		});			
		
		ListView lv = (ListView)findViewById(R.id.lv);
		lv.setAdapter(new BaseAdapter() {
			
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
				ll.setPadding(10, 10, 10, 10);
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
				
				// Customize color for out-of-date schedule.
				if(alSch.get(position).isPassed())
				{				
					tvDate.setTextColor(getResources().getColor(R.color.passedschtext));
					tvTime.setTextColor(getResources().getColor(R.color.passedschtext));
					ll.setBackgroundColor(getResources().getColor(R.color.passedschgb));
				}
				
				// Set selected item's bg color.
				if(alIsSelected.size()>0 && alIsSelected.get(position))
				{
					ll.setBackgroundColor(getResources().getColor(R.color.selectedsch));
				}
				
				// Ln41: Draw alarm.
				if(alSch.get(position).getAlarmSet())
				{
					ImageView iv = new ImageView(RcActivity.this);
					iv.setImageDrawable(getResources().getDrawable(R.drawable.alarm));
					iv.setLayoutParams(new LayoutParams(20, 20));
					llUp.addView(iv);
				}
				
				// Ln47: Show schedule text.
				TextView tvType = new TextView(RcActivity.this);
				tvType.setText(alSch.get(position).typeForListView());
				tvType.setTextSize(17);
				tvType.setTextColor(Color.parseColor("#b20000"));				
				llDown.addView(tvType);
						
				// Ln52: Show title.
				TextView tvTitle = new TextView(RcActivity.this);
				tvTitle.setText(alSch.get(position).getTitle());				
				tvTitle.setTextSize(17);
				tvTitle.setTextColor(Color.parseColor("#000000"));	
				llDown.addView(tvTitle);
				
				ll.addView(llUp);
				ll.addView(llDown);
				
				return ll;
			}
		});
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Schedule schTemp;
//				bCheck.setEnabled(true);
//				bEdit.setEnabled(true);
//				bDel.setEnabled(true);
				schTemp=alSch.get(position);
				// Clear all selection, then set the selected item to true.
				for(int i=0; i<alIsSelected.size(); i++)
				{
					alIsSelected.put(i, false);
				}
				
				alIsSelected.put(position, true);
			}
			
		});		
	}

}
