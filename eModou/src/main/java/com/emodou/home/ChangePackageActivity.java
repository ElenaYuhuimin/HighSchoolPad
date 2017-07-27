package com.emodou.home;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouBook;
import com.emodou.domain.EmodouPackage;
import com.emodou.domain.EmodouUserInfo;
import com.emodou.home.ChangeBookActivity.ViewHolder;
import com.emodou.home.CourseListActivity.classItemClickListener;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChangePackageActivity extends Activity implements OnClickListener{
	
	private ActionBar actionBar;
	private TextView titleTv;
	private ImageButton returnBtn;
	
	private EditText codeEdText;
	private String codeStr;
	private PullToRefreshListView pullListView;
	private List<EmodouPackage> paclist = new ArrayList<EmodouPackage>();
	private PacListAdapter pacListAdapter;
	private long refreshTime;
	private String userid, packageid;
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_changepackage);
		
		setActionbar();
		
		init();
	}

	public void setActionbar() {
		actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.actionbar_login);
		
		View view = actionBar.getCustomView();
		titleTv = (TextView)view.findViewById(R.id.login_ancionbar_text);
		titleTv.setText(getResources().getString(R.string.word_changepackage_title));
		returnBtn = (ImageButton)view.findViewById(R.id.login_imbtn_return);
		returnBtn.setOnClickListener(this);
	}
	
	private void init() {
		// TODO Auto-generated method stub
		codeEdText = (EditText)findViewById(R.id.word_search_find_edt);
		
		
		pullListView = (PullToRefreshListView)findViewById(R.id.home_changepackage_listView);
		
		userid = ValidateUtils.getUserid(this);
		packageid = getIntent().getStringExtra("packageid");
		
		initSqlPacFromHttp();
		
		
		imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
		//设置加载图片的配置
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.home_changepackage_item_icondefault)
				.showImageForEmptyUri(R.drawable.home_changepackage_item_icondefault)
				.showImageOnFail(R.drawable.home_changepackage_item_icondefault)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.build();
				
//		pacListAdapter = new PacListAdapter(paclist, options, this);
//		pullListView.setAdapter(pacListAdapter);
//		pullListView.setOnItemClickListener(new pacItemClickListener());
		
		
		
		pullListView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				initSqlPacFromHttp();
				pullListView.postDelayed(new Runnable() {

					
					@Override
					public void run() {
						pullListView.onRefreshComplete();
						pacListAdapter.notifyDataSetChanged();
					}
				}, refreshTime);
			}
		});
		
