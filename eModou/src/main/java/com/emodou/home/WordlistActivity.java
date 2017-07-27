package com.emodou.home;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import com.emodou.domain.EmodouBook;
import com.emodou.domain.EmodouClass;
import com.emodou.domain.EmodouClassManager;
import com.emodou.domain.EmodouUnit;
import com.emodou.domain.EmodouUserInfo;
import com.emodou.domain.EmodouWordClass;
import com.emodou.domain.EmodouWordManager;
import com.emodou.domain.EmodouWordUnit;
import com.emodou.util.Constants;
import com.example.emodou.R;
import com.emodou.home.WordwlistActivity;
import com.iflytek.cloud.InitListener;
import com.iflytek.ise.result.FinalResult;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
/*
 * 单词的课程列表界面
 */
public class WordlistActivity extends Activity implements OnClickListener{
	
	//actionbar及其相关
	private ActionBar actionbar;
	private TextView titletext;
	private ImageButton imbReturn, imbdelete;
	private RelativeLayout rl_color;
	private ImageView searchImv, newwordImv;
	
	//界面及其相关属性
	//private Button courseStart;//被学习按钮取代
	private ListView unitListView, classListView;
	private List<EmodouWordUnit> unitlist = new ArrayList<EmodouWordUnit>();
	private List<List<EmodouWordClass>> classlist = new ArrayList<List<EmodouWordClass>>();
	private classListAdapter classAdapter;
	private unitListAdapter unitAdapter;
	private String bookid, userid;
	private LinearLayout learnLl, testLl;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.word_courselist);
		 
		 //获得WordmainActivity和WordwlistActivity界面传过来的参数
		 bookid = getIntent().getExtras().getString("bookid");
		//得到用户信息和书本信息
		DbUtils dbUtils = DbUtils.create(this);
		try{
			EmodouUserInfo userInfo = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
			if(userInfo!=null){
				userid = userInfo.getUserid();
			}		
		}catch(DbException e){
			e.printStackTrace();
		}
		
		 
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
 		imbdelete = (ImageButton)view.findViewById(R.id.courselist_imbtn_delete);
 		imbdelete.setVisibility(View.GONE);
 		searchImv = (ImageView)view.findViewById(R.id.actionbar_courselist_searchimv);
 		searchImv.setOnClickListener(this);
 		searchImv.setVisibility(View.VISIBLE);
 		newwordImv = (ImageView)view.findViewById(R.id.actionbar_courselist_newwordimv);
 		newwordImv.setVisibility(View.VISIBLE);
 		newwordImv.setOnClickListener(this);
 		
 		//感觉这两个图标好怪异。。。隐藏
 		searchImv.setVisibility(View.GONE);
 		newwordImv.setVisibility(View.GONE);
 		
 		rl_color.setBackgroundColor(getResources().getColor(R.color.actionbar_word));
 		titletext.setText("单元单词列表");
	
 	}
	
	public void loadList(){
		
		DbUtils dbUtils2 = DbUtils.create(WordlistActivity.this);
		try{
			List<EmodouWordUnit>  wordUnits = dbUtils2.findAll(Selector.from(EmodouWordUnit.class)
															  .where("bookid","=",bookid));
			if(wordUnits!=null){
				unitlist = wordUnits;
				
				//根据单元名取出相应的列表添加到相应的list中
				for(int i = 0; i < unitlist.size(); i++){
					String unitIdStr = unitlist.get(i).getUnitid();
					List<EmodouWordClass> wordClasses = new ArrayList<EmodouWordClass>();
					wordClasses = dbUtils2.findAll(Selector.from(EmodouWordClass.class)
												  .where("unitid","=",unitIdStr)
												  .and("bookid","=",bookid)
												  .and("userid","=",userid));
					classlist.add(wordClasses);
				}
			}
			
		}catch(DbException e){
			e.printStackTrace();
		}
	}
	
	public void init() {
//		courseStart = (Button)findViewById(R.id.word_courselist_startlearn);
//		courseStart.setOnClickListener(this);
		
		unitListView = (ListView)findViewById(R.id.word_courselist_listview_unit);
		classListView = (ListView)findViewById(R.id.word_courselist_listview_class);
		
		learnLl = (LinearLayout)findViewById(R.id.word_courselist_learn_lil);
		learnLl.setOnClickListener(this);
		testLl = (LinearLayout)findViewById(R.id.word_courselist_test_lil);
		testLl.setOnClickListener(this);
		
		loadList();
		
		// 没有内容则弹出对话框提示内容错误
		if (unitlist == null || unitlist.size() == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(WordlistActivity.this)
			.setTitle(R.string.prompt)
			.setMessage(R.string.prompt_wrong_content)
			.setCancelable(false)
			.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
		           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
		           }					            
		       }); 
		
			builder.create().show();
		} else {
			
			unitAdapter = new unitListAdapter(unitlist,WordlistActivity.this);
			//为listview设置数据
			unitListView.setAdapter(unitAdapter);
			

		}
		
		unitListView.setOnItemClickListener(new unitItemClickListener());
		
		//设置默认为第一个选中,但这个函数不会让classlistview装进来
		unitListView.setSelection(0);
		classAdapter = new classListAdapter(unitlist, classlist, 0, WordlistActivity.this);
		classListView.setAdapter(classAdapter);
		classListView.setOnItemClickListener(new classItemClickListener(0));
	}
	
	private static class unitViewHolder{
		TextView unitname;
		LinearLayout linear;
	}
	
	private class unitListAdapter extends BaseAdapter{
		
		private List<EmodouWordUnit> myunitlist;
		private LayoutInflater inflater;
		private int unitposition;
		
		
		public int getUnitposition() {
			return unitposition;
		}
		
		public void setUnitposition(int unitposition) {
			this.unitposition = unitposition;
		}
		
		public unitListAdapter(List<EmodouWordUnit> myunitlist, Context context){
			super();
			this.myunitlist = myunitlist;
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return myunitlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		//第一个参数position---该视图在适配器数据中的位置 
		//第二个参数convertView---旧视图 
		//第三个参数parent: 此视图最终会被附加到的父级视图 
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view  = convertView;
			unitViewHolder unitholder;
			if(convertView == null){
				view = inflater.inflate(R.layout.word_courselist_unititem, parent, false);
				unitholder = new unitViewHolder();
				unitholder.unitname = (TextView)view.findViewById(R.id.word_courselist_unititem_nametext);
				unitholder.linear = (LinearLayout)view.findViewById(R.id.word_courselist_unititem_linear);
				view.setTag(unitholder);
			}else {
				unitholder = (unitViewHolder)view.getTag();
			}
			unitholder.unitname.setText(myunitlist.get(position).getUnitname());
			
			if(position == unitposition){
				unitholder.linear.setBackgroundResource(R.color.courselist_unititem_click);
                unitholder.unitname.setTextColor(getResources().getColor(R.color.courselist_unititem_numtext_click));
			}else{
				unitholder.linear.setBackgroundResource(R.color.courselist_unititem_unclick);
                unitholder.unitname.setTextColor(getResources().getColor(R.color.courselist_unititem_numtext));
			}
			return view;
		}
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
//			case R.id.word_courselist_startlearn:
//							
//				String wordchoiceStr = "";
//				//由于每次点击时，classlist都是实时刷新，因此此时只要查询classlist中state为选中的Emodouwordclass即可
//				List<EmodouWordClass> wordClass = new ArrayList<EmodouWordClass>();
//				for(int i = 0; i<classlist.size();i++){
//					for(int m = 0; m < classlist.get(i).size(); m++){
//						if(classlist.get(i).get(m).getState().equals(Constants.WORD_CLASS_SELECTED)){
//							wordClass.add(classlist.get(i).get(m));
//						}
//					}
//				}
//				for(int i = 0; i < wordClass.size(); i++){
//					if(i == wordClass.size()-1){
//						wordchoiceStr += wordClass.get(i).getTitle()+" ? ";
//					}else{
//						wordchoiceStr += wordClass.get(i).getTitle()+" ; ";
//					}
//					
//				}
//				
//				AlertDialog.Builder builder = new AlertDialog.Builder(WordlistActivity.this)
//				.setTitle(R.string.prompt)
//				.setMessage("您是否确认选择学习：\n" + wordchoiceStr+"\n")
//				.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
//			           public void onClick(DialogInterface dialog, int whichButton) { 
//			        	   Intent intent = new Intent(WordlistActivity.this, WordwlistActivity.class);
//			        	   intent.putExtra("bookid", bookid);
//			        	   startActivity(intent);
//			           }					            
//			       });
//				builder.show();
//				
//				
//				break;
			
				
			case R.id.courselist_imbtn_return:
				Intent intent2 = new Intent(this, WordmainActivity.class);
				intent2.putExtra("bookid", bookid);
				intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent2);
				break;
				
			case R.id.word_courselist_learn_lil:
				String wordchoiceStr = "";
				//由于每次点击时，classlist都是实时刷新，因此此时只要查询classlist中state为选中的Emodouwordclass即可
				List<EmodouWordClass> wordClass = new ArrayList<EmodouWordClass>();
				for(int i = 0; i<classlist.size();i++){
					for(int m = 0; m < classlist.get(i).size(); m++){
						if(classlist.get(i).get(m).getState().equals(Constants.WORD_CLASS_SELECTED)){
							wordClass.add(classlist.get(i).get(m));
						}
					}
				}
				for(int i = 0; i < wordClass.size(); i++){
					if(i == wordClass.size()-1){
						wordchoiceStr += wordClass.get(i).getTitle()+" ? ";
					}else{
						wordchoiceStr += wordClass.get(i).getTitle()+" ; "+"\n";
					}
					
				}
				
				//如果没有选择任何课程进行学习，弹出提示对话框
				if(wordchoiceStr == ""){
					AlertDialog.Builder builder = new AlertDialog.Builder(WordlistActivity.this)
					.setTitle(R.string.prompt)
					.setMessage(getResources().getString(R.string.word_courselist_nochoice))
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() { 
						 public void onClick(DialogInterface dialog, int whichButton) {  	  
				           }	
				           				            
				       });
					builder.show();		
				}else{
					
					//先判断所选课程的单词是否已经全部学完,即看单词review状态是否全为2
					List<EmodouWordManager> wordManagerList = new ArrayList<EmodouWordManager>();
					DbUtils dbUtils = DbUtils.create(WordlistActivity.this);
					try{
						for(EmodouWordClass wordClass2 : wordClass){
							List<EmodouWordManager> wordManagerPartList = new ArrayList<EmodouWordManager>();
							wordManagerPartList = dbUtils.findAll(Selector.from(EmodouWordManager.class)
									                                  .where("userid","=",userid)
									                                  .and("classid","=",wordClass2.getClassid()));
							wordManagerList.addAll(wordManagerPartList);
						}
						boolean learnedAll = true;
						for(EmodouWordManager wordManager : wordManagerList){
							if(wordManager.getReviewState() != Constants.WORD_REVIEW_STATE_RIGHT){
								learnedAll = false;
							}
						}
						if(learnedAll == true){
							//提示所选课程的单词已经全部学完，是否重新开始学习
							AlertDialog.Builder builder = new AlertDialog.Builder(WordlistActivity.this)
							.setTitle(R.string.prompt)
							.setMessage(getResources().getString(R.string.word_courselist_learnedAll))
							.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() { 
								 public void onClick(DialogInterface dialog, int whichButton) {  
									    Intent intent = new Intent(WordlistActivity.this, WordLearnActivity.class);
						        	    intent.putExtra("bookid", bookid);
									    intent.putExtra("userid", userid);
									    intent.putExtra("learnedAll", "true");
						        	    startActivity(intent);
						        	    //同时在shareprefence中记录下此时的时间，以便学习过程中返回时计算本次学习时间
										SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
										editor.putLong("wordLearn_startTime", System.currentTimeMillis());
										editor.commit();
									 
						           }	   				            
						       });
							builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									
								}
							});
							builder.show();	
						}else{
							//还有没学完的单词，开始学习
							 Intent intent = new Intent(WordlistActivity.this, WordLearnActivity.class);
			        	     intent.putExtra("bookid", bookid);
						     intent.putExtra("userid", userid);
						     intent.putExtra("learnedAll", "false");
			        	     startActivity(intent);
			        	     		
							
						}
						
						
					}catch(DbException e){
						e.printStackTrace();
					}
					
					
				}					
				break;
				
			case R.id.word_courselist_test_lil:
				//由于每次点击时，classlist都是实时刷新，因此此时只要查询classlist中state为选中的Emodouwordclass即可
				int wordNum = 0;
				for(int i = 0; i<classlist.size();i++){
					for(int m = 0; m < classlist.get(i).size(); m++){
						if(classlist.get(i).get(m).getState().equals(Constants.WORD_CLASS_SELECTED)){
							wordNum += classlist.get(i).get(m).getSize();
						}
					}
				}
				if(wordNum == 0){
					AlertDialog.Builder builder = new AlertDialog.Builder(WordlistActivity.this)
					.setTitle(R.string.prompt)
					.setMessage(getResources().getString(R.string.word_courselist_nowordtest))
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() { 
						 public void onClick(DialogInterface dialog, int whichButton) {  	  
				           }	
				           				            
				       });
					builder.show();	
				}else{
					Intent intent = new Intent(WordlistActivity.this, WordTestActivity.class);
			        intent.putExtra("bookid", bookid);
			        intent.putExtra("userid", userid);
			        startActivity(intent);
				}
				break;
				
			default:
				break;
		}
	}
	
	public class unitItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int unitposition, long id) {
			// TODO Auto-generated method stub
			
			//选中哪个，调用classlist的函数 
			classAdapter = new classListAdapter(unitlist, classlist, unitposition, WordlistActivity.this);
			classListView.setAdapter(classAdapter);
			classListView.setOnItemClickListener(new classItemClickListener(unitposition));
			unitAdapter.setUnitposition(unitposition);
			unitAdapter.notifyDataSetChanged();
		}
	}
	
	private static class classViewHolder {
		TextView classname, wordnum;
		ImageView notlearn, learned, learning;
		ImageView imgUnclick, imgClick;
		RelativeLayout rl;
	}

	private class classListAdapter extends BaseAdapter{
		
		private List<EmodouWordUnit> myunitlist;
		private List<List<EmodouWordClass>> myclasslist;
		private int unitposition, myclassposition;
		private LayoutInflater inflater; 
		
		public classListAdapter(List<EmodouWordUnit> myunitlist, List<List<EmodouWordClass>> myclasslist, int unitposition, Context context){
			super();
			this.myunitlist = myunitlist;
			this.myclasslist = myclasslist;
			this.unitposition = unitposition;
			inflater = LayoutInflater.from(context);
		}
		
		public void setClassposition(int classposition){
			this.myclassposition = classposition;
		}
		public void setClasslist(List<List<EmodouWordClass>> myclasslist) {
			this.myclasslist = myclasslist;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return myclasslist.get(unitposition).size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return myclasslist.get(unitposition).get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int classposition, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			classViewHolder classHolder;
			if(convertView == null){
				view = inflater.inflate(R.layout.word_courselist_classitem, parent, false);
				classHolder = new classViewHolder();
				
				classHolder.classname = (TextView)view.findViewById(R.id.word_courselist_classlist_nametext);
				classHolder.notlearn = (ImageView)view.findViewById(R.id.word_courselist_classlist_notlearn);
				classHolder.learning = (ImageView)view.findViewById(R.id.word_courselist_classlist_learning);
				classHolder.learned = (ImageView)view.findViewById(R.id.word_courselist_classlist_learned);
				classHolder.imgUnclick = (ImageView)view.findViewById(R.id.word_courselist_classlist_img_unclick);
				classHolder.imgClick = (ImageView)view.findViewById(R.id.word_courselist_classlist_img_click);
				classHolder.rl = (RelativeLayout)view.findViewById(R.id.word_courselist_classlist_rl);
				classHolder.wordnum = (TextView)view.findViewById(R.id.word_courselist_classlist_wordnum);
				view.setTag(classHolder);
			}else{
				classHolder = (classViewHolder)view.getTag();
			}
			
			
			EmodouWordClass wordClass = myclasslist.get(unitposition).get(classposition);
			classHolder.classname.setText(wordClass.getTitle());
			classHolder.wordnum.setText(wordClass.getSize()+"个单词");
			
			String state = wordClass.getState();
			if(state.equals(Constants.WORD_CLASS_NOT_SELECT)){
				classHolder.rl.setBackgroundColor(Color.WHITE);
				classHolder.imgUnclick.setVisibility(View.VISIBLE);
				classHolder.imgClick.setVisibility(View.GONE);
			}else{
				classHolder.rl.setBackgroundColor(Color.rgb(235, 219, 232));
				classHolder.imgUnclick.setVisibility(View.GONE);
				classHolder.imgClick.setVisibility(View.VISIBLE);
			}
			
			//查找该课的学习状态
			final DbUtils dbUtils = DbUtils.create(WordlistActivity.this);
			try{
				EmodouClassManager classManager = new EmodouClassManager();
				classManager = dbUtils.findFirst(Selector.from(EmodouClassManager.class)
														.where("userid","=",userid)
														.and("classid","=",wordClass.getClassid()));
				if(classManager!=null){
					if(classManager.getState() == Constants.EMODOU_CLASS_STATE_NOT_LEAREN){
						classHolder.notlearn.setVisibility(View.VISIBLE);
						classHolder.learning.setVisibility(View.GONE);
						classHolder.learned.setVisibility(View.GONE);
					}else if(classManager.getState() == Constants.EMODOU_CLASS_STATE_LEARENING){
						classHolder.notlearn.setVisibility(View.GONE);
						classHolder.learning.setVisibility(View.VISIBLE);
						classHolder.learned.setVisibility(View.GONE);
					}else{
						classHolder.notlearn.setVisibility(View.GONE);
						classHolder.learning.setVisibility(View.GONE);
						classHolder.learned.setVisibility(View.VISIBLE);
					}
				}
			}catch(DbException e){
				e.printStackTrace();
			}
			
			//若多选框被选中
			classHolder.imgUnclick.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//点击后重设数值,选中的点击设为没选中 反之相同
					String state = classlist.get(unitposition).get(classposition).getState();
					String classid = classlist.get(unitposition).get(classposition).getClassid();
					String title = classlist.get(unitposition).get(classposition).getTitle();
					
					EmodouWordClass wordClass = new EmodouWordClass();
					try {
						wordClass = dbUtils.findFirst(Selector.from(EmodouWordClass.class)
						        											    .where("bookid","=",bookid)
						        											 	.and("userid","=",userid)
						        											 	.and("classid","=",classid)
						        											 	.and("title","=",title));
						wordClass.setState(Constants.WORD_CLASS_SELECTED);
						dbUtils.update(wordClass);
						classlist.clear();
						loadList();
						classAdapter.setClasslist(classlist);
						classAdapter.notifyDataSetChanged();
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}			
				}
			});
			classHolder.imgClick.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//点击后重设数值,选中的点击设为没选中 反之相同
					String state = classlist.get(unitposition).get(classposition).getState();
					String classid = classlist.get(unitposition).get(classposition).getClassid();
					String title = classlist.get(unitposition).get(classposition).getTitle();
					
					EmodouWordClass wordClass = new EmodouWordClass();
					try {
						wordClass = dbUtils.findFirst(Selector.from(EmodouWordClass.class)
						        											    .where("bookid","=",bookid)
						        											 	.and("userid","=",userid)
						        											 	.and("classid","=",classid)
						        											 	.and("title","=",title));
						wordClass.setState(Constants.WORD_CLASS_NOT_SELECT);
						dbUtils.update(wordClass);
						classlist.clear();
						loadList();
						classAdapter.setClasslist(classlist);
						classAdapter.notifyDataSetChanged();
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}			
				}
			});
			return view;
		}
		
		
	}
	
	public class  classItemClickListener implements OnItemClickListener{
		
		int unitposition;
		public classItemClickListener(int unitposition){
			this.unitposition = unitposition;
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			
			// TODO Auto-generated method stub
			//点击后重设数值,选中的点击设为没选中 反之相同
			/*String state = classlist.get(unitposition).get(position).getState();
			String classid = classlist.get(unitposition).get(position).getClassid();
			String title = classlist.get(unitposition).get(position).getTitle();
			DbUtils dbUtils4 = DbUtils.create(WordlistActivity.this);
			try{
				EmodouWordClass wordClass = dbUtils4.findFirst(Selector.from(EmodouWordClass.class)
                        .where("bookid","=",bookid)
                        .and("userid","=",userid)
                        .and("classid","=",classid)
                        .and("title","=",title));
				
				if(state.equals(Constants.WORD_CLASS_NOT_SELECT)){
					wordClass.setState(Constants.WORD_CLASS_SELECTED);
				}else{
					wordClass.setState(Constants.WORD_CLASS_NOT_SELECT);
				}
				dbUtils4.update(wordClass);
			}catch(DbException e){
				e.printStackTrace();
			}
			classlist.clear();
			loadList();
			classAdapter.setClasslist(classlist);
			classAdapter.notifyDataSetChanged();*/
			
			String hint = "fromWordlistActivity";
			String classid = classlist.get(unitposition).get(position).getClassid();
			Intent intent = new Intent(WordlistActivity.this, WordwlistActivity.class);
			intent.putExtra("classid", classid);
			intent.putExtra("userid", userid);
			intent.putExtra("bookid", bookid);
			intent.putExtra("hint", hint);
			startActivity(intent);
			
		}
		
	}
	
	public void onBackPressed(){
		Intent intent2 = new Intent(this, WordmainActivity.class);
		intent2.putExtra("bookid", bookid);
		intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent2);
	}
}
