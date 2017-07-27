package com.example.emodou;
import com.emodou.person.CountFragment;
import com.emodou.person.MyclassAppFragment;
import com.emodou.person.MyclassDetailFragment;
import com.emodou.person.MyclassFragment;
import com.emodou.person.PersonInfoFragment;
import com.emodou.set.PushFragment;
import com.emodou.util.ValidateUtils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PersonActivity extends Activity implements OnClickListener{
	
	private RelativeLayout personInfoRl, myclassRl, countRl;
	private TextView editTv, completeTv, refreshTv;
	private PersonInfoFragment personInfoFrag;
	private CountFragment countFrag;	
	private MyclassFragment myclassFrag;
	private MyclassAppFragment myclassAppFrag;
	private MyclassDetailFragment myclassDetailFrag;
	
	
	private String userid, ticket;
	private static String EDITHAVE = "1";
	private static String CHOOSE_RL; // 记录当前选中状态，1代表个人信息，2代表我的班级，3代表个性化统计
	private String myclassFragTag = "myclassFragTag";
	private FragmentManager fm = getFragmentManager();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_major);
		
		init();
		
		// 设置默认的Fragment
        setDefaultFragment();
	}
	
	public void init() {
		// TODO Auto-generated method stub
		personInfoRl = (RelativeLayout)findViewById(R.id.person_list_personInfo_rl);
		myclassRl = (RelativeLayout)findViewById(R.id.person_list_myclass_rl);
		countRl = (RelativeLayout)findViewById(R.id.person_list_count_rl);
		editTv = (TextView)findViewById(R.id.person_major_baredit);
		completeTv = (TextView)findViewById(R.id.person_major_barcompelete);
		refreshTv = (TextView)findViewById(R.id.person_major_barrefresh);
		personInfoRl.setOnClickListener(this);
		myclassRl.setOnClickListener(this);
		countRl.setOnClickListener(this);
		editTv.setOnClickListener(this);
		completeTv.setOnClickListener(this);
		refreshTv.setOnClickListener(this);
		
		userid = ValidateUtils.getUserid(this);
		ticket = ValidateUtils.getTicket(this);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(CHOOSE_RL.equals("1")){
			if(EDITHAVE.equals("1")){
				editTv.setVisibility(View.VISIBLE);
				completeTv.setVisibility(View.GONE);
			}else{
				editTv.setVisibility(View.GONE);
				completeTv.setVisibility(View.VISIBLE);
			}
			refreshTv.setVisibility(View.GONE);
		}else if(CHOOSE_RL.equals("2")){
			refreshTv.setVisibility(View.VISIBLE);
		}else if(CHOOSE_RL.equals("3")){
			editTv.setVisibility(View.GONE);
			completeTv.setVisibility(View.GONE);
			refreshTv.setVisibility(View.GONE);
		}else if(CHOOSE_RL.equals("2_APP")){
			refreshTv.setVisibility(View.GONE);
		}
	}
	
	private void setDefaultFragment()
    {
        FragmentTransaction transaction = fm.beginTransaction();
        personInfoFrag = new PersonInfoFragment();
        personInfoFrag.passUserid(userid);
        transaction.replace(R.id.person_major_framelayout, personInfoFrag);
        personInfoRl.setBackgroundColor(getResources().getColor(R.color.set_list_pressed));
        transaction.commit();
        editTv.setVisibility(View.VISIBLE);
        completeTv.setVisibility(View.GONE);
        refreshTv.setVisibility(View.GONE);
        
        CHOOSE_RL = "1";
    }
	
	public void setBcgColor(){
		personInfoRl.setBackgroundColor(getResources().getColor(R.color.set_list_unpressed));
		myclassRl.setBackgroundColor(getResources().getColor(R.color.set_list_unpressed));
		countRl.setBackgroundColor(getResources().getColor(R.color.set_list_unpressed));
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub	
		//开启fragment事务
		FragmentTransaction transaction = fm.beginTransaction();
		
		switch (v.getId()) {
			case R.id.person_list_personInfo_rl:
				setBcgColor();
				personInfoRl.setBackgroundColor(getResources().getColor(R.color.set_list_pressed));
				if(personInfoFrag == null){
					try{
						personInfoFrag = new PersonInfoFragment();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				// 使用当前Fragment的布局替代fragmentlayout的控件 
				
				transaction.replace(R.id.person_major_framelayout, personInfoFrag);
				//上面的修改/完成按钮显示
				if(EDITHAVE.equals("1")){
					editTv.setVisibility(View.VISIBLE);
					completeTv.setVisibility(View.GONE);
				}else{
					editTv.setVisibility(View.GONE);
					completeTv.setVisibility(View.VISIBLE);
				}
				refreshTv.setVisibility(View.GONE);
				
				CHOOSE_RL = "1";
				break;
				
			case R.id.person_major_baredit:
				if(ValidateUtils.isNetworkConnected(this))
					personInfoFrag.hideTv();
				else 
					Toast.makeText(this, R.string.prompt_network, 0).show();
				editTv.setVisibility(View.GONE);
				completeTv.setVisibility(View.VISIBLE);
				EDITHAVE = "0";
				break;
				
			case R.id.person_major_barcompelete:
				if(ValidateUtils.isNetworkConnected(this))
					personInfoFrag.showTv();
				else 
					Toast.makeText(this, R.string.prompt_network, 0).show();
				editTv.setVisibility(View.VISIBLE);
				completeTv.setVisibility(View.GONE);
				EDITHAVE = "1";
				break;
				
			case R.id.person_list_count_rl:
				setBcgColor();
				countRl.setBackgroundColor(getResources().getColor(R.color.set_list_pressed));
				if(countFrag == null){
					try{
						countFrag = new CountFragment();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				// 使用当前Fragment的布局替代fragmentlayout的控件 
				transaction.replace(R.id.person_major_framelayout, countFrag);
				editTv.setVisibility(View.GONE);
				completeTv.setVisibility(View.GONE);
				refreshTv.setVisibility(View.GONE);
				
				CHOOSE_RL = "3";
				break;
				
			case R.id.person_list_myclass_rl:
				setBcgColor();
				myclassRl.setBackgroundColor(getResources().getColor(R.color.set_list_pressed));
				if(myclassFrag == null){
					try{
						myclassFrag = new MyclassFragment();
						myclassFrag.passUserid(userid);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				// 使用当前Fragment的布局替代fragmentlayout的控件 
				transaction.replace(R.id.person_major_framelayout, myclassFrag);
				editTv.setVisibility(View.GONE);
				completeTv.setVisibility(View.GONE);
				refreshTv.setVisibility(View.VISIBLE);
				
				CHOOSE_RL = "2";
				break;
			
			case R.id.person_major_barrefresh:
				if(ValidateUtils.isNetworkConnected(PersonActivity.this))
					myclassFrag.refreshClassList();
				else 
					Toast.makeText(PersonActivity.this, R.string.prompt_network, 0).show();
				
				break;
			default:
				break;
				
		}
		//事务提交
		transaction.commit();
	}
	
	/*在fragment的管理类中，我们要实现这部操作，而他的主要作用是，当D这个activity回传数据到
	这里碎片管理器下面的fragnment中时，往往会经过这个管理器中的onActivityResult的方法。*/
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*在这里，我们通过碎片管理器中的Tag，就是每个碎片的名称，来获取对应的fragment*/
        
        /*然后在碎片中调用重写的onActivityResult方法*/
        myclassFrag.onActivityResult(requestCode, resultCode, data);
	}
	
	public void nextStep(String codeStr) {
		if(ValidateUtils.isNetworkConnected(this)){
			FragmentTransaction transaction = fm.beginTransaction();
			if(myclassAppFrag == null){
				myclassAppFrag = new MyclassAppFragment();
			}
			myclassAppFrag.passCodeStr(codeStr, userid);
			transaction.replace(R.id.person_major_framelayout, myclassAppFrag);
			transaction.commit();
			
			editTv.setVisibility(View.GONE);
			completeTv.setVisibility(View.GONE);
			refreshTv.setVisibility(View.GONE);
			CHOOSE_RL = "2_APP";
		}else 
			Toast.makeText(this, R.string.prompt_network, 0).show();
		
	}
	
	public void appComplete() {
		FragmentTransaction transaction = fm.beginTransaction();
		if(myclassFrag == null){
			try{
				myclassFrag = new MyclassFragment();
				myclassFrag.passUserid(userid);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		// 使用当前Fragment的布局替代fragmentlayout的控件 
		transaction.replace(R.id.person_major_framelayout, myclassFrag);
		transaction.commit();
		editTv.setVisibility(View.GONE);
		completeTv.setVisibility(View.GONE);
		refreshTv.setVisibility(View.VISIBLE);
		
		CHOOSE_RL = "2";
	}
	
	public void changeToDetailFrag(String codeStr) {
		FragmentTransaction transaction = fm.beginTransaction();
		if(myclassDetailFrag == null){
			myclassDetailFrag = new MyclassDetailFragment();
		}
		myclassDetailFrag.passData(userid, codeStr, ticket);
		transaction.replace(R.id.person_major_framelayout, myclassDetailFrag);
		transaction.commit();
		
		editTv.setVisibility(View.GONE);
		completeTv.setVisibility(View.GONE);
		refreshTv.setVisibility(View.GONE);
		CHOOSE_RL = "2_APP";
	}
	
	public void returnMajor(){
		//开启fragment事务
		FragmentTransaction transaction = fm.beginTransaction();
		if(myclassFrag == null){
			try{
				myclassFrag = new MyclassFragment();
				myclassFrag.passUserid(userid);
				//有可能退出了班级，因此这里返回时要刷新列表
				//myclassFrag.refreshClassList();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		// 使用当前Fragment的布局替代fragmentlayout的控件 
		transaction.replace(R.id.person_major_framelayout, myclassFrag);
		transaction.commit();
		editTv.setVisibility(View.GONE);
		completeTv.setVisibility(View.GONE);
		refreshTv.setVisibility(View.VISIBLE);
		
		CHOOSE_RL = "2";
	}

}
