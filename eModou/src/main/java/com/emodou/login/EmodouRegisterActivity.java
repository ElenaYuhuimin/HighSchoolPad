package com.emodou.login;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.emodou.domain.EmodouUserInfo;
import com.emodou.util.Constants;
import com.emodou.util.LogUtil;
import com.emodou.util.ValidateUtils;
import com.example.emodou.MainActivity;
import com.example.emodou.R;
import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class EmodouRegisterActivity extends Activity implements OnClickListener{
	
	//界面上的控件，激活码是code，验证码是captcha
	private ImageButton btn_code, btn_wt_code;
	private EditText edt_code;
	private TextView edt_phone, edt_captcha, edt_psd, edt_cfm_psd;
	private Button btn_getcaptcha, btn_register, btn_protocol;
	
	//界面上的控件对应到逻辑代码
	private String str_phone, str_captcha, str_psd, str_cfm_psd, str_code;
	
	//actionbar及其相关
	private ActionBar actionbar;
	private TextView titletext;
	private ImageButton imbReturn;//actionbar的返回按钮
	
	//其余属性
	private String CITY_PATH_JSON = Constants.EMODOU_URL;
	private TimeCount time;
		
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.register);
	        
	        init();
	    }
	 
	 public void init(){
		 
		 actionbar = getActionBar();
	 	 actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	 	 actionbar.setCustomView(R.layout.actionbar_login);
	 	 View view = actionbar.getCustomView();
	 	 imbReturn = (ImageButton)view.findViewById(R.id.login_imbtn_return);
	 	 imbReturn.setVisibility(View.GONE);
	 	 titletext = (TextView)findViewById(R.id.login_ancionbar_text);
	 	 titletext.setText("注        册");
		 
		 edt_phone = (TextView)findViewById(R.id.register_phone);
		 edt_captcha = (TextView)findViewById(R.id.register_captcha);
		 edt_psd = (TextView)findViewById(R.id.register_password);
		 edt_cfm_psd = (TextView)findViewById(R.id.register_confirm_password);
		 edt_code = (EditText)findViewById(R.id.register_edittext_code);
		 
		//隐藏密码，变成点
		 edt_psd.setInputType((InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
		 edt_cfm_psd.setInputType((InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD));
		 
		 btn_code = (ImageButton)findViewById(R.id.register_button_code);
		 btn_code.setOnClickListener(this);
		 
		 btn_getcaptcha = (Button)findViewById(R.id.register_button_getcapcha);
		 btn_getcaptcha.setOnClickListener(this);
		 
		 btn_register = (Button)findViewById(R.id.register_button_register);
		 btn_register.setOnClickListener(this);
		 
		 btn_protocol = (Button)findViewById(R.id.register_button_protocol);
		 btn_protocol.setOnClickListener(this);
		 
		 btn_wt_code = (ImageButton)findViewById(R.id.register_button_what_code);
		 btn_wt_code.setOnClickListener(this);
		 
		 //本地做一些初步的输入正确与否判断
		 edt_phone.addTextChangedListener(new TextWatcher() {
				
				public void onTextChanged(CharSequence s, int start, int before, int count) {
						
				}
				
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
				
					
				}
				
				public void afterTextChanged(Editable s) {
					str_phone = edt_phone.getText().toString().trim();	
						if (str_phone != null && !ValidateUtils.isMobile(str_phone) && str_phone.length() >= 11){
							edt_phone.setError("无效号码");
						}
					
				}
			});
		 
		 edt_phone.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					str_phone = edt_phone.getText().toString().trim();	

						if(str_phone != null && !ValidateUtils.isMobile(str_phone) && hasFocus == false){
							edt_phone.setError("无效号码");
						}
					
					if(hasFocus == false){
						hideinput();
					}
					
				}
			});
		 
		 
		 edt_captcha.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					str_captcha = edt_captcha.getText().toString().trim();	
					if(str_captcha.length() != 0 &&str_captcha.length() != 6 && hasFocus == false){
						edt_captcha.setError("验证码格式错误");
					}
					
				}
			});
		 
		 edt_psd.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					str_psd = edt_psd.getText().toString().trim();	
					if(str_psd.length() < 6 && hasFocus == false && str_psd.length()!=0){
						edt_psd.setError("密码至少为6位字母或数字组成");
					}
					if(hasFocus == false){
						hideinput();
					}
					
				}
			});
		 
		 
		 edt_cfm_psd.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {

					str_psd = edt_psd.getText().toString().trim();	
					str_cfm_psd = edt_cfm_psd.getText().toString().trim();
					if(str_psd.length() == str_cfm_psd.length() && !str_cfm_psd.equals(str_psd)){
						edt_cfm_psd.setError("密码错误");
					}
					
				
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
	 }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
			
			case R.id.register_button_code:
				btn_code.setVisibility(View.GONE);
				edt_code.setVisibility(View.VISIBLE);
				break;
			
			case R.id.register_button_what_code:
				
				AlertDialog.Builder builder = new AlertDialog.Builder(EmodouRegisterActivity.this);
				builder.setTitle("激活码")
				.setMessage("激活码是激活学习包的钥匙。通过两种方式获得：\n1.以学校为单位统一发放；\n2.用户自主购买学习包时需通过短信付费，成功后系统会返回激活码，用户可以在本页面或者发现页面输入激活码进行学习。"
 + "本版本属于内测版，受邀用户可以将已知的激活码输入框中，非受邀用户目前没有激活码，仅可以使用免费的体验包。")
				.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
			           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
			           }					            
			       }); 
				builder.create().show();	
				
				break;
			
			case R.id.register_button_getcapcha:
				
				str_phone = edt_phone.getText().toString().trim();
				
				if(!ValidateUtils.isNetworkConnected(EmodouRegisterActivity.this)){
					Toast.makeText(EmodouRegisterActivity.this, "请检查网络连接", 0).show();
					break;
				}
				else if (TextUtils.isEmpty(str_phone)) {
					
					AlertDialog.Builder builder2 = new AlertDialog.Builder(EmodouRegisterActivity.this)
					.setTitle("手机号码错误")
					.setMessage("请先输入您的手机号码")
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
				           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
				           }					            
				       }); 
				
					builder2.create().show();	
					
					break;
				} else if (!ValidateUtils.isMobile(str_phone)) {
					
					AlertDialog.Builder builder3 = new AlertDialog.Builder(EmodouRegisterActivity.this)
					.setTitle("手机号码错误")
					.setMessage("您输入的是一个无效的手机号码")
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
				           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
				           }					            
				       }); 
				
					builder3.create().show();	

					break;
				} 
				
				
				RequestParams params = new RequestParams();
