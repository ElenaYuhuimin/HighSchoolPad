package com.emodou.home;

import java.util.ArrayList;
import java.util.List;

import com.emodou.domain.EmodouPickAnswer;
import com.emodou.domain.EmodouPracticeManager;
import com.example.emodou.R;
import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class QusListenReviewActivity extends Activity implements OnClickListener{
	
	private ActionBar actionbar;
	private TextView actionTitle;
	private ImageButton actionReturn;
	private RelativeLayout actionRl;
	private HoloCircularProgressBar circleProgress;
	
	
	private String classid, bookid, type;
	private EmodouPickAnswer pickAnswer;
	
	private ListView listView;
	private WrongListAdapter wronglistAdapter;
	
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_listen_qusreview);
        
        getParams();
		
        setActionbar();
        
        init();
        
    }
	
	public void getParams(){
		bookid = getIntent().getExtras().getString("bookid");
		classid = getIntent().getExtras().getString("classid");
		type = getIntent().getExtras().getString("type");
		pickAnswer = (EmodouPickAnswer)getIntent().getSerializableExtra("pickAnswer");
	}
	
	public void setActionbar(){
 		
 		actionbar = getActionBar();
 		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
 		actionbar.setCustomView(R.layout.actionbar_listen_qus);
 		
 		View view = actionbar.getCustomView();
 		
 		//actionbar属性获取操作
 		actionTitle = (TextView)view.findViewById(R.id.actionbar_listen_qus_title);	
 		actionTitle.setText(R.string.home_listen_qusReview_title);
 		actionReturn = (ImageButton)view.findViewById(R.id.actionbar_listen_qus_return);
 		actionReturn.setOnClickListener(this);
 				
 		circleProgress = (HoloCircularProgressBar)view.findViewById(R.id.actionbar_listen_qus_progress);
 		circleProgress.setVisibility(View.GONE);
	}
	
	public void init() {
		
		listView = (ListView)findViewById(R.id.home_listen_qusreview_reviewList);
		wronglistAdapter = new WrongListAdapter(pickAnswer);
		listView.setAdapter(wronglistAdapter);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
			case R.id.actionbar_listen_qus_return:
				Intent intent = new Intent(this, ListenActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putString("type", type);
				mBundle.putString("classid", classid);
				mBundle.putString("bookid", bookid);
				intent.putExtras(mBundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);				
				break;
		}
	}
	
	private static class ViewHolder{
		FrameLayout titleLayout, aLayout, bLayout, cLayout, dLayout;
		TextView titleText, aText, bText, cText, dText;
		ImageView aRight, aWrong, bRight, bWrong, cRight, cWrong, dRight, dWrong;
		ImageView aChoice, bChoice, cChoice, dChoice;
		TextView aA, bB, cC, dD;
	}
	
	class WrongListAdapter extends BaseAdapter{

		
		private EmodouPickAnswer pickAnswerAda = new EmodouPickAnswer();
		
		public WrongListAdapter(EmodouPickAnswer pickAnswer){
			super();
			this.pickAnswerAda = pickAnswer;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pickAnswer.getPick().size();
		}

		@Override
		public  EmodouPracticeManager getItem(int position) {
			// TODO Auto-generated method stub
			return pickAnswerAda.getReview().get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.home_listen_qus_item, parent,false);
				holder = new ViewHolder();
				holder.titleLayout = (FrameLayout)view.findViewById(R.id.home_listen_qus_item_titleLayout);
				holder.aLayout = (FrameLayout)view.findViewById(R.id.home_listen_qus_item_ALayout);
				holder.aLayout.setOnClickListener(QusListenReviewActivity.this);
				holder.bLayout = (FrameLayout)view.findViewById(R.id.home_listen_qus_item_BLayout);
				holder.bLayout.setOnClickListener(QusListenReviewActivity.this);
				holder.cLayout = (FrameLayout)view.findViewById(R.id.home_listen_qus_item_CLayout);
				holder.cLayout.setOnClickListener(QusListenReviewActivity.this);
				holder.dLayout = (FrameLayout)view.findViewById(R.id.home_listen_qus_item_DLayout);
				holder.dLayout.setOnClickListener(QusListenReviewActivity.this);
				holder.titleText = (TextView)view.findViewById(R.id.home_listen_qus_item_titleText);
				holder.aText = (TextView)view.findViewById(R.id.home_listen_qus_item_AText);
				holder.bText = (TextView)view.findViewById(R.id.home_listen_qus_item_BText);
				holder.cText = (TextView)view.findViewById(R.id.home_listen_qus_item_CText);
				holder.dText = (TextView)view.findViewById(R.id.home_listen_qus_item_DText);
				holder.aRight = (ImageView)view.findViewById(R.id.home_listen_qus_item_ARight);
				holder.aWrong = (ImageView)view.findViewById(R.id.home_listen_qus_item_AWrong);
				holder.bRight = (ImageView)view.findViewById(R.id.home_listen_qus_item_BRight);
				holder.bWrong = (ImageView)view.findViewById(R.id.home_listen_qus_item_BWrong);
				holder.cRight = (ImageView)view.findViewById(R.id.home_listen_qus_item_CRight);
				holder.cWrong = (ImageView)view.findViewById(R.id.home_listen_qus_item_CWrong);
				holder.dRight = (ImageView)view.findViewById(R.id.home_listen_qus_item_DRight);
				holder.dWrong = (ImageView)view.findViewById(R.id.home_listen_qus_item_DWrong);
				
				holder.aChoice = (ImageView)view.findViewById(R.id.home_listen_qus_item_Achoice);
				holder.bChoice = (ImageView)view.findViewById(R.id.home_listen_qus_item_Bchoice);
				holder.cChoice = (ImageView)view.findViewById(R.id.home_listen_qus_item_Cchoice);
				holder.dChoice = (ImageView)view.findViewById(R.id.home_listen_qus_item_Dchoice);
				holder.aChoice.setVisibility(View.INVISIBLE);
				holder.bChoice.setVisibility(View.INVISIBLE);
				holder.cChoice.setVisibility(View.INVISIBLE);
				holder.dChoice.setVisibility(View.INVISIBLE);
				
				holder.aA = (TextView)view.findViewById(R.id.home_listen_qus_item_AA);
				holder.bB = (TextView)view.findViewById(R.id.home_listen_qus_item_BB);
				holder.cC = (TextView)view.findViewById(R.id.home_listen_qus_item_CC);
				holder.dD = (TextView)view.findViewById(R.id.home_listen_qus_item_DD);
				

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			holder.titleText.setText(pickAnswerAda.getReview().get(position).getNo() + " . " +
									 pickAnswerAda.getReview().get(position).getQue());
			holder.aText.setText(pickAnswerAda.getReview().get(position).getAsw().getA());
			holder.bText.setText(pickAnswerAda.getReview().get(position).getAsw().getB());
			if(pickAnswerAda.getReview().get(position).getNum().equals("3")){
				holder.cLayout.setVisibility(View.VISIBLE);
				holder.cText.setText(pickAnswerAda.getReview().get(position).getAsw().getC());
				holder.dLayout.setVisibility(View.GONE);
			}else if(pickAnswerAda.getReview().get(position).getNum().equals("4")){
				holder.cLayout.setVisibility(View.VISIBLE);
				holder.dLayout.setVisibility(View.VISIBLE);
				holder.cText.setText(pickAnswerAda.getReview().get(position).getAsw().getC());
				holder.dText.setText(pickAnswerAda.getReview().get(position).getAsw().getD());
			}
			
			
			
			//先将之前已经选好的选项背景清空
			holder.aLayout.setBackgroundColor(getResources().getColor(R.color.white));
			holder.bLayout.setBackgroundColor(getResources().getColor(R.color.white));
			holder.cLayout.setBackgroundColor(getResources().getColor(R.color.white));
			holder.dLayout.setBackgroundColor(getResources().getColor(R.color.white));
			holder.aRight.setVisibility(View.GONE);
			holder.bRight.setVisibility(View.GONE);
			holder.cRight.setVisibility(View.GONE);
			holder.dRight.setVisibility(View.GONE);
			holder.aWrong.setVisibility(View.GONE);
			holder.bWrong.setVisibility(View.GONE);
			holder.cWrong.setVisibility(View.GONE);
			holder.dWrong.setVisibility(View.GONE);
			holder.aText.setTextColor(getResources().getColor(R.color.black));
			holder.bText.setTextColor(getResources().getColor(R.color.black));
			holder.cText.setTextColor(getResources().getColor(R.color.black));
			holder.dText.setTextColor(getResources().getColor(R.color.black));
			holder.aA.setTextColor(getResources().getColor(R.color.black));
			holder.bB.setTextColor(getResources().getColor(R.color.black));
			holder.cC.setTextColor(getResources().getColor(R.color.black));
			holder.dD.setTextColor(getResources().getColor(R.color.black));
			
			if(pickAnswerAda.getReview().get(position).getAsw().getRight().equals("a")){
				holder.aText.setTextColor(getResources().getColor(R.color.home_listen_qusreview_righttext));
				holder.aA.setTextColor(getResources().getColor(R.color.home_listen_qusreview_righttext));
				holder.aRight.setVisibility(View.VISIBLE);
			}else if(pickAnswerAda.getReview().get(position).getAsw().getRight().equals("b")){
				holder.bText.setTextColor(getResources().getColor(R.color.home_listen_qusreview_righttext));
				holder.bB.setTextColor(getResources().getColor(R.color.home_listen_qusreview_righttext));
				holder.bRight.setVisibility(View.VISIBLE);
			}else if(pickAnswerAda.getReview().get(position).getAsw().getRight().equals("c")){
				holder.cText.setTextColor(getResources().getColor(R.color.home_listen_qusreview_righttext));
				holder.cC.setTextColor(getResources().getColor(R.color.home_listen_qusreview_righttext));
				holder.cRight.setVisibility(View.VISIBLE);
			}else if(pickAnswerAda.getReview().get(position).getAsw().getRight().equals("d")){
				holder.dText.setTextColor(getResources().getColor(R.color.home_listen_qusreview_righttext));
				holder.dD.setTextColor(getResources().getColor(R.color.home_listen_qusreview_righttext));
				holder.dRight.setVisibility(View.VISIBLE);
			}
			
			if(pickAnswerAda.getReview().get(position).getChoice().equals("a")){
				holder.aText.setTextColor(getResources().getColor(R.color.home_listen_qusreview_wrongtext));
				holder.aA.setTextColor(getResources().getColor(R.color.home_listen_qusreview_wrongtext));
				holder.aWrong.setVisibility(View.VISIBLE);
			}else if(pickAnswerAda.getReview().get(position).getChoice().equals("b")){
				holder.bText.setTextColor(getResources().getColor(R.color.home_listen_qusreview_wrongtext));
				holder.bB.setTextColor(getResources().getColor(R.color.home_listen_qusreview_wrongtext));
				holder.bWrong.setVisibility(View.VISIBLE);
			}else if(pickAnswerAda.getReview().get(position).getChoice().equals("c")){
				holder.cText.setTextColor(getResources().getColor(R.color.home_listen_qusreview_wrongtext));
				holder.cC.setTextColor(getResources().getColor(R.color.home_listen_qusreview_wrongtext));
				holder.cWrong.setVisibility(View.VISIBLE);
			}else if(pickAnswerAda.getReview().get(position).getChoice().equals("d")){
				holder.dText.setTextColor(getResources().getColor(R.color.home_listen_qusreview_wrongtext));
				holder.dD.setTextColor(getResources().getColor(R.color.home_listen_qusreview_wrongtext));
				holder.dWrong.setVisibility(View.VISIBLE);
			}
			return view;
		}
		
	
	}
	
	public void onBackPressed() {
		Intent intent = new Intent(this, ListenActivity.class);
		Bundle mBundle = new Bundle();
		mBundle.putString("type", type);
		mBundle.putString("classid", classid);
		mBundle.putString("bookid", bookid);
		intent.putExtras(mBundle);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);		
	};

}
