package com.emodou.set;

import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouUserInfo;
import com.emodou.home.CourseListActivity;
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
import android.content.DialogInterface.OnClickListener;
import android.net.sip.SipAudioCall.Listener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class FeedbackFragment extends Fragment implements View.OnClickListener{
	
	private View FeedbackView;
	
	private Button commitbtn;
	private EditText edt;
	private TextView count1;
	private String userid;
	
	private ProgressDialog progressDialog;
	
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
		FeedbackView = inflater.inflate(R.layout.set_fragment_feedback, container, false);
        
		init();
        
        return FeedbackView;
    } 
	
	public void init(){
		
		commitbtn  = (Button)FeedbackView.findViewById(R.id.set_fragment_feedback_commit);
		commitbtn.setOnClickListener(this);
		edt = (EditText)FeedbackView.findViewById(R.id.set_fragment_feedback_edt);
		count1 = (TextView)FeedbackView.findViewById(R.id.set_fragment_feedback_count);
		edt.addTextChangedListener(new TextWatcher() {
			
			private CharSequence temp; 
		    private int selectionStart; 
		    private int selectionEnd;
		    
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				count1.setText((count+start) + "/200");
				temp = s;
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
				selectionStart = edt.getSelectionStart(); 
		        selectionEnd = edt.getSelectionEnd(); 
		        if (temp.length() > 200) { 
		            Toast.makeText(getActivity(), "只能输入200字符", 
		              Toast.LENGTH_SHORT).show(); 
		            s.delete(selectionStart - 1, selectionEnd); 
		            int tempSelection = selectionEnd; 
		            edt.setText(s); 
		            edt.setSelection(tempSelection); 
		        } 
			}
		});
		
		DbUtils dbUtils = DbUtils.create(getActivity());
        try {
			EmodouUserInfo user = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
			if(user!=null){
				userid = user.getUserid();
			}
			
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
			
			case R.id.set_fragment_feedback_commit:
				
				progressDialog = new ProgressDialog(getActivity());      
	            progressDialog.setIndeterminate(true);
	            progressDialog.setMessage("Loading...");
	            progressDialog.show();
	            
	            String comments = edt.getText().toString().trim();
	            if (TextUtils.isEmpty(comments)){
	        	  
	        	  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
	  			 .setTitle(R.string.prompt)
	  			 .setMessage(R.string.set_fragment_feedback_empty)
	  			 .setCancelable(false)
	  			 .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
	  		           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
	  		           }					            
	  		       }); 
				} else{
						String	CITY_PATH_JSON = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_FEEDBACK;
						String ticket = ValidateUtils.getTicket(getActivity());

						RequestParams params = new RequestParams();
						params.addBodyParameter("userid", userid);
						params.addBodyParameter("ticket",ticket);
						params.addBodyParameter("content", comments);

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
												Toast.makeText(getActivity(), "非常感谢！您的意见提交成功", 1).show();
												edt.setText("");
												edt.setHint("再次发送您的宝贵意见");
											}else{
												Toast.makeText(getActivity(), "提交失败", 1).show();
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										progressDialog.dismiss();
										
										AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
										.setTitle("提交成功")
						  			 	.setMessage("非常感谢您的建议，我们的客服将会在三个工作日内处理。")
						  			 	.setCancelable(false)
						  			 	.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
						  		           public void onClick(DialogInterface dialog, int whichButton) { 					        	   
						  		           }					            
						  			 	});
									
									}

									@Override
									public void onFailure(HttpException error,
											String msg) {
										progressDialog.dismiss();
										Toast.makeText(getActivity(), "提交失败", 1).show();

									}
								});
					}
					
					break;
				
				
		}
	}
}
