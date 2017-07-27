package com.emodou.home;

import java.io.File;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import java.util.List;


import com.emodou.domain.EmodouBook;
import com.emodou.domain.EmodouClass;
import com.emodou.domain.EmodouClassManager;
import com.emodou.domain.EmodouReadText;
import com.emodou.domain.EmodouUnit;
import com.emodou.home.ListenActivity;
import com.emodou.home.ReadActivity;
import com.emodou.home.SpeakActivity;
import com.emodou.login.EmodouLoginAcvitity;
import com.emodou.service.RecordIntentService;
import com.emodou.util.Constants;
import com.emodou.util.ValidateUtils;
import com.example.emodou.HomeActivity;
import com.example.emodou.MainActivity;
import com.example.emodou.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;
import android.view.View.OnClickListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter.LengthFilter;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



public class CourseListActivity extends Activity implements OnClickListener{
	
	
	//actionbar及其相关
	private ActionBar actionbar;
	private TextView titletext;
	private ImageButton imbReturn, imbdelete;
	private RelativeLayout rl_color;
	
	//界面相关属性
	private ListView unit_listview, class_listview;
	private String type, bookid,  userid;
	private String articleUrl, classname, classid, classurl;
	private String unitIdString;
	//private Button downloadall;
	private List<EmodouUnit> unitlist = new ArrayList<EmodouUnit>();
	private List<List<EmodouClass>> classlist = new ArrayList<List<EmodouClass>>();
	private ProgressDialog progressDialog;
	private classListAdapter classAdapter;
	private unitListAdapter unitAdapter;
	//private TextView numtext;
	
	//用来保存下载handler的map
	private Map<String, HttpHandler> myhandlerMap = new HashMap<String, HttpHandler>();
	
