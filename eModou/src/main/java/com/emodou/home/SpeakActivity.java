package com.emodou.home;

import java.io.File;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouBook;
import com.emodou.domain.EmodouClassManager;
import com.emodou.domain.EmodouSentenceManager;
import com.emodou.domain.EmodouSpeakBean;
import com.emodou.domain.EmodouSpeakItem;
import com.emodou.domain.EmodouWordForWlist;
import com.emodou.domain.EmodouWordManager;
import com.emodou.domain.EmodouWorkDetail;
import com.emodou.extend.MyAudioTrackThread;
import com.emodou.extend.XmlResultParser;
import com.emodou.myclass.WorkDetailActivity;
import com.emodou.util.Constants;
import com.emodou.util.LogUtil;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.iflytek.cloud.EvaluatorListener;
import com.iflytek.cloud.EvaluatorResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvaluator;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.ise.result.Result;
import com.iflytek.ise.result.entity.Sentence;
import com.iflytek.ise.result.entity.Word;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SpeakActivity extends Activity implements OnClickListener{
	
	private String classid, bookid, type, userid, ticket;
	private boolean needUpdate = false;
	private static int textsize;
	private MediaPlayer mediaplayer = new MediaPlayer();
	private ImageButton seriesPlay, pause; // 连续播放,暂停播放
	private ImageButton hanUclick, hanClick;
	private SpeakAdapter adapter;
	private int myindex;
	private static String TAG = SpeakActivity.class.getSimpleName();
	private Boolean isCnVisible = false;
	private String[] playStatus = { "end of music list", "top of music list","play" }; // 播放状态
	
	private final static String PREFER_NAME = "ise_settings";
	// 评测语种
	private String language;
	// 评测题型
	private String category;
	// 结果等级
	private String result_level;	
	private String mLastResult;
	private boolean isRecording = false;
	
	//设置录制pcm所需的变量
	private static AudioRecord mRecord; 
	// 音频大小  
    private int mMinBufSize;  
    byte []mAudioTrackdata = null;
    Thread mAudioTrackThread = null;
    Handler mAudioTrackHandler;
    //播放pcm所需的变量
  	final int EVENT_PLAY_OVER = 0x100;
  	// 音频获取源  
    private int mAudioSource = MediaRecorder.AudioSource.MIC;  
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025  
    private static int mSampleRateInHz = 16000;// 44100;  
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道  
    private static int mChannelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;// AudioFormat.CHANNEL_IN_STEREO;  
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。  
    private static int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;  
	
	//actionbar及其相关
	private ActionBar actionbar;
	private TextView titletext;
	private ImageButton imbReturn;//actionbar的返回按钮
	private RelativeLayout rl_color;
	private String titleString;
	
	private SpeechEvaluator mSpeechEvaluator;
	private Toast mToast;
	private List<EmodouSpeakItem> textList;
	
	// 播放音频状态1是没有播放任何音频2是播放系统音乐3是播放我的声音
	public static int PLAY_NOTPLAYING = 1;
	public static int PLAY_SYSTEM = 2;
	public static int PLAY_MYVOICE = 3;
	
	//与口语item的合上和打开有关
	public static int PLAY_SATE_UNCHECK = 1;
	public static int PLAY_SATE_CHECKED = 2;
	
	//界面相关实体属性
	private ListView mlistView;
	private View pageEndView;
	
	//是否从做作业界面跳转过来
	private String classroomid, workid, itemid, starttime;
	private Button uploadNoBtn, uploadBtn;
	private List<EmodouSentenceManager> sentenceManagerList = new ArrayList<EmodouSentenceManager>();
	private int position;//记录当前位置，如果点击的是同一个item，则列表不刷新，否则刷新，这种机制是为了防止点击同一个item时，播放自己的声音会停止
//	private Handler mHandler;
	
	private int height;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_speak);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		classid = getIntent().getExtras().getString("classid");
		bookid = getIntent().getExtras().getString("bookid");
		type = getIntent().getExtras().getString("type");
		//是否从做作业界面跳过来
		classroomid = getIntent().getExtras().getString("classroomid");
		workid = getIntent().getExtras().getString("workid");
		itemid = getIntent().getExtras().getString("itemid");
		starttime = getIntent().getExtras().getString("starttime");	
		
		userid = ValidateUtils.getUserid(this);
		ticket = ValidateUtils.getTicket(this);
		
		
		setActionbar();
		
		//获取屏幕的高度和宽度
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		height = wm.getDefaultDisplay().getHeight();
		LogUtil.d("height", height+"");
		
		DbUtils dbUtils = DbUtils.create(this);
		//刚进入，还没有录音，若录了一条音则设置状态为已做完，否则目前设置状态为正在学习中
		try {
			EmodouClassManager classManager =  dbUtils.findFirst(Selector.from(EmodouClassManager.class).where("bookid", "=", bookid)
					.and("classid", "=", classid).and("type", "=", type).and("userid", "=", userid));
			
			if(classManager.getState()==Constants.EMODOU_CLASS_STATE_NOT_LEAREN){
				classManager.setState(Constants.EMODOU_CLASS_STATE_LEARENING);
				needUpdate = true;
			}
		
			dbUtils.update(classManager);
		
		} catch (DbException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		seriesPlay = (ImageButton)findViewById(R.id.home_speak_continueplay_unclick);
		seriesPlay.setOnClickListener(this);
		pause = (ImageButton)findViewById(R.id.home_speak_continueplay_click);
		pause.setOnClickListener(this);
		
		hanClick = (ImageButton)findViewById(R.id.home_speak_han_click);
		hanClick.setOnClickListener(this);
		hanUclick = (ImageButton)findViewById(R.id.home_speak_han_unclick);
		hanUclick.setOnClickListener(this);
		
		
		//初始化口语打分对象
		SpeechUtility.createUtility(this, SpeechConstant.APPID +"=54d1884f");
		mSpeechEvaluator = SpeechEvaluator.createEvaluator(SpeakActivity.this, null);
		mToast = Toast.makeText(SpeakActivity.this, "", Toast.LENGTH_LONG);
		
		getList();	
		
		mlistView = (ListView)findViewById(R.id.home_speak_listview);
		
		uploadNoBtn = (Button)findViewById(R.id.home_speak_uploadNo);
		uploadNoBtn.setOnClickListener(this);
		uploadBtn = (Button)findViewById(R.id.home_speak_upload);
		uploadBtn.setOnClickListener(this);
		//文章解析完后判断是否已经全录音完
		//判断是否所有的已经都做完，可以提交
		if(classroomid != null){
			ifRecordAll();
		}else{
			uploadBtn.setVisibility(View.GONE);
			uploadNoBtn.setVisibility(View.GONE);
		}
				
		pageEndView = getLayoutInflater().inflate(R.layout.home_page_end_speak, null);
		mlistView.addFooterView(pageEndView);
		
		adapter = new SpeakAdapter(textList);
		mlistView.setAdapter(adapter);	
		mlistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (mediaplayer != null && mediaplayer.isPlaying()) {
					mediaplayer.pause();
				}
				for (int i = 0; i < textList.size(); i++) {
					textList.get(i).setState(PLAY_SATE_UNCHECK);
				}
				textList.get(position).setState(PLAY_SATE_CHECKED);
				playSysMusic(textList.get(position), position);

				adapter.notifyDataSetChanged();

			}
		});
		//停止播放的handler
        mAudioTrackHandler = new Handler(){
        	public void handleMessage(Message message){
        		if (message.what == EVENT_PLAY_OVER){
        			mAudioTrackThread = null;
        		}
        	}
        };
		
        //初始化录音相关变量
        mMinBufSize = AudioRecord.getMinBufferSize(mSampleRateInHz, mChannelConfig,  
                mAudioFormat);
        
        
        
		
		mRecord = new AudioRecord(mAudioSource, mSampleRateInHz, mChannelConfig,  
                mAudioFormat, mMinBufSize);
		
		
