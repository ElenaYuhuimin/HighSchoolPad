package com.emodou.home;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;

import com.emodou.myclass.WorkDetailActivity;
import com.emodou.util.Constants;
import com.emodou.util.ValidateUtils;
import com.emodou.domain.EmodouBook;
import com.emodou.domain.EmodouClassManager;
import com.emodou.domain.EmodouReadText;
import com.emodou.domain.EmodouUserInfo;
import com.emodou.util.TextPage;
import com.example.emodou.HomeActivity;
import com.example.emodou.MainActivity;
import com.example.emodou.R;
import com.example.emodou.R.color;
import com.example.emodou.R.id;
import com.example.emodou.R.layout;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

public class ReadActivity extends Activity implements OnClickListener{
	
	//actionbar及其相关
	private ActionBar actionbar;
	private TextView actionTitle, actionTime;
	private ImageButton actionReturn;
	
	//界面其他属性
	String str_title, str_page, classid, bookid, type, userid, ticket;
	ListView read_rl_page;
	private List<EmodouReadText> textList;
	private ItemAdapter readAdapter;
	private ListView lvRead;
	private ImageButton read_han_unclick, read_han_click;
	//private ImageButton read_timing_unclick, read_timing_click;
	//private TextView read_timetext;
	private Handler stepTimeHandler;//控制计时
	private Runnable mTicker; //控制计时
	long startTime = 0;
	private Long starttimePass;
	private static int textsize;
	
	//是否从做作业界面跳转过来
	private String classroomid, workid, itemid, starttime;
	
	//做题界面
	//private SlidingDrawer sDrawer;
	//private ImageButton im_que;
	private Boolean flag=false;
	//private SlidingMenu mSlidingMenu;
	//private android.app.FragmentTransaction fragmentTransaction;
	//private LinearLayout linearLayout;
	private ImageButton img_qus;
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_read);
        textList = new ArrayList<EmodouReadText>();
        
        //获得courselist界面传过来的参数
        bookid = getIntent().getExtras().getString("bookid");
		classid = getIntent().getExtras().getString("classid");
		type = getIntent().getExtras().getString("type");
		starttimePass = getIntent().getExtras().getLong("starttimePass");
		//是否从做作业界面跳过来
		classroomid = getIntent().getExtras().getString("classroomid");
		workid = getIntent().getExtras().getString("workid");
		itemid = getIntent().getExtras().getString("itemid");
		starttime = getIntent().getExtras().getString("starttime");
		
        setActionbar();
        //初始化
        init();
        
        //setSlidingMenu();
        
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
 		actionTime.setText("00:00");
		stepTimeHandler = new Handler();
		
		//若为空，则是从courselist界面传过来的
		if(starttimePass==null){
			startTime = System.currentTimeMillis();
		}else{
			startTime = System.currentTimeMillis()-starttimePass;
		}
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
		Toast.makeText(ReadActivity.this, "计时开始", 0).show();
		mTicker.run();
 		
