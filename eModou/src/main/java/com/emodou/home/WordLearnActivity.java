package com.emodou.home;

import java.util.ArrayList;
import java.util.List;

import org.kymjs.kjframe.ui.BindView;

import com.emodou.domain.EmodouClassManager;
import com.emodou.domain.EmodouWordClass;
import com.emodou.domain.EmodouWordInfo;
import com.emodou.domain.EmodouWordManager;
import com.emodou.home.WordLearnFragment.OnLearnWordFinished;
import com.emodou.service.RecordIntentService;
import com.emodou.util.Constants;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.example.emodou.R.string;
import com.iflytek.cloud.InitListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.R.bool;
import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WordLearnActivity extends Activity implements OnClickListener, OnLearnWordFinished{
	
	//actionbar及其相关
	private RelativeLayout actionbarRl;
	private ActionBar actionbar;
	private TextView titletext;
	private ImageButton imbReturn, imbDelete;
	
	//界面数据
	private String bookid, userid;
	private String learnedAll;
	public static final int RETURN = 1;
	
	//界面控件
	private WordLearnFragment wordLearnFrag;
	private LinearLayout firstLl;
	private LinearLayout secondLl;
	private RelativeLayout nextWordRl;
	private RelativeLayout whatThisRl;
	private RelativeLayout easyRlLayout;
	private RelativeLayout stillForgetRl;
	private RelativeLayout rememberRl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.word_learn_activity);
		
		bookid = getIntent().getStringExtra("bookid");
		userid = getIntent().getStringExtra("userid");
		learnedAll = getIntent().getStringExtra("learnedAll");
		
		setActionbar();
		init();
	}
	
	public void setActionbar(){
 		
 		actionbar = getActionBar();
 		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
 		actionbar.setCustomView(R.layout.actionbar_courselist);
 		
 		View view = actionbar.getCustomView();
 		
 		//actionbar属性获取操作
 		actionbarRl = (RelativeLayout)view.findViewById(R.id.couselist_color);
 		actionbarRl.setBackgroundColor(getResources().getColor(R.color.actionbar_word));
 		titletext = (TextView)view.findViewById(R.id.courselist_ancionbar_text);	
 		imbReturn = (ImageButton)view.findViewById(R.id.courselist_imbtn_return);
 		imbReturn.setOnClickListener(this);
 		imbDelete = (ImageButton)view.findViewById(R.id.courselist_imbtn_delete);
 		imbDelete.setVisibility(View.GONE);
 		
 		
 		titletext.setText(getResources().getString(R.string.word_learn_title));				
 	}
	
	public void init() {
				
		wordLearnFrag = (WordLearnFragment)getFragmentManager().findFragmentById(R.id.word_learn_activity_fragment);
		firstLl = (LinearLayout)findViewById(R.id.word_learn_activity_firstLl);
		secondLl = (LinearLayout)findViewById(R.id.word_learn_activity_secondLl);
		nextWordRl = (RelativeLayout)findViewById(R.id.word_learn_activity_nextwordRl);
		nextWordRl.setOnClickListener(this);
		whatThisRl = (RelativeLayout)findViewById(R.id.word_learn_activity_whatThisRl);
		whatThisRl.setOnClickListener(this);
		easyRlLayout = (RelativeLayout)findViewById(R.id.word_learn_activity_EasyRl);
		easyRlLayout.setOnClickListener(this);
		stillForgetRl = (RelativeLayout)findViewById(R.id.word_learn_activity_stillForgetRl);
		stillForgetRl.setOnClickListener(this);
		rememberRl = (RelativeLayout)findViewById(R.id.word_learn_activity_RememberRl);
		rememberRl.setOnClickListener(this);
		
		firstLl.setVisibility(View.VISIBLE);
		secondLl.setVisibility(View.GONE);
		nextWordRl.setVisibility(View.GONE);
		wordLearnFrag.hideContentRl();
		
		//同时在shareprefence中记录下此时的时间，以便学习过程中返回时计算本次学习时间
		 SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
		 editor.putLong("wordLearn_startTime", System.currentTimeMillis());
		 editor.commit();
	}
	
