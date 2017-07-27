package com.emodou.home;

import java.util.ArrayList;

import com.emodou.domain.EmodouUserInfo;
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
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WordwDetailFragment  extends Fragment implements OnClickListener{
	
	
	//界面属性
	private String classid, userid, hint;
	private int index;
	private ArrayList<String> wordNameList = new ArrayList<String>();
	private TextView wordnameTv, phonen, phonus, trans, tenseComparative, sentence;
	private ImageView imgVoice;
	private EmodouWordInfo wordInfo;
	private EmodouWordManager wordManager;
	private MediaPlayer mediaPlayer = new MediaPlayer();
	private ImageButton imbAddWord, imbDeleteWord;
		
	public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState){
		 View view = inflater.inflate(R.layout.word_wordlist_detail_fragment, container, false);
		 
		 classid = getActivity().getIntent().getStringExtra("classid");
		 index = getActivity().getIntent().getExtras().getInt("index");
		 wordNameList = getActivity().getIntent().getStringArrayListExtra("wordNameList");
		 hint = getActivity().getIntent().getStringExtra("hint");//为了区分是从WordwlistActivity页面跳转过来，还是从WordneWordlistActvity页面跳转过来
		 //单词wordname的颜色不同，同时到最后一个单词时，AlertDialog的返回提示不同
		 
		 wordnameTv = (TextView)view.findViewById(R.id.word_detail_wordname);
		phonen = (TextView)view.findViewById(R.id.word_detail_phonen);
		phonus = (TextView)view.findViewById(R.id.word_detail_phonam);
		trans = (TextView)view.findViewById(R.id.word_detail_mean);
		tenseComparative = (TextView)view.findViewById(R.id.word_detail_tenseComparative);
		sentence = (TextView)view.findViewById(R.id.word_detail_setence);
		imgVoice = (ImageView)view.findViewById(R.id.word_detail_voice);
		imgVoice.setOnClickListener(this);
		imbAddWord = (ImageButton)view.findViewById(R.id.word_detail_addword);
		imbAddWord.setOnClickListener(this);
		imbDeleteWord = (ImageButton)view.findViewById(R.id.word_detail_deleteword);
		imbDeleteWord.setOnClickListener(this);
			
		 refresh(index);
		 
		 return view;
	}
	
	
	
	public void refresh(int index) {

		this.index = index;
		DbUtils dbUtils = DbUtils.create(getActivity());
		try{
			EmodouUserInfo userInfo = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
			userid = userInfo.getUserid();
			wordInfo = dbUtils.findFirst(Selector.from(EmodouWordInfo.class)
										.where("word_name","=",wordNameList.get(index))
										.and("classid","=",classid));
			wordManager = dbUtils.findFirst(Selector.from(EmodouWordManager.class)
													.where("wordname","=",wordNameList.get(index))
													.and("classid","=",classid)
													.and("userid","=",userid));
													
													
		}catch(DbException e){
			e.printStackTrace();
		}
		
		if(wordManager.getIsAddToNewWordsBood().equals(Constants.WORD_ISADDTONEW_NO)){
			//尚未添加到生词本
			imbAddWord.setVisibility(View.VISIBLE);
			imbDeleteWord.setVisibility(View.GONE);
		}else if(wordManager.getIsAddToNewWordsBood().equals(Constants.WORD_ISADDTONEW_YES)){
			imbAddWord.setVisibility(View.GONE);
			imbDeleteWord.setVisibility(View.VISIBLE);
		}
		
		wordnameTv.setText(wordInfo.getWord_name());
		
		if(hint.equals("fromWordwlistActivity")){
			if(wordManager.getLearnState() == com.emodou.util.Constants.WORD_STATE_NOT_LEARN){
				wordnameTv.setTextColor(Color.BLACK);
			}else if(wordManager.getLearnState() == com.emodou.util.Constants.WORD_STATE_UNFAMILIAR){
				wordnameTv.setTextColor(getResources().getColor(R.color.word_wordlist_unfamiliarhint_bcg));
			}else if(wordManager.getLearnState() == com.emodou.util.Constants.WORD_STATE_VAGUE){
				wordnameTv.setTextColor(getResources().getColor(R.color.word_wordlist_vaguehint_bcg));
			}else if(wordManager.getLearnState() == com.emodou.util.Constants.WORD_STATE_FAMILIAR){
				wordnameTv.setTextColor(getResources().getColor(R.color.word_wordlist_familiarhint_bcg));
			}
		}
		
		
		
		phonen.setText("英 【" + wordInfo.getPh_en() + "】  ");
		phonus.setText("美 【" + wordInfo.getPh_am() + "】");
		trans.setText(wordInfo.getMeaning().replaceAll("#", "  "));
		
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
		tenseComparative.setText(exchageBuilder.toString());
		
		SpannableStringBuilder spanStrContent = new SpannableStringBuilder(wordInfo.getSentence());
		ForegroundColorSpan span = new ForegroundColorSpan(Color.rgb(48, 169, 216));
		int start = wordInfo.getSentence().indexOf(wordInfo.getWord_name());
		if(start >= 0 ){
			spanStrContent.setSpan(span, start, start + wordInfo.getWord_name().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			sentence.setText(spanStrContent);
		}else{
			sentence.setText(wordInfo.getSentence());
		}
		
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
	
			case R.id.word_detail_voice:			
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
				
			case R.id.word_detail_addword:
				ValidateUtils.addToNewWord(getActivity(), wordNameList.get(index), classid);
				imbAddWord.setVisibility(View.GONE);
				imbDeleteWord.setVisibility(View.VISIBLE);
				Toast.makeText(getActivity(), wordNameList.get(index)+
						getResources().getString(R.string.word_detail_added), 0).show();
			    break;
			    
			case R.id.word_detail_deleteword:
				ValidateUtils.deleteFromNewWord(getActivity(), wordNameList.get(index), classid);
				imbAddWord.setVisibility(View.VISIBLE);
				imbDeleteWord.setVisibility(View.GONE);
				Toast.makeText(getActivity(), wordNameList.get(index)+
						getResources().getString(R.string.word_detail_deleted), 0).show();
				break;
			
			
				
			default:
				break;
		}
	}
	
	

}
