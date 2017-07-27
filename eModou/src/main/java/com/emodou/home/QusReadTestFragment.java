package com.emodou.home;

//阅读做题界面右面的题目列表模块
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouAnswer;
import com.emodou.domain.EmodouClassManager;
import com.emodou.domain.EmodouPickAnswer;
import com.emodou.domain.EmodouPracticeManager;
import com.emodou.domain.EmodouUserInfo;
import com.emodou.home.QusListenActivity.MyThread;
import com.emodou.home.QusListenActivity.QusItemAdapter;
import com.emodou.util.Constants;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class QusReadTestFragment extends Fragment implements OnClickListener{
	
	private String classid, bookid, type, userid;
	
	//界面属性
	private String str_page;
	private ListView qusListView, indicatorListView;
	private QusItemAdapter qusItemAdapter;
	private IndicatorAdapter indicatorAdapter;
	private Button summit;
	
	private EmodouPracticeManager practiceManager;
	private List<EmodouPracticeManager> practiceManagerList = new ArrayList<EmodouPracticeManager>();//正确题目和选项
	private List<String> indicatorList = new ArrayList<String>();//用来记录用户在提交前哪个题做了哪个未做，1代表做了，0代表未做
	private EmodouPickAnswer pickAnswer = new EmodouPickAnswer();//为了对reviewlist和pick进行页面间的传递而Serializable化
	private List<EmodouPracticeManager> reviewList = new ArrayList<EmodouPracticeManager>();
	private List<String> pickList = new ArrayList<String>();
	private List<String> rightWrong = new ArrayList<String>();//为了显示做题结果中的错题详情
	private int totalQus;
	
	private long startTime, testTime, testUseTime;
	private int rightNum = 0, rightRate = 0;
	
	//是否从做作业界面跳转过来
	private String classroomid, workid, itemid, starttime;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveInstanceState) {
		
		View view = inflater.inflate(R.layout.home_read_qus_test, container, false);
		
		qusListView = (ListView)view.findViewById(R.id.home_read_qus_test_qusList);
		indicatorListView = (ListView)view.findViewById(R.id.home_read_qus_test_indicatorList);
        
        getParams();
        
        init();
        
        return view;
        
    }
	
	public void getParams(){
		bookid = getActivity().getIntent().getExtras().getString("bookid");
		classid = getActivity().getIntent().getExtras().getString("classid");
		type = getActivity().getIntent().getExtras().getString("type");
		//是否从做作业界面跳过来
		classroomid = getActivity().getIntent().getExtras().getString("classroomid");
		workid = getActivity().getIntent().getExtras().getString("workid");
		itemid = getActivity().getIntent().getExtras().getString("itemid");
		starttime = getActivity().getIntent().getExtras().getString("starttime");
		
	}
	
	public void init() {
		
		practiceManagerList.clear();
		indicatorList.clear();
		reviewList.clear();
		pickList.clear();
		rightWrong.clear();
		
		startTime = System.currentTimeMillis();
		
		str_page = ValidateUtils.readFromFile(getActivity(), Constants.LOCAL_START+type+"/"+classid+Constants.LOCAL_JSON1);
		
		try{
			//解析文章文件 获取 actionbar的题目信息
			JSONObject dataJson = new JSONObject(str_page);	
		}catch(JSONException e){
			e.printStackTrace();
		}

		
		//读取题目文本
		String qusString = ValidateUtils.readFromFile(getActivity(), Constants.LOCAL_START + type + "/" + classid + Constants.LOCAL_JSON2);
		try{
			JSONArray qusArray = new JSONArray(qusString);
			for(int i = 0; i<qusArray.length();i++){
				JSONObject object = (JSONObject)qusArray.get(i);
				
				practiceManager = new EmodouPracticeManager();
				
				practiceManager.setQue((String)object.get("que"));
				practiceManager.setNum((String)object.get("num"));
				practiceManager.setType((String)object.get("type"));
				practiceManager.setNo(i+1+"");
				practiceManager.setChoice("unchoose");
				
				JSONObject object2 = (JSONObject)object.get("ans");
				//默认至少有两个选项，最多支持四个选项
				EmodouAnswer answer = new EmodouAnswer();
				answer.setA((String)object2.get("a"));
				answer.setB(object2.getString("b"));
				if(((String)object.get("num")).equals("3")){
					answer.setC(object2.getString("c"));
				}else if(((String)object.get("num")).equals("4")){
					answer.setC(object2.getString("c"));
					answer.setD(object2.getString("d"));
				}
				answer.setType(object2.getString("type"));
				answer.setRight(object2.getString("right"));
				
				practiceManager.setAsw(answer);
				practiceManagerList.add(practiceManager);
				indicatorList.add("0");
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		totalQus = practiceManagerList.size();
		

		qusItemAdapter = new QusItemAdapter(practiceManagerList);
		View view2 = getActivity().getLayoutInflater().inflate(R.layout.home_listen_qus_qusfootview, null);
		summit = (Button)view2.findViewById(R.id.home_listen_qus_qusfootview_summit);
		summit.setBackground(getResources().getDrawable(R.drawable.home_read_qus_test_qusfootview_imvselector));
		summit.setOnClickListener(this);
		qusListView.addFooterView(view2);
		qusListView.setAdapter(qusItemAdapter);
		//qusListView.setOnItemClickListener(new QusOnItemClickListener(qusItemAdapter));
		

		indicatorAdapter = new IndicatorAdapter(indicatorList);
		indicatorListView.setAdapter(indicatorAdapter);
		setLvHeight(indicatorListView);
		
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			
			case R.id.home_listen_qus_qusfootview_summit:
				
				//若题没有全做完
				if(!IfFinish()){
					//提示还有题目没做完，点击右侧灰色圆点
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
					.setTitle(R.string.prompt)
					.setMessage(R.string.home_listen_qus_summitHint)
					.setCancelable(false)
					.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {  
				           public void onClick(DialogInterface dialog, int whichButton) {
				           }					            
				       });
					builder.show();
					break;
				}else{
					ResultCount();
					
					UpdateClassManager();
					
					Intent intent2 = new Intent(getActivity(), QusListenResultActivity.class);
					Bundle mBundle2 = new Bundle();
					mBundle2.putString("type", type);
					mBundle2.putString("classid", classid);
					mBundle2.putString("bookid", bookid);
					mBundle2.putInt("rightNum", rightNum);
					mBundle2.putInt("totalQus", totalQus);
					mBundle2.putInt("rightRate", rightRate);
					mBundle2.putLong("testUseTime", testUseTime);
					if(classroomid != null){
						mBundle2.putString("classroomid", classroomid);
						mBundle2.putString("workid", workid);
						mBundle2.putString("itemid", itemid);
						mBundle2.putString("starttime", starttime);
						//因为正确的选项也要上传,所以要把选项整个传过去
						mBundle2.putSerializable("practiceManagerList", (Serializable)practiceManagerList);
					}
					intent2.putExtras(mBundle2);
					intent2.putExtra("pickAnswer", (Serializable)pickAnswer);
					startActivity(intent2);
					break;
				}
	
				
			default:
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
	
	class QusItemAdapter extends BaseAdapter{
		
		private List<EmodouPracticeManager> practiceManagerAdaList = new ArrayList<EmodouPracticeManager>();
		
		public QusItemAdapter(List<EmodouPracticeManager> practiceManagerList){
			super();
			this.practiceManagerAdaList = practiceManagerList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return practiceManagerAdaList.size();
		}

		@Override
		public  EmodouPracticeManager getItem(int position) {
			// TODO Auto-generated method stub
			return practiceManagerAdaList.get(position);
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
				view = getActivity().getLayoutInflater().inflate(R.layout.home_listen_qus_item, parent,false);
				holder = new ViewHolder();
				holder.titleLayout = (FrameLayout)view.findViewById(R.id.home_listen_qus_item_titleLayout);
				holder.aLayout = (FrameLayout)view.findViewById(R.id.home_listen_qus_item_ALayout);
				holder.aLayout.setOnClickListener((OnClickListener) getActivity());
				holder.bLayout = (FrameLayout)view.findViewById(R.id.home_listen_qus_item_BLayout);
				holder.bLayout.setOnClickListener((OnClickListener) getActivity());
				holder.cLayout = (FrameLayout)view.findViewById(R.id.home_listen_qus_item_CLayout);
				holder.cLayout.setOnClickListener((OnClickListener) getActivity());
				holder.dLayout = (FrameLayout)view.findViewById(R.id.home_listen_qus_item_DLayout);
				holder.dLayout.setOnClickListener((OnClickListener) getActivity());
				holder.titleText = (TextView)view.findViewById(R.id.home_listen_qus_item_titleText);
				holder.aText = (TextView)view.findViewById(R.id.home_listen_qus_item_AText);
				holder.bText = (TextView)view.findViewById(R.id.home_listen_qus_item_BText);
				holder.cText = (TextView)view.findViewById(R.id.home_listen_qus_item_CText);
				holder.dText = (TextView)view.findViewById(R.id.home_listen_qus_item_DText);
				holder.aChoice = (ImageView)view.findViewById(R.id.home_listen_qus_item_Achoice);
				holder.bChoice = (ImageView)view.findViewById(R.id.home_listen_qus_item_Bchoice);
				holder.cChoice = (ImageView)view.findViewById(R.id.home_listen_qus_item_Cchoice);
				holder.dChoice = (ImageView)view.findViewById(R.id.home_listen_qus_item_Dchoice);				
				holder.aA = (TextView)view.findViewById(R.id.home_listen_qus_item_AA);
				holder.bB = (TextView)view.findViewById(R.id.home_listen_qus_item_BB);
				holder.cC = (TextView)view.findViewById(R.id.home_listen_qus_item_CC);
				holder.dD = (TextView)view.findViewById(R.id.home_listen_qus_item_DD);
				
				//将各项改成阅读样式
				holder.titleLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.titleText.setTextColor(getResources().getColor(R.color.actionbar_read));
				holder.aChoice.setBackground(getResources().getDrawable(R.drawable.home_read_qus_test_item_choice_selector));
				holder.bChoice.setBackground(getResources().getDrawable(R.drawable.home_read_qus_test_item_choice_selector));
				holder.cChoice.setBackground(getResources().getDrawable(R.drawable.home_read_qus_test_item_choice_selector));
				holder.dChoice.setBackground(getResources().getDrawable(R.drawable.home_read_qus_test_item_choice_selector));
				
				
				
				//由于阅读只有一半，因此将字体调小
				holder.titleText.setTextSize(25);
				holder.aA.setTextSize(25);
				holder.aText.setTextSize(25);
				holder.bB.setTextSize(25);
				holder.bText.setTextSize(25);
				holder.cC.setTextSize(25);
				holder.cText.setTextSize(25);
				holder.dD.setTextSize(25);
				holder.dText.setTextSize(25);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			holder.titleText.setText((position+1) + " . " +practiceManagerAdaList.get(position).getQue());
			holder.aText.setText(practiceManagerAdaList.get(position).getAsw().getA());
			holder.bText.setText(practiceManagerAdaList.get(position).getAsw().getB());
			holder.cLayout.setVisibility(View.GONE);
			holder.dLayout.setVisibility(View.GONE);
			if(practiceManagerAdaList.get(position).getNum().equals("3")){
				holder.cLayout.setVisibility(View.VISIBLE);
				holder.dLayout.setVisibility(View.GONE);
				holder.cText.setText(practiceManagerAdaList.get(position).getAsw().getC());
			}else if(practiceManagerAdaList.get(position).getNum().equals("4")){
				holder.cLayout.setVisibility(View.VISIBLE);
				holder.dLayout.setVisibility(View.VISIBLE);
				holder.cText.setText(practiceManagerAdaList.get(position).getAsw().getC());
				holder.dText.setText(practiceManagerAdaList.get(position).getAsw().getD());
			}
			
			
			holder.aLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					practiceManagerList.get(position).setChoice("a");
					qusItemAdapter.notifyDataSetChanged();
					indicatorList.set(position, "1");
					indicatorAdapter.notifyDataSetChanged();
				}
			});
			
			
			holder.bLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					practiceManagerList.get(position).setChoice("b");
					qusItemAdapter.notifyDataSetChanged();
					indicatorList.set(position, "1");
					indicatorAdapter.notifyDataSetChanged();
				}
			});
			holder.cLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub				
					practiceManagerList.get(position).setChoice("c");
					qusItemAdapter.notifyDataSetChanged();
					indicatorList.set(position, "1");
					indicatorAdapter.notifyDataSetChanged();
				}
			});
			holder.dLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub				
					practiceManagerList.get(position).setChoice("d");
					qusItemAdapter.notifyDataSetChanged();
					indicatorList.set(position, "1");
					indicatorAdapter.notifyDataSetChanged();
				}
			});
			
			if(practiceManagerList.get(position).getChoice().equals("unchoose")){
				holder.aLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.bLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.cLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.dLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.aChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.bChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.cChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.dChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
			}else if(practiceManagerList.get(position).getChoice().equals("a")){
				holder.aLayout.setBackgroundColor(getResources().getColor(R.color.home_listen_qus_item_ansClickBcg));
				holder.bLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.cLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.dLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.aChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_read_qus_test_item_choice_choice));
				holder.bChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.cChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.dChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
			}else if(practiceManagerList.get(position).getChoice().equals("b")){
				holder.aLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.bLayout.setBackgroundColor(getResources().getColor(R.color.home_listen_qus_item_ansClickBcg));
				holder.cLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.dLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.aChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.bChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_read_qus_test_item_choice_choice));
				holder.cChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.dChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
			}else if(practiceManagerList.get(position).getChoice().equals("c")){
				holder.aLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.bLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.cLayout.setBackgroundColor(getResources().getColor(R.color.home_listen_qus_item_ansClickBcg));
				holder.dLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.aChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.bChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.cChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_read_qus_test_item_choice_choice));
				holder.dChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
			}else if(practiceManagerList.get(position).getChoice().equals("d")){
				holder.aLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.bLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.cLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.dLayout.setBackgroundColor(getResources().getColor(R.color.home_listen_qus_item_ansClickBcg));
				holder.aChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.bChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.cChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.dChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_read_qus_test_item_choice_choice));
			}
			return view;
		}
		
	}
	
	
	private static class IndicatorViewHolder{
		ImageView circle;
		FrameLayout frameLayout;
	}
	
	class IndicatorAdapter extends BaseAdapter{
		
		private List<String> indicatorList = new ArrayList<String>();
		
		public IndicatorAdapter(List<String> indicatorList){
			this.indicatorList = indicatorList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return indicatorList.size();
		}

		@Override
		public String getItem(int position) {
			// TODO Auto-generated method stub
			return indicatorList.get(position);
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
			final IndicatorViewHolder holder;
			if (convertView == null) {
				view = getActivity().getLayoutInflater().inflate(R.layout.home_listen_qus_indicator_item, parent,false);
				holder = new IndicatorViewHolder();
				holder.circle = (ImageView)view.findViewById(R.id.home_listen_qus_indicator_item_circle);	
				holder.frameLayout = (FrameLayout)view.findViewById(R.id.home_listen_qus_indicator_item_layout);

				view.setTag(holder);
			} else {
				holder = (IndicatorViewHolder)view.getTag();
			}
			
			if(indicatorList.get(position).equals("0")){
				holder.circle.setBackground(getResources().getDrawable(R.drawable.home_listen_qus_indicator_item_circle_undo));
			}else{
				//阅读蓝色
				holder.circle.setBackground(getResources().getDrawable(R.drawable.home_read_qus_test_indicator_item_circle_done));
			}
			
			holder.frameLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					qusListView.setSelection(position);
				}
			});
			return view;
		}
		
	}
	
	public boolean IfFinish(){
		
		boolean flag = true;
		for(int i = 0; i < indicatorList.size(); i++){
			if(indicatorList.get(i).equals("0")){
				flag = false;
			}
		}
		return flag;
	}
	
	public void ResultCount(){
		
		//正确题数和正确率
		for(int i = 0; i < practiceManagerList.size();i++){
			if(practiceManagerList.get(i).getChoice()
			   .equals(practiceManagerList.get(i).getAsw().getRight())){
				//用户选择正确时
				rightNum += 1;	
				rightWrong.add("right");
			}else{
				//用户选择不正确时
				reviewList.add(practiceManagerList.get(i));
				pickList.add(practiceManagerList.get(i).getChoice());
				rightWrong.add("wrong");
			}
		}
		
		rightRate =(int)((double)rightNum/totalQus*100);
		
		//用时
		testTime = System.currentTimeMillis();
		testUseTime = testTime - startTime;
		pickAnswer.setReview(reviewList);
		pickAnswer.setPick(pickList);
		pickAnswer.setRightWrong(rightWrong);
	}
	
	//最后需要有一个分数同步服务,需要同步score，state，同时别忘了同步Date
	
	
	public void setLvHeight(ListView listView){

		  ListAdapter listAdapter = listView.getAdapter();
	
		  if (listAdapter == null) {
		   return;
		  }
	
		  int totalHeight = 0;
	
		  for (int i = 0; i < listAdapter.getCount(); i++) {
		   View listItem = listAdapter.getView(i, null, listView);
		   listItem.measure(0, 0);
		   totalHeight += listItem.getMeasuredHeight();
		  }
	
		  ViewGroup.LayoutParams params = listView.getLayoutParams();
	
		  params.height = totalHeight
		    + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	
		  ((MarginLayoutParams) params).setMargins(10, 10, 10, 10); // 可删除
	
		  listView.setLayoutParams(params);
	}
	
	public void UpdateClassManager() {
		// 根据有没有题来标识本课有没有完成，更新数据库
		DbUtils dbUtil = DbUtils.create(getActivity());
		try {
			
			EmodouUserInfo userInfo = dbUtil.findFirst(Selector.from(EmodouUserInfo.class));
			userid = userInfo.getUserid();
			
			EmodouClassManager classManager = dbUtil.findFirst(Selector
					.from(EmodouClassManager.class)
					.where("bookid", "=", bookid).and("classid", "=", classid).and("userid", "=", userid));
			
			if(classManager.getState()!=Constants.EMODOU_CLASS_STATE_LEARENED){
				classManager.setState(Constants.EMODOU_CLASS_STATE_LEARENED);
				dbUtil.update(classManager);
			}

		} catch (DbException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
