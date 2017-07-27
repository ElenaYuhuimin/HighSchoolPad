package com.emodou.home;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.kymjs.kjframe.ui.BindView;

import com.emodou.domain.EmodouWordClass;
import com.emodou.domain.EmodouWordInfo;
import com.emodou.domain.EmodouWordManager;
import com.emodou.extend.CustomProgressBar;
import com.emodou.extend.ProgressItem;
import com.emodou.util.Constants;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


//单词学习系列界面的fragment

public class WordLearnFragment extends Fragment implements OnClickListener{
	
	//进度条属性
	private CustomProgressBar studybar;
	private ArrayList<ProgressItem> studyproItemList;
	private ProgressItem studyProgressItem;
	private CustomProgressBar reviewBar;
	private ArrayList<ProgressItem> reviewproItemList;
	private ProgressItem reviewProgressItem;
	
	
	//fragment的界面控件
	private TextView wordnameTv, phonenTv, phonamTv, meanTv, tenseCompTv, sentenceTv;	
	private ImageView voiceImv, addwordImv, deletewordImv;
	private RelativeLayout contentRl;
	private TextView meanTitleTv, tenseCompTitleTv;
	private View line1View, line2View;
	
	//fragment的界面数据
	private String bookid, userid;
	private String learnedAll;
	private boolean learnedAllFrag = true;
	private List<EmodouWordManager> wordManagerList = new ArrayList<EmodouWordManager>();
	private EmodouWordManager wordManager = new EmodouWordManager();
	private EmodouWordInfo wordInfo = new EmodouWordInfo();
	private float unFamiCount, notLearnCount, vagueCount, FamiCount;
	private float needReviewCount, ReviewedCount;
	private MediaPlayer mediaPlayer = new MediaPlayer();
	private OnLearnWordFinished mListener;
	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		try {
			mListener = (OnLearnWordFinished)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.getPackageName() + "not implents OnLearnWordFinished");
			// TODO: handle exception
		}
		
		super.onAttach(activity);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState){
		 View view = inflater.inflate(R.layout.word_learn_fragment, container, false);
		 
		 bookid = getActivity().getIntent().getStringExtra("bookid");
		 userid = getActivity().getIntent().getStringExtra("userid");
		 learnedAll = getActivity().getIntent().getStringExtra("learnedAll");
		 
		 if(learnedAll.equals("true"))
			 clearRevState();
		 
		 contentRl = (RelativeLayout)view.findViewById(R.id.word_learn_fragment_contentRl);
		 
		 wordnameTv = (TextView)view.findViewById(R.id.word_learn_fragment_wordname);
		 phonenTv = (TextView)view.findViewById(R.id.word_learn_fragment_phonen);
		 phonamTv = (TextView)view.findViewById(R.id.word_learn_fragment_phonam);
		 
		 meanTitleTv = (TextView)view.findViewById(R.id.word_learn_fragment_meanTitle);
		 meanTv = (TextView)view.findViewById(R.id.word_learn_fragment_mean);
		 line1View = (View)view.findViewById(R.id.word_learn_fragment_line1);
		 
		 tenseCompTitleTv = (TextView)view.findViewById(R.id.word_learn_fragment_tenseComparativeTitle);
		 tenseCompTv = (TextView)view.findViewById(R.id.word_learn_fragment_tenseComparative);
		 line2View = (View)view.findViewById(R.id.word_learn_fragment_line2);
		 	 
		 sentenceTv = (TextView)view.findViewById(R.id.word_learn_fragment_setence);
		 voiceImv = (ImageView)view.findViewById(R.id.word_learn_fragment_voice);
		 voiceImv.setOnClickListener(this);
		 addwordImv = (ImageView)view.findViewById(R.id.word_learn_fragment_addword);
		 addwordImv.setOnClickListener(this);
		 deletewordImv = (ImageView)view.findViewById(R.id.word_learn_fragment_deleteword);
		 deletewordImv.setOnClickListener(this);
		 
		 studybar = ((CustomProgressBar) view.findViewById(R.id.word_learn_fragment_studyBar));
		 studybar.getThumb().mutate().setAlpha(0);
		 reviewBar = ((CustomProgressBar)view.findViewById(R.id.word_learn_fragment_reviewBar));
		 reviewBar.getThumb().mutate().setAlpha(0);
		 
		 init();
		 
		 return view;
	}
	
	public void clearRevState() {
		//将所有单词的review_state的状态都设为空
		DbUtils dbUtils = DbUtils.create(getActivity());
		try{
			
			List<EmodouWordManager> wordManagerList2 = new ArrayList<EmodouWordManager>();
			
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
					wordManagerList2.addAll(wordManagerPartList);
				}
			}
			if(wordManagerList2 != null && wordManagerList2.size() != 0){
				for(EmodouWordManager wordManager : wordManagerList2){
					EmodouWordManager wordManager2 = dbUtils.findFirst(Selector.from(EmodouWordManager.class)
							                                                   .where("userid","=",userid)
							                                                   .and("classid","=",wordManager.getClassid())
							                                                   .and("wordname","=",wordManager.getWordname()));
					if(wordManager2 != null){
						wordManager2.setReviewState(Constants.WORD_REVIEW_STATE_NOT_LEARN);
						dbUtils.update(wordManager2);
					}
					
				}
			}
		}catch(DbException e){
			e.printStackTrace();
		}
	}
	
	public void init() {

		refreshBar();
		initWordManagerList();
		
	}
	
	public void initWordManagerList() {
		wordManagerList.clear();
		//先将这个用户的这本书classid的state为选中的单词列表都拿出来,不论是否已经学完，即是否需要复习
		DbUtils dbUtils = DbUtils.create(getActivity());
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
			//先判断单词是否已经全部学完
			ifLearnedAll();
			
			//刷新fragment，随机出单词,単分一个函数，这样比较逻辑比较清晰
			if(!learnedAllFrag){
				refreshWord();
			}
			
			
		}catch(DbException e){
			e.printStackTrace();
		}
	}
	
	public void setStudyBar(float unFamiPer, float vaguePer, float FamiPer, float notLearnPer) {

		studyproItemList = new ArrayList<ProgressItem>();
		// unfamiliar span
		studyProgressItem = new ProgressItem();
		studyProgressItem.progressItemPercentage = unFamiPer;
		studyProgressItem.color = R.color.word_wordlist_unfamiliarhint_bcg;
		studyproItemList.add(studyProgressItem);
		//vague span
		studyProgressItem = new ProgressItem();
		studyProgressItem.progressItemPercentage = vaguePer;
		studyProgressItem.color = R.color.word_wordlist_vaguehint_bcg;
		studyproItemList.add(studyProgressItem);
		//fami span
		studyProgressItem = new ProgressItem();
		studyProgressItem.progressItemPercentage = FamiPer;
		studyProgressItem.color = R.color.word_wordlist_familiarhint_bcg;
		studyproItemList.add(studyProgressItem);
		
		//notlearn span
		studyProgressItem = new ProgressItem();
		studyProgressItem.progressItemPercentage = notLearnPer;
		studyProgressItem.color =  R.color.white;
		studyproItemList.add(studyProgressItem);

		studybar.initData(studyproItemList);
		studybar.invalidate();
	}
	
	public void setReviewBar(float reviewedPer, float needReviewPer) {
		
		reviewproItemList = new ArrayList<ProgressItem>();
		// reviewed span
		reviewProgressItem = new ProgressItem();
		reviewProgressItem.progressItemPercentage = reviewedPer;
		reviewProgressItem.color = R.color.actionbar_word;
		reviewproItemList.add(reviewProgressItem);
		//needReview span
		reviewProgressItem = new ProgressItem();
		reviewProgressItem.progressItemPercentage = needReviewPer;
		reviewProgressItem.color = R.color.white;
		reviewproItemList.add(reviewProgressItem);
		

		reviewBar.initData(reviewproItemList);
		reviewBar.invalidate();
	}
	
	public void refreshWord() {

		//在wordManagerList的reviewstate中不为2随机出一个单词
		List<EmodouWordManager> wordMaNeedReviewList = new ArrayList<EmodouWordManager>();
		for(EmodouWordManager wordManager : wordManagerList){
			if(wordManager.getReviewState() != Constants.WORD_REVIEW_STATE_RIGHT)
				wordMaNeedReviewList.add(wordManager);
		}
		
		Random random = new Random();
		int randomInt = random.nextInt(wordMaNeedReviewList.size());
		wordManager = wordMaNeedReviewList.get(randomInt);
		
		if(wordManager.getIsAddToNewWordsBood().equals(Constants.WORD_ISADDTONEW_YES)){
			addwordImv.setVisibility(View.GONE);
			deletewordImv.setVisibility(View.VISIBLE);
		}else if(wordManager.getIsAddToNewWordsBood().equals(Constants.WORD_ISADDTONEW_NO)){
			addwordImv.setVisibility(View.VISIBLE);
			deletewordImv.setVisibility(View.GONE);
		}
		
		DbUtils dbUtils = DbUtils.create(getActivity());
		try {
			wordInfo = dbUtils.findFirst(Selector.from(EmodouWordInfo.class)
					                             .where("classid","=",wordManager.getClassid())
					                             .and("word_name","=",wordManager.getWordname()));
			if(wordInfo != null){
				wordnameTv.setText(wordInfo.getWord_name());
				phonenTv.setText("英 【" + wordInfo.getPh_en() + "】  ");
				phonamTv.setText("美 【" + wordInfo.getPh_am() + "】");
				meanTv.setText(wordInfo.getMeaning().replaceAll("#", "  "));
				
				//settext会清空原来字符串，append会自动连接
				StringBuilder exchageBuilder = new StringBuilder();
				if (wordInfo.getWord_done() != null && !wordInfo.getWord_done().equals("")) {
					exchageBuilder.append("过去式：" + wordInfo.getWord_done().replaceAll("#", "/"));
				}
				if (wordInfo.getWord_er() != null && !wordInfo.getWord_er().equals("")) {
					exchageBuilder.append(" 比较级：" + wordInfo.getWord_er().replaceAll("#", "/"));
				}
				if (wordInfo.getWord_est() != null && !wordInfo.getWord_est().equals("")) {
					exchageBuilder.append(" 最高级：" + wordInfo.getWord_est().replaceAll("#", "/"));
				}
				if (wordInfo.getWord_ing() != null && !wordInfo.getWord_ing().equals("")) {
					exchageBuilder.append(" 进行式：" + wordInfo.getWord_ing().replaceAll("#", "/"));
				}
				if (wordInfo.getWord_past() != null && !wordInfo.getWord_past().equals("")) {
					exchageBuilder.append(" 最高级：" + wordInfo.getWord_past().replaceAll("#", "/"));
				}

				if (exchageBuilder.toString().equals("")) {
					exchageBuilder.append("词库完善中，敬请期待！");
				}
				tenseCompTv.setText(exchageBuilder.toString());
				
				SpannableStringBuilder spanStrContent = new SpannableStringBuilder(wordInfo.getSentence());
				ForegroundColorSpan span = new ForegroundColorSpan(Color.rgb(48, 169, 216));
				int start = wordInfo.getSentence().indexOf(wordInfo.getWord_name());
				if(start >= 0 ){
					spanStrContent.setSpan(span, start, start + wordInfo.getWord_name().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					sentenceTv.setText(spanStrContent);
				}else{
					sentenceTv.setText(wordInfo.getSentence());
				}
				
			}
		} catch (DbException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public void ifLearnedAll() {
		learnedAllFrag = true;
		for(EmodouWordManager wordManager : wordManagerList){
			if(wordManager.getReviewState() != Constants.WORD_REVIEW_STATE_RIGHT){
				learnedAllFrag = false;
				break;
			}
				
		}
		if(learnedAllFrag){
//			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
//			.setTitle(R.string.prompt)
//			.setMessage(getResources().getString(R.string.word_learn_learnedAll))
//			.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() { 
//				 public void onClick(DialogInterface dialog, int whichButton) { 
//					 //调用父类方法，返回单词课程列表
////					 WordLearnActivity parent = (WordLearnActivity)getActivity();
////					 parent.returnWordlistAct();
//					 
//		           }	           				            
//		       });
//			dialog.show();	
//			Intent intent = new Intent(context, testActivity.class);
//			intent.putExtra("bookid", bookid);
//			startActivity(intent);
			mListener.onFinished();
			//在数据库中设置该课的学习状态为已学完,需要查找课程列表
            //ValidateUtils.setClassLearned(getActivity(), wordManager.getClassid(), wordManager.getUserid());
//			new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					Message message = new Message();
//		            message.what = RETURN;
//		            handler.sendMessage(message);
//				}
//			}).start();
            
			
		}
	}

	
//	private Handler handler = new Handler() {
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//				case RETURN:
//					Intent intent = new Intent(context, testActivity.class);
//					intent.putExtra("bookid", bookid);
//					startActivity(intent);
//					Toast.makeText(context, "return", 1).show();
//					break;
//	
//				default:
//					break;
//			}
//		}
//	};

	
	public interface OnLearnWordFinished{
		void onFinished();
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.word_learn_fragment_voice:
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
				break;
				
			case R.id.word_learn_fragment_addword:
				ValidateUtils.addToNewWord(getActivity(), wordInfo.getWord_name(), wordInfo.getClassid());
				addwordImv.setVisibility(View.GONE);
				deletewordImv.setVisibility(View.VISIBLE);
				Toast.makeText(getActivity(), wordInfo.getWord_name()+
						getResources().getString(R.string.word_detail_added), 0).show();
				break;
				
			case R.id.word_learn_fragment_deleteword:
				ValidateUtils.deleteFromNewWord(getActivity(), wordInfo.getWord_name(), wordInfo.getClassid());
				addwordImv.setVisibility(View.VISIBLE);
				deletewordImv.setVisibility(View.GONE);
				Toast.makeText(getActivity(), wordInfo.getWord_name()+
						getResources().getString(R.string.word_detail_deleted), 0).show();
				break;
				
			default:
				break;
		}
	}
	
	public void hideContentRl() {
		contentRl.setVisibility(View.INVISIBLE);
	}

	public void showSententce() {
		contentRl.setVisibility(View.VISIBLE);
		meanTitleTv.setVisibility(View.GONE);
		meanTv.setVisibility(View.GONE);
		line1View.setVisibility(View.GONE);
		tenseCompTitleTv.setVisibility(View.GONE);
		tenseCompTv.setVisibility(View.GONE);
		line2View.setVisibility(View.GONE);
	}
	
	public void showAll() {
		contentRl.setVisibility(View.VISIBLE);
		meanTitleTv.setVisibility(View.VISIBLE);
		meanTv.setVisibility(View.VISIBLE);
		line1View.setVisibility(View.VISIBLE);
		tenseCompTitleTv.setVisibility(View.VISIBLE);
		tenseCompTv.setVisibility(View.VISIBLE);
		line2View.setVisibility(View.VISIBLE);
	}
	
	public void setSqlLearnUnfami() {
		DbUtils dbUtils = DbUtils.create(getActivity());
		try {
			EmodouWordManager wordManager2 = dbUtils.findFirst(Selector.from(EmodouWordManager.class)
					                                                   .where("userid","=",userid)
					                                                   .and("classid","=",wordManager.getClassid())
					                                                   .and("wordname","=",wordManager.getWordname()));
			if(wordManager2 != null){
				if(wordManager2.getReviewState() == Constants.WORD_REVIEW_STATE_NOT_LEARN
						&& wordManager2.getLearnState() == Constants.WORD_STATE_NOT_LEARN){
					//说明该单词是用户第一次学,复习状态不改变，依旧为0
				}else if(wordManager2.getReviewState() == Constants.WORD_REVIEW_STATE_NOT_LEARN
						&& wordManager2.getLearnState() != Constants.WORD_STATE_NOT_LEARN){
					//这种状态下是两种情况，一种是用户第一次学习，但单词是第二次出；
					//第二种情况是用户再次点击学习进来，之前那次都复习学习过了，所以review_state被清空，这时learnstate状态一定为2
					wordManager2.setReviewState(Constants.WORD_REVIEW_STATE_WRONG);
				}else if(wordManager2.getReviewState() == Constants.WORD_REVIEW_STATE_WRONG){
					//这种状态是两种情况，这种状态下不做任何操作
					//第一种情况是用户第一次学习，单词第三次以上出
					//第二种情况是用户再次点击学习进来，单词第二次以上出，这时单词状态为（-1,1）或（1,1）
				}
				
				//最新版API加上的
				int times = wordManager2.getCwrong();
				wordManager2.setCwrong(times + 1);
				wordManager2.setLast(Constants.WORD_LAST_WRONG);
				
				wordManager2.setLearnState(Constants.WORD_STATE_UNFAMILIAR);
				dbUtils.update(wordManager2);
			}
		} catch (DbException e) {
			// TODO: handle exception
		}
	}
	
	public void setSqlLearnVague() {
		DbUtils dbUtils = DbUtils.create(getActivity());
		try {
			EmodouWordManager wordManager2 = dbUtils.findFirst(Selector.from(EmodouWordManager.class)
					                                                   .where("userid","=",userid)
					                                                   .and("classid","=",wordManager.getClassid())
					                                                   .and("wordname","=",wordManager.getWordname()));
			if(wordManager2 != null){
				if(wordManager2.getReviewState() == Constants.WORD_REVIEW_STATE_NOT_LEARN
						&& wordManager2.getLearnState() == Constants.WORD_STATE_NOT_LEARN){
					//说明该单词是用户第一次学，复习状态不改变，依旧为0
				}else if(wordManager2.getReviewState() == Constants.WORD_REVIEW_STATE_NOT_LEARN
						&& wordManager2.getLearnState() != Constants.WORD_STATE_NOT_LEARN){
					//这种状态下是两种情况，一种是用户第一次学习，但单词是第二次出；
					//第二种情况是用户再次点击学习进来，之前那次都复习学习过了，所以review_state被清空，单词第一次出，，这时learnstate状态一定为2
					wordManager2.setReviewState(Constants.WORD_REVIEW_STATE_WRONG);
				}else if(wordManager2.getReviewState() == Constants.WORD_REVIEW_STATE_WRONG){
					//这种状态是两种情况，这种状态下不做任何操作
					//第一种情况是用户第一次学习，单词第三次以上出
					//第二种情况是用户再次点击学习进来，单词第二次以上出，这时单词状态从（-1,1）或（1,1）变为（1,1）
				}
				
				//最新版API加上的
				int times = wordManager2.getCprompt();
				wordManager2.setCprompt(times + 1);
				wordManager2.setLast(Constants.WORD_LAST_HINT);
				
				wordManager2.setLearnState(Constants.WORD_STATE_VAGUE);
				dbUtils.update(wordManager2);
			}
		} catch (DbException e) {
			// TODO: handle exception
		}
	}
	
	
	public void setSqlLearnFami() {
		DbUtils dbUtils = DbUtils.create(getActivity());
		try{
			EmodouWordManager wordManager2 = dbUtils.findFirst(Selector.from(EmodouWordManager.class)
					                                                  .where("userid","=",userid)
					                                                  .and("classid","=",wordManager.getClassid())
					                                                  .and("wordname","=",wordManager.getWordname()));
			if(wordManager2 != null){
				
				if(wordManager2.getReviewState() == Constants.WORD_REVIEW_STATE_NOT_LEARN
						&& wordManager2.getLearnState() == Constants.WORD_STATE_NOT_LEARN){
					//说明该单词用户是第一次学，且第一次出，复习状态不改变，依旧为0
				}else if(wordManager2.getReviewState() == Constants.WORD_REVIEW_STATE_NOT_LEARN
						&& wordManager2.getLearnState() != Constants.WORD_STATE_NOT_LEARN){
					//这种状态分为两种情况
					//一种情况是用户第一次学习,或者继续学习
					//第二种情况是用户第二次学习，用户的状态由初始状态（2,0）变为（2，2）
					wordManager2.setReviewState(Constants.WORD_REVIEW_STATE_RIGHT);
//				    else if(learnedAll == "true"){
//						
//						if(wordManager2.getLearnState() == Constants.WORD_STATE_FAMILIAR)
//							wordManager2.setReviewState(Constants.WORD_REVIEW_STATE_RIGHT);
//						else 
//							wordManager2.setReviewState(Constants.WORD_REVIEW_STATE_WRONG);
//					}
				}else if(wordManager2.getReviewState() == Constants.WORD_REVIEW_STATE_WRONG){
					//这种状态分为两种情况
					//第一种情况是第一次学习，单词第二次以上出，用户选对了
					//第二种情况是第二次学习，单词最初是例如-1,0，变为2,1，接下来再出时用户选对了，则做这个操作，变为2,2（删）
					//第二种情况是第二次学习，单词从（-1,1）或（1,1）变为（2,2）
					wordManager2.setReviewState(Constants.WORD_REVIEW_STATE_RIGHT);
				}
				
				//不论是以上那种情况，这时都要记录下当前的learn_state以及时间
				wordManager2.setLastState(wordManager2.getLearnState());
				wordManager2.setLastStateTime(System.currentTimeMillis());
				
				//最新版API加上的
				int times = wordManager2.getCright();
				wordManager2.setCright(times + 1);
				wordManager2.setLast(Constants.WORD_LAST_RIGHT);
				
				wordManager2.setLearnState(Constants.WORD_STATE_FAMILIAR);
				dbUtils.update(wordManager2);
			}			
		}catch(DbException e){
			e.printStackTrace();
		}
	}
	
	public void refreshBar() {
		//由于bar和单词刷新的时间不同步，因此要单独将这个函数拿出来，否则会出现复习到最后一个单词时，仍有留白的问题
		unFamiCount = notLearnCount = vagueCount = FamiCount = 0;
        needReviewCount = ReviewedCount = 0;
        
        List<EmodouWordManager> wordManagerList2 = new ArrayList<EmodouWordManager>();
		//先将这个用户的这本书classid的state为选中的单词列表都拿出来,不论是否已经学完，即是否需要复习
		DbUtils dbUtils = DbUtils.create(getActivity());
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
					wordManagerList2.addAll(wordManagerPartList);
				}
			}
			//由于在WordlistActivity界面进行跳转的时候已经判断所选课程中有未学完的单词，因此继续学习即可
			//先初始化下面的进度列表
			//对查出来的wordManagerList进行遍历，统计两个进度条的状态
			if(wordManagerList2 != null && wordManagerList2.size()!=0){
				for(EmodouWordManager wordManager : wordManagerList2){
					if(wordManager.getLearnState() == Constants.WORD_STATE_UNFAMILIAR)
						unFamiCount++;
					else if(wordManager.getLearnState() == Constants.WORD_STATE_NOT_LEARN)
						notLearnCount++;
					else if(wordManager.getLearnState() == Constants.WORD_STATE_FAMILIAR)
						FamiCount++;
					else if(wordManager.getLearnState() == Constants.WORD_STATE_VAGUE)
						vagueCount++;
					
					if(wordManager.getReviewState() == Constants.WORD_REVIEW_STATE_RIGHT)
						ReviewedCount++;
					else if(wordManager.getReviewState() == Constants.WORD_REVIEW_STATE_NOT_LEARN
							||wordManager.getReviewState() == Constants.WORD_REVIEW_STATE_WRONG)
						needReviewCount++;
				}
			}
			
			float unFamiPer = unFamiCount * 100 / wordManagerList2.size();
			float notLearnPer = notLearnCount * 100 / wordManagerList2.size();
			float FamiPer = FamiCount * 100 / wordManagerList2.size();
			float vaguePer = vagueCount * 100 / wordManagerList2.size();
			
			Toast.makeText(getActivity(), "" + unFamiPer + "/" + vaguePer + "/" + FamiPer + "/" + notLearnPer, 0);
			
			float reviewedPer = ReviewedCount * 100 / wordManagerList2.size();
			float needReviewPer = needReviewCount * 100 / wordManagerList2.size();
			
			Toast.makeText(getActivity(), ""+ reviewedPer + "/" + needReviewPer, 0);
			
			setStudyBar(unFamiPer, vaguePer, FamiPer, notLearnPer);
			setReviewBar(reviewedPer, needReviewPer);
			
			
		}catch(DbException e){
			e.printStackTrace();
		}
	}
	
	public float getUnFamiCount() {
		return unFamiCount;
	}
	
	public float getVagueCount() {
		return vagueCount;
	}
	
	public float getFamiCount() {
		return FamiCount;
	}
	
	public float getNotLearnCount() {
		return notLearnCount;
	}
}