	// 日志tag
	private static final String LOG_TAG = CourseListActivity.class.getSimpleName();
		
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_courselist);       
        
        //获得home界面传过来的参数
        bookid = getIntent().getExtras().getString("bookid");
		type = getIntent().getExtras().getString("type");
		
		userid = ValidateUtils.getUserid(CourseListActivity.this);
        
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
 		imbdelete.setOnClickListener(this);
 		
 		
 		
 		DbUtils dbUtils = DbUtils.create(this);
 		EmodouBook book1 = null;
 		try{
 			book1 = dbUtils.findFirst(Selector.from(EmodouBook.class).where(
					"bookid", "=", bookid));
 			if(book1!=null){
 				//需要根据传过来的参数更改颜色
 		 		if(Constants.EMODOU_TYPE_LISTEN.equals(type)){
 		 			rl_color.setBackgroundColor(getResources().getColor(R.color.actionbar_listen));
 		 			titletext.setText(R.string.actionbar_courselist_listen);
 		 		}else if(Constants.EMODOU_TYPE_READ.equals(type)){
 		 			rl_color.setBackgroundColor(getResources().getColor(R.color.actionbar_read));
 		 			titletext.setText(R.string.actionbar_courselist_read);
 		 		}else if(Constants.EMODOU_TYPE_SPEAK.equals(type)){
 		 			rl_color.setBackgroundColor(getResources().getColor(R.color.actionbar_speak));
 		 			titletext.setText(R.string.actionbar_courselist_speak);
 		 		}
 			}
 		}catch(DbException e){
 			e.printStackTrace();
 		}
 	}
	
	public void loadList(){ 
		DbUtils dbUtils = DbUtils.create(CourseListActivity.this);
		
		try{
			List<EmodouUnit> entityUnitList = dbUtils.findAll(Selector
											.from(EmodouUnit.class)
											.where("bookid","=",bookid));
			
			if(entityUnitList!=null){
				unitlist = entityUnitList;
				
				//根据单元名和类别取出相应的列表添加到相应的list中
				for(int i=0; i<unitlist.size();i++){
					unitIdString = unitlist.get(i).getUnitid();
					List<EmodouClass> entityClassList = new ArrayList<EmodouClass>();
					entityClassList = dbUtils.findAll(Selector
									.from(EmodouClass.class)
									.where("unitid","=",unitIdString)
									.and("type","=",type));
					if(entityClassList == null || entityClassList.size() ==0){
						unitlist.get(i).setUnitid("delete");//不能用remove函数，remove会减少数组长度
					}else{
						classlist.add(entityClassList); 
					}
				}
				//这个问题是说,你不能在对一个List进行遍历的时候将其中的元素删除掉
				//解决办法是,你可以先将要删除的元素用另一个list装起来,等遍历结束再remove掉
				List<EmodouUnit> unitDeleteList = new ArrayList<EmodouUnit>();
				for(EmodouUnit emodouUnit : unitlist){
					if(emodouUnit.getUnitid().equals("delete"))
						unitDeleteList.add(emodouUnit);
				}
				unitlist.removeAll(unitDeleteList);
				
			}
		}catch(DbException e){
			e.printStackTrace();
		}
		
	}
	
	private void init() {
		// TODO Auto-generated method stub
		unit_listview = (ListView)findViewById(R.id.home_courselist_listview_unit);
		class_listview = (ListView)findViewById(R.id.home_courselist_listview_class);
		//downloadall = (Button)findViewById(R.id.home_courselist_downloadall);
		
		//是否隐藏下载全部的按钮
		DbUtils dbUtils = DbUtils.create(this);
		EmodouClassManager classManager1 = null;
		try{
			//如果有没下载的课程，就让下载全部按钮可见
			classManager1 = dbUtils.findFirst(Selector
							.from(EmodouClassManager.class)
							.where("bookid", "=", bookid)
							.and("downloadstate","!=",Constants.EMODOU_CLASS_DOWNLOAD_STATE_ALREADY_DOWNLOAD)
							.and("type","=",type));
		}catch(DbException e){
			e.printStackTrace();
		}
		
//		if(classManager1!=null){
//			downloadall.setVisibility(View.VISIBLE);
//		}
		
		
		loadList();
		
		
		// 没有内容则弹出对话框提示内容错误
		if (unitlist == null || unitlist.size() == 0||classlist.size() == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(CourseListActivity.this)
			.setTitle(R.string.prompt)
			.setMessage(R.string.home_courselist_nocourse)
			.setCancelable(false)
			.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
		           public void onClick(DialogInterface dialog, int whichButton) {
		        	   onBackPressed();
		           }					            
		       }); 
		
			builder.create().show();  
		}else {
			
			unitAdapter = new unitListAdapter(unitlist,CourseListActivity.this);
			//为listview设置数据
			unit_listview.setAdapter(unitAdapter);
			
			//为unit_listview设置点击函数
			unit_listview.setOnItemClickListener(new unitItemClickListener());
			
			//设置默认为第一个选中,但这个函数不会让classlistview装进来，后面两行手动设置将classAdapter装进来
			unit_listview.setSelection(0);
			classAdapter = new classListAdapter(unitlist, classlist, 0, CourseListActivity.this);
			class_listview.setAdapter(classAdapter);
			class_listview.setOnItemClickListener(new classItemClickListener(0));

		}
		
		
		
	}
	
	
	
	private class unitListAdapter extends BaseAdapter{
		
		private List<EmodouUnit> myunitlist;
		private LayoutInflater inflater;
		private int unitposition;
		
		
		
		public int getUnitposition() {
			return unitposition;
		}
		public void setUnitposition(int unitposition) {
			this.unitposition = unitposition;
		}
		
		public unitListAdapter(List<EmodouUnit> myunitlist, Context context){
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
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		//第一个参数position---该视图在适配器数据中的位置 
		//第二个参数convertView---旧视图 
		//第三个参数parent: 此视图最终会被附加到的父级视图 
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			unitViewHolder unitholder;
			if(convertView == null){
				view = inflater.inflate(R.layout.home_courselist_unititem, parent, false);
				unitholder = new unitViewHolder();
				unitholder.unitnum = (TextView)view.findViewById(R.id.home_courselist_unititem_numtext);
				unitholder.unitname = (TextView)view.findViewById(R.id.home_courselist_unititem_nametext);
				unitholder.linear = (LinearLayout)view.findViewById(R.id.home_courselist_unititem_ln);
				view.setTag(unitholder);
			}else{
				unitholder = (unitViewHolder)view.getTag();
			}
			unitholder.unitnum.setText(myunitlist.get(position).getUnitname());
			String nameString = myunitlist.get(position).getUnitdes();
			if (nameString != null) {
				unitholder.unitname.setText(nameString);
			}else{
				unitholder.unitname.setText("");
			}
			
			if(position == unitposition){
				unitholder.linear.setBackgroundResource(R.color.courselist_unititem_click);
                unitholder.unitnum.setTextColor(getResources().getColor(R.color.courselist_unititem_numtext_click));
			}else{
				unitholder.linear.setBackgroundResource(R.color.courselist_unititem_unclick);
                unitholder.unitnum.setTextColor(getResources().getColor(R.color.courselist_unititem_numtext));
			}
			
			
			return view;
		}
		
	}
	
	private static class unitViewHolder{
		TextView unitnum, unitname;
		LinearLayout linear;
	}
	
	public class unitItemClickListener implements OnItemClickListener{
		
		@Override
		//Y里有a,b,c,d这4个item
		//adapter相当于listview 适配器的一个指针，可以通过它来获得listview里装着的一切东西
		//view是你点的b这个view的句柄，就是你可以用这个view，来获得b里的控件的id后操作控件
		// position是b在Y适配器里的位置（生成listview时，适配器一个一个的做item，然后把他们按顺序排好队，
		//在放到listview里，意思就是这个b是第position号做好的）,通常从0开始
		// id是b在listview Y里的第几行的位置（很明显是第2行），大部分时候position和id的值是一样的
		public void onItemClick(AdapterView<?> adapter, View view, int unitposition,
				long id) {
			// TODO Auto-generated method stub
			
			//选中哪个，调用classlist的函数
			classAdapter = new classListAdapter(unitlist, classlist, unitposition, CourseListActivity.this);
			class_listview.setAdapter(classAdapter);
			
			class_listview.setOnItemClickListener(new classItemClickListener(unitposition));
			
			unitAdapter.setUnitposition(unitposition);
			unitAdapter.notifyDataSetChanged();
			//设置改变点击unit颜色
			
		
		}
		
	}
	
	private static class classViewHolder{
		TextView classname;
		ImageView home_courselist_notlearn, home_courselist_listen_learned, home_courselist_listen_learning,
				 home_courselist_read_learned, home_courselist_read_learning, home_courselist_speak_learned,
				 home_courselist_speak_learning;
		HoloCircularProgressBar downloadProgressBar, downloadingProgressBar,
								downloadPauseProgressbar;
		ImageView home_courselist_downloaded;
		ImageView home_courselist_wait;
		
	}
	
	private class classListAdapter extends BaseAdapter{
		
		private List<EmodouUnit> myunitlist;
		private List<List<EmodouClass>> myclasslist;
		private int unitposition;
		private LayoutInflater inflater;
		
		public classListAdapter(List<EmodouUnit> myunitlist, List<List<EmodouClass>> myclasslist, int unitposition, Context context){
			super();
			this.myunitlist = myunitlist;
			this.myclasslist = myclasslist;
			this.unitposition = unitposition;
			inflater = LayoutInflater.from(context);
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
			return  arg0;
		}

		@Override
		public View getView(int classposition, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			final classViewHolder classholder;
			if(convertView == null){
				view = inflater.inflate(R.layout.home_courselist_classitem, parent, false);
				classholder = new classViewHolder();
				
				classholder.classname = (TextView)view.findViewById(R.id.home_courselist_classlist_nametext);
				classholder.home_courselist_notlearn = (ImageView)view.findViewById(R.id.home_courselist_notlearn);
				
				classholder.home_courselist_listen_learned = (ImageView)view.findViewById(R.id.home_courselist_listen_learned);
				classholder.home_courselist_listen_learning = (ImageView)view.findViewById(R.id.home_courselist_listen_learning);
				 
				classholder.home_courselist_read_learned = (ImageView)view.findViewById(R.id.home_courselist_read_learned);
				classholder.home_courselist_read_learning = (ImageView)view.findViewById(R.id.home_courselist_read_learning);
				
				classholder.home_courselist_speak_learned = (ImageView)view.findViewById(R.id.home_courselist_speak_learned);
				classholder.home_courselist_speak_learning = (ImageView)view.findViewById(R.id.home_courselist_speak_learning);
				
				//下载进度条
				classholder.downloadProgressBar = (HoloCircularProgressBar)view.findViewById(R.id.home_courselist_hcp_download);
				classholder.downloadingProgressBar = (HoloCircularProgressBar)view.findViewById(R.id.home_courselist_hcp_downloading);
				classholder.downloadPauseProgressbar = (HoloCircularProgressBar)view.findViewById(R.id.home_courselist_hcp_loadpause);
				
				classholder.home_courselist_downloaded = (ImageView)view.findViewById(R.id.home_courselist_downloaded);				
				
				classholder.home_courselist_wait = (ImageView)view.findViewById(R.id.home_courselist_wait);
				
				view.setTag(classholder);
			}else{
				classholder = (classViewHolder)view.getTag();
			}
			
			
			// 设置课程名称
			classholder.classname.setText(myclasslist.get(unitposition).get(classposition).getTitle());
			//如果可用则进行各种状态的正常变化
				
				
			classholder.classname.setTextColor(getResources().getColor(R.color.courselist_classitem_nametext));
			// 获取每个位置的课程id
			classid = classlist.get(unitposition).get(classposition).getClassid();

			// 初始化课程管理对象
			EmodouClassManager classManager = null;

			DbUtils dbUtils = DbUtils.create(CourseListActivity.this);
			try {
				classManager = dbUtils.findFirst(Selector
						.from(EmodouClassManager.class)
						.where("bookid", "=", bookid)
						.and("classid", "=", classid)
						.and("type", "=", type)
						.and("userid", "=",userid));
				// 根据是否学完本课内容显示列表前面的图片
				if (classManager.getState() == Constants.EMODOU_CLASS_STATE_LEARENED) {
					classholder.home_courselist_notlearn.setVisibility(View.INVISIBLE);

					if (Constants.EMODOU_TYPE_READ.equals(type)) {
						classholder.home_courselist_read_learned.setVisibility(View.VISIBLE);
					} else if (Constants.EMODOU_TYPE_SPEAK.equals(type)) {
						classholder.home_courselist_speak_learned.setVisibility(View.VISIBLE);
					} else if (Constants.EMODOU_TYPE_LISTEN.equals(type)) {
						classholder.home_courselist_listen_learned.setVisibility(View.VISIBLE);
					}

				} else if (classManager.getState() == Constants.EMODOU_CLASS_STATE_NOT_LEAREN) {
					classholder.home_courselist_notlearn.setVisibility(View.VISIBLE);

					if (Constants.EMODOU_TYPE_READ.equals(type)) {
						classholder.home_courselist_read_learned.setVisibility(View.INVISIBLE);
					} else if (Constants.EMODOU_TYPE_SPEAK.equals(type)) {
						classholder.home_courselist_speak_learned.setVisibility(View.INVISIBLE);
					} else if (Constants.EMODOU_TYPE_LISTEN.equals(type)) {
						classholder.home_courselist_listen_learned.setVisibility(View.INVISIBLE);
					}
				} else if (classManager.getState() == Constants.EMODOU_CLASS_STATE_LEARENING) {

					classholder.home_courselist_notlearn.setVisibility(View.INVISIBLE);

					if (Constants.EMODOU_TYPE_READ.equals(type)) {
						classholder.home_courselist_read_learning.setVisibility(View.VISIBLE);
					} else if (Constants.EMODOU_TYPE_SPEAK.equals(type)) {
						classholder.home_courselist_speak_learning.setVisibility(View.VISIBLE);
					} else if (Constants.EMODOU_TYPE_LISTEN.equals(type)) {
						classholder.home_courselist_listen_learning.setVisibility(View.VISIBLE);
					}

				}
			}catch (DbException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			// 初始化进度条
			classholder.downloadProgressBar.setProgress(classManager.getProgress());
			classholder.downloadProgressBar.setThumbEnabled(false);//箭头
			classholder.downloadProgressBar.setMarkerEnabled(false);
			classholder.downloadProgressBar.setProgressBackgroundColor((int) Color.rgb(206, 206, 206));//设置灰色圆圈边
			classholder.downloadProgressBar.setWheelSize(5);
			// 初始化下载中进度条
			classholder.downloadingProgressBar.setProgress(classManager.getProgress());
			classholder.downloadingProgressBar.setThumbEnabled(false);
			classholder.downloadingProgressBar.setMarkerEnabled(false);
			classholder.downloadingProgressBar.setProgressBackgroundColor((int) Color.rgb(206, 206, 206));
			classholder.downloadingProgressBar.setWheelSize(5);
			// 初始化暂停进度条
			classholder.downloadPauseProgressbar.setProgress(classManager.getProgress());
			classholder.downloadPauseProgressbar.setThumbEnabled(false);
			classholder.downloadPauseProgressbar.setMarkerEnabled(false);
			classholder.downloadPauseProgressbar.setProgressBackgroundColor((int) Color.rgb(206, 206, 206));
			classholder.downloadPauseProgressbar.setWheelSize(5);
			
			
			// 根据听说读设置右侧需要显示的视图
			if (Constants.EMODOU_TYPE_READ.equals(type)) {
				classholder.downloadProgressBar.
							setBackgroundResource(
							R.drawable.home_courselist_classitem_read_download_selector);
				classholder.home_courselist_downloaded.
							setImageDrawable(getResources().getDrawable(
							R.drawable.home_courselist_classitem_read_downloaded_selector));
				classholder.downloadProgressBar.
							setProgressColor((int) Color.rgb(48,173, 214));
				
				classholder.downloadingProgressBar.
							setBackgroundResource(R.drawable.home_courselist_classitem_pause_selector);					
				classholder.downloadPauseProgressbar.
							setBackgroundResource(R.drawable.home_courselist_classitem_read_downloading_selector);
				
				
				classholder.downloadingProgressBar.
							setProgressColor((int) Color.rgb(48, 173, 214));
				classholder.downloadPauseProgressbar.
							setProgressColor((int) Color.rgb(48, 173, 214));

			} else if (Constants.EMODOU_TYPE_SPEAK.equals(type)) {
				classholder.downloadProgressBar.
							setBackgroundResource(
							R.drawable.home_courselist_classitem_speak_download_selector);
				classholder.home_courselist_downloaded.
							setImageDrawable(getResources().getDrawable(
							R.drawable.home_courselist_classitem_speak_downloaded_selector));					
				classholder.downloadProgressBar.
							setProgressColor((int) Color.rgb(255, 137, 55));

				classholder.downloadingProgressBar.
							setBackgroundResource(R.drawable.home_courselist_classitem_pause_selector);					
				classholder.downloadPauseProgressbar.
							setBackgroundResource(R.drawable.home_courselist_classitem_speak_downloading_selector);					

				classholder.downloadingProgressBar.setProgressColor((int) Color.rgb(255, 137, 55));
				classholder.downloadPauseProgressbar.setProgressColor((int) Color.rgb(255, 137, 55));

			} 				
			//DEFAULT为听
			else {
				classholder.downloadProgressBar.
							setBackgroundResource(
							R.drawable.home_courselist_classitem_listen_download_selector);
				classholder.home_courselist_downloaded.
							setImageDrawable(getResources().getDrawable(
							R.drawable.home_courselist_classitem_listen_downloaded_selector));
				classholder.downloadProgressBar.
							setProgressColor((int) Color.rgb(19,220, 152));

				classholder.downloadingProgressBar
							.setBackgroundResource(R.drawable.home_courselist_classitem_pause_selector);
				
				classholder.downloadPauseProgressbar
							.setBackgroundResource(R.drawable.home_courselist_classitem_listen_downloading_selector);

				classholder.downloadingProgressBar
							.setProgressColor((int) Color.rgb(19, 220, 152));

				classholder.downloadPauseProgressbar
							.setProgressColor((int) Color.rgb(19, 220, 152));
			}
			
			
			
			classid = classlist.get(unitposition).get(classposition).getClassid();
			
			//如果本地已经存在下载好的内容更新数据库 将状态改为已经下载完成
			articleUrl = Constants.LOCAL_START + type + "/" + classid
					+ Constants.LOCAL_JSON1;
			
			if(ValidateUtils.isExist(articleUrl)){
				classManager.setDownloadstate(Constants.EMODOU_CLASS_DOWNLOAD_STATE_ALREADY_DOWNLOAD);
			}
			try {
				dbUtils.update(classManager);
			} catch (DbException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//根据下载状态显示右边的相应图片或进度条
			if (classManager.getDownloadstate() == Constants.EMODOU_CLASS_DOWNLOAD_STATE_ALREADY_DOWNLOAD) {
				classholder.downloadProgressBar.setVisibility(View.INVISIBLE);
				classholder.home_courselist_downloaded.setVisibility(View.VISIBLE);
				classholder.downloadingProgressBar.setVisibility(View.INVISIBLE);
				classholder.home_courselist_wait.setVisibility(View.INVISIBLE);
				classholder.downloadPauseProgressbar.setVisibility(View.INVISIBLE);
			} else if (classManager.getDownloadstate() == Constants.EMODOU_CLASS_DOWNLOAD_STATE_NOT_DOWNLOAD) {
				classholder.downloadProgressBar.setVisibility(View.VISIBLE);
				classholder.home_courselist_downloaded.setVisibility(View.INVISIBLE);
				classholder.downloadingProgressBar.setVisibility(View.INVISIBLE);
				classholder.home_courselist_wait.setVisibility(View.INVISIBLE);
				classholder.downloadPauseProgressbar.setVisibility(View.INVISIBLE);
			} else if (classManager.getDownloadstate() == Constants.EMODOU_CLASS_DOWNLOAD_STATE_DOWNLOADING) {
				classholder.downloadingProgressBar.setVisibility(View.VISIBLE);
				classholder.downloadingProgressBar.setProgress(classManager
						.getProgress());
				classholder.downloadProgressBar.setVisibility(View.INVISIBLE);
				classholder.home_courselist_downloaded.setVisibility(View.INVISIBLE);
				classholder.home_courselist_wait.setVisibility(View.INVISIBLE);
				classholder.downloadPauseProgressbar.setVisibility(View.INVISIBLE);
			} else if (classManager.getDownloadstate() == Constants.EMODOU_CLASS_DOWNLOAD_STATE_WAIT) {
				classholder.downloadProgressBar.setVisibility(View.INVISIBLE);
				classholder.home_courselist_wait.setVisibility(View.VISIBLE);
				classholder.downloadingProgressBar.setVisibility(View.INVISIBLE);
				classholder.downloadPauseProgressbar.setVisibility(View.INVISIBLE);
				classholder.home_courselist_downloaded.setVisibility(View.INVISIBLE);
			} else if (classManager.getDownloadstate() == Constants.EMODOU_CLASS_DOWNLOAD_STATE_PAUSE) {
				classholder.downloadProgressBar.setVisibility(View.INVISIBLE);
				classholder.home_courselist_wait.setVisibility(View.INVISIBLE);
				classholder.downloadingProgressBar.setVisibility(View.INVISIBLE);
				classholder.downloadPauseProgressbar.setVisibility(View.VISIBLE);
				classholder.home_courselist_downloaded.setVisibility(View.INVISIBLE);
			}
			
			//如果正在下载
			if (classManager.getDownloadstate() == Constants.EMODOU_CLASS_DOWNLOAD_STATE_DOWNLOADING) {

				classholder.downloadingProgressBar.setVisibility(View.VISIBLE);
				classholder.downloadProgressBar.setVisibility(View.INVISIBLE);

				classurl = Constants.EMODOU_RESURL + "/"+ classlist.get(unitposition).get(classposition).getResurl();

				System.out.println(classurl);
				Log.d("url", classurl);
				final String classid = classlist.get(unitposition).get(classposition).getClassid();
				classname = classlist.get(unitposition).get(classposition).getClassdes();
				articleUrl = Constants.LOCAL_START + type + "/" + classid+ Constants.LOCAL_JSON1;

				HttpUtils http = new HttpUtils();

				//找运行时变量map里面是否有相应的handler如果没有就新建一个
				if (myhandlerMap.get(classid) == null) {
					HttpHandler httpHandler = http.download(classurl,
							Constants.LOCAL_START + type + "/" + classid + "/"
									+ classid + ".zip", true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将重新下载。
							true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
							new RequestCallBack<File>() {

								@Override
								public void onStart() {
								}

								@Override
								//下载的过程中实时更新数据库中的进度
								public void onLoading(long total, long current,
										boolean isUploading) {

									Log.d(LOG_TAG, "progress" + current);

									classholder.downloadingProgressBar
											.setProgress((float) ((int) (100 * current / total)) / 100);
									DbUtils dbUtils = DbUtils
											.create(CourseListActivity.this);
									try {
										EmodouClassManager classManager = dbUtils
												.findFirst(Selector
														.from(EmodouClassManager.class)
														.where("bookid", "=",bookid)
														.and("classid", "=",classid)
														.and("type", "=", type)
														.and("userid", "=", userid));
										if(classManager != null && dbUtils != null){
											classManager.setProgress((float) ((int) (100 * current / total)) / 100);
											dbUtils.update(classManager);
										}											
									} catch (Exception e) {
										e.printStackTrace();
									}

								}

								@Override
								public void onCancelled() {
									Log.d("progress", classname + "cancel");
									System.out.println(classname + "cancel");
									super.onCancelled();
								}

								@Override
								//下载成功后 首先将本条状态在数据库中更新为已下载，其次如果还有等待中的任务则开启一个新的任务，并且在
								//运行时变量map中将本条记录的handler移除5
								public void onSuccess(
										ResponseInfo<File> responseInfo) {
									DbUtils dbUtils = DbUtils.create(CourseListActivity.this);
									try {
										EmodouClassManager classManager = dbUtils.findFirst(Selector
																		.from(EmodouClassManager.class)
																		.where("bookid", "=",bookid)
																		.and("classid", "=",classid)
																		.and("type", "=", type)
																		.and("userid", "=", userid));
										
										if(classManager != null && dbUtils != null){
											classManager.setDownloadstate(Constants.EMODOU_CLASS_DOWNLOAD_STATE_ALREADY_DOWNLOAD);
											dbUtils.update(classManager);
										}
										

										EmodouClassManager classManager2 = dbUtils.findFirst(Selector
																		.from(EmodouClassManager.class)
																		.where("bookid", "=",bookid)
																		.and("downloadstate","=",Constants.EMODOU_CLASS_DOWNLOAD_STATE_WAIT)
																		.and("type", "=", type)
																		.and("userid", "=", userid));
										if (classManager2 != null && dbUtils != null) {
											classManager2.setDownloadstate(Constants.EMODOU_CLASS_DOWNLOAD_STATE_DOWNLOADING);
											dbUtils.update(classManager2);
										}

										if(myhandlerMap != null){
											myhandlerMap.remove(classid);
										}
										

									} catch (Exception e) {
										e.printStackTrace();
									}
									if(classAdapter != null){
										classAdapter.notifyDataSetChanged();
									}
									
								}

								@Override
								public void onFailure(HttpException error,String msg) {
									error.printStackTrace();

								}

								
							});
					
					//将handler放进map

					myhandlerMap.put(classid, httpHandler);

				}try {
					dbUtils.update(classManager);
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
									
			}
		
			
			return view;
		}
	}//本listAdapter结束
	
	
	public class classItemClickListener implements OnItemClickListener{
		
		int unitposition;
		public classItemClickListener(int unitposition) {
			// TODO Auto-generated constructor stub
			this.unitposition = unitposition;
		}

		

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int classposition,
				long id) {
			// TODO Auto-generated method stub
			
			//获取classid 和正常解压之后文件应该存在的地址
			classid = classlist.get(unitposition).get(classposition).getClassid();
			articleUrl = Constants.LOCAL_START + type + "/" + classid + Constants.LOCAL_JSON1;

			
			//打开数据库连接 并且将当前选中的课程的课程管理类选中
			final DbUtils dbUtils = DbUtils.create(CourseListActivity.this);
			EmodouClassManager classManager = null;
			try {
				classManager = dbUtils.findFirst(Selector
							.from(EmodouClassManager.class)
							.where("bookid", "=", bookid)
							.and("classid", "=", classid)
							.and("type", "=", type)
							.and("userid", "=", userid));
			} catch (Exception e) {
				e.printStackTrace();
			}

			Intent itent = null;
			//如果发现已经解压完成直接跳相应的页面
			if (ValidateUtils.isExist(articleUrl)) {

				if (Constants.EMODOU_TYPE_LISTEN.equals(type)) {
					itent = new Intent(CourseListActivity.this, ListenActivity.class);
				} else if (Constants.EMODOU_TYPE_READ.equals(type)) {
					itent = new Intent(CourseListActivity.this, ReadActivity.class);
				} else if (Constants.EMODOU_TYPE_SPEAK.equals(type)) {
					itent = new Intent(CourseListActivity.this, SpeakActivity.class);
				}
				Bundle mBundle = new Bundle();
				mBundle.putString("type", type);
				mBundle.putString("classid", classid);
				mBundle.putString("bookid", bookid);

				itent.putExtras(mBundle);
				startActivity(itent);

			} 
			//没有发现解压完成的文件 但是相应的管理类显示状态为已经下载完成
			else if (classManager.getDownloadstate() == Constants.EMODOU_CLASS_DOWNLOAD_STATE_ALREADY_DOWNLOAD) {

				
				//为了避免解压不完全的情况，首先盘算是否有解压出来的资源文件 文件夹如果有 删掉
				if (ValidateUtils.isExist(Constants.LOCAL_START + type + "/"+ classid + Constants.LOCAL_RES)) {
					ValidateUtils.deleteDirectory(Constants.LOCAL_START + type + "/" + classid + Constants.LOCAL_RES);
				}

				
				//弹出对话等待框
				progressDialog = new ProgressDialog(CourseListActivity.this);
				progressDialog.setIndeterminate(true);
				progressDialog.setMessage("Loading...");
				progressDialog.show();

				//拼串解压相应文件 ， 解压完之后 进行相应的跳转
				Log.d("url", "step1");
				File zipFile = new File(Constants.LOCAL_START + type + "/"
						+ classid + "/" + classid + Constants.LOCAL_ZIP);
				try {
					String unzipLocation = Constants.LOCAL_START + type + "/" + classid;
					ValidateUtils.unzipFiles3(zipFile, unzipLocation);

					if (Constants.EMODOU_TYPE_LISTEN.equals(type)) {
						itent = new Intent(CourseListActivity.this, ListenActivity.class);
					} else if (Constants.EMODOU_TYPE_READ.equals(type)) {
						itent = new Intent(CourseListActivity.this, ReadActivity.class);
					} else if (Constants.EMODOU_TYPE_SPEAK.equals(type)) {
						itent = new Intent(CourseListActivity.this, SpeakActivity.class);
					}

					progressDialog.dismiss();
					Bundle mBundle = new Bundle();
					mBundle.putString("type", type);
					mBundle.putString("classid", classid);
					mBundle.putString("bookid", bookid);

					itent.putExtras(mBundle);
					startActivity(itent);

					classAdapter.notifyDataSetChanged();

				} catch (Exception e) {
					Toast.makeText(CourseListActivity.this, e.toString(), 0).show();
					progressDialog.dismiss();
					e.printStackTrace();
				}

			}

			// 如果正在下载的过程中有点击时间 就暂停当前下载并且 如果等待序列中存在就开启等待序列的下载
			else if (classManager.getDownloadstate() == Constants.EMODOU_CLASS_DOWNLOAD_STATE_DOWNLOADING) {
				classManager.setDownloadstate(Constants.EMODOU_CLASS_DOWNLOAD_STATE_PAUSE);
				if (myhandlerMap.get(classid) != null) {
					myhandlerMap.get(classid).cancel();//删除运行时变量map里面的相应handler目的是再打开的时候可以创建新的变量，断点续传。不这样容易引起多线程异常
				}

				EmodouClassManager classManager2;
				try {
					dbUtils.update(classManager);
					classManager2 = dbUtils.findFirst(Selector
								.from(EmodouClassManager.class)
								.where("bookid", "=", bookid)
								.and("downloadstate", "=",Constants.EMODOU_CLASS_DOWNLOAD_STATE_WAIT)
								.and("type", "=", type)
								.and("userid", "=", userid));

					if (classManager2 != null) {
						classManager2.setDownloadstate(Constants.EMODOU_CLASS_DOWNLOAD_STATE_DOWNLOADING);
						dbUtils.update(classManager2);
					}
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				classAdapter.notifyDataSetChanged();
				//return true;

			}
			// 如果暂停的过程中有点击事件首先查找是否有正在下载的 如果有就加入下载队列 如果没有 开始下载
			else if (classManager.getDownloadstate() == Constants.EMODOU_CLASS_DOWNLOAD_STATE_PAUSE) {
				if (myhandlerMap.get(classid) != null) {
					myhandlerMap.remove(classid);
				}

				EmodouClassManager classManager2;
				try {

					classManager2 = dbUtils.findFirst(Selector
									.from(EmodouClassManager.class)
									.where("bookid", "=", bookid)
									.and("downloadstate","=",Constants.EMODOU_CLASS_DOWNLOAD_STATE_DOWNLOADING)
									.and("type", "=", type)
									.and("userid", "=", userid));

					if (classManager2 != null) {
						classManager.setDownloadstate(Constants.EMODOU_CLASS_DOWNLOAD_STATE_WAIT);
						dbUtils.update(classManager);
					} else {
						classManager.setDownloadstate(Constants.EMODOU_CLASS_DOWNLOAD_STATE_DOWNLOADING);
						dbUtils.update(classManager);
					}
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				classAdapter.notifyDataSetChanged();
				//return true;
			}

			else if (!ValidateUtils.isNetworkConnected(CourseListActivity.this)) {

				Toast.makeText(CourseListActivity.this, R.string.prompt_network, 1).show();
				//return true;

			} else if (!ValidateUtils.isWifiConnected(CourseListActivity.this)) {

				new AlertDialog.Builder(CourseListActivity.this)
						.setTitle(R.string.prompt)
						// 设置标题
						.setMessage(R.string.prompt_nonwifi)
						// 设置提示消息
						.setPositiveButton("取消",
								new DialogInterface.OnClickListener() {// 设置取消的按键
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								})
						.setNegativeButton("继续",
								new DialogInterface.OnClickListener() {
							
								// 设置继续按键
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										try {
											//内部类需要从新获取一下内容
											
											EmodouClassManager classManager = dbUtils.findFirst(Selector
																			.from(EmodouClassManager.class)
																			.where("bookid", "=", bookid)
																			.and("classid", "=", classid)
																			.and("type", "=", type)
																			.and("userid", "=", userid));
											
											
											//首先查看一下数据库里是否有正在下载的内容如果有就将词条设为等待 如果没有就将此项的管理类设置为正在下载

											EmodouClassManager classManagerentity = dbUtils
																				.findFirst(Selector.from(EmodouClassManager.class)
																				.where("bookid", "=", bookid)
																				.and("type", "=", type)
																				.and("userid", "=", userid)
																				.and("downloadstate", "=", Constants.EMODOU_CLASS_DOWNLOAD_STATE_DOWNLOADING));

											if(classManagerentity != null){
												classManager.setDownloadstate(Constants.EMODOU_CLASS_DOWNLOAD_STATE_WAIT);
											}else{
												classManager.setDownloadstate(Constants.EMODOU_CLASS_DOWNLOAD_STATE_DOWNLOADING);
											}
												dbUtils.update(classManager);

										} catch (Exception e) {
											e.printStackTrace();
										}
										classAdapter.notifyDataSetChanged();
									}
								})

						.setCancelable(false)// 设置按返回键是否响应返回，这是不响应
						.show();// 显示

				//return true;

			}
			
			//如果网络正常就按正常步骤下载
			else {
				try {
					//首先查看一下数据库里是否有正在下载的内容如果有就将词条设为等待 如果没有就将此项的管理类设置为正在下载

					EmodouClassManager classManagerentity = dbUtils
														.findFirst(Selector.from(EmodouClassManager.class)
														.where("bookid", "=", bookid)
														.and("type", "=", type)
														.and("userid", "=", userid)
														.and("downloadstate", "=", Constants.EMODOU_CLASS_DOWNLOAD_STATE_DOWNLOADING));

					if(classManagerentity != null){
						classManager.setDownloadstate(Constants.EMODOU_CLASS_DOWNLOAD_STATE_WAIT);
					}else{
						classManager.setDownloadstate(Constants.EMODOU_CLASS_DOWNLOAD_STATE_DOWNLOADING);
					}
						dbUtils.update(classManager);

				} catch (Exception e) {

				}
				classAdapter.notifyDataSetChanged();
			}

			//return false;
		}



		
			
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		//返回首页
		case R.id.courselist_imbtn_return:
//			Intent intent = new Intent(this, MainActivity.class);
//
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);	
			//退出时，要把本书课程列表中的所有学习中或者学习完的课程状态上传
			Intent intentService = new Intent(this, RecordIntentService.class);     
			intentService.putExtra("uploadtype", "class");
			intentService.putExtra("userid", userid);
			intentService.putExtra("bookid", bookid);
			startService(intentService);
			onBackPressed();
			break;

			//根据type来判断标题栏文字
		case R.id.courselist_imbtn_delete:
			String ttString = "";
			if (type.equals(Constants.EMODOU_TYPE_LISTEN)) {
				ttString ="听力";
			} else if (type.equals(Constants.EMODOU_TYPE_READ)) {
				ttString ="阅读";
			} else if (type.equals(Constants.EMODOU_TYPE_SPEAK)) {
				ttString ="口语";
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(CourseListActivity.this)
			.setTitle(R.string.prompt)
			.setMessage("您要删除本册已下载的" + ttString + "内容么")
			.setCancelable(false)
			.setNegativeButton("取消",null)
			.setPositiveButton("继续",new DialogInterface.OnClickListener() {  
		           public void onClick(DialogInterface dialog, int whichButton) {    
		        	   
		        	   Delete();
		           }

									            
		       }); 
			
		
			builder.create().show();
			break;
		}
	}
	
		
		private void Delete() {
			// TODO Auto-generated method stub
			ValidateUtils.deleteDirectory(Constants.LOCAL_START+type);
			DbUtils db = DbUtils.create(this);
			try {
				List<EmodouClassManager> classManagers = db.findAll(Selector.from(EmodouClassManager.class).where("bookid", "=", bookid).and("type", "=", type));
				for(int i=0; i< classManagers.size(); i ++){
					classManagers.get(i).setState(Constants.EMODOU_CLASS_STATE_NOT_LEAREN);
					classManagers.get(i).setDownloadstate(Constants.EMODOU_CLASS_DOWNLOAD_STATE_NOT_DOWNLOAD);
					classManagers.get(i).setProgress(0);
					db.update(classManagers.get(i));
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			Intent intent = new Intent(this,MainActivity.class);
			startActivity(intent);
		}
	
}
