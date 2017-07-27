package com.emodou.home;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouWordClass;
import com.emodou.domain.EmodouWordForWlist;
import com.emodou.domain.EmodouWordManager;
import com.emodou.domain.EmodouWorkDetail;
import com.emodou.myclass.WorkDetailActivity;
import com.emodou.util.Constants;
import com.emodou.util.LogUtil;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.iflytek.cloud.InitListener;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;

import android.R.integer;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Contactables;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WordTestResultActivity extends Activity implements OnClickListener{
	
	//actionbar及其相关
	private RelativeLayout actionbarRl;
	private ActionBar actionbar;
	private TextView titletext;
	private ImageButton imbReturn, imbDelete;
		
		
	private HoloCircularProgressBar cirProgressBar;
	private TextView progressTv;
	private ListView wrongWordList;
	private RelativeLayout leaveRl, retestRl;
	private TextView rightNumTv, wrongNumTv, rightAllTv;
	private LinearLayout buttonLl;
	
	private String bookid, userid;
	private int rightBigRate;//正确率，总体的分数，因为要上传这个，所以变成全局变量
	private List<EmodouWordManager> wordManagerList = new ArrayList<EmodouWordManager>();
	private List<EmodouWordManager> wordManaWrongList = new ArrayList<EmodouWordManager>();
	private WordListAdapter wordListAdapter;
	
	//是否从做题界面跳转过来
	private String ticket, classroomid, workid, itemid, starttime, classid;
	private RelativeLayout uploadRl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.word_test_result);
		
		setActionbar();
		init();
	}
	
	public void setActionbar(){
 		
 		actionbar = getActionBar();
 		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
 		actionbar.setCustomView(R.layout.actionbar_courselist);
 		
 		View view = actionbar.getCustomView();
 		
 		//actionbar属性获取操作
 		actionbarRl = (RelativeLayout)view.findViewById(R.id.couselist_color);
 		actionbarRl.setBackgroundColor(getResources().getColor(R.color.actionbar_word));
 		titletext = (TextView)view.findViewById(R.id.courselist_ancionbar_text);	
 		imbReturn = (ImageButton)view.findViewById(R.id.courselist_imbtn_return);
 		imbReturn.setOnClickListener(this);
 		imbDelete = (ImageButton)view.findViewById(R.id.courselist_imbtn_delete);
 		imbDelete.setVisibility(View.GONE);
 		
 		
 		titletext.setText(getResources().getString(R.string.word_test_result_title));				
 	}
	
	public void init() {
		cirProgressBar = (HoloCircularProgressBar)findViewById(R.id.word_main_cirprogress);
		cirProgressBar.setThumbEnabled(false);
		cirProgressBar.setMarkerEnabled(false);
		cirProgressBar.setProgressColor(getResources().getColor(R.color.actionbar_word));
		cirProgressBar.setProgressBackgroundColor((int) Color.rgb(205, 205, 205));
		cirProgressBar.setWheelSize(30);
		
		progressTv = (TextView)findViewById(R.id.word_test_result_rightRateNum);
		wrongWordList = (ListView)findViewById(R.id.word_test_result_list);
		rightNumTv = (TextView)findViewById(R.id.word_test_result_rightNumTv);
		wrongNumTv = (TextView)findViewById(R.id.word_test_result_wrongNumTv);
		rightAllTv = (TextView)findViewById(R.id.word_test_result_rightAll);
		buttonLl = (LinearLayout)findViewById(R.id.word_test_result_buttonRl);
		leaveRl = (RelativeLayout)findViewById(R.id.word_test_result_leaveRl);
		leaveRl.setOnClickListener(this);
		retestRl = (RelativeLayout)findViewById(R.id.word_test_result_retestRl);
		retestRl.setOnClickListener(this);
		
		bookid = getIntent().getStringExtra("bookid");
		userid = getIntent().getStringExtra("userid");
		//是否从做题界面跳转过来
		ticket = getIntent().getStringExtra("ticket");
		classroomid = getIntent().getStringExtra("classroomid");
		workid = getIntent().getStringExtra("workid");
		itemid = getIntent().getStringExtra("itemid");
		starttime = getIntent().getStringExtra("starttime");
		classid = getIntent().getStringExtra("classid");
		
		uploadRl = (RelativeLayout)findViewById(R.id.word_test_result_uploadRl);
		uploadRl.setOnClickListener(this);
		
		if(ticket!=null){
			imbReturn.setVisibility(View.GONE);
			buttonLl.setVisibility(View.GONE);
			uploadRl.setVisibility(View.VISIBLE);
		}else{
			imbReturn.setVisibility(View.VISIBLE);
			buttonLl.setVisibility(View.VISIBLE);
			uploadRl.setVisibility(View.GONE);
		}
		
		getWordManagerList();
		
		calculate();
		
		
	}
	public void getWordManagerList() {
		// TODO Auto-generated method stub
		DbUtils dbUtils = DbUtils.create(this);
		try {
			List<EmodouWordClass> wordClasses = new ArrayList<EmodouWordClass>();
			wordClasses = dbUtils.findAll(Selector.from(EmodouWordClass.class)
												  .where("userid","=",userid)
												  .and("bookid","=",bookid)
												  .and("state","=",Constants.WORD_CLASS_SELECTED));
			for(EmodouWordClass wordClass : wordClasses){
				List<EmodouWordManager> wordManagerPartList = new ArrayList<EmodouWordManager>();
				wordManagerPartList = dbUtils.findAll(Selector.from(EmodouWordManager.class)
						                                      .where("userid","=",userid)
						                                      .and("classid","=",wordClass.getClassid()));
				if(wordManagerPartList != null && wordManagerPartList.size() != 0)
					wordManagerList.addAll(wordManagerPartList);
			}
		} catch (DbException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void calculate() {
		
		wordManaWrongList.clear();
		int wrongNum = 0;
		for(EmodouWordManager wordManager : wordManagerList){
			if(!(wordManager.getEntitleWrongtimes() == 0 && wordManager.getCntitleWrongtimes() == 0)){
				wordManaWrongList.add(wordManager);
				wrongNum ++ ;
			}		
		}
		if(wrongNum == 0){
			wrongWordList.setVisibility(View.GONE);
			rightAllTv.setVisibility(View.VISIBLE);
			rightNumTv.setText("正确单词数：" + wordManagerList.size());
			wrongNumTv.setText("错误单词数: "+ 0);
			progressTv.setText("100%");
			cirProgressBar.setProgress((float)1);
		}else{
			wrongWordList.setVisibility(View.VISIBLE);
			rightAllTv.setVisibility(View.GONE);
			
			int rightNum = wordManagerList.size() - wrongNum;
			
			rightNumTv.setText("正确单词数：" +rightNum);
			wrongNumTv.setText("错误单词数: "+ wrongNum);
			
			rightBigRate = rightNum * 100/ wordManagerList.size();
			float rightRate = ((float)rightNum * 100/ wordManagerList.size())/100;
			progressTv.setText(rightBigRate + "%");
			cirProgressBar.setProgress(rightRate);
			
			wordListAdapter = new WordListAdapter(wordManaWrongList, this);
			wrongWordList.setAdapter(wordListAdapter);
		}
	}
	
	private static class classViewHolder{
		TextView wordnameTv, wrongNumTv;
		ImageView addedImv, notaddImv;
	}
	
	private class WordListAdapter extends BaseAdapter{
		
		private List<EmodouWordManager> wordWrongList = new ArrayList<EmodouWordManager>();
		private LayoutInflater inflater;
		
		public WordListAdapter(List<EmodouWordManager> wordWrongList, Context context) {
			this.wordWrongList = wordWrongList;
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return wordWrongList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return wordWrongList.get(position);
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
			final classViewHolder classholder;
			if(convertView == null){
				view = inflater.inflate(R.layout.word_test_result_listitem, parent, false);
				classholder = new classViewHolder();
				
				classholder.wordnameTv = (TextView)view.findViewById(R.id.word_test_result_listItem_wordname);
				classholder.wrongNumTv = (TextView)view.findViewById(R.id.word_test_result_listItem_wrongNum);
				classholder.addedImv = (ImageView)view.findViewById(R.id.word_test_result_listItem_added);
				classholder.notaddImv = (ImageView)view.findViewById(R.id.word_test_result_listItem_notadd);
				
				view.setTag(classholder);
			}else{
				classholder = (classViewHolder)view.getTag();
			}
			
			//根据错误次数，进行重新排序
			Collections.sort(wordWrongList, new Comparator<EmodouWordManager>() {
	    		public int compare(EmodouWordManager word1, EmodouWordManager word2) {
					return (new Integer(word1.getEntitleWrongtimes() + word1.getCntitleWrongtimes()))
							.compareTo(new Integer(word2.getEntitleWrongtimes() + word2.getCntitleWrongtimes()));
				}
			});
			
			final EmodouWordManager wordManager = wordWrongList.get(position);
			classholder.wordnameTv.setText(wordManager.getWordname());
			classholder.wrongNumTv.setText("错" + (wordManager.getEntitleWrongtimes() + wordManager.getCntitleWrongtimes()) + "次");
			if(wordManager.getIsAddToNewWordsBood().equals(Constants.WORD_ISADDTONEW_YES)){
				classholder.addedImv.setVisibility(View.VISIBLE);
				classholder.notaddImv.setVisibility(View.GONE);
			}	
			else if(wordManager.getIsAddToNewWordsBood().equals(Constants.WORD_ISADDTONEW_NO)){
				classholder.notaddImv.setVisibility(View.VISIBLE);
				classholder.addedImv.setVisibility(View.GONE);
			}
			classholder.notaddImv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Toast.makeText(WordTestResultActivity.this, wordManager.getWordname() + "已经添加到生词本", 0).show();
					ValidateUtils.addToNewWord(WordTestResultActivity.this, wordManager.getWordname(), wordManager.getClassid());
					classholder.addedImv.setVisibility(View.VISIBLE);
					classholder.notaddImv.setVisibility(View.GONE);
				}
			});
			
			classholder.addedImv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Toast.makeText(WordTestResultActivity.this, wordManager.getWordname() + "已经从生词本中删除", 0).show();
					ValidateUtils.deleteFromNewWord(WordTestResultActivity.this, wordManager.getWordname(), wordManager.getClassid());
					classholder.addedImv.setVisibility(View.GONE);
					classholder.notaddImv.setVisibility(View.VISIBLE);
				}
			});
			
			
			
			return view;
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.word_test_result_leaveRl:
				Intent intent = new Intent(WordTestResultActivity.this, WordlistActivity.class);
				intent.putExtra("bookid", bookid);
				startActivity(intent);
				break;
				
			case R.id.word_test_result_retestRl:
				Intent intent2 = new Intent(WordTestResultActivity.this, WordTestActivity.class);
				intent2.putExtra("bookid", bookid);
				intent2.putExtra("userid", userid);
				startActivity(intent2);
				break;
				
			case R.id.courselist_imbtn_return:
				Intent intent3 = new Intent(WordTestResultActivity.this, WordlistActivity.class);
				intent3.putExtra("bookid", bookid);
				startActivity(intent3);
				break;
				
			case R.id.word_test_result_uploadRl:
				uploadWord();
				break;
	
			default:
				break;
		}
	}
	
	public void uploadWord() {
		RequestParams params = new RequestParams();
		try {
			JSONObject result = new JSONObject();
			result.put("UserId", userid);
			result.put("ItemId", itemid);
			result.put("ClassId", classid);
			result.put("Ticket", ticket);
			
			JSONObject ResultObject = new JSONObject();
			ResultObject.put("classid", classid);
			ResultObject.put("starttime", starttime);
			ResultObject.put("endtime", System.currentTimeMillis()+"");
			ResultObject.put("score", rightBigRate);
			
			JSONArray detailArray = new JSONArray();
			for(int i = 0; i < wordManagerList.size(); i++){
				JSONObject detailObject = new JSONObject();
				String wordname = wordManagerList.get(i).getWordname();
				int wrongint = wordManagerList.get(i).getCntitleWrongtimes() + wordManagerList.get(i).getEntitleWrongtimes();
				String wrong = wrongint +"";
				detailObject.put("WRONG", wrong);
				detailObject.put("WORD", wordname);
				detailArray.put(i, detailObject);
			}
			ResultObject.put("detail", detailArray);
			result.put("Result", ResultObject);
			/*把JSON数据转换成String类型使用输出流向服务器写*/
//			params.addBodyParameter("Result", String.valueOf(ResultObject));
			try {
				params.setBodyEntity(new StringEntity(String.valueOf(result)));
				LogUtil.d("params", String.valueOf(result));
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
						Toast.makeText(WordTestResultActivity.this, getResources().getString(R.string.home_listen_queResult_uploadSuc), 0).show();
						//将数据库中的作业详细信息改为DONE
						DbUtils dbUtils = DbUtils.create(WordTestResultActivity.this);
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
							Intent intent = new Intent(WordTestResultActivity.this, WorkDetailActivity.class);
							intent.putExtra("userid", userid);
							intent.putExtra("ticket", ticket);
							intent.putExtra("classroomid", classroomid);
							intent.putExtra("workid", workid);
							startActivity(intent);
						}
					}else{
						String message = result.getString("Message");
						if(message.equals("Error_Work_Timeout")) 
							Toast.makeText(WordTestResultActivity.this, getResources().getString(R.string.home_listen_qusResult_timeout), 0).show();
						else 
							Toast.makeText(WordTestResultActivity.this, getResources().getString(R.string.home_listen_queResult_uploadFail), 0).show();					
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
				Toast.makeText(WordTestResultActivity.this, getResources().getString(R.string.home_listen_queResult_uploadFail), 0).show();
			}
		});
	}	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent3 = new Intent(WordTestResultActivity.this, WordlistActivity.class);
		intent3.putExtra("bookid", bookid);
		startActivity(intent3);
		super.onBackPressed();
	}

}
