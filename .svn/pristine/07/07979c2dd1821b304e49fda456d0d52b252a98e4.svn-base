package com.emodou.home;

//阅读做题界面左面原文
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.emodou.domain.EmodouReadText;
import com.emodou.home.ListenActivity.ItemAdapter;
import com.emodou.util.Constants;
import com.emodou.util.TextPage;
import com.emodou.util.ValidateUtils;
import com.example.emodou.R;

import android.R.integer;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class QusReadPageFragment extends Fragment{
	
	//获得的参数
	private String bookid, classid, type;
	
	//界面属性
	private ListView pageListView;
	private PageItemAdapter pageAdapter;
	private List<EmodouReadText> pageTextList = new ArrayList<EmodouReadText>();
	private String str_page;
	private static int textsize;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveInstanceState) {
		View view = inflater.inflate(R.layout.home_read_qus_page, container, false);
		
		pageListView = (ListView)view.findViewById(R.id.home_read_qus_pagelist);
						
		getParams();
		
		return view;
	}
	
	public void getParams(){
		
		bookid = getActivity().getIntent().getStringExtra("bookid");
		classid = getActivity().getIntent().getStringExtra("classid");
		type = getActivity().getIntent().getStringExtra("type");
		textsize = getActivity().getIntent().getIntExtra("textsize", 1);
		
		str_page = ValidateUtils.readFromFile(getActivity(), Constants.LOCAL_START+type+"/"+classid+Constants.LOCAL_JSON1);
		
		try {
			
			//解析文章文件 获取 相应的信息
			JSONObject dataJson = new JSONObject(str_page);

			JSONArray array = dataJson.getJSONArray("text");

			for (int i = 0; i < array.length(); i++) {

				JSONObject object = (JSONObject) array.get(i);
				String en = (String) object.get("en");

				//将资源里面的换行 用空替换掉
				String cn = (String) object.get("cn");
				en = en.replace('\n',' '); 
				cn = cn.replace('\n',' '); 
				EmodouReadText text = new EmodouReadText();
				text.setCn(cn);
				text.setEn(en);
				if (text != null){
					pageTextList.add(text);	
				}
					

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pageAdapter = new PageItemAdapter(pageTextList);
		pageListView.setAdapter(pageAdapter);
	}
	
	private static class ViewHolder{
		TextPage en;
		TextView cn;
	}
	
	public class PageItemAdapter extends BaseAdapter{
		
		private List<EmodouReadText> pageTextList = new ArrayList<EmodouReadText>();
		
		public PageItemAdapter(List<EmodouReadText> pageTextList) {
			super();
			this.pageTextList = pageTextList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pageTextList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return pageTextList.get(position);
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
			final ViewHolder holder;
			if(convertView == null){
				view = getActivity().getLayoutInflater().inflate(R.layout.read_item_list, parent, false);
				holder = new ViewHolder();
				holder.en = (TextPage)view.findViewById(R.id.mytestpage);
				holder.cn = (TextView)view.findViewById(R.id.tv_han);
				view.setTag(holder);
			}else{
				holder = (ViewHolder)view.getTag();
			}
			
			//修改字体大小
			if (textsize == 1) {
				holder.cn.setTextSize(35);
				holder.en.setTextSize(30);
			} else if (textsize == 2) {
				holder.cn.setTextSize(30);
				holder.en.setTextSize(25);
			} else if (textsize == 3) {
				holder.cn.setTextSize(25);
				holder.en.setTextSize(20);
			}
			
			//设置中英文
			holder.en.setText(pageTextList.get(position).getEn());
			holder.cn.setText(pageTextList.get(position).getCn());
			
			holder.cn.setVisibility(View.GONE);
			return view;
		}
		
	}
}
