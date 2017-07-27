package com.emodou.set;

import com.emodou.extend.SwitchButton;
import com.emodou.extend.SwitchButton.OnChangeListener;
import com.emodou.home.ListenActivity;
import com.emodou.home.ReadActivity;
import com.emodou.home.SpeakActivity;
import com.example.emodou.R;
import com.example.emodou.SetActivity;
import com.iflytek.cloud.InitListener;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class FontFragment extends Fragment implements OnClickListener{
	
	private View fontView;
	
	private TextView bigbtn, middlebtn, smallbtn;
	private TextView fonttext;
	//private Handler handler;
	//private SetActivity set=(SetActivity) getActivity();//set是空？！
	private ListenActivity listen = new ListenActivity();
	private ReadActivity read = new ReadActivity();
	private SpeakActivity speak = new SpeakActivity();
	private static int fontchoice;
	
	
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
        fontView = inflater.inflate(R.layout.set_fragment_font, container, false);
        //handler=set.handler;    
        init();
        return fontView;
        
    } 
	
    
	public void init(){
		
		
		bigbtn = (TextView)fontView.findViewById(R.id.set_fragment_font_bigbtn);
		
		middlebtn = (TextView)fontView.findViewById(R.id.set_fragment_font_middlebtn);
		
		smallbtn = (TextView)fontView.findViewById(R.id.set_fragment_font_smallbtn);
		
		
		
		//handler = setActivity.
		
		//middlebtn.setClickable(true);
		fonttext = (TextView)getActivity().findViewById(R.id.set_list_font_choice);
	}
	
	public void onResume() {
		bigbtn.setOnClickListener(this);
		middlebtn.setOnClickListener(this);
		smallbtn.setOnClickListener(this);
		//remember();
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
			case R.id.set_fragment_font_bigbtn:
				fontchoice=1;
				setcolor();
				bigbtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.set_fragment_font_titlebtn_pressed_shape));
				fonttext.setText("大号");
				listen.setTextsize(1);
				read.setTextsize(1);
				speak.setTextsize(1);
				//handler.sendEmptyMessage(1);
				//set.test();
				break;
			case R.id.set_fragment_font_middlebtn:
				setcolor();
				fontchoice=2;
				middlebtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.set_fragment_font_titlebtn_pressed_shape));
				fonttext.setText("中号");
				listen.setTextsize(2);
				read.setTextsize(2);
				speak.setTextsize(2);
				//handler.sendEmptyMessage(2);
				break;
			case R.id.set_fragment_font_smallbtn:
				setcolor();
				fontchoice=3;
				smallbtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.set_fragment_font_titlebtn_pressed_shape));
				fonttext.setText("小号");
				listen.setTextsize(3);
				read.setTextsize(3);
				speak.setTextsize(3);
				//handler.sendEmptyMessage(3);
				break;
			
	
			default:
				break;
		}
	}
	
	public void setcolor(){
		bigbtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.set_fragment_font_titlebtn_unpressed_shape));
		middlebtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.set_fragment_font_titlebtn_unpressed_shape));
		smallbtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.set_fragment_font_titlebtn_unpressed_shape));	
	}
	
//	public void remember(){
//		
//		if(fontchoice==1){
//			bigbtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.set_fragment_font_titlebtn_pressed_shape));
//			middlebtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.set_fragment_font_titlebtn_unpressed_shape));
//			smallbtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.set_fragment_font_titlebtn_unpressed_shape));
//			fonttext = (TextView)getActivity().findViewById(R.id.set_list_font_choice);
//			//fonttext.setText("大号");
//		}else if(fontchoice==2){
//			bigbtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.set_fragment_font_titlebtn_unpressed_shape));
//			middlebtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.set_fragment_font_titlebtn_pressed_shape));
//			smallbtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.set_fragment_font_titlebtn_unpressed_shape));
//			fonttext = (TextView)getActivity().findViewById(R.id.set_list_font_choice);
//			//fonttext.setText("中号");
//		}else if(fontchoice==3){
//			bigbtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.set_fragment_font_titlebtn_unpressed_shape));
//			middlebtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.set_fragment_font_titlebtn_unpressed_shape));
//			smallbtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.set_fragment_font_titlebtn_pressed_shape));
//			fonttext = (TextView)getActivity().findViewById(R.id.set_list_font_choice);
//			//fonttext.setText("小号");
//		}
//	}
}
