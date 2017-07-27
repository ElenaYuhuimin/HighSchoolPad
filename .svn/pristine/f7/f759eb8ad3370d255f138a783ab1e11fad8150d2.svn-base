package com.emodou.home;

import java.util.ArrayList;

import com.emodou.domain.EmodouUserInfo;
import com.emodou.domain.EmodouWordInfo;
import com.emodou.login.EmodouLoginAcvitity;
import com.example.emodou.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WordwDetailActivity extends Activity implements OnClickListener{
	
	//actionbar及其相关
	private RelativeLayout actionbarRl;
	private ActionBar actionbar;
	private TextView titletext;
	private ImageButton imbReturn, imbDelete;
	
	//界面属性
	private String classid, userid;
	private String bookid, hint;
	private int index;
	private ArrayList<String> wordNameList = new ArrayList<String>();
	private RelativeLayout FrontRl, NextRl;
	private WordwDetailFragment detailFragment;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.word_wordlist_detail_activity);
		 
		 setActionbar();
		 
		 classid = getIntent().getStringExtra("classid");
		 index = getIntent().getExtras().getInt("index");
		 wordNameList = getIntent().getStringArrayListExtra("wordNameList");
		 hint = getIntent().getStringExtra("hint");
		 
		 bookid = getIntent().getStringExtra("bookid");
		 init();
		 
		 detailFragment = (WordwDetailFragment)getFragmentManager().findFragmentById(R.id.word_detail_content_fragment);
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
 		
 		
 		titletext.setText(getResources().getString(R.string.word_detail_title));			
 		
 	}
	
	public void init() {
		FrontRl = (RelativeLayout)findViewById(R.id.word_detial_front);
		FrontRl.setOnClickListener(this);
		NextRl = (RelativeLayout)findViewById(R.id.word_detail_next);
		NextRl.setOnClickListener(this);
		
		if(hint.equals("fromWordSearchActivity")){
			//如果从查词界面过来，则只有一个单词，没有前后箭头
			FrontRl.setVisibility(View.GONE);
			NextRl.setVisibility(View.GONE);
		}else{
			if(index == 0){
				FrontRl.setVisibility(View.INVISIBLE);
				Toast.makeText(this, R.string.word_detail_first, 0).show();
			}else if(index == wordNameList.size()){
				NextRl.setVisibility(View.INVISIBLE);
				Toast.makeText(this, R.string.word_detail_last, 0).show();
			}
		}
		
		
		DbUtils dbUtils = DbUtils.create(this);
		try{
			EmodouUserInfo userInfo = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
			userid = userInfo.getUserid();
		}catch(DbException e){
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 
		switch (v.getId()){
			case R.id.courselist_imbtn_return:
//				String hint="fromWordwDetailActivity";
//		    	Intent intent = new Intent(WordwDetailActivity.this, WordwlistActivity.class);
//		    	intent.putExtra("classid", classid);
//		    	intent.putExtra("userid", userid);
//		    	intent.putExtra("bookid", bookid);
//		    	intent.putExtra("packageid", packageid);
//		    	intent.putExtra("hint", hint);
//		    	startActivity(intent);
				onBackPressed();
		    	break;
		    
			case R.id.word_detial_front:
				index--;
				if(index == 0){
					FrontRl.setVisibility(View.INVISIBLE);
					Toast.makeText(this, R.string.word_detail_first, 0).show();
				}else{
					FrontRl.setVisibility(View.VISIBLE);
					NextRl.setVisibility(View.VISIBLE);
				}
				detailFragment.refresh(index);
				break;
			
			case R.id.word_detail_next:
				index++;
				if(index == wordNameList.size()){
					NextRl.setVisibility(View.INVISIBLE);
					if(hint.equals("fromWordwlistActivity")){
						AlertDialog.Builder builder = new AlertDialog.Builder(WordwDetailActivity.this)
						.setTitle(R.string.prompt)
						.setMessage(R.string.word_detail_last)
						.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								index--;
							}
						})
						.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
					           public void onClick(DialogInterface dialog, int whichButton) {
//					        	    Intent intent2 = new Intent(WordwDetailActivity.this, WordlistActivity.class);
//							    	intent2.putExtra("bookid", bookid);
//							    	intent2.putExtra("packageid", packageid);
//							    	startActivity(intent2);
					        	    onBackPressed();
					           }					            
					       }); 
					
						builder.create().show();
					}else if(hint.equals("fromWordneWordlistActivity")){
						AlertDialog.Builder builder = new AlertDialog.Builder(WordwDetailActivity.this)
						.setTitle(R.string.prompt)
						.setMessage(R.string.word_neword_last)
						.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								index--;
							}
						})
						.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
					           public void onClick(DialogInterface dialog, int whichButton) {
//					        	    Intent intent2 = new Intent(WordwDetailActivity.this, WordlistActivity.class);
//							    	intent2.putExtra("bookid", bookid);
//							    	intent2.putExtra("packageid", packageid);
//							    	startActivity(intent2);
					        	    onBackPressed();
					           }					            
					       }); 
						builder.create().show();
					}
							
				}else{
					FrontRl.setVisibility(View.VISIBLE);
					NextRl.setVisibility(View.VISIBLE);
					detailFragment.refresh(index);
				}			
				break;
		}
		
	}
	
	
}
