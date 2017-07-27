package com.example.emodou;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.test.IsolatedContext;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TabHost;

public class MainActivity extends TabActivity implements TabListener, OnClickListener{
	

	//tab内容
	TabHost tabHost;
	private RadioButton main_tab_home, main_tab_dis, main_tab_learndata, main_tab_set;
	
	
	private static String HOME_TAB = "home";
	private static String DISCOVERY_TAB = "dis";
	private static String LEARNDATA_TAB = "learndata";
	private static String SET_TAB = "set";
	private String changebook;
	private String isFromWorkDetail;
	private String typefirst;
	
	private String isComeFromLoginActivity;//是否从登陆界面跳转过来
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        getActionBar().hide();
        
        //初始化最左面的四个控件tab
        main_tab_home = (RadioButton)findViewById(R.id.main_tab_home);
    	main_tab_dis = (RadioButton) findViewById(R.id.main_tab_dis);
    	main_tab_learndata = (RadioButton) findViewById(R.id.main_tab_learndata);
    	main_tab_set = (RadioButton) findViewById(R.id.main_tab_set);
    	
    	isComeFromLoginActivity = getIntent().getStringExtra("typefirst");//是否从登陆界面跳转过来
    	isFromWorkDetail = getIntent().getStringExtra("isFromWorkDetail");//是否从作业详细列表跳转过来的
 		
 		initTab();
 		
 		init();
    }


    private void init() {
		// TODO Auto-generated method stub
    	
    	changebook = getIntent().getStringExtra("changebook");
    	
    	//从登陆或者EmodoulogoActivity进来的时候要进行同步，同步课程信息和单词信息
    	typefirst = getIntent().getStringExtra("typefirst");
    	
    	main_tab_home.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				tabHost.setCurrentTabByTag(HOME_TAB);
				main_tab_home.setChecked(true);
			}

			
		});

		main_tab_dis.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				tabHost.setCurrentTabByTag(DISCOVERY_TAB);
				main_tab_dis.setChecked(true);

			}


		});
		
		main_tab_learndata.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				tabHost.setCurrentTabByTag(LEARNDATA_TAB);
				main_tab_learndata.setChecked(true);
			}


		});
		
		main_tab_set.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				tabHost.setCurrentTabByTag(SET_TAB);
				main_tab_set.setChecked(true);
			}


		});
		
		if(isFromWorkDetail != null && isFromWorkDetail.equals("true")){
			//从作业详情界面跳转过来
			tabHost.setCurrentTabByTag(DISCOVERY_TAB);
			main_tab_dis.setChecked(true);
		}else{
			//从登陆界面跳转过来
			tabHost.setCurrentTabByTag(HOME_TAB);
			main_tab_home.setChecked(true);
		}
		
	}


	private void initTab() {

		// TODO Auto-generated method stub
    	
    	tabHost = getTabHost();
    	
    	Intent homeIntent = new Intent(this, HomeActivity.class);
    	
    	if(changebook != null && changebook.equals("changebook")){
    		homeIntent.putExtra("changebook", "changebook");
    	}
    	
    	if(typefirst != null && typefirst.equals("login")){
    		homeIntent.putExtra("typeFirst", "login");
    	}
    	
//    	if(isComeFromLoginActivity!=null&&isComeFromLoginActivity.equals("login")){
//    		homeIntent.putExtra("type", "first");
//    	}
    	
    	tabHost.addTab(tabHost.newTabSpec(HOME_TAB).setIndicator(HOME_TAB)
				.setContent(homeIntent));
    	tabHost.addTab(tabHost.newTabSpec(DISCOVERY_TAB).setIndicator(DISCOVERY_TAB)
				.setContent(new Intent(this, MyclassActivity.class)));
    	tabHost.addTab(tabHost.newTabSpec(LEARNDATA_TAB).setIndicator(LEARNDATA_TAB)
				.setContent(new Intent(this, PersonActivity.class)));
    	
    	tabHost.addTab(tabHost.newTabSpec(SET_TAB).setIndicator(SET_TAB)
				.setContent(new Intent(this, SetActivity.class)));
		
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}



}