//		if (textList != null && textList.size() >= 1) {	
//			if (mediaplayer != null && mediaplayer.isPlaying()) {
//				mediaplayer.pause();
//			}
//			for (int i = 0; i < textList.size(); i++) {
//				textList.get(i).setState(PLAY_SATE_UNCHECK);
//			}
//			textList.get(0).setState(PLAY_SATE_CHECKED);
//			//playSysMusic(textList.get(0), 0);
//			adapter.notifyDataSetChanged();
//		}
	}
	
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		if(mediaplayer != null){
//			mediaplayer.stop();
//			mediaplayer.release();
//		}
//		
//	}
	
	public void ifRecordAll() {
		DbUtils dbUtils = DbUtils.create(this);
		try {
			sentenceManagerList = dbUtils.findAll(Selector.from(EmodouSentenceManager.class)
					                                      .where("userid","=",userid)
					                                      .and("classid","=",classid));
			if(sentenceManagerList == null){
				//防止空指针
				uploadBtn.setVisibility(View.GONE);
				uploadNoBtn.setVisibility(View.VISIBLE);
			}else{
				if(sentenceManagerList.size() == textList.size()){
					//分数和文章的个数相同，则所有的item都打了分，可以上传了
					uploadBtn.setVisibility(View.VISIBLE);
					uploadNoBtn.setVisibility(View.GONE);
				}else{
					uploadBtn.setVisibility(View.GONE);
					uploadNoBtn.setVisibility(View.VISIBLE);
				}
			}
			
			
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	
	/**
	 * 解析文件中的内容组装成对象，然后加入相应的listview
	 */
	public void getList() {

		textList = new ArrayList<EmodouSpeakItem>();

		String stringjson1 = ValidateUtils.readFromFile(this, Constants.LOCAL_START + type + "/"
				+ classid + Constants.LOCAL_JSON1);

		DbUtils dbUtil = DbUtils.create(this);
		try {
			EmodouClassManager classManager = dbUtil.findFirst(Selector
					.from(EmodouClassManager.class)
					.where("bookid", "=", bookid).and("classid", "=", classid)
					.and("type", "=", type));
			//3为学完
			if (classManager.getState() != 3) {
				classManager.setState(2);
			}

			dbUtil.update(classManager);

		} catch (DbException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			JSONObject dataJson = new JSONObject(stringjson1);

			JSONArray array = dataJson.getJSONArray("text");
			titleString = (String) dataJson.get("title");
			titletext.setText(titleString);

			for (int i = 0; i < array.length(); i++) {

				JSONObject object = (JSONObject) array.get(i);
				String imgurl = (String) object.get("head");
				String en = (String) object.get("en");
				String mp3ul = (String) object.get("mp3");

				EmodouSpeakItem speakItem = new EmodouSpeakItem();

				//约定用三个#来分割人名 图片 英文 翻译 等
				String[] speak = en.split("###");
				EmodouSpeakBean speakBean = new EmodouSpeakBean();
				speakBean.setName(speak[0]);
				int ii = speak.length;
				switch (ii) {
				case 4:
					speakBean.setImageurl(speak[1]);
					speakBean.setEnString(speak[2]);
					speakBean.setCnString(speak[3]);
					break;

				case 3:
					speakBean.setImageurl(speak[1]);
					speakBean.setEnString(speak[2]);
					speakBean.setCnString("");
					break;

				case 2:
					speakBean.setImageurl(speak[1]);
					speakBean.setEnString("");
					speakBean.setCnString("");
					break;

				default:
					break;
				}
				speakItem.setSpeakbean(speakBean);

				// 根据mp3的相对路径设置绝对路径
				speakItem.setMp3url(Constants.LOCAL_START + type + "/" + classid
						+ "/" + mp3ul);
				speakItem.setPlay(PLAY_NOTPLAYING);
				speakItem.setState(PLAY_SATE_UNCHECK);
				speakItem.setMyVoiceUrl("");
				if (speakItem != null)
					textList.add(speakItem);

			}
			
			if (textList != null && textList.size() >= 1) {	
				textList.get(0).setState(PLAY_SATE_CHECKED);
				//playSysMusic(textList.get(0), 0);
			}

			/* tv_content.setText(entext); */
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private static class ViewHolder {
		TextView en, name, cn, totalScore,fen;
		ImageView  ivhead, ivGood, ivBad;
		ImageButton ib_record;
		
		HoloCircularProgressBar play_sys_music, ib_myVoice, pause_sys_music;

		RelativeLayout rlbox;
		
	}
	
	public class SpeakAdapter extends BaseAdapter {

		private List<EmodouSpeakItem> speaklist;
		private Long currentTimeT;



		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return speaklist.size();
		}

		public SpeakAdapter(List<EmodouSpeakItem> speaklist) {
			super();
			this.speaklist = speaklist;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return speaklist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final int index = position;

			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.home_speak_item, parent, false);
				holder = new ViewHolder();
				holder.en = (TextView) view.findViewById(R.id.home_speak_en);
				holder.name = (TextView) view.findViewById(R.id.home_speak_name);
				holder.cn = (TextView) view.findViewById(R.id.home_speak_cn);
				holder.ivhead = (ImageView) view.findViewById(R.id.home_speak_head);
				holder.totalScore = (TextView) view.findViewById(R.id.home_speak_score);
				holder.fen = (TextView)view.findViewById(R.id.home_speak_fen);
				
				
				holder.rlbox = (RelativeLayout) view.findViewById(R.id.home_speak_buttonbox);
				holder.play_sys_music = (HoloCircularProgressBar) view.findViewById(R.id.home_speak_play_sysmusic);
				//holder.pause_sys_music = (HoloCircularProgressBar)view.findViewById(R.id.home_speak_pause_sysmusic);
				holder.ib_record = (ImageButton) view.findViewById(R.id.home_speak_record);
				holder.ib_myVoice = (HoloCircularProgressBar) view.findViewById(R.id.home_speak_myvoice);				
				holder.ivGood = (ImageView) view.findViewById(R.id.home_speak_laugh);
				holder.ivBad = (ImageView) view.findViewById(R.id.home_speak_cry);
				
				
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			// 根据在列表中的位置获取对应的封装对象
			final EmodouSpeakItem speakItem = speaklist.get(position);
			final EmodouSpeakBean speakBean = speakItem.getSpeakbean();
			final DbUtils dbUtils = DbUtils.create(SpeakActivity.this);
			
			//设置播放mp3的mediaplayer，以便获得播放时间
//			try {
//				mediaplayer.setDataSource(speakItem.getMp3url());
//			} catch (IllegalArgumentException e2) {
//				// TODO Auto-generated catch block
//				e2.printStackTrace();
//			} catch (SecurityException e2) {
//				// TODO Auto-generated catch block
//				e2.printStackTrace();
//			} catch (IllegalStateException e2) {
//				// TODO Auto-generated catch block
//				e2.printStackTrace();
//			} catch (IOException e2) {
//				// TODO Auto-generated catch block
//				e2.printStackTrace();
//			}
			
			// 设置英汉和名字
			holder.en.setText(speakBean.getEnString());
			holder.name.setText(speakBean.getName());
			holder.cn.setText(speakBean.getCnString());

			
			//根据句子得分管理类来设置颜色
			try {
				EmodouSentenceManager sentenceManagerUtility = dbUtils.findFirst(Selector.from(EmodouSentenceManager.class).where("bookid", "=", bookid)
						.and("classid", "=", classid).and("indexx", "=", position).and("userid","=",userid));
				if(sentenceManagerUtility != null && sentenceManagerUtility.getTotalScore() != 0){
					holder.totalScore.setText((sentenceManagerUtility.getTotalScore() + "").subSequence(0, (sentenceManagerUtility.getTotalScore() + "").indexOf(".")));
					if(sentenceManagerUtility.getTotalScore() > 90){
						holder.ivGood.setVisibility(View.VISIBLE);
						holder.ivBad.setVisibility(View.GONE);
						holder.totalScore.setVisibility(View.VISIBLE);
						holder.totalScore.setTextColor(Color.WHITE);
						holder.fen.setTextColor(Color.WHITE);
					}else if(sentenceManagerUtility.getTotalScore() < 50) {
						holder.ivGood.setVisibility(View.GONE);
						holder.ivBad.setVisibility(View.VISIBLE);
						holder.totalScore.setVisibility(View.VISIBLE);
						holder.totalScore.setTextColor(Color.WHITE);
						holder.fen.setTextColor(Color.WHITE);
					}else{
						holder.ivGood.setVisibility(View.GONE);
						holder.ivBad.setVisibility(View.GONE);
						holder.totalScore.setVisibility(View.VISIBLE);
						holder.totalScore.setTextColor(Color.BLACK);
						holder.fen.setTextColor(Color.BLACK);
					}
					
					
					
					
					String[] wordLit = sentenceManagerUtility.getWordScore().split("##");
					//对文本进行标记
					SpannableStringBuilder styled = new SpannableStringBuilder(speakBean.getEnString());
					if(wordLit != null && wordLit.length != 0){
						for(int i=0; i<wordLit.length; i++){
							if(!wordLit[i].equals("")){
								String[] wordInfo = wordLit[i].split(":");
								String word = wordInfo[0];
								String store = wordInfo[1];
								
								int f = speakBean.getEnString().indexOf(word);
								
								//大于八十分用绿色
								if(f != -1 && Float.parseFloat(store)> 80.0){
									
									
									int l = word.length();
										styled.setSpan(new ForegroundColorSpan(Color.rgb(2, 197, 103)), f, l+f,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

								}
								
								//小于六十分用红色
								else if(f != -1 &&Float.parseFloat(store) < 60.0){

									int l = word.length();
									if(f != -1){
										styled.setSpan(new ForegroundColorSpan(Color.rgb(255, 81, 81)), f, l+f,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);							
								}									
								}
								
							}
							
							holder.en.setText(styled);
						}
					}
					
					
					
					
					
					
					
				}else{
					holder.ivGood.setVisibility(View.GONE);
					holder.ivBad.setVisibility(View.GONE);
					holder.totalScore.setVisibility(View.GONE);	
					holder.fen.setVisibility(View.GONE);
				}
				
			} catch (DbException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//根据按钮来显示汉语是否显示
			if(isCnVisible == false){
				holder.cn.setVisibility(View.GONE);
			}else{
				holder.cn.setVisibility(View.VISIBLE);
			}
			
			// 对头像进行设置
			String imageurl = speakBean.getImageurl();
			if (imageurl != null && !imageurl.equals("")) {
				imageurl = imageurl.toLowerCase();

				if (!imageurl.equals("") && imageurl.equals("d_c01")) {
					holder.ivhead.setImageDrawable(getResources().getDrawable(
							R.drawable.d_c01));
				} else if (imageurl.equals("d_o01")) {
					holder.ivhead.setImageDrawable(getResources().getDrawable(
							R.drawable.d_o01));
				} else if (imageurl.equals("d_y02")) {
					holder.ivhead.setImageDrawable(getResources().getDrawable(
							R.drawable.d_y02));
				} else if (imageurl.equals("m_c01")) {
					holder.ivhead.setImageDrawable(getResources().getDrawable(
							R.drawable.m_c01));
				} else if (imageurl.equals("m_o01")) {
					holder.ivhead.setImageDrawable(getResources().getDrawable(
							R.drawable.m_o01));
				} else if (imageurl.equals("m_y05")) {
					holder.ivhead.setImageDrawable(getResources().getDrawable(
							R.drawable.m_y05));
				} else if (imageurl.equals("m_y04")) {
					holder.ivhead.setImageDrawable(getResources().getDrawable(
							R.drawable.m_y04));
				} else if (imageurl.equals("m_y03")) {
					holder.ivhead.setImageDrawable(getResources().getDrawable(
							R.drawable.m_y03));
				} else if (imageurl.equals("m_y02")) {
					holder.ivhead.setImageDrawable(getResources().getDrawable(
							R.drawable.m_y02));
				} else if (imageurl.equals("w_y01")) {
					holder.ivhead.setImageDrawable(getResources().getDrawable(
							R.drawable.w_y01));
				} else if (imageurl.equals("w_y02")) {
					holder.ivhead.setImageDrawable(getResources().getDrawable(
							R.drawable.w_y02));
				} else if (imageurl.equals("w_y03")) {
					holder.ivhead.setImageDrawable(getResources().getDrawable(
							R.drawable.w_y03));
				} else if (imageurl.equals("w_c01")) {
					holder.ivhead.setImageDrawable(getResources().getDrawable(
							R.drawable.w_c01));
				} else if (imageurl.equals("w_o01")) {
					holder.ivhead.setImageDrawable(getResources().getDrawable(
							R.drawable.w_o01));
				}  else if (imageurl.equals("n_y01")) {
					holder.ivhead.setImageDrawable(getResources().getDrawable(
							R.drawable.n_y01));
				} else if (imageurl.equals("m_y01")) {
					holder.ivhead.setImageDrawable(getResources().getDrawable(
							R.drawable.m_y01));
				}

			}

			// 对播放系统音频控件初始化
			holder.play_sys_music.setThumbEnabled(false);
			holder.play_sys_music.setMarkerEnabled(false);
			holder.play_sys_music.setProgressBackgroundColor((int) Color.rgb(190, 190, 190));
			holder.play_sys_music.setWheelSize(6);
			holder.play_sys_music.setProgressColor((int) Color.rgb(248, 106, 0));
			
			//对暂停系统音频控件进行初始化
//			holder.pause_sys_music.setThumbEnabled(false);
//			holder.pause_sys_music.setMarkerEnabled(false);
//			holder.pause_sys_music.setProgressBackgroundColor((int) Color.rgb(190, 190, 190));
//			holder.pause_sys_music.setWheelSize(6);
//			holder.pause_sys_music.setProgressColor((int) Color.rgb(248, 106, 0));

			// 播放录音进行初始化
			holder.ib_myVoice.setThumbEnabled(false);
			holder.ib_myVoice.setMarkerEnabled(false);
			holder.ib_myVoice.setProgressBackgroundColor((int) Color.rgb(217,217, 217));
			holder.ib_myVoice.setWheelSize(6);
			holder.ib_myVoice.setProgressColor((int) Color.rgb(189, 189, 189));

			// 设置三个圆形进度条的进度
			holder.play_sys_music.setProgress(speakItem.getProgress());
//			holder.pause_sys_music.setProgress(speakItem.getProgress());
			holder.ib_myVoice.setProgress(speakItem.getMyVoiceProgres());

//			if (speakItem.getPlay() == PLAY_SYSTEM) {
//				holder.play_sys_music.setVisibility(View.VISIBLE);
//			} else if (speakItem.getPlay() == PLAY_MYVOICE) {
//				holder.play_sys_music.setVisibility(View.INVISIBLE);
//			}

			holder.play_sys_music.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					playSysMusic(speakItem, index);
//					holder.play_sys_music.setVisibility(View.GONE);
//				    holder.pause_sys_music.setVisibility(View.VISIBLE);
//					mHandler = new Handler() {
//						// 注意：在各个case后面不能做太耗时的操作，否则出现ANR对话框
//						@Override
//						public void handleMessage(Message msg) {
//								float progress = msg.getData().getFloat("progress");
//							switch (msg.what) {
//							case 1:
//								int index = msg.getData().getInt("index");
////								textList.get(index).setProgress(
////										msg.getData().getFloat("progress"));
//								holder.play_sys_music.setProgress(progress);
//								//holder.pause_sys_music.setProgress(progress);
//								break;
//
//							case 2:
//								int index2 = msg.getData().getInt("index");
////								textList.get(index2).setMyVoiceProgres(
////										msg.getData().getFloat("myprogress"));
//								holder.play_sys_music.setProgress((float) 0.5);
//								//holder.pause_sys_music.setProgress(progress);
//								break;
//
//							case 0:
//								mediaplayer.pause();
//								adapter.notify();
//								break;
//						
//
//							}
//							super.handleMessage(msg);
//						}
//					};
				}
			});
			
//			holder.pause_sys_music.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					if(mediaplayer.isPlaying()&&mediaplayer!=null)
//						mediaplayer.pause();
//					holder.play_sys_music.setVisibility(View.VISIBLE);
//				    holder.pause_sys_music.setVisibility(View.GONE);
//				}
//			});
			
			
			
			//这里添加一个函数显示自己的声音的进度

			if (ValidateUtils.isExist(Constants.LOCAL_START + type + "/" + classid + "/res/"
					+ index + "_read.pcm")) {
				holder.ib_myVoice.setVisibility(View.VISIBLE);
			}else{
				holder.ib_myVoice.setVisibility(View.INVISIBLE);
			}

			// 如果选中则展开 下面的按钮
			if (speakItem.getState() == PLAY_SATE_CHECKED) {

				view.setBackgroundColor(Color.WHITE);
				holder.rlbox.setVisibility(View.VISIBLE);
				
				//mlistView.smoothScrollToPosition(position);
				
//				if(convertView !=null){
//					int Pos[] = {-1, -1};
//					convertView.getLocationOnScreen(Pos);
//					float Y = (float)Pos[1];
//					LogUtil.d("yOnSecreen", Y+"");
//					if(Y > (float)(height/2)){
////						convertView.setY((height/2));
////						convertView.setPivotY((height/2));
////						convertView.setRotationY((height/2));
////						convertView.setScaleY((height/2));
//						//mlistView
//						//convertView.setScaleY((float)(height/2));
//						//mlistView.smoothScrollToPosition(position-1);
//						//mlistView.setSelection(position-1);
//						
//					}
//				}
				
				
				//playSysMusic(speaklist.get(position), position);
//				mHandler = new Handler() {
//					// 注意：在各个case后面不能做太耗时的操作，否则出现ANR对话框
//					@Override
//					public void handleMessage(Message msg) {
//							float progress = msg.getData().getFloat("progress");
//							LogUtil.d("progress", progress+"");
//						switch (msg.what) {
//						case 1:
//							int index = msg.getData().getInt("index");
////							textList.get(index).setProgress(
////									msg.getData().getFloat("progress"));
//							holder.play_sys_music.setProgress(progress);
//							//holder.pause_sys_music.setProgress(progress);
//							break;
//
//						case 2:
//							int index2 = msg.getData().getInt("index");
////							textList.get(index2).setMyVoiceProgres(
////									msg.getData().getFloat("myprogress"));
//							holder.play_sys_music.setProgress((float) 0.5);
//							//holder.pause_sys_music.setProgress(progress);
//							break;
//
//						case 0:
//							mediaplayer.pause();
//							adapter.notify();
//							break;
//					
//
//						}
//						super.handleMessage(msg);
//					}
//				};

				holder.ib_record.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						System.out.println(event.toString() + "-------------------------");
						
						String path = Constants.LOCAL_START + type + "/" + classid + "/res/"
								+ index + "_read.pcm";

						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							//保存按下的时间
							currentTimeT = System.currentTimeMillis();
							
							// 判断是否播放音频中，有则停止
							if (mediaplayer != null && mediaplayer.isPlaying()) {
								mediaplayer.pause();
							}
							// 开始录音
							
							//如果有网络就采取讯飞打分api
							if(ValidateUtils.isNetworkConnected(SpeakActivity.this)){
								if (mSpeechEvaluator != null) {
								
									
									if(ValidateUtils.isExist(path)){
										ValidateUtils.deleteFile(path);
									}
									
									Toast.makeText(SpeakActivity.this,"录音开始", Toast.LENGTH_SHORT).show();
									mSpeechEvaluator.getEvaluator().setParameter(SpeechConstant.ISE_AUDIO_PATH, path);
									myindex = index;
									setParams();
									
									//在下面的评测接口执行判断是否是第一次录音，并改变classManager状态
									mSpeechEvaluator.startEvaluating(speakBean.getEnString(), null, mEvaluatorListener);

								}
							}
							//如果没有网络就只录音录制格式为pcm
							else  if(System.currentTimeMillis() - currentTimeT <= 1.5*mediaplayer.getDuration()){
								Log.i("hint","时间已停止1，口语音频时长为"+mediaplayer.getDuration());
								if(ValidateUtils.isExist(path)){
									ValidateUtils.deleteFile(path);
								}
								isRecording = true;
								writeDateTOFile(path);
								
								Toast.makeText(SpeakActivity.this,"录音开始", Toast.LENGTH_SHORT).show();
							}
								
								
					
								
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							//有网络是不做任何处理
							if(ValidateUtils.isNetworkConnected(SpeakActivity.this)){
								
							}
							//小于2秒视为时间太短不予记录
							else{
								if (System.currentTimeMillis() - currentTimeT < 2000) {
									isRecording=false;
									Toast.makeText(
											SpeakActivity.this,
											"录音时间太短。", 0).show();
									if (ValidateUtils.isExist(Constants.LOCAL_START + type + "/"
											+ classid + "/res/" + index
											+ "_read.pcm")) {
										ValidateUtils.fileDelete(Constants.LOCAL_START + type
												+ "/" + classid + "/res/"
												+ index + "_read.pcm");
									}
									adapter.notifyDataSetChanged();
									
								}
								//超过两秒则将分数清零，并录音
								else if(System.currentTimeMillis() - currentTimeT > 1.5*mediaplayer.getDuration()){
									isRecording=false;
									Log.i("hint","时间已停止1，口语音频时长为"+mediaplayer.getDuration());
									try {
										EmodouSentenceManager sentenceManagerUtility = dbUtils.findFirst(Selector.from(EmodouSentenceManager.class).where("bookid", "=", bookid)
												.and("classid", "=", classid).and("indexx", "=", index));
										
										if(sentenceManagerUtility!=null){
											sentenceManagerUtility.setTotalScore(0);
											sentenceManagerUtility.setWordScore("");
											dbUtils.update(sentenceManagerUtility);
										}
									} catch (DbException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									adapter.notifyDataSetChanged();
								}
								
							}
						
						
						} else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
							//有网络是不做任何处理
							if(ValidateUtils.isNetworkConnected(SpeakActivity.this)){
								
							}
							//
							else{
								if (System.currentTimeMillis() - currentTimeT < 2000) {
									isRecording=false;
									Toast.makeText(
											SpeakActivity.this,
											"录音时间太短", 0).show();
									if (ValidateUtils.isExist(Constants.LOCAL_START + type + "/"
											+ classid + "/res/" + index
											+ "_read.pcm")) {
										ValidateUtils.fileDelete(Constants.LOCAL_START + type
												+ "/" + classid + "/res/"
												+ index + "_read.pcm");
									}
									adapter.notifyDataSetChanged();
									
								}else if(System.currentTimeMillis() - currentTimeT > 1.5*mediaplayer.getDuration()){
									isRecording=false;
									Log.i("hint","时间已停止1，口语音频时长为"+mediaplayer.getDuration());
									try {
										EmodouSentenceManager sentenceManagerUtility = dbUtils.findFirst(Selector.from(EmodouSentenceManager.class).where("bookid", "=", bookid)
												.and("classid", "=", classid).and("indexx", "=", index));
										
										if(sentenceManagerUtility!=null){
											sentenceManagerUtility.setTotalScore(0);
											sentenceManagerUtility.setWordScore("");
											dbUtils.update(sentenceManagerUtility);
										}
									} catch (DbException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									adapter.notifyDataSetChanged();
								}
								
							}
						
						
						} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
							//有网络是不做任何处理
							if(ValidateUtils.isNetworkConnected(SpeakActivity.this)){
								
							}
							//
							else{
								if (System.currentTimeMillis() - currentTimeT < 2000) {
									isRecording=false;
									Toast.makeText(
											SpeakActivity.this,
											"录音时间太短", 0).show();
									if (ValidateUtils.isExist(Constants.LOCAL_START + type + "/"
											+ classid + "/res/" + index
											+ "_read.pcm")) {
										ValidateUtils.fileDelete(Constants.LOCAL_START + type
												+ "/" + classid + "/res/"
												+ index + "_read.pcm");
									}
									adapter.notifyDataSetChanged();
									
								}else if(System.currentTimeMillis() - currentTimeT > 1.5*mediaplayer.getDuration()){
									isRecording=false;
									Log.i("hint","时间已停止1，口语音频时长为"+mediaplayer.getDuration());
									try {
										EmodouSentenceManager sentenceManagerUtility = dbUtils.findFirst(Selector.from(EmodouSentenceManager.class).where("bookid", "=", bookid)
												.and("classid", "=", classid).and("indexx", "=", index));
										
										if(sentenceManagerUtility!=null){
											sentenceManagerUtility.setTotalScore(0);
											sentenceManagerUtility.setWordScore("");
											dbUtils.update(sentenceManagerUtility);
										}
									} catch (DbException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									adapter.notifyDataSetChanged();
								}
								
							}
						
						
						}
						return false;
					}
				});
				
				if(needUpdate == true){
					
					DbUtils dbUtils2 = DbUtils.create(SpeakActivity.this);
					//刚进入，还没有录音，若录了一条音则设置状态为已做完，否则目前设置状态为正在学习中
					try {
						EmodouClassManager classManager =  dbUtils2.findFirst(Selector.from(EmodouClassManager.class).where("bookid", "=", bookid)
								.and("classid", "=", classid).and("type", "=", type).and("userid", "=", userid));	
						
						//传递参数 同步数据
						Intent intent = ValidateUtils.sycStudyRecord(SpeakActivity.this, bookid,
								classid, classManager.getState() + "", userid, ticket,
								Constants.STUDY_RECORD_HAVE);

						// 启动同步服务
						startService(intent);
					
					} catch (DbException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
				}

			} else {

				holder.rlbox.setVisibility(View.GONE);
				view.setBackgroundColor(Color.rgb(241, 241, 241));
			}

			
			//播放pcm格式录音
			holder.ib_myVoice.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					
					Toast.makeText(SpeakActivity.this,"播放录音 ", 0).show();
					try {

						if (mediaplayer != null && mediaplayer.isPlaying()) {
							mediaplayer.pause();
						}
						
						 mAudioTrackdata = getPCMData(Constants.LOCAL_START + type + "/" + classid
									+ "/res/" + index + "_read.pcm");
						 
						 
						 if(mAudioTrackdata != null){
							 play(); 
						 }
					
					//我的声音进度条
					MyThread m = new MyThread(index, PLAY_MYVOICE);
					new Thread(m).start();	

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

			if (textsize == 1) {
				holder.en.setTextSize(35);
				holder.name.setTextSize(30);
				holder.cn.setTextSize(30);
			} else if (textsize == 2) {
				holder.en.setTextSize(30);
				holder.name.setTextSize(25);
				holder.cn.setTextSize(25);
			} else if (textsize == 3) {
				holder.en.setTextSize(25);
				holder.name.setTextSize(20);
				holder.cn.setTextSize(20);
			}

			return view;

		}

	}
	
	
	/**
	 * 播放系统音乐 包括新开一个进程来控制进度条的显示
	 * 
	 * @param speakItem
	 * @param index
	 */
	public void playSysMusic(EmodouSpeakItem speakItem, int index) {

		try {

			if (mediaplayer != null && mediaplayer.isPlaying()) {
				mediaplayer.pause();
			}

			ResetMusic(speakItem.getMp3url());
			mediaplayer.start();

			mediaplayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					seriesPlay.setVisibility(View.VISIBLE);
					pause.setVisibility(View.INVISIBLE);

				}
			});

			MyThread m = new MyThread(index, PLAY_SYSTEM);
			new Thread(m).start();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		adapter.notifyDataSetChanged();

	}
	
	public void ResetMusic(String path) {

		mediaplayer.reset();
		try {
			seriesPlay.setVisibility(View.INVISIBLE);
			pause.setVisibility(View.VISIBLE);
			mediaplayer.setDataSource(path);
			mediaplayer.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//进程刷新进度条
	class MyThread implements Runnable {
		int index;
		int state;

		public MyThread(int index, int state) {
			this.index = index;
			this.state = state;
		}

		public void run() {

			while (mediaplayer.isPlaying()) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (state == PLAY_SYSTEM) {
					Message msg1 = new Message();
					msg1.what = 1;
					Bundle bundle = new Bundle();
					bundle.putInt("state", state);
					bundle.putInt("index", index);
					bundle.putFloat("progress",
							(float) ((int) (100 * mediaplayer
									.getCurrentPosition() / mediaplayer
									.getDuration())) / 100);
					msg1.setData(bundle);
					mHandler.sendMessage(msg1);
					
				}

			}

			if (state == PLAY_SYSTEM) {
				Message msg1 = new Message();
				msg1.what = 1;
				Bundle bundle = new Bundle();
				bundle.putInt("state", state);
				bundle.putInt("index", index);
				bundle.putFloat("progress", (float) (0));
				msg1.setData(bundle);
				mHandler.sendMessage(msg1);
			}
			
			if(state == PLAY_MYVOICE){
				Message msg2 = new Message();
				msg2.what = 2;
				Bundle bundle = new Bundle();
				bundle.putInt("state", state);
				bundle.putInt("index", index);
				bundle.putFloat("progress",textList.get(index).getMyVoiceProgres());
//				Looper.prepare();
//				Toast.makeText(SpeakActivity.this,"myvoice______>"+textList.get(index).getMyVoiceProgres(),Toast.LENGTH_LONG).show();
//				Looper.loop();
			}

		}

	}
	
	Handler mHandler = new Handler() {
		// 注意：在各个case后面不能做太耗时的操作，否则出现ANR对话框
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				int index = msg.getData().getInt("index");
				textList.get(index).setProgress(
						msg.getData().getFloat("progress"));
				adapter.notifyDataSetChanged();
				break;

			case 2:
				int index2 = msg.getData().getInt("index");
				textList.get(index2).setMyVoiceProgres(
						msg.getData().getFloat("progress"));
				adapter.notifyDataSetChanged();

				/*
				 * MyThread m = new MyThread(); new Thread(m).start();
				 */
				break;

			case 0:
				mediaplayer.pause();
				adapter.notify();
				break;
		

			}
			super.handleMessage(msg);
		}
	};
	
	
	
	//设置打分对象的参数
	private void setParams() {
		SharedPreferences pref = getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
		language = pref.getString(SpeechConstant.LANGUAGE, "en_us");
		category = pref.getString(SpeechConstant.ISE_CATEGORY, "read_sentence");
		result_level = pref.getString(SpeechConstant.RESULT_LEVEL, "complete");
		String vad_bos = pref.getString(SpeechConstant.VAD_BOS, "5000");
		int time = (int) (mediaplayer.getDuration()*1.5);
		String vad_eos = pref.getString(SpeechConstant.VAD_EOS, "2000");
		String speech_timeout = pref.getString(SpeechConstant.KEY_SPEECH_TIMEOUT, "true");
		
		mSpeechEvaluator.setParameter(SpeechConstant.LANGUAGE, language);
		mSpeechEvaluator.setParameter(SpeechConstant.ISE_CATEGORY, category);
		mSpeechEvaluator.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
		mSpeechEvaluator.setParameter(SpeechConstant.VAD_BOS, vad_bos);
		mSpeechEvaluator.setParameter(SpeechConstant.VAD_EOS, "time");
		mSpeechEvaluator.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "time");//time不读取
		mSpeechEvaluator.setParameter(SpeechConstant.RESULT_LEVEL, result_level);
	}
	
	// 评测监听接口
	private EvaluatorListener mEvaluatorListener = new EvaluatorListener() {
		
		@Override
		public void onResult(EvaluatorResult result, boolean isLast) {
			Log.d(TAG, "evaluator result :" + isLast);

			DbUtils dbUtils = DbUtils.create(SpeakActivity.this);
			if (isLast) {
				StringBuilder builder = new StringBuilder();
				builder.append(result.getResultString());
				
				if(!TextUtils.isEmpty(builder)) {
					Log.d(TAG, "evaluator result :" + builder.toString());
				}
				
				Log.d(TAG, "evaluator result :" + builder.toString());
				mLastResult = builder.toString();   
				
				
				if (!TextUtils.isEmpty(mLastResult)) {
					XmlResultParser resultParser = new XmlResultParser();
					Result result2 = resultParser.parse(mLastResult);
					
					//更新句子打分管理类
					if (null != result2) {
						try {
							EmodouSentenceManager sentenceManagerUtility = dbUtils.findFirst(Selector.from(EmodouSentenceManager.class).where("bookid", "=", bookid)
									.and("classid", "=", classid).and("indexx", "=", myindex).and("userid","=",userid));
							
							//如果是第一次打分，存储打分对象
							if(sentenceManagerUtility == null){
								EmodouSentenceManager sentenceManager = new EmodouSentenceManager();
								sentenceManager.setBookid(bookid);
								sentenceManager.setClassid(classid);
								sentenceManager.setIndexx(myindex);
								sentenceManager.setUserid(userid);
								sentenceManager.setTotalScore(result2.total_score * 20);
								StringBuilder stringBuilder = new StringBuilder();
								
								for(int i =0 ; i<result2.sentences.size(); i++){
									Sentence sentence = result2.sentences.get(i);
									for(int j= 0; j<sentence.words.size(); j++){
										Word word = sentence.words.get(j);
											stringBuilder.append(word.content + ":" + word.total_score * 20 + "##");
									}
								}
								
								sentenceManager.setWordScore(stringBuilder.toString());
								dbUtils.saveBindingId(sentenceManager);
								
								//判断是否所有的已经都做完，可以提交
								if(classroomid != null){
									ifRecordAll();
								}else{
									uploadBtn.setVisibility(View.GONE);
									uploadNoBtn.setVisibility(View.GONE);
								}
								
								
								//设置为已做完

								try {
									EmodouClassManager classManager =  dbUtils.findFirst(Selector.from(EmodouClassManager.class).where("bookid", "=", bookid)
											.and("classid", "=", classid).and("type", "=", type).and("userid", "=", userid));
									
									
									classManager.setState(Constants.EMODOU_CLASS_STATE_LEARENED);
									
									needUpdate = true;
							
								
									dbUtils.update(classManager);
								
								} catch (DbException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}

							}
							//如果以前打过分数则修改数据库
							else{
								sentenceManagerUtility.setTotalScore(result2.total_score * 20);
								StringBuilder stringBuilder = new StringBuilder();
								
								for(int i =0 ; i<result2.sentences.size(); i++){
									Sentence sentence = result2.sentences.get(i);
									for(int j= 0; j<sentence.words.size(); j++){
										Word word = sentence.words.get(j);
											stringBuilder.append(word.content + ":" + word.total_score * 20 + "##");
									}
								}
								
								sentenceManagerUtility.setWordScore(stringBuilder.toString());
								dbUtils.update(sentenceManagerUtility);
								
								//判断是否所有的已经都做完，可以提交
								if(classroomid != null){
									ifRecordAll();
								}else{
									uploadBtn.setVisibility(View.GONE);
									uploadNoBtn.setVisibility(View.GONE);
								}
							}
							
							
							
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} else {
						showTip("结析结果为空");
					}
					
					adapter.notifyDataSetChanged();
				}
				
				
				showTip("评测结束");
			}
		}

		@Override
		public void onError(SpeechError error) {
			if(error != null) {	
				Log.d(TAG, "error:"+ error.getErrorCode() + "," + error.getErrorDescription());
			} else {
				Log.d(TAG, "evaluator over");
			}
		}

		@Override
		public void onBeginOfSpeech() {
			Log.d(TAG, "evaluator begin");
		}

		@Override
		public void onEndOfSpeech() {
			Log.d(TAG, "evaluator stoped");
		}

		@Override
		public void onVolumeChanged(int volume) {
			showTip("当前音量：" + volume);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// TODO Auto-generated method stub
		}
		
	};
	
	//存储录音pcm格式
    private void writeDateTOFile(final String fileName) {  
		
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mRecord.startRecording();
				
		        // new一个byte数组用来存一些字节数据，大小为缓冲区大小  
		        byte[] audiodata = new byte[mMinBufSize];  
		        FileOutputStream fos = null;  
		        int readsize = 0;  
		        try {  
		            File file = new File(fileName);  
		            if (file.exists()) {  
		                file.delete();  
		            }  
		            fos = new FileOutputStream(file);// 建立一个可存取字节的文件  
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        }  
		        while (isRecording == true) {  
		            readsize = mRecord.read(audiodata, 0, mMinBufSize); 		            
		            Log.i("采集大小", String.valueOf(readsize));  
		            if (AudioRecord.ERROR_INVALID_OPERATION != readsize) {  
		                try {  
		                    fos.write(audiodata);  
		                } catch (IOException e) {  
		                    e.printStackTrace();  
		                }  
		            }  
		        }  
		        try {  
		            fos.close();// 关闭写入流  
		        } catch (IOException e) {  
		            e.printStackTrace();  
		        }  
			}}).start();
		
		
    }  

    private void showTip(String str) {
		if(!TextUtils.isEmpty(str)) {
			mToast.setText(str);
			mToast.show();
		}
	}
    
    
	  //播放pcm格式读入文件流
	  public byte[] getPCMData(String filePath){
    	
    	File file = new File(filePath);
    	if (file == null){
    		return null;
    	}
    	
    	FileInputStream inStream;
		try {
			inStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		byte[] data_pack = null;
    	if (inStream != null){
    		long size = file.length();
    		
    		data_pack = new byte[(int) size];
    		try {
				inStream.read(data_pack);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
  
    	}
    	
    	return data_pack;
    }
	    
    //播放pcm
    public void play(){
    	if (mAudioTrackdata == null){
    		Toast.makeText(this, "No File...", 200).show();
    		return ;
    	}
    		
    	
    	if (mAudioTrackThread == null){
    		mAudioTrackThread = new Thread(new MyAudioTrackThread(mAudioTrackdata, mAudioTrackHandler));
    		mAudioTrackThread.start();
    	}else{
    		mAudioTrackThread.interrupt();
    		mAudioTrackThread = null;
    		mAudioTrackThread = new Thread(new MyAudioTrackThread(mAudioTrackdata, mAudioTrackHandler));
    		mAudioTrackThread.start();
    	}
    	
    }
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch ((v.getId())) {
		case R.id.home_speak_han_unclick:
			isCnVisible = true;
			hanClick.setVisibility(View.VISIBLE);
			hanUclick.setVisibility(View.GONE);
			adapter.notifyDataSetChanged();
			break;
		
		case R.id.home_speak_han_click:
			isCnVisible = false;	
			hanUclick.setVisibility(View.VISIBLE);
			hanClick.setVisibility(View.GONE);
			adapter.notifyDataSetChanged();
			break;
			
		case R.id.home_speak_continueplay_unclick:
			mlistView.setClickable(false);
			try {
				// 连续播放时关闭打开窗口
				for (int i = 0; i < textList.size(); i++) {
					if (textList.get(i).getState() == PLAY_SATE_CHECKED) {
						playMusic(textList.get(i), i);
						break;
					}
				}
				adapter.notifyDataSetChanged();
				// 如果正在录音录音停止

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case R.id.home_speak_continueplay_click:
			pause.setVisibility(View.GONE);
			seriesPlay.setVisibility(View.VISIBLE);
			if (mediaplayer != null && mediaplayer.isPlaying()) {
				mediaplayer.pause();
				// 清空监听消除 那个讨厌的bug
				mediaplayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub

					}
				});
				mlistView.setClickable(true);
				pause.setVisibility(View.INVISIBLE);
				seriesPlay.setVisibility(View.VISIBLE);

				adapter.notifyDataSetInvalidated();
			}

			break;
			
		case R.id.login_imbtn_return:
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
			
		case R.id.home_speak_upload:
			upLoadScore();
			break;
		default:
			break;
		}
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
		super.onBackPressed();
	}
	
