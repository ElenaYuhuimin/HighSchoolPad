package com.emodou.set;

import android.app.Fragment;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.iflytek.cloud.InitListener;

public class WeChatFragment extends Fragment implements OnClickListener{
	
	private View wechatView;
	private Button concernbtn;
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
		wechatView = inflater.inflate(R.layout.set_fragment_wechat, container, false);
        
		init();
        
        return wechatView;
    } 
	
	public void init(){
		
		concernbtn = (Button)wechatView.findViewById(R.id.set_fragment_wechat_concern);
		concernbtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.set_fragment_wechat_concern:
				
				if(ValidateUtils.isAvilible(getActivity(), "com.tencent.mm")){
					Intent intent3 = new Intent();

					ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
					intent3.setAction(Intent.ACTION_MAIN);
					intent3.addCategory(Intent.CATEGORY_LAUNCHER);
					intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent3.setComponent(cmp);
					
					ClipboardManager cmb = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
					cmb.setText("易魔豆");
					Toast.makeText(getActivity(), "公众号已经复制到剪贴板", 1).show();
					startActivityForResult(intent3, 0);
				}else {
					Toast.makeText(getActivity(), "请先安装微信", 0).show();
				}
				break;
	
			default:
				break;
		}
	}
}
