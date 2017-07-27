package com.emodou.person;

import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouUserInfo;
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

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class PersonInfoFragment extends Fragment{
	
	private View personInfoView;
	
	private TextView usernameTv, nikinameTv, sexTv, QQtv, phoneTv, emailTv,  locationTv;
	private TextView schoolTv, gradeTv, parentnameTv, parentphoneTv, parentemailTv;
	private EditText usernameEdt, nikinameEdt, QQEdt, phoneEdt, emailEdt;
	private EditText schoolEdt, gradeEdt, parentnameEdt, parentphoneEdt, parentemailEdt;
	private String usernameStr, nikinameStr, sexStr, QQStr, phoneStr, emailStr, locationStr;
	private String schoolStr, gradeStr, parentnameStr, parentphoneStr, parentemailStr;
	private String classcodeStr;
	private RadioGroup raGroup;
	private RadioButton raButtonMan, raButtonWoman;
	private String userid;
	private EmodouUserInfo userInfo = new EmodouUserInfo();
	private DbUtils dbUtils;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		personInfoView = inflater.inflate(R.layout.person_personinfo, container, false);
		
		init();
		return personInfoView;
	}
	
	public void passUserid(String userid){
		this.userid = userid;
	}

	public void init() {
		// TODO Auto-generated method stub
		dbUtils = DbUtils.create(getActivity());
		try {
			userInfo = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
			if(userInfo != null){
				usernameStr = userInfo.getUsername();
				nikinameStr = userInfo.getNikiname();
				sexStr = userInfo.getSex();
				QQStr = userInfo.getQqnum();
				phoneStr = userInfo.getPhone();
				emailStr = userInfo.getEmail();
				locationStr = userInfo.getLocation();
				schoolStr = userInfo.getSchool();
				gradeStr = userInfo.getGrade();
				parentnameStr = userInfo.getParentname();
				parentphoneStr = userInfo.getParentphone();
				classcodeStr = userInfo.getClasscode();
				parentemailStr = userInfo.getParentemail();
			}
		} catch (DbException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		usernameTv = (TextView)personInfoView.findViewById(R.id.person_personinfo_username_tv);
		usernameEdt = (EditText)personInfoView.findViewById(R.id.person_personinfo_username_edit);
		nikinameTv = (TextView)personInfoView.findViewById(R.id.person_personinfo_nikiname_tv);
		nikinameEdt = (EditText)personInfoView.findViewById(R.id.person_personinfo_nikiname_edit);
		QQEdt = (EditText)personInfoView.findViewById(R.id.person_personinfo_QQ_edit);
		QQtv = (TextView)personInfoView.findViewById(R.id.person_personinfo_QQ_tv);
		QQEdt = (EditText)personInfoView.findViewById(R.id.person_personinfo_QQ_edit);
		phoneTv = (TextView)personInfoView.findViewById(R.id.person_personinfo_phone_tv);
		phoneEdt = (EditText)personInfoView.findViewById(R.id.person_personinfo_phone_edit);
		emailTv = (TextView)personInfoView.findViewById(R.id.person_personinfo_email_tv);
		emailEdt = (EditText)personInfoView.findViewById(R.id.person_personinfo_email_edit);
		locationTv = (TextView)personInfoView.findViewById(R.id.person_personinfo_location_tv);
		schoolTv = (TextView)personInfoView.findViewById(R.id.person_personinfo_school_tv);
		schoolEdt = (EditText)personInfoView.findViewById(R.id.person_personinfo_school_edit);
		gradeTv = (TextView)personInfoView.findViewById(R.id.person_personinfo_grade_tv);
		gradeEdt = (EditText)personInfoView.findViewById(R.id.person_personinfo_grade_edit);
		parentnameTv = (TextView)personInfoView.findViewById(R.id.person_personinfo_parentname_tv);
		parentnameEdt = (EditText)personInfoView.findViewById(R.id.person_personinfo_parentname_edit);
		parentphoneTv = (TextView)personInfoView.findViewById(R.id.person_personinfo_parentphone_tv);
		parentphoneEdt = (EditText)personInfoView.findViewById(R.id.person_personinfo_parentphone_edit);
		parentemailTv = (TextView)personInfoView.findViewById(R.id.person_personinfo_parentemail_tv);
		parentemailEdt = (EditText)personInfoView.findViewById(R.id.person_personinfo_parentemail_edit);
		raGroup = (RadioGroup)personInfoView.findViewById(R.id.person_personinfo_sex_ragroup);
		raButtonMan = (RadioButton)personInfoView.findViewById(R.id.person_personinfo_sex_rabuttonMan);
		raButtonWoman = (RadioButton)personInfoView.findViewById(R.id.person_personinfo_sex_rabuttonWoman);
		sexTv = (TextView)personInfoView.findViewById(R.id.person_personinfo_sex_tv);
		
		
		if(!TextUtils.isEmpty(usernameStr)){
			usernameTv.setText(usernameStr);
			usernameEdt.setText(usernameStr);
		}
		if(!TextUtils.isEmpty(nikinameStr)){
			nikinameTv.setText(nikinameStr);
			nikinameEdt.setText(nikinameStr);
		}
		if(!TextUtils.isEmpty(sexStr)){
			if(sexStr.equals("0")){
				sexTv.setText("女");
				raButtonWoman.setChecked(true);
			}	 
			else if(sexStr.equals("1")){
				sexTv.setText("男");
				raButtonMan.setChecked(true);
			}	
			else if(sexStr.equals("2")){
				sexTv.setText("");
				raGroup.clearCheck();
			}		
		}
		if(!TextUtils.isEmpty(QQStr)){
			QQtv.setText(QQStr);
			QQEdt.setText(QQStr);
		}
		if(!TextUtils.isEmpty(phoneStr)){
			phoneTv.setText(phoneStr);
			phoneEdt.setText(phoneStr);
		}
		if(!TextUtils.isEmpty(emailStr)){
			emailTv.setText(emailStr);
			emailEdt.setText(emailStr);
		}
		if(!TextUtils.isEmpty(locationStr)){
			locationTv.setText(locationStr);
		}	
		if(!TextUtils.isEmpty(schoolStr)){
			schoolTv.setText(schoolStr);
			schoolEdt.setText(schoolStr);
		}
		if(!TextUtils.isEmpty(gradeStr)){
			gradeTv.setText(gradeStr);
			gradeEdt.setText(gradeStr);
		}
		if(!TextUtils.isEmpty(parentnameStr)){
			parentnameTv.setText(parentnameStr);
			parentnameEdt.setText(parentnameStr);
		}
		if(!TextUtils.isEmpty(parentphoneStr)){
			parentphoneTv.setText(parentphoneStr);
			parentphoneEdt.setText(parentphoneStr);
		}
		if(!TextUtils.isEmpty(parentemailStr)){
			parentemailTv.setText(parentemailStr);
			parentemailEdt.setText(parentemailStr);
		}
	}
	
	
	
	public void setTextView() {
		if(!TextUtils.isEmpty(usernameStr)){
			usernameTv.setText(usernameStr);
		}
		if(!TextUtils.isEmpty(nikinameStr)){
			nikinameTv.setText(nikinameStr);
		}
		if(!TextUtils.isEmpty(sexStr)){
			if(sexStr.equals("0"))
				sexTv.setText("女"); 
			else if(sexStr.equals("1")) 
				sexTv.setText("男");
			else if(sexStr.equals("2")) 
				sexTv.setText("");
		}
		if(!TextUtils.isEmpty(QQStr)){
			QQtv.setText(nikinameStr);
		}
		if(!TextUtils.isEmpty(phoneStr)){
			phoneTv.setText(phoneStr);
		}
		if(!TextUtils.isEmpty(emailStr)){
			emailTv.setText(emailStr);
		}
		if(!TextUtils.isEmpty(locationStr)){
			locationTv.setText(locationStr);
		}	
		if(!TextUtils.isEmpty(schoolStr)){
			schoolTv.setText(schoolStr);
		}
		if(!TextUtils.isEmpty(gradeStr)){
			gradeTv.setText(gradeStr);
		}
		if(!TextUtils.isEmpty(parentnameStr)){
			parentnameTv.setText(parentnameStr);
		}
		if(!TextUtils.isEmpty(parentphoneStr)){
			parentphoneTv.setText(parentphoneStr);
		}
		if(!TextUtils.isEmpty(parentemailStr)){
			parentemailTv.setText(parentemailStr);
		}
	}
	
	public void setEditText() {
		if(!TextUtils.isEmpty(usernameStr)){
			usernameEdt.setText(usernameStr);
		}
		if(!TextUtils.isEmpty(nikinameStr)){
			nikinameEdt.setText(nikinameStr);
		}
		if(!TextUtils.isEmpty(sexStr)){
			if(sexStr.equals("0")){
				raButtonWoman.setChecked(true);
			}	 
			else if(sexStr.equals("1")){
				raButtonMan.setChecked(true);
			}	
			else if(sexStr.equals("2")){
				raGroup.clearCheck();
			}	
		}
		if(!TextUtils.isEmpty(QQStr)){
			QQEdt.setText(QQStr);
		}
		if(!TextUtils.isEmpty(phoneStr)){
			phoneEdt.setText(phoneStr);
		}
		if(!TextUtils.isEmpty(emailStr)){
			emailEdt.setText(emailStr);
		}	
		if(!TextUtils.isEmpty(schoolStr)){
			schoolEdt.setText(schoolStr);
		}
		if(!TextUtils.isEmpty(gradeStr)){
			gradeEdt.setText(gradeStr);
		}
		if(!TextUtils.isEmpty(parentnameStr)){
			parentnameEdt.setText(parentnameStr);
		}
		if(!TextUtils.isEmpty(parentphoneStr)){
			parentphoneEdt.setText(parentphoneStr);
		}
		if(!TextUtils.isEmpty(parentemailStr)){
			parentemailEdt.setText(parentemailStr);
		}
	}
	public void hideTv() {
		//开始修改
		usernameTv.setVisibility(View.GONE);
		usernameEdt.setVisibility(View.VISIBLE);
		nikinameTv.setVisibility(View.GONE);
		nikinameEdt.setVisibility(View.VISIBLE);
		sexTv.setVisibility(View.GONE);
		raGroup.setVisibility(View.VISIBLE);
		QQtv.setVisibility(View.GONE);
		QQEdt.setVisibility(View.VISIBLE);
		phoneTv.setVisibility(View.GONE);
		phoneEdt.setVisibility(View.VISIBLE);
		emailTv.setVisibility(View.GONE);
		emailEdt.setVisibility(View.VISIBLE);
		schoolTv.setVisibility(View.GONE);
		schoolEdt.setVisibility(View.VISIBLE);
		gradeTv.setVisibility(View.GONE);
		gradeEdt.setVisibility(View.VISIBLE);
		parentnameTv.setVisibility(View.GONE);
		parentnameEdt.setVisibility(View.VISIBLE);
		parentphoneTv.setVisibility(View.GONE);
		parentphoneEdt.setVisibility(View.VISIBLE);
		parentemailTv.setVisibility(View.GONE);
		parentemailEdt.setVisibility(View.VISIBLE);
		
		//有可能经过前面的n次修改了，所以要重新赋值
		setEditText();
		
		usernameEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
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
				usernameStr = s.toString();
			}
		});
		nikinameEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
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
				nikinameStr = s.toString();
			}
		});
		
		raGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int arg1) {
				// TODO Auto-generated method stub
				//获取变更后的选中项的ID
				int radioButtonId = group.getCheckedRadioButtonId();
				//根据ID获取RadioButton的实例
				RadioButton rb = (RadioButton)personInfoView.findViewById(radioButtonId);
				//更新文本内容，以符合选中项
				if(rb.getText().equals("男")) 
					sexStr = "1";
				else if(rb.getText().equals("女")) 
					sexStr = "0";
				else if(rb.getText().equals("女"))
					sexStr = "2";
			}
		});
		QQEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
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
				QQStr = s.toString();
			}
		});
		phoneEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
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
				phoneStr = s.toString();
			}
		});
		emailEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
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
				emailStr = s.toString();
			}
		});
		schoolEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
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
				schoolStr = s.toString();
			}
		});
		gradeEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
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
				gradeStr = s.toString();
			}
		});
		parentnameEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
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
				parentnameStr = s.toString();
			}
		});
		parentphoneEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
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
				parentphoneStr = s.toString();
			}
		});
		parentemailEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
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
				parentemailStr = s.toString();
			}
		});
		
	}
	
	public void showTv() {
		//修改完成
		
		
		//将修改后的内容上传,不开网络不让用户修改	
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid", userid);
		params.addBodyParameter("username",usernameStr);
		params.addBodyParameter("nickname",nikinameStr);
		params.addBodyParameter("sex",sexStr);
		params.addBodyParameter("location",locationStr);
		params.addBodyParameter("qq",QQStr);
		params.addBodyParameter("email",emailStr);
		params.addBodyParameter("role","s");
		params.addBodyParameter("grade",gradeStr);
		if(!TextUtils.isEmpty(classcodeStr))
			params.addBodyParameter("classroomcode",classcodeStr);
		params.addBodyParameter("parentname",parentnameStr);
		params.addBodyParameter("parentphone",parentphoneStr);
		params.addBodyParameter("parentemail",parentemailStr);
		String path = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_SAVEINFO;
		
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, path, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				try{									
					String res = responseInfo.result.toString();
					int index = res.indexOf("{");
					if(index == -1){
						Toast.makeText(getActivity(), R.string.person_personinfo_updateFail,Toast.LENGTH_LONG).show();								
					}else{
						
						//从index位开始截取
						res = res.substring(index);
						
						//res已经是string类型
						JSONObject result = new JSONObject(res);
						String status = (String)result.get("Status");
						
						if(status.equals("Success")){
							Toast.makeText(getActivity(), R.string.person_personinfo_updateSuc,Toast.LENGTH_LONG).show();
						}
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
				//不论是否成功，都要更新本地数据库
				userInfo.setUsername(usernameStr);
				userInfo.setNikiname(nikinameStr);
				userInfo.setSex(sexStr);
				userInfo.setLocation(locationStr);
				userInfo.setQqnum(QQStr);
				userInfo.setPhone(phoneStr);
				userInfo.setEmail(emailStr);
				userInfo.setGrade(gradeStr);
				userInfo.setParentname(parentnameStr);
				userInfo.setParentphone(parentphoneStr);
				userInfo.setParentemail(parentemailStr);
				try {
					dbUtils.update(userInfo);
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), R.string.person_personinfo_updateFail,Toast.LENGTH_LONG).show();
				//不论是否成功，都要更新本地数据库
				userInfo.setUsername(usernameStr);
				userInfo.setNikiname(nikinameStr);
				userInfo.setSex(sexStr);
				userInfo.setLocation(locationStr);
				userInfo.setQqnum(QQStr);
				userInfo.setPhone(phoneStr);
				userInfo.setEmail(emailStr);
				userInfo.setGrade(gradeStr);
				userInfo.setParentname(parentnameStr);
				userInfo.setParentphone(parentphoneStr);
				userInfo.setParentemail(parentemailStr);
				try {
					dbUtils.update(userInfo);
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		});
	    
	    //完成修改
	    usernameTv.setVisibility(View.VISIBLE);
		usernameEdt.setVisibility(View.GONE);
		nikinameTv.setVisibility(View.VISIBLE);
		nikinameEdt.setVisibility(View.GONE);
		sexTv.setVisibility(View.VISIBLE);
		raGroup.setVisibility(View.GONE);
		QQtv.setVisibility(View.VISIBLE);
		QQEdt.setVisibility(View.GONE);
		phoneTv.setVisibility(View.VISIBLE);
		phoneEdt.setVisibility(View.GONE);
		emailTv.setVisibility(View.VISIBLE);
		emailEdt.setVisibility(View.GONE);
		schoolTv.setVisibility(View.VISIBLE);
		schoolEdt.setVisibility(View.GONE);
		gradeTv.setVisibility(View.VISIBLE);
		gradeEdt.setVisibility(View.GONE);
		parentnameTv.setVisibility(View.VISIBLE);
		parentnameEdt.setVisibility(View.GONE);
		parentphoneTv.setVisibility(View.VISIBLE);
		parentphoneEdt.setVisibility(View.GONE);
		parentemailTv.setVisibility(View.VISIBLE);
		parentemailEdt.setVisibility(View.GONE);
		
		setTextView();
	}
}
