package com.emodou.home;

//阅读做题界面第一个
import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.util.Constants;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.iflytek.cloud.InitListener;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QusReadActivity extends Activity implements OnClickListener{
	
	//actionbar 及相关
	private ActionBar actionbar;
	private TextView actionTitle, actionTime;
	private ImageButton actionReturn;
	private Handler stepTimeHandler;//控制计时
	private Runnable mTicker; //控制计时
	private long startTime = 0;
	private int textsize;
	
	//界面相关属性
	private String bookid, classid, type;
	private Long startTimePass;
	private TextView title;
	private String str_page;
	
	//是否从做作业界面跳转过来
	private String classroomid, workid, itemid, starttime;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_read_qus);
        
        getParams();
        
        setActionbar();
        
        init();
	}
	
	public void getParams(){
		
		bookid = getIntent().getExtras().getString("bookid");
		classid = getIntent().getExtras().getString("classid");
		type = getIntent().getExtras().getString("type");
		startTimePass = getIntent().getExtras().getLong("starttimePass");
		textsize = getIntent().getExtras().getInt("textsize");
		
		//是否从做作业界面跳过来
		classroomid = getIntent().getExtras().getString("classroomid");
		workid = getIntent().getExtras().getString("workid");
		itemid = getIntent().getExtras().getString("itemid");
		starttime = getIntent().getExtras().getString("starttime");
	}
	
	public void setActionbar(){
 		
 		actionbar = getActionBar();
 		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
 		actionbar.setCustomView(R.layout.actionbar_read);
 		
 		View view = actionbar.getCustomView();
 		
 		//actionbar属性获取操作
 		actionTitle = (TextView)view.findViewById(R.id.actionbar_read_title);	 		
 		actionReturn = (ImageButton)view.findViewById(R.id.actionbar_read_return);
 		actionReturn.setOnClickListener(this);
 		actionTime = (TextView)view.findViewById(R.id.actionbar_read_time);
 				
 		//开启计时功能
 		actionTime.setText(showTimeCount(startTimePass));
		stepTimeHandler = new Handler();
		startTime = System.currentTimeMillis()-startTimePass;
		mTicker = new Runnable() {
			public void run() {
				String content = showTimeCount(System.currentTimeMillis()- startTime);
				actionTime.setText(content);

				long now = SystemClock.uptimeMillis();
				long next = now + (1000 - now % 1000);
				stepTimeHandler.postAtTime(mTicker, next);
			}
		};
		// 启动计时线程，定时更新
		mTicker.run();

	}
	
	public String showTimeCount(long time) {
		if (time >= 360000000) {
			return "00:00:00";
		}
		String timeCount = "";
		long hourc = time / 3600000;
		String hour = "0" + hourc;
		hour = hour.substring(hour.length() - 2, hour.length());

		long minuec = (time - hourc * 3600000) / (60000);
		String minue = "0" + minuec;
		minue = minue.substring(minue.length() - 2, minue.length());

		long secc = (time - hourc * 3600000 - minuec * 60000) / 1000;
		String sec = "0" + secc;
		sec = sec.substring(sec.length() - 2, sec.length());
		if(time < 3600000){
			timeCount = minue + ":" + sec;
		}else if(time >= 3600000){
			timeCount = hour +":" + minue + ":" + sec;
		}
		return timeCount;
	}
	
	public void init() {
		
		str_page = ValidateUtils.readFromFile(this, Constants.LOCAL_START+type+"/"+classid+Constants.LOCAL_JSON1);
		
		try{
			//解析文章文件 获取 相应的信息
			JSONObject dataJson = new JSONObject(str_page);	
			actionTitle.setText(dataJson.getString("title"));
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
					
					
			

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
			case R.id.actionbar_read_return:
				Intent intent = new Intent(this, ReadActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putString("type", type);
				mBundle.putString("classid", classid);
				mBundle.putString("bookid", bookid);
				mBundle.putLong("starttimePass", System.currentTimeMillis()-startTime);
				if(classroomid != null){
					mBundle.putString("classroomid", classroomid);
					mBundle.putString("workid", workid);
					mBundle.putString("itemid", itemid);
					mBundle.putString("starttime", starttime);
				}
				intent.putExtras(mBundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;

			default:
				break;
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, ReadActivity.class);
		Bundle mBundle = new Bundle();
		mBundle.putString("type", type);
		mBundle.putString("classid", classid);
		mBundle.putString("bookid", bookid);
		mBundle.putLong("starttimePass", System.currentTimeMillis()-startTime);
		if(classroomid != null){
			mBundle.putString("classroomid", classroomid);
			mBundle.putString("workid", workid);
			mBundle.putString("itemid", itemid);
			mBundle.putString("starttime", starttime);
		}
		intent.putExtras(mBundle);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		super.onBackPressed();
		
	}
	
}
