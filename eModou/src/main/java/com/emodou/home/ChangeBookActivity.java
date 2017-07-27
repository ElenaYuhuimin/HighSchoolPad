package com.emodou.home;

//E_getpackageinfo.php

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouBook;
import com.emodou.domain.EmodouClass;
import com.emodou.domain.EmodouClassManager;
import com.emodou.domain.EmodouPackage;
import com.emodou.domain.EmodouUnit;
import com.emodou.util.Constants;
import com.emodou.util.ValidateUtils;
import com.example.emodou.MainActivity;
import com.example.emodou.R;
import com.example.emodou.R.string;
import com.iflytek.cloud.InitListener;
import com.iflytek.ise.result.FinalResult;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class ChangeBookActivity extends Activity implements OnClickListener{
	
	private ActionBar actionBar;
	private TextView titleTv;
	private ImageButton returnBtn;
	
	private String packageid, userid, ticket;
	private Integer packChangetime;
	private List<EmodouBook> bookList = new ArrayList<EmodouBook>();
	private GridAdapter gridAdapter;
	private String upadateSucceed = "0";
	
	private TextView changeHintTv;
	private GridView bookGv;
	private ProgressDialog progressDialog;
	
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_changebook);       
        
        setActionbar();
        
        init();
        
    }
	
	public void setActionbar() {
		actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.actionbar_login);
		
		View view = actionBar.getCustomView();
		titleTv = (TextView)view.findViewById(R.id.login_ancionbar_text);
		titleTv.setText(getResources().getString(R.string.home_changebook_title));
		returnBtn = (ImageButton)view.findViewById(R.id.login_imbtn_return);
		returnBtn.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.login_imbtn_return:
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
				break;
	
			default:
				break;
		}
	}
	
	public void init() {
		
		changeHintTv = (TextView)findViewById(R.id.home_changebook_hint);
		bookGv = (GridView)findViewById(R.id.home_changebook_gridview);
		
		packageid = getIntent().getStringExtra("packageid");
		userid = ValidateUtils.getUserid(this);
		ticket = ValidateUtils.getTicket(this);
		
		DbUtils dbUtils = DbUtils.create(this);
		try{
			EmodouBook bookSelect = dbUtils.findFirst(Selector.from(EmodouBook.class)
															  .where("state","=",Constants.BOOK_STATE_SELECT)
															  .and("packageid","=",packageid)
															  .and("userid","=",userid));
			if(bookSelect == null){
				changeHintTv.setText(getResources().getString(R.string.home_changebook_bookHintNone));
			}else{
				changeHintTv.setText("当前选择"+bookSelect.getBookname()+" ， 点击课本可进行其他操作");
			}
			
			EmodouPackage packageSelect = dbUtils.findFirst(Selector.from(EmodouPackage.class)
					                                                .where("packageid","=",packageid)
					                                                .and("userid","=",userid));
			if(packageSelect != null){
				packChangetime = packageSelect.getChangetime();
			}
			
			bookList = dbUtils.findAll(Selector.from(EmodouBook.class)
											   .where("packageid","=",packageid)
											   .and("userid","=",userid));
			if(packChangetime != null &&bookList!=null && bookList.size()!=0){
				gridAdapter = new GridAdapter(bookList,this);
				bookGv.setAdapter(gridAdapter);
				//获得行数bookGv.getNumColumns()
				//获得列数bookGv.getStretchMode();
				bookGv.setOnItemClickListener(new myOnclickListener());
				
				isNeedUpdate();
			}else{
				//没有更新时间或者列表为空，则去服务器去数据
				freshBooklist();
			}
											   
		}catch(DbException e){
			e.printStackTrace();
		}
	}
	
	public class ViewHolder{
		private TextView booknameTv, progressTv;
		private ProgressBar progressBar;
		private ImageView bookBcg;
	}
	
	public class  GridAdapter extends BaseAdapter{
		
		private List<EmodouBook> booklist = new ArrayList<EmodouBook>();
		private LayoutInflater inflater;
		
		public GridAdapter(List<EmodouBook> booklist, Context context) {
			super();
			this.booklist = booklist;
			inflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return booklist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return booklist.get(position);
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
			ViewHolder viewHolder;
			if(convertView == null){
				view = inflater.inflate(R.layout.home_changebook_bookitem, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.booknameTv = (TextView)view.findViewById(R.id.home_changebook_bookitem_nameTv);
				viewHolder.bookBcg = (ImageView)view.findViewById(R.id.home_changebook_bookitem_bcg);
				viewHolder.progressTv = (TextView)view.findViewById(R.id.home_changebook_bookitem_progressTv);
				viewHolder.progressBar = (ProgressBar)view.findViewById(R.id.home_changebook_bookitem_progress);
				view.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder)view.getTag();
			}
			
			EmodouBook book = booklist.get(position);
			String bookTotalName = booklist.get(position).getBookname();
			if(bookTotalName.indexOf("[")>0){
				viewHolder.booknameTv.setText(bookTotalName.subSequence(0, bookTotalName.indexOf("[")));
			}else{
				viewHolder.booknameTv.setText(bookTotalName);
			}
			
			
			//查找每本课本的完成情况
			DbUtils dbUtils = DbUtils.create(inflater.getContext());
			try{
				List<EmodouClassManager> classList = dbUtils.findAll(Selector.from(EmodouClassManager.class)
																			 .where("bookid","=",book.getBookid())
																			 .and("userid","=",userid));
				int totalClass = 0;
				int learnedClass = 0;
				if(classList != null){
					for(int i = 0; i < classList.size(); i++){
						EmodouClassManager classManager = classList.get(i);
						if(classManager != null){
							totalClass++;
							if(classManager.getState() == Constants.EMODOU_CLASS_STATE_LEARENED)
								learnedClass++;
						}
					}			
					
				}
				
				if(learnedClass != 0){
					viewHolder.progressBar.setProgress(100*learnedClass/totalClass);
				}else{
					viewHolder.progressBar.setProgress(0);
				}
				
				if(booklist.get(position).getState() == Constants.BOOK_STATE_NOT_SELECT){
					viewHolder.bookBcg.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_changebook_bookitem_bcg_unclick));
					viewHolder.progressTv.setTextColor(getResources().getColor(R.color.home_changebook_purple));
				}else if(booklist.get(position).getState() == Constants.BOOK_STATE_SELECT){
					viewHolder.bookBcg.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_changebook_bookitem_bcg_click));
					viewHolder.progressTv.setTextColor(getResources().getColor(R.color.white));
				}
				
				
			}catch(DbException e){
				e.printStackTrace();
			}
			
			return view;
		}
		
		
		
	}
	
	//将本地的changetime
	public void isNeedUpdate() {
		
		if(ValidateUtils.isNetworkConnected(this)){	
			RequestParams params = new RequestParams();
			params.addBodyParameter("packageid",packageid);
			params.addBodyParameter("userid",userid);
			params.addBodyParameter("ticket",ticket);
			params.addBodyParameter("changetime",packChangetime.toString());
			HttpUtils httpUtils = new HttpUtils();
			String CITY_PATH_JSON = Constants.EMODOU_URL + Constants.JS_API2_START+ Constants.JS_UPDATE_PACKAGE;
			httpUtils.send(HttpRequest.HttpMethod.POST, CITY_PATH_JSON, params,
					new RequestCallBack<String>() {
				
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String res = responseInfo.result.toString();
							res = res.substring(res.indexOf("{"));
							
							try{
								JSONObject result = new JSONObject(res);
								String status = result.getString("Status");
								if(status.equals("Success")&&result.getString("Version").equals("OLD")){
									freshBooklist();
								}
								
								else if(status.equals("Failed")
										&&result.getString("Message").equals("Error_Wrong_Ticket")){
									Toast.makeText(ChangeBookActivity.this, getResources().getString(R.string.home_changebook_wrongticket), 
											Toast.LENGTH_LONG).show();
								}
							}catch(JSONException e){
								e.printStackTrace();
							}
						}
						
						@Override
						public void onFailure(HttpException error, String msg) {
	
						}
					});
		}else{
			//暂不提示用户没有网络，否则每次点开这个页面都会进行提示
		}
		
	}
	
	public void freshBooklist() {
		
		if(ValidateUtils.isNetworkConnected(this)){
			progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage("Loading...");
			progressDialog.show();
			
			
			RequestParams params = new RequestParams();
			params.addBodyParameter("userid",userid);
			params.addBodyParameter("packageid",packageid);
			params.addBodyParameter("ticket",ticket);
			HttpUtils httpUtils = new HttpUtils();
			String CITY_PATH_JSON = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_PACKAGE_INFO;
			httpUtils.send(HttpRequest.HttpMethod.POST, CITY_PATH_JSON, params,
					new RequestCallBack<String>() {
				
						public void onSuccess(ResponseInfo<String> responseInfo) {
							System.out.println(responseInfo.result.toString());
							try{
								String res = responseInfo.result.toString();
								res = res.substring(res.indexOf("{"));
								
								JSONObject result = new JSONObject(res);
								
								String status = (String)result.get("Status");
								
								if(status.equals("Success")){
									
									String packageid = (String)result.get("Packageid");
									String packagename = (String)result.get("Packagename");
									String packageicon = (String)result.get("Packageicon");//新加项
									String packChangeTime = result.getString("Changetime");
									String ifgroup = (String)result.get("Ifgroup");//是否是大客户包（1是 0不是）
									Integer packChangetimeInt = new Integer(0);
									
									if(packChangeTime!=null && !packChangeTime.equals("")){
										packChangetimeInt = Integer.parseInt(packChangeTime);
									}
									
									
									JSONArray array = result.getJSONArray("Book");
									DbUtils dbUtils = DbUtils.create(ChangeBookActivity.this);
									
									for(int i = 0; i < array.length(); i++){
										JSONObject object = (JSONObject)array.get(i);
										EmodouBook book = new EmodouBook();
										book.setBookid(object.getString("bookid"));
										book.setBookname(object.getString("bookname"));
										book.setBookicon(object.getString("bookicon"));
										book.setISBN(object.getString("ISBN"));
										book.setPublishTime(object.getString("publishtime"));
										book.setChangetime(object.getInt("changetime"));
										book.setDescription(object.getString("description"));
										book.setPackageid(packageid);
										book.setState(Constants.BOOK_STATE_NOT_SELECT);
										book.setUserid(userid);
										
										//接下来判断数据库是否已经有这本书的记录
										EmodouBook bookfind = dbUtils.findFirst(Selector.from(EmodouBook.class)
												                                        .where("bookid","=",object.getString("bookid"))
												                                        .and("packageid","=",packageid)
												                                        .and("userid","=",userid));
										if(bookfind == null){
											dbUtils.saveBindingId(book);
										}else{
											//如果有这条数据，首先需要保持他之前的选中状态，然后绑定新数据，删除旧数据
											book.setState(bookfind.getState());
											dbUtils.delete(bookfind);
											dbUtils.saveBindingId(book);
										}
									}
									
									//用新获取的时间更新本地的包的数据库
									EmodouPackage packagefind = dbUtils.findFirst(Selector.from(EmodouPackage.class)
											                                            .where("packageid","=",packageid)
											                                            .and("userid","=",userid));
									
									packagefind.setPackageid(packageid);
									packagefind.setPackagename(packagename);
									packagefind.setPackageicon(packageicon);
									packagefind.setChangetime(packChangetimeInt);
									packagefind.setIfgroup(ifgroup);
									dbUtils.update(packagefind);
									
									//从数据库中取出book的list，初始化gridAdapter
									bookList = dbUtils.findAll(Selector.from(EmodouBook.class)
																						.where("packageid","=",packageid)
																						.and("userid","=",userid)
																						.orderBy("bookid"));
									if(bookList != null){
										gridAdapter = new GridAdapter(bookList, ChangeBookActivity.this);
										bookGv.setAdapter(gridAdapter);
										bookGv.setOnItemClickListener(new myOnclickListener());
									}
									
									progressDialog.dismiss();
									
								}else{
									//Status为failed
									String message = result.getString("Message");
									if(message.equals("Error_Wrong_Package")){
										Toast.makeText(ChangeBookActivity.this, getResources().getString(R.string.home_changebook_wrongpackage),
												Toast.LENGTH_SHORT).show();
									}else if(message.equals("Error_Wrong_Ticket")){
										Toast.makeText(ChangeBookActivity.this, getResources().getString(R.string.home_changebook_wrongticket),
												Toast.LENGTH_SHORT).show();
									}else{
										Toast.makeText(ChangeBookActivity.this, getResources().getString(R.string.home_changebook_failbook),
												Toast.LENGTH_SHORT).show();
									}
									
									progressDialog.dismiss();
								}
							}catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
						}
						
						public void onFailure(HttpException error, String msg){
							System.out.println(msg + error.toString());
							progressDialog.dismiss();
						}
					});
		}else{
			AlertDialog.Builder builder = new AlertDialog.Builder(ChangeBookActivity.this)
			.setTitle(R.string.prompt)
			.setMessage(getResources().getString(R.string.home_changebook_noNetConnected))
			.setCancelable(false)
			.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
		           public void onClick(DialogInterface dialog, int whichButton) {
		        	   //直接跳转回主界面
		        	   Intent intent = new Intent(ChangeBookActivity.this, MainActivity.class);
					   startActivity(intent);
		           }					            
		       });
			builder.show();
		}
		
	}
	
