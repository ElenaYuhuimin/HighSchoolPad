package com.emodou.home;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouBook;
import com.emodou.domain.EmodouClassManager;
import com.emodou.domain.EmodouReadText;
import com.emodou.domain.EmodouUserInfo;
import com.emodou.extend.AudioWife;
import com.emodou.extend.TextPageSelectTextCallBack;
import com.emodou.home.ReadActivity.ItemAdapter;
import com.emodou.myclass.WorkDetailActivity;
import com.emodou.util.Constants;
import com.emodou.util.LrcBean;
import com.emodou.util.TextPage;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.R.string;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ListenActivity extends Activity implements OnClickListener, TextPageSelectTextCallBack{
	
	//actionbar及其相关
	private ActionBar actionbar;
	private TextView titletext;
	private ImageButton imbReturn;//actionbar的返回按钮
	private RelativeLayout rl_color;
	private String titleString;
	
	//运行时变量
	private String classid, bookid, type, userid, ticket;
	
	//界面属性
	private List<LrcBean> lrclist = new ArrayList<LrcBean>();
	private ListView mylistview; 
	private ItemAdapter itemadapter;
	private String urlString;
	private Context mContext;
	private  Context contex = ListenActivity.this;
	private static int textsize;
	
	//界面实体属性
	private ImageButton hanUnclick, hanClick, hideClick, hideUnclick, qusImb;
	private SeekBar mMediaSeekBar;
	private ImageButton mPlayMedia, mPauseMedia;
	private TextView mRunTime, mTotalTime;
	
	//是否从做作业界面跳转过来
	private String classroomid, workid, itemid, starttime;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_listen);
        
        //获得courselist界面传过来的参数
        bookid = getIntent().getExtras().getString("bookid");
		classid = getIntent().getExtras().getString("classid");
		type = getIntent().getExtras().getString("type");
		//是否从做作业界面跳过来
		classroomid = getIntent().getExtras().getString("classroomid");
		workid = getIntent().getExtras().getString("workid");
		itemid = getIntent().getExtras().getString("itemid");
		starttime = getIntent().getExtras().getString("starttime");
		
        setActionbar();
        
        // 读取lrc文件到list中
     	lrclist = this.read(Constants.LISTEN_START + classid + Constants.LISTEN_LRC);
        //初始化
        init();
    }
	
	
	


	public void setActionbar(){
 		
 		actionbar = getActionBar();
 		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
 		actionbar.setCustomView(R.layout.actionbar_login);
 		
 		View view = actionbar.getCustomView();
 		
 		//actionbar属性获取操作
 		rl_color = (RelativeLayout)findViewById(R.id.rl_color);
 		rl_color.setBackgroundColor(getResources().getColor(R.color.actionbar_listen));
 		titletext = (TextView)view.findViewById(R.id.login_ancionbar_text);	 		
 		imbReturn = (ImageButton)view.findViewById(R.id.login_imbtn_return);
 		imbReturn.setOnClickListener(this);
 		qusImb = (ImageButton)findViewById(R.id.home_listen_qus);
 		qusImb.setOnClickListener(this);
 		
 		
 		
 		DbUtils dbUtils = DbUtils.create(this);
 		EmodouBook book1 = null;
 		try{
 			book1 = dbUtils.findFirst(Selector.from(EmodouBook.class).where(
					"bookid", "=", bookid));
 			if(book1!=null){
 				//需要根据传过来的参数更改颜色
 		 		if(Constants.EMODOU_TYPE_LISTEN.equals(type)){
 		 			rl_color.setBackgroundColor(getResources().getColor(R.color.actionbar_listen));
 		 		}else if(Constants.EMODOU_TYPE_READ.equals(type)){
 		 			rl_color.setBackgroundColor(getResources().getColor(R.color.actionbar_read));
 		 		}else if(Constants.EMODOU_TYPE_SPEAK.equals(type)){
 		 			rl_color.setBackgroundColor(getResources().getColor(R.color.actionbar_speak));
 		 		}
 			}
 		}catch(DbException e){
 			e.printStackTrace();
 		}
 		
	}

	
	
	private void init() {
		// TODO Auto-generated method stub
		// 汉字两个按钮
		hanClick = (ImageButton) findViewById(R.id.home_listen_han_click);
		hanUnclick = (ImageButton) findViewById(R.id.home_listen_han_unclick);

		hanClick.setOnClickListener(this);
		hanUnclick.setOnClickListener(this);
		
		//隐藏按钮
		hideClick = (ImageButton)findViewById(R.id.home_listen_hide_click);
		hideClick.setOnClickListener(this);
		
		hideUnclick = (ImageButton)findViewById(R.id.home_listen_hide_unclick);
		hideUnclick.setOnClickListener(this);
		
		// 获取 播放器各个按钮 以及时间显示视图的引用
		mPlayMedia = (ImageButton)findViewById(R.id.home_listen_play);
		mPauseMedia = (ImageButton)findViewById(R.id.home_listen_pause);
		mMediaSeekBar = (SeekBar) findViewById(R.id.home_listen_seekbar);
		mRunTime = (TextView) findViewById(R.id.home_listen_runtime);
		mTotalTime = (TextView) findViewById(R.id.home_listen_totaltime);
		
		mylistview = (ListView)findViewById(R.id.home_listen_listView);
		// 添加footbar
		View myview = getLayoutInflater().inflate(R.layout.home_page_end,null);
		mylistview.addFooterView(myview);
		
		//判断做题模块是否存在
		if(ValidateUtils.isExist(Constants.LOCAL_START + type + "/" + classid + Constants.LOCAL_JSON2)){
			qusImb.setVisibility(View.VISIBLE);
		}else {
			qusImb.setVisibility(View.GONE);
		}
		
		// 拼串得到1.json文件里的内容
		String stringjson1 = ValidateUtils.readFromFile(this,
				Constants.LOCAL_START + type + "/" + classid
						+ Constants.LOCAL_JSON1);
		try {
			JSONObject dataJson = new JSONObject(stringjson1);
			urlString = (String) dataJson.get("url");
			titleString = (String) dataJson.get("title");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		titletext.setText(titleString);
		// 初始化adapter并设置listview相关属性
		itemadapter = new ItemAdapter(lrclist);
		mylistview.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		mylistview.setAdapter(itemadapter);
		mylistview.setDivider(null);
		mContext = contex;
		
		// 拼串音频路径
		Uri uri = Uri.parse(Constants.LISTEN_START + classid + "/" + urlString);
		
		// audiowife是一个音乐播放控制类 在这里对他进行初始化，并添加相应的监听
		AudioWife.getInstance().init(mContext, uri).setPlayView(mPlayMedia)
				.setPauseView(mPauseMedia).setSeekBar(mMediaSeekBar)
				.setRuntimeView(mRunTime).setTotalTimeView(mTotalTime);

		AudioWife.getInstance().addOnCompletionListener(
				new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						Toast.makeText(getBaseContext(), "Completed",
								Toast.LENGTH_SHORT).show();
						// do you stuff.
					}
				});

		// 监听开始播放
		AudioWife.getInstance().addOnPlayClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
					}
				});

		// 监听暂停播放
		AudioWife.getInstance().addOnPauseClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
					}
				});

		// 显示时间的textview添加字幕变化监听
		mRunTime.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			// 在这里监听时间 并根据时间进行高亮调整和自动换行调整
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

				String text = mRunTime.getText().toString();
				String[] timer = text.split(":");
				int min = Integer.parseInt(timer[0]);
				int sec = Integer.parseInt(timer[1]);
				int current = (min * 60 + sec) * 1000;

				current = AudioWife.getInstance().getmMediaPlayer()
						.getCurrentPosition();
				int play = itemadapter.getCurrentplay();

				for (int i = 0; i <= lrclist.size() - 1; i++) {
					LrcBean lrc = lrclist.get(i);
					int lrctime = lrc.beginTime;
					if (current >= lrctime) {
						if (i < lrclist.size() - 1) {
							if (lrclist.get(i + 1).beginTime > current
									&& play != i) {
								itemadapter.setCurrentplay(i);
								if (i > 2 && i < lrclist.size() - 1) {
									mylistview.setSelection(i - 1);
								} else {
									mylistview.setSelection(i);
								}

								itemadapter.notifyDataSetInvalidated();

							}
						} else {
							if (play != i) {
								itemadapter.setCurrentplay(i);
								if (i > 2 && i < lrclist.size() - 1) {
									mylistview.setSelection(i - 1);
								} else {
									mylistview.setSelection(i);
								}
								itemadapter.notifyDataSetInvalidated();
							}
						}
					} else {
						break;
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		
		// 根据有没有题来标识本课有没有完成，更新数据库
		DbUtils dbUtil = DbUtils.create(this);
		boolean needUpdate = true;
		try {
			
			EmodouUserInfo userInfo = dbUtil.findFirst(Selector.from(EmodouUserInfo.class));
			userid = userInfo.getUserid();
			ticket = userInfo.getTicket();
			
			EmodouClassManager classManager = dbUtil.findFirst(Selector
					.from(EmodouClassManager.class)
					.where("bookid", "=", bookid).and("classid", "=", classid).and("userid", "=", userid));

			// 如果存在练习
			if (ValidateUtils.isExist(Constants.LOCAL_START + type + "/"
					+ classid + Constants.LOCAL_JSON2)) {

				// 如果以前的记录不是已经学完则 设为正在学习中
				if (classManager.getState() == Constants.EMODOU_CLASS_STATE_NOT_LEAREN) {
					classManager.setState(Constants.EMODOU_CLASS_STATE_LEARENING);
				} else {
					needUpdate = false;
				}

			}
			// 没有课后习题则直接设置为已经学完
			else if (classManager.getState() != Constants.EMODOU_CLASS_STATE_LEARENED) {
				classManager.setState(Constants.EMODOU_CLASS_STATE_LEARENED);
			} else {
				needUpdate = false;
			}

			dbUtil.update(classManager);

			if (needUpdate) {
				//如果需要更新就设置好intent的参数
				Intent intent = ValidateUtils.sycStudyRecord(this, bookid,
						classid, classManager.getState() + "", userid, ticket,
						Constants.STUDY_RECORD_HAVE);

				// 启动同步服务
				startService(intent);
			}

		} catch (DbException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case  R.id.login_imbtn_return:
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
			
			case R.id.home_listen_han_click:
				hanClick.setVisibility(View.GONE);
				hanUnclick.setVisibility(View.VISIBLE);
				itemadapter.setHan(false);
				itemadapter.notifyDataSetChanged();
				break;

			case R.id.home_listen_han_unclick:
				hanClick.setVisibility(View.VISIBLE);
				hanUnclick.setVisibility(View.GONE);
				itemadapter.setHan(true);
				itemadapter.notifyDataSetChanged();
				break;
				
			case R.id.home_listen_hide_unclick:
				hideClick.setVisibility(View.VISIBLE);
				hideUnclick.setVisibility(View.GONE);
				mylistview.setVisibility(View.GONE);
				break;
			
			case R.id.home_listen_hide_click:
				hideUnclick.setVisibility(View.VISIBLE);
				hideClick.setVisibility(View.GONE);
				mylistview.setVisibility(View.VISIBLE);
				break;
				
				
			case R.id.home_listen_qus:
				Intent intent2 = new Intent(this, QusListenActivity.class);
				Bundle mBundle2 = new Bundle();
				mBundle2.putString("type", type);
				mBundle2.putString("classid", classid);
				mBundle2.putString("bookid", bookid);
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
				
			default:
				break;
		}
	}
	
	/**
	 * 解析文章
	 * 
	 * @param file
	 * @return
	 */
	public List<LrcBean> read(String file) {
		List<LrcBean> lrclist2 = new ArrayList<LrcBean>();
		TreeMap<Integer, LrcBean> lrc_read = new TreeMap<Integer, LrcBean>();
		String data = "";
		try {
			File savefile = new File(file);
			FileInputStream stream = new FileInputStream(savefile);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					stream, "utf-8"));

			int i = 0;
			Pattern pattern = Pattern.compile("\\d{2}");
			while ((data = br.readLine()) != null) {
				data = data.replace("]", "@");
				String splitdata[] = data.split("@");
				if (data.endsWith("@")) {
					for (int k = 0; k < splitdata.length; k++) {
						String str = splitdata[k];
						str = str.replace(":", ".");
						str = str.replace(".", "@");

						String timedata[] = str.split("@");
						Matcher matcher = pattern.matcher(timedata[0]);
						if (timedata.length == 3 && matcher.matches()) {
							int m = Integer.parseInt(timedata[0]);
							int s = Integer.parseInt(timedata[1]);
							int ms = Integer.parseInt(timedata[2]);
							int currTime = (m * 60 + s) * 1000 + ms * 10;
							LrcBean item1 = new LrcBean();
							item1.beginTime = currTime;
							item1.lrcBody = "";
							lrclist2.add(item1);
							lrc_read.put(currTime, item1);
						}

					}
				}

				else {
					String lrcContent = splitdata[splitdata.length - 1];
					String[] lrcStrings = lrcContent.split("###");
					for (int j = 0; j < splitdata.length - 1; j++) {

						String tmpstr = splitdata[j];
						tmpstr = tmpstr.replace("[", ".");
						tmpstr = tmpstr.replace(":", ".");
						tmpstr = tmpstr.replace(".", "@");
						String timedata[] = tmpstr.split("@");
						String timedata0 = timedata[1];
						Matcher matcher = pattern.matcher(timedata[1]);

						if (timedata.length == 4 && matcher.find()) {
							int m = Integer.parseInt(timedata[1]); // 分
							int s = Integer.parseInt(timedata[2]); // 秒
							int ms = Integer.parseInt(timedata[3]); // 毫秒
							int currTime = (m * 60 + s) * 1000 + ms * 10;
							LrcBean item1 = new LrcBean();
							item1.beginTime = currTime;
							if (lrcStrings.length == 1) {
								item1.lrcBody = lrcContent;
								item1.lrccn = "";
							} else {
								item1.lrcBody = lrcStrings[0];
								item1.lrccn = lrcStrings[1];
							}

							lrclist2.add(item1);
							lrc_read.put(currTime, item1);// 将currTime当标签
															// item1当数据
															// 插入TreeMap里

							i++;
						}

					}
				}
			}
			stream.close();
		} catch (Exception e) {

		}

		return lrclist2;

	}
	
	private static class ViewHolder {
		TextPage text;
		TextView text2;
		ImageView imListenButton;

	}
	
	class ItemAdapter extends BaseAdapter {

		private List<LrcBean> lrclist2 = new ArrayList<LrcBean>();
		private String selecttext;
		private boolean han = false;
		private int currentplay = -1;
		private int index2, start, end;

		public String getSelecttext() {
			return selecttext;
		}
		
		public int getCurrentplay() {
			return currentplay;
		}

		public void setCurrentplay(int currentplay) {
			this.currentplay = currentplay;
		}

		public void setSelecttext(String selecttext) {
			this.selecttext = selecttext;
		}

		@Override
		public int getCount() {
			return lrclist2.size();
		}

		public ItemAdapter(List<LrcBean> lrclist2) {
			super();
			this.lrclist2 = lrclist2;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int index = position;

			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.home_listenlist_item, parent,false);
				holder = new ViewHolder();
				holder.text = (TextPage) view.findViewById(R.id.home_listen_testpage);
				holder.text2 = (TextView) view.findViewById(R.id.home_listen_pagehan);
				holder.imListenButton = (ImageView) view.findViewById(R.id.home_listen_laba);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			// 根据是否有中文显示汉字
			holder.text2.setText(lrclist2.get(position).lrccn);
			if (!han) {
				holder.text2.setVisibility(View.GONE);
			} else {
				holder.text2.setVisibility(View.VISIBLE);
			}

			// 根据当前是否为当前播放 设置是否高亮
			if (currentplay == position) {
				holder.text.setTextColor(Color.rgb(196, 49, 93));
				holder.text2.setTextColor(Color.rgb(196, 49, 93));
			} else {
				holder.text.setTextColor(Color.rgb(73, 73, 73));
				holder.text2.setTextColor(Color.rgb(190, 190, 190));
			}

			// 英文使用重写的textview支持屏幕取词
			holder.text.setText(lrclist2.get(position).lrcBody);
			holder.text.setTextpageSelectTextCallBack(ListenActivity.this, index);

			// 将选中的词用浅绿色的高亮
			if (index2 == index && selecttext != null) {
				SpannableStringBuilder style = new SpannableStringBuilder(
						lrclist2.get(position).lrcBody);

				style.setSpan(
						new BackgroundColorSpan(Color.rgb(108, 250, 202)),
						start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				holder.text.setText(style);
			}

			// 每行前面的小喇叭 添加监听
			holder.imListenButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int vsibale = 0;
					if (mMediaSeekBar.getVisibility() == View.GONE) {
						vsibale = 1;
					}
					setCurrentplay(index);
					AudioWife.getInstance().getmMediaPlayer()
							.seekTo(lrclist2.get(index).beginTime);
					if (vsibale == 1) {
						mMediaSeekBar.setVisibility(View.GONE);
					}
					AudioWife.getInstance().play();
					itemadapter.notifyDataSetInvalidated();
				}
			});

			// 设置字体大小
			if (textsize == 1) {
				holder.text.setTextSize(35);
				holder.text2.setTextSize(30);
			} else if (textsize == 2) {
				holder.text.setTextSize(30);
				holder.text2.setTextSize(25);
			} else if (textsize == 3) {
				holder.text.setTextSize(25);
				holder.text2.setTextSize(20);
			}

			return view;
		}

		public boolean isHan() {
			return han;
		}

		public void setHan(boolean han) {
			this.han = han;
		}

	}
	
	protected void onPause() {
		AudioWife.getInstance().pause();
		super.onPause();
	}

	@Override
	public void selectTextEvent(String selectText, TextPage tp, int start,
			int end, int index) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void selectParagraph(int paragraph) {
		// TODO Auto-generated method stub
		
	}
	
	public void setTextsize(int textsize){
		this.textsize = textsize;
	}

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
	}
	
}
