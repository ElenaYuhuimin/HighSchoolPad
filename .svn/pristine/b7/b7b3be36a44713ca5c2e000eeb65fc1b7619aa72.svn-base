package com.example.emodou;

import junit.framework.Test;

import com.emodou.domain.EmodouUserInfo;
import com.emodou.extend.SwitchButton;

import com.emodou.extend.SwitchButton.OnChangeListener;
import com.emodou.home.ListenActivity;
import com.emodou.login.EmodouLoginAcvitity;
import com.emodou.set.AboutusFragment;
import com.emodou.set.ChangePsdFragment;
import com.emodou.set.FeedbackFragment;
import com.emodou.set.FontFragment;
import com.emodou.set.MobileFragment;
import com.emodou.set.NormalqusFragment;
import com.emodou.set.NormalqusFragment;
import com.emodou.set.PushFragment;
import com.emodou.set.WeChatFragment;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetActivity extends Activity implements OnClickListener{

	private RelativeLayout listPushRl, listFontRl, listMobileRl, listChangePsdRl;
	private RelativeLayout listWeChatRl, listNormalqus, listFeedbackRl, listAboutusRl;
	private PushFragment pushFragment;
	private FontFragment fontFragment;
	private MobileFragment mobileFragment;
	private ChangePsdFragment changePsdFragment;
	private WeChatFragment weChatFragment;
	private NormalqusFragment normalqusFragment;
	private FeedbackFragment feedbackFragment;
	private AboutusFragment aboutusFragment;
	
	private ImageButton logoutbtn;
	
	private ListenActivity listen = new ListenActivity();
	
	private SwitchButton sb;
	
				
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.set_major);  
          
        sb = (SwitchButton) findViewById(R.id.set_list_wiperSwitch1);  
        
        sb.setOnChangeListener(new OnChangeListener() {  
            
            @Override  
            public void onChange(SwitchButton sb, boolean state) {  
                // TODO Auto-generated method stub  
                Toast.makeText(SetActivity.this, state ? "开":"关", Toast.LENGTH_SHORT).show();  
            }  
        });
        sb.setSelected(false);
        
        //初始化控件和声明事件
        listPushRl = (RelativeLayout)findViewById(R.id.set_list_push_rl);
        listPushRl.setOnClickListener(this);
        listFontRl = (RelativeLayout)findViewById(R.id.set_list_font_rl);
        listFontRl.setOnClickListener(this);
        listMobileRl = (RelativeLayout)findViewById(R.id.set_list_mobile_rl);
        listMobileRl.setOnClickListener(this);
        listChangePsdRl = (RelativeLayout)findViewById(R.id.set_list_changepsd_rl);
        listChangePsdRl.setOnClickListener(this);
        listWeChatRl = (RelativeLayout)findViewById(R.id.set_list_wechat_rl);
        listWeChatRl.setOnClickListener(this);
        listNormalqus = (RelativeLayout)findViewById(R.id.set_list_normalqus_rl);
        listNormalqus.setOnClickListener(this);
        listFeedbackRl = (RelativeLayout)findViewById(R.id.set_list_feedback_rl);
        listFeedbackRl.setOnClickListener(this);
        listAboutusRl = (RelativeLayout)findViewById(R.id.set_list_aboutus_rl);
        listAboutusRl.setOnClickListener(this);
        
        logoutbtn = (ImageButton)findViewById(R.id.set_logout);
        logoutbtn.setOnClickListener(this);
        
        
        // 设置默认的Fragment
        setDefaultFragment();
    }
	
	
	private void setDefaultFragment()
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        pushFragment = new PushFragment();
        transaction.replace(R.id.set_major_framelayout, pushFragment);
        listPushRl.setBackgroundColor(getResources().getColor(R.color.set_list_pressed));
        transaction.commit();
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		FragmentManager fm = getFragmentManager();
		//开启fragment事务
		FragmentTransaction transaction = fm.beginTransaction();
		
		switch (v.getId()) {
			case R.id.set_list_push_rl:
				
				//设置背景颜色
				setBcgColor();
				listPushRl.setBackgroundColor(getResources().getColor(R.color.set_list_pressed));
				
				
				if(pushFragment == null){
					try{
						pushFragment = new PushFragment();
					}catch(Exception e){
						Toast.makeText(this, "wrong_pushFragment", Toast.LENGTH_SHORT);
						e.printStackTrace();
					}
				}
				// 使用当前Fragment的布局替代fragmentlayout的控件 
				transaction.replace(R.id.set_major_framelayout, pushFragment);
				break;
			
			case R.id.set_list_font_rl:
				
				//设置背景颜色
				setBcgColor();
				listFontRl.setBackgroundColor(getResources().getColor(R.color.set_list_pressed));
				
				
				if(fontFragment == null){
					try{
						fontFragment = new FontFragment();
					}catch(Exception e){
						Toast.makeText(this, "wrong_fontFragment", Toast.LENGTH_SHORT);
						e.printStackTrace();
					}
				}
				// 使用当前Fragment的布局替代fragmentlayout的控件 
				transaction.replace(R.id.set_major_framelayout, fontFragment);
				break;
				
			case R.id.set_list_mobile_rl:
				
				//设置背景颜色
				setBcgColor();
				listMobileRl.setBackgroundColor(getResources().getColor(R.color.set_list_pressed));
				
				if(mobileFragment == null){
					try{
						mobileFragment = new MobileFragment();
					}catch(Exception e){
						Toast.makeText(this, "wrong_mobileFragment", Toast.LENGTH_SHORT);
						e.printStackTrace();
					}
				}
				// 使用当前Fragment的布局替代fragmentlayout的控件 
				transaction.replace(R.id.set_major_framelayout, mobileFragment);
				break;
				
			case R.id.set_list_changepsd_rl:
				
				//设置背景颜色
				setBcgColor();
				listChangePsdRl.setBackgroundColor(getResources().getColor(R.color.set_list_pressed));
				
				
				if(changePsdFragment == null){
					try{
						changePsdFragment = new ChangePsdFragment();
					}catch(Exception e){
						Toast.makeText(this, "wrong_changePsdFragment", Toast.LENGTH_SHORT);
					}
				}
				// 使用当前Fragment的布局替代fragmentlayout的控件 
				transaction.replace(R.id.set_major_framelayout, changePsdFragment);
				break;
				
			case R.id.set_list_wechat_rl:
				
				//设置背景颜色
				setBcgColor();
				listWeChatRl.setBackgroundColor(getResources().getColor(R.color.set_list_pressed));
				
				
				if(weChatFragment == null){
					try{
						weChatFragment = new WeChatFragment();
					}catch(Exception e){
						Toast.makeText(this, "wrong_weChatFragment", Toast.LENGTH_SHORT);
					}	
				}
				// 使用当前Fragment的布局替代fragmentlayout的控件 
				transaction.replace(R.id.set_major_framelayout, weChatFragment);
				break;
				
			case R.id.set_list_normalqus_rl:
				
				//设置背景颜色
				setBcgColor();
				listNormalqus.setBackgroundColor(getResources().getColor(R.color.set_list_pressed));
				
				
				if(normalqusFragment == null){
					try{
						normalqusFragment = new NormalqusFragment();
					}catch(Exception e){
						Toast.makeText(this, "wrong_normalqusFragment", Toast.LENGTH_SHORT);
					}	
				}
				// 使用当前Fragment的布局替代fragmentlayout的控件 
				transaction.replace(R.id.set_major_framelayout, normalqusFragment);
				break;
				
			case R.id.set_list_feedback_rl:
				
				//设置背景颜色
				setBcgColor();
				listFeedbackRl.setBackgroundColor(getResources().getColor(R.color.set_list_pressed));
				
				
				if(feedbackFragment == null){
					try{
						feedbackFragment = new FeedbackFragment();
					}catch(Exception e){
						Toast.makeText(this, "wrong_feedbackFragment", Toast.LENGTH_SHORT);
					}	
				}
				// 使用当前Fragment的布局替代fragmentlayout的控件 
				transaction.replace(R.id.set_major_framelayout, feedbackFragment);
				break;
				
			case R.id.set_list_aboutus_rl:
				
				//设置背景颜色
				setBcgColor();
				listAboutusRl.setBackgroundColor(getResources().getColor(R.color.set_list_pressed));
				
				
				if(aboutusFragment == null){
					try{
						aboutusFragment = new AboutusFragment();
					}catch(Exception e){
						Toast.makeText(this, "wrong_AboutusFragment", Toast.LENGTH_SHORT);
					}	
				}
				// 使用当前Fragment的布局替代fragmentlayout的控件 
				transaction.replace(R.id.set_major_framelayout, aboutusFragment);
				break;
			
			case R.id.set_logout:
				
				DbUtils dbUtils = DbUtils.create(this);
		        try {
					EmodouUserInfo user = dbUtils.findFirst(Selector.from(EmodouUserInfo.class));
					dbUtils.delete(user);
				
					
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Intent intent9 = new Intent(this, EmodouLoginAcvitity.class);
				intent9.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent9);
				break;	
				
			default:
				break;
		}
		//事务提交
		transaction.commit();
	}
	
	public void setBcgColor(){
		listWeChatRl.setBackgroundColor(getResources().getColor(R.color.set_list_unpressed));
		listChangePsdRl.setBackgroundColor(getResources().getColor(R.color.set_list_unpressed));
		listMobileRl.setBackgroundColor(getResources().getColor(R.color.set_list_unpressed));
		listFontRl.setBackgroundColor(getResources().getColor(R.color.set_list_unpressed));
		listPushRl.setBackgroundColor(getResources().getColor(R.color.set_list_unpressed));
		listNormalqus.setBackgroundColor(getResources().getColor(R.color.set_list_unpressed));
		listFeedbackRl.setBackgroundColor(getResources().getColor(R.color.set_list_unpressed));
		listAboutusRl.setBackgroundColor(getResources().getColor(R.color.set_list_unpressed));
	}
	
	//设置字体
	public Handler handler=new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
	      super.handleMessage(msg);
	      if(msg!=null){
	        switch (msg.what) {
		        case 1:
		          listen.setTextsize(1); 
		          Toast.makeText(SetActivity.this, "1", Toast.LENGTH_LONG);
		          break;
		          
		        case 2:
		          listen.setTextsize(2); 
		          break;
		          
		        case 3:
		          listen.setTextsize(3); 
		          break;
	
		        default:
		          break;
	        }
	      }
	    }
	    
	  };
	  public void test(){
		  Toast.makeText(SetActivity.this, "1", Toast.LENGTH_LONG);
		  System.out.printf("test","1");
	  }
	  
	  @Override
	  protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		//super.onSaveInstanceState(outState);
	 }


}
