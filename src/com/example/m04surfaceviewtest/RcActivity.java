package com.example.m04surfaceviewtest;

// Note!! To impoart DBUtil's static methods.
import static com.example.m04surfaceviewtest.DBUtil.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.m04surfaceviewtest.Constant.Layout;
import com.example.m04surfaceviewtest.Constant.WhoCall;
import com.example.m04surfaceviewtest.R.layout;

import android.app.Activity;
import android.database.DataSetObserver;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RcActivity extends Activity {
	
	//Main UI
	private ImageButton bNew;
	private ImageButton bCheck;
	private ImageButton bEdit;
	private ImageButton bDel;
	private ImageButton bDelAll;
	
	//Setting UI
	private EditText etTitle;
	private EditText etNote;
	private Spinner spType;
	
	//Type management UI
	private EditText etNew;
	
	private Layout curr;	
	private WhoCall wcNewOrEdit;
	private int sel = 0;
	private Schedule schTemp;

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
	

	//Ln:27
	public void gotoMain() {
		
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		setContentView(R.layout.main);		
		curr=Layout.MAIN;
		sel=0;
		
		//variabes:
		bNew = (ImageButton)findViewById(R.id.ibAddSch);
		bCheck = (ImageButton)findViewById(R.id.ibChkSch);
		bEdit =(ImageButton)findViewById(R.id.ibEditSch);
		bDel =(ImageButton)findViewById(R.id.ibDelSch);
		bDelAll =(ImageButton)findViewById(R.id.ibDelAllSch);
		
		// Ln9: UI settings.
		bCheck.setEnabled(false);
		bEdit.setEnabled(false);
		bDel.setEnabled(false);
		alSch.clear();
		
		// Read data from DB. 		
		loadSchedule(this);
		loadType(this);
		
		bDelAll.setEnabled((alSch.size() != 0));
		alIsSelected.clear();
		for(int i=0;i<alSch.size();i++)
		{
			alIsSelected.add(false);
		}
		
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
				bCheck.setEnabled(true);
				bEdit.setEnabled(true);
				bDel.setEnabled(true);
				schTemp=alSch.get(position);
				// Clear all selection, then set the selected item to true.
				for(int i=0; i<alIsSelected.size(); i++)
				{
					alIsSelected.set(i, false);
				}
				
				alIsSelected.set(position, true);
			}
			
		});		
		
		//Ln40:
		bNew.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar c =Calendar.getInstance();
				int t1=c.get(Calendar.YEAR);
				int t2=c.get(Calendar.MONTH)+1;
				int t3=c.get(Calendar.DAY_OF_MONTH);
				schTemp=new Schedule(t1, t2, t3);
				wcNewOrEdit=WhoCall.NEW;
				gotoSetting();
			}
		});
		
		bEdit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				wcNewOrEdit=WhoCall.EDIT;
				gotoSetting();
				
			}
		});
		
		bDel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//showDialog(DIALOG_SCH_DEL_CONFIRM);
				
			}
		});
		
		
	}

	//Ln:28
	public void gotoSetting()
	{
		setContentView(R.layout.newschedule);
		curr=Layout.SETTING;
		
		TextView tvTitle=(TextView)findViewById(R.id.tvnewscheduleTitle);
		if(wcNewOrEdit==WhoCall.NEW)
		{
			tvTitle.setText("New Schedule");			
		}
		else if (wcNewOrEdit==WhoCall.EDIT)
		{
			tvTitle.setText("Modify Schedule");
		}
		
		// variables...
		etTitle = (EditText)findViewById(R.id.etTitle);
		etNote = (EditText)findViewById(R.id.etNote);
		TextView tvDate = (TextView)findViewById(R.id.tvDate);
		TextView tvTime = (TextView)findViewById(R.id.tvTime);
		TextView tvAlarm = (TextView)findViewById(R.id.tvAlarm);
				
		etTitle.setText(schTemp.getTitle());
		etNote.setText(schTemp.getNote());
		tvDate.setText(schTemp.getDate1());
		tvTime.setText(schTemp.getTimeSet()?schTemp.getTime1():"no time");
		tvAlarm.setText(schTemp.getAlarmSet()?schTemp.getDate2()+"    "+schTemp.getTime2():"no alarm");
		
		spType = (Spinner)findViewById(R.id.spType);
		//Ln16:
		spType.setAdapter(new BaseAdapter() {
			@Override
			public int getCount() {
				return alType.size();
			}
			
			@Override
			public Object getItem(int position) {				
				return alType.get(position);
			}
			
			@Override
			public long getItemId(int position) {				
				return 0;
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {				
				LinearLayout ll = new LinearLayout(RcActivity.this);
				ll.setOrientation(LinearLayout.HORIZONTAL);
				TextView tv = new TextView(RcActivity.this);
				tv.setText(alType.get(position));
				tv.setTextSize(17);
				tv.setTextColor(getResources().getColor(R.color.black));
				return tv;
			}			
		});
		
		spType.setSelection(sel);
		
		// button listener
		Button bNewType = (Button)findViewById(R.id.bNewType);		
		bNewType.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				schTemp.setTitle(etTitle.getText().toString());
				schTemp.setNote(etNote.getText().toString());
				sel=spType.getSelectedItemPosition();
				gotoTypeManager();				
			}
		});
		
		Button bSetDate=(Button)findViewById(R.id.bSetDate);
		bSetDate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				schTemp.setTitle(etTitle.getText().toString());
				schTemp.setNote(etNote.getText().toString());
				sel=spType.getSelectedItemPosition();
				
				//ToDo:
//				wcSetTimeOrAlarm=WhoCall.SETTING_DATE;
//				showDialog(DIALOG_SET_DATETIME);
			}
		});
	}
	
	//Ln:29
	public void gotoTypeManager()
	{
		setContentView(R.layout.typemanager);
		curr=Layout.TYPE_MANAGER;
		
		//variables
		
		Button bBack=(Button)findViewById(R.id.bBack);		
		bBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				gotoSetting();
				
			}
		});
		
		ListView lvType=(ListView)findViewById(R.id.lvType);
		lvType.setAdapter(new BaseAdapter() {
			@Override
			public int getCount() {
				return alType.size();
			}
			
			@Override
			public Object getItem(int position) {
				return alType.get(position);
			}
			
			@Override
			public long getItemId(int position) {
				return 0;
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView tv = new TextView(RcActivity.this);
				tv.setText(alType.get(position));
				tv.setTextSize(17);
				tv.setTextColor(getResources().getColor(R.color.black));
				return tv;
			}			
		});
	
		Button bNew=(Button)findViewById(R.id.bNewType);
		etNew=(EditText)findViewById(R.id.etNew);
		bNew.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String newType=etNew.getText().toString();
				if(newType.equals(""))
				{
					Toast.makeText(RcActivity.this, "Type cannot be empty.", 
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				insertType(RcActivity.this, newType);
				gotoTypeManager();
			}
		});
	}
}
