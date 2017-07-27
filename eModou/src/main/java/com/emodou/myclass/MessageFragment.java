package com.emodou.myclass;

import com.example.emodou.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MessageFragment extends Fragment{
	
	private View messageView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		messageView = inflater.inflate(R.layout.myclass_message, container,false);
		return messageView;
	}
}
