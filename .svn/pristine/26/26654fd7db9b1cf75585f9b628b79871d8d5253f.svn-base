package com.emodou.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

//TextView字体类


public class CustomFontTextView extends TextView{

	public CustomFontTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	public CustomFontTextView(Context context,AttributeSet attrs) {
		super(context,attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	public CustomFontTextView(Context context,AttributeSet attrs,int defStyles) {
		super(context,attrs,defStyles);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private void init(Context context){
		AssetManager assertMgr = context.getAssets();
		Typeface font = Typeface.createFromAsset(assertMgr, "fonts/msyh.ttf");
		setTypeface(font);
	}
}
