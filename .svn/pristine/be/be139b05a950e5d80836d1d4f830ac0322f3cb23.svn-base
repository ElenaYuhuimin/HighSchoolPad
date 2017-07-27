package com.emodou.person;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouClass;
import com.emodou.domain.EmodouClassRoom;
import com.emodou.extend.CircleImageView;
import com.emodou.extend.MipcaActivityCapture;
import com.emodou.login.RegistClassActivity;
import com.emodou.util.Constants;
import com.emodou.util.LogUtil;
import com.emodou.util.ValidateUtils;
import com.example.emodou.PersonActivity;
import com.example.emodou.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyclassFragment extends Fragment implements OnClickListener{
	
	private View myclassView;
	
	private EditText codeEdit;
	private ImageView scanImv;
	private ListView classhavedLv, classAppLv;
	private RelativeLayout nextRl;
	private Button nextBtn;
	private TextView havedTitleTv, appTitleTv;
	
	private final static int SCANNIN_GREQUEST_CODE = 2;
	private String userid, codeStr;
	private List<EmodouClassRoom> havedList, appList;
	private CRListAdapter havedListAdapter, appListAdapter;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		myclassView = inflater.inflate(R.layout.person_myclass, container, false);
		
		init();
		return myclassView;
	}

	public void init() {
		// TODO Auto-generated method stub
		codeEdit = (EditText)myclassView.findViewById(R.id.person_myclass_joinclass_codeEdt);
		scanImv = (ImageView)myclassView.findViewById(R.id.person_myclass_joinclass_scanimv);
		classhavedLv = (ListView)myclassView.findViewById(R.id.person_myclass_classhaved_list);
		classAppLv = (ListView)myclassView.findViewById(R.id.person_myclass_classApp_list);
		havedTitleTv = (TextView)myclassView.findViewById(R.id.person_myclass_classhaved_title);
		appTitleTv = (TextView)myclassView.findViewById(R.id.person_myclass_classApp_title);
		
		havedList = new ArrayList<EmodouClassRoom>();
		appList = new ArrayList<EmodouClassRoom>();
		
		nextRl = (RelativeLayout)myclassView.findViewById(R.id.person_myclass_nextRl);
		nextBtn = (Button)myclassView.findViewById(R.id.person_myclass_nextBtn);
		nextBtn.setOnClickListener(this);
		
		//点击按钮跳转到二维码扫描界面，这里用的是startActivityForResult跳转
		//扫描完了之后调到该界面
		scanImv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				getActivity().startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});	

		imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity().getBaseContext()));
		//设置加载图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.home_changepackage_item_icondefault)
				.showImageForEmptyUri(R.drawable.home_changepackage_item_icondefault)
				.showImageOnFail(R.drawable.home_changepackage_item_icondefault)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.build();
		
		refreshClassList();
		
		codeEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
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
				if(!TextUtils.isEmpty(s.toString())){
					nextRl.setVisibility(View.VISIBLE);
					codeStr = s.toString();
				}else 
					nextRl.setVisibility(View.GONE);
			}
		});
		
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if(resultCode == getActivity().RESULT_OK){
				Bundle bundle = data.getExtras();
				//显示扫描到的内容
				String result = bundle.getString("result");
				String[] resArray = result.split("#&#");
				codeStr = resArray[2];
				codeEdit.setText(resArray[2]);
				
			}
			break;
		}
    }	
	public void passUserid(String userid) {
		this.userid = userid;
	}
	public void refreshClassList() {
		
		havedList.clear();
		appList.clear();
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid",userid);
		params.addBodyParameter("ticket",ValidateUtils.getTicket(getActivity()));
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
						refreshFromLocal();							
					}else{
						
						//从index位开始截取
						res = res.substring(index);
						
						//res已经是string类型
						JSONObject result = new JSONObject(res);
						String status = (String)result.get("Status");
						
						if(status.equals("Success")){
							
							DbUtils dbUtils = DbUtils.create(getActivity());
							try {
								JSONObject classroomObject = result.getJSONObject("Classroom");	
								
								
								//为了解决如果在其他应用上删除了退出了某个课程，本地不更新，永远无法删除的问题
								List<EmodouClassRoom>  classRoomList = new ArrayList<EmodouClassRoom>();
								classRoomList = dbUtils.findAll(Selector.from(EmodouClassRoom.class)
										                                .where("userid","=",userid));
								
								JSONArray classroomCArray = classroomObject.getJSONArray("classroom_c");
								for(int i = 0; i<classroomCArray.length() ; i++){
									JSONObject classroomCObject = classroomCArray.getJSONObject(i);
									Log.d("classroom_c",classroomCObject.toString());
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
										
										havedList.add(classRoom);
										
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
										
										havedList.add(classRoomNew);
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
										
										havedList.add(classRoom);
										
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
										
										havedList.add(classRoomNew);
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
										
										appList.add(classRoom);
										
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
										
										appList.add(classRoomNew);
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
							
							havedListAdapter = new CRListAdapter(havedList, options, getActivity());
							classhavedLv.setAdapter(havedListAdapter);
							setListViewHeightBasedOnChildren(classhavedLv);
							classhavedLv.setOnItemClickListener(new CRItemClickListener(havedList));
							appListAdapter = new CRListAdapter(appList, options, getActivity());
							classAppLv.setAdapter(appListAdapter);
							setListViewHeightBasedOnChildren(classAppLv);
							classAppLv.setOnItemClickListener(new CRItemClickListener(appList));
							
							if(havedList.size() == 0 || havedList == null){
								havedTitleTv.setVisibility(View.GONE);
							}else{
								havedTitleTv.setVisibility(View.VISIBLE);
							}
							
							if(appList.size() == 0 || appList == null){
								appTitleTv.setVisibility(View.GONE);
							}else{
								appTitleTv.setVisibility(View.VISIBLE);
							}
							
							Toast.makeText(getActivity(), R.string.person_myclass_detail_updateSun, 0).show();
						}else{
							refreshFromLocal();
						}
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				refreshFromLocal();
			}
		});
		
		
		
	}
	
	public void refreshFromLocal() {
		havedList.clear();
		appList.clear();
		//首次从本地数据库取
		DbUtils dbUtils = DbUtils.create(getActivity());
		try {
			List<EmodouClassRoom> classRoomList = new ArrayList<EmodouClassRoom>();
			classRoomList = dbUtils.findAll(Selector.from(EmodouClassRoom.class)
													.where("userid","=",userid)
													.and("classroomtype","=",Constants.CLASSROOM_TYPE_C));
			if(classRoomList != null && classRoomList.size() != 0){
				havedList.addAll(classRoomList);
				classRoomList.clear();
			}	
			
			classRoomList = dbUtils.findAll(Selector.from(EmodouClassRoom.class)
													.where("userid","=",userid)
													.and("classroomtype","=",Constants.CLASSROOM_TYPE_G));
			if(classRoomList != null && classRoomList.size() != 0){
				havedList.addAll(classRoomList);
				classRoomList.clear();
			}
				
			classRoomList = dbUtils.findAll(Selector.from(EmodouClassRoom.class)
					                                .where("userid","=",userid)
					                                .and("classroomtype","=", Constants.CLASSROOM_TYPE_A));
			if(classRoomList != null && classRoomList.size() != 0){
				appList.addAll(classRoomList);
			}
			
//			if(!((havedList != null && havedList.size() != 0)||(appList != null && appList.size()!=0))){
//				//只要两者都为空
//				refreshClassList();
//			}else{
			havedListAdapter = new CRListAdapter(havedList, options, getActivity());
			classhavedLv.setAdapter(havedListAdapter);
			setListViewHeightBasedOnChildren(classhavedLv);
			classhavedLv.setOnItemClickListener(new CRItemClickListener(havedList));
			appListAdapter = new CRListAdapter(appList, options, getActivity());
			classAppLv.setAdapter(appListAdapter);
			setListViewHeightBasedOnChildren(classAppLv);
			classAppLv.setOnItemClickListener(new CRItemClickListener(appList));
			
			if(havedList.size() == 0 || havedList == null){
				havedTitleTv.setVisibility(View.GONE);
			}else{
				havedTitleTv.setVisibility(View.VISIBLE);
			}
			
			if(appList.size() == 0 || appList == null){
				appTitleTv.setVisibility(View.GONE);
			}else{
				appTitleTv.setVisibility(View.VISIBLE);
			}
//			}
		} catch (DbException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}
	
	private static class CRViewHolder{
		ImageView Image;
		TextView crnameTv, crgradeTv, schoolnameTv, teachernameTv, snumberTv;
		Button cancelBtn;
		
	}
	
	private class  CRListAdapter extends BaseAdapter{
		
		private List<EmodouClassRoom> myclassRoomList;
		private LayoutInflater inflater;
		private DisplayImageOptions myoptions;
		
		public CRListAdapter(List<EmodouClassRoom> classroomList, DisplayImageOptions options, Context context){
			super();
			this.myclassRoomList = classroomList;
			this.myoptions = options;
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			final CRViewHolder viewHolder;
			if(convertView == null){
				view = inflater.inflate(R.layout.person_myclass_classitem, parent, false);
				viewHolder = new CRViewHolder();
				
				viewHolder.Image = (ImageView)view.findViewById(R.id.person_myclass_classitem_icon);
				viewHolder.crnameTv = (TextView)view.findViewById(R.id.person_myclass_classitem_crname);
				viewHolder.crgradeTv = (TextView)view.findViewById(R.id.person_myclass_classitem_crgrade);
				viewHolder.schoolnameTv = (TextView)view.findViewById(R.id.person_myclass_classitem_schoolname);
				viewHolder.teachernameTv = (TextView)view.findViewById(R.id.person_myclass_classitem_teachername);
				viewHolder.snumberTv = (TextView)view.findViewById(R.id.person_myclass_classitem_snumber);
//				viewHolder.packnameTv = (TextView)view.findViewById(R.id.person_myclass_classitem_packname);
				viewHolder.cancelBtn = (Button)view.findViewById(R.id.person_myclass_cancelBtn);
				
				view.setTag(viewHolder);
			}else{
				viewHolder = (CRViewHolder)view.getTag();
			}
			
			String url = Constants.EMODOU_URL + "/" + myclassRoomList.get(position).getClassroomicon();
			imageLoader.displayImage(url, viewHolder.Image, myoptions);
			
			String crnameStr = myclassRoomList.get(position).getClassroomname();
			String crgradeStr = myclassRoomList.get(position).getClassroomgrade();
			String schoolnameStr = myclassRoomList.get(position).getSchoolname();
			String teachernameStr = myclassRoomList.get(position).getTeachername();
			String snumberStr = myclassRoomList.get(position).getSnumber();
			String packnameStr = myclassRoomList.get(position).getClassroompackage();
			
			viewHolder.crnameTv.setText(crnameStr);
			viewHolder.crgradeTv.setText("(" + crgradeStr + ")");
			viewHolder.schoolnameTv.setText(schoolnameStr);
			
//			if(!TextUtils.isEmpty(teachernameStr)){
//				viewHolder.teachernameTv.setVisibility(View.VISIBLE);
//				viewHolder.teachernameTv.setText("教师 : " + teachernameStr);
//			}else 
//				viewHolder.teachernameTv.setVisibility(View.GONE);
			
//			if(!TextUtils.isEmpty(snumberStr)){
//				viewHolder.snumberTv.setVisibility(View.VISIBLE);
//				viewHolder.snumberTv.setText("学生数: " + snumberStr);
//			}else  
//				viewHolder.snumberTv.setVisibility(View.GONE);
			
			
			
//			if(!TextUtils.isEmpty(pacTotalName)){
//				viewHolder.packnameTv.setVisibility(View.VISIBLE);
//				viewHolder.packnameTv.setText("学习包: " + pacTotalName);
//			}else  
//				viewHolder.packnameTv.setVisibility(View.GONE);
			
			if(myclassRoomList.get(position).getClassroomtype().equals("a")){
				viewHolder.cancelBtn.setVisibility(View.VISIBLE);
				viewHolder.cancelBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						String codeStr = myclassRoomList.get(position).getClassroomid();
						cancelClassRoom(codeStr);
					}
				});
			}else{
				//非申请中的班级，不显示撤销申请
				viewHolder.cancelBtn.setVisibility(View.GONE);
			}
			return view;
		}
		
	}
	
	public class CRItemClickListener implements OnItemClickListener{
		
		List<EmodouClassRoom> classroomList = new ArrayList<EmodouClassRoom>();
		public CRItemClickListener(List<EmodouClassRoom> crList){
			this.classroomList = crList;
		}
		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			if(classroomList.get(position).getClassroomtype().equals("a")){
				Toast.makeText(getActivity(), R.string.person_myclass_checkFail, 0).show();
			}else{
				PersonActivity personActivity = (PersonActivity)getActivity();
				personActivity.changeToDetailFrag(classroomList.get(position).getClassroomid());
			}
			
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.person_myclass_nextBtn:
				PersonActivity personActivity = (PersonActivity)getActivity();
				personActivity.nextStep(codeStr);
				break;
	
			default:
				break;
		}
	}
	
    /** 
     * 为了解决ListView在ScrollView中只能显示一行数据的问题 
     *  
     * @param listView 
     */  
    public static void setListViewHeightBasedOnChildren(ListView listView) {  
        // 获取ListView对应的Adapter  
    	ListAdapter listAdapter = listView.getAdapter();  
        if (listAdapter == null) {  
            return;  
        }  
  
        int totalHeight = 0;  
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0); // 计算子项View 的宽高  
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度  
        }  
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight  
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
        // listView.getDividerHeight()获取子项间分隔符占用的高度  
        // params.height最后得到整个ListView完整显示需要的高度  
        listView.setLayoutParams(params);  
    }  

	
    public void cancelClassRoom(final String crIdStr) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid",userid);
		params.addBodyParameter("classroomid",crIdStr);
		params.addBodyParameter("ticket",ValidateUtils.getTicket(getActivity()));
		
		String path = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_CANCEL_APPLY;
		
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, path, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				try{									
					String res = responseInfo.result.toString();
					int index = res.indexOf("{");
					if(index == -1){
						Toast.makeText(getActivity(), R.string.person_myclass_detail_cancelFail, 0).show();
					}else{
						
						//从index位开始截取
						res = res.substring(index);
						
						//res已经是string类型
						JSONObject result = new JSONObject(res);
						String status = (String)result.get("Status");
						
						if(status.equals("Success")){
							
							//先在 数据库中删除
							DbUtils dbUtils = DbUtils.create(getActivity());
							EmodouClassRoom classRoom = new EmodouClassRoom();
							try {
								classRoom = dbUtils.findFirst(Selector.from(EmodouClassRoom.class)
										                              .where("userid","=",userid)
										                              .and("classroomid","=",crIdStr)
										                              .and("classroomtype","=","a"));
								
								if(classRoom != null)
									dbUtils.delete(classRoom);
							} catch (DbException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
							Toast.makeText(getActivity(), R.string.person_myclass_detail_cancelSuc, 0).show();
							refreshClassList();
						}else{
							Toast.makeText(getActivity(), R.string.person_myclass_detail_cancelFail, 0).show();
						}
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), R.string.person_myclass_detail_cancelFail, 0).show();
			}
		});
	}
	
}
