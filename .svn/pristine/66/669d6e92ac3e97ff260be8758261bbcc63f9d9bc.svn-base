package com.emodou.person;

import java.util.zip.Inflater;

import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouUserInfo;
import com.emodou.login.RegistClassActivity;
import com.emodou.util.Constants;
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

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyclassAppFragment extends Fragment implements OnClickListener{
	
	private View myclassAppView;
	
	private EditText editUsername, editNikiname, editLocation;
	private EditText editQQ, editEmail, editParentname, editParentphone;
	private Button appBtn;
	private TextView schoolnameTv, gradeTv, classnameTv, teachernameTv, groupTv;
	
	private String codeStr, userid;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		myclassAppView = inflater.inflate(R.layout.person_myclass_app, container, false);
		
		init();
		return myclassAppView;
	}

	public void init() {
		// TODO Auto-generated method stub
		editUsername = (EditText)myclassAppView.findViewById(R.id.person_myclass_app_editUsername);
		editNikiname = (EditText)myclassAppView.findViewById(R.id.person_myclass_app_editNikiname);
		editLocation = (EditText)myclassAppView.findViewById(R.id.person_myclass_app_editLocation);
		editQQ = (EditText)myclassAppView.findViewById(R.id.person_myclass_app_editQQ);
		editEmail = (EditText)myclassAppView.findViewById(R.id.person_myclass_app_editEmail);
		editParentname = (EditText)myclassAppView.findViewById(R.id.person_myclass_app_editParentname);
		editParentphone = (EditText)myclassAppView.findViewById(R.id.person_myclass_app_editParentphone);
		schoolnameTv = (TextView)myclassAppView.findViewById(R.id.person_myclass_app_schoolname);
		gradeTv = (TextView)myclassAppView.findViewById(R.id.person_myclass_app_grade);
		groupTv = (TextView)myclassAppView.findViewById(R.id.person_myclass_app_group);
		classnameTv = (TextView)myclassAppView.findViewById(R.id.person_myclass_app_classname);
		teachernameTv = (TextView)myclassAppView.findViewById(R.id.person_myclass_app_teachername);
		appBtn = (Button)myclassAppView.findViewById(R.id.person_myclass_app_btn);
		appBtn.setOnClickListener(this);
		
		if(ValidateUtils.isNetworkConnected(getActivity()))
			getCodeClassRoom();
		else 
			Toast.makeText(getActivity(), R.string.prompt_network, 0).show();
		
	}
	
	public void passCodeStr(String codestr, String userid) {
		this.codeStr = codestr;
		this.userid = userid;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.person_myclass_app_btn:
				if(ValidateUtils.isNetworkConnected(getActivity()))
					appClassRoom();
				else 
					Toast.makeText(getActivity(), R.string.prompt_network, 0).show();
				break;
	
			default:
				break;
		}
	}
	
	public void getCodeClassRoom() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid",userid);
		params.addBodyParameter("classcode",codeStr);
		
		String path = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_CLASSCODE_INFO;
		
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, path, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				try{									
					String res = responseInfo.result.toString();
					int index = res.indexOf("{");
					if(index == -1){
						Toast.makeText(getActivity(), R.string.person_appclass_getCodeInfoFailed,Toast.LENGTH_LONG).show();								
					}else{
						
						//从index位开始截取
						res = res.substring(index);
						
						//res已经是string类型
						JSONObject result = new JSONObject(res);
						String status = (String)result.get("Status");
						
						if(status.equals("Success")){
							String schoolName = result.getString("SchoolName");
							String grade = result.getString("Grade");
							String classname = result.getString("ClassName");
							String teachername = result.getString("TeacherName");
							String group = result.getString("Group");
							
							 EmodouUserInfo userInfo = new EmodouUserInfo();
							 DbUtils dbUtils = DbUtils.create(getActivity());
							 try {
								 userInfo = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
								 if(userInfo != null){
									 String username = userInfo.getUsername();
									 String nikiname = userInfo.getNikiname();
									 String location = userInfo.getLocation();
									 String QQ = userInfo.getQqnum();
									 String email = userInfo.getEmail();
									 String parentname = userInfo.getParentname();
									 String parentphone = userInfo.getParentphone();
									 
									 if(!TextUtils.isEmpty(username))
										 editUsername.setText(username);
									 if(!TextUtils.isEmpty(nikiname))
										 editNikiname.setText(nikiname);
									 if(!TextUtils.isEmpty(location))
										 editLocation.setText(location);
									 if(!TextUtils.isEmpty(QQ))
										 editQQ.setText(QQ);
									 if(!TextUtils.isEmpty(email))
										 editEmail.setText(email);
									 if(!TextUtils.isEmpty(parentname))
										 editParentname.setText(parentname);
									 if(!TextUtils.isEmpty(parentphone))
										 editParentphone.setText(parentphone);
									 
								 }
								 
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							 
							 if(!TextUtils.isEmpty(schoolName)&& !schoolName.equals("null"))
								 schoolnameTv.setText("学校名称: "+schoolName);
							 if(!TextUtils.isEmpty(grade)&& !grade.equals("null"))
								 gradeTv.setText("年级: "+grade);
							 if(!TextUtils.isEmpty(classname)&& !classname.equals("null"))
								 classnameTv.setText("班级名称: "+classname);
							 if(!TextUtils.isEmpty(teachername)&& !teachername.equals("null"))
								 teachernameTv.setText("教师名称: "+teachername);
							 //班级分组：0其他组；1幼儿组；2小学组；3初中组；4高中组；5大学组
							 if(!TextUtils.isEmpty(group)&& !group.equals("null")){
								 if(group.equals("0")) 
									 groupTv.setText("班级分组: " + "其他组");
								 else if(group.equals("1")) 
									 groupTv.setText("班级分组: " + "幼儿组");
								 else if(group.equals("2")) 
									 groupTv.setText("班级分组: " + "小学组");
								 else if(group.equals("3")) 
									 groupTv.setText("班级分组: " + "初中组");
								 else if(group.equals("4")) 
									 groupTv.setText("班级分组: " + "高中组");
								 else if(group.equals("5")) 
									 groupTv.setText("班级分组: " + "大学组");
									 
							 }
							 
						}else{
							Toast.makeText(getActivity(), R.string.person_appclass_getCodeInfoFailed,Toast.LENGTH_LONG).show();
						}
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), R.string.person_appclass_getCodeInfoFailed,Toast.LENGTH_LONG).show();
			}
		});
	}

	public void appClassRoom() {
		String usernameStr = editUsername.getText().toString().trim();
		String nikinameStr = editNikiname.getText().toString().trim();
		String locationStr = editLocation.getText().toString().trim();
		String QQStr = editQQ.getText().toString().trim();
		String emailStr = editEmail.getText().toString().trim();
		final String parentNameStr = editParentname.getText().toString().trim();
		final String parentPhoneStr = editParentphone.getText().toString().trim();
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("usertype","s");
		params.addBodyParameter("userid",userid);
		params.addBodyParameter("username",usernameStr);
		params.addBodyParameter("nikiname",nikinameStr);
		params.addBodyParameter("location",locationStr);
		params.addBodyParameter("qq",QQStr);
		params.addBodyParameter("email",emailStr);
		params.addBodyParameter("classroomcode",codeStr);
		params.addBodyParameter("parentname",parentNameStr);
		params.addBodyParameter("parentphone",parentPhoneStr);
		
		HttpUtils httpUtils = new HttpUtils();
		String PATH_JSON = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_SAVEINFO;
		httpUtils.send(HttpMethod.POST, PATH_JSON,params,  new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				try{									
					String res = responseInfo.result.toString();
					String resInfo = responseInfo.toString();
					int index = res.indexOf("{");
					if(index == -1){
						Toast.makeText(getActivity(), R.string.person_myclass_app_appFailed,Toast.LENGTH_LONG).show();									
					}else{
						
						//从index位开始截取
						res = res.substring(index);
						
						//res已经是string类型
						JSONObject result = new JSONObject(res);
						String status = (String)result.get("Status");
						
						if(status.equals("Success")){
							
//							JSONObject baseInfo = result.getJSONObject("BaseInfo");
//							String userid = (String)baseInfo.get("UserId");
//							String username = (String)baseInfo.get("UserName");
//							String nikiname = (String)baseInfo.get("NikiName");
//							String qqnum = (String)baseInfo.get("QQNum");
//							String usericon = (String)baseInfo.get("UserIcon");
//							String phone = (String) baseInfo.get("Phone");
//							String email = (String) baseInfo.get("Email");
//							String location = (String) baseInfo.get("Location");
//							String school = (String) baseInfo.get("School");
//							String grade = (String) baseInfo.get("Grade");
//							String role = (String)baseInfo.get("Role");
//							String ticket = (String)baseInfo.get("Ticket");
//							String tickettime = (String) baseInfo.get("TicketTime");
//							String vipString = baseInfo.getString("VipList");
//							String pagString = result.getString("PackageList");
//
//							//这里和服务器沟通有问题，服务器没有返回家长信息
//					        String parentname = parentNameStr;
//					        String parentphone = parentPhoneStr;
//
//							DbUtils db = DbUtils.create(getActivity());
							
							try {
//								EmodouUserInfo entity = db.findFirst(Selector.from(EmodouUserInfo.class));
//								if(entity != null){
//									entity.setUserid(userid);
//									entity.setUsername(username);
//									entity.setNikiname(nikiname);
//									entity.setQqnum(qqnum);
//									entity.setUsericon(usericon);
//									entity.setPhone(phone);
//									entity.setEmail(email);
//									entity.setLocation(location);
//									entity.setSchool(school);
//									entity.setGrade(grade);
//									entity.setRole(role);
//									entity.setTicket(ticket);
//									entity.setTickettimeString(tickettime);
//									entity.setViplistString(vipString);
//									entity.setPackagelistString(pagString);
//									entity.setDate(System.currentTimeMillis());
//
//									if(!TextUtils.isEmpty(parentname))
//										entity.setParentname(parentname);
//									if(!TextUtils.isEmpty(parentphone))
//										entity.setParentphone(parentphone);
//
//									db.update(entity);
//								}else{
//
//									//将object对象转换成类对象
//									EmodouUserInfo uif = new EmodouUserInfo();
//									uif.setUserid(userid);
//									uif.setUsername(username);
//									uif.setNikiname(nikiname);
//									uif.setQqnum(qqnum);
//									uif.setUsericon(usericon);
//									uif.setPhone(phone);
//									uif.setEmail(email);
//									uif.setLocation(location);
//									uif.setSchool(school);
//									uif.setGrade(grade);
//									uif.setRole(role);
//									uif.setTicket(ticket);
//									uif.setTickettimeString(tickettime);
//									uif.setViplistString(vipString);
//									uif.setPackagelistString(pagString);
//									uif.setDate(System.currentTimeMillis());
//
//									if(!TextUtils.isEmpty(parentname))
//										entity.setParentname(parentname);
//									if(!TextUtils.isEmpty(parentphone))
//										entity.setParentphone(parentphone);
//
//									db.saveBindingId(uif);
//
//								}
								
								PersonActivity personActivity = (PersonActivity)getActivity();
								personActivity.appComplete();
								Toast.makeText(getActivity(), R.string.person_myclass_app_appSuc, 0).show();
								
							}catch(Exception e){
								e.printStackTrace();
							}
						}else 
							Toast.makeText(getActivity(), R.string.person_myclass_app_appFailed, 0).show();
					}
				}catch(JSONException e){
						Toast.makeText(getActivity(), R.string.person_myclass_app_appFailed, 1).show();
						e.printStackTrace();
					}
				
				}

				@Override
				public void onFailure(HttpException error, String msg) {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(), R.string.person_myclass_app_appFailed, 1).show();
				}
			});
		
	}
	
}
