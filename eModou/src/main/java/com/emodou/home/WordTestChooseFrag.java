package com.emodou.home;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.emodou.domain.EmodouWordClass;
import com.emodou.domain.EmodouWordInfo;
import com.emodou.domain.EmodouWordManager;
import com.emodou.util.Constants;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WordTestChooseFrag extends Fragment implements OnClickListener{
		
	private TextView AaTv, BbTv, CcTv, DdTv;
	private TextView titleTv, AmeanTv, BmeanTv, CmeanTv, DmeanTv;
	private ImageView voiceImv;
	private ImageView ArightImv, BrightImv, CrightImv, DrightImv;
	private ImageView AwrongImv, BwrongImv, CwrongImv, DwrongImv;
	private RelativeLayout ARl, BRl, CRl, DRl;
	private TextView couseCountTv, progressTv, leftCountTv;
	private ProgressBar progressBar;
	
	private String bookid, userid;
	private List<EmodouWordManager> wordManagerList = new ArrayList<EmodouWordManager>();
	private List<EmodouWordInfo> wordInfoBookList = new ArrayList<EmodouWordInfo>();
	private WordTestActivity wordTestActivity;
	private EmodouWordInfo wordInfo = new EmodouWordInfo();
	private EmodouWordManager wordManager = new EmodouWordManager();
	private MediaPlayer mediaPlayer = new MediaPlayer();
	private int randomAns;//表示现在正确答案在A到D中哪一个
	private int randomNow;//表示目前出的是wordManagerList中的哪个词
	private int randomEnorCn;//表示目前是英语题目还是汉语题目，0表示给英文，出汉意，1表示给汉意，出英文
	private int totalWordNum; //最初的总单词数，为了统计进度
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.word_test_choose_fragment);
//		bookid = getIntent().getStringExtra("bookid");
//		userid = getIntent().getStringExtra("userid");
//		
//		AaTv = (TextView)findViewById(R.id.word_test_choose_fragment_Aa);
//		BbTv = (TextView)findViewById(R.id.word_test_choose_fragment_Bb);
//		CcTv = (TextView)findViewById(R.id.word_test_choose_fragment_Cc);
//		DdTv = (TextView)findViewById(R.id.word_test_choose_fragment_Dd);
//		titleTv = (TextView)findViewById(R.id.word_test_choose_fragment_titleTv);
//		AmeanTv = (TextView)findViewById(R.id.word_test_choose_fragment_AmeanTv);
//		BmeanTv = (TextView)findViewById(R.id.word_test_choose_fragment_BmeanTv);
//		CmeanTv = (TextView)findViewById(R.id.word_test_choose_fragment_CmeanTv);
//		DmeanTv = (TextView)findViewById(R.id.word_test_choose_fragment_DmeanTv);
//		voiceImv = (ImageView)findViewById(R.id.word_test_choose_fragment_voiceImv);
//		ArightImv = (ImageView)findViewById(R.id.word_test_choose_fragment_Aright);
//		BrightImv = (ImageView)findViewById(R.id.word_test_choose_fragment_Bright);
//		CrightImv = (ImageView)findViewById(R.id.word_test_choose_fragment_Cright);
//		DrightImv = (ImageView)findViewById(R.id.word_test_choose_fragment_Dright);
//		AwrongImv = (ImageView)findViewById(R.id.word_test_choose_fragment_Awrong);
//		BwrongImv = (ImageView)findViewById(R.id.word_test_choose_fragment_Bwrong);
//		CwrongImv = (ImageView)findViewById(R.id.word_test_choose_fragment_Cwrong);
//		DwrongImv = (ImageView)findViewById(R.id.word_test_choose_fragment_Dwrong);
//		ARl = (RelativeLayout)findViewById(R.id.word_test_choose_fragment_ARl);
//		ARl.setOnClickListener(this);
//		BRl = (RelativeLayout)findViewById(R.id.word_test_choose_fragment_BRl);
//		BRl.setOnClickListener(this);
//		CRl = (RelativeLayout)findViewById(R.id.word_test_choose_fragment_CRl);
//		CRl.setOnClickListener(this);
//		DRl = (RelativeLayout)findViewById(R.id.word_test_choose_fragment_DRl);
//		DRl.setOnClickListener(this);
//		
//		initWordMnagerList();
//	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.word_test_choose_fragment,container,  false);
		
		bookid = getActivity().getIntent().getStringExtra("bookid");
		userid = getActivity().getIntent().getStringExtra("userid");
		
		AaTv = (TextView)view.findViewById(R.id.word_test_choose_fragment_Aa);
		BbTv = (TextView)view.findViewById(R.id.word_test_choose_fragment_Bb);
		CcTv = (TextView)view.findViewById(R.id.word_test_choose_fragment_Cc);
		DdTv = (TextView)view.findViewById(R.id.word_test_choose_fragment_Dd);
		titleTv = (TextView)view.findViewById(R.id.word_test_choose_fragment_titleTv);
		AmeanTv = (TextView)view.findViewById(R.id.word_test_choose_fragment_AmeanTv);
		BmeanTv = (TextView)view.findViewById(R.id.word_test_choose_fragment_BmeanTv);
		CmeanTv = (TextView)view.findViewById(R.id.word_test_choose_fragment_CmeanTv);
		DmeanTv = (TextView)view.findViewById(R.id.word_test_choose_fragment_DmeanTv);
		voiceImv = (ImageView)view.findViewById(R.id.word_test_choose_fragment_voiceImv);
		ArightImv = (ImageView)view.findViewById(R.id.word_test_choose_fragment_Aright);
		BrightImv = (ImageView)view.findViewById(R.id.word_test_choose_fragment_Bright);
		CrightImv = (ImageView)view.findViewById(R.id.word_test_choose_fragment_Cright);
		DrightImv = (ImageView)view.findViewById(R.id.word_test_choose_fragment_Dright);
		AwrongImv = (ImageView)view.findViewById(R.id.word_test_choose_fragment_Awrong);
		BwrongImv = (ImageView)view.findViewById(R.id.word_test_choose_fragment_Bwrong);
		CwrongImv = (ImageView)view.findViewById(R.id.word_test_choose_fragment_Cwrong);
		DwrongImv = (ImageView)view.findViewById(R.id.word_test_choose_fragment_Dwrong);
		ARl = (RelativeLayout)view.findViewById(R.id.word_test_choose_fragment_ARl);
		ARl.setOnClickListener(this);
		BRl = (RelativeLayout)view.findViewById(R.id.word_test_choose_fragment_BRl);
		BRl.setOnClickListener(this);
		CRl = (RelativeLayout)view.findViewById(R.id.word_test_choose_fragment_CRl);
		CRl.setOnClickListener(this);
		DRl = (RelativeLayout)view.findViewById(R.id.word_test_choose_fragment_DRl);
		DRl.setOnClickListener(this);
		
		couseCountTv = (TextView)view.findViewById(R.id.word_test_activity_couseCount);
		leftCountTv = (TextView)view.findViewById(R.id.word_test_activity_leftCount);
		progressTv = (TextView)view.findViewById(R.id.word_test_activity_progressTv);
		progressBar = (ProgressBar)view.findViewById(R.id.word_test_activity_progress);
		
		initWordMnagerList();
				
		return view;
	}
	
	public void initWordMnagerList() {
		// TODO Auto-generated method stub
		wordManagerList.clear();
		//先将这个用户的这本书classid的state为选中的单词列表都拿出来
		DbUtils dbUtils = DbUtils.create(getActivity());
		try{
			
			//获取所选课程的wordManager的列表
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
			
			//将所选课程的测试状态都清空
			for(EmodouWordManager wordManager : wordManagerList){
				EmodouWordManager wordManagerDb = dbUtils.findFirst(Selector.from(EmodouWordManager.class)
                                                       .where("userid","=", wordManager.getUserid())
                                                       .and("classid","=",wordManager.getClassid())
                                                       .and("wordname","=",wordManager.getWordname()));
				if(wordManagerDb != null){
					wordManagerDb.setEntitleRighttimes(0);
					wordManagerDb.setEntitleWrongtimes(0);
					wordManagerDb.setCntitleRighttimes(0);
					wordManagerDb.setCntitleWrongtimes(0);
					
					dbUtils.update(wordManagerDb);
				}
			}
			
			//获取本书所有的wordinfo的列表
			List<EmodouWordClass> wordAllClassList = new ArrayList<EmodouWordClass>();
			wordAllClassList = dbUtils.findAll(Selector.from(EmodouWordClass.class)
													   .where("userid","=",userid)
													   .and("bookid","=",bookid));
			if(wordAllClassList != null && wordAllClassList.size() != 0){
				for(EmodouWordClass wordClass : wordAllClassList){
					List<EmodouWordInfo> wordInfoPartList = new ArrayList<EmodouWordInfo>();
					wordInfoPartList = dbUtils.findAll(Selector.from(EmodouWordInfo.class)
															   .where("classid","=",wordClass.getClassid()));
					if(wordInfoPartList != null && wordInfoPartList.size() != 0){
						wordInfoBookList.addAll(wordInfoPartList);
					}
				}
			}
			
			int classSize = wordClassList.size();
			totalWordNum = wordManagerList.size();
			
			wordTestActivity = (WordTestActivity)getActivity();
			couseCountTv.setText("课程：" + classSize + "|总单词：" + totalWordNum);
 			leftCountTv.setText("|剩余单词: " + totalWordNum);
			
			if(wordManagerList != null && wordManagerList.size() != 0){
				refreshChoFrag();
			}
			
			
			
		}catch(DbException e){
			e.printStackTrace();
		}
	}

	public void refreshChoFrag() {
		// TODO Auto-generated method stub
//		if(wordManagerList.size() == 0){
//			//说明已经学完,应该显示结果统计页
//			Toast.makeText(getActivity(), R.string.word_test_testAll, 0).show();
//			wordTestActivity.showResult();
//		}
		
		AaTv.setTextColor(Color.BLACK);
		AmeanTv.setTextColor(Color.BLACK);
		ArightImv.setVisibility(View.GONE);
		AwrongImv.setVisibility(View.GONE);
		BbTv.setTextColor(Color.BLACK);
		BmeanTv.setTextColor(Color.BLACK);
		BrightImv.setVisibility(View.GONE);
		BwrongImv.setVisibility(View.GONE);
		CcTv.setTextColor(Color.BLACK);
		CmeanTv.setTextColor(Color.BLACK);
		CrightImv.setVisibility(View.GONE);
		CwrongImv.setVisibility(View.GONE);
		DdTv.setTextColor(Color.BLACK);
		DmeanTv.setTextColor(Color.BLACK);
		DrightImv.setVisibility(View.GONE);
		DwrongImv.setVisibility(View.GONE);
		voiceImv.setVisibility(View.VISIBLE);
		
		
		Random random = new Random();
		randomNow = random.nextInt(wordManagerList.size());
		
		wordManager = wordManagerList.get(randomNow);
		DbUtils dbUtils = DbUtils.create(getActivity());
		try {
			wordInfo = dbUtils.findFirst(Selector.from(EmodouWordInfo.class)
												 .where("classid","=",wordManager.getClassid())
												 .and("word_name","=",wordManager.getWordname()));
			if(wordInfo != null){
				if(wordManager.getEntitleRighttimes() == 0 && wordManager.getCntitleRighttimes() == 0){
					randomEnorCn = random.nextInt(2);
					if(randomEnorCn == 0){
						//给英文，出汉意
						setEnTitleFrag();
					}else if(randomEnorCn == 1){
						//给中文，出英文
						setCnTitleFrag();
					}
				}else if(wordManager.getEntitleRighttimes() != 0 && wordManager.getCntitleRighttimes() == 0){
					//给中文，出英文没对过
					setCnTitleFrag();
					randomEnorCn = 1;
				}else if(wordManager.getEntitleRighttimes() == 0 && wordManager.getCntitleRighttimes() != 0){
					//给英文，出中文没对过
					setEnTitleFrag();
					randomEnorCn = 0;
				}
			}
		} catch (DbException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void setEnTitleFrag() {
		titleTv.setText(wordManager.getWordname());
		titleTv.setTextSize(40);
		voiceImv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String mp3Local = wordInfo.getLocal_voice();
				String wordname = wordInfo.getWord_name();
				String mp3Iciba = wordInfo.getVoice();
				if(mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				} else {

					if (mp3Local == null || mp3Local.equals("")) {
						if (mp3Iciba == null) {
							Toast.makeText(getActivity(),
									"该单词没有发音", 0).show();
						} else {
							ValidateUtils.playUrl(mp3Iciba, wordname,
									getActivity(), userid);
						}

					} else {
						ValidateUtils.playUrl(mp3Local, wordname,
								getActivity(), userid);
					}

				}
			}
		});
		Random random = new Random();
		randomAns = random.nextInt(4);
		String meanRight = wordInfo.getMeaning().replaceAll("#", "  ");
		//从本书中所有单词中选择三个不重复的随机数，取出单词,并且该单词还不能与目前该页单词重复
		List<Integer> ranWrongList = new ArrayList<Integer>();
		while (ranWrongList.size()<3) {
			int randomWrong = random.nextInt(wordInfoBookList.size());
			if(!ranWrongList.contains(randomWrong) 
					&& !wordInfoBookList.get(randomWrong).getWord_name().equals(wordInfo.getWord_name()))
				ranWrongList.add(randomWrong);
		}
		String meanWrong1 = wordInfoBookList.get(ranWrongList.get(0)).getMeaning().replaceAll("#", "  ");
		String meanWrong2 = wordInfoBookList.get(ranWrongList.get(1)).getMeaning().replaceAll("#", "  ");
		String meanWrong3 = wordInfoBookList.get(ranWrongList.get(2)).getMeaning().replaceAll("#", "  ");
		if(randomAns == 0){
			AmeanTv.setText(meanRight);
			BmeanTv.setText(meanWrong1);
			CmeanTv.setText(meanWrong2);
			DmeanTv.setText(meanWrong3);
		}else if(randomAns == 1){
			BmeanTv.setText(meanRight);
			AmeanTv.setText(meanWrong1);
			CmeanTv.setText(meanWrong2);
			DmeanTv.setText(meanWrong3);
		}else if(randomAns == 2){
			CmeanTv.setText(meanRight);
			AmeanTv.setText(meanWrong1);
			BmeanTv.setText(meanWrong2);
			DmeanTv.setText(meanWrong3);
		}else if(randomAns == 3){
			DmeanTv.setText(meanRight);
			AmeanTv.setText(meanWrong1);
			BmeanTv.setText(meanWrong2);
			CmeanTv.setText(meanWrong3);
		}
	}
	
	public void setCnTitleFrag() {
		voiceImv.setVisibility(View.GONE);
		titleTv.setText(wordInfo.getMeaning().replaceAll("#", "  "));
		titleTv.setTextSize(15);
		Random random = new Random();
		randomAns = random.nextInt(4);
		//从本书中所有单词中选择三个不重复的随机数，取出单词,并且该单词还不能与目前该页单词重复
		List<Integer> ranWrongList = new ArrayList<Integer>();
		while (ranWrongList.size()<3) {
			int randomWrong = random.nextInt(wordInfoBookList.size());
			if(!ranWrongList.contains(randomWrong) 
					&& !wordInfoBookList.get(randomWrong).getWord_name().equals(wordInfo.getWord_name()))
				ranWrongList.add(randomWrong);
		}
		String nameRight = wordInfo.getWord_name();
		String nameWrong1 = wordInfoBookList.get(ranWrongList.get(0)).getWord_name();
		String nameWrong2 = wordInfoBookList.get(ranWrongList.get(1)).getWord_name();
		String nameWrong3 = wordInfoBookList.get(ranWrongList.get(2)).getWord_name();
		if(randomAns == 0){
			AmeanTv.setText(nameRight);
			BmeanTv.setText(nameWrong1);
			CmeanTv.setText(nameWrong2);
			DmeanTv.setText(nameWrong3);
		}else if(randomAns == 1){
			BmeanTv.setText(nameRight);
			AmeanTv.setText(nameWrong1);
			CmeanTv.setText(nameWrong2);
			DmeanTv.setText(nameWrong3);
		}else if(randomAns == 2){
			CmeanTv.setText(nameRight);
			AmeanTv.setText(nameWrong1);
			BmeanTv.setText(nameWrong2);
			DmeanTv.setText(nameWrong3);
		}else if(randomAns == 3){
			DmeanTv.setText(nameRight);
			AmeanTv.setText(nameWrong1);
			BmeanTv.setText(nameWrong2);
			CmeanTv.setText(nameWrong3);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.word_test_choose_fragment_ARl:
				if(randomAns == 0){
					RightChoice();
				}else if(randomAns == 1 || randomAns == 2 || randomAns == 3){
					WrongChoice(randomAns, 0);
				}	
				break;
				
			case R.id.word_test_choose_fragment_BRl:
				if(randomAns == 1){
					RightChoice();
				}else if(randomAns == 0 || randomAns == 2 || randomAns == 3)
					WrongChoice(randomAns, 1);
				break;
			
			case R.id.word_test_choose_fragment_CRl:
				if(randomAns == 2){
					RightChoice();
				}else if(randomAns == 0 || randomAns == 1 || randomAns == 3)
					WrongChoice(randomAns, 2);
				break;
				
			case R.id.word_test_choose_fragment_DRl:
				if(randomAns == 3){
					RightChoice();
				}else if(randomAns == 0 || randomAns == 1 || randomAns == 2)
					WrongChoice(randomAns, 3);
					break;
	
			default:
				break;
		}
	}
	
	public void RightChoice() {
		//选择正确，直接自刷新本fragment
		DbUtils dbUtils = DbUtils.create(getActivity());
		try {
			//将数据库中该单词的正确次数加1
			EmodouWordManager wordMana = dbUtils.findFirst(Selector.from(EmodouWordManager.class)
					                                                  .where("userid","=",userid)
					                                                  .and("classid","=",wordManager.getClassid())
					                                                  .and("wordname","=",wordManager.getWordname()));
			if(wordMana != null){
				if(randomEnorCn == 0){
					wordMana.setEntitleRighttimes(1);
					wordManagerList.get(randomNow).setEntitleRighttimes(1);
				}else if(randomEnorCn == 1){
					wordMana.setCntitleRighttimes(1);
					wordManagerList.get(randomNow).setCntitleRighttimes(1);
				}
				dbUtils.update(wordMana);
			}
			
			
		} catch (DbException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if(wordManagerList.get(randomNow).getEntitleRighttimes() == 1 
				&& wordManagerList.get(randomNow).getCntitleRighttimes() == 1)
			wordManagerList.remove(randomNow);
		//刷新自身的fragment
		if(wordManagerList.size() != 0)
			refreshChoFrag();
		else if(wordManagerList.size() == 0){
			Toast.makeText(getActivity(), R.string.word_test_testAll, 0).show();
			wordTestActivity.showResult();
		}		
		//刷新进度条
		int leftCount = wordManagerList.size();
		int progress = (totalWordNum - leftCount) * 100 / totalWordNum;
		//wordTestActivity.refreshBar(progress, leftCount);
		progressTv.setText("进度  "+ progress + "%");
		progressBar.setProgress(progress);
		leftCountTv.setText("|剩余单词: " + leftCount);
		Toast.makeText(getActivity(), R.string.word_test_rightChoice, 0).show();
	}
	
	//right为正确选项，wrong为用户所选的错误选项
	public void WrongChoice(int right, int wrong) {
		//选择错误，跳到详细列表页
		DbUtils dbUtils = DbUtils.create(getActivity());
		try {
			//将数据库中该单词的错误次数加1
			EmodouWordManager wordMana = dbUtils.findFirst(Selector.from(EmodouWordManager.class)
					                                               .where("userid","=",userid)
					                                               .and("classid","=",wordManager.getClassid())
					                                               .and("wordname","=",wordManager.getWordname()));
			if(wordMana != null){
				if(randomEnorCn == 0){
					int wrongNum = wordMana.getEntitleWrongtimes();
					wordMana.setEntitleWrongtimes(wrongNum + 1);
					wordManagerList.get(randomNow).setEntitleWrongtimes(wrongNum + 1);
				}else if(randomEnorCn == 1){
					int wrongNum = wordMana.getCntitleWrongtimes();
					wordMana.setCntitleWrongtimes(wrongNum + 1);
					wordManagerList.get(randomNow).setCntitleWrongtimes(wrongNum + 1);
				}
				dbUtils.update(wordMana);
			}
		} catch (DbException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if(right == 0){
			AaTv.setTextColor(Color.GREEN);
			AmeanTv.setTextColor(Color.GREEN);
			ArightImv.setVisibility(View.VISIBLE);
		}else if(right == 1){
			BbTv.setTextColor(Color.GREEN);
			BmeanTv.setTextColor(Color.GREEN);
			BrightImv.setVisibility(View.VISIBLE);
		}else if(right == 2){
			CcTv.setTextColor(Color.GREEN);
			CmeanTv.setTextColor(Color.GREEN);
			CrightImv.setVisibility(View.VISIBLE);
		}else if(right == 3){
			DdTv.setTextColor(Color.GREEN);
			DmeanTv.setTextColor(Color.GREEN);
			DrightImv.setVisibility(View.VISIBLE);
		}
		
		if(wrong == 0){
			AaTv.setTextColor(Color.RED);
			AmeanTv.setTextColor(Color.RED);
			AwrongImv.setVisibility(View.VISIBLE);
		}else if(wrong == 1){
			BbTv.setTextColor(Color.RED);
			BmeanTv.setTextColor(Color.RED);
			BwrongImv.setVisibility(View.VISIBLE);
		}else if(wrong == 2){
			CcTv.setTextColor(Color.RED);
			CmeanTv.setTextColor(Color.RED);
			CwrongImv.setVisibility(View.VISIBLE);
		}else if(wrong == 3){
			DdTv.setTextColor(Color.RED);
			DmeanTv.setTextColor(Color.RED);
			DwrongImv.setVisibility(View.VISIBLE);
		}
		
		Toast.makeText(getActivity(), R.string.word_test_wrongChoice, 0).show();
		//延迟2秒后，给用户出详细界面的fragment
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub				
			}
		}, 1000);
		wordTestActivity.showDetail(wordInfo.getWord_name(), wordManager.getClassid(), wordManager.getUserid());
	}
	

}