//	public static final int UPDATE_BOOK = 1;
//	private Handler handler = new Handler(){
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//				case UPDATE_BOOK:
//					int position = msg.getData().getInt("position");
//					
//					break;
//	
//				default:
//					break;
//			}
//		}
//	};
	
	public class myOnclickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view,  final int position,
				long id) {
			// TODO Auto-generated method stub
			//DbUtils dbUtils = DbUtils.create(ChangeBookActivity.this);
			updateBookDetailList(position);
			//如果未选中，先将以前选中的设为未选中，然后将本书选中
			if(bookList.get(position).getState() == Constants.BOOK_STATE_NOT_SELECT){
				
//				new Thread(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						Message message = new Message();
//						message.what = UPDATE_BOOK;
//						Bundle bundle = new Bundle();
//						bundle.putInt("position", position);
//						message.setData(bundle);
//						handler.sendMessage(message);
//					}
//				}).start();
				
//				new Thread(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						updateBookDetailList(position);
//					}
//				}).start();
				
				//progressDialog.dismiss();
//				if(upadateSucceed.equals("1")){
//					try{
//						EmodouBook bookSelect = dbUtils.findFirst(Selector.from(EmodouBook.class)
//								                                          .where("state","=",Constants.BOOK_STATE_SELECT)
//								                                          .and("userid","=",userid));
//						if(bookSelect != null){
//							bookSelect.setState(Constants.BOOK_STATE_NOT_SELECT);
//							dbUtils.update(bookSelect);
//						}
//						
//						EmodouPackage packageSelect = dbUtils.findFirst(Selector.from(EmodouPackage.class)
//								                                                .where("userid","=",userid)
//								                                                .and("state","=",Constants.PACKAGE_STATE_SELECT));
//						if(packageSelect != null){
//							packageSelect.setState(Constants.PACKAGE_STATE_NOT_SELECT);
//							dbUtils.update(packageSelect);
//						}
//						
//						String bookid = bookList.get(position).getBookid();
//						EmodouBook bookNow = dbUtils.findFirst(Selector.from(EmodouBook.class)
//																	   .where("bookid","=",bookid)
//																	   .and("packageid","=",packageid)
//																	   .and("userid","=",userid));
//						if(bookNow != null){
//							bookNow.setState(Constants.BOOK_STATE_SELECT);
//							dbUtils.update(bookNow);
//						}
//						
//						EmodouPackage packageNow = dbUtils.findFirst(Selector.from(EmodouPackage.class)
//								                                             .where("userid","=",userid)
//								                                             .and("packageid","=",packageid));
//						if(packageNow != null){
//							packageNow.setState(Constants.PACKAGE_STATE_SELECT);
//							dbUtils.update(packageNow);
//						}
//						
//						Intent intent = new Intent(ChangeBookActivity.this, MainActivity.class);
//						intent.putExtra("changebook", "changebook");
//						startActivity(intent);
//					}catch (Exception e){
//						e.printStackTrace();
//					}
//				}
			}else if(bookList.get(position).getState() == Constants.BOOK_STATE_SELECT){
				Intent intent = new Intent(parent.getContext(), MainActivity.class);
				startActivity(intent);
			}
			
			
			
		}
		
		
		
	}
	
	//更新课程列表
	public void updateBookDetailList(final int position) {
		
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Loading...");
		progressDialog.show();
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("bookid",bookList.get(position).getBookid());
		params.addBodyParameter("ticket",ticket);
		params.addBodyParameter("userid",userid);
		params.addBodyParameter("withlist",Constants.WITHLIST_YES);
		HttpUtils httpUtils = new HttpUtils();
		String CITY_PATH_JSON = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_BOOK_INFO;
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
								System.out.println(result.toString());
								String status = result.getString("Status");
								DbUtils dbUtils = DbUtils.create(ChangeBookActivity.this);
								if(status.equals("Success")){
									String bookid = result.getString("BookId");
									String changetimeStr = result.getString("ChangeTime");
									Integer changetimeInteger = new Integer(0);
									if(changetimeStr!= null)
										changetimeInteger = Integer.parseInt(changetimeStr);
									
									EmodouBook book2 = dbUtils.findFirst(Selector.from(EmodouBook.class)
											                                     .where("packageid","=",packageid)
											                                     .and("userid","=",userid)
											                                     .and("state","=",Constants.BOOK_STATE_SELECT));
									if(book2 != null){
										book2.setState(Constants.BOOK_STATE_NOT_SELECT);
										dbUtils.update(book2);
									}		
									
									EmodouBook book = dbUtils.findFirst(Selector.from(EmodouBook.class)
											                                    .where("bookid","=",bookid)
											                                    .and("packageid","=",packageid)
											                                    .and("userid","=",userid));
									
									if(book != null){
										book.setBookname(result.getString("BookName"));
										book.setBookicon(result.getString("BookIcon"));
										book.setISBN(result.getString("ISBN"));
										book.setPublishTime(result.getString("PublishTime"));
										book.setChangetime(changetimeInteger);
										book.setDescription(result.getString("Description"));
										book.setWordurl(result.getString("WordUrl"));
										
										String wordChangeTimeStr = result.getString("WordChangetime");
										Integer wordChangeTimeInt = new Integer(0);
										if(wordChangeTimeStr != null && !wordChangeTimeStr.equals("")
												&&!((wordChangeTimeStr.charAt(0)+"").equals("-"))){
											wordChangeTimeInt = Integer.parseInt(wordChangeTimeStr);
											book.setWordChangetime(wordChangeTimeInt);
										}
										
										
										book.setWordsize(result.getString("WordSize"));
										book.setPress(result.getString("Press"));
										book.setState(Constants.BOOK_STATE_SELECT);
										dbUtils.update(book);
									}//不可能为空，为空用户怎么点击?
									
									
									JSONArray unitArray = result.getJSONArray("Unit");
									for(int i = 0; i<unitArray.length();i++){
										JSONObject unitObject = unitArray.getJSONObject(i);
										
										String unitidStr = unitObject.getString("unitid");
										EmodouUnit unit = new EmodouUnit();
										
										unit.setBookid(bookid);
										unit.setUnitid(unitidStr);
										unit.setUnitname(unitObject.getString("unitname"));
										unit.setUniticon(unitObject.getString("uniticon"));
										unit.setUnitdes(unitObject.getString("description"));	
										
										EmodouUnit unitFind = dbUtils.findFirst(Selector.from(EmodouUnit.class)
												                                        .where("unitid","=",unitObject.getString("unitid")));
										if(unitFind == null){
											dbUtils.saveBindingId(unit);
										}
										
										//解析阅读模块
										String readStr = unitObject.getString("read");
										if(readStr != null && !readStr.equals("")){
											JSONArray readArray = new JSONArray(readStr);
											for(int readi = 0; readi < readArray.length(); readi++){
												JSONObject classObject = readArray.getJSONObject(readi);
												
												EmodouClass readClass = new EmodouClass();
												
												String readChangetimeStr = classObject.getString("changetime");
												Integer readChangetimeInt = new Integer(0);
												if(readChangetimeStr != null)
													readChangetimeInt = Integer.parseInt(readChangetimeStr);
												
												String classidStr = classObject.getString("classid");
												readClass.setPackageid(packageid);
												readClass.setBookid(bookid);
												readClass.setUnitid(unitidStr);
												readClass.setClassid(classidStr);
												readClass.setTitle(classObject.getString("title"));
												readClass.setIcon(classObject.getString("icon"));
												readClass.setChangetimeInt(readChangetimeInt);
												readClass.setResurl(classObject.getString("resurl"));
												readClass.setSize(classObject.getString("size"));
												readClass.setClassdes(classObject.getString("description"));	
												readClass.setType(Constants.EMODOU_TYPE_READ);
												
												EmodouClass classFind = dbUtils.findFirst(Selector.from(EmodouClass.class)
														                                          .where("classid","=",classidStr)
														                                          .and("type","=",Constants.EMODOU_TYPE_READ)
														                                          .and("packageid","=",packageid));
												if(classFind == null){
													dbUtils.saveBindingId(readClass);
												}else{
													classFind.setTitle(classObject.getString("title"));
													classFind.setIcon(classObject.getString("icon"));
													classFind.setChangetimeInt(readChangetimeInt);
													classFind.setResurl(classObject.getString("resurl"));
													classFind.setSize(classObject.getString("size"));
													classFind.setClassdes(classObject.getString("description"));
													dbUtils.update(classFind);
												}
												
												//课程管理类和user有关
												EmodouClassManager classManager = dbUtils.findFirst(Selector.from(EmodouClassManager.class)
														                                                    .where("bookid","=",bookid)
														                                                    .and("classid","=",classidStr)
														                                                    .and("userid","=",unitidStr)
														                                                    .and("type","=",Constants.EMODOU_TYPE_READ));
												
												if(classManager == null){
													EmodouClassManager classManagerFind = new EmodouClassManager();
													classManagerFind.setClassid(classidStr);
													classManagerFind.setUserid(userid);
													classManagerFind.setBookid(bookid);
													classManagerFind.setType(Constants.EMODOU_TYPE_READ);
													classManagerFind.setState(Constants.EMODOU_CLASS_STATE_NOT_LEAREN);
													classManagerFind.setScore("0");
													dbUtils.saveBindingId(classManagerFind);
												}
											}
										}
										
										//解析听力模块
										String listenStr = unitObject.getString("listen");
										if(listenStr != null && !listenStr.equals("")){
											JSONArray listenArray = new JSONArray(listenStr);
											for(int listeni = 0; listeni < listenArray.length(); listeni++){
												JSONObject classObject = listenArray.getJSONObject(listeni);
												
												EmodouClass listenClass = new EmodouClass();
												String listenChangetimeStr = classObject.getString("changetime");
												Integer listenChangetimeInt = new Integer(0);
												if(listenChangetimeStr != null)
													listenChangetimeInt = Integer.parseInt(listenChangetimeStr);
												
												String classidStr = classObject.getString("classid");
												listenClass.setPackageid(packageid);
												listenClass.setBookid(bookid);
												listenClass.setUnitid(unitidStr);
												listenClass.setClassid(classidStr);
												listenClass.setTitle(classObject.getString("title"));
												listenClass.setIcon(classObject.getString("icon"));
												listenClass.setChangetimeInt(listenChangetimeInt);
												listenClass.setResurl(classObject.getString("resurl"));
												listenClass.setSize(classObject.getString("size"));
												listenClass.setClassdes(classObject.getString("description"));
												listenClass.setType(Constants.EMODOU_TYPE_LISTEN);
												
												EmodouClass classFind = dbUtils.findFirst(Selector.from(EmodouClass.class)
														                                          .where("classid","=",classidStr)
														                                          .and("type","=",Constants.EMODOU_TYPE_LISTEN)
														                                          .and("packageid","=",packageid));
												if(classFind == null){
													dbUtils.saveBindingId(listenClass);
												}else{
													classFind.setTitle(classObject.getString("title"));
													classFind.setIcon(classObject.getString("icon"));
													classFind.setChangetimeInt(listenChangetimeInt);
													classFind.setResurl(classObject.getString("resurl"));
													classFind.setSize(classObject.getString("size"));
													classFind.setClassdes(classObject.getString("description"));
													dbUtils.update(classFind);
												}
												
												//课程管理类和user有关
												EmodouClassManager classManager = dbUtils.findFirst(Selector.from(EmodouClassManager.class)
														                                                    .where("bookid","=",bookid)
														                                                    .and("classid","=",classidStr)
														                                                    .and("userid","=",unitidStr)
														                                                    .and("type","=",Constants.EMODOU_TYPE_LISTEN));
												
												if(classManager == null){
													EmodouClassManager classManagerFind = new EmodouClassManager();
													classManagerFind.setClassid(classidStr);
													classManagerFind.setUserid(userid);
													classManagerFind.setBookid(bookid);
													classManagerFind.setType(Constants.EMODOU_TYPE_LISTEN);
													classManagerFind.setState(Constants.EMODOU_CLASS_STATE_NOT_LEAREN);
													classManagerFind.setScore("0");
													dbUtils.saveBindingId(classManagerFind);
												}
											}
										}
										
										
										//解析口语模块
										String speakStr = unitObject.getString("speak");
										if(speakStr != null && !speakStr.equals("")){
											JSONArray speakArray = new JSONArray(speakStr);
											for(int speaki = 0; speaki < speakArray.length(); speaki++){
												JSONObject classObject = speakArray.getJSONObject(speaki);
												
												EmodouClass speakClass = new EmodouClass();
												
												String speakChangetimeStr = classObject.getString("changetime");
												Integer speakChangetimeInt = new Integer(0);
												if(speakChangetimeStr != null)
													speakChangetimeInt = Integer.parseInt(speakChangetimeStr);
												
												String classidStr = classObject.getString("classid");
												speakClass.setPackageid(packageid);
												speakClass.setBookid(bookid);
												speakClass.setUnitid(unitidStr);
												speakClass.setClassid(classidStr);	
												speakClass.setTitle(classObject.getString("title"));
												speakClass.setIcon(classObject.getString("icon"));
												speakClass.setChangetimeInt(speakChangetimeInt);
												speakClass.setResurl(classObject.getString("resurl"));
												speakClass.setSize(classObject.getString("size"));
												speakClass.setClassdes(classObject.getString("description"));
												
												speakClass.setType(Constants.EMODOU_TYPE_SPEAK);
												
												EmodouClass classFind = dbUtils.findFirst(Selector.from(EmodouClass.class)
														                                          .where("classid","=",classidStr)
														                                          .and("type","=",Constants.EMODOU_TYPE_SPEAK)
														                                          .and("packageid","=",packageid));
												if(classFind == null){
													dbUtils.saveBindingId(speakClass);
												}else{
													classFind.setTitle(classObject.getString("title"));
													classFind.setIcon(classObject.getString("icon"));
													classFind.setChangetimeInt(speakChangetimeInt);
													classFind.setResurl(classObject.getString("resurl"));
													classFind.setSize(classObject.getString("size"));
													classFind.setClassdes(classObject.getString("description"));
													dbUtils.update(classFind);
												}
												
												//课程管理类和user有关
												EmodouClassManager classManager = dbUtils.findFirst(Selector.from(EmodouClassManager.class)
														                                                    .where("bookid","=",bookid)
														                                                    .and("classid","=",classidStr)
														                                                    .and("userid","=",unitidStr)
														                                                    .and("type","=",Constants.EMODOU_TYPE_SPEAK));
												
												if(classManager == null){
													EmodouClassManager classManagerFind = new EmodouClassManager();
													classManagerFind.setClassid(classidStr);
													classManagerFind.setUserid(userid);
													classManagerFind.setBookid(bookid);
													classManagerFind.setType(Constants.EMODOU_TYPE_SPEAK);
													classManagerFind.setState(Constants.EMODOU_CLASS_STATE_NOT_LEAREN);
													classManagerFind.setScore("0");
													dbUtils.saveBindingId(classManagerFind);
												}
											}
										
									}
								}
									
									
									//upadateSucceed = "1";
									EmodouBook bookSelect = dbUtils.findFirst(Selector.from(EmodouBook.class)
							                                       .where("state","=",Constants.BOOK_STATE_SELECT)
							                                       .and("userid","=",userid));
									if(bookSelect != null){
										bookSelect.setState(Constants.BOOK_STATE_NOT_SELECT);
										dbUtils.update(bookSelect);
									}
									
									EmodouPackage packageSelect = dbUtils.findFirst(Selector.from(EmodouPackage.class)
										                                                .where("userid","=",userid)
										                                                .and("state","=",Constants.PACKAGE_STATE_SELECT));
									if(packageSelect != null){
										packageSelect.setState(Constants.PACKAGE_STATE_NOT_SELECT);
										dbUtils.update(packageSelect);
									}
									
									String bookid2 = bookList.get(position).getBookid();
									EmodouBook bookNow = dbUtils.findFirst(Selector.from(EmodouBook.class)
																			   .where("bookid","=",bookid2)
																			   .and("packageid","=",packageid)
																			   .and("userid","=",userid));
									if(bookNow != null){
										bookNow.setState(Constants.BOOK_STATE_SELECT);
										dbUtils.update(bookNow);
									}
									
									EmodouPackage packageNow = dbUtils.findFirst(Selector.from(EmodouPackage.class)
										                                             .where("userid","=",userid)
										                                             .and("packageid","=",packageid));
									if(packageNow != null){
										packageNow.setState(Constants.PACKAGE_STATE_SELECT);
										dbUtils.update(packageNow);
									}
									
									Intent intent = new Intent(ChangeBookActivity.this, MainActivity.class);
									intent.putExtra("changebook", "changebook");
									startActivity(intent);
									progressDialog.dismiss();
							}else{
								
								
								String message = result.getString("Message");
								if (message.equals("Error_Wrong_Book")) {

									Toast.makeText(ChangeBookActivity.this, 
											getResources().getString(R.string.home_changebook_wrongbook), 1).show();
								} else if (message.equals("Error_Wrong_Ticket")) {

									Toast.makeText(ChangeBookActivity.this,
											getResources().getString(R.string.home_changebook_wrongticket), 1).show();
								} else {
									Toast.makeText(ChangeBookActivity.this, 
											R.string.home_changebook_wrongchange, 1).show();
								}
								upadateSucceed="0";
								progressDialog.dismiss();
							}
						}catch (JSONException e) {
							// TODO: handle exception
							e.printStackTrace();
						}catch (DbException e) {
							// TODO: handle exception
							e.printStackTrace();
						}
							
						gridAdapter.notifyDataSetChanged();
					}

						@Override
					public void onFailure(HttpException error, String msg) {
						// TODO Auto-generated method stub
						progressDialog.dismiss();
						Toast.makeText(ChangeBookActivity.this,
								getResources().getString(R.string.home_changebook_wrongchange_noNetWork), 1).show();
						upadateSucceed="0";
						progressDialog.dismiss();
					}
		
				});
		
//		if(succeed[0].equals("1")){
//			return true;
//		}else if(succeed[0].equals("0")){
//			return false;
//		}
		//progressDialog.dismiss(); 
	}
	
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}