//	//根据其重载实现
//    public List<EmodouSentenceManager> CompareWord(List<EmodouSentenceManager> wordManagerList){
//    	
//    	Collections.sort(wordManagerList, new Comparator<EmodouSentenceManager>() {
//    		public int compare(EmodouSentenceManager sentenceManager1, EmodouSentenceManager sentenceManager2) {
//				return (sentenceManager1.getIndexx()+"").compareTo(sentenceManager2.getIndexx()+"");
//			}
//		});
//    	return wordManagerList;
//    }
    
    
	
	public void upLoadScore() {
		//这时候必然已是所有的都打完分了
		//先将list按照indexx从小到大排序，以便分数能和资源对照起来
		//根据错误次数，进行重新排序
		Collections.sort(sentenceManagerList, new Comparator<EmodouSentenceManager>() {
			public int compare(EmodouSentenceManager sentenceManager1, EmodouSentenceManager sentenceManager2) {
				return new Integer(sentenceManager1.getIndexx())
						.compareTo(new Integer(sentenceManager2.getIndexx()));
			}
		});
		int allTotalScore = 0;
		for(EmodouSentenceManager sentenceManager : sentenceManagerList){
			allTotalScore += sentenceManager.getTotalScore();
		}
		int avergeScore = allTotalScore / sentenceManagerList.size();
		RequestParams params = new RequestParams();
		try {	
			JSONObject result = new JSONObject();
			result.put("UserId", userid);
			result.put("ItemId", itemid);
			result.put("ClassId", classid);
			result.put("Ticket", ticket);
			
			JSONObject ResultObject = new JSONObject();
			ResultObject.put("classid", classid);
			ResultObject.put("starttime", starttime);
			ResultObject.put("endtime", System.currentTimeMillis()+"");
			ResultObject.put("score", avergeScore);
			
			JSONArray detailArray = new JSONArray();
			for(int i = 0; i < sentenceManagerList.size(); i++){
				JSONObject detailObject = new JSONObject();
				detailObject.put("ID", (i+1)+"");
				detailObject.put("SCORE", ((int)sentenceManagerList.get(i).getTotalScore())+"");
				detailArray.put(i, detailObject);
			}
			ResultObject.put("detail", detailArray);
			result.put("Result", ResultObject);
			/*把JSON数据转换成String类型使用输出流向服务器写*/
//			params.addBodyParameter("Result", String.valueOf(ResultObject));
			try {
				params.setBodyEntity(new StringEntity(String.valueOf(result)));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpUtils httpUtils = new HttpUtils();
		String path = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_SUBMITWORK; 
		httpUtils.send(HttpMethod.POST, path, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				String res = responseInfo.result.toString();
				res = res.substring(res.indexOf("{"));
				LogUtil.d("upload", res);
				
				try{
					JSONObject result = new JSONObject(res);
					String status = result.getString("Status");
					if(status.equals("Success")){
						Toast.makeText(SpeakActivity.this, getResources().getString(R.string.home_listen_queResult_uploadSuc), 0).show();
						//将数据库中的作业详细信息改为DONE
						DbUtils dbUtils = DbUtils.create(SpeakActivity.this);
						EmodouWorkDetail workDetail = new EmodouWorkDetail();
						workDetail = dbUtils.findFirst(Selector.from(EmodouWorkDetail.class)
								                               .where("userid","=", userid)
								                               .and("classroomid","=",classroomid)
								                               .and("workid","=",workid)
								                               .and("itemid","=",itemid));
						if(workDetail != null){
							workDetail.setStatus("DONE");
							dbUtils.update(workDetail);
							
							//跳转回WorkDetailActivity界面
							Intent intent = new Intent(SpeakActivity.this, WorkDetailActivity.class);
							intent.putExtra("userid", userid);
							intent.putExtra("ticket", ticket);
							intent.putExtra("classroomid", classroomid);
							intent.putExtra("workid", workid);
							startActivity(intent);
						}
					}else{
						String message = result.getString("Message");
						if(message.equals("Error_Work_Timeout")) 
							Toast.makeText(SpeakActivity.this, getResources().getString(R.string.home_listen_qusResult_timeout), 0).show();
						else 
							Toast.makeText(SpeakActivity.this, getResources().getString(R.string.home_listen_queResult_uploadFail), 0).show();					
					}
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				LogUtil.d("uploadFail", msg);
				Toast.makeText(SpeakActivity.this, getResources().getString(R.string.home_listen_queResult_uploadFail), 0).show();
			}
		});
	}
	
	//连续播放
	private String playMusic(EmodouSpeakItem speakItem, int index)
			throws Exception {
		if (index >= textList.size()) { // 首先判断当前播放的文件是否超多了列表
			mlistView.setClickable(true); // 返回到低了，你也可以直接写成currentFilePosition=0，这样就能循环播放列表了
			return "";
		} else {
			seriesPlay.setVisibility(View.GONE);
			pause.setVisibility(View.VISIBLE);
			
			//mlistView.setSelection(index);

			try {

				if (mediaplayer != null && mediaplayer.isPlaying()) {
					mediaplayer.pause();
				}
				

				ResetMusic(speakItem.getMp3url());
				mediaplayer.start();

				MyThread m = new MyThread(index, PLAY_SYSTEM);
				new Thread(m).start();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			final int index2 = index + 1;
			LogUtil.e("index2", index2 + "");
			if (index >= textList.size()) {
				mlistView.setClickable(true); // 返回到低了，你也可以直接写成currentFilePosition=0，这样就能循环播放列表了
				return "";
			} else {
				mediaplayer.setOnCompletionListener(new OnCompletionListener() {
					public void onCompletion(MediaPlayer mp) {
						try {
							if (textList.get(index2) != null) {
								playNextMusic(textList.get(index2), index2); // 在每次播放完成之后都用播放下一首
								//mlistView.setSelection(index2);
								mlistView.setSelectionFromTop(index2, 300);
								LogUtil.e("complete", "playnextmusic");
							}

						} catch (Exception e) {
							e.printStackTrace();
							LogUtil.d("e", e.toString());
						}
					}
				});

				for (int i = 0; i < textList.size(); i++) {
					textList.get(i).setPlay(PLAY_NOTPLAYING);
					textList.get(i).setState(PLAY_SATE_UNCHECK);

				}

				speakItem.setPlay(PLAY_SYSTEM);
				speakItem.setState(PLAY_SATE_CHECKED);
				
				
				//mlistView.smoothScrollToPosition(position);
				
//				mlistView.bringToFront();
//				mlistView.requestFocusFromTouch();
//				mlistView.clearFocus();
//				mlistView.post(new Runnable() {
//				    @Override
//				    public void run() {
//				    	mlistView.setSelection(index2 -1);
//				    	int i = index2  - 1;
//				    	if (i > 2 && i < textList.size() - 1) {
//				    		mlistView.setSelection(i - 1);
//						} else {
//							mlistView.setSelection(i);
//						}
//
//				    	
//				    	
//				    }
//				});
				
				adapter.notifyDataSetChanged();
				//mlistView.setSelection(index);
				return playStatus[2]; // 返回正在播放
			}

		}

	}

	public String playNextMusic(EmodouSpeakItem speakItem, int index)
			throws Exception {
		return playMusic(speakItem, index);
	}
	
	protected void onPause() {
		// TODO Auto-generated method stub
		if (mediaplayer != null && mediaplayer.isPlaying()) {
			mediaplayer.pause();
		}
		if(mAudioTrackThread != null){
			if(!mAudioTrackThread.currentThread().isInterrupted()){
				mAudioTrackThread.interrupt();
				LogUtil.d("interrupt", "in");
			}
		}
		super.onPause();
	}
	
	public void setTextsize(int textsize){
		this.textsize = textsize;
	}
}
