package com.emodou.home;
/*
 * 单词学习界面返回时，弹出的提示框，提示目前的学习状态以及学习时长
 */

import java.text.SimpleDateFormat;
import java.util.DuplicateFormatFlagsException;

import com.emodou.util.ValidateUtils;
import com.example.emodou.R;

import android.R.integer;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class WordLearnReDialog extends Dialog implements OnClickListener{
	
	Context context;
	private TextView unFamiTv, vagueTv, famiTv, notLearnTv;
	private TextView timeTv;
	private ImageView exitImv, continueImv; 
	
	private DialogListener listener;
	
	public interface DialogListener{
		public void onClick(View view);
	}
	
	public WordLearnReDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public WordLearnReDialog(Context context, int style) {
		// TODO Auto-generated constructor stub
		super(context,style);
		this.context = context;
		setDialogView();
	}
	
	public void setListener(DialogListener listener) {
		this.listener = listener;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.setContentView(R.layout.word_learn_dialog_return);
		
		exitImv.setOnClickListener(this);
		continueImv.setOnClickListener(this);
	}
	
	public void setDialogView() {
		View view = LayoutInflater.from(getContext()).inflate(R.layout.word_learn_dialog_return, null);
		unFamiTv = (TextView)view.findViewById(R.id.word_learn_dialog_return_unfamiliarTv);
		vagueTv = (TextView)view.findViewById(R.id.word_learn_dialog_return_vagueTv);
		famiTv = (TextView)view.findViewById(R.id.word_learn_dialog_return_familiarTv);
		notLearnTv = (TextView)view.findViewById(R.id.word_learn_dialog_return_notLearnTv);
		timeTv = (TextView)view.findViewById(R.id.word_learn_dialog_return_time);
		
		exitImv = (ImageView)view.findViewById(R.id.word_learn_dialog_return_exit);
		continueImv = (ImageView)view.findViewById(R.id.word_learn_dialog_return_continue);
		
		super.setContentView(view);
	}
	
	public void setStateNum(int unFamiNum, int vagueNum, int famiNum, int notLearnNum) {
		if(unFamiNum == 0){
			unFamiTv.setVisibility(View.GONE);
		}else if(unFamiNum > 0){
			unFamiTv.setText("陌生"+"\n("+ unFamiNum +")");
		}
		
		if(vagueNum == 0){
			vagueTv.setVisibility(View.GONE);
		}else if(vagueNum > 0){
			vagueTv.setText("模糊\n("+vagueNum+")");
		}
		if(famiNum == 0){
			famiTv.setVisibility(View.GONE);
		}else if(famiNum > 0){
			famiTv.setText("熟悉\n("+famiNum+")");
		}
		
		if(notLearnNum == 0){
			notLearnTv.setVisibility(View.GONE);
		}else if(notLearnNum > 0){
			notLearnTv.setText("未学\n("+notLearnNum+")");
		}
		
		
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		listener.onClick(view);
	}
	
	public void setLearnTime(long learnTime) {
		if(learnTime >= 360000000){
			timeTv.setText(getContext().getResources().getString(R.string.word_learn_dialog_return_timeMuch));
		}else{
			String transTime = ValidateUtils.transTimeFormat(learnTime);
			timeTv.setText(getContext().getResources().getString(R.string.word_learn_dialog_return_time) + transTime);
		}
		
	}

}
