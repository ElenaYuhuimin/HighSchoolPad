package com.emodou.set;



import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.emodou.domain.EmodouUserInfo;
import com.emodou.login.EmodouRegisterActivity;
import com.emodou.util.Constants;
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

public class MobileFragment extends Fragment implements OnClickListener{
		
	private View mobileView;
	
	private String userid, mobileStr, ticket, oldmobilestr, pinStr;
	private String CITY_PATH_JSON= Constants.EMODOU_URL;
	private String MOBILE_PATH_JSON = Constants.EMODOU_URL;
	private TextView mobiletext, oldmobileText;
	private EditText newmobile, pinEdt;
	private Button btnGetpin, btnChange;
	private TimeCount time;
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
        mobileView = inflater.inflate(R.layout.set_fragment_mobile, container, false);
        
        init();
        return mobileView;
    } 
	
	public void init(){
		
		DbUtils dbUtils = DbUtils.create(getActivity());
        try {
			EmodouUserInfo user = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
			if(user!=null){
				userid = user.getUserid();
				ticket = user.getTicket();
				oldmobilestr = user.getPhone();
			}
			
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        mobiletext = (EditText)mobileView.findViewById(R.id.set_fragment_mobile_mobile);
        oldmobileText = (TextView)mobileView.findViewById(R.id.set_fragment_mobile_text3);
        oldmobileText.setText(oldmobilestr);
        
        btnGetpin = (Button)mobileView.findViewById(R.id.set_fragment_mobile_getpin);
        btnGetpin.setOnClickListener(this);
        
        btnChange = (Button)mobileView.findViewById(R.id.set_fragment_mobile_change);
        btnChange.setOnClickListener(this);
        
        pinEdt = (EditText)mobileView.findViewById(R.id.set_fragment_mobile_inputpin);
        
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.set_fragment_mobile_getpin:
				mobileStr = mobiletext.getText().toString().trim();
				
				if(!ValidateUtils.isNetworkConnected(getActivity())){
					Toast.makeText(getActivity(), "请检查网络连接", 0).show();
					break;
				}
				else if (TextUtils.isEmpty(mobileStr)) {
					
					AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity())
					.setTitle("您未输入您的手机号码")
					.setMessage("请先输入您的手机号码")
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
				           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
				           }					            
				       }); 
				
					builder2.create().show();	
					
					break;
				} else if (!ValidateUtils.isMobile(mobileStr)) {
					
					AlertDialog.Builder builder3 = new AlertDialog.Builder(getActivity())
					.setTitle("手机号码错误")
					.setMessage("您输入的是一个无效的手机号码")
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
				           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
				           }					            
				       }); 
				
					builder3.create().show();	

					break;
				} 
				
				btnGetpin.setClickable(false);
				RequestParams params = new RequestParams();
				params.addBodyParameter("type", "bind");
				params.addBodyParameter("phone", mobileStr);
				HttpUtils http = new HttpUtils();
				
				CITY_PATH_JSON += Constants.JS_API2_START + Constants.JS_SENDCODE;
				
				http.send(HttpMethod.POST, CITY_PATH_JSON, params,
						new RequestCallBack<String>() {

							@Override
							public void onSuccess(
									ResponseInfo<String> responseInfo) {
								// TODO Auto-generated method stub
								
								System.out.println(responseInfo.contentType);
								System.out.println(responseInfo.result);
								System.out.println(responseInfo.result.toString());
								try {
									String res = responseInfo.result.toString();
									res = res.substring(res.indexOf("{"));

									JSONObject result = new JSONObject(res);
									String status = (String) result.get("Status");
									if (status.equals("Success")) {
										time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
										time.start();// 开始计时
										pinEdt.setInputType(InputType.TYPE_CLASS_TEXT);
										Toast.makeText(getActivity(),"验证码发送成功", 1).show();
										
									
										
									} else {
										
										AlertDialog.Builder builder4 = new AlertDialog.Builder(getActivity())
										.setTitle("提示")
										.setMessage("您输入的号码已经被注册")
										.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
									           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
									           }					            
									       }); 
									
										builder4.create().show();	
										
										
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							@Override
							public void onFailure(HttpException error,
									String msg) {
								// TODO Auto-generated method stub
								
								Toast.makeText(getActivity(),"发送验证码失败，请检查网络连接", 1).show();
							}
					
				});
				break;
			
			
			//点击更换按钮
			case R.id.set_fragment_mobile_change:
				mobileStr = mobiletext.getText().toString().trim();
				pinStr = pinEdt.getText().toString().trim();
				if(!ValidateUtils.isNetworkConnected(getActivity())){
					Toast.makeText(getActivity(), "请检查网络连接", 0).show();
					break;
				}else if (TextUtils.isEmpty(mobileStr)) {
					
					AlertDialog.Builder builder4 = new AlertDialog.Builder(getActivity())
					.setTitle("您未输入您的手机号码")
					.setMessage("请先输入您的手机号码")
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
				           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
				           }					            
				       }); 
				
					builder4.create().show();	
					
					break;
				} else if (TextUtils.isEmpty(pinStr)) {
					
					AlertDialog.Builder builder5 = new AlertDialog.Builder(getActivity())
					.setTitle("您未输入您的验证码")
					.setMessage("请先输入您的验证码")
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
				           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
				           }					            
				       }); 
				
					builder5.create().show();	
					
					break;
				} else if (!ValidateUtils.isMobile(mobileStr)) {
					
					AlertDialog.Builder builder6 = new AlertDialog.Builder(getActivity())
					.setTitle("手机号码错误")
					.setMessage("您输入的是一个无效的手机号码")
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
				           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
				           }					            
				       }); 
				
					builder6.create().show();	

					break;
				} 
				
				RequestParams params2 = new RequestParams();
				params2.addBodyParameter("userid",userid);
				params2.addBodyParameter("ticket", ticket);
				params2.addBodyParameter("account", mobileStr);
				params2.addBodyParameter("code",pinStr);
				HttpUtils http2 = new HttpUtils();
				
				MOBILE_PATH_JSON += Constants.JS_API2_START + Constants.JS_BINDACCOUNT;
				
				http2.send(HttpMethod.POST, MOBILE_PATH_JSON, params2,
						new RequestCallBack<String>() {

							@Override
							public void onSuccess(
									ResponseInfo<String> responseInfo) {
								// TODO Auto-generated method stub
								
								System.out.println(responseInfo.contentType);
								System.out.println(responseInfo.result);
								System.out.println(responseInfo.result.toString());
								try {
									String res = responseInfo.result.toString();
									res = res.substring(res.indexOf("{"));

									JSONObject result = new JSONObject(res);
									String status = (String) result.get("Status");
									if (status.equals("Success")) {
								
										Toast.makeText(getActivity(),"更换手机号码成功，当前手机号为"+mobileStr, 1).show();
										oldmobileText.setText(mobileStr);
										
									
										
									} else{
										if( ((String) result.get("Message")).equals("Error_Empty_Account")){
											AlertDialog.Builder builder7 = new AlertDialog.Builder(getActivity())
											.setTitle("错误信息")
											.setMessage("您的新手机号为空")
											.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
										           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
										           }					            
										       }); 
											builder7.create().show();	
										}else if( ((String) result.get("Message")).equals("Error_Wrong_Ticket")){
											AlertDialog.Builder builder8 = new AlertDialog.Builder(getActivity())
											.setTitle("错误信息")
											.setMessage("您可能已经在其他地方登陆")
											.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
										           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
										           }					            
										       }); 
											builder8.create().show();	
										}else if( ((String) result.get("Message")).equals("Error_Code_Notmatch")){
											AlertDialog.Builder builder9 = new AlertDialog.Builder(getActivity())
											.setTitle("错误信息")
											.setMessage("您的验证码不匹配")
											.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
										           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
										           }					            
										       }); 
											builder9.create().show();	
										}else if( ((String) result.get("Message")).equals("Error_Code_Timeout")){
											AlertDialog.Builder builder10 = new AlertDialog.Builder(getActivity())
											.setTitle("错误信息")
											.setMessage("您的验证码已过期")
											.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
										           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
										           }					            
										       }); 
											builder10.create().show();	
										}else if( ((String) result.get("Message")).equals("Error_Code_Notexist")){
											AlertDialog.Builder builder11 = new AlertDialog.Builder(getActivity())
											.setTitle("错误信息")
											.setMessage("您的验证码不存在")
											.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
										           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
										           }					            
										       }); 
											builder11.create().show();	
										}else if( ((String) result.get("Message")).equals("Error_Query_Failed")){
											AlertDialog.Builder builder12 = new AlertDialog.Builder(getActivity())
											.setTitle("错误信息")
											.setMessage("数据库查询错误，请稍后再试")
											.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
										           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
										           }					            
										       }); 
											builder12.create().show();	
										}
										
									} 
								}catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							@Override
							public void onFailure(HttpException error,
									String msg) {
								// TODO Auto-generated method stub
								
								
								Toast.makeText(getActivity(),"更换手机号码失败", 1).show();
							}
					
				});
				break;
				
			default:
				break;
		}
	}
	
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			btnGetpin.setText("重新获取");
			btnGetpin.getBackground().setAlpha(255);
			btnGetpin.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示

			btnGetpin.setText("" + millisUntilFinished / 1000 + "秒");
			btnGetpin.getBackground().setAlpha(90);//
			btnGetpin.setClickable(false);
		}

	}
}
