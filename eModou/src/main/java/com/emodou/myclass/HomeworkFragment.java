package com.emodou.myclass;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kymjs.kjframe.http.HttpCallBack;

import com.emodou.domain.EmodouWork;
import com.emodou.home.ChangeBookActivity.ViewHolder;
import com.emodou.home.CourseListActivity.classItemClickListener;
import com.emodou.util.Constants;
import com.emodou.util.LogUtil;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;
import android.R.integer;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class HomeworkFragment extends Fragment{
	
	private View homeworkView ;
	
	private PullToRefreshListView listView ;
	private ProgressDialog progressDialog;
	
	private String userid, classroomid, ticket;
    private Context context;
	private List<List<EmodouWork>> workList = new ArrayList<List<EmodouWork>>();
	private List<String> timeList = new ArrayList<String>();
	private WorkListAdapter workListAdapter;
	private SmallitemAdapter smallAdapter;
	private long refreshTime;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		homeworkView = inflater.inflate(R.layout.myclass_homework, container, false);	
		
		init();

		return homeworkView;
	}
	
	
	
	private void init() {
		// TODO Auto-generated method stub
		listView = (PullToRefreshListView)homeworkView.findViewById(R.id.myclass_homework_listview);
		listView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				if(ValidateUtils.isNetworkConnected(getActivity())){
					refreshListFromHttp();
				}
				else{
					Toast.makeText(getActivity(), R.string.prompt_network, 0).show();
				}
				
				listView.postDelayed(new Runnable() {				
					@Override
					public void run() {
						listView.onRefreshComplete();
						workListAdapter.notifyDataSetChanged();
					}
				}, refreshTime);
			}
		});
	}



	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
		super.onResume();
	}
	
	public void passData(String userid, String classroomid, String ticket, Context context) {

		this.userid = userid;
		this.classroomid = classroomid;
		this.ticket = ticket;
		this.context = context;
		
//		refreshFromLocal();
//		if(workList == null){
//			
//		}
		
		if(ValidateUtils.isNetworkConnected(this.context)){
			refreshListFromHttp();
		}
		else{
			Toast.makeText(this.context, R.string.prompt_network, 0).show();
		}
			
	}

	public void refreshListFromHttp() {
		final long refreshStart = System.currentTimeMillis();
		progressDialog = new ProgressDialog(context);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Loading...");
		progressDialog.show();
		
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addBodyParameter("userid",userid);
		params.addBodyParameter("classroomid", classroomid);
		params.addBodyParameter("role","s");
		params.addBodyParameter("ticket",ticket);
		
		String path = Constants.EMODOU_URL + Constants.JS_API2_START + Constants.JS_GET_WORKLIST;
		
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, path, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				try{									
					String res = responseInfo.result.toString();
					int index = res.indexOf("{");
					if(index == -1){
						Toast.makeText(getActivity(), R.string.myclass_homework_refreshHttpFail,Toast.LENGTH_LONG).show();			
						progressDialog.dismiss();
					}else{
						
						//从index位开始截取
						res = res.substring(index);
						
						//res已经是string类型
						JSONObject result = new JSONObject(res);
						String status = (String)result.get("Status");
						
						if(status.equals("Success")){
							
							DbUtils dbUtils = DbUtils.create(getActivity());
							
							JSONArray workArray = result.getJSONArray("WorkList");
							for(int i = 0; i<workArray.length(); i++){
								JSONObject workObject = workArray.getJSONObject(i);
								String workid = workObject.getString("workid");
								String workname = workObject.getString("workname");
								String starttime = workObject.getString("starttime");
								String addtime = workObject.getString("addtime");
								String endtime = workObject.getString("endtime");
								String total = workObject.getString("total");
								String finished = workObject.getString("finished"); 
								
								EmodouWork work = new EmodouWork();
								work = dbUtils.findFirst(Selector.from(EmodouWork.class)
										                         .where("userid","=",userid)
										                         .and("classroomid","=",classroomid)
										                         .and("workid","=",workid));
								
								if(work != null){
									work.setWorkname(workname);
									work.setStarttime(starttime);
									work.setAddtime(addtime);
									work.setEndtime(endtime);
									work.setTotal(total);
									work.setFinished(finished);
									if(Long.valueOf(endtime)*1000 <= System.currentTimeMillis()) 
										work.setIfcutoff(Constants.WORK_CUTOFF);
									else 
										work.setIfcutoff(Constants.WORK_UNCUTOFF);
									dbUtils.update(work);
								}else{
									EmodouWork workNew = new EmodouWork();
									workNew.setUserid(userid);
									workNew.setClassroomid(classroomid);
									workNew.setWorkid(workid);
									workNew.setWorkname(workname);
									workNew.setStarttime(starttime);
									workNew.setAddtime(addtime);
									workNew.setEndtime(endtime);
									workNew.setTotal(total);
									workNew.setFinished(finished);
									workNew.setIfcheck(Constants.WORK_UNCHECK);
									Long now = System.currentTimeMillis();
									Long endLong = Long.valueOf(endtime);
									if(endLong*1000 <= now) 
										workNew.setIfcutoff(Constants.WORK_CUTOFF);
									else 
										workNew.setIfcutoff(Constants.WORK_UNCUTOFF);
									//这里要为将来同步留接口
									workNew.setIfdone(Constants.WORK_UNDONE);
									workNew.setIfcheck(Constants.WORK_UNCHECK);
									dbUtils.saveBindingId(workNew);
								}
							}
							
							//初始化列表list
							refreshFromLocal();
							long refreshEnd = System.currentTimeMillis();
							refreshTime = refreshEnd - refreshStart;
							progressDialog.dismiss();
						}else{
							//返回错误信息
							Toast.makeText(getActivity(), R.string.myclass_homework_refreshHttpFail,Toast.LENGTH_LONG).show();			
							progressDialog.dismiss();
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), R.string.myclass_homework_refreshHttpFail,Toast.LENGTH_LONG).show();			
				progressDialog.dismiss();
			}
		});
		
		
	}
	
	public void refreshFromLocal() {
		workList.clear();
		timeList.clear();
		DbUtils dbUtils = DbUtils.create(context);
		try{
			List<EmodouWork> works = new ArrayList<EmodouWork>();
			works = dbUtils.findAll(Selector.from(EmodouWork.class)
					                           .where("userid","=",userid)
					                           .and("classroomid","=",classroomid));
			if(works != null && works.size() != 0){
				listView.setVisibility(View.VISIBLE);
				//因为JS返回来的时间是从远到近的，因此要掉过来看
				for(int i = works.size() -1; i>=0;i--){
					String dateStr = works.get(i).getStarttime();
					Long dateLong = Long.parseLong(dateStr);
					String fomatdateStr = ValidateUtils.transferLongToDate("yyyy-MM-dd", dateLong*1000);
					timeList.add(fomatdateStr);
				}
				timeList = removeDuplicate(timeList);
				for(String timeStr : timeList){
					List<EmodouWork> works2 = new ArrayList<EmodouWork>();
					for(int i = works.size() -1; i>=0;i--){
						String dateStr = works.get(i).getStarttime();
						Long dateLong = Long.parseLong(dateStr);
						String fomatdateStr = ValidateUtils.transferLongToDate("yyyy-MM-dd", dateLong*1000);
						if(timeStr.equals(fomatdateStr))
							works2.add(works.get(i));
					}
					workList.add(works2);
				}
				
				workListAdapter = new WorkListAdapter(workList, context);
				listView.setAdapter(workListAdapter);
			}else{
				listView.setVisibility(View.GONE);
			}
			
		}catch(DbException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * 去除重复数据
	 */
	public static List<String> removeDuplicate(List<String> list)
	{
	        Set set = new LinkedHashSet<String>();
	        set.addAll(list);
	        list.clear();
	        list.addAll(set);
	        return list;
	} 
	
	public class ViewHolder{
		private TextView dateTv;
		private ListView smallLv;
		private ImageView uncheckImv, checkImv;
	}
	
	public class WorkListAdapter extends BaseAdapter{
		
		private List<List<EmodouWork>> myworkList = new ArrayList<List<EmodouWork>>();
		private LayoutInflater inflater;
		
		public WorkListAdapter(List<List<EmodouWork>> workList, Context context){
			this.myworkList = workList;
			inflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return myworkList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return myworkList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			ViewHolder viewHolder;
			if(convertView == null){
				view = inflater.inflate(R.layout.myclass_homework_bigitem, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.dateTv = (TextView)view.findViewById(R.id.myclass_homework_bigitem_dateTv);
				viewHolder.smallLv = (ListView)view.findViewById(R.id.myclass_homework_bigitem_smallLv);
				viewHolder.uncheckImv = (ImageView)view.findViewById(R.id.myclass_homework_bigitem_uncheck);
				viewHolder.checkImv = (ImageView)view.findViewById(R.id.myclass_homework_bigitem_check);
				view.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder)view.getTag();
			}
			
			String dateStr = myworkList.get(position).get(0).getStarttime();
			Long dateLong = Long.parseLong(dateStr);
			viewHolder.dateTv.setText(ValidateUtils.transferLongToDate("yyyy-MM-dd", dateLong*1000));
			
		    int ifcheck = 1;
			List<EmodouWork> works = new ArrayList<EmodouWork>();
			works = myworkList.get(position);
			for(EmodouWork work : works){
				if(work.getIfcheck().equals(Constants.WORK_UNCHECK))
					ifcheck = 0;
			}
			if(ifcheck == 0){
				//有没查看过的作业
				viewHolder.uncheckImv.setVisibility(View.VISIBLE);
				viewHolder.checkImv.setVisibility(View.GONE);
			}else{
				viewHolder.uncheckImv.setVisibility(View.GONE);
				viewHolder.checkImv.setVisibility(View.VISIBLE);
			}
			
			smallAdapter = new SmallitemAdapter(works, context);
			viewHolder.smallLv.setAdapter(smallAdapter);
			viewHolder.smallLv.setOnItemClickListener(new smallItemClickListener(works, userid, ticket));
			LayoutParams lParams = viewHolder.smallLv.getLayoutParams();
			lParams.height = ValidateUtils.setListViewHeightBasedOnChildren(viewHolder.smallLv);
			viewHolder.smallLv.setLayoutParams(lParams);
			return view;
		}
		
	}
	
	public class SmallViewHolder{
		private TextView nameTv, endtimeTv;
		private ImageView inImv, doneImv, cutoffImv;
	}
	
	public class SmallitemAdapter extends BaseAdapter{
		
		private List<EmodouWork> smallworkList = new ArrayList<EmodouWork>();
		private LayoutInflater inflater;
		
		public SmallitemAdapter(List<EmodouWork> workList, Context context){
			this.smallworkList = workList;
			inflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return smallworkList.size();
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return smallworkList.get(position);
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			SmallViewHolder smallHolder;
			if(convertView == null){
				view = inflater.inflate(R.layout.myclass_homework_smallitem, parent, false);
				smallHolder = new SmallViewHolder();
				smallHolder.nameTv = (TextView)view.findViewById(R.id.myclass_homework_smallitem_nameTv);
				smallHolder.endtimeTv = (TextView)view.findViewById(R.id.myclass_homework_smallitem_endtime);
				smallHolder.inImv = (ImageView)view.findViewById(R.id.myclass_homework_smallitem_inImv);
				smallHolder.doneImv = (ImageView)view.findViewById(R.id.myclass_homework_smallitem_doneImv);
				smallHolder.cutoffImv = (ImageView)view.findViewById(R.id.myclass_homework_smallitem_cutoffImv);
				view.setTag(smallHolder);
			}else{
				smallHolder = (SmallViewHolder)view.getTag();
			}
			
			smallHolder.nameTv.setText(smallworkList.get(position).getWorkname());
			smallHolder.endtimeTv.setText("截止日期  : " + ValidateUtils.transferLongToDate("yyyy-MM-dd", Long.valueOf(smallworkList.get(position).getEndtime())*1000));
			if(smallworkList.get(position).getIfcheck().equals(Constants.WORK_UNCHECK)){
				//没有查看，则字体为红
				smallHolder.nameTv.setTextColor(getResources().getColor(R.color.red));
				smallHolder.endtimeTv.setTextColor(getResources().getColor(R.color.red));
			}else if(smallworkList.get(position).getIfcheck().equals(Constants.WORK_CHECK)){
				smallHolder.nameTv.setTextColor(getResources().getColor(R.color.black));
				smallHolder.endtimeTv.setTextColor(getResources().getColor(R.color.black));
			}
			if(smallworkList.get(position).getIfdone().equals(Constants.WORK_DONE)){
				smallHolder.doneImv.setVisibility(View.VISIBLE);
				smallHolder.cutoffImv.setVisibility(View.GONE);
				smallHolder.inImv.setVisibility(View.GONE);
				smallHolder.nameTv.setTextColor(getResources().getColor(R.color.black));
				smallHolder.endtimeTv.setTextColor(getResources().getColor(R.color.black));
			}else{
				//没做完或者ifdone为空
				if(smallworkList.get(position).getIfcutoff().equals(Constants.WORK_CUTOFF)){
					//做完了但过期了
					smallHolder.doneImv.setVisibility(View.GONE);
					smallHolder.cutoffImv.setVisibility(View.VISIBLE);
					smallHolder.inImv.setVisibility(View.GONE);
				}else{
					//没做完但还没过期
					smallHolder.doneImv.setVisibility(View.GONE);
					smallHolder.cutoffImv.setVisibility(View.GONE);
					smallHolder.inImv.setVisibility(View.VISIBLE);
				}
			}
			return view;
		}		
		
	}
	
	public class smallItemClickListener implements OnItemClickListener{
		
		private String  userid, ticket;
		private List<EmodouWork> workList;
		
		public smallItemClickListener(List<EmodouWork> works, String userid, String ticket){
			this.workList = works;
			this.userid = userid;
			this.ticket = ticket;
		}

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			//将数据库中的状态变为查看
			DbUtils dbUtils = DbUtils.create(getActivity());
			try {
				EmodouWork emodouWork = new EmodouWork();
				emodouWork = dbUtils.findFirst(Selector.from(EmodouWork.class)
						                               .where("userid","=",userid)
						                               .and("classroomid","=",workList.get(position).getClassroomid())
						                               .and("workid","=",workList.get(position).getWorkid()));
				if(emodouWork != null){
					emodouWork.setIfcheck(Constants.WORK_CHECK);
					dbUtils.update(emodouWork);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			Intent intent = new Intent(getActivity(), WorkDetailActivity.class);
			intent.putExtra("userid", userid);
			intent.putExtra("ticket", ticket);
			intent.putExtra("classroomid", workList.get(position).getClassroomid());
			intent.putExtra("workid", workList.get(position).getWorkid());
			startActivity(intent);
		}
		
	}
}
