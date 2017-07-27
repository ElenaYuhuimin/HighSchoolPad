package com.emodou.home;

import com.emodou.domain.EmodouPracticeManager;
import com.example.emodou.R;

import android.graphics.Color;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import junit.framework.Test;


public final class TestFragment extends Fragment {
    private String pick;
    private EmodouPracticeManager sj;

	public TestFragment(){

	}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout =  inflater.inflate(R .layout.home_read_review, container, false);   
        final ViewHolder holder;
      
		holder = new ViewHolder();
		holder.text = (TextView) layout
				.findViewById(R.id.tv_test_qus);
	/*	holder.image1 = (ImageView) layout
				.findViewById(R.id.im_test_1);
		holder.image2 = (ImageView) layout
				.findViewById(R.id.im_test_2);
		holder.image3 = (ImageView) layout
				.findViewById(R.id.im_test_3);
		
		holder.image4 = (ImageView) layout
				.findViewById(R.id.im_test_4);*/
		holder.text1 = (TextView) layout
				.findViewById(R.id.tv_test_answer1);
		holder.text2 = (TextView) layout.findViewById(R.id.tv_test_answer2);
		holder.text3 = (TextView) layout
				.findViewById(R.id.tv_test_answer3);
		
		holder.text4 = (TextView) layout
				.findViewById(R.id.tv_test_answer4);
		
		holder.rl3 = (RelativeLayout) layout.findViewById(R.id.rl_3);
		holder.rl4 = (RelativeLayout) layout.findViewById(R.id.rl_4);
			
          
		holder.text.setText(sj.getQue());
		holder.text1.setText(sj.getAsw().getA());
		holder.text2.setText(sj.getAsw().getB());
		holder.text3.setText(sj.getAsw().getC());
		holder.text4.setText(sj.getAsw().getD());
	
		
		
		if(sj.getNum().equals("3")){
			holder.rl3.setVisibility(View.VISIBLE);
		}else if(sj.getNum().equals("4")){
			holder.rl3.setVisibility(View.VISIBLE);
			holder.rl4.setVisibility(View.VISIBLE);
			
		}
		
		if(sj.getAsw().getRight().equals("a")){
			holder.text1.setBackgroundResource(R.drawable.border_right);
			holder.text1.setTextColor(Color.WHITE);
			
		}else if(sj.getAsw().getRight().equals("b")){
			holder.text2.setBackgroundResource(R.drawable.border_right);
			holder.text2.setTextColor(Color.WHITE);
			
			
		}else if(sj.getAsw().getRight().equals("c")){
			holder.text3.setBackgroundResource(R.drawable.border_right);
			holder.text3.setTextColor(Color.WHITE);
			
			
		}else if(sj.getAsw().getRight().equals("d")){
			holder.text4.setBackgroundResource(R.drawable.border_right);
			holder.text4.setTextColor(Color.WHITE);
			
			
		}
		
		
		if(pick.equals("a")){
			holder.text1.setBackgroundResource(R.drawable.border_wrong);
			holder.text1.setTextColor(Color.WHITE);
			
		}else if (pick.equals("b")){
			holder.text2.setBackgroundResource(R.drawable.border_wrong);
			holder.text2.setTextColor(Color.WHITE);
			
		}else if(pick.equals("c")){
			holder.text3.setBackgroundResource(R.drawable.border_wrong);
			holder.text3.setTextColor(Color.WHITE);
			
		}else if(pick.equals("d")){
			holder.text4.setBackgroundResource(R.drawable.border_wrong);
			holder.text4.setTextColor(Color.WHITE);
			
		}

        return layout;
    }
    
    
    private static class ViewHolder {
		TextView text, text1, text2, text3, text4;
		ImageView image1, image2, image3, image4 ;
		RelativeLayout rl3, rl4;
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


//	public TestFragment(EmodouPracticeManager sj,String pick) {
//		super();
//		this.sj = sj;
//		this.pick = pick;
//	}

	public static TestFragment newInstance(EmodouPracticeManager sj, String pick){
		TestFragment newFragment = new TestFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable("sj",sj);
		bundle.putString("pick", pick);
		newFragment.setArguments(bundle);
		return newFragment;
	}


	public String getPick() {
		return pick;
	}


	public void setPick(String pick) {
		this.pick = pick;
	}


	public EmodouPracticeManager getSj() {
		return sj;
	}


	public void setSj(EmodouPracticeManager sj) {
		this.sj = sj;
	}
}
