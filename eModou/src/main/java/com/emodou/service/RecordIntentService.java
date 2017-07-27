package com.emodou.service;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.emodou.domain.EmodouClassManager;
import com.emodou.domain.EmodouUserInfo;
import com.emodou.domain.EmodouWordManager;
import com.emodou.util.Constants;
import com.emodou.util.LogUtil;
import com.emodou.util.MyApplication;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.google.gson.JsonObject;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.utils.L;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

public class RecordIntentService extends IntentService{
	
	private String uploadtype, userid, ticket, lastRecordTime;
	private String bookid;
	private int wordClassCount;
	private ArrayList<String> wordClassArray = new ArrayList<String>(); 

	public RecordIntentService() {
		super("RecordIntentService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
		uploadtype = intent.getStringExtra("uploadtype");
		ticket = ValidateUtils.getTicket(this);
		EmodouUserInfo userInfo;
		final DbUtils dbUtils = DbUtils.create(this);
		try {
			userInfo = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
			lastRecordTime = userInfo.getLastRecordTime();
		} catch (DbException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if(!TextUtils.isEmpty(uploadtype)&&uploadtype.equals("word")){
			LogUtil.e("wordrecord", "start");
			userid = intent.getStringExtra("userid");
			//wordClassArray = intent.getExtras().getStringArray("wordClassArray");
			wordClassArray = intent.getStringArrayListExtra("wordClassArray");
			wordClassCount = wordClassArray.size();
			LogUtil.d("wordClassArray", wordClassArray.toString());
			LogUtil.d("wordClassCount", wordClassCount +"");
			
			if(wordClassCount != 0){
				//在退出单词学习时上传单词同步信息，上传的是单词的学习信息,不需要返回任何信息,当点击返回时，上传所选课程组的所有单词
				RequestParams params = new RequestParams();
				try {
//					JSONObject result = new JSONObject();
//					result.put("userid", userid);
//					result.put("flag_up", "1");
//					result.put("lasttime", lastRecordTime);
//					result.put("return_type", new JSONArray());
//					result.put("ticket", ticket);
					
					JSONStringer resultStringer = new JSONStringer();
					resultStringer.object();
					resultStringer.key("userid").value(userid);
					resultStringer.key("flag_up").value("1");
					if(!TextUtils.isEmpty(lastRecordTime))
						resultStringer.key("lasttime").value(lastRecordTime);
					else 
						resultStringer.key("lasttime").value("");
					resultStringer.key("return_type").value(new JSONArray());
					resultStringer.key("ticket").value(ticket);
					resultStringer.endObject();
					
//					JSONObject record = new JSONObject();
//					JSONArray wordRecordArray = new JSONArray();
					String wordRecordStr = "\"wordrecord\":[";
					int id = 0;//因为上面的Array涉及到两个循环，所以要进行计数
					for(int i = 0; i < wordClassCount; i++){
						String classid = wordClassArray.get(i);
						List<EmodouWordManager> wordManagerList = new ArrayList<EmodouWordManager>();
						
						try {
							wordManagerList = dbUtils.findAll(Selector.from(EmodouWordManager.class)
									                                .where("userid","=",userid)
									                                .and("classid","=",classid));
							if(wordManagerList != null){
								for(int j = 0; j<wordManagerList.size();j++){
//									JSONObject wordjson = new JSONObject();
//									wordjson.put("wordname", wordManagerList.get(j).getWordname());
//									wordjson.put("cright", wordManagerList.get(j).getCright()+"");
//									wordjson.put("cprompt", wordManagerList.get(j).getCprompt()+"");
//									wordjson.put("cwrong", wordManagerList.get(j).getCwrong()+"");
//									wordjson.put("last", wordManagerList.get(j).getLast()+"");
									JSONStringer wordStringer = new JSONStringer();
									wordStringer.object();
									wordStringer.key("wordname").value(wordManagerList.get(j).getWordname());
									wordStringer.key("cright").value(wordManagerList.get(j).getCright()+"");
									wordStringer.key("cprompt").value(wordManagerList.get(j).getCprompt()+"");
									wordStringer.key("cwrong").value(wordManagerList.get(j).getCwrong()+"");
									wordStringer.key("last").value(wordManagerList.get(j).getLast()+"");
									wordStringer.endObject();
									wordRecordStr += wordStringer.toString() +",";
									//wordRecordArray.put(id, wordjson);
									id++;
								}
								
							}
							
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if(id != 0)
						wordRecordStr = wordRecordStr.substring(0, wordRecordStr.length()-1);
					
					wordRecordStr += "]";
					
					String recordStr = ",\"record\":{" + "\"timerecord\":[]," + "\"classrecord\":[]," + wordRecordStr +"}";
					String resultStr = resultStringer.toString();
					
//					record.put("timerecord", new JSONArray());
//					record.put("classrecord", new JSONArray());
//					record.put("wordrecord", wordRecordArray);
//					result.put("record", record);
					
					int start = resultStr.indexOf("}");
					StringBuilder resultBuilder = new StringBuilder(resultStr);
					resultBuilder.insert(start, recordStr);
					
					LogUtil.d("resultStr", resultBuilder.toString());
					String paramsEncode = URLEncoder.encode(resultBuilder.toString());
					
					try {
						params.setBodyEntity(new StringEntity(paramsEncode));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				
				HttpUtils httpUtils = new HttpUtils();
				String path = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_STUDY_RECORD;
				httpUtils.send(HttpMethod.POST, path, params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						// TODO Auto-generated method stub
						String res = responseInfo.result.toString();
						LogUtil.d("uploadSuc", res);
						try{
							JSONObject result = new JSONObject(res);
							String status = result.getString("Status");
							if(status.equals("Success")){
								Toast.makeText(MyApplication.getContext(), R.string.record_word_upload_success, 0).show();
								
								String time = result.getString("Time");
								EmodouUserInfo userInfo2 = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
								userInfo2.setLastRecordTime(time);
								dbUtils.update(userInfo2);
							}else{
								Toast.makeText(MyApplication.getContext(), R.string.record_word_upload_failed, 0).show();
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
						Toast.makeText(MyApplication.getContext(), R.string.record_word_upload_failed, 0).show();
					}
				});
			}
			
			
			
		}else if(!TextUtils.isEmpty(uploadtype)&&uploadtype.equals("class")){
			//上传单词同步信息，上传的是单词的学习信息,不需要返回任何信息,当点击返回时，上传所选课程组的所有单词
			LogUtil.e("classrecord", "start");
			userid = intent.getStringExtra("userid");
			bookid = intent.getStringExtra("bookid");
			RequestParams params = new RequestParams();
			try {
//				JSONObject result = new JSONObject();
//				result.put("userid", userid);
				String paramsEncode = null;
				JSONStringer jsonStringer = new JSONStringer();
				jsonStringer.object();
				jsonStringer.key("userid");jsonStringer.value(userid);
				jsonStringer.key("flag_up");jsonStringer.value("1");
				if(!TextUtils.isEmpty(lastRecordTime)){
					jsonStringer.key("lasttime");jsonStringer.value(lastRecordTime);
				}else{
					jsonStringer.key("lasttime");jsonStringer.value("");
				}
				JSONArray emptyArray = new JSONArray();
				LogUtil.d("emptyarray", emptyArray.toString());
				jsonStringer.key("return_type");jsonStringer.value(emptyArray);
				jsonStringer.key("ticket");jsonStringer.value(ticket);
				
				
				//JSONObject record = new JSONObject();
				String classrecordStr = "\"classrecord\":[";
				//JSONArray classRecordArray = new JSONArray();
				List<EmodouClassManager> classManagerList = new ArrayList<EmodouClassManager>();
				try {
					classManagerList = dbUtils.findAll(Selector.from(EmodouClassManager.class)
							                                   .where("userid","=",userid)
							                                   .and("bookid","=",bookid));
				} catch (DbException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(classManagerList != null && classManagerList.size() != 0){
					for(int i = 0; i<classManagerList.size(); i++){
						EmodouClassManager classManager = classManagerList.get(i);
						if(classManager.getState() != Constants.EMODOU_CLASS_STATE_NOT_LEAREN){
							//因为上传的学习记录中，未学习的不上传
//							JSONObject classObject = new JSONObject();
//							classObject.put("bookid", bookid);
//							classObject.put("classid", classManager.getClassid());
//							classObject.put("status", classManager.getState());
//							classObject.put("score", classManager.getScore()); 
//							classObject.put("studytime", "");
							JSONStringer jsonStringer2 = new JSONStringer();
							jsonStringer2.object();
							jsonStringer2.key("bookid");jsonStringer2.value(bookid);
							jsonStringer2.key("classid");jsonStringer2.value(classManager.getClassid());
							jsonStringer2.key("status");jsonStringer2.value(classManager.getState()+"");
							jsonStringer2.key("score");jsonStringer2.value(classManager.getScore());
							jsonStringer2.key("studytime");jsonStringer2.value("");
							jsonStringer2.endObject();
							classrecordStr += jsonStringer2.toString()+",";
							//classRecordArray.put(jsonStringer2);
						}
					}
					classrecordStr = classrecordStr.substring(0, classrecordStr.length() - 1);
					classrecordStr += "],";
//					if(count == 0){
//						JSONObject classObject = new JSONObject();
//						classObject.put("bookid", bookid);
//						classObject.put("classid", "");
//						classObject.put("status", "");
//						classObject.put("score", ""); 
//						classObject.put("studytime", "");
//						
////						JSONStringer jsonStringer2 = new JSONStringer();
////						jsonStringer2.object();
////						jsonStringer2.key("bookid");jsonStringer2.value(bookid);
////						jsonStringer2.key("classid");jsonStringer2.value("");
////						jsonStringer2.key("status");jsonStringer2.value("");
////						jsonStringer2.key("score");jsonStringer2.value("");
////						jsonStringer2.key("studytime");jsonStringer2.value("");
////						jsonStringer2.endObject();
//								
////						String str = jsonStringer2.toString();
////						JSONObject classObject = new JSONObject(str);
////						LogUtil.d("str", str);
////						LogUtil.d("classObject", classObject.toString());
//						classRecordArray.put(0,classObject);
//						LogUtil.d("classArray", classRecordArray.toString());
//					}
				}
//				JSONArray timeRecordArray = new JSONArray();
//				record.put("timerecord", timeRecordArray);
//				
//				JSONArray wordRecordArray = new JSONArray();
//				record.put("wordrecord", wordRecordArray);
//				
//				record.put("classrecord", classRecordArray);
				
				
				
//				JSONStringer recordStringer = new JSONStringer();
//				recordStringer.object();
//	
//				recordStringer.key("timerecord").value(new JSONArray());
//				recordStringer.key("classrecord").value(classRecordStringer);
//				recordStringer.key("wordrecord").value(new JSONArray());
////				
//				recordStringer.endObject();  
				
				String recordStr = ",\"record\":{" + "\"timerecord\":[]," + classrecordStr + "\"wordrecord\":[]"+"}";
				//String recordStr = ",\"record\":{" + "\"timerecord\":[]," + "\"classrecord\":[]," + "\"wordrecord\":[]"+"}";
				
				
				//result.put("record", record);
				//jsonStringer.key("record");jsonStringer.value(record);
				LogUtil.d("recordStringer", recordStr);
				jsonStringer.endObject();		
				//jsonStringer.key("record").value(recordStringer.toString());
				String resultStr = jsonStringer.toString();
//				String recordStr = +recordStringer.toString();
				int start = resultStr.indexOf("}");
				StringBuilder resultBuilder = new StringBuilder(resultStr);
				resultBuilder.insert(start, recordStr);
				
//				result.put("flag_up", "1");
//				result.put("lasttime", lastRecordTime);
//				JSONArray emptyArray = new JSONArray();
//				LogUtil.d("emptyarray", emptyArray.toString());
//				result.put("return_type", emptyArray);
//				result.put("ticket", ticket);
//				LogUtil.d("classresult", String.valueOf(result));
				
				
				
				LogUtil.d("resultBuilder", resultBuilder.toString());
				paramsEncode = URLEncoder.encode(resultBuilder.toString());
				LogUtil.d("paramsEncode", paramsEncode);
				
				try {
					//params.setBodyEntity(new StringEntity(String.valueOf(result)));
					//params.setBodyEntity(new StringEntity(String.valueOf(jsonStringer)));
					params.setBodyEntity(new StringEntity(paramsEncode));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			
			HttpUtils httpUtils = new HttpUtils();
			String path = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_STUDY_RECORD;
			LogUtil.d("path", path);
			httpUtils.send(HttpMethod.POST, path, params, new RequestCallBack<String>() {

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					// TODO Auto-generated method stub
					String res = responseInfo.result.toString();
					LogUtil.d("uploadSuc", res);
					try{
						JSONObject result = new JSONObject(res);
						String status = result.getString("Status");
						if(status.equals("Success")){
							Toast.makeText(MyApplication.getContext(), R.string.record_class_upload_success, 0).show();
							
							//手机上的时间是可以调的
							String time = result.getString("Time");
							EmodouUserInfo userInfo2 = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
							userInfo2.setLastRecordTime(time);
							dbUtils.update(userInfo2);
						}else{
							Toast.makeText(MyApplication.getContext(), R.string.record_class_upload_failed, 0).show();
						}				
					}catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(HttpException error, String msg) {
					// TODO Auto-generated method stub
					LogUtil.e("uploadfailClass", msg);
					Toast.makeText(MyApplication.getContext(), R.string.record_class_upload_failed, 0).show();
				}
			});
		}
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
