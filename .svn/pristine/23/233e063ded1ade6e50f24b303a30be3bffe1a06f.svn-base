package com.emodou.home;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.emodou.domain.EmodouClass;
import com.emodou.domain.EmodouUnit;
import com.emodou.domain.EmodouWordForWlist;
import com.emodou.extend.FlipLayout;
import com.emodou.extend.FlipLayout.OnFlipListener;
import com.emodou.util.Constants;
import com.emodou.util.LogUtil;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.iflytek.cloud.InitListener;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class WordSearchActivity extends Activity implements OnClickListener{
	
	//actionbar及其相关
	private ActionBar actionbar;
	private TextView titletext;
	private ImageButton imbReturn;
	private RelativeLayout rl_color;
	private ImageButton deleteTotal;
	
	//界面属性
	private ImageView enFindImv, cnFindImv;
	private EditText editText;
	private ListView listView;
	private listAdapter listadapter;
	private MediaPlayer voicePlayer = new MediaPlayer();
	
	private String bookid;
	private List<EmodouWordForWlist> wordForWlists = new ArrayList<EmodouWordForWlist>();
	private static final int CHANGE_EN = 1;
	private static final int CHANGE_CN = 2;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.word_search);
		
		setActionbar();
		
		init();
	}
	
	public void setActionbar(){
 		
 		actionbar = getActionBar();
 		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
 		actionbar.setCustomView(R.layout.actionbar_courselist);
 		
 		View view = actionbar.getCustomView();
 		
 		//actionbar属性获取操作
 		rl_color = (RelativeLayout)view.findViewById(R.id.couselist_color);
 		titletext = (TextView)view.findViewById(R.id.courselist_ancionbar_text);	
 		imbReturn = (ImageButton)view.findViewById(R.id.courselist_imbtn_return);
 		imbReturn.setOnClickListener(this);
 		deleteTotal = (ImageButton)view.findViewById(R.id.courselist_imbtn_delete);
 		deleteTotal.setOnClickListener(this);
 		
 		
 		rl_color.setBackgroundColor(getResources().getColor(R.color.actionbar_word));
 		titletext.setText(getResources().getString(R.string.word_search_title));		
 	}
	
	public void init() {
		enFindImv = (ImageView)findViewById(R.id.word_search_find_change_en);
		enFindImv.setOnClickListener(this);
		cnFindImv = (ImageView)findViewById(R.id.word_search_find_change_cn);
		cnFindImv.setOnClickListener(this);
		editText = (EditText)findViewById(R.id.word_search_find_edt);
		
		listView = (ListView)findViewById(R.id.word_search_listview);
		
		bookid = getIntent().getStringExtra("bookid");
		
		transEnOrCn();
		
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			final DbUtils dbUtils = DbUtils.create(WordSearchActivity.this);
			try {
				String search = msg.getData().getString("searchStr");
				switch (msg.what) {
					case CHANGE_EN:
						SearchWordEn(search, dbUtils);
						break;
						
					case CHANGE_CN:
						SearchWordCn(search, dbUtils);
						break;
		
					default:
						break;
				}
				listadapter = new listAdapter(wordForWlists, search,  WordSearchActivity.this);
				listView.setAdapter(listadapter);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	};
	
	public void transEnOrCn() {
		//不能放在create函数中，否则只会被执行一次
		if(enFindImv.getVisibility() == View.VISIBLE)
			editText.setHint("search");
		else if(cnFindImv.getVisibility() == View.VISIBLE)
			editText.setHint("查词");
		
		editText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				 editText.removeTextChangedListener(this);//解除文字改变事件
                 int index = editText.getSelectionStart();//获取光标位置
                 String searchStr = s.toString().toLowerCase();
                 editText.setText(s);//转换成小写
                 editText.setSelection(index);//重新设置光标位置
                 editText.addTextChangedListener(this);//重新绑定事件
                 if(!TextUtils.isEmpty(searchStr)){
                	 //防止下面出现空指针
                	 listView.setVisibility(View.VISIBLE);
                	 if(enFindImv.getVisibility() == View.VISIBLE){
                		 Message message = new Message();
                		 message.what = CHANGE_EN;
                		 Bundle data = new Bundle();
                		 data.putString("searchStr", searchStr);
                		 message.setData(data);
                		 handler.sendMessage(message);
                	 }
  					 else if(cnFindImv.getVisibility() == View.VISIBLE){
  						 Message message = new Message();
	               		 message.what = CHANGE_CN;
	               		 Bundle data = new Bundle();
	               		 data.putString("searchStr", searchStr);
	               		 message.setData(data);
	               		 handler.sendMessage(message);
  					 }
		         }else{
		        	 listView.setVisibility(View.GONE);
		        	 Toast.makeText(WordSearchActivity.this, getResources().getString(R.string.word_search_noresult), 0).show();
		         }
                 
			}
		});
		
	}
	
	
	
	public void SearchWordEn(String s , DbUtils db) {
		String sql = "SELECT * FROM com_emodou_domain_EmodouWordManager as m " +
			         "where m.wordname LIKE '" + s + "%'";
		wordForWlists = ValidateUtils.getWordManagerListBySql(this, db, sql);
		wordForWlists = CompareWord(wordForWlists);
	}
	
	public void SearchWordCn(String s, DbUtils db) {
		String sql = "SELECT * FROM com_emodou_domain_EmodouWordInfo as m " +
		             "where m.meaning LIKE '%" + s + "%'";
	    wordForWlists = ValidateUtils.getWordInfoListBySql(this, db, sql);
	}
	
	
	//根据其重载实现
    public List<EmodouWordForWlist> CompareWord(List<EmodouWordForWlist> wordManagerList){
    	
    	Collections.sort(wordManagerList, new Comparator<EmodouWordForWlist>() {
    		public int compare(EmodouWordForWlist word1, EmodouWordForWlist word2) {
				return word1.getWordname().compareTo(word2.getWordname());
			}
		});
    	return wordManagerList;
    }
    
	private static class ViewHolder{
		ImageView findlabel, checkImv;
		TextView wordnameTv, wordmeanTv;
		FlipLayout flipItem;
	}
	
	private class listAdapter extends BaseAdapter{
		
		private List<EmodouWordForWlist> wordlist = new ArrayList<EmodouWordForWlist>();
		private LayoutInflater inflater;
		private int[] flip;//防止刷新时position与holder的位置不同
		private String s;//为了显示高亮
		
		public listAdapter(List<EmodouWordForWlist> mywordlist, String s,  Context context){
			super();
			this.wordlist = mywordlist;
			inflater = LayoutInflater.from(context);
			flip = new int[wordlist.size()];
			for (int i = 0; i < wordlist.size(); i++) {
				flip[i] = -1;//初始状态为-1即不需要翻转
			}
			this.s = s;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return wordlist.size();
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return wordlist.get(position);
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			final ViewHolder viewholder;
			if(convertView == null){
				view = inflater.inflate(R.layout.word_newordlist_item, parent, false);
				viewholder = new ViewHolder();
				viewholder.findlabel = (ImageView)view.findViewById(R.id.word_newordlist_item_findlabel);
				viewholder.checkImv = (ImageView)view.findViewById(R.id.word_newordlist_check);
				viewholder.wordnameTv = (TextView)view.findViewById(R.id.word_newordlist_item_wordname);
				viewholder.wordmeanTv = (TextView)view.findViewById(R.id.word_newordlist_mean);
				viewholder.flipItem = (FlipLayout)view.findViewById(R.id.word_newordlist_item_fliplayout);
				view.setTag(viewholder);
			}else{
				viewholder = (ViewHolder)view.getTag();
			}
			
			viewholder.findlabel.setVisibility(View.GONE);
					
					
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 
					ViewGroup.LayoutParams.WRAP_CONTENT);//定义一个LayoutParams
			if(enFindImv.getVisibility() == View.VISIBLE){
				
				viewholder.wordnameTv.setTextSize(30);
				layoutParams.setMargins(150, 30, 150, 30);
				viewholder.wordnameTv.setLayoutParams(layoutParams);
				
				SpannableStringBuilder spanStrContent = new SpannableStringBuilder(wordlist.get(position).getWordname());
				ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.actionbar_word));
				int start = wordlist.get(position).getWordname().indexOf(s);
				if(start >= 0 ){
					spanStrContent.setSpan(span, start, start + s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					viewholder.wordnameTv.setText(spanStrContent);
				}else{
					viewholder.wordnameTv.setText(wordlist.get(position).getWordname());
				}
				
				viewholder.wordmeanTv.setText(wordlist.get(position).getWordmean().replaceAll("#", " "));
			}else if(cnFindImv.getVisibility() == View.VISIBLE){
				
				viewholder.wordnameTv.setTextSize(16);
				layoutParams.setMargins(50, 8, 50, 8);
				viewholder.wordnameTv.setLayoutParams(layoutParams);
				
				SpannableStringBuilder spanStrContent = new SpannableStringBuilder(wordlist.get(position).getWordmean().replaceAll("#", " "));
				ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.actionbar_word));
				int start = wordlist.get(position).getWordmean().replaceAll("#", " ").indexOf(s);
				if(start >= 0 ){
					spanStrContent.setSpan(span, start, start + s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					viewholder.wordnameTv.setText(spanStrContent);
				}else{
					viewholder.wordnameTv.setText(wordlist.get(position).getWordmean().replaceAll("#", " "));
				}
				
				viewholder.wordmeanTv.setText(wordlist.get(position).getWordname());
			}
			
			
			if(flip[position] == 1){
				viewholder.flipItem.flip();
				flip[position] = -1;
			}
			
			//设置翻转音频
			final String mp3Iciba = wordlist.get(position).getVoice();
			final String mp3Local = wordlist.get(position).getLocal_voice();
			final String wordnameFinal = wordlist.get(position).getWordname();	
			final String userid = wordlist.get(position).getUserid();
			
			//正面在上为true，反面在上为false
			if(wordlist.get(position).getFlipState() == false){
				viewholder.flipItem.getFrontView().setVisibility(View.GONE);
				viewholder.flipItem.getBackView().setVisibility(View.VISIBLE);
				
			}else if(wordlist.get(position).getFlipState() == true){
				viewholder.flipItem.getFrontView().setVisibility(View.VISIBLE);
				viewholder.flipItem.getBackView().setVisibility(View.GONE);
			}
			viewholder.flipItem.setOnFlipListener(new OnFlipListener() {		
				
				@Override
				public void onFlipStart(FlipLayout view) {
					// TODO Auto-generated method stub
						
				}
				
				
				@Override
				public void onFlipEnd(FlipLayout view) {
					// TODO Auto-generated method stub
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(wordlist.get(position).getFlipState() == false){
								wordlist.get(position).setFlipState(true);
								flip[position] = 1;
								listadapter.notifyDataSetChanged();
							}
						}
					}, 3000);
					
					
				}
				
				@Override
				public void onClick(FlipLayout view) {
					// TODO Auto-generated method stub
					if(mp3Iciba == null && mp3Local == null){
						Toast.makeText(WordSearchActivity.this, R.string.word_wordlist_novoice, 1).show();
					}else{
						if(voicePlayer.isPlaying()){
							voicePlayer.stop();
						}else{
							if(mp3Local == null || mp3Local.equals("")){
								if(mp3Iciba == null){
									Toast.makeText(WordSearchActivity.this, R.string.word_wordlist_novoice, 0).show();
								}else{
									//播放网络音频
									ValidateUtils.playUrl(mp3Iciba, wordnameFinal, WordSearchActivity.this, userid);
								}
							}else{
								//播放本地音频
								ValidateUtils.playUrl(mp3Local, wordnameFinal, WordSearchActivity.this, userid);
							}
						}
					}
					//正面在上为true，反面在上为false
					if(wordlist.get(position).getFlipState() == true){
						wordlist.get(position).setFlipState(false);
						viewholder.flipItem.reset();
						listadapter.notifyDataSetChanged();
					}					
					else if(wordlist.get(position).getFlipState() == false){
						wordlist.get(position).setFlipState(true);
						viewholder.flipItem.flip();
						listadapter.notifyDataSetChanged();
					}
				}
			});
			
			viewholder.checkImv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					ArrayList<String> wordNameList = new ArrayList<String>();
					wordNameList.add(wordlist.get(position).getWordname());
					Intent intent = new Intent(WordSearchActivity.this, WordwDetailActivity.class);
					intent.putStringArrayListExtra("wordNameList", wordNameList);
					intent.putExtra("classid", wordlist.get(position).getClassid());
					intent.putExtra("index", 0);
					intent.putExtra("bookid", bookid);
					intent.putExtra("hint", "fromWordSearchActivity");
					startActivity(intent);
				}
			});
			
			return view;
		}
		
		
	}
	
	@Override
	public void onClick(View v) { 
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.courselist_imbtn_return:
				Intent intent = new Intent(WordSearchActivity.this, WordmainActivity.class);
				intent.putExtra("bookid", bookid);
				startActivity(intent);
				break;
				
			case R.id.word_search_find_change_en:
				enFindImv.setVisibility(View.GONE);
				cnFindImv.setVisibility(View.VISIBLE);
				transEnOrCn();
				Toast.makeText(WordSearchActivity.this, getResources().getString(R.string.word_search_inputCn), 0).show();
				break;
				
			case R.id.word_search_find_change_cn:
				cnFindImv.setVisibility(View.GONE);
				enFindImv.setVisibility(View.VISIBLE);
				transEnOrCn();
				Toast.makeText(WordSearchActivity.this, getResources().getString(R.string.word_search_inputEn), 0).show();
				break;
	
			default:
				break;
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(WordSearchActivity.this, WordmainActivity.class);
		intent.putExtra("bookid", bookid);
		startActivity(intent);
		super.onBackPressed();
	}

}
