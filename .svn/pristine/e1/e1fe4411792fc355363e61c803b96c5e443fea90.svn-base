package com.emodou.login;

import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouBook;
import com.emodou.domain.EmodouClass;
import com.emodou.domain.EmodouClassManager;
import com.emodou.domain.EmodouPackage;
import com.emodou.domain.EmodouUnit;
import com.emodou.domain.EmodouUserInfo;
import com.emodou.extend.MD5Util;
import com.emodou.util.LogUtil;
import com.emodou.util.ValidateUtils;
import com.example.emodou.MainActivity;
import com.example.emodou.R;

import android.R.integer;
import android.R.layout;
import android.R.string;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AutoCompleteTextView.Validator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.emodou.util.Constants;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

//登陆界面
public class EmodouLoginAcvitity extends Activity implements OnClickListener{
	//输入控件
	private String CITY_PATH_JSON = Constants.EMODOU_URL;
	//对话框
	private ProgressDialog progressDialog;
	// 输入控件
	private EditText unameEt, upassEt;
	private String username,password;
	
	// 按钮控件
	private Button loginbtn, rgsbtn, forgetpsd;
	private String logintype, rgstype;//type为email或phone
	private Toast toast;
	
	//actionbar及其相关
	private ActionBar actionbar;
	private TextView titletext;
	private ImageButton imbReturn;//actionbar的返回按钮
	
	private String phoneStr;
	
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.login);
	        
	        //这里还未判断是否由退出登录跳转过来
	        
	        //初始化
	        init();
	    }

	 	public void init(){
	 		
	 		actionbar = getActionBar();
	 		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	 		actionbar.setCustomView(R.layout.actionbar_login);
	 		
	 		View view = actionbar.getCustomView();
	 		
	 		//actionbar属性获取操作
	 		titletext = (TextView)view.findViewById(R.id.login_ancionbar_text);	 		
	 		imbReturn = (ImageButton)view.findViewById(R.id.login_imbtn_return);
	 		
	 		imbReturn.setVisibility(View.GONE);
	 		
	 		loginbtn = (Button)findViewById(R.id.login_button_login);
	 		loginbtn.setOnClickListener(this);
	 		
	 		rgsbtn = (Button)findViewById(R.id.login_button_register);
	 		rgsbtn.setOnClickListener(this);
	 		
	 		forgetpsd = (Button)findViewById(R.id.login_ForgetPassword);
	 		forgetpsd.setOnClickListener(this);
	 		
	 		unameEt = (EditText)findViewById(R.id.login_phonemail);
	 		upassEt = (EditText)findViewById(R.id.login_password);
	 		
	 	    //根据是否是退出登录跳转过来的 选择性隐藏或显示返回按钮
	 		//没有写
	 		
	 		titletext.setText("登     录");
	 		
	 		phoneStr = getIntent().getStringExtra("phone");
	 		if(phoneStr != null){
	 			unameEt.setText(phoneStr);
	 		}
	 	}
	 	

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			
				//找回密码按钮
				case R.id.login_ForgetPassword:
					Intent intent = new Intent(EmodouLoginAcvitity.this, GetPasswordActivity.class);
					intent.putExtra("phone", username);
					intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					break;
					
				//注册按钮
				case R.id.login_button_register:
					Intent intent2 = new Intent(EmodouLoginAcvitity.this,EmodouRegisterActivity.class);
					//这里没有传递loginType
					intent2.setFlags(intent2.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent2);
					break;
				
				//这里没有判断actionbar中的返回按钮
				
				//登陆按钮
				case R.id.login_button_login:
					login();
					break;
			}
			
		}
		
	public void login(){
		progressDialog = new ProgressDialog(EmodouLoginAcvitity.this);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("正在登陆...");
		progressDialog.show();
		
		username = unameEt.getText().toString().trim();
		password = upassEt.getText().toString().trim();
		
		if(!ValidateUtils.isNetworkConnected(EmodouLoginAcvitity.this)){
			Toast.makeText(EmodouLoginAcvitity.this, 
					"请检查网络连接",Toast.LENGTH_SHORT).show();
			progressDialog.dismiss();
			return;
		}else if(TextUtils.isEmpty(username)||TextUtils.isEmpty(password)){
			
			AlertDialog.Builder builder = new AlertDialog.Builder(EmodouLoginAcvitity.this)
				.setTitle("提示")
				.setMessage("手机号码或密码不能为空")
				.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
			           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
			           }					            
			       }); 
			
			builder.create().show();						
		}
			
		CITY_PATH_JSON = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_LOGIN;
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("type", "s");
		params.addBodyParameter("account", username);
		//密码加密
		String randomStr = MD5Util.getRandomString(4);
		StringBuilder sbPassword = new StringBuilder(password);
		sbPassword.append(randomStr);
		String passwordFinal = MD5Util.parseStrToMd5L32(sbPassword.toString());
		passwordFinal = passwordFinal.replace(passwordFinal.substring(passwordFinal.length() - 4), randomStr);
		
		params.addBodyParameter("password", passwordFinal);

