package com.emodou.myclass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.emodou.domain.EmodouBook;
import com.emodou.domain.EmodouClass;
import com.emodou.domain.EmodouClassManager;
import com.emodou.domain.EmodouWordClass;
import com.emodou.domain.EmodouWordInfo;
import com.emodou.domain.EmodouWordManager;
import com.emodou.domain.EmodouWordUnit;
import com.emodou.domain.EmodouWork;
import com.emodou.domain.EmodouWorkDetail;
import com.emodou.home.CourseListActivity;
import com.emodou.home.ListenActivity;
import com.emodou.home.ReadActivity;
import com.emodou.home.SpeakActivity;
import com.emodou.home.WordTestActivity;
import com.emodou.home.WordlistActivity;
import com.emodou.home.WordmainActivity;
import com.emodou.home.CourseListActivity.classItemClickListener;
import com.emodou.myclass.HomeworkFragment.SmallViewHolder;
import com.emodou.util.Constants;
import com.emodou.util.LogUtil;
import com.emodou.util.ValidateUtils;
import com.example.emodou.MainActivity;
import com.example.emodou.MyclassActivity;
import com.example.emodou.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WorkDetailActivity extends Activity implements OnClickListener{
	
	private ActionBar actionBar;
	private TextView titleTv;
	private ImageButton returnBtn;
	
	//界面数据
	private String classroomid, workid, userid, ticket;
	private String classid, type;
	private List<EmodouWorkDetail> workDetailList = new ArrayList<EmodouWorkDetail>();
	//用来保存下载handler的map
	private Map<String, HttpHandler> myhandlerMap = new HashMap<String, HttpHandler>();
	private int alldone;
	
	private detailAdapter dAdapter;
	private ListView dListView;
	private TextView booknameTv;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.myclass_workdetail);
		super.onCreate(savedInstanceState);
		
		setActionbar();
		init();
	}
	
	public void setActionbar() {
		actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.actionbar_login);
		
		View view = actionBar.getCustomView();
		titleTv = (TextView)view.findViewById(R.id.login_ancionbar_text);
		titleTv.setText(getResources().getString(R.string.myclass_workdetail_title));
		returnBtn = (ImageButton)view.findViewById(R.id.login_imbtn_return);
		returnBtn.setOnClickListener(this);
	}
	
	public void init() {
		userid = getIntent().getStringExtra("userid");
		ticket = getIntent().getStringExtra("ticket");
		classroomid = getIntent().getStringExtra("classroomid");
		workid = getIntent().getStringExtra("workid");
		
		dListView = (ListView)findViewById(R.id.myclass_workdetail_lv);
		booknameTv = (TextView)findViewById(R.id.myclass_workdetail_needbook);
		
		//先从本地数据库中找，找不到再从服务器请求
		refreshFromLocal();
		
		if(null == workDetailList || 0 == workDetailList.size()){
			refreshFromHttp();
		}
	}

	

	public void refreshFromLocal() {
		// TODO Auto-generated method stub
		DbUtils dbUtils = DbUtils.create(this);
		try {
			workDetailList = dbUtils.findAll(Selector.from(EmodouWorkDetail.class)
					                                 .where("userid","=",userid)
					                                 .and("workid","=",workid));
			
			if(null == workDetailList){
				
			}else{
				//判断作业是否都做完了
				alldone = 1;
				for(EmodouWorkDetail workDetail : workDetailList){
					if(workDetail.getStatus().equals(Constants.WORK_ITEM_DOING)){
						alldone = 0;
					}		
				}
				if(alldone == 1&&workDetailList.size()!=0){
					EmodouWork work = new EmodouWork();
					work = dbUtils.findFirst(Selector.from(EmodouWork.class)
							                         .where("userid","=",userid)
							                         .and("classroomid","=",classroomid)
							                         .and("workid","=",workid));
					if(work != null){
						work.setIfdone(Constants.WORK_DONE);
						dbUtils.update(work);
					}
				}
				dAdapter = new detailAdapter(workDetailList, this);
				dListView.setAdapter(dAdapter);
				dListView.setOnItemClickListener(new detailItemClickListener(workDetailList));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void refreshFromHttp() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Loading...");
		progressDialog.show();
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid",userid);
		params.addBodyParameter("ticket",ticket);
		params.addBodyParameter("classroomid",classroomid);
		params.addBodyParameter("workid",workid);
		params.addBodyParameter("role","s");
		
		String path = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_GET_WORKINFO;
		
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, path, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				String res = responseInfo.result.toString();
				res = res.substring(res.indexOf("{"));
				try{
					JSONObject result = new JSONObject(res);
					String status = result.getString("Status");
					if(status.equals("Success")){
						JSONArray itemArray = new JSONArray();
						itemArray = result.getJSONArray("ItemInfo");
						for(int i = 0; i < itemArray.length(); i++){
							JSONObject itemObject = itemArray.getJSONObject(i);
							
							String itemid = itemObject.getString("itemid");
							String itemtype = itemObject.getString("itemtype");
							String itemname = itemObject.getString("itemname");
							String classid = itemObject.getString("classid");
							String Status = itemObject.getString("status");
							
							WorkDetailActivity.this.classid = classid;
							type = itemtype;
							
							DbUtils dbUtils = DbUtils.create(WorkDetailActivity.this);
							EmodouWorkDetail workDetail = new EmodouWorkDetail();
							workDetail = dbUtils.findFirst(Selector.from(EmodouWorkDetail.class)
									                               .where("userid","=",userid)
									                               .and("workid","=",workid)
									                               .and("itemid","=",itemid));
							if(workDetail != null){
								workDetail.setItemtype(itemtype);
								workDetail.setItemname(itemname);
								workDetail.setClassid(classid);
								workDetail.setStatus(Status);
								dbUtils.update(workDetail);
							}else{
								EmodouWorkDetail workDetailNew = new EmodouWorkDetail();
								workDetailNew.setItemid(itemid);
								workDetailNew.setUserid(userid);
								workDetailNew.setClassroomid(classroomid);
								workDetailNew.setWorkid(workid);
								workDetailNew.setItemtype(itemtype);
								workDetailNew.setItemname(itemname);
								workDetailNew.setClassid(classid);
								workDetailNew.setStatus(Status);
								
								dbUtils.saveBindingId(workDetailNew);
							}
						}
						
						refreshFromLocal();
						progressDialog.dismiss();
						
					}else{
						Toast.makeText(WorkDetailActivity.this, R.string.myclass_workdetail_refreshFail, 0).show();
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
				Toast.makeText(WorkDetailActivity.this, R.string.myclass_workdetail_refreshFail, 0).show();
				progressDialog.dismiss();
			}
		});
		
		
	}
	
	public class detailViewHolder{
		private ImageView doneImv;
		private TextView nameTv;
	}
	
	public class detailAdapter extends BaseAdapter{
		private List<EmodouWorkDetail> workDetails = new ArrayList<EmodouWorkDetail>();
		private LayoutInflater inflater;
		
		public detailAdapter(List<EmodouWorkDetail> workDetails, Context context){
			this.workDetails = workDetails;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return workDetails.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return workDetails.get(position);
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
			detailViewHolder detailHolder;
			if(convertView == null){
				view = inflater.inflate(R.layout.myclass_workdetail_item, parent, false);
				detailHolder = new detailViewHolder();
				detailHolder.doneImv = (ImageView)view.findViewById(R.id.myclass_workdetail_item_doneImv);
				detailHolder.nameTv = (TextView)view.findViewById(R.id.myclass_workdetail_item_nameTv);
				view.setTag(detailHolder);
			}else{
				detailHolder = (detailViewHolder)view.getTag();
			}
			
			if(workDetails.get(position).getStatus().equals("DONE"))
				detailHolder.doneImv.setVisibility(View.VISIBLE);
			else 
				detailHolder.doneImv.setVisibility(View.INVISIBLE);
			
			detailHolder.nameTv.setText(workDetails.get(position).getItemname());
			return view;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.login_imbtn_return:
				Intent intent = new Intent(WorkDetailActivity.this, MainActivity.class);
				intent.putExtra("isFromWorkDetail", "true");
				startActivity(intent);
				break;
	
			default:
				break;
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(WorkDetailActivity.this, MainActivity.class);
		intent.putExtra("isFromWorkDetail", "true");
		startActivity(intent);
		super.onBackPressed();
	}
	
	public class detailItemClickListener implements OnItemClickListener{
		
		private List<EmodouWorkDetail> workDetails = new ArrayList<EmodouWorkDetail>();
		
		public detailItemClickListener(List<EmodouWorkDetail> workDetails){
			this.workDetails = workDetails;
		}
		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			//根据不同的作业类型跳转到不同的界面
			String classid = workDetails.get(position).getClassid();
			String type = workDetails.get(position).getItemtype();
			String itemid = workDetails.get(position).getItemid();
			refreshBookFromHttp(classid, type, itemid);
			
			//EmodouWorkDetail workDetail = workDetails.get(position);
			//还是为空
//			String bookid = bookidName[0];
//			String bookname = bookidName[1];
//			if(bookid == null||bookname==null){
//				//第一次初始化，所以要对list重新赋值，否则bookid在这次点击事件中会为null
//				//因为http是异步的，所以执行到这部的时候，数据库还没存储完
//				workDetails = getWorkDetails();
//				workDetail = workDetails.get(position);
//				bookid = workDetail.getBookid();
//				bookname = workDetail.getBookName();
//			}
			
			
		}
		
	}
	
	public List<EmodouWorkDetail> getWorkDetails() {
		List<EmodouWorkDetail> workDetails = new ArrayList<EmodouWorkDetail>();
		DbUtils dbUtils = DbUtils.create(this);
		try {
			workDetails = dbUtils.findAll(Selector.from(EmodouWorkDetail.class)
					                                 .where("userid","=",userid)
					                                 .and("workid","=",workid));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return workDetails;
	}
	
	public void refreshBookFromHttp(final String classid, final String type, final String itemid) {
		// TODO Auto-generated method stub
		RequestParams params1 = new RequestParams();
		params1.addBodyParameter("userid",userid);
		params1.addBodyParameter("ticket",ticket);
		params1.addBodyParameter("classid",classid);
		params1.addBodyParameter("type",type);
		
		String path1 = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_GET_CLASSSOURCE;
		
		HttpUtils httpUtils1 = new HttpUtils();
		httpUtils1.send(HttpMethod.POST, path1, params1, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				
				// TODO Auto-generated method stub
				String res = responseInfo.result.toString();
				res = res.substring(res.indexOf("{"));
				
				try{
					JSONObject result = new JSONObject(res);
					String status = result.getString("Status");
					if(status.equals("Success")){
						String bookid = result.getString("BookId");
						String bookname = result.getString("BookName");
						DbUtils dbUtils = DbUtils.create(WorkDetailActivity.this);
						try {
							EmodouWorkDetail workDetail = dbUtils.findFirst(Selector.from(EmodouWorkDetail.class)
																				    .where("userid","=",userid)
													                                .and("workid","=",workid)
													                                .and("itemid","=",itemid));
							if(workDetail != null){
								workDetail.setBookid(bookid);
								workDetail.setBookName(bookname);
								dbUtils.update(workDetail);
								refreshFromLocal();
								
								String itemid = workDetail.getItemid();
								
								if(type.equals("listen")){
									loadFile(Constants.EMODOU_TYPE_LISTEN, classid, bookid, bookname, itemid);
								}else if(type.equals("read")){
									loadFile(Constants.EMODOU_TYPE_READ, classid, bookid, bookname, itemid);
								}else if(type.equals("speak")){
									loadFile(Constants.EMODOU_TYPE_SPEAK, classid, bookid, bookname, itemid);
								}else if(type.equals("word")){
									if(ValidateUtils.isExist("/sdcard/emodou/" + Constants.EMODOU_TYPE_WORD + "/"+ bookid + "/1.JSON")){
										//如果存在过了，说明已经下载并且解压过了
										//判断是否已经解析过了
										try{
											List<EmodouWordClass> wordClassentity = dbUtils.findAll(Selector.from(EmodouWordClass.class)
																									.where("bookid","=",bookid)
																									.and("userid","=",userid));
											if(wordClassentity!=null && wordClassentity.size()!=0){
												//可能会遗留从主页面进入时选择的状态，因为那时退出时不会把选择状态清空
												for(int i = 0; i < wordClassentity.size(); i++){
													wordClassentity.get(i).setState(Constants.WORD_CLASS_NOT_SELECT);	
													dbUtils.update(wordClassentity.get(i));
												}
												
												EmodouWordClass wordClass = new EmodouWordClass();
												try {
													wordClass = dbUtils.findFirst(Selector.from(EmodouWordClass.class)
															                              .where("userid","=",userid)
															                              .and("bookid","=",bookid)
															                              .and("classid","=",classid));
													if(wordClass != null){
														wordClass.setState(Constants.WORD_CLASS_SELECTED);
														dbUtils.update(wordClass);
													}
												} catch (DbException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												//已经解析过了
												Intent intent1 = new Intent(WorkDetailActivity.this,WordTestActivity.class);
												intent1.putExtra("bookid", bookid);
												intent1.putExtra("userid", userid);
												intent1.putExtra("ticket", ticket);
												intent1.putExtra("classroomid", classroomid);
												intent1.putExtra("workid", workid);
												intent1.putExtra("itemid", itemid);
												intent1.putExtra("starttime", System.currentTimeMillis()+"");
												intent1.putExtra("classid", classid);
												startActivity(intent1);
											}else{
												//已经下载但没有存到本地数据库，这时还没学过，需要解析
												progressDialog = new ProgressDialog(WorkDetailActivity.this);
												progressDialog.setIndeterminate(true);
												progressDialog.setMessage("Loading ...");
												progressDialog.show();
												if(ValidateUtils.isExist("/sdcard/emodou/" + Constants.EMODOU_TYPE_WORD + "/" + bookid + "/1.JSON")){
													//解析json文件并保存到数据库
													analyWordjson(bookid);
													EmodouWordClass wordClass = new EmodouWordClass();
													try {
														wordClass = dbUtils.findFirst(Selector.from(EmodouWordClass.class)
																                              .where("userid","=",userid)
																                              .and("bookid","=",bookid)
																                              .and("classid","=",classid));
														if(wordClass != null){
															wordClass.setState(Constants.WORD_CLASS_SELECTED);
															dbUtils.update(wordClass);
														}
													} catch (DbException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
													Intent intent2 = new Intent(WorkDetailActivity.this,WordTestActivity.class);
													intent2.putExtra("bookid", bookid);
													intent2.putExtra("userid", userid);
													intent2.putExtra("ticket", ticket);
													intent2.putExtra("classroomid", classroomid);
													intent2.putExtra("workid", workid);
													intent2.putExtra("itemid", itemid);
													intent2.putExtra("starttime", System.currentTimeMillis()+"");
													intent2.putExtra("classid", classid);
													startActivity(intent2);
												
												}
												progressDialog.dismiss();
											}
										}catch(Exception e){
											e.printStackTrace();
										}
									}else{
										//没有下载解压过
										LoadAndUnzipFile(bookid, itemid, classid);
									}
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}	
					}else{
						Toast.makeText(WorkDetailActivity.this, R.string.myclass_workdetail_refreshFail, 0).show();					
					}
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				Toast.makeText(WorkDetailActivity.this, R.string.myclass_workdetail_refreshFail, 0).show();
				
			}
		});
	}
	
	public void loadFile(final String type, final String classid, final String bookid, String bookname, final String itemid) {
		//听、说和读的下载列表
		//必须选择的是同一本书，因为目前API没有返回足够的信息，如课程resurl，其他本书无法去下载
		//先判断当前作业的这本书和系统正在学习的书是不是一本
		//弹出对话等待框
		progressDialog = new ProgressDialog(this);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Loading 0%...");
		progressDialog.show();
		
		DbUtils dbUtils = DbUtils.create(this);
		EmodouBook book = new EmodouBook();
		try {
			book = dbUtils.findFirst(Selector.from(EmodouBook.class)
					                         .where("state","=",Constants.BOOK_STATE_SELECT)
					                         .and("userid","=",userid));
		} catch (DbException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(book != null){
			if(!book.getBookid().equals(bookid)){
				Toast.makeText(this, getResources().getString(R.string.myclass_workdetail_bookNotSelect)+bookname, 0).show();
				progressDialog.dismiss();
			}else{
				//当前正在学习的书和作业的书是同一本
				//type要是2，3,4这种
				Intent itent = null;
				EmodouClassManager classManager = new EmodouClassManager();
				try {
					classManager = dbUtils.findFirst(Selector.from(EmodouClassManager.class)
							                                 .where("userid","=",userid)
							                                 .and("bookid","=",bookid)
							                                 .and("classid","=",classid));
				} catch (DbException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String articleUrl = Constants.LOCAL_START + type + "/" + classid + Constants.LOCAL_JSON1;
				//首先如果能找到相应的资源则直接进行跳转
				if (ValidateUtils.isExist(articleUrl)) {

					if (Constants.EMODOU_TYPE_LISTEN.equals(type)) {
						itent = new Intent(WorkDetailActivity.this, ListenActivity.class);
					} else if (Constants.EMODOU_TYPE_READ.equals(type)) {
						itent = new Intent(this, ReadActivity.class);
					} else if (Constants.EMODOU_TYPE_SPEAK.equals(type)) {
						itent = new Intent(this, SpeakActivity.class);
					}
					Bundle mBundle = new Bundle();
					mBundle.putString("type", type);
					mBundle.putString("classid", classid);
					mBundle.putString("bookid", bookid);
					mBundle.putString("classroomid", classroomid);
					mBundle.putString("workid", workid);
					mBundle.putString("itemid", itemid);
					mBundle.putString("starttime", System.currentTimeMillis()+"");

					itent.putExtras(mBundle);
					startActivity(itent);
					
					progressDialog.dismiss();
				} 
				//如果找不到本地res资源，则找压缩包看是否存在
				else if (classManager != null && classManager.getDownloadstate() == Constants.EMODOU_CLASS_DOWNLOAD_STATE_ALREADY_DOWNLOAD) {
					//如果该课是已下载的状态，则解压完后还是已下载的状态，不需要改变
					
					//为了避免解压不完全的情况，首先盘算是否有解压出来的资源文件 文件夹如果有 删掉
					if (ValidateUtils.isExist(Constants.LOCAL_START + type + "/"+ classid + Constants.LOCAL_RES)) {
						ValidateUtils.deleteDirectory(Constants.LOCAL_START + type + "/" + classid + Constants.LOCAL_RES);
					}

					//拼串解压相应文件 ， 解压完之后 进行相应的跳转
					Log.d("url", "step1");
					File zipFile = new File(Constants.LOCAL_START + type + "/"+ classid + "/" + classid + Constants.LOCAL_ZIP);
					try {
						String unzipLocation = Constants.LOCAL_START + type + "/" + classid;
						ValidateUtils.unzipFiles3(zipFile, unzipLocation);

						if (Constants.EMODOU_TYPE_LISTEN.equals(type)) {
							itent = new Intent(this, ListenActivity.class);
						} else if (Constants.EMODOU_TYPE_READ.equals(type)) {
							itent = new Intent(this, ReadActivity.class);
						} else if (Constants.EMODOU_TYPE_SPEAK.equals(type)) {
							itent = new Intent(this, SpeakActivity.class);
						}

						progressDialog.dismiss();
						Bundle mBundle = new Bundle();
						mBundle.putString("type", type);
						mBundle.putString("classid", classid);
						mBundle.putString("bookid", bookid);
						mBundle.putString("classroomid", classroomid);
						mBundle.putString("workid", workid);
						mBundle.putString("itemid", itemid);
						mBundle.putString("starttime", System.currentTimeMillis()+"");
						itent.putExtras(mBundle);
						startActivity(itent);


					} catch (Exception e) {
						Toast.makeText(this, e.toString(), 0).show();
						progressDialog.dismiss();
						e.printStackTrace();
					}

				}
				
				//如果本地压缩包也不存在，则从网络上进行下载
				else{
					EmodouClass classentity = new EmodouClass();
					try {
						classentity = dbUtils.findFirst(Selector.from(EmodouClass.class)
								                                .where("classid","=",classid));
					} catch (DbException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						progressDialog.dismiss();
					}
					
					String classurl = Constants.EMODOU_URL + "/"+ classentity.getResurl();
					LogUtil.d("classurl", classurl);
					HttpUtils http = new HttpUtils();
					//找运行时变量map里面是否有相应的handler如果没有就新建一个
					if (myhandlerMap.get(classid) == null) {
						HttpHandler httpHandler = http.download(classurl,
								Constants.LOCAL_START + type + "/" + classid + "/"+ classid + ".zip", true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将重新下载。
								true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
								new RequestCallBack<File>() {

									@Override
									public void onStart() {
									}

									@Override
									//下载的过程中实时更新数据库中的进度
									public void onLoading(long total, long current,
											boolean isUploading) {


										DbUtils dbUtils = DbUtils.create(WorkDetailActivity.this);
										try {
											EmodouClassManager classManager = dbUtils.findFirst(Selector
															.from(EmodouClassManager.class)
															.where("bookid", "=",bookid)
															.and("classid", "=",classid)
															.and("type", "=", type)
															.and("userid", "=", userid));
											if(classManager != null && dbUtils != null){
												classManager.setProgress((float) ((int) (100 * current / total)) / 100);
												dbUtils.update(classManager);
											}
											progressDialog.setMessage("Loading  "+  ((int) (100 * current / total)) +"%...");
										} catch (Exception e) {
											e.printStackTrace();
										}

									}

									@Override
									public void onCancelled() {
										Log.d("progress_workdetail", classid + "cancel");
										super.onCancelled();
									}

									@Override
									//下载成功后 首先将本条状态在数据库中更新为已下载，其次如果还有等待中的任务则开启一个新的任务，并且在
									//运行时变量map中将本条记录的handler移除5
									public void onSuccess(
											ResponseInfo<File> responseInfo) {
										DbUtils dbUtils = DbUtils.create(WorkDetailActivity.this);
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
											//下载之后进行解压
											//为了避免解压不完全的情况，首先盘算是否有解压出来的资源文件 文件夹如果有 删掉
											if (ValidateUtils.isExist(Constants.LOCAL_START + type + "/"+ classid + Constants.LOCAL_RES)) {
												ValidateUtils.deleteDirectory(Constants.LOCAL_START + type + "/" + classid + Constants.LOCAL_RES);
											}

											//拼串解压相应文件 ， 解压完之后 进行相应的跳转
											Log.d("url", "step1");
											File zipFile = new File(Constants.LOCAL_START + type + "/"+ classid + "/" + classid + Constants.LOCAL_ZIP);
											try {
												String unzipLocation = Constants.LOCAL_START + type + "/" + classid;
												ValidateUtils.unzipFiles3(zipFile, unzipLocation);
												Intent intent = null;

												if (Constants.EMODOU_TYPE_LISTEN.equals(type)) {
													intent = new Intent(WorkDetailActivity.this, ListenActivity.class);
												} else if (Constants.EMODOU_TYPE_READ.equals(type)) {
													intent = new Intent(WorkDetailActivity.this, ReadActivity.class);
												} else if (Constants.EMODOU_TYPE_SPEAK.equals(type)) {
													intent = new Intent(WorkDetailActivity.this, SpeakActivity.class);
												}

												progressDialog.dismiss();
												Bundle mBundle = new Bundle();
												mBundle.putString("type", type);
												mBundle.putString("classid", classid);
												mBundle.putString("bookid", bookid);
												mBundle.putString("classroomid", classroomid);
												mBundle.putString("workid", workid);
												mBundle.putString("itemid", itemid);
												mBundle.putString("starttime", System.currentTimeMillis()+"");
												intent.putExtras(mBundle);
												startActivity(intent);


											} catch (Exception e) {
												Toast.makeText(WorkDetailActivity.this, e.toString(), 0).show();
												progressDialog.dismiss();
												e.printStackTrace();
											}

											if(myhandlerMap != null){
												myhandlerMap.remove(classid);
											}
											

										} catch (Exception e) {
											e.printStackTrace();
										}
									}

									@Override
									public void onFailure(HttpException error,String msg) {
										error.printStackTrace();

									}

									
								});
						
						//将handler放进map

						myhandlerMap.put(classid, httpHandler);

					}
				}
				
			}
		}	
	}
	
	public void LoadAndUnzipFile(final String bookid, final String itemid, final String classid) {
		//单词的下载和解压
		String url = null;
		progressDialog = new ProgressDialog(this);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Loading 0%...");
		progressDialog.show();
		DbUtils dbUtils2 = DbUtils.create(this);
		try{
			EmodouBook bookentitiy2 = dbUtils2.findFirst(Selector.from(EmodouBook.class)
																	 .where("bookid","=",bookid));
			url = Constants.EMODOU_URL+"/"+bookentitiy2.getWordurl();
			System.out.printf("wordurl",url);
			}catch(DbException e){
				e.printStackTrace();
			}
		HttpUtils http = new HttpUtils();
		http.getHttpClient().getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 300000);
		HttpHandler hanlder;
		hanlder = http.download(url, "/sdcard/emodou/" + Constants.EMODOU_TYPE_WORD
				+ "/" + bookid + "/" + bookid + ".zip", true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将重新下载。
				true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
				new RequestCallBack<File>() {
					public void onStart() {
						super.onStart();
					}
					
					public void onLoading(long total, long current, boolean isUploading){
						progressDialog.setMessage("Loading "+(int)(100*current/total)+"%...");
					}
					public void onSuccess(ResponseInfo<File> responseInfo) {
						
						//下载成功后，修改课本下载状态为已下载
						DbUtils dbUtils = DbUtils.create(WorkDetailActivity.this);
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
						File zipFile = new File("/sdcard/emodou/"+ Constants.EMODOU_TYPE_WORD + "/" + bookid + "/"+ bookid + ".zip");
						try{
							ValidateUtils.unzipFiles3(zipFile, "/sdcard/emodou/"+ Constants.EMODOU_TYPE_WORD + "/" + bookid);
						}catch(Exception e){
							e.printStackTrace();
						}
						//下载后进行解析
						analyWordjson(bookid);
						
						EmodouWordClass wordClass = new EmodouWordClass();
						try {
							wordClass = dbUtils.findFirst(Selector.from(EmodouWordClass.class)
									                              .where("userid","=",userid)
									                              .and("bookid","=",bookid)
									                              .and("classid","=",classid));
							if(wordClass != null){
								wordClass.setState(Constants.WORD_CLASS_SELECTED);
								dbUtils.update(wordClass);
							}
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						Intent intent = new Intent(WorkDetailActivity.this, WordTestActivity.class);
						intent.putExtra("bookid", bookid);
						intent.putExtra("userid", userid);
						intent.putExtra("ticket", ticket);
						intent.putExtra("classroomid", classroomid);
						intent.putExtra("workid", workid);
						intent.putExtra("itemid", itemid);
						intent.putExtra("starttime", System.currentTimeMillis()+"");
						intent.putExtra("classid", classid);
						startActivity(intent);
						
						progressDialog.dismiss();
					}
					
					public void onFailure(HttpException error, String msg) {
						if (msg.equals("maybe the file has downloaded completely")) {
							Toast.makeText(WorkDetailActivity.this,"文件已下载", 0).show();
							DbUtils dbUtils = DbUtils.create(WorkDetailActivity.this);
							//接下来解压下载的包
							File zipFile = new File("/sdcard/emodou/"+ Constants.EMODOU_TYPE_WORD + "/" + bookid + "/"+ bookid + ".zip");
							try{
								ValidateUtils.unzipFiles3(zipFile, "/sdcard/emodou/"+ Constants.EMODOU_TYPE_WORD + "/" + bookid);
							}catch(Exception e){
								e.printStackTrace();
							}
							//下载后进行解析
							analyWordjson(bookid);
							
							EmodouWordClass wordClass = new EmodouWordClass();
							try {
								wordClass = dbUtils.findFirst(Selector.from(EmodouWordClass.class)
										                              .where("userid","=",userid)
										                              .and("bookid","=",bookid)
										                              .and("classid","=",classid));
								if(wordClass != null){
									wordClass.setState(Constants.WORD_CLASS_SELECTED);
									dbUtils.update(wordClass);
								}
							} catch (DbException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							Intent intent = new Intent(WorkDetailActivity.this, WordTestActivity.class);
							intent.putExtra("bookid", bookid);
							intent.putExtra("userid", userid);
							intent.putExtra("ticket", ticket);
							intent.putExtra("classroomid", classroomid);
							intent.putExtra("workid", workid);
							intent.putExtra("itemid", itemid);
							intent.putExtra("starttime", System.currentTimeMillis()+"");
							intent.putExtra("classid", classid);
							startActivity(intent);
						}
						progressDialog.dismiss();
						System.out.println(msg);											
					}
			});
		
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
	
	public void analyWordjson(final String bookid) {
		//单词解析
		String wordjsonStr = "";
		try{
			wordjsonStr = readFileSdcardFile("/sdcard/emodou/" + Constants.EMODOU_TYPE_WORD + "/"+ bookid + "/1.JSON");
		}catch(IOException e){
			e.printStackTrace();
		}
		
		//存储到本地数据库中
		DbUtils worDbUtils = DbUtils.create(this);
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
							wordInfo.setLocal_voice("/emodou/" + Constants.EMODOU_TYPE_WORD  + "/" + bookid + "/" + wordObject.getString("local_voice"));
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
							wordentity.setLocal_voice("/emodou/" + Constants.EMODOU_TYPE_WORD  + "/" + bookid + "/" + wordObject.getString("local_voice"));
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
	
}
