package com.emodou.home;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
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
import com.emodou.home.CourseListActivity.classItemClickListener;
import com.emodou.home.QusListenReviewActivity.WrongListAdapter;
import com.emodou.home.SpeakActivity.MyThread;
import com.emodou.util.Constants;
import com.emodou.util.TextPage;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.iflytek.cloud.InitListener;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;

import android.R.integer;
import android.R.string;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class QusListenActivity extends FragmentActivity implements OnClickListener{
	
	private String classid, bookid, type, userid;
	
	//actionbar 及相关
	private ActionBar actionbar;
	private TextView  actionTitle;
	private ImageButton actionReturn;
	private HoloCircularProgressBar progress;
	private MediaPlayer mediaPlayer = new MediaPlayer();
	private String state = "unplay";//是否播放过一遍了
	
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
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_listen_qus);
        
        getParams();
		
        setActionbar();
        
        init();
        
    }
	
	public void getParams(){
		bookid = getIntent().getExtras().getString("bookid");
		classid = getIntent().getExtras().getString("classid");
		type = getIntent().getExtras().getString("type");
		
		//是否从做作业界面跳过来
		classroomid = getIntent().getExtras().getString("classroomid");
		workid = getIntent().getExtras().getString("workid");
		itemid = getIntent().getExtras().getString("itemid");
		starttime = getIntent().getExtras().getString("starttime");
	}
	
	public void setActionbar(){
 		
 		actionbar = getActionBar();
 		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
 		actionbar.setCustomView(R.layout.actionbar_listen_qus);
 		
 		View view = actionbar.getCustomView();
 		
 		//actionbar属性获取操作
 		actionTitle = (TextView)view.findViewById(R.id.actionbar_listen_qus_title);	 		
 		actionReturn = (ImageButton)view.findViewById(R.id.actionbar_listen_qus_return);
 		actionReturn.setOnClickListener(this);
 		progress = (HoloCircularProgressBar)view.findViewById(R.id.actionbar_listen_qus_progress);
 		
 		progress.setProgressColor(getResources().getColor(R.color.actionbar_listen_qus_progressbar));
 		progress.setProgressBackgroundColor(getResources().getColor(R.color.actionbar_listen_qus_progressbcg));
 		progress.setWheelSize(9);
 		progress.setThumbEnabled(false);
 		progress.setMarkerEnabled(false);
 		progress.setProgress((float) (0));
 		progress.setOnClickListener(this);
 		
 		try {
			mediaPlayer.setDataSource(Constants.LOCAL_START+type+"/"+classid+"/res"+Constants.LOCAL_MP31);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 				

	}
	
	public void init() {
		
		practiceManagerList.clear();
		indicatorList.clear();
		reviewList.clear();
		pickList.clear();
		rightWrong.clear();
		
		startTime = System.currentTimeMillis();
		
		str_page = ValidateUtils.readFromFile(this, Constants.LOCAL_START+type+"/"+classid+Constants.LOCAL_JSON1);
		
		try{
			//解析文章文件 获取 actionbar的题目信息
			JSONObject dataJson = new JSONObject(str_page);	
			actionTitle.setText(dataJson.getString("title"));
		}catch(JSONException e){
			e.printStackTrace();
		}

		
		//读取题目文本
		String qusString = ValidateUtils.readFromFile(this, Constants.LOCAL_START + type + "/" + classid + Constants.LOCAL_JSON2);
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
		
		qusListView = (ListView)findViewById(R.id.home_listen_qus_qusList);
		qusItemAdapter = new QusItemAdapter(practiceManagerList);
		View view2 = getLayoutInflater().inflate(R.layout.home_listen_qus_qusfootview, null);
		summit = (Button)view2.findViewById(R.id.home_listen_qus_qusfootview_summit);
		summit.setOnClickListener(this);
		qusListView.addFooterView(view2);
		qusListView.setAdapter(qusItemAdapter);
		//qusListView.setOnItemClickListener(new QusOnItemClickListener(qusItemAdapter));
		
		indicatorListView = (ListView)findViewById(R.id.home_listen_qus_indicatorList);
		indicatorAdapter = new IndicatorAdapter(indicatorList);
		indicatorListView.setAdapter(indicatorAdapter);
		setLvHeight(indicatorListView);
		
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.actionbar_listen_qus_return:
				Intent intent = new Intent(this, ListenActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putString("type", type);
				mBundle.putString("classid", classid);
				mBundle.putString("bookid", bookid);
				if(classroomid != null){
					mBundle.putString("classroomid", classroomid);
					mBundle.putString("workid", workid);
					mBundle.putString("itemid", itemid);
					mBundle.putString("starttime", starttime);
				}
				intent.putExtras(mBundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);				
				break;
			
			case R.id.actionbar_listen_qus_progress:
				
				//还没有播放过
				if(state.equals("unplay")||state.equals("pause")||state.equals("played")){
					progress.setBackground(getResources().getDrawable(R.drawable.actionbar_listen_qus_progress_pause));
					state = "playing";
					try {
						mediaPlayer.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mediaPlayer.start();
					mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
						
						@Override
						public void onCompletion(MediaPlayer arg0) {
							// TODO Auto-generated method stub
							progress.setBackground(getResources().getDrawable(R.drawable.actionbar_listen_qus_progress_replay));	
							state = "played";
							mediaPlayer.reset();
							try {
								mediaPlayer.setDataSource(Constants.LOCAL_START+type+"/"+classid+"/res"+Constants.LOCAL_MP31);
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (SecurityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					});
				}else if(state.equals("playing")){
					progress.setBackground(getResources().getDrawable(R.drawable.actionbar_listen_qus_progress_play));
					state = "pause";
					mediaPlayer.pause();
				}else if(state.equals("pause")){
					progress.setBackground(getResources().getDrawable(R.drawable.actionbar_listen_qus_progress_pause));
					mediaPlayer.start();
					state = "playing";				
				}
				
				MyThread thread = new MyThread();
				new Thread(thread).start();
				break;
				
			
			case R.id.home_listen_qus_qusfootview_summit:
				
				//若题没有全做完
				if(!IfFinish()){
					//提示还有题目没做完，点击右侧灰色圆点
					AlertDialog.Builder builder = new AlertDialog.Builder(QusListenActivity.this)
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
					
					Intent intent2 = new Intent(this, QusListenResultActivity.class);
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
				view = getLayoutInflater().inflate(R.layout.home_listen_qus_item, parent,false);
				holder = new ViewHolder();
				holder.titleLayout = (FrameLayout)view.findViewById(R.id.home_listen_qus_item_titleLayout);
				holder.aLayout = (FrameLayout)view.findViewById(R.id.home_listen_qus_item_ALayout);
				holder.aLayout.setOnClickListener(QusListenActivity.this);
				holder.bLayout = (FrameLayout)view.findViewById(R.id.home_listen_qus_item_BLayout);
				holder.bLayout.setOnClickListener(QusListenActivity.this);
				holder.cLayout = (FrameLayout)view.findViewById(R.id.home_listen_qus_item_CLayout);
				holder.cLayout.setOnClickListener(QusListenActivity.this);
				holder.dLayout = (FrameLayout)view.findViewById(R.id.home_listen_qus_item_DLayout);
				holder.dLayout.setOnClickListener(QusListenActivity.this);
				holder.titleText = (TextView)view.findViewById(R.id.home_listen_qus_item_titleText);
				holder.aText = (TextView)view.findViewById(R.id.home_listen_qus_item_AText);
				holder.bText = (TextView)view.findViewById(R.id.home_listen_qus_item_BText);
				holder.cText = (TextView)view.findViewById(R.id.home_listen_qus_item_CText);
				holder.dText = (TextView)view.findViewById(R.id.home_listen_qus_item_DText);
				holder.aChoice = (ImageView)view.findViewById(R.id.home_listen_qus_item_Achoice);
				holder.bChoice = (ImageView)view.findViewById(R.id.home_listen_qus_item_Bchoice);
				holder.cChoice = (ImageView)view.findViewById(R.id.home_listen_qus_item_Cchoice);
				holder.dChoice = (ImageView)view.findViewById(R.id.home_listen_qus_item_Dchoice);

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
				holder.aChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_choice));
				holder.bChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.cChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.dChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
			}else if(practiceManagerList.get(position).getChoice().equals("b")){
				holder.aLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.bLayout.setBackgroundColor(getResources().getColor(R.color.home_listen_qus_item_ansClickBcg));
				holder.cLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.dLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.aChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.bChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_choice));
				holder.cChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.dChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
			}else if(practiceManagerList.get(position).getChoice().equals("c")){
				holder.aLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.bLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.cLayout.setBackgroundColor(getResources().getColor(R.color.home_listen_qus_item_ansClickBcg));
				holder.dLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.aChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.bChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.cChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_choice));
				holder.dChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
			}else if(practiceManagerList.get(position).getChoice().equals("d")){
				holder.aLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.bLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.cLayout.setBackgroundColor(getResources().getColor(R.color.white));
				holder.dLayout.setBackgroundColor(getResources().getColor(R.color.home_listen_qus_item_ansClickBcg));
				holder.aChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.bChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.cChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_unchoice));
				holder.dChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_listen_qus_item_choice_choice));
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
				view = getLayoutInflater().inflate(R.layout.home_listen_qus_indicator_item, parent,false);
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
				holder.circle.setBackground(getResources().getDrawable(R.drawable.home_listen_qus_indicator_item_circle_done));
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
	
	class MyThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(mediaPlayer.isPlaying()){
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.what = 1;
				Bundle bundle = new Bundle();
				bundle.putFloat("progress",
						(float) ((int) (100 * mediaPlayer
								.getCurrentPosition() / mediaPlayer
								.getDuration())) / 100);
				msg.setData(bundle);
				mHandler.sendMessage(msg);
			}
			
			Message msg = new Message();
			msg.what = 1;
			Bundle bundle = new Bundle();
			bundle.putFloat("progress",
					(float) ((int) (100 * mediaPlayer
							.getCurrentPosition() / mediaPlayer
							.getDuration())) / 100);
			msg.setData(bundle);
			mHandler.sendMessage(msg);
		}
		
	}
	
	Handler mHandler = new Handler() {
		// 注意：在各个case后面不能做太耗时的操作，否则出现ANR对话框
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				progress.setProgress(msg.getData().getFloat("progress"));
				break;	

			}			
			super.handleMessage(msg);
		}
	};
	
	public void onPause() {
		mediaPlayer.pause();
		super.onPause();
	}
	
	public void onResume() {
		if(state != "unplay"&&state == "playing"){
			mediaPlayer.start();
			MyThread thread = new MyThread();
			new Thread(thread).start();
		}
		super.onStart();
	}
	
	public void UpdateClassManager() {
		// 根据有没有题来标识本课有没有完成，更新数据库
		DbUtils dbUtil = DbUtils.create(this);
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
	
	public void onBackPressed() {
		Intent intent = new Intent(this, ListenActivity.class);
		Bundle mBundle = new Bundle();
		mBundle.putString("type", type);
		mBundle.putString("classid", classid);
		mBundle.putString("bookid", bookid);
		if(classroomid != null){
			mBundle.putString("classroomid", classroomid);
			mBundle.putString("workid", workid);
			mBundle.putString("itemid", itemid);
			mBundle.putString("starttime", starttime);
		}
		intent.putExtras(mBundle);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);	
	}
}
