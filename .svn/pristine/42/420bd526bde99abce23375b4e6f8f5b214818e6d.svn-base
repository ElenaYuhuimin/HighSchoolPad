package com.emodou.set;

import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouUserInfo;
import com.emodou.login.EmodouLoginAcvitity;
import com.emodou.util.Constants;
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

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePsdFragment extends Fragment implements OnClickListener{
	
	private View changePsdView;
	
	private EditText edtOldpsd, edtNewpsd, edtConfirmpsd;
	private Button confirmbtn;
	private ProgressDialog progressDialog;
	
	String userid;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
		changePsdView = inflater.inflate(R.layout.set_fragment_changepsd, container, false);
        
        init();
        return changePsdView;
    } 
	
	public void init(){
		
		edtOldpsd = (EditText)changePsdView.findViewById(R.id.set_fragment_changepsd_oldpsd);
		edtNewpsd = (EditText)changePsdView.findViewById(R.id.set_fragment_changepsd_newpsd);
		edtConfirmpsd = (EditText)changePsdView.findViewById(R.id.set_fragment_changepsd_confirmpsd);
		
		confirmbtn = (Button)changePsdView.findViewById(R.id.set_fragment_changepsd_confirm);
		confirmbtn.setOnClickListener(this);
		
		DbUtils dbUtils = DbUtils.create(getActivity());
        try {
			EmodouUserInfo user = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
			if(user!=null){
				userid = user.getPhone();
			}
			
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
			case R.id.set_fragment_changepsd_confirm:
				progressDialog = new ProgressDialog(getActivity());      
	            progressDialog.setIndeterminate(true);
	            progressDialog.setMessage("Loading...");
	            progressDialog.show();
	            
	            String old = edtOldpsd.getText().toString().trim();
	            String newpas = edtNewpsd.getText().toString().trim();
	            String confirm = edtConfirmpsd.getText().toString().trim();
	          if (TextUtils.isEmpty(old)
						|| TextUtils.isEmpty(confirm) || TextUtils.isEmpty(newpas)) {
	        	     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
		  			 .setTitle(R.string.prompt)
		  			 .setMessage("密码输入不能为空")
		  			 .setCancelable(false)
		  			 .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
	  		           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
	  		           }					            
		  			 }); 
					progressDialog.dismiss();
					break;
				} else {
					if (!newpas.equals(confirm)) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
			  			 .setTitle(R.string.prompt)
			  			 .setMessage("新密码两次输入不相同")
			  			 .setCancelable(false)
			  			 .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
		  		           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
		  		           }					            
			  			 }); 
						progressDialog.dismiss();
					} else{
						String	CITY_PATH_JSON = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_CHANGE_PASSWORD;

						RequestParams params = new RequestParams();
						params.addBodyParameter("userid", ValidateUtils.getUserid(getActivity()));
						params.addBodyParameter("ticket", ValidateUtils.getTicket(getActivity()));
						params.addBodyParameter("old", old);
						params.addBodyParameter("new", confirm);

						HttpUtils http = new HttpUtils();
						http.send(HttpMethod.POST, CITY_PATH_JSON, params,
								new RequestCallBack<String>() {

									@Override
									public void onSuccess(
											ResponseInfo<String> responseInfo) {
										System.out.println(responseInfo.contentType);
										System.out.println(responseInfo.result);
										System.out.println(responseInfo.result
												.toString());
										String res = responseInfo.result.toString();
										res = res.substring(res.indexOf("{"));

										JSONObject result;
										try {
											result = new JSONObject(res);
											String status = (String) result.get("Status");
											if (status.equals("Success")) {
												Toast.makeText(getActivity(), "修改密码成功", 1).show();
												Intent intent = new Intent(getActivity(),EmodouLoginAcvitity.class);
												intent.putExtra("rgstype", userid);
												intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(intent);
												
												
											}else{
												Toast.makeText(getActivity(), "修改密码失败", 1).show();
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										progressDialog.dismiss();
									
									}

									@Override
									public void onFailure(HttpException error,
											String msg) {
										progressDialog.dismiss();
										Toast.makeText(getActivity(), "登录失败，请检查大小写是否正确", 1).show();

									}
								});
						}
					
				}

				break;

	
			default:
				break;
		}
	}
}
