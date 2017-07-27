package com.emodou.home;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouPickAnswer;
import com.emodou.domain.EmodouPracticeManager;
import com.emodou.domain.EmodouWorkDetail;
import com.emodou.myclass.WorkDetailActivity;
import com.emodou.util.Constants;
import com.emodou.util.LogUtil;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.utils.L;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class QusListenResultActivity extends Activity implements OnClickListener{
	
	private ActionBar actionbar;
	private TextView actionTitle;
	private ImageButton actionReturn;
	private RelativeLayout actionRl;
	
	private String classid, bookid, type;
	private Long testUseTime;
	private int rightNum, rightRate, totalQus;
	private EmodouPickAnswer pickAnswer = new EmodouPickAnswer();
	private Date date;
	private GridViewAdapter gridviewAdatper;
	
	private TextView rightNumTv, rightRateTv, useTimeTv;
	private TextView dateTv;
	private GridView grideView;
	private LinearLayout returnCourse, reviewWrong, tonextWorkLl;
	private ImageView returnCourseIcon;
	
	//是否从做作业界面跳转过来
	private String classroomid, workid, itemid, starttime;
	private List<EmodouPracticeManager> practiceManagerList = new ArrayList<EmodouPracticeManager>();
	private String userid, ticket;
	
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_listen_qusresult);
        
        getParams();
		
        setActionbar();
             
        init();
        
    }
	
	public void getParams(){
		
		type = getIntent().getExtras().getString("type"); 
		classid = getIntent().getExtras().getString("classid");
		bookid = getIntent().getExtras().getString("bookid");
		rightNum = getIntent().getExtras().getInt("rightNum");
		totalQus = getIntent().getExtras().getInt("totalQus");
		rightRate = getIntent().getExtras().getInt("rightRate");
		testUseTime = getIntent().getExtras().getLong("testUseTime");
		pickAnswer = (EmodouPickAnswer)getIntent().getSerializableExtra("pickAnswer");
		
		//是否从做作业界面跳过来
		classroomid = getIntent().getExtras().getString("classroomid");
		workid = getIntent().getExtras().getString("workid");
		itemid = getIntent().getExtras().getString("itemid");
		starttime = getIntent().getExtras().getString("starttime");
		practiceManagerList = (List<EmodouPracticeManager>) getIntent().getSerializableExtra("practiceManagerList");
	}
	
	public void setActionbar(){
 		
 		actionbar = getActionBar();
 		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
 		actionbar.setCustomView(R.layout.actionbar_login);
 		
 		View view = actionbar.getCustomView();
 		
 		//actionbar属性获取操作
 		actionRl = (RelativeLayout)findViewById(R.id.rl_color);
 		if(type.equals(Constants.EMODOU_TYPE_LISTEN)){
 			actionRl.setBackgroundColor(getResources().getColor(R.color.actionbar_listen));
 		}else if(type.equals(Constants.EMODOU_TYPE_READ)){
 			actionRl.setBackgroundColor(getResources().getColor(R.color.actionbar_read));
 		}		
 		actionTitle = (TextView)view.findViewById(R.id.login_ancionbar_text);
 		actionTitle.setText(R.string.home_listen_qusResult_title);
 		actionReturn = (ImageButton)view.findViewById(R.id.login_imbtn_return);
 		actionReturn.setOnClickListener(this);
 				

	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.login_imbtn_return:
				if(type.equals(Constants.EMODOU_TYPE_LISTEN)){
					Intent intent = new Intent(this, ListenActivity.class);
					Bundle mBundle = new Bundle();
					mBundle.putString("type", type);
					mBundle.putString("classid", classid);
					mBundle.putString("bookid", bookid);
					if(classroomid != null){
						mBundle.putString("classroomid", classroomid);
						mBundle.putString("workid", workid);
						mBundle.putString("itemid", itemid);
						mBundle.putString("starttime", starttime);
					}
					intent.putExtras(mBundle);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}else if(type.equals(Constants.EMODOU_TYPE_READ)){
					Intent intent5 = new Intent(this, ReadActivity.class);
					Bundle mBundle5 = new Bundle();
					mBundle5.putString("type", type);
					mBundle5.putString("classid", classid);
					mBundle5.putString("bookid", bookid);
					if(classroomid != null){
						mBundle5.putString("classroomid", classroomid);
						mBundle5.putString("workid", workid);
						mBundle5.putString("itemid", itemid);
						mBundle5.putString("starttime", starttime);
					}
					intent5.putExtras(mBundle5);
					startActivity(intent5);
					break;
				}
								
				break;
				
			case R.id.home_listen_qusresult_returncourselist_layout:
				Intent intent2 = new Intent(this, CourseListActivity.class);
				Bundle mBundle2 = new Bundle();
				mBundle2.putString("type", type);
				mBundle2.putString("bookid", bookid);
				intent2.putExtras(mBundle2);
				intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent2);				
				break;
				
			
			case R.id.home_listen_qusresult_reviewwrong_layout:
				if(type.equals(Constants.EMODOU_TYPE_LISTEN)){
					Intent intent3 = new Intent(this, QusListenReviewActivity.class);
					Bundle mBundle3 = new Bundle();
					mBundle3.putString("type", type);
					mBundle3.putString("classid", classid);
					mBundle3.putString("bookid", bookid);
					intent3.putExtras(mBundle3);
					intent3.putExtra("pickAnswer", (Serializable)pickAnswer);
					startActivity(intent3);
					break;
				}else if(type.equals(Constants.EMODOU_TYPE_READ)){
					Intent intent4 = new Intent(this, QusReadReviewActivity.class);
					Bundle mBundle4 = new Bundle();
					mBundle4.putString("type", type);
					mBundle4.putString("classid", classid);
					mBundle4.putString("bookid", bookid);
					intent4.putExtras(mBundle4);
					intent4.putExtra("pickAnswer", (Serializable)pickAnswer);
					startActivity(intent4);
					break;
				}
			
			case R.id.home_listen_qusresult_toNextWork_layout:
				//先上传，然后回到WorkDetailActivity界面
				if(ValidateUtils.isNetworkConnected(QusListenResultActivity.this)){
					upLoadResult();
				}else{
					Toast.makeText(QusListenResultActivity.this, R.string.prompt_network, 0).show();
				}
				break;
				
			default:
				break;
		}
	}
	
	public void init() {
		
		rightNumTv = (TextView)findViewById(R.id.home_listen_qusresult_rightNumNum);
		rightRateTv = (TextView)findViewById(R.id.home_listen_qusresult_rightRateNum);
		useTimeTv = (TextView)findViewById(R.id.home_listen_qusresult_usetimeTime);
		
		dateTv = (TextView)findViewById(R.id.home_listen_qusresult_date);
		grideView = (GridView)findViewById(R.id.home_listen_qusresult_gridview);
		
		returnCourse = (LinearLayout)findViewById(R.id.home_listen_qusresult_returncourselist_layout);
		returnCourse.setOnClickListener(this);
		reviewWrong = (LinearLayout)findViewById(R.id.home_listen_qusresult_reviewwrong_layout);
		reviewWrong.setOnClickListener(this);
		tonextWorkLl = (LinearLayout)findViewById(R.id.home_listen_qusresult_toNextWork_layout);
		tonextWorkLl.setOnClickListener(this);
		if(classroomid != null){
			tonextWorkLl.setVisibility(View.VISIBLE);
			returnCourse.setVisibility(View.GONE);
			reviewWrong.setVisibility(View.GONE);
		}else{
			tonextWorkLl.setVisibility(View.GONE);
			returnCourse.setVisibility(View.VISIBLE);
			reviewWrong.setVisibility(View.VISIBLE);
		}
		
		rightNumTv.setText(rightNum+"/"+totalQus);
		rightRateTv.setText(rightRate + "%");
		useTimeTv.setText(showTimeCount(testUseTime));
		
		date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = dateFormat.format(date);
		dateTv.setText(dateStr);
		
		gridviewAdatper = new GridViewAdapter(pickAnswer.getRightWrong());
		grideView.setAdapter(gridviewAdatper);
		
		returnCourseIcon = (ImageView)findViewById(R.id.home_listen_qusresult_returncourselist_icon);
		if(type.equals(Constants.EMODOU_TYPE_READ)){
			returnCourseIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_read_qusresult_returncourselist_icon_selector));
		}else{
			returnCourseIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qusresult_returncourselist_icon_selector));
		}
	}
	
	public String showTimeCount(long time) {
		if (time >= 360000000) {
			return "00''";
		}
		String timeCount = "";
		long hourc = time / 3600000;
		String hour = "0" + hourc;
		hour = hour.substring(hour.length() - 2, hour.length());

		long minuec = (time - hourc * 3600000) / (60000);
		String minue = "0" + minuec;
		minue = minue.substring(minue.length() - 2, minue.length());

		long secc = (time - hourc * 3600000 - minuec * 60000) / 1000;
		String sec = "0" + secc;
		sec = sec.substring(sec.length() - 2, sec.length());
		if(time < 60000){
			timeCount = sec + "''";
		}else if(time < 3600000){
			timeCount = minue + "'" + sec + "''";
		}
		return timeCount;
	}
	
	private static class gridViewHolder{
		ImageView rightbcg, wrongbcg;
		TextView rightTv, wrongTv;
	}
	
	class GridViewAdapter extends BaseAdapter{
		
		private List<String> rightWrongList = new ArrayList<String>();
		
		public GridViewAdapter(List<String> rightWrong) {
			super();
			this.rightWrongList = rightWrong;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return totalQus;
		}

		@Override
		public String getItem(int position) {
			// TODO Auto-generated method stub
			return rightWrongList.get(position);
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
			final gridViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.home_listen_qusresult_griditem, parent,false);
				holder = new gridViewHolder();
				holder.rightbcg = (ImageView)view.findViewById(R.id.home_listen_qusresult_griditem_rightbcg);
				holder.wrongbcg = (ImageView)view.findViewById(R.id.home_listen_qusresult_griditem_wrongbcg);
				holder.rightTv = (TextView)view.findViewById(R.id.home_listen_qusresult_griditem_rightText);
				holder.wrongTv = (TextView)view.findViewById(R.id.home_listen_qusresult_griditem_wrongText);
				view.setTag(holder);
			} else {
				holder = (gridViewHolder) view.getTag();
			}
			
			if(rightWrongList.get(position).equals("right")){
				holder.rightbcg.setVisibility(View.VISIBLE);
				holder.rightTv.setVisibility(View.VISIBLE);
				holder.rightTv.setText(position+1+"");
				holder.wrongbcg.setVisibility(View.GONE);
				holder.wrongTv.setVisibility(View.GONE);
			}else if(rightWrongList.get(position).equals("wrong")){
				holder.rightbcg.setVisibility(View.GONE);
				holder.rightTv.setVisibility(View.GONE);
				holder.wrongbcg.setVisibility(View.VISIBLE);
				holder.wrongTv.setVisibility(View.VISIBLE);
				holder.wrongTv.setText(position+1+"");
			}
			return view;
		}
	}
	
	public void upLoadResult() {
		RequestParams params = new RequestParams();
		try {
			userid = ValidateUtils.getUserid(this);
			ticket = ValidateUtils.getTicket(this);
//			params.addBodyParameter("UserId", userid);
//			params.addBodyParameter("ItemId", itemid);
//			params.addBodyParameter("ClassId", classid);
//			params.addBodyParameter("Ticket", ticket);
			
			JSONObject result = new JSONObject();
			result.put("UserId", userid);
			result.put("ItemId", itemid);
			result.put("ClassId", classid);
			result.put("Ticket", ticket);
			
			JSONObject ResultObject = new JSONObject();
			ResultObject.put("classid", classid);
			ResultObject.put("starttime", starttime);
			ResultObject.put("endtime", System.currentTimeMillis()+"");
			ResultObject.put("score", rightRate);
			
			JSONArray detailArray = new JSONArray();
			for(int i = 0; i < practiceManagerList.size(); i++){
				JSONObject detailObject = new JSONObject();
				detailObject.put("ID", i+"");
				detailObject.put("ANS", practiceManagerList.get(i).getChoice());
				detailObject.put("RIGHT", practiceManagerList.get(i).getAsw().getRight());
				detailArray.put(i, detailObject);
			}
			ResultObject.put("detail", detailArray);
			result.put("Result", ResultObject);
			/*把JSON数据转换成String类型使用输出流向服务器写*/
//			params.addBodyParameter("Result", String.valueOf(ResultObject));
			try {
				params.setBodyEntity(new StringEntity(String.valueOf(result)));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpUtils httpUtils = new HttpUtils();
		String path = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_SUBMITWORK; 
		httpUtils.send(HttpMethod.POST, path, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				String res = responseInfo.result.toString();
				res = res.substring(res.indexOf("{"));
				LogUtil.d("upload", res);
				
				try{
					JSONObject result = new JSONObject(res);
					String status = result.getString("Status");
					if(status.equals("Success")){
						Toast.makeText(QusListenResultActivity.this, getResources().getString(R.string.home_listen_queResult_uploadSuc), 0).show();
						//将数据库中的作业详细信息改为DONE
						DbUtils dbUtils = DbUtils.create(QusListenResultActivity.this);
						EmodouWorkDetail workDetail = new EmodouWorkDetail();
						workDetail = dbUtils.findFirst(Selector.from(EmodouWorkDetail.class)
								                               .where("userid","=", userid)
								                               .and("classroomid","=",classroomid)
								                               .and("workid","=",workid)
								                               .and("itemid","=",itemid));
						if(workDetail != null){
							workDetail.setStatus("DONE");
							dbUtils.update(workDetail);
							
							//跳转回WorkDetailActivity界面并直接进行下一项作业
							Intent intent = new Intent(QusListenResultActivity.this, WorkDetailActivity.class);
							intent.putExtra("userid", userid);
							intent.putExtra("ticket", ticket);
							intent.putExtra("classroomid", classroomid);
							intent.putExtra("workid", workid);
							startActivity(intent);
						}
					}else{
						String message = result.getString("Message");
						if(message.equals("Error_Work_Timeout")){
							Toast.makeText(QusListenResultActivity.this, getResources().getString(R.string.home_listen_qusResult_timeout), 0).show();
						}else
							Toast.makeText(QusListenResultActivity.this, getResources().getString(R.string.home_listen_queResult_uploadFail), 0).show();					
					}
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				LogUtil.d("uploadFail", msg);
				Toast.makeText(QusListenResultActivity.this, getResources().getString(R.string.home_listen_queResult_uploadFail), 0).show();
			}
		});
	}
	
	public void onBackPressed() {
		if(type.equals(Constants.EMODOU_TYPE_LISTEN)){
			Intent intent = new Intent(this, ListenActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putString("type", type);
			mBundle.putString("classid", classid);
			mBundle.putString("bookid", bookid);
			if(classroomid != null){
				mBundle.putString("classroomid", classroomid);
				mBundle.putString("workid", workid);
				mBundle.putString("itemid", itemid);
				mBundle.putString("starttime", starttime);
			}
			intent.putExtras(mBundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else if(type.equals(Constants.EMODOU_TYPE_READ)){
			Intent intent5 = new Intent(this, ReadActivity.class);
			Bundle mBundle5 = new Bundle();
			mBundle5.putString("type", type);
			mBundle5.putString("classid", classid);
			mBundle5.putString("bookid", bookid);
			if(classroomid != null){
				mBundle5.putString("classroomid", classroomid);
				mBundle5.putString("workid", workid);
				mBundle5.putString("itemid", itemid);
				mBundle5.putString("starttime", starttime);
			}
			intent5.putExtras(mBundle5);
			startActivity(intent5);
		}
	}
}
