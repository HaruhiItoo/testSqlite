package com.example.m04surfaceviewtest;

// Note!! To impoart DBUtil's static methods.
import static com.example.m04surfaceviewtest.DBUtil.*;
import static com.example.m04surfaceviewtest.Constant.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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
	private ImageButton bSearch;
	
	//Setting UI
	private EditText etTitle;
	private EditText etNote;	
	private Spinner spType;
	private TextView tvDate;
	private TextView tvTime;
	private TextView tvAlarm;
	
	//Type management UI
	private EditText etNew;
	
	private Layout curr;	
	private WhoCall wcNewOrEdit;
	private WhoCall wcSetTimeOrAlarm;
	private Dialog dialogSetRange;
	private int sel = 0;
	
	public Schedule schTemp;

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
		
		//variables:
		bNew = (ImageButton)findViewById(R.id.ibAddSch);
		bCheck = (ImageButton)findViewById(R.id.ibChkSch);
		bEdit =(ImageButton)findViewById(R.id.ibEditSch);
		bDel =(ImageButton)findViewById(R.id.ibDelSch);
		bDelAll =(ImageButton)findViewById(R.id.ibDelAllSch);
		bSearch = (ImageButton)findViewById(R.id.ibSearchSch);
		
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
		
		final ListView lv = (ListView)findViewById(R.id.lv);
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
				
				TextView atvDate = new TextView(RcActivity.this);				
				atvDate.setText(alSch.get(position).getDate1()+"    ");
				atvDate.setTextSize(17);
				atvDate.setTextColor(Color.parseColor("#129666"));
				llUp.addView(atvDate);
				
				tvTime = new TextView(RcActivity.this);
				tvTime.setText(alSch.get(position).timeForListView());
				tvTime.setTextSize(17);
				tvTime.setTextColor(Color.parseColor("#925301"));
				llUp.addView(tvTime);
				
				// Customize color for out-of-date schedule.
				if(alSch.get(position).isPassed())
				{				
					atvDate.setTextColor(getResources().getColor(R.color.passedschtext));
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
				//Note!!! 通知ListView's adapter, 資料有更新.
				//  在getView(...)會重繪ListView, 在alIsSelected被標示選取的, 會改底色.
				//((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
				lv.invalidateViews();
				
				// Diff between notifyDataSetChanged() and invalidateViews():
				// http://stackoverflow.com/questions/10676720/is-there-any-difference-between-listview-invalidateviews-and-adapter-notify
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
				showDialog(DIALOG_SCH_DEL_CONFIRM);				
			}
		});
		
		bDelAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_ALL_DEL_CONFIRM);				
			}
		});
		
		bSearch.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				gotoSearch();
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
		tvDate = (TextView)findViewById(R.id.tvDate);
		tvTime = (TextView)findViewById(R.id.tvTime);
		tvAlarm = (TextView)findViewById(R.id.tvAlarm);
				
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
		
		//spType.setSelection(sel);
		spType.setSelection(alType.indexOf(schTemp.getType()));
		
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
				
				wcSetTimeOrAlarm=WhoCall.SETTING_DATE;
				showDialog(DIALOG_SET_DATETIME);
			}
		});
		
		Button bSetAlarm=(Button)findViewById(R.id.bSetAlarm);
		bSetAlarm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				schTemp.setTitle(etTitle.getText().toString());
				schTemp.setNote(etNote.getText().toString());
				sel=spType.getSelectedItemPosition();
				
				wcSetTimeOrAlarm=WhoCall.SETTING_ALARM;
				showDialog(DIALOG_SET_DATETIME);
			}
		});
		
		//Ln:49
		Button bDone=(Button)findViewById(R.id.bOk);
		bDone.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(schTemp.isPassed())
				{
					Toast.makeText(RcActivity.this, 
							"Fail to create: out-of-date", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(schTemp.getAlarmSet())
				{
					if(schTemp.getDate1().compareTo(schTemp.getDate2())<0
						|| (schTemp.getTimeSet() 
							&& schTemp.getDate1().compareTo(schTemp.getDate2())==0 
							&& schTemp.getTime1().compareTo(schTemp.getTime1())<0))
					{
						Toast.makeText(RcActivity.this, 
								"Fail to create: alarm out-of-date", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				
				String title=etTitle.getText().toString().trim();
				if(title.equals(""))
				{
					title="noTitle";
				}
				schTemp.setTitle(title);
				String note=etNote.getText().toString();
				schTemp.setNote(note);
				String type=(String)spType.getSelectedItem();
				schTemp.setType(type);
				if(wcNewOrEdit==WhoCall.NEW)
				{
					insertSchedule(RcActivity.this);
				}
				else if(wcNewOrEdit==WhoCall.EDIT)
				{
					updateSchedule(RcActivity.this);
				}
				
				gotoMain();
			}
		});
		
		Button bCancel=(Button)findViewById(R.id.bCancel);
		bCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				gotoMain();				
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

	//Ln:30
	public void gotoSearch()
	{
		setContentView(R.layout.search);
		curr=Layout.SEARCH;
		
		//variables...
		TextView tvFrom = (TextView)findViewById(R.id.tvFrom);
		TextView tvTo = (TextView)findViewById(R.id.tvTo);
		CheckBox cbDateRange = (CheckBox)findViewById(R.id.cbDateRange);
		final Button bChange = (Button)findViewById(R.id.bChange);
		CheckBox cbAllType = (CheckBox)findViewById(R.id.cbAllType);
		final ListView lv = (ListView)findViewById(R.id.lv);
		String rangeFrom = "----/--/--";
		String rangeTo = "----/--/--";		
		
		tvFrom.setText(rangeFrom);
		tvTo.setText(rangeTo);
		
		final ArrayList<String> type=getAllType(RcActivity.this);		
		alSelectedType.clear();
		for(int i=0;i<type.size();i++)
		{
			alSelectedType.add(false);
		}
		
		cbDateRange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				bChange.setEnabled(isChecked);				
			}
		});
		
		cbAllType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				for(int i=0;i<type.size();i++)
				{
					alSelectedType.set(i, isChecked);
				}
				// Refresh ListView.
				lv.invalidateViews();
			}
		});
		
		bChange.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_SET_SEARCH_RANGE);				
			}
		});
		
		//Ln:33
		lv.setAdapter(new BaseAdapter() {
			@Override
			public int getCount() {
				return type.size();
			}
			
			@Override
			public Object getItem(int position) {
				return type.get(position);
			}
			
			@Override
			public long getItemId(int position) {
				return 0;
			}
			
			// Note!! 變數"position"設為"final", 因為會給listener使用.
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				LinearLayout ll=new LinearLayout(RcActivity.this);
				ll.setOrientation(LinearLayout.HORIZONTAL);
				ll.setGravity(Gravity.CENTER_VERTICAL);
				
				LinearLayout llin=new LinearLayout(RcActivity.this);
				llin.setPadding(20, 0, 0, 0);
				ll.addView(llin);
				
				final CheckBox cb=new CheckBox(RcActivity.this);				
				//cb.setButtonDrawable(R.drawable.uncheckbox);
				cb.setButtonDrawable(alSelectedType.get(position)?R.drawable.checkbox:R.drawable.uncheckbox);
				cb.setChecked(alSelectedType.get(position));
				cb.setText(type.get(position));
				cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						alSelectedType.set(position, isChecked);
						buttonView.setButtonDrawable(isChecked?R.drawable.checkbox:R.drawable.uncheckbox);
					}
				});								
				llin.addView(cb);
				
				TextView tv=new TextView(RcActivity.this);
				tv.setTag(type.get(position));
				tv.setTextSize(17);
				tv.setTextColor(getResources().getColor(R.color.black));
				ll.addView(tv);
				return ll;
			}			
		});
		
		//ToDo:
		//bOK.click()
		//bCancel.click()
	}
	
	//Ln:33
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		Dialog dialog=null;
		AlertDialog.Builder b;
		switch(id)
		{
			case DIALOG_SET_SEARCH_RANGE:
				// ToDo:
//				b = new AlertDialog.Builder(RcActivity.this);
//				b.setTitle("Search Range");
//				b.setItems(new String[]{"Red", "Green", "Blue"}, null);
//				dialogSetRange=b.create();
				dialog=dialogSetRange;			
				break;
			case DIALOG_SET_DATETIME:				
				Calendar c = Calendar.getInstance();
				int y = c.get(Calendar.YEAR);
				int m = c.get(Calendar.MONTH);
				int d = c.get(Calendar.DAY_OF_MONTH);
				
				DatePickerDialog datepicker = new DatePickerDialog(this, 
						new DatePickerDialog.OnDateSetListener() {
							
							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear,
									int dayOfMonth) {
								if(wcSetTimeOrAlarm==WhoCall.SETTING_DATE)
								{
									tvDate.setText(Schedule.toDateString(year, monthOfYear+1, dayOfMonth));
									schTemp.setDate1(Schedule.toDateString(year, monthOfYear+1, dayOfMonth));
								}
								else
								{
									tvAlarm.setText(Schedule.toDateString(year, monthOfYear+1, dayOfMonth));
									schTemp.setDate2(Schedule.toDateString(year, monthOfYear+1, dayOfMonth));									
								}
							}
						}, y, m, d); 
								
				dialog=datepicker;			
				break;	
			case DIALOG_SCH_DEL_CONFIRM:
				b = new AlertDialog.Builder(RcActivity.this);
				b.setTitle("Delete Schedule")
				.setMessage("Do you sure to delete schedule [" + schTemp.getTitle() +"]?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {			
						deleteSchedule(RcActivity.this);
						// To refresh the schedule list.
						gotoMain();
					}
				})
				.setNegativeButton("No", null);		
				dialog=b.create();				
				break;
			case DIALOG_ALL_DEL_CONFIRM:
				b = new AlertDialog.Builder(RcActivity.this);
				b.setTitle("Delete All Schedule")
				.setMessage("Do you sure to delete ALL schedules?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {			
						deleteAllSchedule(RcActivity.this);
						// To refresh the schedule list.
						gotoMain();
					}
				})
				.setNegativeButton("No", null);		
				dialog=b.create();	
				break;
		}
		
		return dialog;
	}
	
	//Ln:38
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			switch(curr)
			{
				case MAIN:
					// 建立alertDialog以確認是否離開程式.
					(new AlertDialog.Builder(this)
					.setMessage("Exit app?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							System.exit(0);							
						}
					})
					.setNegativeButton("No", null)).show();					
					break;
				case SETTING:
					gotoMain();
					break;
				case TYPE_MANAGER:
					gotoSetting();
					break;
				case SEARCH:
					gotoMain();
					break;
				case SEARCH_RESULT:
					gotoSearch();
					break;
				case HELP:
					gotoMain();
					break;
				case ABOUT:
					gotoMain();
					break;			
			} //End: switch
			return true;
		}
		
		return false;
	}
	
	//Ln:40
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return super.onMenuItemSelected(featureId, item);
	}

	//Ln:49
	public String[] splitYMD(String ss)
	{
		return ss.split("/");
	}
}
