package com.emodou.home;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import javax.security.auth.PrivateCredentialPermission;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;

import android.app.Activity;

import com.emodou.domain.EmodouUserInfo;
import com.emodou.domain.EmodouWordClass;
import com.emodou.domain.EmodouWordInfo;
import com.emodou.domain.EmodouWordForWlist;
import com.emodou.extend.FlipLayout;
import com.emodou.extend.FlipLayout.OnFlipListener;
import com.emodou.util.Constants;
import com.emodou.util.LogUtil;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.example.emodou.R.string;
import com.iflytek.cloud.InitListener;
import com.iflytek.ise.result.FinalResult;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;


import android.R.bool;
import android.R.integer;
import android.R.menu;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//单词 单词列表界面
public class WordwlistActivity extends Activity implements OnClickListener, OnFlipListener{

	
	
	//actionbar及其相关
	private ActionBar actionbar;
	private TextView titletext;
	private ImageButton imbReturn, imbdelete;
	private RelativeLayout rl_color;
	private ImageButton imvFilter;
	
	//主界面属性
	private String bookid, userid, classid, hint;
	private ListView wordListView;
	private List<EmodouWordForWlist> wordList = new ArrayList<EmodouWordForWlist>();
	private List<EmodouWordForWlist> wordlist_unfami = new ArrayList<EmodouWordForWlist>();
	private List<EmodouWordForWlist> wordlist_notlearn = new ArrayList<EmodouWordForWlist>();
	private List<EmodouWordForWlist> wordlist_vague = new ArrayList<EmodouWordForWlist>();
	private List<EmodouWordForWlist> wordlist_familiar = new ArrayList<EmodouWordForWlist>();
	private MediaPlayer voicePlayer = new MediaPlayer();
	private WordlistAdapter wordlistAdapter;
	private ProgressDialog dialog; 
	
	
	//定义数据库中的单词包括陌生、模糊和熟悉和未学习中有哪些状态，默认只有未学习状态
	private boolean HAVE_UNFAMILIAR = false;
	private boolean HAVE_VAGUE = false;
	private boolean HAVE_FAMILIAR = false;
	private boolean HAVE_NOTLEARN = false;
	//定义四种学习状态是否是选中状态，默认均为选中状态
	private boolean CHO_UNFAMILIAR = true;
	private boolean CHO_VAGUE = true;
	private boolean CHO_FAMILIAR = true;
	private boolean CHO_NOTLEARN = true;
	//判断有几个状态，若用户点击后没有状态，如本来只有一个状态，用户点击后就没有了，这时提示用户不许更改状态；若只有一个状态，则隐藏熟悉程度
	private int stateNum = 0;
	//判断最初共有几个状态，初始化initPopWindow的时候用
	private int stateTotal = 0;
	//判断当前状态为何种排序,有BOOK，WORD和DEGREE三种状态
	private String orderState = "BOOK";
	
	
	//slidingMenu界面属性,click为true设置点击事件
	private PopupWindow popupWindow;
	private View popupWindow_view;
	private Button btn_notLearnUnclick;
	private Button btn_notLearnClick; 
	private Button btn_unFamiUnclick;
	private Button btn_unFamiClick;
	private Button btn_vagueUnclick; 
	private Button btn_vagueClick;
	private Button btn_famiUnclick;
	private Button btn_famiClick;
	private RadioButton rabtn_bookOrder;
	private RadioButton rabtn_wordOrder; 
	private RadioButton rabtn_degreeOrder;
	private TextView totalWordTv;
	private TextView leftWordTv;
	private RadioGroup radioGroup;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.word_wordlist);
		 
		 setActionbar();
		 
		 userid = getIntent().getStringExtra("userid");
		 classid = getIntent().getStringExtra("classid");
		 
		 bookid = getIntent().getStringExtra("bookid");
		 hint = getIntent().getStringExtra("hint");
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
 		imbdelete = (ImageButton)view.findViewById(R.id.courselist_imbtn_delete);
 		imbdelete.setVisibility(View.GONE);
 		imvFilter = (ImageButton)view.findViewById(R.id.actionbar_courselist_filter);	
 		imvFilter.setOnClickListener(this);
 		imvFilter.setVisibility(View.VISIBLE);
 		
 		rl_color.setBackgroundColor(getResources().getColor(R.color.actionbar_word));
 		titletext.setText("单词列表");
 		
 	}
	
	public void init() {
		
		
//		slidingMenu = new SlidingMenu(this);
//		slidingMenu = (SlidingMenu)getLayoutInflater().inflate(R.layout.word_wordlist_slidingmenu, null);
//		//SlidingMenu界面
//		slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
//		//设置触摸屏幕的模式
//		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
//		//渐入渐出的值
//		slidingMenu.setFadeDegree(0.35f);
//		//SlidingMenu划出时主页面显示的剩余宽度
//		slidingMenu.setBehindOffset(2000);
//		//使SlidingMenu附加在Activity上
//		//slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
//		//为侧滑菜单设置布局
//		//slidingMenu.setMenu(R.layout.word_wordlist_slidingmenu);
//		//slidingMenu.toggle(true);
		
		wordListView = (ListView)findViewById(R.id.word_wordlist_list);				
		
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.setMessage("Loading...");
	    dialog.show();
		
	    //从数据库中查找当前课程的所有单词并且设置该状态的有无，如未学习
		wordList = findAllWords();
		if(wordList!=null ){
			wordlistAdapter = new WordlistAdapter(wordList);
			wordListView.setAdapter(wordlistAdapter);
		}
		
		initPopuptWindow();
		
	}
	
	public void initPopuptWindow() {
		// 获取自定义布局文件activity_popupwindow_left.xml的视图  
        popupWindow_view = getLayoutInflater().inflate(R.layout.word_wordlist_slidingmenu, null,  
                false);  
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度  
        popupWindow = new PopupWindow(popupWindow_view, 500, LayoutParams.MATCH_PARENT, true);  
        // 设置动画效果  
        popupWindow.setAnimationStyle(R.style.AnimationFade); 
        popupWindow.setOutsideTouchable(true);
//        popupWindow.setTouchable(false);
//        popupWindow.setSplitTouchEnabled(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 点击其他地方消失  
//        popupWindow_view.setOnTouchListener(new OnTouchListener() {  
//            @Override  
//            public boolean onTouch(View v, MotionEvent event) {  
//                // TODO Auto-generated method stub  
//                if (popupWindow != null && popupWindow.isShowing()) {  
//                    popupWindow.dismiss();  
//                    popupWindow = null;  
//                }  
//                return false;  
//            }  
//        });  
        
        radioGroup = (RadioGroup)popupWindow_view.findViewById(R.id.word_wordlist_menu_radiogroup);
		radioGroup.setOnCheckedChangeListener(new RadiochangedListener());
		rabtn_bookOrder = (RadioButton)popupWindow_view.findViewById(R.id.word_wordlist_menu_bookorder);
		rabtn_wordOrder = (RadioButton)popupWindow_view.findViewById(R.id.word_wordlist_menu_wordorder);
		rabtn_degreeOrder = (RadioButton)popupWindow_view.findViewById(R.id.word_wordlist_menu_degreeorder);
		
		btn_notLearnUnclick = (Button)popupWindow_view.findViewById(R.id.word_wordlist_menu_notlearn_unclick);
		btn_notLearnUnclick.setOnClickListener(this);
		btn_notLearnClick = (Button)popupWindow_view.findViewById(R.id.word_wordlist_menu_notlearn_click);
		btn_notLearnClick.setOnClickListener(this);
		btn_unFamiUnclick = (Button)popupWindow_view.findViewById(R.id.word_wordlist_menu_unfamiliar_unclick);
		btn_unFamiUnclick.setOnClickListener(this);
		btn_unFamiClick = (Button)popupWindow_view.findViewById(R.id.word_wordlist_menu_unfamiliar_click);
		btn_unFamiClick.setOnClickListener(this);
		btn_vagueUnclick = (Button)popupWindow_view.findViewById(R.id.word_wordlist_menu_vague_unclick);
		btn_vagueUnclick.setOnClickListener(this);
		btn_vagueClick = (Button)popupWindow_view.findViewById(R.id.word_wordlist_menu_vague_click);
		btn_vagueClick.setOnClickListener(this);
		btn_famiUnclick = (Button)popupWindow_view.findViewById(R.id.word_wordlist_menu_familiar_unclick);
		btn_famiUnclick.setOnClickListener(this);
		btn_famiClick = (Button)popupWindow_view.findViewById(R.id.word_wordlist_menu_familiar_click);
		btn_famiClick.setOnClickListener(this);
		
		totalWordTv = (TextView)popupWindow_view.findViewById(R.id.word_wordlist_menu_totalword);
		leftWordTv = (TextView)popupWindow_view.findViewById(R.id.word_wordlist_menu_leftword);
		
		if(HAVE_NOTLEARN == true&& CHO_NOTLEARN == true){
			btn_notLearnClick.setVisibility(View.VISIBLE);
			btn_notLearnUnclick.setVisibility(View.GONE);
		}else if(HAVE_NOTLEARN == true && CHO_NOTLEARN == false){
			btn_notLearnClick.setVisibility(View.GONE);
			btn_notLearnUnclick.setVisibility(View.VISIBLE);
		}else if(HAVE_NOTLEARN == false){
			btn_notLearnClick.setVisibility(View.GONE);
			btn_notLearnUnclick.setVisibility(View.GONE);
			CHO_NOTLEARN = false;
		}
		
		if(HAVE_UNFAMILIAR == true&& CHO_UNFAMILIAR == true){
			btn_unFamiClick.setVisibility(View.VISIBLE);
			btn_unFamiUnclick.setVisibility(View.GONE);
		}else if(HAVE_UNFAMILIAR == true && CHO_UNFAMILIAR == false){
			btn_unFamiClick.setVisibility(View.GONE);
			btn_unFamiUnclick.setVisibility(View.VISIBLE);
		}else if(HAVE_UNFAMILIAR == false){
			btn_unFamiClick.setVisibility(View.GONE);
			btn_unFamiUnclick.setVisibility(View.GONE);
			CHO_UNFAMILIAR = false;
		}
		
		if(HAVE_VAGUE == true && CHO_VAGUE == true){
			btn_vagueClick.setVisibility(View.VISIBLE);
			btn_vagueUnclick.setVisibility(View.GONE);
		}else if(HAVE_VAGUE == true && CHO_VAGUE == false){
			btn_vagueClick.setVisibility(View.GONE);
			btn_vagueUnclick.setVisibility(View.VISIBLE);
		}else if(HAVE_VAGUE == false){
			btn_vagueClick.setVisibility(View.GONE);
			btn_vagueUnclick.setVisibility(View.GONE);
			CHO_VAGUE = false;
		}
		
		if(HAVE_FAMILIAR == true && CHO_FAMILIAR == true){
			btn_famiClick.setVisibility(View.VISIBLE);
			btn_famiUnclick.setVisibility(View.GONE);
		}else if(HAVE_FAMILIAR == true && CHO_FAMILIAR == false){
			btn_famiClick.setVisibility(View.GONE);
			btn_famiUnclick.setVisibility(View.VISIBLE);
		}else if(HAVE_FAMILIAR == false){
			btn_famiClick.setVisibility(View.GONE);
			btn_famiUnclick.setVisibility(View.GONE);
			CHO_FAMILIAR = false;
		}
		
		totalWordTv.setText("全部 : "+ wordList.size() + "词");
		leftWordTv.setText("剩余 : " + wordList.size() + "词");
		
		if(stateNum == 1){
    		rabtn_degreeOrder.setVisibility(View.GONE);
    	}
		
		if(orderState == "BOOK"){
			rabtn_bookOrder.setChecked(true);
		}else if(orderState == "WORD"){
			rabtn_wordOrder.setChecked(true);
		}else if(orderState == "DEGREE"){
			rabtn_degreeOrder.setChecked(true);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (popupWindow != null && popupWindow.isShowing()) {  
		            popupWindow.dismiss();  
		            popupWindow = null;  
		        }  
				
				break;
	
			default:
				break;
		}
		return super.onTouchEvent(event);
	}
	
	/*** 
     * 获取PopupWindow实例 
     */  
    private void getPopupWindow() {  
        if (null != popupWindow) {  
            popupWindow.dismiss();  
            return;  
        } else {  
            initPopuptWindow();  
        }  
    }  
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
			switch (v.getId()) {	
				case R.id.courselist_imbtn_return:
					if(hint != "" && hint.equals("fromWordlistActivity")){
						onBackPressed();
					}else{
						Intent intent = new Intent(WordwlistActivity.this, WordlistActivity.class);
						intent.putExtra("bookid", bookid);
						startActivity(intent);
					}
					break;
					
				case R.id.actionbar_courselist_filter:
						getPopupWindow();
						popupWindow.showAtLocation(v, Gravity.RIGHT, 0, 0);
					break;
				
				case R.id.word_wordlist_menu_notlearn_click:
					//用户不想要未学习状态
					if(stateNum == 1){
						//用户只剩下一种状态，不能不想要，这种情况不做任何操作
						Toast.makeText(this, R.string.word_wordlist_noNotLearnWord, 1).show();
					}else{
						stateNum -- ;
						CHO_NOTLEARN = false;
						
						addAndSort();
						
						btn_notLearnClick.setVisibility(View.GONE);
						btn_notLearnUnclick.setVisibility(View.VISIBLE);
					}
					break;
					
				case R.id.word_wordlist_menu_notlearn_unclick:
					//用户想要选择未学习状态
					stateNum++;
					CHO_NOTLEARN = true;
					
					addAndSort();
					
					btn_notLearnClick.setVisibility(View.VISIBLE);
					btn_notLearnUnclick.setVisibility(View.GONE);
					
					break;
					
				case R.id.word_wordlist_menu_unfamiliar_click:
					//用户不想要陌生状态
					if(stateNum == 1){
						//用户只剩下一种状态，不能不想要，这种情况不做任何操作
						Toast.makeText(this, R.string.word_wordlist_noUnfamiWord, 1).show();
					}else{
						stateNum -- ;
						CHO_UNFAMILIAR = false;
						
						addAndSort();
						
						btn_unFamiClick.setVisibility(View.GONE);
						btn_unFamiUnclick.setVisibility(View.VISIBLE);
					}
					break;
				
				case R.id.word_wordlist_menu_unfamiliar_unclick:
					//用户想要选择陌生状态
					stateNum++;
					CHO_UNFAMILIAR = true;
					
					addAndSort();
					
					btn_unFamiClick.setVisibility(View.VISIBLE);
					btn_unFamiUnclick.setVisibility(View.GONE);
					
					break;
					
				case R.id.word_wordlist_menu_vague_click:
					//用户不想要模糊状态
					if(stateNum == 1){
						//用户只剩下一种状态，不能不想要，这种情况不做任何操作
						Toast.makeText(this, R.string.word_wordlist_noVagueWord, 1).show();
					}else{
						stateNum -- ;
						CHO_VAGUE = false;
						
						addAndSort();
						
						btn_vagueClick.setVisibility(View.GONE);
						btn_vagueUnclick.setVisibility(View.VISIBLE);
					}
					break;
				
				case R.id.word_wordlist_menu_vague_unclick:
					//用户想要选择模糊状态
					stateNum++;
					CHO_VAGUE = true;
					
					addAndSort();
					
					btn_vagueClick.setVisibility(View.VISIBLE);
					btn_vagueUnclick.setVisibility(View.GONE);
					break;
					
				case R.id.word_wordlist_menu_familiar_click:
					//用户不想要熟悉状态
					if(stateNum == 1){
						//用户只剩下一种状态，不能不想要，这种情况不做任何操作
						Toast.makeText(this, R.string.word_wordlist_noFamiWord, 1).show();
					}else{
						stateNum -- ;
						CHO_FAMILIAR = false;
						
						addAndSort();
						
						btn_famiClick.setVisibility(View.GONE);
						btn_famiUnclick.setVisibility(View.VISIBLE);
					}
					break;
				
				case R.id.word_wordlist_menu_familiar_unclick:
					//用户想要选择模糊状态
					stateNum++;
					CHO_FAMILIAR = true;
					
					addAndSort();
					
					btn_famiClick.setVisibility(View.VISIBLE);
					btn_famiUnclick.setVisibility(View.GONE);
					
					break;
					
				default:
					break;
		}
	}
	
	
	
	public class ViewHolder {
		private TextView wordname;
		private View hint_unFamiliar, hint_vague, hint_familiar;
		private TextView wordmean;
		private ImageView checkImv;
		private FlipLayout flipItem;
	}
	
	public class WordlistAdapter extends BaseAdapter {
		
		private List<EmodouWordForWlist> wordList;
		private int[] check;
		private int playmusic = -1;
		private int[] flip;//防止刷新时position与holder的位置不同
		
		public WordlistAdapter(List<EmodouWordForWlist> wordList){
			super();
			this.wordList = wordList;
			flip = new int[wordList.size()];
			for (int i = 0; i < wordList.size(); i++) {
				flip[i] = -1;//初始状态为-1即不需要翻转
			}
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return wordList.size();
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return wordList.get(position);
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
			final ViewHolder holder;
			if(convertView == null){
				view = getLayoutInflater().inflate(R.layout.word_wordlist_worditem, parent, false);
				holder = new ViewHolder();
				holder.wordname = (TextView)view.findViewById(R.id.word_wordlist_name);
				holder.hint_unFamiliar = (View)view.findViewById(R.id.word_wordlist_unfamiliarhint);
				holder.hint_vague = (View)view.findViewById(R.id.word_wordlist_vaguehint);
				holder.hint_familiar = (View)view.findViewById(R.id.word_wordlist_familiarhint);
				holder.wordmean = (TextView)view.findViewById(R.id.word_wordlist_mean);
				holder.checkImv = (ImageView)view.findViewById(R.id.word_wordlist_check);
				holder.flipItem = (FlipLayout)view.findViewById(R.id.word_wordlist_fliplayout);
				view.setTag(holder);
			}else{
				holder = (ViewHolder)view.getTag();
			}
			
			holder.wordname.setText(wordList.get(position).getWordname());
			holder.wordmean.setText(wordList.get(position).getWordmean().replaceAll("#", " "));
			
			if(flip[position] == 1){
				holder.flipItem.flip();
				flip[position] = -1;
			}
				
			
			//设置提示颜色
			if(wordList.get(position).getLearnState() == Constants.WORD_STATE_NOT_LEARN){
				holder.hint_familiar.setVisibility(View.GONE);
				holder.hint_unFamiliar.setVisibility(View.GONE);
				holder.hint_vague.setVisibility(View.GONE);
			}else if(wordList.get(position).getLearnState() == Constants.WORD_STATE_UNFAMILIAR){
				holder.hint_unFamiliar.setVisibility(View.VISIBLE);
				holder.hint_vague.setVisibility(View.GONE);
				holder.hint_familiar.setVisibility(View.GONE);
			}else if(wordList.get(position).getLearnState() == Constants.WORD_STATE_VAGUE){
				holder.hint_vague.setVisibility(View.VISIBLE);
				holder.hint_familiar.setVisibility(View.GONE);
				holder.hint_unFamiliar.setVisibility(View.GONE);
			}else if(wordList.get(position).getLearnState() == Constants.WORD_STATE_FAMILIAR){
				holder.hint_familiar.setVisibility(View.VISIBLE);
				holder.hint_unFamiliar.setVisibility(View.GONE);
				holder.hint_vague.setVisibility(View.GONE);
			}
			
			//设置翻转音频
			final String mp3Iciba = wordList.get(position).getVoice();
			final String mp3Local = wordList.get(position).getLocal_voice();
			final String wordnameFinal = wordList.get(position).getWordname();	
			
			//正面在上为true，反面在上为false
			if(wordList.get(position).getFlipState() == false){
				holder.flipItem.getFrontView().setVisibility(View.GONE);
				holder.flipItem.getBackView().setVisibility(View.VISIBLE);
				
			}else if(wordList.get(position).getFlipState() == true){
				holder.flipItem.getFrontView().setVisibility(View.VISIBLE);
				holder.flipItem.getBackView().setVisibility(View.GONE);
			}
			holder.flipItem.setOnFlipListener(new OnFlipListener() {		
				
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
							if(wordList.get(position).getFlipState() == false){
								wordList.get(position).setFlipState(true);
								flip[position] = 1;
								wordlistAdapter.notifyDataSetChanged();
							}
						}
					}, 3000);
					
					
				}
				
				@Override
				public void onClick(FlipLayout view) {
					// TODO Auto-generated method stub
					if(mp3Iciba == null && mp3Local == null){
						Toast.makeText(WordwlistActivity.this, R.string.word_wordlist_novoice, 1).show();
					}else{
						if(voicePlayer.isPlaying()){
							voicePlayer.stop();
						}else{
							if(mp3Local == null || mp3Local.equals("")){
								if(mp3Iciba == null){
									Toast.makeText(WordwlistActivity.this, R.string.word_wordlist_novoice, 0).show();
								}else{
									//播放网络音频
									ValidateUtils.playUrl(mp3Iciba, wordnameFinal, WordwlistActivity.this, userid);
								}
							}else{
								//播放本地音频
								ValidateUtils.playUrl(mp3Local, wordnameFinal, WordwlistActivity.this, userid);
							}
						}
					}
					//正面在上为true，反面在上为false
					if(wordList.get(position).getFlipState() == true){
						wordList.get(position).setFlipState(false);
						holder.flipItem.reset();
						wordlistAdapter.notifyDataSetChanged();
					}					
					else if(wordList.get(position).getFlipState() == false){
						wordList.get(position).setFlipState(true);
						holder.flipItem.flip();
						wordlistAdapter.notifyDataSetChanged();
					}
				}
			});
			
			holder.checkImv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					ArrayList<String> wordNameList = new ArrayList<String>();
					for(EmodouWordForWlist wordForWlist : wordList){
						wordNameList.add(wordForWlist.getWordname());
					}
					Intent intent = new Intent(WordwlistActivity.this, WordwDetailActivity.class);
					intent.putStringArrayListExtra("wordNameList", wordNameList);
					intent.putExtra("classid", classid);
					intent.putExtra("index", position);
					intent.putExtra("bookid", bookid);
					intent.putExtra("hint", "fromWordwlistActivity");
					startActivity(intent);
				}
			});
			
			return view;
		}
	
	}
	
	public class RadiochangedListener implements android.widget.RadioGroup.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if(checkedId == rabtn_bookOrder.getId()){
				
				Toast.makeText(WordwlistActivity.this, R.string.word_wordlist_bookOrdered, 0).show();
				wordList = CompareId(wordList);
				wordlistAdapter = new WordlistAdapter(wordList);
				wordListView.setAdapter(wordlistAdapter);
				orderState = "BOOK";
				
			}else if(checkedId == rabtn_wordOrder.getId()){
				
				Toast.makeText(WordwlistActivity.this, R.string.word_wordlist_wordOrdered, 0).show();
				wordList = CompareWord(wordList);
				wordlistAdapter = new WordlistAdapter(wordList);
				wordListView.setAdapter(wordlistAdapter);
				orderState = "WORD";
				
			}else if(checkedId == rabtn_degreeOrder.getId()){
				
				Toast.makeText(WordwlistActivity.this, R.string.word_wordlist_degreeOrdered, 0).show();
				wordList = CompareDegree(wordList);
				wordlistAdapter = new WordlistAdapter(wordList);
				wordListView.setAdapter(wordlistAdapter);
				orderState = "DEGREE";
			}
		}
	}
	
	public class classItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public List<EmodouWordForWlist> findAllWords(){
		wordList.clear();
		DbUtils dbUtils = DbUtils.create(WordwlistActivity.this);
		
		//先将四个状态的单词都选出来,按照字母顺序排好
		String sql_NotLearn = "SELECT * FROM com_emodou_domain_EmodouWordManager as m " +
							  "where m.userid = " + userid +
							  " and m.classid = " + classid +
							  " and m.learnState = "+Constants.WORD_STATE_NOT_LEARN;
		wordlist_notlearn.addAll(ValidateUtils.getWordManagerListBySql(this, dbUtils, sql_NotLearn));
		if(wordlist_notlearn != null && wordlist_notlearn.size() != 0){
			HAVE_NOTLEARN = true;
			stateNum++;
			stateTotal++;
			wordList.addAll(wordlist_notlearn);
			CHO_NOTLEARN = true;
		}else{
			HAVE_NOTLEARN = false;
		}
		
		String sql_UnFami = "SELECT * FROM com_emodou_domain_EmodouWordManager as m " +
						    "where m.userid = " + userid +
						    " and m.classid = " + classid +
						    " and m.learnState = "+Constants.WORD_STATE_UNFAMILIAR ;
		wordlist_unfami.addAll(ValidateUtils.getWordManagerListBySql(this, dbUtils, sql_UnFami));
		if(wordlist_unfami != null && wordlist_unfami.size() != 0){
			HAVE_UNFAMILIAR = true;
			wordList.addAll(wordlist_unfami);
			stateNum++;
			stateTotal++;
			CHO_UNFAMILIAR = true;
		}else{
			HAVE_UNFAMILIAR = false;
		}
		
		String sql_Vague = "SELECT * FROM com_emodou_domain_EmodouWordManager as m " +
			    		   "where m.userid = " + userid +
			    		   " and m.classid = " + classid +
			    		   " and m.learnState = "+Constants.WORD_STATE_VAGUE ;
		wordlist_vague.addAll(ValidateUtils.getWordManagerListBySql(this, dbUtils, sql_Vague));
		if(wordlist_vague != null && wordlist_vague.size() != 0){
			HAVE_VAGUE = true;
			wordList.addAll(wordlist_vague);
			stateNum++;
			stateTotal++;
			CHO_VAGUE = true;
		}else{
			HAVE_VAGUE = false;
		}
		
		String sql_Familiar = "SELECT * FROM com_emodou_domain_EmodouWordManager as m " +
				    		  "where m.userid = " + userid +
				    		  " and m.classid = " + classid +
				    		  " and m.learnState = "+Constants.WORD_STATE_FAMILIAR ;
		wordlist_familiar.addAll(ValidateUtils.getWordManagerListBySql(this, dbUtils, sql_Familiar));
		if(wordlist_familiar != null && wordlist_familiar.size() != 0){
			HAVE_FAMILIAR = true;
			wordList.addAll(wordlist_familiar);
			stateNum++;
			stateTotal++;
			CHO_FAMILIAR = true;
		}else{
			HAVE_FAMILIAR = false;
		}
		
		//上面已经把所有满足的课程都添加进来了，然后默认是按照课本排序，就是按照Id排序
		
		wordList = CompareId(wordList);
		
		dialog.dismiss();
		
		return wordList;
	}

    
    //先将name取出来进行排序，然后再根据排好的name排序wordManagerList
    public List<EmodouWordForWlist> CompareWord(List<EmodouWordForWlist> wordManagerList){
    	
    	List<String> wordNameList = new ArrayList<String>();
    	List<EmodouWordForWlist> wordManaNewList = new ArrayList<EmodouWordForWlist>();
    	for (int i = 0; i < wordManagerList.size(); i++) {
			wordNameList.add(wordManagerList.get(i).getWordname());
		}
    	if(wordNameList != null && wordNameList.size() != 0){
    		Collections.sort(wordNameList);
    	}
    	for(int i=0; i<wordNameList.size(); i++){
    		for(int j = 0; j < wordManagerList.size(); j++){
    			if(wordNameList.get(i).equals(wordManagerList.get(j).getWordname()))
    				wordManaNewList.add(wordManagerList.get(j));
    		}
    	}
    	
    	return wordManaNewList;
    }
    
    public List<EmodouWordForWlist> CompareId(List<EmodouWordForWlist> wordManagers){
    	List<Integer> idList = new ArrayList<Integer>();
    	List<EmodouWordForWlist> wordManaNewList = new ArrayList<EmodouWordForWlist>();
    	for(int i = 0; i<wordManagers.size(); i++){
    		idList.add(new Integer(wordManagers.get(i).getId()));
    	}
    	if(idList != null && idList.size() !=0){
    		Collections.sort(idList);
    	}
    	for(int i=0; i<idList.size(); i++){
    		for(int j = 0; j < wordManagers.size(); j++){
    			if(idList.get(i) == wordManagers.get(j).getId()){
    				wordManaNewList.add(wordManagers.get(j));
    			}	
    		}
    	}
    	
    	return wordManaNewList;
    	
    }
    
    //将learnState从小到大排序，陌生、未认识、模糊、熟悉
    public List<EmodouWordForWlist> CompareDegree(List<EmodouWordForWlist> wordManagers) {
//		List<Integer> degreeList = new ArrayList<Integer>();
//		List<EmodouWordForWlist> wordManaNewList = new ArrayList<EmodouWordForWlist>();
//		for(int i = 0; i<wordManagers.size();i++){
//			degreeList.add(new Integer(wordManagers.get(i).getLearnState()));
//		}
//		if(degreeList != null && degreeList.size() !=0){
//			Collections.sort(degreeList);
//		}
//		for(int i = 0; i<degreeList.size();i++){
//			for(int j = 0; j < wordManagers.size();j++){
//				if(degreeList.get(i).equals(wordManagers.get(j).getLearnState())){
//					wordManaNewList.add(wordManagers.get(j));
//				    wordManagers.remove(j);
//				}		
//			}
//		}
//		return wordManaNewList;
    	
    	//根据其重载实现
    	Collections.sort(wordManagers, new Comparator<EmodouWordForWlist>() {
    		public int compare(EmodouWordForWlist word1, EmodouWordForWlist word2) {
				return String.valueOf(word1.getLearnState()).compareTo(String.valueOf(word2.getLearnState()));
			}
		});
    	return wordManagers;
			
	}
    //根据学习状态和排序方式的不同，更新wordList
    public void addAndSort() {
    	
    	//如果只有一个学习状态
    	if(stateNum == 1){
    		rabtn_degreeOrder.setVisibility(View.GONE);
    	}else if(stateNum > 1){
    		rabtn_degreeOrder.setVisibility(View.VISIBLE);
    	}
    	
    	wordList.clear();
    	//似乎Have状态没有用，但为了保险，还是加上
		if(CHO_NOTLEARN == true && HAVE_NOTLEARN == true){
			wordList.addAll(wordlist_notlearn);
		}	
		if(CHO_UNFAMILIAR == true && HAVE_UNFAMILIAR == true){
			wordList.addAll(wordlist_unfami);
		}
		if(CHO_VAGUE == true && HAVE_VAGUE == true){
			wordList.addAll(wordlist_vague);
		}
		if(CHO_FAMILIAR == true && HAVE_FAMILIAR == true){
			wordList.addAll(wordlist_familiar);
		}
		
		if(orderState.equals("BOOK")){
			wordList = CompareId(wordList);
		}else if(orderState.equals("WORD")){
			wordList = CompareWord(wordList);
		}else if(orderState.equals("DEGREE")){
			wordList = CompareDegree(wordList);
		}
		
		wordlistAdapter = new WordlistAdapter(wordList);
		wordListView.setAdapter(wordlistAdapter);
		leftWordTv.setText("剩余 : " + wordList.size() + "词");
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		if(hint != "" && hint.equals("fromWordlistActivity")){
//			onBackPressed();
//		}else{
//			Intent intent = new Intent(WordwlistActivity.this, WordlistActivity.class);
//			intent.putExtra("bookid", bookid);
//			startActivity(intent);
//		}]
		Intent intent = new Intent(WordwlistActivity.this, WordlistActivity.class);
		intent.putExtra("bookid", bookid);
		startActivity(intent);
		super.onBackPressed();
	}

	@Override
	public void onClick(FlipLayout view) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFlipStart(FlipLayout view) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFlipEnd(FlipLayout view) {
		// TODO Auto-generated method stub
		
	}

	

}
