package com.emodou.home;

import java.util.ArrayList;

import com.emodou.domain.EmodouPracticeManager;
import com.example.emodou.R;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;


import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class TestReviewActivity extends FragmentActivity implements OnClickListener{
	

	private String type;
	private String classid, bookid;
    TestFragmentAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;
    private ArrayList<EmodouPracticeManager> reviewlist ;
	private ArrayList<String> picklist ;
	private  ActionBar actionBar;
	private RelativeLayout rlColor;
	private ImageButton imbReturn;
	private TextView  title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_test_review);
        
		classid = getIntent().getExtras().getString("classid");
		bookid = getIntent().getExtras().getString("bookid");
		type = getIntent().getExtras().getString("type");  
	
	  
        reviewlist =  (ArrayList<EmodouPracticeManager>) getIntent().getSerializableExtra("reviewlist");
		picklist =  (ArrayList<String>) getIntent().getSerializableExtra("picklist");
		
        mAdapter = new TestFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }
    
    
    

	class TestFragmentAdapter extends FragmentPagerAdapter{
	    
	    private int mCount = reviewlist.size();
	
	    public TestFragmentAdapter(FragmentManager fm) {
	        super(fm);
	    }
	
	    @Override
	    public Fragment getItem(int position) {
	        return TestFragment.newInstance(reviewlist.get(position), picklist.get(position));
	    }
	
	    @Override
	    public int getCount() {
	        return mCount;
	    }
	
	   
	
	    public void setCount(int count) {
	        if (count > 0 && count <= 10) {
	            mCount = count;
	            notifyDataSetChanged();
	        }
	    }
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
	
	
		default:
			break;
		}
	}
}