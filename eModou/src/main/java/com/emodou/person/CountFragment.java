package com.emodou.person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouClassManager;
import com.emodou.domain.EmodouUserInfo;
import com.emodou.util.Constants;
import com.emodou.util.LogUtil;
import com.emodou.util.ValidateUtils;
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

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class CountFragment extends Fragment{
	
	private View countView;
	private ProgressDialog progressDialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		countView = inflater.inflate(R.layout.person_count, container, false);
		
		//去服务器请求时间信息
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Loading...");
		progressDialog.show();
		
		final DbUtils dbUtils = DbUtils.create(getActivity());
		EmodouUserInfo userInfo = new EmodouUserInfo();
		final String userid = userInfo.getUserid();
		try {
			userInfo = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(userInfo != null){
			RequestParams upRecord = new RequestParams();
			//JSONObject upRecord = new JSONObject();
			upRecord.addBodyParameter("userid", ValidateUtils.getUserid(getActivity()));
			upRecord.addBodyParameter("flag_up", "0");
			upRecord.addBodyParameter("lasttime", userInfo.getLastRecordTime());
			
			JSONArray returnTypeArray = new JSONArray();
			try {
				returnTypeArray.put(0, "time");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
					ValidateUtils.WriteTxtFile(res, "CountRecord");
					try{
						JSONObject result = new JSONObject(res);
						String status = result.getString("Status");
						if(status.equals("Success")){
							String time = result.getString("Time");
							
							
							String record = result.getString("Record");
							JSONObject recordObject = new JSONObject(record);
							
							JSONObject timeRecordObject = new JSONObject();
							timeRecordObject = recordObject.getJSONObject("TimeRecord");
							JSONObject weeklyObject = timeRecordObject.getJSONObject("Weekly");
							String monday = weeklyObject.getString("monday");
							String tuesday = weeklyObject.getString("tuesday");
							String wednesday = weeklyObject.getString("wednesday");
							String thursday = weeklyObject.getString("thursday");
							String friday = weeklyObject.getString("friday");
							String saturday = weeklyObject.getString("saturday");
							String sunday = weeklyObject.getString("sunday");
							
							String total = timeRecordObject.getString("Total");
							
							Toast.makeText(getActivity(), R.string.record_count_download_success, 0).show();
							progressDialog.dismiss();
						}else{
							Toast.makeText(getActivity(), R.string.record_count_download_failed, 0).show();
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
					Toast.makeText(getActivity(), R.string.record_count_download_failed, 0).show();
					progressDialog.dismiss();
				}
			});
		}
		return countView;
	}
}
