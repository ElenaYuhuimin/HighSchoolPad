package com.emodou.login;

import com.emodou.domain.EmodouUserInfo;
import com.example.emodou.MainActivity;
import com.example.emodou.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

//logo界面，并判断登陆是否超过三天

public class EmodouLogoActivity extends Activity{
	
	private ImageView logo;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.logo);
		
		logo = (ImageView)findViewById(R.id.logo_logo);
		
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
		alphaAnimation.setDuration(2000);
		
		logo.startAnimation(alphaAnimation);
		
		alphaAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				DbUtils db3 = DbUtils.create(EmodouLogoActivity.this);

				EmodouUserInfo entity;
				try {
					entity = db3.findFirst(Selector.from(EmodouUserInfo.class));
					if (entity != null
							&& (entity.getDate() + 3 * 24 * 60 * 60 * 1000) > System
									.currentTimeMillis()) {
						entity.setDate(System.currentTimeMillis());
						db3.update(entity);
						Intent intent = new Intent();
						//这个参数传到MainActivity里面，然后再传到HomeActivity里面，则在首页要进行同步
						intent.putExtra("typeFirst", "login");
						//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.setClass(EmodouLogoActivity.this, MainActivity.class);
						startActivity(intent);
						finish();
					} else {

						Intent intent = new Intent();
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.setClass(EmodouLogoActivity.this,
								EmodouLoginAcvitity.class);
						intent.putExtra("typeFirst", "login");
						startActivity(intent);
						finish();

					}
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}
}