// 		DbUtils dbUtils = DbUtils.create(this);
// 		EmodouBook book1 = null;
// 		try{
// 			book1 = dbUtils.findFirst(Selector.from(EmodouBook.class).where(
//					"bookid", "=", bookid));
// 			if(book1!=null){
// 				//需要根据传过来的参数更改颜色
// 		 		if(Constants.EMODOU_TYPE_LISTEN.equals(type)){
// 		 			rl_color.setBackgroundColor(getResources().getColor(R.color.actionbar_listen));
// 		 		}else if(Constants.EMODOU_TYPE_READ.equals(type)){
// 		 			rl_color.setBackgroundColor(getResources().getColor(R.color.actionbar_read));
// 		 		}else if(Constants.EMODOU_TYPE_SPEAK.equals(type)){
// 		 			rl_color.setBackgroundColor(getResources().getColor(R.color.actionbar_speak));
// 		 		}
// 			}
// 		}catch(DbException e){
// 			e.printStackTrace();
// 		}
 		
 	}
	
	private void init() {
		// TODO Auto-generated method stub
		
		lvRead = (ListView) findViewById(R.id.read_rl_page);
		read_han_unclick = (ImageButton)findViewById(R.id.read_han_unclick);
		read_han_unclick.setOnClickListener(this);
		read_han_click = (ImageButton)findViewById(R.id.read_han_click);
		read_han_click.setOnClickListener(this);
		img_qus = (ImageButton)findViewById(R.id.read_qus);
 		img_qus.setOnClickListener(this);
 		
 		DbUtils dbUtils = DbUtils.create(this);
 		boolean needUpdate = true;
 		try{
 			EmodouUserInfo userInfo = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
 			if(userInfo!=null){
 				userid = userInfo.getUserid();
 				ticket = userInfo.getTicket();
 			}
 			EmodouClassManager classManager=dbUtils.findFirst(Selector.from(EmodouClassManager.class)
 																	  .where("bookid","=",bookid)
 																	  .and("classid","=",classid)
 																	  .and("userid","=",userid));
 			
 			if(ValidateUtils.isExist(Constants.LOCAL_START + type + "/" + classid + Constants.LOCAL_JSON2)){
 	 			img_qus.setVisibility(View.VISIBLE);
 	 			if(classManager.getState()==Constants.EMODOU_CLASS_STATE_NOT_LEAREN){
 	 				classManager.setState(Constants.EMODOU_CLASS_STATE_LEARENING);
 	 				//此时需要更新，状态不变
 	 			}else{
 	 				needUpdate = false;
 	 			}
 	 		}else{
 	 			img_qus.setVisibility(View.GONE);
 	 			if(classManager.getState()!=Constants.EMODOU_CLASS_STATE_LEARENED){
 	 				classManager.setState(Constants.EMODOU_CLASS_STATE_LEARENED);
 	 				//此时需要更新，状态不变
 	 			}else{
 	 				needUpdate = false;
 	 			}
 	 		}
 			
 			dbUtils.update(classManager);
 			
 			if (needUpdate) {
				//如果需要更新就设置好intent的参数
				Intent intent = ValidateUtils.sycStudyRecord(this, bookid,
						classid, classManager.getState() + "", userid, ticket,
						Constants.STUDY_RECORD_HAVE);

				// 启动同步服务
				startService(intent);
			}
 			
 		}catch(DbException e){
 			e.printStackTrace();
 		}
 		
//		read_timing_unclick = (ImageButton)findViewById(R.id.read_timing_unclick);
//		read_timing_unclick.setOnClickListener(this);
//		read_timing_click = (ImageButton)findViewById(R.id.read_timing_click);
//		read_timing_click.setOnClickListener(this);
//		read_timetext = (TextView)findViewById(R.id.read_timetext);
 		
		
		
		str_page = ValidateUtils.readFromFile(this, Constants.LOCAL_START+type+"/"+classid+Constants.LOCAL_JSON1);
			
		try {
			
			//解析文章文件 获取 相应的信息
			JSONObject dataJson = new JSONObject(str_page);
			
			str_title = dataJson.getString("title");
			actionTitle.setText(str_title);

			JSONArray array = dataJson.getJSONArray("text");

			for (int i = 0; i < array.length(); i++) {

				JSONObject object = (JSONObject) array.get(i);
				String en = (String) object.get("en");

				//将资源里面的换行 用空替换掉
				String cn = (String) object.get("cn");
				en = en.replace('\n',' '); 
				cn = cn.replace('\n',' '); 
				EmodouReadText text = new EmodouReadText();
				text.setCn(cn);
				text.setEn(en);
				if (text != null){
					textList.add(text);	
				}
					

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		readAdapter = new ItemAdapter(textList);
		lvRead.setAdapter(readAdapter);
		
		
		//做题界面
		/*im_que=(ImageButton)findViewById(R.id.handle);
		sDrawer=(SlidingDrawer)findViewById(R.id.slidingdrawer);
		//tv=(TextView)findViewById(R.id.read_que);
		sDrawer.setVisibility(View.VISIBLE);
		
		fragmentTransaction = getFragmentManager().beginTransaction();
		
		
		sDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener()
		  {
		   @Override
		   public void onDrawerOpened() {
		    flag=true;
		    im_que.setImageResource(R.drawable.home_read_que);
		    Toast.makeText(ReadActivity.this, "开始做题", 0).show();
		    if(slidingmenu == null){
		    	slidingmenu = new SlidingmenuFragment();
		    }
		    fragmentTransaction.replace(R.id.frame, slidingmenu);
		    fragmentTransaction.commit();
		   }
		   
		  });
		  
		  sDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener(){

		   @Override
		   public void onDrawerClosed() {
		    flag=false;
		    im_que.setImageResource(R.drawable.home_read_que);
		    Toast.makeText(ReadActivity.this, "结束做题", 0).show();
		   }
		   
		  });
		  
		  sDrawer.setOnDrawerScrollListener(new SlidingDrawer.OnDrawerScrollListener(){

		   @Override
		   public void onScrollEnded() {
		    //tv.setText("结束拖动");
		   }

		   @Override
		   public void onScrollStarted() {
			   //tv.setText("开始拖动");
		   }
		   
		  });*/
		  
		 
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
			case R.id.actionbar_read_return:
				if(classroomid != null){
					Intent intent = new Intent(this, WorkDetailActivity.class);
					intent.putExtra("userid", userid);
					intent.putExtra("ticket", ticket);
					intent.putExtra("classroomid", classroomid);
					intent.putExtra("workid", workid);
					startActivity(intent);
				}else{
					Intent intent = new Intent(this, CourseListActivity.class);
					Bundle mBundle = new Bundle();
					mBundle.putString("type", type);
					mBundle.putString("classid", classid);
					mBundle.putString("bookid", bookid);
					intent.putExtras(mBundle);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				break;
				
			case R.id.read_han_unclick:			
				readAdapter.han = 1;
				readAdapter.notifyDataSetChanged();

				read_han_unclick.setVisibility(View.GONE);
				read_han_click.setVisibility(View.VISIBLE);

				break;
		   
			case R.id.read_han_click:			
				readAdapter.han = 0;
				readAdapter.notifyDataSetChanged();

				read_han_click.setVisibility(View.GONE);
				read_han_unclick.setVisibility(View.VISIBLE);

				break;
				
			case R.id.read_qus:
				Intent intent2 = new Intent(this, QusReadActivity.class);
				Bundle mBundle2 = new Bundle();
				mBundle2.putString("bookid", bookid);
				mBundle2.putString("classid",classid);
				mBundle2.putString("type", type);
				mBundle2.putLong("starttimePass", System.currentTimeMillis()-startTime);
				mBundle2.putInt("textsize", textsize);
				//从做题界面跳转过来
				if(classroomid != null){
					mBundle2.putString("classroomid", classroomid);
					mBundle2.putString("workid", workid);
					mBundle2.putString("itemid", itemid);
					mBundle2.putString("starttime", starttime);
				}
				intent2.putExtras(mBundle2);
				intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent2);
				break;
			
			
//			case R.id.read_timing_unclick:
//				read_timing_unclick.setVisibility(View.GONE);
//				read_timing_click.setVisibility(View.VISIBLE);
//				read_timetext.setVisibility(View.VISIBLE);
//
//				read_timetext.setText("00:00");
//				stepTimeHandler = new Handler();
//				startTime = System.currentTimeMillis();
//				mTicker = new Runnable() {
//					public void run() {
//						String content = showTimeCount(System.currentTimeMillis()- startTime);
//						read_timetext.setText(content);
//
//						long now = SystemClock.uptimeMillis();
//						long next = now + (1000 - now % 1000);
//						stepTimeHandler.postAtTime(mTicker, next);
//					}
//				};
//				// 启动计时线程，定时更新
//				Toast.makeText(ReadActivity.this, "计时开始", 0).show();
//				mTicker.run();
//				break;
//
//			//暂停计时
//			case R.id.read_timing_click:
//				
//				AlertDialog.Builder builder = new AlertDialog.Builder(ReadActivity.this)
//				.setTitle(R.string.prompt)
//				.setMessage("本次阅读用时    " + read_timetext.getText().toString().trim())
//				.setCancelable(false)
//				.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
//			           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
//			           }					            
//			       }); 
//			
//				builder.create().show();
//
//				read_timing_unclick.setVisibility(View.VISIBLE);
//				read_timing_click.setVisibility(View.GONE);
//				read_timetext.setVisibility(View.GONE);
//				stepTimeHandler.removeCallbacks(mTicker);
//				break;
//				
//			//做题
//			case R.id.read_qus_unclick:
//				mSlidingMenu.toggle();
//				break;
		}
	}
	
	
	class ItemAdapter extends BaseAdapter {

		private List<EmodouReadText> lrclist2 = new ArrayList<EmodouReadText>();
		private int han, start, end , index2;
		private String selecttext;
		

		public ItemAdapter(List<EmodouReadText> lrclist) {
			super();
			this.lrclist2 = lrclist;
			
		}

		public String getSelecttext() {
			return selecttext;
		}

		public void setSelecttext(String selecttext) {
			this.selecttext = selecttext;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return lrclist2.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return lrclist2.get(position);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.read_item_list,parent, false);
				holder = new ViewHolder();
				holder.en = (TextPage) view.findViewById(R.id.mytestpage);
				holder.cn = (TextView) view.findViewById(R.id.tv_han);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			
			//修改字体大小
			if (textsize == 1) {
				holder.cn.setTextSize(35);
				holder.en.setTextSize(30);
			} else if (textsize == 2) {
				holder.cn.setTextSize(30);
				holder.en.setTextSize(25);
			} else if (textsize == 3) {
				holder.cn.setTextSize(25);
				holder.en.setTextSize(20);
			}

			

			//设置中英文
			holder.en.setText(lrclist2.get(position).getEn());
			holder.cn.setText(lrclist2.get(position).getCn());
			
			
			//给文字添加点词翻译监听
			//holder.en.setTextpageSelectTextCallBack(ReadAcitivity.this, position);
			
			
			//是否显示翻译
			if (han == 1) {
				holder.cn.setVisibility(View.VISIBLE);
			} else {
				holder.cn.setVisibility(View.GONE);
			}
			
			//holder.cn.setVisibility(View.VISIBLE);
			
			
			
			
			//将选中的单词改变颜色
			SpannableStringBuilder style = new SpannableStringBuilder(holder.en
					.getText().toString());

			/*if (selecttext != null && !selecttext.equals("") && position == index2) {
				int l = selecttext.length();
				if (l != -1) {
					
					//找出所有字符串中所有包含所选字并改变他们的背景颜色
						style.setSpan(
								new BackgroundColorSpan(Color
										.rgb(135, 220, 241)), start,
								end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

					holder.en.setText(style);

				}

				

			}*/
		
			
			
			return view;
		}
	
		private  class ViewHolder {
			TextPage en;
			TextView cn;
	
		}

	}
	
	public String showTimeCount(long time) {
//		if (time >= 360000000) {
//			return "00:00:00";
//		}
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
	
	public void setTextsize(int textsize){
		this.textsize = textsize;
	}
	
//	public void setSlidingMenu() {
//		setTitle("SlidingMenu");
//		mSlidingMenu = getSlidingMenu();
//		mSlidingMenu.setMode(SlidingMenu.RIGHT);
//		setBehindContentView(R.layout.fragmenttest);
//		mSlidingMenu.setBehindOffset(1200); 
//		setSlidingActionBarEnabled(false);
//		mSlidingMenu.setFadeDegree(0.3f);
//		
//		mSlidingMenu.showMenu();
//		mSlidingMenu.showContent();
//		
//		android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//		SlidingmenuFragment sldingmenu = new SlidingmenuFragment();
//		fragmentTransaction.replace(R.id.frame, sldingmenu);
//		fragmentTransaction.commit();
//	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(classroomid != null){
			Intent intent = new Intent(this, WorkDetailActivity.class);
			intent.putExtra("userid", userid);
			intent.putExtra("ticket", ticket);
			intent.putExtra("classroomid", classroomid);
			intent.putExtra("workid", workid);
			startActivity(intent);
		}else{
			Intent intent = new Intent(this, CourseListActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putString("type", type);
			mBundle.putString("classid", classid);
			mBundle.putString("bookid", bookid);
			intent.putExtras(mBundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		super.onBackPressed();
	}
}