//		Log.d("psdmd555555", params.getQueryStringParams().toString());
		
		
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST, CITY_PATH_JSON, params,
				new RequestCallBack<String>() {
			
					public void onSuccess(ResponseInfo<String> responseInfo) {
							
						System.out.println(responseInfo.result);
						
						try{									
							String res = responseInfo.result.toString();
							int index = res.indexOf("{");
							if(index == -1){
								toast = Toast.makeText(getApplicationContext(), R.string.server_exception,Toast.LENGTH_LONG);
								toast.setGravity(Gravity.CENTER, 0, 0);
								
								LinearLayout toastview = (LinearLayout)toast.getView();
								toastview.setBackgroundColor(getResources().getColor(R.color.red));
								toast.setView(toastview);
								toast.show();
								
								progressDialog.dismiss();									
							}else{
								
								//从index位开始截取
								res = res.substring(index);
								
								//res已经是string类型
								JSONObject result = new JSONObject(res);
								String status = (String)result.get("Status");
								
								if(status.equals("Success")){
									
									JSONObject baseInfo = result.getJSONObject("BaseInfo");
									String userid = (String)baseInfo.get("UserId");
									String username = (String)baseInfo.get("UserName");
									String nikiname = (String)baseInfo.get("Nickname");
									String sex = (String)baseInfo.get("Sex");
									String phone = (String) baseInfo.get("Phone");
									String email = (String) baseInfo.get("Email");
									String qqnum = (String)baseInfo.get("QQNum");
									String location = (String) baseInfo.get("Location");
									String usericon = (String)baseInfo.get("UserIcon");
									String school = (String) baseInfo.get("School");
									String grade = (String) baseInfo.get("Grade");
									String role = (String)baseInfo.get("Role");
									String ticket = (String)baseInfo.get("Ticket");
									String tickettime = (String) baseInfo.get("TicketTime");
									String vipString = baseInfo.getString("VipList");
									String pagString = result.getString("PackageList");										
									
									DbUtils db = DbUtils.create(EmodouLoginAcvitity.this);
									
									try {
										EmodouUserInfo entity = db.findFirst(Selector.from(EmodouUserInfo.class));
										if(entity != null){
											entity.setUserid(userid);
											entity.setUsername(username);
											entity.setNikiname(nikiname);
											entity.setQqnum(qqnum);
											entity.setUsericon(usericon);
											entity.setPhone(phone);
											entity.setEmail(email);
											entity.setLocation(location);
											entity.setSchool(school);
											entity.setGrade(grade);
											entity.setRole(role);	
											entity.setTicket(ticket);
											entity.setTickettimeString(tickettime);	
											entity.setViplistString(vipString);
											entity.setPackagelistString(pagString);
											entity.setDate(System.currentTimeMillis());
											
											db.update(entity);
										}else{
											
											//将object对象转换成类对象
											EmodouUserInfo uif = new EmodouUserInfo();
											uif.setUserid(userid);
											uif.setUsername(username);
											uif.setNikiname(nikiname);
											uif.setQqnum(qqnum);
											uif.setUsericon(usericon);
											uif.setPhone(phone);
											uif.setEmail(email);
											uif.setLocation(location);
											uif.setSchool(school);
											uif.setGrade(grade);
											uif.setRole(role);	
											uif.setTicket(ticket);
											uif.setTickettimeString(tickettime);	
											uif.setViplistString(vipString);
											uif.setPackagelistString(pagString);
											uif.setDate(System.currentTimeMillis());
											
											db.saveBindingId(uif);
											
										}
										
										EmodouPackage emodoupackage = new EmodouPackage();
										
										//用户当前正在使用的包的名字和id
										String packageid = result.getString("PackageId");
										String packagename = result.getString("PackageName");
										String packagemode = result.getString("PackageMode");
										
										emodoupackage.setPackageid(packageid);
										emodoupackage.setPackagename(packagename);
										emodoupackage.setPackageMode(packagemode);
										emodoupackage.setUserid(userid);												
										//该参数表示该体验包所属服务包的id
										//emodoupackage.setPackageBelong(packageString);
										
										//声明一个包，找到上次存储的包，改为未选中
										EmodouPackage emodoupackage2 = new EmodouPackage();
										//将上次本地存储的包由选择改为未选，将得到的packageid所对应的包改为选择
										emodoupackage2 = db.findFirst(Selector.from(EmodouPackage.class)
																			  .where("state","=",Constants.PACKAGE_STATE_SELECT)
												                              .and("userid","=",userid));
										
									    //如果不为空，则改为未选中，杨阳的逻辑有一个问题是，如果当服务包在上述三个属性中更改了，本地不会改，
										//只有到换包刷新列表的时候才会变化
										if(emodoupackage2 != null&& !emodoupackage2.getPackageid().equals(packageid)){
											
											emodoupackage2.setState(Constants.PACKAGE_STATE_NOT_SELECT);
											db.update(emodoupackage2);
											//声明一个包，找到从服务器查找到的正在学习的包
											EmodouPackage emodoupackage3 = new EmodouPackage();
											emodoupackage3 = db.findFirst(Selector.from(EmodouPackage.class)
																				  .where("packageid","=",packageid)
														                          .and("userid","=",userid));
											if(emodoupackage3 != null){
												
												emodoupackage3.setState(Constants.PACKAGE_STATE_SELECT);
												emodoupackage3.setPackagename(packagename);
												emodoupackage3.setPackageMode(packagemode);
												db.update(emodoupackage3);
												
											}else{
												
												emodoupackage.setState(Constants.PACKAGE_STATE_SELECT);
												db.saveBindingId(emodoupackage);
												
											}
											
										}else if(emodoupackage2 == null){
											//首次登陆，本地没有选中的（正在学习）的包,本地也不可能有包的记录
											emodoupackage.setState(Constants.PACKAGE_STATE_SELECT);
											db.saveBindingId(emodoupackage);
										}else if(emodoupackage2.getPackageid().equals(packageid)){
											//正在使用的包没有变化
											emodoupackage2.setPackagename(packagename);
											emodoupackage2.setPackageMode(packagemode);
											db.update(emodoupackage2);
										}
										
										
										String bookid = result.getString("BookId");
										if(bookid == null || bookid.equals("")){
											
										}else{
											//如果用户首次登陆，会返回空，因此要判断这种情况
											JSONObject bookinfo = result.getJSONObject("BookInfo");
											String bookname = bookinfo.getString("bookname");
											String bookicon = bookinfo.getString("bookicon");
											String publishtime = bookinfo.getString("publishtime");
											String description = bookinfo.getString("description");
											String changetimestring = bookinfo.getString("changetime");
											String press = bookinfo.getString("press");//出版社
											String wordurl = bookinfo.getString("wordurl");												
											String wordsize = bookinfo.getString("wordsize");
											String wordChangetimeStr = bookinfo.getString("wordchangetime");
											
											Integer changetimeint = new Integer(0);
											Integer wordChangetimeInt = new Integer(wordChangetimeStr);
											
											if(changetimestring!=null){												
												changetimeint = Integer.parseInt(changetimestring);
											}
											if(wordChangetimeStr != null){
												wordChangetimeInt = Integer.parseInt(wordChangetimeStr);
											}
											
											//同上面服务包，将state为选中的书改为未选中
											List<EmodouBook>emodoubook = db.findAll(Selector.from(EmodouBook.class)
																							.where("state","=",Constants.BOOK_STATE_SELECT)
																							.and("userid","=",userid));
											
											if(emodoubook!=null&&emodoubook.size()!=0){
												for(int i = 0;i < emodoubook.size();i++){
													emodoubook.get(i).setState(Constants.BOOK_STATE_NOT_SELECT);
												}
												db.updateAll(emodoubook);
											}
											
											EmodouBook emodoubook2 = db.findFirst(Selector.from(EmodouBook.class)
																						  .where("bookid","=",bookid)
																						  .and("userid","=",userid)
																						  .and("packageid","=",packageid));
											
											if(emodoubook2!=null){
												emodoubook2.setBookname(bookname);
												emodoubook2.setBookicon(bookicon);
												emodoubook2.setPublishTime(publishtime);
												emodoubook2.setDescription(description);
												emodoubook2.setChangetime(changetimeint);
												emodoubook2.setPress(press);
												emodoubook2.setWordurl(wordurl);
												emodoubook2.setWordsize(wordsize);
												emodoubook2.setWordChangetime(wordChangetimeInt);
												emodoubook2.setState(Constants.BOOK_STATE_SELECT);		
												db.update(emodoubook2);
											}else{
												
												EmodouBook emodoubook3 = new EmodouBook();
												emodoubook3.setBookid(bookid);
												emodoubook3.setUserid(userid);
												emodoubook3.setPackageid(packageid);	
												emodoubook3.setBookname(bookname);
												emodoubook3.setBookicon(bookicon);
												emodoubook3.setPublishTime(publishtime);
												emodoubook3.setDescription(description);
												emodoubook3.setChangetime(changetimeint);
												emodoubook3.setPress(press);
												emodoubook3.setWordurl(wordurl);
												emodoubook3.setWordsize(wordsize);
												emodoubook3.setWordChangetime(wordChangetimeInt);
												emodoubook3.setState(Constants.BOOK_STATE_SELECT);
												db.saveBindingId(emodoubook3);
											}
											
											JSONArray unitarray = bookinfo.getJSONArray("unit");
											for(int uniti=0;uniti<unitarray.length();uniti++){
												
												JSONObject unitobject = unitarray.getJSONObject(uniti);
												EmodouUnit unit = new EmodouUnit();
												String unitidstr = unitobject.getString("unitid");
												unit.setBookid(bookid);
												unit.setUnitid(unitidstr);
												unit.setUnitname(unitobject.getString("unitname"));
												unit.setUniticon(unitobject.optString("uniticon"));
												unit.setUnitdes(unitobject.optString("description"));
												
												EmodouUnit unit2 = new EmodouUnit();
												unit2 = db.findFirst(Selector.from(EmodouUnit.class)
																			 .where("unitid","=",unitidstr));
												
												if(unit2 == null){
													db.saveBindingId(unit);
												}else{
													unit2.setUnitname(unitobject.getString("unitname"));
													unit2.setUniticon(unitobject.optString("uniticon"));
													unit2.setUnitdes(unitobject.optString("description"));
													db.update(unit2);
												}
												
												JSONArray readarray = unitobject.getJSONArray("read");
												for(int readi = 0; readi < readarray.length(); readi++){
													JSONObject classobject = readarray.getJSONObject(readi);
													
													
													String classid = classobject.getString("classid");
													String title = classobject.getString("title");
													String icon = classobject.getString("icon");
													String changetimeStr = classobject.getString("changetime");
													String resurl = classobject.getString("resurl");
													String size = classobject.getString("size");
													String classdescription = classobject.getString("description");
													
													Integer changetimeInt = new Integer(0);
													if(changetimeStr != null){
														changetimeInt = Integer.parseInt(changetimeStr);
													}
												
													
													EmodouClass classNow = new EmodouClass();
													classNow = db.findFirst(Selector.from(EmodouClass.class)
																					.where("packageid","=",packageid)
																					.and("classid","=",classid)
																					.and("type","=",Constants.EMODOU_TYPE_READ));												
													
													
													if(classNow == null){
														EmodouClass readclass = new EmodouClass();
														readclass.setPackageid(packageid);
														readclass.setBookid(bookid);
														readclass.setUnitid(unitidstr);
														readclass.setClassid(classid);
														readclass.setTitle(title);
														readclass.setIcon(icon);
														readclass.setChangetimeInt(changetimeInt);
														readclass.setResurl(resurl);
														readclass.setSize(size);
														readclass.setClassdes(classdescription);
	
														//1代表的是读
														readclass.setType(Constants.EMODOU_TYPE_READ);
														db.saveBindingId(readclass);
													}else{
														classNow.setTitle(title);
														classNow.setIcon(icon);
														classNow.setChangetimeInt(changetimeInt);
														classNow.setResurl(resurl);
														classNow.setSize(size);
														classNow.setClassdes(classdescription);
														db.update(classNow);
													}
													
													EmodouClassManager classmanager = new EmodouClassManager();
													classmanager = db.findFirst(Selector.from(EmodouClassManager.class)
																						.where("userid","=",userid)
																						.and("classid","=",classid)
																						.and("bookid","=",bookid)
																						.and("type","=",Constants.EMODOU_TYPE_READ));
													
													if(classmanager == null){
														EmodouClassManager classManager = new EmodouClassManager();
														
														classManager.setUserid(userid);
														classManager.setClassid(classid);
														classManager.setBookid(bookid);
														classManager.setType(Constants.EMODOU_TYPE_READ);
														classManager.setState(Constants.EMODOU_CLASS_STATE_NOT_LEAREN);
														classManager.setScore("0");
																													
														db.saveBindingId(classManager);
													}
												}
												
												JSONArray listenarray = unitobject.getJSONArray("listen");
												for(int listeni = 0; listeni < listenarray.length(); listeni++){
													JSONObject classobject = listenarray.getJSONObject(listeni);						
													
													String classid = classobject.getString("classid");
													String title = classobject.getString("title");
													String icon = classobject.getString("icon");
													String changetimeStr = classobject.getString("changetime");
													String resurl = classobject.getString("resurl");
													String size = classobject.getString("size");
													String classdescription = classobject.getString("description");
	
													Integer changetimeInt = new Integer(0);
													if(changetimeStr != null){
														changetimeInt = Integer.parseInt(changetimeStr);
													}
													
													EmodouClass classNow = new EmodouClass();
													classNow = db.findFirst(Selector.from(EmodouClass.class)
																					.where("packageid","=",packageid)
																					.and("classid","=",classid)
																					.and("type","=",Constants.EMODOU_TYPE_LISTEN));
													
													if(classNow == null){
														EmodouClass listenclass = new EmodouClass();
														listenclass.setPackageid(packageid);
														listenclass.setBookid(bookid);
														listenclass.setUnitid(unitidstr);
														listenclass.setClassid(classid);
														listenclass.setTitle(title);
														listenclass.setIcon(icon);
														listenclass.setChangetimeInt(changetimeInt);
														listenclass.setResurl(resurl);
														listenclass.setSize(size);
														listenclass.setClassdes(classdescription);										
														//2代表的是听
														listenclass.setType(Constants.EMODOU_TYPE_LISTEN);
														db.saveBindingId(listenclass);
													}else{
														classNow.setTitle(title);
														classNow.setIcon(icon);
														classNow.setChangetimeInt(changetimeInt);
														classNow.setResurl(resurl);
														classNow.setSize(size);
														classNow.setClassdes(classdescription);
														db.update(classNow);
													}
													
													EmodouClassManager classmanager = new EmodouClassManager();
													classmanager = db.findFirst(Selector.from(EmodouClassManager.class)
																						.where("userid","=",userid)
																						.and("classid","=",classid)
																						.and("bookid","=",bookid)
																						.and("type","=",Constants.EMODOU_TYPE_LISTEN));
													
													if(classmanager == null){
														EmodouClassManager classManager = new EmodouClassManager();
														
														classManager.setUserid(userid);
														classManager.setClassid(classid);
														classManager.setBookid(bookid);
														classManager.setType(Constants.EMODOU_TYPE_LISTEN);
														classManager.setState(Constants.EMODOU_CLASS_STATE_NOT_LEAREN);
														classManager.setScore("0");
																													
														db.saveBindingId(classManager);
													}
												}
												
												JSONArray speakarray = unitobject.getJSONArray("speak");
												for(int speaki = 0; speaki < speakarray.length(); speaki++){
													JSONObject classobject = speakarray.getJSONObject(speaki);
													
													
													String classid = classobject.getString("classid");
													String title = classobject.getString("title");
													String icon = classobject.getString("icon");
													String changetimeStr = classobject.getString("changetime");
													String resurl = classobject.getString("resurl");
													String size = classobject.getString("size");
													String classdescription = classobject.getString("description");
	
													Integer changetimeInt = new Integer(0);
													if(changetimeStr != null){
														changetimeInt = Integer.parseInt(changetimeStr);
													}
													
													
													EmodouClass classNow = new EmodouClass();
													classNow = db.findFirst(Selector.from(EmodouClass.class)
																					.where("packageid","=",packageid)
																					.and("classid","=",classid)
																					.and("type","=",Constants.EMODOU_TYPE_SPEAK));
													
													if(classNow == null){
														EmodouClass speakclass = new EmodouClass();
														speakclass.setPackageid(packageid);
														speakclass.setBookid(bookid);
														speakclass.setUnitid(unitidstr);
														speakclass.setClassid(classid);
														speakclass.setTitle(title);
														speakclass.setIcon(icon);
														speakclass.setChangetimeInt(changetimeInt);
														speakclass.setResurl(resurl);
														speakclass.setSize(size);
														speakclass.setClassdes(classdescription);
														//3代表的是说
														speakclass.setType(Constants.EMODOU_TYPE_SPEAK);
														db.saveBindingId(speakclass);
													}else{
														classNow.setTitle(title);
														classNow.setIcon(icon);
														classNow.setChangetimeInt(changetimeInt);
														classNow.setResurl(resurl);
														classNow.setSize(size);
														classNow.setClassdes(classdescription);
														db.update(classNow);
													}
													
													EmodouClassManager classmanager = new EmodouClassManager();
													classmanager = db.findFirst(Selector.from(EmodouClassManager.class)
																						.where("userid","=",userid)
																						.and("classid","=",classid)
																						.and("bookid","=",bookid)
																						.and("type","=",Constants.EMODOU_TYPE_SPEAK));
													
													if(classmanager == null){
														EmodouClassManager classManager = new EmodouClassManager();
														
														classManager.setUserid(userid);
														classManager.setClassid(classid);
														classManager.setBookid(bookid);
														classManager.setType(Constants.EMODOU_TYPE_SPEAK);
														classManager.setState(Constants.EMODOU_CLASS_STATE_NOT_LEAREN);
														classManager.setScore("0");
																													
														db.saveBindingId(classManager);
													}
												}
											}
																								
										}
										
										
										
									}catch(DbException e){											
										e.printStackTrace();
									}
									
									progressDialog.dismiss();
									Intent intent3 = new Intent(EmodouLoginAcvitity.this,
																MainActivity.class);
									intent3.putExtra("typeFirst", "login");
									//intent3.putExtra("changebook", "login");
									intent3.setFlags(intent3.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent3);
								}else{
									
									progressDialog.dismiss();
									String message = result.getString("Message");
									
									if(message.equals("Error_Wrong_Password")){
										Toast.makeText(EmodouLoginAcvitity.this, 
													  "账号或密码错误", Toast.LENGTH_LONG).show();
									}else if(message.equals("Error_Query_Failed ")){
										Toast.makeText(EmodouLoginAcvitity.this, 
													  "服务器数据库错误", Toast.LENGTH_LONG).show();
									}else{
										Toast.makeText(EmodouLoginAcvitity.this,
													  "登录失败", Toast.LENGTH_LONG).show();
									}
								}
							}
						}catch(JSONException e){
							
							progressDialog.dismiss();
							Toast.makeText(EmodouLoginAcvitity.this,
									"登录失败2，json解析失败", Toast.LENGTH_LONG).show();
							e.printStackTrace();
							System.out.println(e);
						}
					}

					@Override
					public void onFailure(HttpException error,
							String msg) {
						// TODO Auto-generated method stub
						LogUtil.d("loginErrorMsg", msg);
						progressDialog.dismiss();
						Toast.makeText(EmodouLoginAcvitity.this, 
								"登录失败3，网络异常", Toast.LENGTH_LONG).show();
					}
				});
		}
		

}
