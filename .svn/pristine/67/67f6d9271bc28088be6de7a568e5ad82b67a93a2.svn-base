package com.emodou.person;
/*
 * 班级详细信息界面，可退出班级
 */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouClassRoom;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyclassDetailFragment extends Fragment implements OnClickListener{
	
	private View myclassDetailView;
	
	private ImageView iconImv;
	private TextView crIdTv, schoolnameTv, crNameTv, gradeTv, createTimeTv;
	private TextView workDoingTv, workDoneTv, questionTv, answerTv;
	private TextView packageTv;
	private Button leaveBtn;
	
//	private ImageLoader imageLoader = ImageLoader.getInstance();
//	private DisplayImageOptions options;
	private String useridStr, crIdStr, ticketStr;
	
	public static final int UPDATE = 1;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		myclassDetailView = inflater.inflate(R.layout.person_myclass_detail, container, false);
		
		init();
		if(ValidateUtils.isNetworkConnected(getActivity())){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message message = new Message();
					message.what = UPDATE;
					handler.sendMessage(message);
				}
			}).start();
		}else{
			Toast.makeText(getActivity(), R.string.person_myclass_detail_updateFail, 0).show();
			refreshFromLocal();
		}
		return myclassDetailView;
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case UPDATE:
					refreshFromHttp();
					break;
	
				default:
					break;
			}
		}
	};

	public void init() {
		// TODO Auto-generated method stub
//		iconImv = (ImageView)myclassDetailView.findViewById(R.id.person_myclass_detail_icon);
		packageTv = (TextView)myclassDetailView.findViewById(R.id.person_myclass_detail_package);
		crIdTv = (TextView)myclassDetailView.findViewById(R.id.person_myclass_detail_crId);
		schoolnameTv = (TextView)myclassDetailView.findViewById(R.id.person_myclass_detail_schoolname);
		crNameTv = (TextView)myclassDetailView.findViewById(R.id.person_myclass_detail_classroomName);
		gradeTv = (TextView)myclassDetailView.findViewById(R.id.person_myclass_detail_grade);
		createTimeTv = (TextView)myclassDetailView.findViewById(R.id.person_myclass_detail_createTime);
		workDoingTv = (TextView)myclassDetailView.findViewById(R.id.person_myclass_detail_workDoing);
		workDoneTv = (TextView)myclassDetailView.findViewById(R.id.person_myclass_detail_workDone);
		questionTv = (TextView)myclassDetailView.findViewById(R.id.person_myclass_detail_question);
		answerTv = (TextView)myclassDetailView.findViewById(R.id.person_myclass_detail_answer);
		leaveBtn = (Button)myclassDetailView.findViewById(R.id.person_myclass_detail_leaveBtn);
		leaveBtn.setOnClickListener(this);
		
//		imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity().getBaseContext()));
//		//设置加载图片的配置
//		options = new DisplayImageOptions.Builder()
//				.showImageOnLoading(R.drawable.home_changepackage_item_icondefault)
//				.showImageForEmptyUri(R.drawable.home_changepackage_item_icondefault)
//				.showImageOnFail(R.drawable.home_changepackage_item_icondefault)
//				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
//				.build();
		
		
	}
	
	public void passData(String userid, String crId, String ticket){
		this.useridStr = userid;
		this.crIdStr = crId;
		this.ticketStr = ticket;
	}
	
	public void refreshFromHttp() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid",useridStr);
		params.addBodyParameter("classroomid",crIdStr);
		params.addBodyParameter("ticket",ticketStr);
		params.addBodyParameter("role","s");
		
		String path = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_GET_CLASSROOMINFO;
		
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, path, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				LogUtil.d("resultSuc", responseInfo.result.toString());
				try{									
					String res = responseInfo.result.toString();
					int index = res.indexOf("{");
					if(index == -1){
						refreshFromLocal();	
						Toast.makeText(getActivity(), R.string.person_myclass_detail_updateFail, 0).show();
					}else{
						
						//从index位开始截取
						res = res.substring(index);
						
						//res已经是string类型
						JSONObject result = new JSONObject(res);
						String status = (String)result.get("Status");
						
						if(status.equals("Success")){
							String schoolname = result.getString("SchoolName");
							String classroomId = result.getString("ClassroomId");
							String classroomname = result.getString("ClassroomName");
							String group = result.getString("Group");
							String grade = result.getString("Grade");
							String snum = result.getString("SNum");
							String teachername = result.getString("TeacherName");
							String workdoing = result.getString("WorkDoing");
							String workdone = result.getString("WorkDone");
							String question = result.getString("Question");
							String answer = result.getString("Answer");
							
							
							
							DbUtils dbUtils = DbUtils.create(getActivity());
							EmodouClassRoom classRoom = new EmodouClassRoom();
							classRoom = dbUtils.findFirst(Selector.from(EmodouClassRoom.class)
									                              .where("classroomid","=",classroomId)
									                              .and("userid","=",useridStr));
							if(classRoom != null){
								//因为是从前一个页面跳过来的，所以不可能为空
								classRoom.setSchoolname(schoolname);
								classRoom.setClassroomname(classroomname);
								classRoom.setGroup(group);
								classRoom.setClassroomgrade(grade);
								classRoom.setSnumber(snum);
								classRoom.setTeachername(teachername);
								classRoom.setWorkDoing(workdoing);
								classRoom.setWorkDone(workdone);
								classRoom.setQuestion(question);
								classRoom.setAnswer(answer);
								dbUtils.update(classRoom);
								
								String url = Constants.EMODOU_URL + "/" + classRoom.getClassroomicon();
								String packnameStr = classRoom.getClassroompackage();
								
								String pacTotalName = "";
								try {
									JSONArray pacJsonArray = new JSONArray(packnameStr);
									for(int i = 0; i < pacJsonArray.length() ; i ++){
										JSONObject pacJsonObject = pacJsonArray.getJSONObject(i);
										String pacNameStr = pacJsonObject.getString("packagename");
										
										if(!TextUtils.isEmpty(pacNameStr)){
											if(i != (pacJsonArray.length() -1))
												pacTotalName += pacNameStr + " / ";
											else 
											    pacTotalName += pacNameStr;
										}
											
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								
//								imageLoader.displayImage(url, iconImv, options);
								packageTv.setText(pacTotalName);
								crIdTv.setText(classroomId);
								schoolnameTv.setText(schoolname);
								crNameTv.setText(classroomname);
								gradeTv.setText(grade);
								createTimeTv.setText(teachername);
								workDoingTv.setText(workdoing);
								workDoneTv.setText(workdone);
								questionTv.setText(question);
								answerTv.setText(answer);
							}
						}else{
							refreshFromLocal();	
							Toast.makeText(getActivity(), R.string.person_myclass_detail_updateFail, 0).show();
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), R.string.person_myclass_detail_updateFail, 0).show();
				LogUtil.d("resultFail", msg);
			}
		});
		
	}
	
	public void refreshFromLocal() {
		
		
		DbUtils dbUtils = DbUtils.create(getActivity());
		EmodouClassRoom classRoom = new EmodouClassRoom();
		try {
			classRoom = dbUtils.findFirst(Selector.from(EmodouClassRoom.class)
					                                              .where("classroomid","=",crIdStr)
					                                              .and("userid","=",useridStr));
			if(classRoom != null){
				String url = Constants.EMODOU_URL + "/" + classRoom.getClassroomicon();
				String schoolname = classRoom.getSchoolname();
				String classroomname = classRoom.getClassroomname();
				String grade = classRoom.getClassroomgrade();
				String teachername = classRoom.getTeachername();
				String workdoing = classRoom.getWorkDoing();
				String workdone = classRoom.getWorkDone();
				String question = classRoom.getQuestion();
				String answer = classRoom.getAnswer();
				
				
				String packnameStr = classRoom.getClassroompackage();
				
				String pacTotalName = "";
				try {
					JSONArray pacJsonArray = new JSONArray(packnameStr);
					for(int i = 0; i < pacJsonArray.length() ; i ++){
						JSONObject pacJsonObject = pacJsonArray.getJSONObject(i);
						String pacNameStr = pacJsonObject.getString("packagename");
						
						if(!TextUtils.isEmpty(pacNameStr)){
							if(i != (pacJsonArray.length() -1))
								pacTotalName += pacNameStr + " / ";
							else 
							    pacTotalName += pacNameStr;
						}
							
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				imageLoader.displayImage(url, iconImv, options);
				packageTv.setText(pacTotalName);
				crIdTv.setText(crIdStr);
				schoolnameTv.setText(schoolname);
				crNameTv.setText(classroomname);
				gradeTv.setText(grade);
				createTimeTv.setText(teachername);
				workDoingTv.setText(workdoing);
				workDoneTv.setText(workdone);
				questionTv.setText(question);
				answerTv.setText(answer);
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.person_myclass_detail_leaveBtn:
				leaveClassRoom();
				break;
			
			default:
				break;
		}
	}

	public void leaveClassRoom() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid",useridStr);
		params.addBodyParameter("classroomid",crIdStr);
		params.addBodyParameter("ticket",ticketStr);
		
		String path = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_LEAVE_CLASSROOM;
		
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, path, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				try{									
					String res = responseInfo.result.toString();
					int index = res.indexOf("{");
					if(index == -1){
						Toast.makeText(getActivity(), R.string.person_myclass_detail_leaveFail, 0).show();
					}else{
						
						//从index位开始截取
						res = res.substring(index);
						
						//res已经是string类型
						JSONObject result = new JSONObject(res);
						String status = (String)result.get("Status");
						
						if(status.equals("Success")){
							Toast.makeText(getActivity(), R.string.person_myclass_detail_leaveSuc, 0).show();
							//先从本地数据库中删除
							DbUtils dbUtils = DbUtils.create(getActivity());
							try {
								EmodouClassRoom classRoom = new EmodouClassRoom();
								classRoom = dbUtils.findFirst(Selector.from(EmodouClassRoom.class)
										                              .where("classroomid","=",crIdStr)
										                              .and("userid","=",useridStr));
								if(classRoom != null){
									dbUtils.delete(classRoom);
								}
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							//返回列表界面
							PersonActivity personActivity = (PersonActivity)getActivity();
							personActivity.returnMajor();
						}else{
							Toast.makeText(getActivity(), R.string.person_myclass_detail_leaveFail, 0).show();
						}
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), R.string.person_myclass_detail_leaveFail, 0).show();
			}
		});
	}
	
	
}
