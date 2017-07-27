package com.emodou.home;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.kymjs.kjframe.database.SqlBuilder;

import com.emodou.domain.EmodouBook;
import com.emodou.domain.EmodouClassManager;
import com.emodou.domain.EmodouUserInfo;
import com.emodou.domain.EmodouWordClass;
import com.emodou.domain.EmodouWordInfo;
import com.emodou.domain.EmodouWordManager;
import com.emodou.domain.EmodouWordUnit;
import com.emodou.extend.MyProgressBar;
import com.emodou.util.Constants;
import com.emodou.util.LogUtil;
import com.emodou.util.ValidateUtils;
import com.example.emodou.HomeActivity;
import com.example.emodou.MainActivity;
import com.example.emodou.R;
import com.iflytek.cloud.InitListener;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.backup.BackupAgent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WordmainActivity extends Activity implements OnClickListener{
	
	
	//actionbar及其相关
	private ActionBar actionbar;
	private TextView titletext;
	private ImageButton imbReturn;
	private RelativeLayout rl_color;
	private ImageButton deleteTotal;
	
	
	//界面属性
	private ImageButton btnGo;
	private String bookid, userid, type;
	private HoloCircularProgressBar cirprogress;
	private TextView bookname, learnedWordTv, totalWordTv, neWordCount;
	private Button btnGotolist, btnPrepare;
	private MyProgressBar downprogress;
	private String url;
	private RelativeLayout newRl;
	private EditText editSearch;
	private ProgressDialog progressDialog;
		
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.word_main);
		 
		bookid = getIntent().getStringExtra("bookid");
		type = "4";
		
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
 		deleteTotal = (ImageButton)view.findViewById(R.id.courselist_imbtn_delete);
 		deleteTotal.setOnClickListener(this);
 		
 		
 		rl_color.setBackgroundColor(getResources().getColor(R.color.actionbar_word));
 		titletext.setText("单    词");		
 	}
	
	
	public void init(){
		
		btnGo = (ImageButton)findViewById(R.id.word_main_go);
		btnGo.setOnClickListener(this);
		btnGotolist = (Button)findViewById(R.id.word_main_golist);
		btnGotolist.setOnClickListener(this);
		btnPrepare = (Button)findViewById(R.id.word_main_prepare);
		
		cirprogress = (HoloCircularProgressBar)findViewById(R.id.word_main_cirprogress);
		cirprogress.setThumbEnabled(false);
		cirprogress.setMarkerEnabled(false);
		cirprogress.setProgressBackgroundColor((int) Color.rgb(206, 206, 206));
		cirprogress.setProgressColor((int) Color.rgb(211, 93, 189));
		cirprogress.setWheelSize(28);
		
		downprogress = (MyProgressBar)findViewById(R.id.word_main_downprogress);
		downprogress.setTextSize(80);
		
		newRl = (RelativeLayout)findViewById(R.id.word_main_newRl);
		newRl.setOnClickListener(this);
		
		bookname = (TextView)findViewById(R.id.word_main_bookname);
		learnedWordTv = (TextView)findViewById(R.id.word_main_learnedword);
		totalWordTv = (TextView)findViewById(R.id.word_main_totalword);
		neWordCount = (TextView)findViewById(R.id.word_main_newordCount);
		editSearch = (EditText)findViewById(R.id.word_main_find_edt);
		editSearch.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				Intent intent2 = new Intent(WordmainActivity.this, WordSearchActivity.class);
				intent2.putExtra("bookid", bookid);
				startActivity(intent2);
				return true;
			}
		});
		
		
		btnGotolist.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
					
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					if(btnGo.VISIBLE == View.GONE && btnGotolist.VISIBLE == View.VISIBLE){
						btnGotolist.setVisibility(View.GONE);
						btnPrepare.setVisibility(View.VISIBLE);
					}
				}
				return false;
			}
		});
		
		//如果存在1.json说明已经解压过了，按钮设置为进入背词列表
		if(ValidateUtils.isExist("/sdcard/emodou/"+type+"/"+bookid+"/1.json")){
			btnGo.setVisibility(View.GONE);
			btnGotolist.setVisibility(View.VISIBLE);
		}
		
	}
	
	public void count() {
		//得到用户信息和书本信息
		DbUtils dbUtils = DbUtils.create(this);
		try{
			EmodouUserInfo userInfo = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
			if(userInfo!=null){
				userid = userInfo.getUserid();
			}
			
			//获取课本信息 设置需要显示的信息
			EmodouBook bookentity = dbUtils.findFirst(Selector.from(EmodouBook.class)
												.where("bookid","=",bookid));
			bookname.setText(bookentity.getBookname());
			List<EmodouWordManager> wordManagerList = new ArrayList<EmodouWordManager>();
			wordManagerList = dbUtils.findAll(Selector.from(EmodouWordManager.class)
					                                  .where("userid","=",userid)
					                                  .and("bookid","=",bookid));
			float totalWord = 0;
			float reviewedWord = 0;
			if(wordManagerList== null){
				learnedWordTv.setText("掌握" + 0 + "词");
				totalWordTv.setText("共" + 0 + "词");
			}else{
				for(EmodouWordManager wordManager : wordManagerList){
					totalWord ++ ;
					if(wordManager.getReviewState() == Constants.WORD_REVIEW_STATE_RIGHT)
						reviewedWord ++;
				}
				cirprogress.setProgress(reviewedWord / totalWord);
				
				learnedWordTv.setText("掌握" + (int)reviewedWord + "词");
				totalWordTv.setText("共" + (int)totalWord + "词");
			}
			
			
			String sql = "SELECT * FROM com_emodou_domain_EmodouWordManager as m " +
					     "where m.userid = " + userid +
			             " and m.isAddToNewWordsBood = "+Constants.WORD_ISADDTONEW_YES;
			Cursor cursor = dbUtils.execQuery(sql);
			int num = cursor.getCount();
			neWordCount.setText("共"+num+"词");
		}catch(DbException e){
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//掌握单词数等是实时更新的，所以要放到resume中
		count();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
			case R.id.word_main_go:
				//区分是否有网络
				if(ValidateUtils.isNetworkConnected(this)){
					//如果是无线网的环境下，自动下载，否则，要对用户进行提示
					if(ValidateUtils.isWifiConnected(this)){
						//没有下载单词包，下载单词包
						if(btnGo.VISIBLE == View.VISIBLE){
							
							LoadAndUnzipFile();
						
						}
						break;
					}else{
						//没有无线网，是用自己手机的4G
						AlertDialog.Builder builder = new AlertDialog.Builder(WordmainActivity.this)
						.setTitle(R.string.prompt)
						.setMessage(getResources().getString(R.string.word_main_nowifi))
						.setCancelable(false)
						.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
					           public void onClick(DialogInterface dialog, int whichButton) {
					        	   LoadAndUnzipFile();
					           }					            
					       });
					}
				}else{//没有网络的条件下，提示用户没有网络
					AlertDialog.Builder builder = new AlertDialog.Builder(WordmainActivity.this)
					.setTitle(R.string.prompt)
					.setMessage(getResources().getString(R.string.word_main_nonetwork))
					.setCancelable(false)
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
				           public void onClick(DialogInterface dialog, int whichButton) {
				           }					            
				       });
				}
				
				
			case R.id.word_main_golist:
				//首先判断是否下载解压包
				if(btnGotolist.VISIBLE == View.VISIBLE){
					progressDialog = new ProgressDialog(this);
					progressDialog.setIndeterminate(true);
					progressDialog.setMessage("Loading...");
					progressDialog.show();
					DbUtils dbUtils = DbUtils.create(this);
					try{
						List<EmodouWordClass> wordClassentity = dbUtils.findAll(Selector.from(EmodouWordClass.class)
																				.where("bookid","=",bookid)
																				.and("userid","=",userid));
						if(wordClassentity!=null && wordClassentity.size()!=0){
							//已经下载并且存到本地数据库，已经学过了，则这时应该将shareprefrence中状态设置为直接开始学习，而非跳转到列表
							//将之前选择的state在数据库中清空
							//下面的逻辑要改
							for(int i = 0; i < wordClassentity.size(); i++){
								wordClassentity.get(i).setState(Constants.WORD_CLASS_NOT_SELECT);	
								dbUtils.update(wordClassentity.get(i));
							}
							Intent intent1 = new Intent(this,WordlistActivity.class);
							intent1.putExtra("bookid", bookid);
							startActivity(intent1);
//							progressDialog.dismiss();
						}else{
							//已经下载但没有存到本地数据库，这时还没学过，则直接跳转到单词单元列表
							if(ValidateUtils.isExist("/sdcard/emodou/" + type + "/" + bookid + "/1.JSON")){
								btnPrepare.setVisibility(View.VISIBLE);
								btnGotolist.setVisibility(View.GONE);
								
								//解析json文件并保存到数据库
								analyWordjson();
								Intent intent2 = new Intent(this,WordlistActivity.class);
								intent2.putExtra("bookid", bookid);
								startActivity(intent2);					
//								progressDialog.dismiss();
							}	
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
//				progressDialog.dismiss();
				break;
				
			case R.id.courselist_imbtn_delete:
				try{
					
					
					ValidateUtils.deleteDirectory("/sdcard/emodou/"+ type + "/" + bookid);
					if(ValidateUtils.isExist("/sdcard/emodou/"+ type + "/" + bookid)){
						//删除失败
						AlertDialog.Builder builder = new AlertDialog.Builder(WordmainActivity.this)
						.setTitle(R.string.prompt)
						.setMessage("抱歉，删除失败！")
						.setCancelable(false)
						.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
					           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
					           }					            
					       }); 
					
						builder.create().show();
					}else {
						//删除成功
						//删除失败
						AlertDialog.Builder builder2 = new AlertDialog.Builder(WordmainActivity.this)
						.setTitle(R.string.prompt)
						.setMessage("恭喜您，删除成功！")
						.setCancelable(false)
						.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
					           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
					           }					            
					       }); 
					
						builder2.create().show();
						btnGotolist.setVisibility(View.GONE);
						btnGotolist.setClickable(false);
						btnPrepare.setVisibility(View.GONE);
						btnPrepare.setClickable(false);
						btnGo.setVisibility(View.VISIBLE);
						btnGo.setClickable(true);
						
						//删除成功后，修改课本下载状态为未下载
						DbUtils dbUtils = DbUtils.create(WordmainActivity.this);
						try{
							EmodouBook bookentity = dbUtils.findFirst(Selector.from(EmodouBook.class)
																			  .where("bookid","=",bookid));
							if(bookentity!=null){
								bookentity.setWordstate(Constants.EMODOU_CLASS_DOWNLOAD_STATE_NOT_DOWNLOAD);
								dbUtils.update(bookentity);
								
							}
						}catch(DbException e){
							e.printStackTrace();
						}
					}
					
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
				break;
				
			case R.id.courselist_imbtn_return:
				Intent intent = new Intent(this, MainActivity.class);
				
				intent.putExtra("bookid", bookid);
				//intent.addFlags(Intent.);
				startActivity(intent);
				//onBackPressed();
				break;
				
			case R.id.word_main_newRl:
				Intent intent2 = new Intent(WordmainActivity.this, WordNewordlistActivity.class);
				intent2.putExtra("userid", userid);
				intent2.putExtra("bookid", bookid);
				startActivity(intent2);
				break;
				
				
			default:
				break;
		}
	}
	
	public String readFileSdcardFile(String fileName) throws IOException {
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(fileName);

			int length = fin.available();

			byte[] buffer = new byte[length];
			fin.read(buffer);

			res = EncodingUtils.getString(buffer, "UTF-8");

			fin.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public void analyWordjson() {
		
		String wordjsonStr = "";
		try{
			wordjsonStr = readFileSdcardFile("/sdcard/emodou/" + type + "/"+ bookid + "/1.JSON");
		}catch(IOException e){
			e.printStackTrace();
		}
		
		//存储到本地数据库中
		DbUtils worDbUtils = DbUtils.create(WordmainActivity.this);
		try{
			JSONObject wordJsonObject = new JSONObject(wordjsonStr);
			
			String typeStr = wordJsonObject.getString("Type");
			String bookidStr = wordJsonObject.getString("Bookid");
			
			//读第二组包括lesson的json数组
			JSONArray unitArray = wordJsonObject.getJSONArray("Unit");
			for(int i = 0;i<unitArray.length();i++){
				JSONObject bookObject = unitArray.getJSONObject(i);
				String unitidStr = bookObject.getString("unitid");
				String unitnameStr = bookObject.getString("unitname");
				
				
				EmodouWordUnit neWordUnit = new EmodouWordUnit();
				neWordUnit.setBookid(bookid);
				neWordUnit.setUnitid(unitidStr);
				neWordUnit.setUnitname(unitnameStr);
				
				EmodouWordUnit wordUnit = worDbUtils.findFirst(Selector.from(EmodouWordUnit.class)
						  .where("unitid", "=", unitidStr)
						  .and("unitname", "=", unitnameStr));
				
				
				if(wordUnit == null){			
					worDbUtils.saveBindingId(neWordUnit);
				}else {
					wordUnit.setBookid(bookid);
					wordUnit.setUnitid(unitidStr);
					wordUnit.setUnitname(unitnameStr);
					worDbUtils.update(wordUnit);
				}
								
				JSONArray wordClassArray = bookObject.getJSONArray("wordclass");
				for(int k = 0; k < wordClassArray.length();k++){
					JSONObject wordClassObject = wordClassArray.getJSONObject(k);
					
					String classidStr = wordClassObject.getString("classid");
					String titleStr = wordClassObject.getString("title");
					
					EmodouWordClass wordClass = new EmodouWordClass();					
					wordClass.setBookid(bookidStr);
					wordClass.setClassid(classidStr);
					wordClass.setTitle(titleStr);
					wordClass.setUserid(userid);
					wordClass.setState(Constants.WORD_CLASS_NOT_SELECT);
					wordClass.setUnitid(unitidStr);
					
					JSONArray wordArray = wordClassObject.getJSONArray("word");
					wordClass.setSize(wordArray.length());
								
					EmodouWordClass wordClassentity = worDbUtils.findFirst(Selector.from(EmodouWordClass.class)
																		   .where("classid","=",classidStr)
																		   .and("userid","=",userid));
					if(wordClassentity == null){
						worDbUtils.saveBindingId(wordClass);
					}else{
						wordClassentity.setBookid(bookidStr);
						wordClassentity.setClassid(classidStr);
						wordClassentity.setTitle(titleStr);
						wordClassentity.setUserid(userid);
						wordClassentity.setState(Constants.WORD_CLASS_NOT_SELECT);
						wordClassentity.setUnitid(unitidStr);
						worDbUtils.update(wordClassentity);
					}
					
					EmodouClassManager classManager = new EmodouClassManager();
					classManager.setBookid(bookidStr);
					classManager.setClassid(classidStr);
					classManager.setUserid(userid);
					classManager.setType(Constants.EMODOU_TYPE_WORD);
					classManager.setState(Constants.EMODOU_CLASS_STATE_NOT_LEAREN);
					classManager.setScore("0");
					EmodouClassManager classManagerFind = worDbUtils.findFirst(Selector.from(EmodouClassManager.class)
																					   .where("classid","=","classid")
																					   .and("bookid","=",bookidStr)
																					   .and("userid","=",userid));
					if(classManagerFind == null){
						worDbUtils.saveBindingId(classManager);
					}else{
						classManagerFind.setType(Constants.EMODOU_TYPE_WORD);
						classManagerFind.setState(Constants.EMODOU_CLASS_STATE_NOT_LEAREN);
						classManagerFind.setScore("0");
						worDbUtils.update(classManagerFind);
					}
					
					for(int m = 0; m < wordArray.length(); m++){
						JSONObject wordObject = wordArray.getJSONObject(m);
						
						EmodouWordInfo wordentity = worDbUtils.findFirst(Selector.from(EmodouWordInfo.class)
																			.where("word_name","=",wordObject.getString("word_name")));
						
						if(wordentity == null){
							EmodouWordInfo wordInfo = new EmodouWordInfo();
							wordInfo.setClassid(classidStr);
							wordInfo.setWord_name(wordObject.getString("word_name"));
							wordInfo.setWord_pl(wordObject.getString("word_pl"));
							wordInfo.setWord_past(wordObject.getString("word_past"));
							wordInfo.setWord_done(wordObject.getString("word_done"));
							wordInfo.setWord_ing(wordObject.getString("word_ing"));
							wordInfo.setWord_third(wordObject.getString("word_third"));
							wordInfo.setWord_er(wordObject.getString("word_er"));
							wordInfo.setWord_est(wordObject.getString("word_est"));
							wordInfo.setSentence(wordObject.getString("sentence"));
							wordInfo.setVoice(wordObject.getString("voice"));
							wordInfo.setLocal_voice("/emodou/" + type  + "/" + bookid + "/" + wordObject.getString("local_voice"));
							wordInfo.setPh_am(wordObject.getString("ph_am"));
							wordInfo.setPh_en(wordObject.getString("ph_en"));
							wordInfo.setMeaning(wordObject.getString("meaning"));
							
							
							worDbUtils.saveBindingId(wordInfo);
							
						}else {
							wordentity.setClassid(classidStr);
							wordentity.setWord_name(wordObject.getString("word_name"));
							wordentity.setWord_pl(wordObject.getString("word_pl"));
							wordentity.setWord_past(wordObject.getString("word_past"));
							wordentity.setWord_done(wordObject.getString("word_done"));
							wordentity.setWord_ing(wordObject.getString("word_ing"));
							wordentity.setWord_third(wordObject.getString("word_third"));
							wordentity.setWord_er(wordObject.getString("word_er"));
							wordentity.setWord_est(wordObject.getString("word_est"));
							wordentity.setSentence(wordObject.getString("sentence"));
							wordentity.setVoice(wordObject.getString("voice"));
							wordentity.setLocal_voice("/emodou/" + type  + "/" + bookid + "/" + wordObject.getString("local_voice"));
							wordentity.setPh_am(wordObject.getString("ph_am"));
							wordentity.setPh_en(wordObject.getString("ph_en"));
							wordentity.setMeaning(wordObject.getString("meaning"));
							worDbUtils.update(wordentity);
						}
						
						EmodouWordManager wordManager = worDbUtils.findFirst(Selector.from(EmodouWordManager.class)
								                                             .where("wordname","=", wordObject.getString("word_name"))
								                                             .and("userid", "=", userid)
								                                             .and("classid","=",classidStr));
						
						
						if(wordManager == null){
							EmodouWordManager neWordManager = new EmodouWordManager();
							neWordManager.setUserid(userid);
							neWordManager.setIsAddToNewWordsBood("0");
							neWordManager.setEntitleRighttimes(0);
							neWordManager.setEntitleWrongtimes(0);
							neWordManager.setCntitleRighttimes(0);
							neWordManager.setCntitleWrongtimes(0);
							neWordManager.setWordname(wordObject.getString("word_name"));
							neWordManager.setClassid(classidStr);
							neWordManager.setLearnState(0);
							neWordManager.setReviewState(0);
							neWordManager.setLastState(0);
							neWordManager.setLastStateTime(0);
							neWordManager.setBookid(bookidStr);					
							neWordManager.setCright(0);
							neWordManager.setCprompt(0);
							neWordManager.setCwrong(0);
							neWordManager.setLast("0");
													
							worDbUtils.saveBindingId(neWordManager);
						}
					}
				}
				
				
								
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void LoadAndUnzipFile() {
		DbUtils dbUtils2 = DbUtils.create(this);
		try{
			EmodouBook bookentitiy2 = dbUtils2.findFirst(Selector.from(EmodouBook.class)
																	 .where("bookid","=",bookid));
			url = Constants.EMODOU_RESURL+"/"+bookentitiy2.getWordurl();
			System.out.printf("wordurl",url);
			}catch(DbException e){
				e.printStackTrace();
			}
		HttpUtils http = new HttpUtils();
		http.getHttpClient().getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 300000);
		btnGo.setVisibility(View.GONE);
		btnGo.setClickable(false);
		downprogress.setVisibility(View.VISIBLE);
		HttpHandler hanlder;
		hanlder = http.download(url, "/sdcard/emodou/" + type
				+ "/" + bookid + "/" + bookid + ".zip", true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将重新下载。
				true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
				new RequestCallBack<File>() {
					public void onStart() {
						btnGo.setVisibility(View.GONE);
						btnGo.setClickable(false);
						downprogress.setVisibility(View.VISIBLE);
						super.onStart();
					}
					
					public void onLoading(long total, long current, boolean isUploading){
						System.out.println((int)(100*current/total));
						downprogress.setProgress((int)(100*current/total));
						downprogress.setText((int)(100*current/total)+"");
					}
					public void onSuccess(ResponseInfo<File> responseInfo) {
						
						//下载成功后，修改课本下载状态为已下载
						DbUtils dbUtils = DbUtils.create(WordmainActivity.this);
						try{
							EmodouBook bookentity = dbUtils.findFirst(Selector.from(EmodouBook.class)
																			  .where("bookid","=",bookid));
							if(bookentity!=null){
								bookentity.setWordstate(Constants.EMODOU_CLASS_DOWNLOAD_STATE_ALREADY_DOWNLOAD);
								dbUtils.update(bookentity);
								
							}
						}catch(DbException e){
							e.printStackTrace();
						}
						
						//接下来解压下载的包
						File zipFile = new File("/sdcard/emodou/"+ type + "/" + bookid + "/"+ bookid + ".zip");
						try{
							ValidateUtils.unzipFiles3(zipFile, "/sdcard/emodou/"+ type + "/" + bookid);
						}catch(Exception e){
							e.printStackTrace();
						}
						btnGotolist.setVisibility(View.VISIBLE);
						btnGotolist.setClickable(true);
						downprogress.setVisibility(View.GONE);
					}
					
					public void onFailure(HttpException error, String msg) {
						if (msg.equals("maybe the file has downloaded completely")) {
							btnGotolist.setVisibility(View.VISIBLE);
							btnGotolist.setClickable(true);
							downprogress.setVisibility(View.GONE);
							Toast.makeText(WordmainActivity.this,"文件已下载", 0).show();
						}

						System.out.println(msg);											
					}
			});
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, MainActivity.class);
		
		intent.putExtra("bookid", bookid);
		//intent.addFlags(Intent.);
		startActivity(intent);
		super.onBackPressed();
	}

}
