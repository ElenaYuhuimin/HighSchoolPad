package com.emodou.home;

import java.util.ArrayList;
import java.util.List;

import com.emodou.domain.EmodouWordClass;
import com.emodou.domain.EmodouWordInfo;
import com.emodou.domain.EmodouWordManager;
import com.emodou.myclass.WorkDetailActivity;
import com.emodou.util.Constants;
import com.example.emodou.R;
import com.iflytek.cloud.InitListener;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WordTestActivity extends Activity implements OnClickListener{
	
	//actionbar及其相关
	private RelativeLayout actionbarRl;
	private ActionBar actionbar;
	private TextView titletext;
	private ImageButton imbReturn, imbDelete;
	
	private String bookid, userid;
	
	private RelativeLayout restudyRl, nextRl;
	private LinearLayout buttonsLl;
	private WordTestChooseFrag chooseFrag;
	private WordTestDetailFrag detailFrag;
	private FrameLayout chooseFram, detailFrame;
	
	//是否从做题界面跳转过来
	private String ticket, classroomid, workid, itemid, starttime, classid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.word_test_activity);
		
		bookid = getIntent().getStringExtra("bookid");
		userid = getIntent().getStringExtra("userid");
		//是否从做题界面跳转过来
		ticket = getIntent().getStringExtra("ticket");
		classroomid = getIntent().getStringExtra("classroomid");
		workid = getIntent().getStringExtra("workid");
		itemid = getIntent().getStringExtra("itemid");
		starttime = getIntent().getStringExtra("starttime");
		classid = getIntent().getStringExtra("classid");
		
		init();
		setActionbar();
	}

	public void init() {
		// TODO Auto-generated method stub
		restudyRl = (RelativeLayout)findViewById(R.id.word_test_activity_restudyRl);
		restudyRl.setOnClickListener(this);
		nextRl = (RelativeLayout)findViewById(R.id.word_test_activity_nextRl);
		nextRl.setOnClickListener(this);
		buttonsLl = (LinearLayout)findViewById(R.id.word_test_activity_buttonLl);
		chooseFram = (FrameLayout)findViewById(R.id.word_test_activity_Choosefram);
		detailFrame = (FrameLayout)findViewById(R.id.word_test_activity_Detailfram);
		chooseFrag = (WordTestChooseFrag)getFragmentManager().findFragmentById(R.id.word_test_activity_ChooseFrag);
		detailFrag = (WordTestDetailFrag)getFragmentManager().findFragmentById(R.id.word_test_activity_DetailFrag);
//		chooseFrag = new WordTestChooseFrag();
//		detailFrag = new WordTestDetailFrag();
		if(ticket!=null){
			restudyRl.setVisibility(View.GONE);
		}else{
			restudyRl.setVisibility(View.VISIBLE);
		}
	}
	
	//这个是从WordTestResultActivity跳回来时，wordManagerList不刷新问题
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		chooseFrag.initWordMnagerList();
		super.onResume();
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
 		
 		
 		titletext.setText(getResources().getString(R.string.word_test_title));				
 	}
	
//	public void setcourseCount(int classSize, int wordSize) {
//		couseCountTv.setText("课程：" + classSize + "|总单词：" + wordSize);
//		leftCountTv.setText("|剩余单词: " + wordSize);
//	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.word_test_activity_restudyRl:
				String learnedAll = ifLearnedAll();
				Intent intent = new Intent(this, WordLearnActivity.class);
				intent.putExtra("bookid", bookid);
				intent.putExtra("userid", userid);
				intent.putExtra("learnedAll", learnedAll);
				startActivity(intent);
				break;
			
			case R.id.word_test_activity_nextRl:
				buttonsLl.setVisibility(View.GONE);
//				FragmentManager fragmentManager = getFragmentManager();
//				FragmentTransaction transaction = fragmentManager.beginTransaction();
//				transaction.replace(R.id.word_test_activity_fram, chooseFrag);
//				chooseFrag.refreshChoFrag();
//				transaction.commit();
				chooseFrag.refreshChoFrag();
				detailFrame.setVisibility(View.GONE);
				chooseFram.setVisibility(View.VISIBLE);
				break;
				
			case R.id.courselist_imbtn_return:
				if(ticket!=null){
					Intent intent1 = new Intent(WordTestActivity.this, WorkDetailActivity.class);
					intent1.putExtra("userid", userid);
					intent1.putExtra("ticket", ticket);
					intent1.putExtra("classroomid", classroomid);
					intent1.putExtra("workid", workid);
					startActivity(intent1);
				}else{
					//因为有可能是从WordTestResult中跳过来的，所以不能用onbackpressed
					Intent intent2 = new Intent(WordTestActivity.this, WordlistActivity.class);
					intent2.putExtra("bookid", bookid);
					startActivity(intent2);
				}
				break;
				
			default:
				break;
				
		}
	}
	
	public String ifLearnedAll() {
		List<EmodouWordManager> wordManagerList = new ArrayList<EmodouWordManager>();
		String learnedAll = "";
		DbUtils dbUtils = DbUtils.create(this);
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
					wordManagerList.addAll(wordManagerPartList);
				}
			}
			
			learnedAll = "true";
			for(EmodouWordManager wordManager : wordManagerList){
				if(wordManager.getReviewState() != Constants.WORD_REVIEW_STATE_RIGHT){
					learnedAll = "false";
					break;
				}
					
			}	
		}catch(DbException e){
			e.printStackTrace();
		}
		
		return learnedAll;
	}
	
	public void showResult() {
		Intent intent = new Intent(this, WordTestResultActivity.class);
		intent.putExtra("bookid", bookid);
		intent.putExtra("userid", userid);
		if(ticket!=null){
			intent.putExtra("ticket", ticket);
			intent.putExtra("classroomid", classroomid);
			intent.putExtra("workid", workid);
			intent.putExtra("itemid", itemid);
			intent.putExtra("starttime", starttime);
			intent.putExtra("classid", classid);
		}
		startActivity(intent);
	}
	
	public void showDetail(String wordname, String classid, String userid) {
		//detailFrag = new WordTestDetailFrag();
//		FragmentManager fragmentManager = getFragmentManager();
//		FragmentTransaction transaction = fragmentManager.beginTransaction();
//		transaction.replace(R.id.word_test_activity_fram, detailFrag);
		detailFrag.refresh(wordname, classid, userid);
//		transaction.commit();
		detailFrame.setVisibility(View.VISIBLE);
		chooseFram.setVisibility(View.GONE);
		
		buttonsLl.setVisibility(View.VISIBLE);
	}
	
//	public void refreshBar(int progress, int leftCount) {
//		progressTv.setText("进度  "+ progress + "%");
//		progressBar.setProgress(progress);
//		leftCountTv.setText("|剩余单词: " + leftCount);
//	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(ticket!=null){
			Intent intent1 = new Intent(WordTestActivity.this, WorkDetailActivity.class);
			intent1.putExtra("userid", userid);
			intent1.putExtra("ticket", ticket);
			intent1.putExtra("classroomid", classroomid);
			intent1.putExtra("workid", workid);
			startActivity(intent1);
		}else{
			//因为有可能是从WordTestResult中跳过来的，所以不能用onbackpressed
			Intent intent2 = new Intent(WordTestActivity.this, WordlistActivity.class);
			intent2.putExtra("bookid", bookid);
			startActivity(intent2);
		}
		super.onBackPressed();
	}
}
