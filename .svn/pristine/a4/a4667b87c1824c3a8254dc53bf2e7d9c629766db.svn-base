package com.emodou.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.emodou.util.Constants;
import com.emodou.util.ValidateUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.entity.BodyParamsEntity;

public class SycStudyRecordService extends Service {

	private String record, catory, useridstring = "";
	private Context contex = SycStudyRecordService.this;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		useridstring = ValidateUtils.getUserid(this);

		if (intent != null) {

			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				catory = bundle.getString("category");
				record = bundle.getString("record");

			}
			HttpUtils http = new HttpUtils();

			String CITY_PATH_JSON = Constants.EMODOU_URL;
			CITY_PATH_JSON += Constants.JS_API2_START + Constants.JS_STUDY_RECORD;
			System.out.println(record);
			record = URLEncoder.encode(record);
			RequestParams params = new RequestParams();
			params.addHeader("Content-type", "application/json");
			// params.setContentType("application/json");
			try {
				params.setBodyEntity(new StringEntity(record, "utf-8"));
			} catch (UnsupportedEncodingException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			System.out.println(record);

			http.send(HttpRequest.HttpMethod.POST, CITY_PATH_JSON, params,
					new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							System.out.println(responseInfo.contentType);
							System.out.println(responseInfo.result);
							System.out.println(responseInfo.result.toString());
							try {
								String res = responseInfo.result.toString();
								int index = res.indexOf("{");
								res = res.substring(index);

								JSONObject result = new JSONObject(res);
								String status = (String) result.get("Status");

								if (status.equals("Success")) { // 同步时间更新到本地数据库
									ValidateUtils.updateLastTimeFromServer(
											result, useridstring, contex);

									// 检查是否有下行数据
									String flag_down = result
											.getString("Flag_Down");
									if (flag_down
											.equals(Constants.STUDY_RECORD_HAVE)) {
										JSONObject record = result
												.getJSONObject("Record");

										// 判断是否有下行课程学习进度
										String classrecordString = record
												.getString("classrecord");
										if (!classrecordString.equals("")) {
											// 更新下行课程学习进度到本地
											ValidateUtils
													.updateStudyRecordFromServer(
															classrecordString,
															useridstring,
															contex);
										}

										// 判断是否有下行时间进度
										String timerecordString = record
												.getString("timerecord");
										if (!timerecordString.equals("")) {
											// 更新下行时间进度到本地
											ValidateUtils
													.updateTimeRecordFromServer(
															timerecordString,
															useridstring,
															contex);
										}

										// 将下行的单词同步信息 存入数据库
										/*String wordrecordString = record
												.getString("wordrecord");

										if (!wordrecordString.equals("")) {
											ValidateUtils
													.updateWordRecordFromServer(
															wordrecordString,
															useridstring,
															contex);
										}*/
									}
								} else {
									String message = result
											.getString("Message");
									if (message.equals("Error_Wrong_Ticket")) {

										Toast.makeText(
												SycStudyRecordService.this,
												"可能已在其他地方登陆", 1).show();
									} else if (message
											.equals("Error_Invalid_Record")) {

										Toast.makeText(
												SycStudyRecordService.this,
												"上传格式错误", 1).show();
									} else {
										Toast.makeText(
												SycStudyRecordService.this,
												"同步失败1", 1).show();
									}

								}

							} catch (JSONException e) {
								Toast.makeText(SycStudyRecordService.this,
										"同步失败2", 1).show();
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Toast.makeText(SycStudyRecordService.this,
									"同步失败3, 网络异常", 1).show();

						}
					});

		}
	}
}
