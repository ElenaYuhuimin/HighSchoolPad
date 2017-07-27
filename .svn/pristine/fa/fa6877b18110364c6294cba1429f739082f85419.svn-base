package com.example.emodou;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouClassRoom;
import com.emodou.home.CourseListActivity.classItemClickListener;
import com.emodou.myclass.HomeworkFragment;
import com.emodou.myclass.MessageFragment;
import com.emodou.util.Constants;
import com.emodou.util.LogUtil;
import com.emodou.util.ValidateUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.R.integer;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyclassActivity extends Activity implements OnClickListener{
	
	private TextView titleTv, emptyTv;
	private ImageView outImv, inImv;
	private RelativeLayout homeworkRl, msgRl, tvBarRl;
	private FrameLayout frameLayout;
	private static PopupWindow popupWindow;
	private ListView popupLv;
	
	private List<EmodouClassRoom> classRoomList = new ArrayList<EmodouClassRoom>();	
	private String userid, ticket, classroomid;
	private static int totalHeight = 0 ;
	private popupListAdaper popupAdaper;
	private FragmentManager fm = getFragmentManager();
	private HomeworkFragment homeworkFrag;
	private MessageFragment messageFrag;
	private static int ifFirsttime = 0;//判断是否是第一次初始化Activity，防止有两次refreshHttp，两次loading
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myclass_major);
		
		init();
	}
	
	public void init() {
		// TODO Auto-generated method stub
		titleTv = (TextView)findViewById(R.id.myclass_major_titleTv);
		outImv = (ImageView)findViewById(R.id.myclass_major_outImv);
		inImv = (ImageView)findViewById(R.id.myclass_major_inImv);
		homeworkRl = (RelativeLayout)findViewById(R.id.myclass_major_homeworkRl);
		homeworkRl.setOnClickListener(this);
		msgRl = (RelativeLayout)findViewById(R.id.myclass_major_messageRl);
		msgRl.setOnClickListener(this);
		tvBarRl = (RelativeLayout)findViewById(R.id.myclass_major_tvbar);
		tvBarRl.setOnClickListener(this);
		emptyTv = (TextView)findViewById(R.id.myclass_major_empty);
		frameLayout = (FrameLayout)findViewById(R.id.myclass_major_framelayout);
		
		userid = ValidateUtils.getUserid(this);
		ticket = ValidateUtils.getTicket(this);
		
		//获取当前用户有哪些班级，只要从数据库中查找即可
		//因为目前设置的是只要用户点击了个人中的我的班级，本地数据库和服务器上的就是一致的
		//如果是从注册过来的，若添加二维码，则数据库中已有记录，若什么班级都没有加，则班级为空，请求也没有
		//然后去个人添加班级，数据库会同步
		//以后每次登陆，只有一种情况是不同步的，那就是在其他应用上删除了某个课程，然后没有点个人，直接点的班级
		//有两种解决方案，一是得到蒋燊的错误返回信息码，然后去请求服务器，二是用通知的方式，在登陆时服务去请求服务器，刷新本地数据库
		
		refreshClassList();
		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(ifFirsttime == 0){
			getListFromLocal();
			
			//在列表中选出状态为选中的班级
			if(classRoomList != null && classRoomList.size() != 0){
				for(EmodouClassRoom classRoom : classRoomList){
					if(classRoom.getState().equals(Constants.CLASSROOM_STATE_CHOICE)){
						titleTv.setText(classRoom.getClassroomname());
						classroomid = classRoom.getClassroomid();
						setDefaultFragment();
					}
				}
			}
		}
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		ifFirsttime = 0;
		super.onPause();
	}
	
	private void setDefaultFragment(){
		ifFirsttime =1;
		homeworkRl.setBackgroundColor(getResources().getColor(R.color.white));
		msgRl.setBackgroundColor(getResources().getColor(R.color.set_fragment_bcg));
		FragmentTransaction transaction = fm.beginTransaction();
		if(homeworkFrag != null){
			homeworkFrag = null;
		}
		homeworkFrag = new HomeworkFragment();
		transaction.replace(R.id.myclass_major_framelayout, homeworkFrag);
		homeworkFrag.passData(userid, classroomid, ticket, this);
		homeworkRl.setClickable(true);
		transaction.commit();
	}
	
	public void getListFromLocal() {
		// TODO Auto-generated method stub
		classRoomList.clear();
		DbUtils dbUtils = DbUtils.create(this);
		try {
			List<EmodouClassRoom> classRooms = new ArrayList<EmodouClassRoom>();
			classRooms = dbUtils.findAll(Selector.from(EmodouClassRoom.class)
					                             .where("userid","=",userid)
					                             .and("classroomtype","=","g"));
			if(classRooms != null)
				classRoomList.addAll(classRooms);
			classRooms = dbUtils.findAll(Selector.from(EmodouClassRoom.class)
                    							 .where("userid","=",userid)
                    							 .and("classroomtype","=","c"));
			if(classRooms != null)
				classRoomList.addAll(classRooms);
			if(classRoomList == null){
				emptyTv.setVisibility(View.VISIBLE);
				frameLayout.setVisibility(View.GONE);	
			}else{
				emptyTv.setVisibility(View.GONE);
				frameLayout.setVisibility(View.VISIBLE);
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		FragmentTransaction transaction = fm.beginTransaction();
		
		switch (v.getId()) {
			case R.id.myclass_major_tvbar:
				getPopupWindow();
				//popupWindow.showAtLocation(v, Gravity.TOP, 0, 0);
				break;
				
			case R.id.myclass_major_messageRl:
				homeworkRl.setBackgroundColor(getResources().getColor(R.color.set_fragment_bcg));
				msgRl.setBackgroundColor(getResources().getColor(R.color.white));
				if(messageFrag == null){
					messageFrag = new MessageFragment();
				}
				transaction.replace(R.id.myclass_major_framelayout, messageFrag);
				break;
				
			case R.id.myclass_major_homeworkRl:
				homeworkRl.setBackgroundColor(getResources().getColor(R.color.white));
				msgRl.setBackgroundColor(getResources().getColor(R.color.set_fragment_bcg));
				if(homeworkFrag == null){
					homeworkFrag = new HomeworkFragment();
				}
				homeworkFrag.passData(userid, classroomid, ticket, this);
				transaction.replace(R.id.myclass_major_framelayout, homeworkFrag);
				break;
	
			default:
				break;
		}
		
		transaction.commit();
	}
	
	public void getPopupWindow() {
		if(null != popupWindow){
			popupWindow.dismiss();
			outImv.setVisibility(View.VISIBLE);
			inImv.setVisibility(View.GONE);
			return;
		}else if(classRoomList.size()!=0&& classRoomList != null){
			
			// 获取自定义布局文件activity_popupwindow_left.xml的视图  
	        View popupWindow_view = getLayoutInflater().inflate(R.layout.myclass_popup, null,  
	                false);  
	        popupLv = (ListView)popupWindow_view.findViewById(R.id.myclass_popup_listview);
	        popupAdaper = new popupListAdaper(classRoomList, this);
	        popupLv.setAdapter(popupAdaper);
	        setListViewHeightBasedOnChildren(popupLv);
	        popupLv.setOnItemClickListener(new popupItemClickListener(classRoomList));
	        WindowManager wm = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
	        
//	        int width = wm.getDefaultDisplay().getWidth() ;
	        int width = getWindow().getDecorView().getWidth();
	        // 创建PopupWindow实例,第二个和第三个参数分别是宽度和高度  
	        popupWindow = new PopupWindow(popupWindow_view, width, totalHeight, true); 
	        popupWindow.setFocusable(true);
	        popupWindow.setOutsideTouchable(true);
//	        int xpos=wm.getDefaultDisplay().getWidth()-popupWindow.getWidth();
//	        popupWindow.showAsDropDown(tvBarRl,xpos, 0);
//	        popupWindow.showAtLocation(tvBarRl, Gravity.RIGHT | Gravity.TOP, -100,60);
//	        int[] location = new int[2];  
//	        getWindow().getDecorView().getLocationOnScreen(location);// 获得指定控件的坐标  
//	        LogUtil.d("x", tvBarRl.getLeft()+"");
//	        LogUtil.d("y", tvBarRl.getBottom()+"");
//	        int xPos = -popupWindow.getWidth() / 2  + this.tvBarRl.getce.getWidth() / 2;  
	        popupWindow.showAsDropDown(tvBarRl, tvBarRl.getLeft(),0);//只要设置好控件xy就可以了。
//	        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.RIGHT, tvBarRl.getBottom(), tvBarRl.getBottom());
//	        AnimationSet animation = new AnimationSet(false);
//	        AlphaAnimation alphaAnimation = new Al
//	        // 设置动画效果  
//	        popupWindow.setAnimationStyle(R.style.animation_myclass_popup);  
	        outImv.setVisibility(View.GONE);
			inImv.setVisibility(View.VISIBLE);
	        // 点击其他地方消失  
	        popupWindow_view.setOnTouchListener(new OnTouchListener() {  
	            @Override  
	            public boolean onTouch(View v, MotionEvent event) {  
	                // TODO Auto-generated method stub  
	                if (popupWindow != null && popupWindow.isShowing()) {  
	                    popupWindow.dismiss();  
	                    outImv.setVisibility(View.VISIBLE);
	    				inImv.setVisibility(View.GONE);
	                    popupWindow = null;  
	                }  
	                return false;  
	            }

	        }); 
		}
	}
	
	/** 
     * 计算popupWindow的高度 
     *  
     * @param listView 
     */  
    public void setListViewHeightBasedOnChildren(ListView listView) {  
        // 获取ListView对应的Adapter  
    	ListAdapter listAdapter = listView.getAdapter();  
        if (listAdapter == null) {  
            totalHeight = 0; 
        }  
  
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0); // 计算子项View 的宽高  
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度  
        }  
        
        // listView.getDividerHeight()获取子项间分隔符占用的高度 
        totalHeight += (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
    }  
    
    private static class popupViewHolder{
    	TextView crNameTv;
    	ImageView choiceImv;
    }
    
    private class popupListAdaper extends BaseAdapter{
    	
    	private List<EmodouClassRoom> myclassRoomList = new ArrayList<EmodouClassRoom>();
    	private LayoutInflater inflater;
    	
    	public popupListAdaper(List<EmodouClassRoom> classRoomList, Context context){
			this.myclassRoomList = classRoomList;
			inflater = LayoutInflater.from(context);
		}
    	
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return myclassRoomList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return myclassRoomList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			popupViewHolder holder;
			if(convertView == null){
				view = inflater.inflate(R.layout.myclass_popup_item, parent, false);
				holder = new popupViewHolder();
				holder.crNameTv = (TextView)view.findViewById(R.id.myclass_popup_item_crName);
				holder.choiceImv = (ImageView)view.findViewById(R.id.myclass_popup_item_choiceImv);
				view.setTag(holder);
			}else{
				holder = (popupViewHolder)view.getTag();
			}
			
			holder.crNameTv.setText(myclassRoomList.get(position).getClassroomname());
			if(myclassRoomList.get(position).getState().equals(Constants.CLASSROOM_STATE_CHOICE))
				holder.choiceImv.setVisibility(View.VISIBLE); 
			else 
				holder.choiceImv.setVisibility(View.GONE);
			return view;
		}
    	
    }
    
    public class popupItemClickListener implements OnItemClickListener{
    	
    	private List<EmodouClassRoom> classRoomList = new ArrayList<EmodouClassRoom>();
    	public popupItemClickListener(List<EmodouClassRoom> classRoomList){
    		this.classRoomList = classRoomList;
    	}

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			if(!classRoomList.get(position).getState().equals(Constants.CLASSROOM_STATE_CHOICE)){
				//先从数据库中将选中的改为未选中
				DbUtils dbUtils = DbUtils.create(MyclassActivity.this);
				String crId = "";
				try {
					EmodouClassRoom classRoomChoice = dbUtils.findFirst(Selector.from(EmodouClassRoom.class)
							                                                    .where("userid","=",userid)
							                                                    .and("state","=",Constants.CLASSROOM_STATE_CHOICE));
					if(classRoomChoice != null){
						classRoomChoice.setState(Constants.CLASSROOM_STATE_UNCHOICE);
						dbUtils.update(classRoomChoice);
					}
					
					crId = classRoomList.get(position).getClassroomid();
					//再将当前的由未选中改为选中
					EmodouClassRoom classRoomNow = dbUtils.findFirst(Selector.from(EmodouClassRoom.class)
							                                                 .where("userid","=",userid)
							                                                 .and("classroomid","=",crId));
					if(classRoomNow != null){
						classRoomNow.setState(Constants.CLASSROOM_STATE_CHOICE);
						dbUtils.update(classRoomNow);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
				//选中后缩回popup，并且刷新界面framlayout，刷新作业列表界面
				popupWindow.dismiss();
				popupWindow = null;
				titleTv.setText(classRoomList.get(position).getClassroomname());
				outImv.setVisibility(View.VISIBLE);
				inImv.setVisibility(View.GONE);
				
				//同时要更新list,因为选中状态变了
				getListFromLocal();
				
				
				//设置默认fragment
				classroomid = crId;
				setDefaultFragment();
				
				return;
			}
		}
    	
    }
    
    public void refreshClassList() {
		//这句话不能在onsuccess中执行，否则还是先执行onresume，有两次loading，因为是异步的。但是这样的话，就需要在失败的时候也从本地获取
		ifFirsttime = 1;
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid",userid);
		params.addBodyParameter("ticket",ticket);
		params.addBodyParameter("role","s");
		
		String path = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_GET_CLASSROOMLIST;
		
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, path, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				try{									
					String res = responseInfo.result.toString();
					int index = res.indexOf("{");
					if(index == -1){
						Toast.makeText(MyclassActivity.this, R.string.person_myclass_updateFailed, 0).show();					
					}else{
						
						//从index位开始截取
						res = res.substring(index);
						
						//res已经是string类型
						JSONObject result = new JSONObject(res);
						String status = (String)result.get("Status");
						
						if(status.equals("Success")){
							
							DbUtils dbUtils = DbUtils.create(MyclassActivity.this);
							try {
								JSONObject classroomObject = result.getJSONObject("Classroom");			
								
								//为了解决如果在其他应用上删除了退出了某个课程，本地不更新，永远无法删除的问题
								List<EmodouClassRoom>  classRoomList = new ArrayList<EmodouClassRoom>();
								classRoomList = dbUtils.findAll(Selector.from(EmodouClassRoom.class)
										                                .where("userid","=",userid));
								
								JSONArray classroomCArray = classroomObject.getJSONArray("classroom_c");
								for(int i = 0; i<classroomCArray.length() ; i++){
									JSONObject classroomCObject = classroomCArray.getJSONObject(i);
									String classroomid = classroomCObject.getString("classroomid");
									String schoolname = classroomCObject.getString("schoolname");
									String classroomname = classroomCObject.getString("classroomname");
									String classroomgrade = classroomCObject.getString("classroomgrade");
									String classroomicon = classroomCObject.getString("classroomicon");
									String classroompac = classroomCObject.getString("classroompackage");
									
									EmodouClassRoom classRoom = new EmodouClassRoom();
									classRoom = dbUtils.findFirst(Selector.from(EmodouClassRoom.class)
																		  .where("classroomid","=",classroomid)
																		  .and("userid","=",userid));
									//下面的操作都是进行输入的															
									if(classRoom != null){
										classRoom.setSchoolname(schoolname);
										classRoom.setClassroomname(classroomname);
										classRoom.setClassroomgrade(classroomgrade);
										classRoom.setClassroomicon(classroomicon);
										classRoom.setClassroompackage(classroompac);
										classRoom.setClassroomtype(Constants.CLASSROOM_TYPE_C);
										classRoom.setUserid(userid);
										dbUtils.update(classRoom);
										
										//如果在本地找到了，则classRoomList中也有，将classroomid设为haved
										//幸好每个人的课程不是很多，否则每次都要遍历一遍，那就是n的平方级别的
										for(EmodouClassRoom classRoom2 : classRoomList){
											if(classRoom2.getClassroomid().equals(classroomid)){
												classRoom2.setClassroomid("haved");
											}
										}
										
										
									}else {
										//这个是新的，说明本地也没有，就不用循环了
										EmodouClassRoom classRoomNew = new EmodouClassRoom();
										classRoomNew.setClassroomid(classroomid);
										classRoomNew.setSchoolname(schoolname);
										classRoomNew.setClassroomname(classroomname);
										classRoomNew.setClassroomgrade(classroomgrade);
										classRoomNew.setClassroomicon(classroomicon);
										classRoomNew.setClassroompackage(classroompac);
										classRoomNew.setClassroomtype(Constants.CLASSROOM_TYPE_C);
										classRoomNew.setUserid(userid);
										//如果是空，要记得更新选中状态
										classRoomNew.setState(Constants.CLASSROOM_STATE_UNCHOICE);
										dbUtils.saveBindingId(classRoomNew);
									}
									
								}
								
								
								JSONArray classroomGArray = classroomObject.getJSONArray("classroom_g");
								for(int i = 0; i<classroomGArray.length() ; i++){
									JSONObject classroomGObject = classroomGArray.getJSONObject(i);
									String classroomid = classroomGObject.getString("classroomid");
									String schoolname = classroomGObject.getString("schoolname");
									String classroomname = classroomGObject.getString("classroomname");
									String teachername = classroomGObject.getString("teachername");
									String classroomgrade = classroomGObject.getString("classroomgrade");
									String snumber = classroomGObject.getString("snumber");
									String classroomicon = classroomGObject.getString("classroomicon");
									String classroompac = classroomGObject.getString("classroompackage");
									
									EmodouClassRoom classRoom = new EmodouClassRoom();
									classRoom = dbUtils.findFirst(Selector.from(EmodouClassRoom.class)
																		  .where("classroomid","=",classroomid)
																		  .and("userid","=",userid));
									if(classRoom != null){
										classRoom.setSchoolname(schoolname);
										classRoom.setClassroomname(classroomname);
										classRoom.setTeachername(teachername);
										classRoom.setClassroomgrade(classroomgrade);
										classRoom.setSnumber(snumber);
										classRoom.setClassroomicon(classroomicon);
										classRoom.setClassroompackage(classroompac);
										classRoom.setClassroomtype(Constants.CLASSROOM_TYPE_G);
										classRoom.setUserid(userid);
										dbUtils.update(classRoom);
										
										//如果在本地找到了，则classRoomList中也有，将classroomid设为haved
										//幸好每个人的课程不是很多，否则每次都要遍历一遍，那就是n的平方级别的
										for(EmodouClassRoom classRoom2 : classRoomList){
											if(classRoom2.getClassroomid().equals(classroomid)){
												classRoom2.setClassroomid("haved");
											}
										}
									}else {
										EmodouClassRoom classRoomNew = new EmodouClassRoom();
										classRoomNew.setClassroomid(classroomid);
										classRoomNew.setSchoolname(schoolname);
										classRoomNew.setClassroomname(classroomname);
										classRoomNew.setTeachername(teachername);
										classRoomNew.setClassroomgrade(classroomgrade);
										classRoomNew.setSnumber(snumber);
										classRoomNew.setClassroomicon(classroomicon);
										classRoomNew.setClassroompackage(classroompac);
										classRoomNew.setClassroomtype(Constants.CLASSROOM_TYPE_G);
										classRoomNew.setUserid(userid);
										//如果是空，要记得更新选中状态
										classRoomNew.setState(Constants.CLASSROOM_STATE_UNCHOICE);
										dbUtils.saveBindingId(classRoomNew);
										
									}
								}	
									
								JSONArray classroomAArray = classroomObject.getJSONArray("classroom_a");
								for(int i = 0; i<classroomAArray.length() ; i++){
									JSONObject classroomAObject = classroomAArray.getJSONObject(i);
									String classroomid = classroomAObject.getString("classroomid");
									String schoolname = classroomAObject.getString("schoolname");
									String classroomname = classroomAObject.getString("classroomname");
									String classroomgrade = classroomAObject.getString("classroomgrade");
									String classroomicon = classroomAObject.getString("classroomicon");
									String classroompac = classroomAObject.getString("classroompackage");
									
									EmodouClassRoom classRoom = new EmodouClassRoom();
									classRoom = dbUtils.findFirst(Selector.from(EmodouClassRoom.class)
																		  .where("classroomid","=",classroomid)
																		  .and("userid","=",userid));
									if(classRoom != null){
										classRoom.setSchoolname(schoolname);
										classRoom.setClassroomname(classroomname);
										classRoom.setClassroomgrade(classroomgrade);
										classRoom.setClassroomicon(classroomicon);
										classRoom.setClassroompackage(classroompac);
										classRoom.setClassroomtype(Constants.CLASSROOM_TYPE_A);
										classRoom.setUserid(userid);
										dbUtils.update(classRoom);
										
										
										//如果在本地找到了，则classRoomList中也有，将classroomid设为haved
										//幸好每个人的课程不是很多，否则每次都要遍历一遍，那就是n的平方级别的
										for(EmodouClassRoom classRoom2 : classRoomList){
											if(classRoom2.getClassroomid().equals(classroomid)){
												classRoom2.setClassroomid("haved");
											}
										}
									}else {
										EmodouClassRoom classRoomNew = new EmodouClassRoom();
										classRoomNew.setClassroomid(classroomid);
										classRoomNew.setSchoolname(schoolname);
										classRoomNew.setClassroomname(classroomname);
										classRoomNew.setClassroomgrade(classroomgrade);
										classRoomNew.setClassroomicon(classroomicon);
										classRoomNew.setClassroompackage(classroompac);
										classRoomNew.setClassroomtype(Constants.CLASSROOM_TYPE_A);
										classRoomNew.setUserid(userid);
										//如果是空，要记得更新选中状态
										classRoomNew.setState(Constants.CLASSROOM_STATE_UNCHOICE);
										dbUtils.saveBindingId(classRoomNew);
										
									}
								}
								
								//所有的都结束后，遍历List，看哪个没有haved，就删掉
								if(classRoomList != null && classRoomList.size() != 0){
									for(EmodouClassRoom classRoom : classRoomList){
										if(!classRoom.getClassroomid().equals("haved")){
											String classroomid = classRoom.getClassroomid();
											EmodouClassRoom classRoom2 = new EmodouClassRoom();
											classRoom2 = dbUtils.findFirst(Selector.from(EmodouClassRoom.class)
																				   .where("classroomid","=",classroomid)
																				   .and("userid","=",userid));
											dbUtils.delete(classRoom2);
										}		
									}
								}
								
							} catch (DbException e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							
							//因为是异步的操作，所以如果只在onresume中操作的话，第一次进入不会刷出来
							getListFromLocal();
							
							//在列表中选出状态为选中的班级
							if(classRoomList != null && classRoomList.size() != 0){
								for(EmodouClassRoom classRoom : classRoomList){
									if(classRoom.getState().equals(Constants.CLASSROOM_STATE_CHOICE)){
										titleTv.setText(classRoom.getClassroomname());
										classroomid = classRoom.getClassroomid();
										setDefaultFragment();
									}
								}
							}

							Toast.makeText(MyclassActivity.this, R.string.person_myclass_detail_updateSun, 0).show();
						}else{
							Toast.makeText(MyclassActivity.this, R.string.person_myclass_updateFailed, 0).show();
							//两次失败的状态时，列表也要从本地获取
							getListFromLocal();
							
							//在列表中选出状态为选中的班级
							if(classRoomList != null && classRoomList.size() != 0){
								for(EmodouClassRoom classRoom : classRoomList){
									if(classRoom.getState().equals(Constants.CLASSROOM_STATE_CHOICE)){
										titleTv.setText(classRoom.getClassroomname());
										classroomid = classRoom.getClassroomid();
										setDefaultFragment();
									}
								}
							}
						}
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				Toast.makeText(MyclassActivity.this, R.string.person_myclass_updateFailed, 0).show();
				//两次失败的状态时，列表也要从本地获取
				getListFromLocal();
				
				//在列表中选出状态为选中的班级
				if(classRoomList != null && classRoomList.size() != 0){
					for(EmodouClassRoom classRoom : classRoomList){
						if(classRoom.getState().equals(Constants.CLASSROOM_STATE_CHOICE)){
							titleTv.setText(classRoom.getClassroomname());
							classroomid = classRoom.getClassroomid();
							setDefaultFragment();
						}
					}
				}
			}
		});
		
		
		
	}
    
   

}
