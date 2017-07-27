package com.emodou.home;

//错题回顾主界面
import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouPickAnswer;
import com.emodou.util.Constants;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;

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

public class QusReadReviewActivity extends Activity implements OnClickListener{
		//actionbar 及相关
		private ActionBar actionbar;
		private TextView actionTitle;
		private ImageButton actionReturn;
		private int textsize;
		private ImageButton clock;
		private TextView time;
		
		//界面相关属性
		private String bookid, classid, type;
		private TextView title;
		private String str_page;
		private EmodouPickAnswer pickAnswer;
		
		public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.home_read_qusreview);
	        
	        getParams();
	        
	        setActionbar();
	        
	        init();
		}
		
		public void getParams(){
			
			bookid = getIntent().getExtras().getString("bookid");
			classid = getIntent().getExtras().getString("classid");
			type = getIntent().getExtras().getString("type");
			pickAnswer = (EmodouPickAnswer)getIntent().getSerializableExtra("pickAnswer");
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
	 		
	 		clock = (ImageButton)view.findViewById(R.id.actionbar_read_clock);
	 		clock.setVisibility(View.GONE);
	 		time = (TextView)view.findViewById(R.id.actionbar_read_time);
	 		time.setVisibility(View.GONE);
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
					mBundle.putLong("starttimePass", 0);
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
			mBundle.putLong("starttimePass", 0);
			intent.putExtras(mBundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		super.onBackPressed();
		}
}