//		codeEdText.setCompoundDrawables(null, null, null, null);
//		final Drawable rigDrawable = getResources().getDrawable(R.drawable.home_changepackage_confirm_selector);
		codeEdText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
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
//				LogUtil.d("charstring", s+"");
//				if(s.toString() == null || s.toString().equals(""))
//					codeEdText.setCompoundDrawables(null, null, null, null);
//				else
//					codeEdText.setCompoundDrawables(null, null, rigDrawable, null);
				codeStr = s.toString().trim();
			}
		});
		codeEdText.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				//getCompoundDrawables() 可以获取一个长度为4的数组，
                //存放drawableLeft，Right，Top，Bottom四个图片资源对象
                //index=2 表示的是 drawableRight 图片资源对象
				Drawable drawable = codeEdText.getCompoundDrawables()[2];
				if(drawable == null)
					return false;
				if(event.getAction() != MotionEvent.ACTION_UP)
					return false;
				//drawable.getIntrinsicWidth() 获取drawable资源图片呈现的宽度
				if(event.getX() > codeEdText.getWidth() - codeEdText.getPaddingRight() - drawable.getIntrinsicWidth()){
					 //进入这表示图片被选中，可以处理相应的逻辑了
					if(codeStr == null || codeStr.equals("")){
						Toast.makeText(ChangePackageActivity.this, getResources().getString(R.string.word_changepackage_codeHint), 0).show();
					}else{
						final ProgressDialog progressDialog = new ProgressDialog(ChangePackageActivity.this);
						progressDialog.setIndeterminate(true);
						progressDialog.setCanceledOnTouchOutside(true);
						progressDialog.setMessage(getResources().getString(R.string.word_changepackage_loading));
						progressDialog.show();
						activatePac();
						codeEdText.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								progressDialog.dismiss();
							}
						}, refreshTime);
					}
					
					
				}
					
				return false;
			}
		});

	}
	
	public void initSqlPacFromUserinfo() {
		DbUtils dbUtils = DbUtils.create(this);
		try {
			List<EmodouPackage> localPacList = new ArrayList<EmodouPackage>();
			EmodouUserInfo userInfo = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
			String packagelistString = userInfo.getPackagelistString();
			if(!packagelistString.equals("")&&packagelistString!=null){
				JSONArray jsonArray = new JSONArray(packagelistString);
				for(int i = 0; i < jsonArray.length(); i++){
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					
					String packageid = (String)jsonObject.get("packageid");
					String deadtime = (String)jsonObject.getString("endtime");
					String available = (String)jsonObject.get("availability");
					String packagename = (String)jsonObject.get("packagename");
					String packageicon = (String)jsonObject.get("packageicon");
					String description = (String)jsonObject.get("description");
					//最新更新时间changetime在换书界面getpackageinfo的E_getpackageinfo.php返回了
					

					
					EmodouPackage package1 = dbUtils.findFirst(Selector.from(EmodouPackage.class)
							                                           .where("userid","=",userid)
							                                           .and("packageid","=", packageid));
					if(package1 != null){
						package1.setEndtime(deadtime);
						package1.setAvailable(available);	
						package1.setPackagename(packagename);
						package1.setPackageicon(packageicon);
						package1.setPackagedes(description);
						dbUtils.update(package1);
					}else{
						EmodouPackage package2 = new EmodouPackage();
						package2.setPackageid(packageid);
						package2.setEndtime(deadtime);
						package2.setAvailable(available);	
						package2.setPackagename(packagename);
						package2.setPackageicon(packageicon);
						package2.setPackagedes(description);
						package2.setUserid(userid);
						package2.setState(Constants.PACKAGE_STATE_NOT_SELECT);//如果没有的包，一定是未选中的
						dbUtils.saveBindingId(package2);
					}
				}
			}
		} catch (DbException e) {
			// TODO: handle exception
			e.printStackTrace();
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void activatePac() {

		// TODO Auto-generated method stub
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setIndeterminate(true);
		progressDialog.setCanceledOnTouchOutside(true);
		progressDialog.setMessage(getResources().getString(R.string.word_changepackage_loading));
		progressDialog.show();
		if(ValidateUtils.isNetworkConnected(this)){
			
			RequestParams params = new RequestParams();
			params.addBodyParameter("userid", ValidateUtils.getUserid(this));
			params.addBodyParameter("activationcode", codeStr);
			HttpUtils httpUtils = new HttpUtils();
			String CITY_PATH_JSON = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_ACTIVATE;
			httpUtils.send(HttpMethod.POST, CITY_PATH_JSON, params, new RequestCallBack<String>() {

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					// TODO Auto-generated method stub
					try {
						String res = responseInfo.result.toString();
						LogUtil.d("active", res);
						int index = res.indexOf("{");
						if(index == -1){
							Toast.makeText(ChangePackageActivity.this, getResources().getString(R.string.word_changepackage_nostart), 0).show();
						}else{
							res = res.substring(index);
							
							JSONObject result = new JSONObject(res);
							
							String status = (String)result.get("Status");
							
							if(status.equals("Success")){
								
								DbUtils dbUtils = DbUtils.create(ChangePackageActivity.this);
								try {
									JSONArray jsonArray = result.getJSONArray("Package");
									for(int i = 0; i < jsonArray.length(); i++){
										
										
										JSONObject jsonObject = jsonArray.getJSONObject(i);
										
										String endtime = (String)jsonObject.get("endtime");
										String packageid = (String)jsonObject.get("packageid");
										String packageicon = (String)jsonObject.get("packageicon");
										String description = (String)jsonObject.get("description");
										String packagename = (String)jsonObject.get("packagename");
										String available = (String)jsonObject.get("availability");
										
										
										EmodouPackage package1 = dbUtils.findFirst(Selector.from(EmodouPackage.class)
												                                           .where("userid","=",userid)
												                                           .and("packageid","=", packageid));
										if(package1 != null){
											
											package1.setEndtime(endtime);
											package1.setPackageicon(packageicon);
											package1.setPackagedes(description);
											package1.setPackagename(packagename);
											package1.setAvailable(available);
											dbUtils.update(package1);
										}else{
											EmodouPackage package2 = new EmodouPackage();
											package2.setPackageid(packageid);
											package2.setEndtime(endtime);
											package2.setPackageicon(packageicon);
											package2.setPackagedes(description);
											package2.setPackagename(packagename);
											package2.setAvailable(available);
											package2.setUserid(userid);
											package2.setState(Constants.PACKAGE_STATE_NOT_SELECT);
											dbUtils.saveBindingId(package2);
										}
									}
								} catch (DbException e) {
									// TODO: handle exception
									e.printStackTrace();
								}
								
								initPacList();
								pacListAdapter.notifyDataSetChanged();
								progressDialog.dismiss();
							}else{
								//返回错误信息
								String message = (String)result.get("Message");
								if(message.equals("Error_Wrong_Ticket"))
									Toast.makeText(ChangePackageActivity.this, R.string.home_changebook_wrongticket, 0).show();
								else
									Toast.makeText(ChangePackageActivity.this, R.string.word_changepackage_nostart, 0).show();
								
								progressDialog.dismiss();
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(HttpException error, String msg) {
					// TODO Auto-generated method stub
					Toast.makeText(ChangePackageActivity.this, R.string.word_changepackage_nostart, 0).show();
					progressDialog.dismiss();
				}
			});
			
		}else{
			Toast.makeText(this, R.string.word_changepackage_nonetword, 0).show();
			progressDialog.dismiss();
		}
	}
	
	
	
	public void initSqlPacFromHttp() { 
		// TODO Auto-generated method stub
		long refreshStart = System.currentTimeMillis();
		if(ValidateUtils.isNetworkConnected(this)){
			
			RequestParams params = new RequestParams();
			params.addBodyParameter("userid", ValidateUtils.getUserid(this));
			params.addBodyParameter("ticket", ValidateUtils.getTicket(this));
			HttpUtils httpUtils = new HttpUtils();
			String CITY_PATH_JSON = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_PACKAGE_LIST;
			httpUtils.send(HttpMethod.POST, CITY_PATH_JSON, params, new RequestCallBack<String>() {

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					// TODO Auto-generated method stub
					try {
						String res = responseInfo.result.toString();
						int index = res.indexOf("{");
						if(index == -1){
							Toast.makeText(ChangePackageActivity.this, getResources().getString(R.string.word_changepackage_nostart), 0).show();
						}else{
							res = res.substring(index);
							
							JSONObject result = new JSONObject(res);
							
							String status = (String)result.get("Status");
							
							if(status.equals("Success")){
								DbUtils dbUtils = DbUtils.create(ChangePackageActivity.this);
								try {
									JSONArray jsonArray = result.getJSONArray("Packagelist");
									for(int i = 0; i < jsonArray.length(); i++){
										
										
										JSONObject jsonObject = jsonArray.getJSONObject(i);
										
										String packageid = (String)jsonObject.get("packageid");
										String deadtime = (String)jsonObject.getString("endtime");
										String available = (String)jsonObject.get("availability");
										String packagename = (String)jsonObject.get("packagename");
										String packageicon = (String)jsonObject.get("packageicon");
										String price = (String)jsonObject.get("price");
										String description = (String)jsonObject.get("description");
										//最新更新时间changetime在换书界面getpackageinfo的E_getpackageinfo.php返回了
										

										
										EmodouPackage package1 = dbUtils.findFirst(Selector.from(EmodouPackage.class)
												                                           .where("userid","=",userid)
												                                           .and("packageid","=", packageid));
										if(package1 != null){
											package1.setEndtime(deadtime);
											package1.setAvailable(available);	
											package1.setPackagename(packagename);
											package1.setPackageicon(packageicon);
											package1.setPrice(price);
											package1.setPackagedes(description);
											dbUtils.update(package1);
										}else{
											EmodouPackage package2 = new EmodouPackage();
											package2.setPackageid(packageid);
											package2.setEndtime(deadtime);
											package2.setAvailable(available);	
											package2.setPackagename(packagename);
											package2.setPackageicon(packageicon);
											package2.setPrice(price);
											package2.setPackagedes(description);
											package2.setUserid(userid);
											package2.setState(Constants.PACKAGE_STATE_NOT_SELECT);//如果没有的包，一定是未选中的
											dbUtils.saveBindingId(package2);
										}
									}
									dbUtils.close();
								} catch (DbException e) {
									// TODO: handle exception
									e.printStackTrace();
								}catch (JSONException e) {
									// TODO: handle exception
									e.printStackTrace();
								}
							}else{
								//返回错误信息
								String message = (String)result.get("Message");
								if(message.equals("Error_Wrong_Ticket"))
									Toast.makeText(ChangePackageActivity.this, R.string.home_changebook_wrongticket, 0).show();
								else
									Toast.makeText(ChangePackageActivity.this, R.string.word_changepackage_nostart, 0).show();
								initSqlPacFromUserinfo();
							}
						}
					} catch (JSONException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(HttpException error, String msg) {
					// TODO Auto-generated method stub
					Toast.makeText(ChangePackageActivity.this, R.string.word_changepackage_nostart, 0).show();
					initSqlPacFromUserinfo();
				}
			});
			
		}else{
			Toast.makeText(this, R.string.word_changepackage_nonetword, 0).show();
			initSqlPacFromUserinfo();
		}
		long refreshEnd = System.currentTimeMillis();
		refreshTime = refreshEnd - refreshStart;
		//initPacList();
		DbUtils dbUtils = DbUtils.create(this);
		try {
			paclist = dbUtils.findAll(Selector.from(EmodouPackage.class)
					                          .where("userid","=",userid));
		} catch (DbException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		pacListAdapter = new PacListAdapter(paclist, options, this);
		pullListView.setAdapter(pacListAdapter);
		pullListView.setOnItemClickListener(new pacItemClickListener());
	}
	
	
	public void initPacList() { 
		DbUtils dbUtils = DbUtils.create(this);
		try {
			paclist = dbUtils.findAll(Selector.from(EmodouPackage.class)
					                          .where("userid","=",userid));
		} catch (DbException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public class ViewHolder{
		private ImageView iconImv, buyImv, selectImv, unselectImv;
		private TextView nameTv, availableTv, deadtimeTv, priceTv;
	}
	
	public class PacListAdapter extends BaseAdapter{
		
		private List<EmodouPackage> pacList = new ArrayList<EmodouPackage>();
		private LayoutInflater inflater;
		private DisplayImageOptions options;
		
		public PacListAdapter(List<EmodouPackage> myPacList, DisplayImageOptions myoptions, Context context) {
			super();
			this.pacList = myPacList;
			this.options = myoptions;
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pacList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return pacList.get(position);
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
				view = inflater.inflate(R.layout.home_changepackage_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.availableTv = (TextView)view.findViewById(R.id.home_changepackage_item_available);
				viewHolder.buyImv = (ImageView)view.findViewById(R.id.home_changepackage_item_buy);
				viewHolder.deadtimeTv = (TextView)view.findViewById(R.id.home_changepackage_item_deadtime);//DEFAULT
				viewHolder.iconImv = (ImageView)view.findViewById(R.id.home_changepackage_item_icon);//DEFAULT
				viewHolder.nameTv = (TextView)view.findViewById(R.id.home_changepackage_item_name);//DEFAULT
				viewHolder.priceTv = (TextView)view.findViewById(R.id.home_changepackage_item_price);
				viewHolder.selectImv = (ImageView)view.findViewById(R.id.home_changepackage_item_select);
				viewHolder.unselectImv = (ImageView)view.findViewById(R.id.home_changepackage_item_unselect);
				view.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder)view.getTag();
			}
			
			if(pacList.get(position).getEndtime() != null && !(pacList.get(position).getEndtime().equals(""))){
				viewHolder.deadtimeTv.setVisibility(View.VISIBLE);
				long deadtime = Long.parseLong(pacList.get(position).getEndtime());
		    	String deadtimeStr = ValidateUtils.transferLongToDate("yyyy年MM月dd日HH时", deadtime);
		    	viewHolder.deadtimeTv.setText("有效期至"+ deadtimeStr);
			}else{ 
				viewHolder.deadtimeTv.setVisibility(View.GONE);
			}
			viewHolder.nameTv.setText(pacList.get(position).getPackagename());
			String url = Constants.EMODOU_RESURL + "/"+ pacList.get(position).getPackageicon();
			
			
			imageLoader.displayImage(url, viewHolder.iconImv, options);
			
			if(pacList.get(position).getAvailable() == null)
				viewHolder.availableTv.setVisibility(View.GONE);
			else{
				viewHolder.availableTv.setVisibility(View.VISIBLE);
				if(pacList.get(position).getAvailable().equals("1"))
					viewHolder.availableTv.setText(getResources().getString(R.string.word_changepackage_complete));
				else 
					viewHolder.availableTv.setText(getResources().getString(R.string.word_changepackage_exp));
			}
			
			if(pacList.get(position).getPrice() == null)
				viewHolder.priceTv.setVisibility(View.GONE);
			else{
				viewHolder.priceTv.setVisibility(View.VISIBLE);
				viewHolder.priceTv.setText("价格为 ： " + pacList.get(position).getPrice() +"元/月");
			}
			
			if(packageid == null){
				//这种可能是用户最初登录，什么都没有的时候
				viewHolder.unselectImv.setVisibility(View.VISIBLE);
				viewHolder.selectImv.setVisibility(View.GONE);
			}else{
				if(packageid.equals(pacList.get(position).getPackageid())){
					viewHolder.selectImv.setVisibility(View.VISIBLE);
					viewHolder.unselectImv.setVisibility(View.GONE);
				}else {
					viewHolder.selectImv.setVisibility(View.GONE);
					viewHolder.unselectImv.setVisibility(View.VISIBLE);
				}	
			}
				
			return view;
		}
		
	}
	
	public class  pacItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(ChangePackageActivity.this, ChangeBookActivity.class);
			intent.putExtra("packageid", paclist.get(position).getPackageid());
			startActivity(intent);
		}
	}
	
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.login_imbtn_return:
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
				break;
			
			case R.id.home_changepackage_item_buy:
				break;
			default:
				break;
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

}