//				JSONObject json = new JSONObject();
//				JSONStringer jsonStringer = new JSONStringer();
//				try {
////					json.put("phone", str_phone);
////					json.put("type", "res");	
////					params.setBodyEntity(new StringEntity(String.valueOf(json)));
////					System.out.println(String.valueOf(json));
//					List<BasicNameValuePair> sendData = new ArrayList<BasicNameValuePair>();
//					sendData.add(new BasicNameValuePair("phone", str_phone));
//					sendData.add(new BasicNameValuePair("type", "res"));
//					
////					Gson gson = new Gson();
////					String string = gson.;]
//					jsonStringer.object();
//					jsonStringer.key("phone");
//					jsonStringer.value(str_phone);
//					jsonStringer.key("type");
//					jsonStringer.value("res");
//					jsonStringer.endObject();
//					LogUtil.d("json", jsonStringer.toString());
//					params.setBodyEntity(new StringEntity(String.valueOf(jsonStringer)));
// 					
////					((HttpEntityEnclosingRequest) params).setEntity(new UrlEncodedFormEntity(sendData, "utf-8"));
////					System.out.println(EntityUtils.toString(((HttpEntityEnclosingRequest) sendData).getEntity()));
//				}  catch (IllegalStateException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} 
				
				params.addBodyParameter("type", "reg");
				params.addBodyParameter("phone", str_phone);
				
				HttpUtils http = new HttpUtils();
				//LogUtil.d("params",params);
				
				CITY_PATH_JSON = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_SENDCODE;
				//CITY_PATH_JSON += Constants.JS_API2_START + Constants.JS_SENDCODE;
				System.out.println(CITY_PATH_JSON);
				
				http.send(HttpMethod.POST, CITY_PATH_JSON, params,new RequestCallBack<String>() {

							@Override
							public void onSuccess(
									ResponseInfo<String> responseInfo) {
								// TODO Auto-generated method stub
								
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
										edt_captcha.setInputType(InputType.TYPE_CLASS_TEXT);
										Toast.makeText(EmodouRegisterActivity.this,
												"验证码发送成功", 1).show();
										
										btn_getcaptcha.setClickable(false);
										//点击“点击获取”的时候，软键盘隐藏了
										((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
									    hideSoftInputFromWindow(EmodouRegisterActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
									
										
									} else {
										
										AlertDialog.Builder builder4 = new AlertDialog.Builder(EmodouRegisterActivity.this)
										.setTitle("提示")
										.setMessage("发送验证码失败")
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
								
								Toast.makeText(EmodouRegisterActivity.this,
										"发送验证码失败，请检查网络连接", 1).show();
							}
					
				});
				break;
			
			case R.id.register_button_register:
				
				str_phone = edt_phone.getText().toString().trim();
				str_captcha = edt_captcha.getText().toString().trim();
				str_psd = edt_psd.getText().toString().trim();
				str_cfm_psd = edt_cfm_psd.getText().toString().trim();
				str_code = edt_code.getText().toString().trim();
				
				if(!ValidateUtils.isNetworkConnected(EmodouRegisterActivity.this)){
					Toast.makeText(EmodouRegisterActivity.this, "请检查网络连接", 0).show();
					break;
				}else if(str_phone.equals("") || str_psd.equals("") || str_cfm_psd.equals("") || str_captcha.equals("")){
					
					AlertDialog.Builder builder5 = new AlertDialog.Builder(EmodouRegisterActivity.this)
					.setTitle("输入错误")
					.setMessage("请完整填写您的个人信息")
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
				           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
				           }					            
				       }); 
				
					builder5.create().show();	
					break;
					
				}else if(!ValidateUtils.isMobile(str_phone)){
					
					AlertDialog.Builder builder6 = new AlertDialog.Builder(EmodouRegisterActivity.this)
					.setTitle("输入错误")
					.setMessage("无效号码")
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
				           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
				           }					            
				       }); 
				
					builder6.create().show();	
					break;
					
				}else if(str_captcha.length() != 6){
					
					AlertDialog.Builder builder7 = new AlertDialog.Builder(EmodouRegisterActivity.this)
					.setTitle("输入错误")
					.setMessage("验证码格式错误")
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
				           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
				           }					            
				       }); 
				
					builder7.create().show();	
					break;
					
				}else if(str_psd.length() < 6){
					
					AlertDialog.Builder builder8 = new AlertDialog.Builder(EmodouRegisterActivity.this)
					.setTitle("输入错误")
					.setMessage("密码格式错误")
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
				           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
				           }					            
				       }); 
				
					builder8.create().show();	
					break;
					
				}else if (!str_cfm_psd.equals(str_psd)){
						
					AlertDialog.Builder builder9 = new AlertDialog.Builder(EmodouRegisterActivity.this)
					.setTitle("输入错误")
					.setMessage("确认密码格式错误")
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
				           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
				           }					            
				       }); 
				
					builder9.create().show();	
					break;
				}
			
				RequestParams params2 = new RequestParams();
				//params2.addBodyParameter("type", "phone");
				params2.addBodyParameter("account", str_phone);
				params2.addBodyParameter("code", str_captcha);
				params2.addBodyParameter("password", str_psd);
				//params2.addBodyParameter("activation", str_code);第二版没有激活码了
				HttpUtils http2 = new HttpUtils();
				String CITY_PATH_JSON2 = Constants.EMODOU_URL;
				CITY_PATH_JSON2 += Constants.JS_API2_START + Constants.JS_SAVEREG;
				http2.send(HttpMethod.POST, CITY_PATH_JSON2, params2,
						new RequestCallBack<String>() {

							@Override
							public void onSuccess(ResponseInfo<String> responseInfo) {
								System.out.println(responseInfo.contentType);
								System.out.println(responseInfo.result);
								System.out.println(responseInfo.result.toString());
								try {
									String res = responseInfo.result.toString();
									res = res.substring(res.indexOf("{"));

									JSONObject result = new JSONObject(res);
									String status = (String) result.get("Status");
									if (status.equals("Success")) {
										String userid = result.getString("UserId");
										//将userid存入数据库中
										DbUtils dbUtils = DbUtils.create(EmodouRegisterActivity.this);
										try {
											EmodouUserInfo userInfo = new EmodouUserInfo();
											userInfo = dbUtils.findFirst(Selector.from(EmodouUserInfo.class)
													                             .where("userid","=",userid));
											if(userInfo != null){
												dbUtils.delete(userInfo);
											}
											
											EmodouUserInfo userInfoNew = new EmodouUserInfo();
											userInfoNew.setUserid(userid);
											userInfoNew.setPhone(str_phone);
											userInfoNew.setDate(System.currentTimeMillis());
											dbUtils.saveBindingId(userInfoNew);
										} catch (DbException e) {
											// TODO: handle exception
											e.printStackTrace();
										}
										Intent intent = new Intent(EmodouRegisterActivity.this, EmodouLoginAcvitity.class);
										intent.putExtra("phone", str_phone);
										startActivity(intent);
									} else {
										
										if( ((String) result.get("Message")).equals("Error_Account_Exist")){
											AlertDialog.Builder builder10 = new AlertDialog.Builder(EmodouRegisterActivity.this)
											.setTitle("错误信息")
											.setMessage("抱歉，您的账号已被注册")
											.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
										           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
										           }					            
										       }); 
										
											builder10.create().show();	
										}else if( ((String) result.get("Message")).equals("Error_Wrong_Accountformat")){
											AlertDialog.Builder builder11 = new AlertDialog.Builder(EmodouRegisterActivity.this)
											.setTitle("错误信息")
											.setMessage("抱歉，您的账号格式有误")
											.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
										           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
										           }					            
										       }); 
										
											builder11.create().show();	
										}else if( ((String) result.get("Message")).equals("Error_Code_Notmatch")){
											AlertDialog.Builder builder12 = new AlertDialog.Builder(EmodouRegisterActivity.this)
											.setTitle("错误信息")
											.setMessage("抱歉，您的验证码输入不正确")
											.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
										           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
										           }					            
										       }); 
										
											builder12.create().show();	
										}else if( ((String) result.get("Message")).equals("Error_Code_Timeout")){
											AlertDialog.Builder builder13 = new AlertDialog.Builder(EmodouRegisterActivity.this)
											.setTitle("错误信息")
											.setMessage("抱歉，您的验证码已过期")
											.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
										           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
										           }					            
										       }); 
										
											builder13.create().show();	
										}else if( ((String) result.get("Message")).equals("Error_Code_Notexist")){
											AlertDialog.Builder builder14 = new AlertDialog.Builder(EmodouRegisterActivity.this)
											.setTitle("错误信息")
											.setMessage("抱歉，您的验证码不存在")
											.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
										           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
										           }					            
										       }); 
										
											builder14.create().show();	
										}else if( ((String) result.get("Message")).equals("Error_Query_Failed")){
											AlertDialog.Builder builder15 = new AlertDialog.Builder(EmodouRegisterActivity.this)
											.setTitle("错误信息")
											.setMessage("抱歉，服务器数据库错误，请稍后再试")
											.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
										           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
										           }					            
										       }); 
										
											builder15.create().show();	
										}else{
											Toast.makeText(EmodouRegisterActivity.this, (String) result.get("Message"), 1).show();
											
											((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).

										    hideSoftInputFromWindow(EmodouRegisterActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
						
										}
										
										
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}

							@Override
							public void onFailure(HttpException error, String msg) {
								Toast.makeText(EmodouRegisterActivity.this,
										"发送验证码失败，请检查网络连接", 1).show();
							}
						});

				
				
				
				break;
				
				default:
				break;
		}
		
		
		
	}
	
	public void hideinput() {
		/**隐藏软键盘**/
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
	}
	
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			btn_getcaptcha.setText("重新获取");
			btn_getcaptcha.getBackground().setAlpha(255);
			btn_getcaptcha.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示

			btn_getcaptcha.setText("" + millisUntilFinished / 1000 + "秒");
			btn_getcaptcha.getBackground().setAlpha(90);//
			btn_getcaptcha.setClickable(false);
		}

	}
	
}
