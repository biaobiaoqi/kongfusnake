package com.snake;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ModeDialog extends Dialog{
	
	private LinearLayout linearLayout;
	private ImageView iv_classic,iv_block,iv_reverse,iv_yunsu,iv_biansu;
	private NewGameInterface iNewGame;
	private SeekBar sb_speed;
	private TextView tv_speed;
	private SharedPreferences settings;
	private Animation animation_fade;
	
	public ModeDialog(Context context) {
		super(context,R.style.translucent);
		// TODO Auto-generated constructor stub	
		
		Window mwindow=getWindow(); 
		WindowManager.LayoutParams lp=mwindow.getAttributes(); 
		lp.x=0; 
		lp.y=ScreenSize.h1*(-1); 
		
		mwindow.setAttributes(lp);
	}
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		 
		LayoutInit();
	}
	 
	//Layout of the Guide Dialog
	private void LayoutInit()
	{
		linearLayout=new LinearLayout(getContext());
    	linearLayout.setOrientation(LinearLayout.VERTICAL);
    	linearLayout.setGravity(Gravity.CENTER);
    
    	LinearLayout l1=new LinearLayout(getContext());
    	l1.setOrientation(LinearLayout.HORIZONTAL);
    	l1.setGravity(Gravity.CENTER);
    	LinearLayout.LayoutParams lp_l1=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    	
    	iv_classic=new ImageView(getContext());
    	LinearLayout.LayoutParams lp_iv_classic=new LinearLayout.LayoutParams(ScreenSize.w3,ScreenSize.w3);
    	iv_classic.setBackgroundResource(R.drawable.mode_paradise_button);
    	iv_classic.setScaleType(ScaleType.FIT_CENTER);
    	
    	iv_block=new ImageView(getContext());
    	LinearLayout.LayoutParams lp_iv_block=new LinearLayout.LayoutParams(ScreenSize.w3,ScreenSize.w3);
    	iv_block.setBackgroundResource(R.drawable.mode_trap_button);
    	iv_block.setScaleType(ScaleType.FIT_CENTER);
    	
    	iv_reverse=new ImageView(getContext());
    	LinearLayout.LayoutParams lp_iv_reverse=new LinearLayout.LayoutParams(ScreenSize.w3,ScreenSize.w3);
    	iv_reverse.setBackgroundResource(R.drawable.mode_hell_button);
    	iv_reverse.setScaleType(ScaleType.FIT_CENTER);
    	lp_iv_reverse.leftMargin=ScreenSize.w1;
    	
    	animation_fade=AnimationUtils.loadAnimation(getContext(),R.anim.fade);
	      	
    	iv_classic.setOnClickListener(new ImageView.OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				if(MainActivity.sound==true)
					MainActivity.mSoundPool.play(MainActivity.mSoundPoolMap.get(MainActivity.mSound), MainActivity.mCurrentVol, MainActivity.mCurrentVol, 1, 0, 1f);		
		
				SharedPreferences settings=getContext().getSharedPreferences("settings", 0);
				Editor editor=settings.edit();
				editor.putInt("mode", MainActivity.Classic_Game);
				editor.commit();
				
				iv_classic.startAnimation(animation_fade);
    	    	iv_block.startAnimation(animation_fade);
    	    	iv_reverse.startAnimation(animation_fade);		
				iNewGame.newGame();
			}
        });
    	
    	iv_block.setOnClickListener(new ImageView.OnClickListener(){
    		public void onClick(View v){
    			if(MainActivity.sound==true)
    				MainActivity.mSoundPool.play(MainActivity.mSoundPoolMap.get(MainActivity.mSound), MainActivity.mCurrentVol, MainActivity.mCurrentVol, 1, 0, 1f);		
    	
    			SharedPreferences settings=getContext().getSharedPreferences("settings", 0);
    			Editor editor=settings.edit();
    			editor.putInt("mode", MainActivity.Block_Game);
    			editor.commit();
    			
     		  	iv_block.startAnimation(animation_fade);
    		  	iv_classic.startAnimation(animation_fade);
    		  	iv_reverse.startAnimation(animation_fade);
    		  	iNewGame.newGame();
    		}
    	});
    	
    	iv_reverse.setOnClickListener(new ImageView.OnClickListener(){
    		public void onClick(View v){
    			if(MainActivity.sound==true)
    				MainActivity.mSoundPool.play(MainActivity.mSoundPoolMap.get(MainActivity.mSound), MainActivity.mCurrentVol, MainActivity.mCurrentVol, 1, 0, 1f);		
    	
    			SharedPreferences settings=getContext().getSharedPreferences("settings", 0);
    			Editor editor=settings.edit();
    			editor.putInt("mode", MainActivity.Reverse_Game);
    			editor.commit();
    			
    			iv_block.startAnimation(animation_fade);
    		  	iv_classic.startAnimation(animation_fade);
    		  	iv_reverse.startAnimation(animation_fade);
    	    	iNewGame.newGame();
    		}
    	});
    	
    	LinearLayout l4=new LinearLayout(getContext());
    	l4.setOrientation(LinearLayout.HORIZONTAL);
    	LinearLayout.LayoutParams lp_l4=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    	lp_l4.topMargin=ScreenSize.h025;
    	lp_l4.gravity=Gravity.CENTER;
    	
    	LinearLayout l5=new LinearLayout(getContext());
    	l5.setOrientation(LinearLayout.VERTICAL);
    	LinearLayout.LayoutParams lp_l5=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
 
    	iv_yunsu=new ImageView(getContext());
    	LinearLayout.LayoutParams lp_yunsu=new LinearLayout.LayoutParams(ScreenSize.w3,ScreenSize.h075);
    	iv_yunsu.setScaleType(ScaleType.FIT_CENTER);
   	
    	iv_biansu=new ImageView(getContext());
    	LinearLayout.LayoutParams lp_biansu=new LinearLayout.LayoutParams(ScreenSize.w3,ScreenSize.h075);
    	iv_biansu.setScaleType(ScaleType.FIT_CENTER);
   
    	l5.addView(iv_biansu,lp_biansu);
    	l5.addView(iv_yunsu,lp_yunsu);
    		    	
    	LinearLayout l6=new LinearLayout(getContext());
    	l6.setOrientation(LinearLayout.VERTICAL);
    	LinearLayout.LayoutParams lp_l6=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,(ScreenSize.h1+ScreenSize.h075));
  
    	sb_speed=new SeekBar(getContext());
    	sb_speed.setMax(4);
    	sb_speed.setBackgroundColor(Color.TRANSPARENT);
    	sb_speed.setIndeterminate(false);
    	sb_speed.setProgressDrawable(getContext().getResources().getDrawable(R.drawable.seekbar_img));	
    	sb_speed.setThumb(getContext().getResources().getDrawable(R.drawable.thumb));   	
    	LinearLayout.LayoutParams lp_sb_speed=new LinearLayout.LayoutParams(ScreenSize.w6,ScreenSize.h1);
    	sb_speed.setThumbOffset(0);
      	
    	Typeface mFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/font_speed.TTF");   	
    	tv_speed=new TextView(getContext());
    	tv_speed.setTextColor(Color.BLACK);
    	tv_speed.setTypeface(mFace);
    	if(ScreenSize.width>320)
    		tv_speed.setTextSize(ScreenSize.w025);
    	else 
    		tv_speed.setTextSize(ScreenSize.w04);
    	LinearLayout.LayoutParams lp_tv_speed=new LinearLayout.LayoutParams(ScreenSize.w6,ScreenSize.h075);
    	tv_speed.setGravity(Gravity.CENTER);
    	
    	l6.addView(sb_speed,lp_sb_speed);
    	l6.addView(tv_speed,lp_tv_speed);
    	
    	l4.addView(l5,lp_l5);
    	l4.addView(l6,lp_l6);
    	
    	settings=getContext().getSharedPreferences("settings", PreferenceActivity.MODE_PRIVATE);
    	boolean biansu=settings.getBoolean("biansu", true);
    	SetSpeedTypeImage(biansu);
    	int speed=settings.getInt("speed", 3);
    	tv_speed.setText("Current speed is:"+String.valueOf(speed));
    	sb_speed.setProgress(speed-1);
    	
    	iv_yunsu.setOnClickListener(new ImageView.OnClickListener(){
    		public void onClick(View v){
    			if(MainActivity.sound==true)
					MainActivity.mSoundPool.play(MainActivity.mSoundPoolMap.get(MainActivity.mSound), MainActivity.mCurrentVol, MainActivity.mCurrentVol, 1, 0, 1f);		
		
     			Editor edit=(Editor)settings.edit();
    			edit.putBoolean("biansu",false);
    			edit.commit();
    			SetSpeedTypeImage(false);
    		}
    	});

    	iv_biansu.setOnClickListener(new ImageView.OnClickListener(){
    		public void onClick(View v){
    			if(MainActivity.sound==true)
					MainActivity.mSoundPool.play(MainActivity.mSoundPoolMap.get(MainActivity.mSound), MainActivity.mCurrentVol, MainActivity.mCurrentVol, 1, 0, 1f);		
		
     			Editor edit=(Editor)settings.edit();
    			edit.putBoolean("biansu", true);
    			edit.commit();
    			SetSpeedTypeImage(true);
    		}
    	});
    	
    	sb_speed.setOnSeekBarChangeListener(lis_speed);
    	
    	l1.addView(iv_block,lp_iv_block);
    	l1.addView(iv_reverse,lp_iv_reverse);
    	linearLayout.addView(l1,lp_l1);
    	linearLayout.addView(iv_classic,lp_iv_classic);
    	linearLayout.addView(l4,lp_l4);
    	
    	setContentView(linearLayout);
	}
	
	private void SetSpeedTypeImage(boolean biansu)
    {
    	if(biansu==true)
    	{
    		iv_biansu.setImageResource(R.drawable.mode_variable_sel);
    		iv_yunsu.setImageResource(R.drawable.mode_uniform);
			iv_biansu.setClickable(false);
			iv_yunsu.setClickable(true);
    	}
    	
    	else
    	{
    		iv_yunsu.setImageResource(R.drawable.mode_uniform_sel);
    		iv_biansu.setImageResource(R.drawable.mode_variable);
			iv_yunsu.setClickable(false);
			iv_biansu.setClickable(true);
    	}
    }
	
	private OnSeekBarChangeListener lis_speed=new SeekBar.OnSeekBarChangeListener() 
	{		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
			Editor edit=settings.edit();
			edit.putInt("speed", (seekBar.getProgress()+1));
			edit.commit();
		}
			
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}
			
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
			// TODO Auto-generated method stub
			tv_speed.setText("Current speed is: "+String.valueOf(progress+1));
		}
	};
	    
	public void setNewGame(NewGameInterface newGame)
	{
		this.iNewGame=newGame;
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		MainActivity.startAnimation();
		super.dismiss();
	}	
}
