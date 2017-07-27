package com.emodou.home;

import java.util.ArrayList;
import java.util.List;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.emodou.domain.EmodouWordForWlist;
import com.emodou.extend.FlipLayout;
import com.emodou.extend.FlipLayout.OnFlipListener;
import com.emodou.util.Constants;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WordNewordlistActivity extends Activity implements OnClickListener{
	//actionbar及其相关
	private RelativeLayout actionbarRl;
	private ActionBar actionbar;
	private TextView titletext;
	private ImageButton imbReturn, imbDelete;
		
	private SwipeMenuListView swipeMenuListView;
	private listAdapter listadapter;
	
	private String userid, bookid;
	private MediaPlayer voicePlayer = new MediaPlayer();
	
	private List<EmodouWordForWlist> wordForWlists = new ArrayList<EmodouWordForWlist>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.word_newordlist);
	    
	    userid = getIntent().getStringExtra("userid");
	    bookid = getIntent().getStringExtra("bookid");
	    
	    setActionbar();
//	    initWordList();
//	    InitSwipeListView();
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
 		
 		
 		titletext.setText(getResources().getString(R.string.word_neword_title));			
 		
 	}
	
	public void initWordList() {
		wordForWlists.clear();
		DbUtils dbUtils = DbUtils.create(this);
		
		String sqlNeword = "SELECT * FROM com_emodou_domain_EmodouWordManager as m " +
						  "where m.userid = " + userid +
				          " and m.isAddToNewWordsBood = "+Constants.WORD_ISADDTONEW_YES;
		
		wordForWlists = ValidateUtils.getWordManagerListBySql(this, dbUtils, sqlNeword);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//这个为了防止在详情页面，用户从生词本中删除某些词，onbackpressed返回时，不会刷新列表
		super.onResume();
		initWordList();
		InitSwipeListView();
	}
	
	private void InitSwipeListView() {// 初始化
		swipeMenuListView = (SwipeMenuListView)findViewById(R.id.listView);
        // ///////////////////////////////////////////////////////////////////
        // 这个是创建了一个滑动菜单的的listview
        SwipeMenuCreator creator = new SwipeMenuCreator() {
 
            @Override
            public void create(SwipeMenu menu) {
                ListViewMenuCreate(menu);
            }
        };
        // set creator
        listadapter = new listAdapter(wordForWlists, this);
        swipeMenuListView.setAdapter(listadapter);
        swipeMenuListView.setMenuCreator(creator);// listview要添加menu
        
       
        
        swipeMenuListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				// TODO Auto-generated method stub
				String value = menu.getMenuItem(index).getTitle().toString();
                if (value.equals("删除")) {
                	ValidateUtils.deleteFromNewWord(WordNewordlistActivity.this, wordForWlists.get(position).getWordname(), 
                			                                             wordForWlists.get(position).getClassid());
                    Toast.makeText(WordNewordlistActivity.this, 
                    		getResources().getString(R.string.word_neword_delete)+ wordForWlists.get(position).getWordname(), 0).show();
                    wordForWlists.remove(position);
                    listadapter.notifyDataSetChanged();
                }
				return false;
			}
		});
	}
        
     // 值得注意的是 每一个listview的item创建的时候 SwipeMenu就创建了一次
        private void ListViewMenuCreate(SwipeMenu menu) {
             
            SwipeMenuItem kankanItem = new SwipeMenuItem(this
                    .getApplicationContext());
            // set item background
            kankanItem.setBackground(getResources().getDrawable(R.color.actionbar_word));// 设置背景颜色
            //kankanItem.setIcon(getResources().getDrawable(R.drawable.word_wordlist_mean_click));
            // set item width
            // kankanItem.setWidth(dp2px(60));// 设置宽度
            kankanItem.setWidth(250);
            kankanItem.setHeight(80);
            // set item title
            kankanItem.setTitle("删除");// 设置第一个标题
            // set item title fontsize
            kankanItem.setTitleSize(30);// 设置标题文字的大小
            // set item title font color
            kankanItem.setTitleColor(Color.WHITE);// 设置标题颜色
            // add to menu
            menu.addMenuItem(kankanItem);// 添加标题到menu类中          
        }
        
        private static class classViewHolder{
    		TextView wordnameTv, wordmeanTv;
    		ImageView findlabelImv, checkImv;
    		FlipLayout flipItem;
    		
    	}
        
        private class listAdapter extends BaseAdapter{
        	private LayoutInflater inflater;
        	private List<EmodouWordForWlist> list = new ArrayList<EmodouWordForWlist>();
        	private int[] flip;//防止刷新时position与holder的位置不同
        	
        	public listAdapter(List<EmodouWordForWlist> list, Context context) {
				// TODO Auto-generated constructor stub
        		super();
        		this.list = list;
        		inflater = LayoutInflater.from(context);
        		flip = new int[list.size()];
    			for (int i = 0; i < list.size(); i++) {
    				flip[i] = -1;//初始状态为-1即不需要翻转
    			}
			}
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return list.size();
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return list.get(position);
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
				final classViewHolder classholder;
				if(convertView == null){
					view = inflater.inflate(R.layout.word_newordlist_item, parent, false);
					classholder = new classViewHolder();
					
					classholder.wordnameTv = (TextView)view.findViewById(R.id.word_newordlist_item_wordname);
					classholder.findlabelImv = (ImageView)view.findViewById(R.id.word_newordlist_item_findlabel);
					classholder.flipItem = (FlipLayout)view.findViewById(R.id.word_newordlist_item_fliplayout);
					classholder.checkImv = (ImageView)view.findViewById(R.id.word_newordlist_check);
					classholder.wordmeanTv = (TextView)view.findViewById(R.id.word_newordlist_mean);
					
					view.setTag(classholder);
				}else{
					classholder = (classViewHolder)view.getTag();
				}
				
				classholder.wordnameTv.setText(list.get(position).getWordname());
				classholder.wordmeanTv.setText(list.get(position).getWordmean().replaceAll("#", " "));
				
				if(list.get(position).getClassid() == null && list.get(position).getClassid().equals(""))
					classholder.findlabelImv.setVisibility(View.VISIBLE);
				else
					classholder.findlabelImv.setVisibility(View.GONE);
				
				if(flip[position] == 1){
					classholder.flipItem.flip();
					flip[position] = -1;
				}
				
				//设置翻转音频
				final String mp3Iciba = list.get(position).getVoice();
				final String mp3Local = list.get(position).getLocal_voice();
				final String wordnameFinal = list.get(position).getWordname();	
				
				//正面在上为true，反面在上为false
				if(list.get(position).getFlipState() == false){
					classholder.flipItem.getFrontView().setVisibility(View.GONE);
					classholder.flipItem.getBackView().setVisibility(View.VISIBLE);
					
				}else if(list.get(position).getFlipState() == true){
					classholder.flipItem.getFrontView().setVisibility(View.VISIBLE);
					classholder.flipItem.getBackView().setVisibility(View.GONE);
				}
				classholder.flipItem.setOnFlipListener(new OnFlipListener() {		
					
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
								if(list.get(position).getFlipState() == false){
									list.get(position).setFlipState(true);
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
							Toast.makeText(WordNewordlistActivity.this, R.string.word_wordlist_novoice, 1).show();
						}else{
							if(voicePlayer.isPlaying()){
								voicePlayer.stop();
							}else{
								if(mp3Local == null || mp3Local.equals("")){
									if(mp3Iciba == null){
										Toast.makeText(WordNewordlistActivity.this, R.string.word_wordlist_novoice, 0).show();
									}else{
										//播放网络音频
										ValidateUtils.playUrl(mp3Iciba, wordnameFinal, WordNewordlistActivity.this, userid);
									}
								}else{
									//播放本地音频
									ValidateUtils.playUrl(mp3Local, wordnameFinal, WordNewordlistActivity.this, userid);
								}
							}
						}
						//正面在上为true，反面在上为false
						if(list.get(position).getFlipState() == true){
							list.get(position).setFlipState(false);
							classholder.flipItem.reset();
							listadapter.notifyDataSetChanged();
						}					
						else if(list.get(position).getFlipState() == false){
							list.get(position).setFlipState(true);
							classholder.flipItem.flip();
							listadapter.notifyDataSetChanged();
						}
					}
				});
				
				classholder.checkImv.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						
						ArrayList<String> wordNameList = new ArrayList<String>();
						for(EmodouWordForWlist wordForWlist : list){
							wordNameList.add(wordForWlist.getWordname());
						}
						Intent intent = new Intent(WordNewordlistActivity.this, WordwDetailActivity.class);
						intent.putStringArrayListExtra("wordNameList", wordNameList);
						intent.putExtra("classid", list.get(position).getClassid());
						intent.putExtra("index", position);
						intent.putExtra("bookid", bookid);
						intent.putExtra("hint", "fromWordneWordlistActivity");
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
					onBackPressed();
					break;
	
				default:
					break;
			}
		}

}
