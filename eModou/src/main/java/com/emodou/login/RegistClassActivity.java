package com.emodou.login;


import java.security.Policy.Parameters;
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
import com.emodou.extend.MipcaActivityCapture;
import com.emodou.util.Constants;
import com.emodou.util.LogUtil;
import com.emodou.util.ValidateUtils;
import com.example.emodou.MainActivity;
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

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class RegistClassActivity extends Activity implements OnClickListener{
	
	//actionbar及其相关
	private ActionBar actionbar;
	private TextView titletext;
	private ImageButton imbReturn;//actionbar的返回按钮
	
	//界面控件属性
	private ImageView startlearnImv, havecodeImv;
	private EditText editCode, editUsername, editNikiname, editLocation;
	private EditText editQQ, editEmail, editParentname, editParentphone;
	private EditText editGrade, editParentemail;
	private ImageView scanImv;
	private Button completeBtn;
	private RelativeLayout contentRl;
	private ImageView whatcodeImv;
	private RadioGroup sexGroup;
	private RadioButton manButton, womanButton;
	
	
	//界面数据
	private final static int SCANNIN_GREQUEST_CODE = 1;
	private static int SHOW_CONTENT = 0;
	private ProgressDialog progressDialog;	
	private Toast toast;
	private String phoneStr;//如果用户直接学习，则手机号就是用户名
	private String sexCode = "2";//性别0女1男2不详
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_class);
		
		setActionbar();
		
		init();
	}
	
	
	public void setActionbar() {
		// TODO Auto-generated method stub
		 actionbar = getActionBar();
	 	 actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	 	 actionbar.setCustomView(R.layout.actionbar_login);
	 	 View view = actionbar.getCustomView();
	 	 imbReturn = (ImageButton)view.findViewById(R.id.login_imbtn_return);
	 	 imbReturn.setVisibility(View.GONE);
	 	 titletext = (TextView)findViewById(R.id.login_ancionbar_text);
	 	 titletext.setText("注        册");
	}
	
	public void init() {
		
		phoneStr = getIntent().getStringExtra("phone");
		
		startlearnImv = (ImageView)findViewById(R.id.register_class_startlearn);
		startlearnImv.setOnClickListener(this);
		havecodeImv = (ImageView)findViewById(R.id.register_class_havecode);
		havecodeImv.setOnClickListener(this);
		editCode = (EditText)findViewById(R.id.register_class_editCode);
		scanImv = (ImageView)findViewById(R.id.register_class_scanimv);
		editUsername = (EditText)findViewById(R.id.register_class_editUsername);
		editNikiname = (EditText)findViewById(R.id.register_class_editNikiname);
		editLocation = (EditText)findViewById(R.id.register_class_editLocation);
		editQQ = (EditText)findViewById(R.id.register_class_editQQ);
		editEmail = (EditText)findViewById(R.id.register_class_editEmail);
		editParentname = (EditText)findViewById(R.id.register_class_editParentname);
		editParentphone = (EditText)findViewById(R.id.register_class_editParentphone);
		editGrade = (EditText)findViewById(R.id.register_class_editGrade);
		editParentemail = (EditText)findViewById(R.id.register_class_editParentemail);
		sexGroup = (RadioGroup)findViewById(R.id.register_class_usersexRgroup);
		manButton = (RadioButton)findViewById(R.id.register_class_usersexMan);
		womanButton = (RadioButton)findViewById(R.id.register_class_usersexWoman);
		completeBtn = (Button)findViewById(R.id.register_class_completeBtn);
		completeBtn.setOnClickListener(this);
		contentRl = (RelativeLayout)findViewById(R.id.register_class_contentRl);
		whatcodeImv = (ImageView)findViewById(R.id.register_class_whatcode);
		whatcodeImv.setOnClickListener(this);
		if(SHOW_CONTENT == 0){
			contentRl.setVisibility(View.GONE);
		}else{
			contentRl.setVisibility(View.VISIBLE);
		}
		
		//点击按钮跳转到二维码扫描界面，这里用的是startActivityForResult跳转
		//扫描完了之后调到该界面
		scanImv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(RegistClassActivity.this, MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});
		
		sexGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int arg1) {
				// TODO Auto-generated method stub
				//获取变更后的选中项的ID
				int radioButtonId = group.getCheckedRadioButtonId();
				//根据ID获取RadioButton的实例
				RadioButton rb = (RadioButton)findViewById(radioButtonId);
				if(rb.getText().equals("男")){
					sexCode = "1";
					LogUtil.d("rbtextMan", rb.getText().toString());
				}else if(rb.getText().equals("女")){
					sexCode = "0";
					LogUtil.d("rbtextWoman", rb.getText().toString());
				}		
			}
		});
	}
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();
				//显示扫描到的内容
				editCode.setText(bundle.getString("result"));
			}
			break;
		}
    }	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.register_class_startlearn:
				if(ValidateUtils.isNetworkConnected(RegistClassActivity.this)){
					registClass(phoneStr);	
				}else{
					Toast.makeText(RegistClassActivity.this, getResources().getString(R.string.prompt_network), 0).show();
				}
				break;
				
			case R.id.register_class_havecode:
				contentRl.setVisibility(View.VISIBLE);
				SHOW_CONTENT = 1;
				break;
				
			case R.id.register_class_completeBtn:
				
				
				if(ValidateUtils.isNetworkConnected(RegistClassActivity.this)){
					
					String usernameStr = editUsername.getText().toString().trim();
					if(TextUtils.isEmpty(usernameStr)){
						Toast.makeText(RegistClassActivity.this, getResources().getString(R.string.register_class_noname), 0).show();
						break;
					}else{
						registClass(usernameStr);
					}
					
				}else{
					Toast.makeText(RegistClassActivity.this, getResources().getString(R.string.prompt_network), 0).show();
				}
				break;
			
			case R.id.register_class_whatcode:
				AlertDialog.Builder builder = new AlertDialog.Builder(RegistClassActivity.this)
				.setTitle(R.string.prompt)
				.setMessage(getResources().getString(R.string.register_class_whatcode))
				.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
			           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
			           }					            
			       }); 
			
				builder.create().show();	
				break;
				
			default:
				break;
		}
	}


	public void registClass(final String usernameStr) {
		
		progressDialog = new ProgressDialog(RegistClassActivity.this);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("正在注册...");
		progressDialog.show();
		
		// TODO Auto-generated method stub
		final String codeStr = editCode.getText().toString().trim();
		final String nikinameStr = editNikiname.getText().toString().trim();
		final String locationStr = editLocation.getText().toString().trim();
		final String QQStr = editQQ.getText().toString().trim();
		final String emailStr = editEmail.getText().toString().trim();
		final String parentNameStr = editParentname.getText().toString().trim();
		final String parentPhoneStr = editParentphone.getText().toString().trim();
		final String userid = ValidateUtils.getUserid(RegistClassActivity.this);
		final String gradeStr = editGrade.getText().toString().trim();
		final String parentEmailStr = editParentemail.getText().toString().trim();
		
		if(codeStr != null && codeStr != ""){
			DbUtils dbUtils = DbUtils.create(this);
			EmodouUserInfo userInfo = new EmodouUserInfo();
			try {
				userInfo = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
				userInfo.setClasscode(codeStr);
				dbUtils.update(userInfo);
			} catch (DbException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		RequestParams params = new RequestParams();
//		params.addBodyParameter("usertype","s");
		params.addBodyParameter("role","s");
		params.addBodyParameter("userid",userid);
		params.addBodyParameter("username",usernameStr);
		params.addBodyParameter("nickname",nikinameStr);
		params.addBodyParameter("location",locationStr);
		params.addBodyParameter("qq",QQStr);
		params.addBodyParameter("email",emailStr);
		params.addBodyParameter("classcode",codeStr);
		params.addBodyParameter("parentname",parentNameStr);
		params.addBodyParameter("parentphone",parentPhoneStr);
		params.addBodyParameter("grade",gradeStr);
		params.addBodyParameter("parentemail",parentEmailStr);
		params.addBodyParameter("sex",sexCode);
		
		HttpUtils httpUtils = new HttpUtils();
		String PATH_JSON = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_SAVEINFO;
		httpUtils.send(HttpMethod.POST, PATH_JSON,params,  new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
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
//					        String parentname = (String)editParentname.getText().toString().trim();
//					        String parentphone = (String)editParentphone.getText().toString().trim();
//							
//							DbUtils db = DbUtils.create(RegistClassActivity.this);
//							
//							try {
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
//								
//								EmodouPackage emodoupackage = new EmodouPackage();
//								
//								//用户当前正在使用的包的名字和id
//								String packageid = result.getString("PackageId");
//								String packagename = result.getString("PackageName");
//								String packagemode = result.getString("PackageMode");
//								
//								emodoupackage.setPackageid(packageid);
//								emodoupackage.setPackagename(packagename);
//								emodoupackage.setPackageMode(packagemode);
//								emodoupackage.setUserid(userid);												
//								//该参数表示该体验包所属服务包的id
//								//emodoupackage.setPackageBelong(packageString);
//								
//								//声明一个包，找到上次存储的包，改为未选中
//								EmodouPackage emodoupackage2 = new EmodouPackage();
//								//将上次本地存储的包由选择改为未选，将得到的packageid所对应的包改为选择
//								emodoupackage2 = db.findFirst(Selector.from(EmodouPackage.class)
//																	  .where("state","=",Constants.PACKAGE_STATE_SELECT)
//										                              .and("userid","=",userid));
//								
//							    //如果不为空，则改为未选中，杨阳的逻辑有一个问题是，如果当服务包在上述三个属性中更改了，本地不会改，
//								//只有到换包刷新列表的时候才会变化
//								if(emodoupackage2 != null&& !emodoupackage2.getPackageid().equals(packageid)){
//									
//									emodoupackage2.setState(Constants.PACKAGE_STATE_NOT_SELECT);
//									db.update(emodoupackage2);
//									//声明一个包，找到从服务器查找到的正在学习的包
//									EmodouPackage emodoupackage3 = new EmodouPackage();
//									emodoupackage3 = db.findFirst(Selector.from(EmodouPackage.class)
//																		  .where("packageid","=",packageid)
//												                          .and("userid","=",userid));
//									if(emodoupackage3 != null){
//										
//										emodoupackage3.setState(Constants.PACKAGE_STATE_SELECT);
//										emodoupackage3.setPackagename(packagename);
//										emodoupackage3.setPackageMode(packagemode);
//										db.update(emodoupackage3);
//										
//									}else{
//										
//										emodoupackage.setState(Constants.PACKAGE_STATE_SELECT);
//										db.saveBindingId(emodoupackage);
//										
//									}
//									
//								}else if(emodoupackage2 == null){
//									//首次登陆，本地没有选中的（正在学习）的包,本地也不可能有包的记录
//									emodoupackage.setState(Constants.PACKAGE_STATE_SELECT);
//									db.saveBindingId(emodoupackage);
//								}else if(emodoupackage2.getPackageid().equals(packageid)){
//									//正在使用的包没有变化
//									emodoupackage2.setPackagename(packagename);
//									emodoupackage2.setPackageMode(packagemode);
//									db.update(emodoupackage2);
//								}
//								
//								
//								String bookid = result.getString("BookId");
//								
//								JSONObject bookinfo = result.getJSONObject("BookInfo");
//								String bookname = bookinfo.getString("bookname");
//								String bookicon = bookinfo.getString("bookicon");
//								String publishtime = bookinfo.getString("publishtime");
//								String description = bookinfo.getString("description");
//								String changetimestring = bookinfo.getString("changetime");
//								String press = bookinfo.getString("press");
//								String wordurl = bookinfo.getString("wordurl");												
//								String wordsize = bookinfo.getString("wordsize");
//								String wordChangetimeStr = bookinfo.getString("wordchangetime");
//								
//								Integer changetimeint = new Integer(0);
//								Integer wordChangetimeInt = new Integer(wordChangetimeStr);
//								
//								if(changetimestring!=null){												
//									changetimeint = Integer.parseInt(changetimestring);
//								}
//								if(wordChangetimeStr != null){
//									wordChangetimeInt = Integer.parseInt(wordChangetimeStr);
//								}
//								
//								//同上面服务包，将state为选中的书改为未选中
//								List<EmodouBook>emodoubook = db.findAll(Selector.from(EmodouBook.class)
//																				.where("state","=",Constants.BOOK_STATE_SELECT)
//																				.and("userid","=",userid));
//								
//								if(emodoubook!=null&&emodoubook.size()!=0){
//									for(int i = 0;i < emodoubook.size();i++){
//										emodoubook.get(i).setState(Constants.BOOK_STATE_NOT_SELECT);
//									}
//									db.updateAll(emodoubook);
//								}
//								
//								EmodouBook emodoubook2 = db.findFirst(Selector.from(EmodouBook.class)
//																			  .where("bookid","=",bookid)
//																			  .and("userid","=",userid)
//																			  .and("packageid","=",packageid));
//								
//								if(emodoubook2!=null){
//									emodoubook2.setBookname(bookname);
//									emodoubook2.setBookicon(bookicon);
//									emodoubook2.setPublishTime(publishtime);
//									emodoubook2.setDescription(description);
//									emodoubook2.setChangetime(changetimeint);
//									emodoubook2.setPress(press);
//									emodoubook2.setWordurl(wordurl);
//									emodoubook2.setWordsize(wordsize);
//									emodoubook2.setWordChangetime(wordChangetimeInt);
//									emodoubook2.setState(Constants.BOOK_STATE_SELECT);		
//									db.update(emodoubook2);
//								}else{
//									
//									EmodouBook emodoubook3 = new EmodouBook();
//									emodoubook3.setBookid(bookid);
//									emodoubook3.setUserid(userid);
//									emodoubook3.setPackageid(packageid);	
//									emodoubook3.setBookname(bookname);
//									emodoubook3.setBookicon(bookicon);
//									emodoubook3.setPublishTime(publishtime);
//									emodoubook3.setDescription(description);
//									emodoubook3.setChangetime(changetimeint);
//									emodoubook3.setPress(press);
//									emodoubook3.setWordurl(wordurl);
//									emodoubook3.setWordsize(wordsize);
//									emodoubook3.setWordChangetime(wordChangetimeInt);
//									emodoubook3.setState(Constants.BOOK_STATE_SELECT);
//									db.saveBindingId(emodoubook3);
//								}
//								
//								JSONArray unitarray = bookinfo.getJSONArray("unit");
//								for(int uniti=0;uniti<unitarray.length();uniti++){
//									
//									JSONObject unitobject = unitarray.getJSONObject(uniti);
//									EmodouUnit unit = new EmodouUnit();
//									String unitidstr = unitobject.getString("unitid");
//									unit.setBookid(bookid);
//									unit.setUnitid(unitidstr);
//									unit.setUnitname(unitobject.getString("unitname"));
//									unit.setUniticon(unitobject.optString("uniticon"));
//									unit.setUnitdes(unitobject.optString("description"));
//									
//									EmodouUnit unit2 = new EmodouUnit();
//									unit2 = db.findFirst(Selector.from(EmodouUnit.class)
//																 .where("unitid","=",unitidstr));
//									
//									if(unit2 == null){
//										db.saveBindingId(unit);
//									}else{
//										unit2.setUnitname(unitobject.getString("unitname"));
//										unit2.setUniticon(unitobject.optString("uniticon"));
//										unit2.setUnitdes(unitobject.optString("description"));
//										db.update(unit2);
//									}
//									
//									JSONArray readarray = unitobject.getJSONArray("read");
//									for(int readi = 0; readi < readarray.length(); readi++){
//										JSONObject classobject = readarray.getJSONObject(readi);
//										
//										
//										String classid = classobject.getString("classid");
//										String availability = classobject.getString("availability");
//										String title = classobject.getString("title");
//										String icon = classobject.getString("icon");
//										String changetimeStr = classobject.getString("changetime");
//										String resurl = classobject.getString("resurl");
//										String size = classobject.getString("size");
//										String classdescription = classobject.getString("description");
//										
//										Integer changetimeInt = new Integer(0);
//										if(changetimeStr != null){
//											changetimeInt = Integer.parseInt(changetimeStr);
//										}
//									
//										
//										EmodouClass classNow = new EmodouClass();
//										classNow = db.findFirst(Selector.from(EmodouClass.class)
//																		.where("packageid","=",packageid)
//																		.and("classid","=",classid)
//																		.and("type","=",Constants.EMODOU_TYPE_READ));												
//										
//										
//										if(classNow == null){
//											EmodouClass readclass = new EmodouClass();
//											readclass.setPackageid(packageid);
//											readclass.setBookid(bookid);
//											readclass.setUnitid(unitidstr);
//											readclass.setClassid(classid);
//											readclass.setAvailability(availability);
//											readclass.setTitle(title);
//											readclass.setIcon(icon);
//											readclass.setChangetimeInt(changetimeInt);
//											readclass.setResurl(resurl);
//											readclass.setSize(size);
//											readclass.setClassdes(classdescription);
//
//											//1代表的是读
//											readclass.setType(Constants.EMODOU_TYPE_READ);
//											db.saveBindingId(readclass);
//										}else{
//											classNow.setAvailability(availability);
//											classNow.setTitle(title);
//											classNow.setIcon(icon);
//											classNow.setChangetimeInt(changetimeInt);
//											classNow.setResurl(resurl);
//											classNow.setSize(size);
//											classNow.setClassdes(classdescription);
//											db.update(classNow);
//										}
//										
//										EmodouClassManager classmanager = new EmodouClassManager();
//										classmanager = db.findFirst(Selector.from(EmodouClassManager.class)
//																			.where("userid","=",userid)
//																			.and("classid","=",classid)
//																			.and("bookid","=",bookid)
//																			.and("type","=",Constants.EMODOU_TYPE_READ));
//										
//										if(classmanager == null){
//											EmodouClassManager classManager = new EmodouClassManager();
//											
//											classManager.setUserid(userid);
//											classManager.setClassid(classid);
//											classManager.setBookid(bookid);
//											classManager.setType(Constants.EMODOU_TYPE_READ);
//											classManager.setState(Constants.EMODOU_CLASS_STATE_NOT_LEAREN);
//											classManager.setScore("0");
//																										
//											db.saveBindingId(classManager);
//										}
//									}
//									
//									JSONArray listenarray = unitobject.getJSONArray("listen");
//									for(int listeni = 0; listeni < listenarray.length(); listeni++){
//										JSONObject classobject = listenarray.getJSONObject(listeni);						
//										
//										String classid = classobject.getString("classid");
//										String availability = classobject.getString("availability");
//										String title = classobject.getString("title");
//										String icon = classobject.getString("icon");
//										String changetimeStr = classobject.getString("changetime");
//										String resurl = classobject.getString("resurl");
//										String size = classobject.getString("size");
//										String classdescription = classobject.getString("description");
//
//										Integer changetimeInt = new Integer(0);
//										if(changetimeStr != null){
//											changetimeInt = Integer.parseInt(changetimeStr);
//										}
//										
//										EmodouClass classNow = new EmodouClass();
//										classNow = db.findFirst(Selector.from(EmodouClass.class)
//																		.where("packageid","=",packageid)
//																		.and("classid","=",classid)
//																		.and("type","=",Constants.EMODOU_TYPE_LISTEN));
//										
//										if(classNow == null){
//											EmodouClass listenclass = new EmodouClass();
//											listenclass.setPackageid(packageid);
//											listenclass.setBookid(bookid);
//											listenclass.setUnitid(unitidstr);
//											listenclass.setClassid(classid);
//											listenclass.setAvailability(availability);
//											listenclass.setTitle(title);
//											listenclass.setIcon(icon);
//											listenclass.setChangetimeInt(changetimeInt);
//											listenclass.setResurl(resurl);
//											listenclass.setSize(size);
//											listenclass.setClassdes(classdescription);										
//											//2代表的是听
//											listenclass.setType(Constants.EMODOU_TYPE_LISTEN);
//											db.saveBindingId(listenclass);
//										}else{
//											classNow.setAvailability(availability);
//											classNow.setTitle(title);
//											classNow.setIcon(icon);
//											classNow.setChangetimeInt(changetimeInt);
//											classNow.setResurl(resurl);
//											classNow.setSize(size);
//											classNow.setClassdes(classdescription);
//											db.update(classNow);
//										}
//										
//										EmodouClassManager classmanager = new EmodouClassManager();
//										classmanager = db.findFirst(Selector.from(EmodouClassManager.class)
//																			.where("userid","=",userid)
//																			.and("classid","=",classid)
//																			.and("bookid","=",bookid)
//																			.and("type","=",Constants.EMODOU_TYPE_LISTEN));
//										
//										if(classmanager == null){
//											EmodouClassManager classManager = new EmodouClassManager();
//											
//											classManager.setUserid(userid);
//											classManager.setClassid(classid);
//											classManager.setBookid(bookid);
//											classManager.setType(Constants.EMODOU_TYPE_LISTEN);
//											classManager.setState(Constants.EMODOU_CLASS_STATE_NOT_LEAREN);
//											classManager.setScore("0");
//																										
//											db.saveBindingId(classManager);
//										}
//									}
//									
//									JSONArray speakarray = unitobject.getJSONArray("speak");
//									for(int speaki = 0; speaki < speakarray.length(); speaki++){
//										JSONObject classobject = speakarray.getJSONObject(speaki);
//										
//										
//										String classid = classobject.getString("classid");
//										String availability = classobject.getString("availability");
//										String title = classobject.getString("title");
//										String icon = classobject.getString("icon");
//										String changetimeStr = classobject.getString("changetime");
//										String resurl = classobject.getString("resurl");
//										String size = classobject.getString("size");
//										String classdescription = classobject.getString("description");
//
//										Integer changetimeInt = new Integer(0);
//										if(changetimeStr != null){
//											changetimeInt = Integer.parseInt(changetimeStr);
//										}
//										
//										
//										EmodouClass classNow = new EmodouClass();
//										classNow = db.findFirst(Selector.from(EmodouClass.class)
//																		.where("packageid","=",packageid)
//																		.and("classid","=",classid)
//																		.and("type","=",Constants.EMODOU_TYPE_SPEAK));
//										
//										if(classNow == null){
//											EmodouClass speakclass = new EmodouClass();
//											speakclass.setPackageid(packageid);
//											speakclass.setBookid(bookid);
//											speakclass.setUnitid(unitidstr);
//											speakclass.setClassid(classid);
//											speakclass.setAvailability(availability);
//											speakclass.setTitle(title);
//											speakclass.setIcon(icon);
//											speakclass.setChangetimeInt(changetimeInt);
//											speakclass.setResurl(resurl);
//											speakclass.setSize(size);
//											speakclass.setClassdes(classdescription);
//											//3代表的是说
//											speakclass.setType(Constants.EMODOU_TYPE_SPEAK);
//											db.saveBindingId(speakclass);
//										}else{
//											classNow.setAvailability(availability);
//											classNow.setTitle(title);
//											classNow.setIcon(icon);
//											classNow.setChangetimeInt(changetimeInt);
//											classNow.setResurl(resurl);
//											classNow.setSize(size);
//											classNow.setClassdes(classdescription);
//											db.update(classNow);
//										}
//										
//										EmodouClassManager classmanager = new EmodouClassManager();
//										classmanager = db.findFirst(Selector.from(EmodouClassManager.class)
//																			.where("userid","=",userid)
//																			.and("classid","=",classid)
//																			.and("bookid","=",bookid)
//																			.and("type","=",Constants.EMODOU_TYPE_SPEAK));
//										
//										if(classmanager == null){
//											EmodouClassManager classManager = new EmodouClassManager();
//											
//											classManager.setUserid(userid);
//											classManager.setClassid(classid);
//											classManager.setBookid(bookid);
//											classManager.setType(Constants.EMODOU_TYPE_SPEAK);
//											classManager.setState(Constants.EMODOU_CLASS_STATE_NOT_LEAREN);
//											classManager.setScore("0");
//																										
//											db.saveBindingId(classManager);
//										}
//									}
//																						
//								}
//								
//								
//								
//							}catch(DbException e){											
//								e.printStackTrace();
//							}
							
							
							progressDialog.dismiss();
							Intent intent3 = new Intent(RegistClassActivity.this,MainActivity.class);
							intent3.setFlags(intent3.FLAG_ACTIVITY_CLEAR_TOP);
							intent3.putExtra("registerclass", "true");
							startActivity(intent3);
						}else{
							
							progressDialog.dismiss();
							String message = result.getString("Message");
							Toast.makeText(RegistClassActivity.this, getResources().getString(R.string.register_class_registFailed), 1).show();
							
						}
					}
				}catch(JSONException e){
					
					progressDialog.dismiss();
					Toast.makeText(RegistClassActivity.this, getResources().getString(R.string.register_class_registFailed), 1).show();
				}
			
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
				Toast.makeText(RegistClassActivity.this, "注册失败，网络异常", 1).show();
			}
		});
	}

}