//	public void returnWordlistAct() {
//		//单词学完时，返回课程列表
//		Intent intent = new Intent(this, WordlistActivity.class);
//		intent.putExtra("bookid", bookid);
//		startActivity(intent);
//		//this.onDestroy();
////		new Thread(new Runnable() {
////			
////			@Override
////			public void run() {
////				// TODO Auto-generated method stub
////				Message message = new Message();
////	            message.what = RETURN;
////	            handler.sendMessage(message);
////			}
////		}).start();
//	}
//	private Handler handler = new Handler() {
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//				case RETURN:
//					Intent intent = new Intent(WordLearnActivity.this, testActivity.class);
//					intent.putExtra("bookid", bookid);
//					startActivity(intent);
//					Toast.makeText(WordLearnActivity.this, "return", 1).show();
//					break;
//	
//				default:
//					break;
//			}
//		}
//	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
			case R.id.word_learn_activity_whatThisRl:
				wordLearnFrag.showSententce();
				firstLl.setVisibility(View.GONE);
				secondLl.setVisibility(View.VISIBLE);
				break;
				
			case R.id.word_learn_activity_EasyRl:
				wordLearnFrag.showAll();
				firstLl.setVisibility(View.GONE);
				nextWordRl.setVisibility(View.VISIBLE);
				wordLearnFrag.setSqlLearnFami();
				wordLearnFrag.refreshBar();
				break;
				
			case R.id.word_learn_activity_stillForgetRl:
				wordLearnFrag.showAll();
				secondLl.setVisibility(View.GONE);
				nextWordRl.setVisibility(View.VISIBLE);
				wordLearnFrag.setSqlLearnUnfami();
				wordLearnFrag.refreshBar();
				break;
			
			case R.id.word_learn_activity_RememberRl:
				wordLearnFrag.showAll();
				secondLl.setVisibility(View.GONE);
				nextWordRl.setVisibility(View.VISIBLE);
				wordLearnFrag.setSqlLearnVague();
				wordLearnFrag.refreshBar();
				break;
			
			case R.id.word_learn_activity_nextwordRl:
				wordLearnFrag.hideContentRl();
				nextWordRl.setVisibility(View.GONE);
				firstLl.setVisibility(View.VISIBLE);
				//这里只改变wordmanagerlist的数据，并且刷新fragment，不改变进度条
				wordLearnFrag.initWordManagerList();
				break;
			
			case R.id.courselist_imbtn_return:
				final WordLearnReDialog dialog = new WordLearnReDialog(WordLearnActivity.this, 
						                                         R.style.WordLearnReDialog);			
				dialog.setCanceledOnTouchOutside(true);
				dialog.setStateNum((int)wordLearnFrag.getUnFamiCount(), 
								   (int)wordLearnFrag.getVagueCount(), 
								   (int)wordLearnFrag.getFamiCount(), 
								   (int)wordLearnFrag.getNotLearnCount());
				
				SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
				long startTime = pref.getLong("wordLearn_startTime", 0);
				long endTime = System.currentTimeMillis();
				long learnTime = endTime - startTime;
				if(learnTime >= 0){
					dialog.setLearnTime(learnTime);
				}
				
				dialog.setListener(new WordLearnReDialog.DialogListener() {
					
					@Override
					public void onClick(View view) {
						// TODO Auto-generated method stub
						switch (view.getId()) {
						case R.id.word_learn_dialog_return_exit:
							//遍历class，设置learnstate状态
							DbUtils dbUtils = DbUtils.create(WordLearnActivity.this);
							try{
								List<EmodouWordClass> wordClassList = new ArrayList<EmodouWordClass>();
								wordClassList = dbUtils.findAll(Selector.from(EmodouWordClass.class)
										                                .where("userid","=",userid)
										                                .and("state","=",Constants.WORD_CLASS_SELECTED));
								//改用arraylist
								ArrayList<String> classidArray = new ArrayList<String>();
								int i = 0;
								for(EmodouWordClass wordClass : wordClassList){
									//将所有已选课程的classid提取出来
									classidArray.add(wordClass.getClassid());
									i++;	
									
									List<EmodouWordManager> wordManagerPartList = new ArrayList<EmodouWordManager>();
									wordManagerPartList = dbUtils.findAll(Selector.from(EmodouWordManager.class)
											                                      .where("userid","=",userid)
											                                      .and("classid","=",wordClass.getClassid()));
									if(wordManagerPartList != null && wordManagerPartList.size()!=0){
										//循环该课的所有的wordManager，查看该课程的wordManager是否review状态都为2，若是，则该课已经学完,
										//否则依据情况，设为未学习或者学习中
										boolean learnedAll = true;
										boolean notlearn = true;
										for(EmodouWordManager wordManager : wordManagerPartList){
											if(wordManager.getReviewState() != Constants.WORD_REVIEW_STATE_RIGHT)
												learnedAll = false;
											if(wordManager.getLearnState() != Constants.WORD_STATE_NOT_LEARN)
												notlearn = false;
										}
										
										EmodouClassManager classManager = dbUtils.findFirst(Selector.from(EmodouClassManager.class)
												                                                    .where("userid","=",userid)
												                                                    .and("classid","=",wordClass.getClassid()));
										if(classManager != null){
											if(learnedAll == true){
												classManager.setState(Constants.EMODOU_CLASS_STATE_LEARENED);
											}else if(notlearn == true){
												classManager.setState(Constants.EMODOU_CLASS_STATE_NOT_LEAREN);
											}else{
												classManager.setState(Constants.EMODOU_CLASS_STATE_LEARENING);
											}
											dbUtils.update(classManager);	
										}	
									
									}
								}
								
								//当本次学习后决定退出时，无论是否学完，都进行单词数据同步上传
								Intent intentService = new Intent(WordLearnActivity.this, RecordIntentService.class);
								intentService.putExtra("uploadtype", "word");
								intentService.putExtra("userid", userid);
								intentService.putStringArrayListExtra("wordClassArray", classidArray);
								//intentService.putExtra("wordClassArray", classidArray);
								startService(intentService);
								
							}catch(DbException e){
								e.printStackTrace();
							}
							//因为是有可能是从测试中的"重新学习”按钮跳过来的，所以不能用onbackpressed
							Intent intent2 = new Intent(WordLearnActivity.this, WordlistActivity.class);
							intent2.putExtra("bookid", bookid);
							startActivity(intent2);
							break;
							
						case R.id.word_learn_dialog_return_continue:
							dialog.dismiss();
							break;
							
						default:
							break;
					}
				}
				});
				dialog.show();
				
				break;
				
			default:
				break;
		}
	}
	
	protected void onDestroy() {super.onDestroy();}

	@Override
	public void onFinished() {
		
		//已经学完全部所选课程时候的跳转，这时候将所有课程的学习状态设置为已学完
		DbUtils dbUtils = DbUtils.create(this);
		try{
			List<EmodouClassManager> classManagerList = new ArrayList<EmodouClassManager>();
			List<EmodouWordClass> wordClassList = new ArrayList<EmodouWordClass>();
			wordClassList = dbUtils.findAll(Selector.from(EmodouWordClass.class)
					                                .where("userid","=",userid)
					                                .and("state","=",Constants.WORD_CLASS_SELECTED));
			for(EmodouWordClass wordClass : wordClassList){
				List<EmodouClassManager> classManagerPartList = new ArrayList<EmodouClassManager>();
				classManagerPartList = dbUtils.findAll(Selector.from(EmodouClassManager.class)
						                                      .where("userid","=",userid)
						                                      .and("classid","=",wordClass.getClassid()));
				if(classManagerPartList != null && classManagerPartList.size()!=0){
					classManagerList.addAll(classManagerPartList);
				}
			}
			
			for(EmodouClassManager classManager : classManagerList){
				classManager.setState(Constants.EMODOU_CLASS_STATE_LEARENED);
				dbUtils.update(classManager);
			}
			
			
		}catch(DbException e){
			e.printStackTrace();
		}
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(this)
		.setTitle(R.string.prompt)
		.setMessage(getResources().getString(R.string.word_learn_learnedAll))
		.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() { 
			 public void onClick(DialogInterface dialog, int whichButton) { 
				    Intent intent = new Intent(WordLearnActivity.this, WordlistActivity.class);
					intent.putExtra("bookid", bookid);
					startActivity(intent);
				    //onBackPressed();
				 
	           }	           				            
	       });
		dialog.show();
		
	}
	
	public void learnReturn() {
		final WordLearnReDialog dialog = new WordLearnReDialog(WordLearnActivity.this, 
                R.style.WordLearnReDialog);			
		dialog.setCanceledOnTouchOutside(true);
		dialog.setStateNum((int)wordLearnFrag.getUnFamiCount(), 
		(int)wordLearnFrag.getVagueCount(), 
		(int)wordLearnFrag.getFamiCount(), 
		(int)wordLearnFrag.getNotLearnCount());
		
		SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
		long startTime = pref.getLong("wordLearn_startTime", 0);
		long endTime = System.currentTimeMillis();
		long learnTime = endTime - startTime;
		if(learnTime >= 0){
			dialog.setLearnTime(learnTime);
		}
		
		dialog.setListener(new WordLearnReDialog.DialogListener() {
		
		@Override
		public void onClick(View view) {
		// TODO Auto-generated method stub
			switch (view.getId()) {
				case R.id.word_learn_dialog_return_exit:
				//遍历class，设置learnstate状态
				DbUtils dbUtils = DbUtils.create(WordLearnActivity.this);
				try{
				List<EmodouWordClass> wordClassList = new ArrayList<EmodouWordClass>();
				wordClassList = dbUtils.findAll(Selector.from(EmodouWordClass.class)
				                       .where("userid","=",userid)
				                       .and("state","=",Constants.WORD_CLASS_SELECTED));
				for(EmodouWordClass wordClass : wordClassList){
				List<EmodouWordManager> wordManagerPartList = new ArrayList<EmodouWordManager>();
				wordManagerPartList = dbUtils.findAll(Selector.from(EmodouWordManager.class)
				                                 .where("userid","=",userid)
				                                 .and("classid","=",wordClass.getClassid()));
				if(wordManagerPartList != null && wordManagerPartList.size()!=0){
				//循环该课的所有的wordManager，查看该课程的wordManager是否review状态都为2，若是，则该课已经学完,
				//否则依据情况，设为未学习或者学习中
				boolean learnedAll = true;
				boolean notlearn = true;
				for(EmodouWordManager wordManager : wordManagerPartList){
				if(wordManager.getReviewState() != Constants.WORD_REVIEW_STATE_RIGHT)
					learnedAll = false;
				if(wordManager.getLearnState() != Constants.WORD_STATE_NOT_LEARN)
					notlearn = false;
				}
				
				EmodouClassManager classManager = dbUtils.findFirst(Selector.from(EmodouClassManager.class)
				                                                   .where("userid","=",userid)
				                                                   .and("classid","=",wordClass.getClassid()));
				if(classManager != null){
				if(learnedAll == true){
				classManager.setState(Constants.EMODOU_CLASS_STATE_LEARENED);
				}else if(notlearn == true){
				classManager.setState(Constants.EMODOU_CLASS_STATE_NOT_LEAREN);
				}else{
				classManager.setState(Constants.EMODOU_CLASS_STATE_LEARENING);
				}
				dbUtils.update(classManager);	
				}	
				
				}
				}
				
				}catch(DbException e){
				e.printStackTrace();
				}
				//因为是有可能是从测试中的"重新学习”按钮跳过来的，所以不能用onbackpressed
				Intent intent2 = new Intent(WordLearnActivity.this, WordlistActivity.class);
				intent2.putExtra("bookid", bookid);
				startActivity(intent2);
				break;
				
				case R.id.word_learn_dialog_return_continue:
				dialog.dismiss();
				break;
				
				default:
				break;
			}
		}
		});
		dialog.show();
	}

	public void onBackPressed() {
		learnReturn();
	}
	
}
