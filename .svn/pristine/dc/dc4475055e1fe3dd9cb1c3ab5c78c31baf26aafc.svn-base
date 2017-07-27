package com.example.emodou;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouBook;
import com.emodou.domain.EmodouClassManager;
import com.emodou.domain.EmodouPackage;
import com.emodou.domain.EmodouUserInfo;
import com.emodou.home.CourseListActivity;

import com.emodou.home.ChangeBookActivity;
import com.emodou.home.ChangePackageActivity;
import com.emodou.home.WordmainActivity;
import com.emodou.home.WordmainActivity;

import com.emodou.home.ReadActivity;


import com.emodou.login.EmodouLoginAcvitity;
import com.emodou.login.EmodouRegisterActivity;
import com.emodou.login.GetPasswordActivity;
import com.emodou.util.Constants;
import com.emodou.util.LogUtil;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.RecoverySystem.ProgressListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnTouchListener;

public class HomeActivity extends Activity implements OnTouchListener{

	private TextView actionbar;
	
	private TextView nickname, package_name, study_time, book_name;
	private ProgressBar home_progress_lisen, home_progress_speak, home_progress_read, home_progress_word;
	private RelativeLayout home_relative_read, home_relative_listen;
	private RelativeLayout home_relative_speak, home_relative_word;
	private ImageView home_read_background, home_read_icon;
	private ImageView home_listen_background, home_listen_icon;
	private ImageView home_speak_background, home_speak_icon;
	private ImageView home_word_background, home_word_icon;
	private Button changebookBtn, changePackageBtn;
	
	private String userid, packageid, bookid;
	private String changebook;//是否换书
	private String typeFirst;//是否是从登陆或者Emodoulogo进来的，进行课程信息和单词信息的同步
	private int listenTotal, readTotal, speakTotal, wordTotal;
	private int listenFinish, readFinish, speakFinish, wordFinish;
	
	private SimpleDraweeView simpleDraweeView;
	private ProgressDialog progressDialog;
	
	//loadRecord里面需要
	//private EmodouUserInfo userInfo = null;
	
	 protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 //Fresco.initialize(this);
		 setContentView(R.layout.home);
		 
		 init();
		 
		 //initView();
		 
