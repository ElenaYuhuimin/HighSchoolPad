package com.emodou.set;

import com.emodou.extend.SwitchButton;
import com.emodou.extend.SwitchButton.OnChangeListener;
import com.example.emodou.R;
import com.example.emodou.SetActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class PushFragment extends Fragment{
	
	private View pushView;
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
        pushView = inflater.inflate(R.layout.set_fragment_push, container, false);
        
        SwitchButton sb = (SwitchButton)pushView.findViewById(R.id.set_fragment_push_switch1);  
        sb.setOnChangeListener(new OnChangeListener() {  
              
            @Override  
            public void onChange(SwitchButton sb, boolean state) {  
                // TODO Auto-generated method stub  
                Toast.makeText(getActivity(), state ? "开":"关", Toast.LENGTH_SHORT).show();  
            }  
        });
        return pushView;
    } 
	
	
}