		 count();
		 
	 }
	 
	 /*public void initView() {
		//创建SimpleDraweeView对象
        simpleDraweeView = (SimpleDraweeView) findViewById(R.id.home_headphoto);
        //创建将要下载的图片的URI
        Uri imageUri = Uri.parse("file://");
        //开始下载
        simpleDraweeView.setImageURI(imageUri);
	}*/

	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
			case R.id.home_relative_read:
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					home_read_background.setBackgroundResource(R.drawable.home_read_background_click);
					home_read_icon.setBackgroundResource(R.drawable.home_read_icon_click);
					Log.i("log", "Action_Down");
					return true;
					
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					home_read_background.setBackgroundResource(R.drawable.home_read_background_selector);
					home_read_icon.setBackgroundResource(R.drawable.home_read_icon_selector);
					Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("type", Constants.EMODOU_TYPE_READ);
					bundle.putString("bookid", bookid);
					bundle.putString("packageid", packageid);
					intent.putExtras(bundle);					
					startActivity(intent);
					Log.i("log", "Action_Up");
					return true;
					
				}else if(event.getAction() == MotionEvent.ACTION_MOVE){
					Log.i("log", "Action_Move");
					return true;
				}
				return false;
			
			case R.id.home_relative_listen:
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					home_listen_background.setBackgroundResource(R.drawable.home_listen_background_click);
					home_listen_icon.setBackgroundResource(R.drawable.home_listen_icon_click);
					Log.i("log", "Action_Down");
					return true;
					
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					home_listen_background.setBackgroundResource(R.drawable.home_listen_background_selector);
					home_listen_icon.setBackgroundResource(R.drawable.home_listen_icon_selector);
					Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("type", Constants.EMODOU_TYPE_LISTEN);
					bundle.putString("bookid", bookid);
					bundle.putString("packageid", packageid);
					intent.putExtras(bundle);					
					startActivity(intent);
					Log.i("log", "Action_Up");
					return true;
					
				}else if(event.getAction() == MotionEvent.ACTION_MOVE){
					Log.i("log", "Action_Move");
					return true;
				}
				return false;
				
			case R.id.home_relative_speak:
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					home_speak_background.setBackgroundResource(R.drawable.home_speak_background_click);
					home_speak_icon.setBackgroundResource(R.drawable.home_speak_icon_click);
					Log.i("log", "Action_Down");
					return true;
					
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					home_speak_background.setBackgroundResource(R.drawable.home_speak_background_selector);
					home_speak_icon.setBackgroundResource(R.drawable.home_speak_icon_selector);
					Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("type", Constants.EMODOU_TYPE_SPEAK);
					bundle.putString("bookid", bookid);
					bundle.putString("packageid", packageid);
					intent.putExtras(bundle);					
					startActivity(intent);
					Log.i("log", "Action_Up");
					return true;
					
				}else if(event.getAction() == MotionEvent.ACTION_MOVE){
					Log.i("log", "Action_Move");
					return true;
				}
				return false;
				
			
			case R.id.home_relative_word:
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					home_word_background.setBackgroundResource(R.drawable.home_word_background_click);
					home_word_icon.setBackgroundResource(R.drawable.home_word_icon_click);
					Log.i("log", "Action_Down");
					return true;
					
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					home_word_background.setBackgroundResource(R.drawable.home_word_background_selector);
					home_word_icon.setBackgroundResource(R.drawable.home_word_icon_selector);
					Intent intent = new Intent(HomeActivity.this, WordmainActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("type", Constants.EMODOU_TYPE_WORD);
					bundle.putString("bookid", bookid);
					bundle.putString("packageid", packageid);
					intent.putExtras(bundle);					
					startActivity(intent);
					Log.i("log", "Action_Up");
					return true;
					
				}else if(event.getAction() == MotionEvent.ACTION_MOVE){
					Log.i("log", "Action_Move");
					return true;
				}
				return false;
			
			case R.id.home_change_book:
				Intent intent = new Intent(HomeActivity.this, ChangeBookActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("type", "");
				bundle.putString("packageid", packageid);
				intent.putExtras(bundle);
				startActivity(intent);
				return true;
				
			case R.id.home_change_package:
				Intent intent2 = new Intent(HomeActivity.this, ChangePackageActivity.class);
				intent2.putExtra("packageid", packageid);
				startActivity(intent2);
//				try {
//					testAPI();
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				return true;
				
				default:
					return false;
		}		
			
	}
	
	public void init() {
		 actionbar = (TextView)findViewById(R.id.actionbar);
		 nickname = (TextView)findViewById(R.id.home_nickname);
		 package_name = (TextView)findViewById(R.id.home_packagename); 
		 study_time = (TextView)findViewById(R.id.home_studytime);
		 study_time.setText("学习时长 : "+"23.5h");
		 book_name = (TextView)findViewById(R.id.home_bookname);
		 changebookBtn = (Button)findViewById(R.id.home_change_book);
		 changebookBtn.setOnTouchListener(this);
		 changePackageBtn = (Button)findViewById(R.id.home_change_package);
		 changePackageBtn.setOnTouchListener(this);
		 
		 changebook = getIntent().getStringExtra("changebook");
		 if(changebook != null && changebook.equals("changebook")){
			 //与服务器同步目前学的书
			 setCurrentbook();
		 }
		 
		 if(typeFirst != null && typeFirst.equals("login")){
			 //与服务器同步目前的课程或者单词学习记录
			 try {
				loadRecord();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 
		 home_progress_lisen = (ProgressBar)findViewById(R.id.home_progress_listen);
		 home_progress_speak = (ProgressBar)findViewById(R.id.home_progress_speak);
		 //home_progress_speak.setProgress(100);
		 home_progress_read = (ProgressBar)findViewById(R.id.home_progress_read);
		 //home_progress_read.setProgress(0);
		 home_progress_word = (ProgressBar)findViewById(R.id.home_progress_word);
		 //home_progress_word.setProgress(30);
		 
		 home_relative_read = (RelativeLayout)findViewById(R.id.home_relative_read);
		 home_relative_read.setOnTouchListener(this);
		 
		 home_relative_listen = (RelativeLayout)findViewById(R.id.home_relative_listen);
		 home_relative_listen.setOnTouchListener(this);
		 
		 home_relative_speak = (RelativeLayout)findViewById(R.id.home_relative_speak);
		 home_relative_speak.setOnTouchListener(this);
		 
		 home_relative_word = (RelativeLayout)findViewById(R.id.home_relative_word);
		 home_relative_word.setOnTouchListener(this);
		 
		 home_read_background = (ImageView)findViewById(R.id.home_read_background);
		 home_read_icon = (ImageView)findViewById(R.id.home_read_icon);
		 
		 home_listen_background = (ImageView)findViewById(R.id.home_listen_background);
		 home_listen_icon = (ImageView)findViewById(R.id.home_listen_icon);
		 
		 home_speak_background = (ImageView)findViewById(R.id.home_speak_background);
		 home_speak_icon = (ImageView)findViewById(R.id.home_speak_icon);
		 
		 home_word_background = (ImageView)findViewById(R.id.home_word_background);
		 home_word_icon = (ImageView)findViewById(R.id.home_word_icon);
		 
		 
	}
	
	public void count() {
		DbUtils dbUtils = DbUtils.create(this);
		try{
			 EmodouUserInfo userInfo = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
			 userid = userInfo.getUserid();
			 if(userInfo!=null && userInfo.getNikiname().equals("")){
				 nickname.setText("Hi , "+userInfo.getPhone());
			 }else{
				 nickname.setText("Hi , "+userInfo.getNikiname());
			 }
			 
			 EmodouPackage userPackage = dbUtils.findFirst(Selector.from(EmodouPackage.class)
					 											   .where("state","=",Constants.PACKAGE_STATE_SELECT)
					 											   .and("userid","=",userid));
			 if(userPackage != null){
				 packageid = userPackage.getPackageid();
				 package_name.setText(userPackage.getPackagename());
			 }else{
				 AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this)
					.setTitle("提示")
					.setMessage("请先选择学习包")
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
				           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
				           }					            
				       }); 
				
				builder.create().show();
			 }
			 
			 EmodouBook userBook = dbUtils.findFirst(Selector.from(EmodouBook.class)
					 										 .where("state","=",Constants.BOOK_STATE_SELECT)
					 										 .and("packageid","=",packageid)
					 										 .and("userid","=",userid));
			 if(userBook != null){
				 book_name.setText(userBook.getBookname());
				 bookid = userBook.getBookid();
				 
				 listenTotal = listenFinish = readTotal = readFinish = speakTotal = speakFinish = 0;
				 wordTotal = wordFinish = 0;
				 List<EmodouClassManager> classManagerList = dbUtils.findAll(Selector.from(EmodouClassManager.class)
						 															.where("bookid","=",bookid)
						 															.and("userid","=",userid));
				 if(classManagerList != null && classManagerList.size() != 0){
					 for(int i = 0; i<classManagerList.size(); i++){
						 EmodouClassManager classManager = classManagerList.get(i);
						 if(classManager.getType().equals(Constants.EMODOU_TYPE_LISTEN)){
							 listenTotal++;
							 if(classManager.getState() == Constants.EMODOU_CLASS_STATE_LEARENED){
								 listenFinish++;
							 }
						 }
						 if(classManager.getType().equals(Constants.EMODOU_TYPE_READ)){
							 readTotal++;
							 if(classManager.getState() == Constants.EMODOU_CLASS_STATE_LEARENED){
								 readFinish++;
							 }
						 }
						 if(classManager.getType().equals(Constants.EMODOU_TYPE_SPEAK)){
							 speakTotal++;
							 if(classManager.getState() == Constants.EMODOU_CLASS_STATE_LEARENED){
								 speakFinish++;
							 }
						 }
						 if(classManager.getType().equals(Constants.EMODOU_TYPE_WORD)){
							 wordTotal++;
							 if(classManager.getState() == Constants.EMODOU_CLASS_STATE_LEARENED){
								 wordFinish++;
							 }
						 }
					 }
				 }
				 
				 if(listenTotal != 0){
					 home_progress_lisen.setProgress((int) (100 * listenFinish / listenTotal));
				 }
				 if(readTotal != 0){
					 home_progress_read.setProgress((int) (100 * readFinish / readTotal));
				 }
				 if(speakTotal != 0){
					 home_progress_speak.setProgress((int) (100 * speakFinish / speakTotal));
				 }
				 if(wordTotal != 0){
					 home_progress_word.setProgress((int) (100 * wordFinish / wordTotal));
				 }
			 }
			 
		 }catch(DbException e){
			 e.printStackTrace();
		 }
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		count();
		super.onResume();
	}
	
	public void setCurrentbook() {
		HttpUtils httpUtils = new HttpUtils();
		String CITY_PATH_JSON = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_SYC_CURRENTBOOK;
		RequestParams params = new RequestParams();
		params.addBodyParameter("bookid",bookid);
		params.addBodyParameter("ticket",ValidateUtils.getTicket(this));
		params.addBodyParameter("userid",userid);
		params.addBodyParameter("packageid",packageid);
		
		httpUtils.send(HttpRequest.HttpMethod.POST, CITY_PATH_JSON, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						// TODO Auto-generated method stub
						System.out.println(responseInfo.result.toString());
						
						try{
							String res = responseInfo.result.toString();
							res = res.substring(res.indexOf("{"));
							
							JSONObject result = new JSONObject(res);
							String status = (String)result.get("Status");
							
							if(status.equals("Failed")){
								String message = result.getString("Message");
								if(message.equals("Error_Wrong_Ticket")){
									Toast.makeText(HomeActivity.this,
											getResources().getString(R.string.home_changebook_wrongticket), 1);
								}else if(message.equals("Error_Invalid_Book")){
									Toast.makeText(HomeActivity.this, 
											getResources().getString(R.string.home_changebook_wrongbook), 1);
								}
								
							}
						}catch(JSONException e){
							Toast.makeText(HomeActivity.this, 
									getResources().getString(R.string.home_changebook_setFailed2), 1).show();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// TODO Auto-generated method stub
						Toast.makeText(HomeActivity.this, 
								getResources().getString(R.string.home_changebook_setFailed1), 1).show();
					}
			
				});
		
	}
	
	public void testAPI() throws JSONException{
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid","100165");
		params.addBodyParameter("activationcode", "ESbNSgRPc");
		HttpUtils httpUtils = new HttpUtils();
		String CITY_PATH_JSON = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_ACTIVATE;
		httpUtils.send(HttpMethod.POST, CITY_PATH_JSON, params, new RequestCallBack<String>() {

			@Override

			public void onSuccess(final ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				//String res = responseInfo.result.toString();
				
				
				try {
					String res = responseInfo.result.toString();
					int index = res.indexOf("{");
					res = res.substring(index);
					JSONObject result = new JSONObject(res);
					ValidateUtils.WriteTxtFile(result.toString(), Constants.JS_ACTIVATE + "2");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();  
				}
				
				
				
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				Toast.makeText(HomeActivity.this, Constants.JS_ACTIVATE + "失败", 0).show();
			}
			
		});
	}
	
	public void loadRecord() throws JSONException {
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Loading...");
		progressDialog.show();
		
		final DbUtils dbUtils = DbUtils.create(this);
		EmodouUserInfo userInfo;
		try {
			userInfo = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
			if(userInfo != null){
				RequestParams upRecord = new RequestParams();
				//JSONObject upRecord = new JSONObject();
				upRecord.addBodyParameter("userid", userid);
				upRecord.addBodyParameter("flag_up", "0");
				upRecord.addBodyParameter("lasttime", userInfo.getLastRecordTime());
				
				JSONArray returnTypeArray = new JSONArray();
				//returnTypeArray.put(0, "time");
				returnTypeArray.put(0, "class");
				LogUtil.d("returnTypeArray", returnTypeArray.toString());
				
				upRecord.addBodyParameter("return_type", returnTypeArray.toString());
				upRecord.addBodyParameter("ticket", userInfo.getTicket());
				
				
				HttpUtils httpUtils = new HttpUtils();
				String path = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_STUDY_RECORD;
				httpUtils.send(HttpMethod.POST, path, upRecord, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						// TODO Auto-generated method stub
						String res = responseInfo.result.toString();
						ValidateUtils.WriteTxtFile(res, "HomeRecord");
						try{
							JSONObject result = new JSONObject(res);
							String status = result.getString("Status");
							if(status.equals("Success")){
								String time = result.getString("Time");
								EmodouUserInfo userInfo2 = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
								userInfo2.setLastRecordTime(time);
								dbUtils.update(userInfo2);
								
								String record = result.getString("Record");
								JSONObject recordObject = new JSONObject(record);
//								
//								JSONObject timeRecordObject = new JSONObject();
//								timeRecordObject = recordObject.getJSONObject("TimeRecord");
//								JSONObject weeklyObject = timeRecordObject.getJSONObject("Weekly");
//								String monday = weeklyObject.getString("monday");
//								String tuesday = weeklyObject.getString("tuesday");
//								String wednesday = weeklyObject.getString("wednesday");
//								String thursday = weeklyObject.getString("thursday");
//								String friday = weeklyObject.getString("friday");
//								String saturday = weeklyObject.getString("saturday");
//								String sunday = weeklyObject.getString("sunday");
								
								JSONArray classRecord = recordObject.getJSONArray("classrecord");
								for(int i = 0; i<classRecord.length(); i++){
									JSONObject classRecordObject =  (JSONObject) classRecord.get(i);
									String bookid = classRecordObject.getString("bookid");
									String classid = classRecordObject.getString("classid");
									String status1 = classRecordObject.getString("status");
									String score = classRecordObject.getString("score");
									
									EmodouClassManager classManager = new EmodouClassManager();
									classManager = dbUtils.findFirst(Selector.from(EmodouClassManager.class)
											                                 .where("userid","=",userid)
											                                 .and("bookid","=",bookid)
											                                 .and("classid","=",classid));
									if(classManager != null){
										classManager.setState(Integer.parseInt(status1));
										classManager.setScore(score);
										dbUtils.update(classManager);
									}
								}
								Toast.makeText(HomeActivity.this, R.string.record_home_download_success, 0).show();
								progressDialog.dismiss();
							}else{
								Toast.makeText(HomeActivity.this, R.string.record_home_download_failed, 0).show();
								progressDialog.dismiss();
							}				
						}catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// TODO Auto-generated method stub
						LogUtil.e("uploadfail", msg);
						Toast.makeText(HomeActivity.this, R.string.record_home_download_failed, 0).show();
						progressDialog.dismiss();
					}
				});
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
