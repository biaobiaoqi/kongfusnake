package com.snake;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;

public class PauseInGameActivity extends Activity{
	private LinearLayout linearLayout;
	private ImageButton ib_sound,ib_music,ib_quit,ib_resume,ib_restart;
	private SharedPreferences settings;
	private boolean sound,music;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        LayoutInit();
        ButtonInit();
    }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences sp = this.getSharedPreferences("settings", 0);
		Log.e("PauseInGameActivity","onResume  getBoolean(home)="+sp.getBoolean("home", true));
		if (sp.getBoolean("home", true)){
			Log.e("PauseInGameActivity","sp.home = true");
	    	Editor editor = sp.edit();
		    editor.putBoolean("home", false);
		    editor.commit();
		    Intent intent = getIntent();    
	        setResult(4,intent);  
	        finish();
	    }
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		//is it mean HOME?
		SharedPreferences sp = this.getSharedPreferences("settings", 0);
	    Editor editor = sp.edit();
	    editor.putBoolean("home", true);
	    editor.commit();
	    if (MainActivity.mediaPlayer.isPlaying()){
			MainActivity.mediaPlayer.pause();
		}
    	Log.e("PauseInGame", "onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}

	//Layout of the activity
	private void LayoutInit()
    {
    	linearLayout=new LinearLayout(this);
    	linearLayout.setOrientation(LinearLayout.VERTICAL);
    	
    	LinearLayout l1=new LinearLayout(this);
    	l1.setOrientation(LinearLayout.HORIZONTAL);
    	LinearLayout.LayoutParams lp_l1=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    	lp_l1.gravity=Gravity.RIGHT;  	
    	    
       	ib_sound=new ImageButton(this);
       	ib_sound.setBackgroundColor(Color.TRANSPARENT);
       	ib_sound.setScaleType(ScaleType.CENTER);
    	LinearLayout.LayoutParams lp_sound;
       	if(ScreenSize.width<320)
       		lp_sound=new LinearLayout.LayoutParams(ScreenSize.w125,ScreenSize.w125);
       	else
       		lp_sound=new LinearLayout.LayoutParams(ScreenSize.w12,ScreenSize.w12);
      	settings = getSharedPreferences("settings", PreferenceActivity.MODE_PRIVATE); //获取一个 SharedPreferences 对象
       	sound=settings.getBoolean("sound", false);
       	setSound(); 
       	
       	ib_music=new ImageButton(this);
    	ib_music.setBackgroundColor(Color.TRANSPARENT);      
       	ib_music.setScaleType(ScaleType.CENTER);
       	LinearLayout.LayoutParams lp_music;
       	if(ScreenSize.width<320)
       		lp_music=new LinearLayout.LayoutParams(ScreenSize.w125,ScreenSize.w125);
       	else
       		lp_music=new LinearLayout.LayoutParams(ScreenSize.w12,ScreenSize.w12);
     	lp_music.leftMargin=ScreenSize.h001;    
     	music=settings.getBoolean("music", false);
       	setMusic();
    	
       	l1.addView(ib_sound,lp_sound);
       	l1.addView(ib_music,lp_music);
       	
       	LinearLayout l2=new LinearLayout(this);
    	l2.setOrientation(LinearLayout.HORIZONTAL);
    	LinearLayout.LayoutParams lp_l2=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    	lp_l2.gravity=Gravity.CENTER;
    	lp_l2.topMargin=ScreenSize.w1;
         	
    	//Layout of button
    	ib_resume=new ImageButton(this);
    	ib_resume.setBackgroundColor(Color.TRANSPARENT);  
    	ib_resume.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_resume_button));
    	LinearLayout.LayoutParams lp_resume=new LinearLayout.LayoutParams(ScreenSize.w2,ScreenSize.w2);
   	
    	ib_restart=new ImageButton(this);
     	ib_restart.setBackgroundColor(Color.TRANSPARENT);
    	ib_restart.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_restart_button));
     	LinearLayout.LayoutParams lp_restart=new LinearLayout.LayoutParams(ScreenSize.w2,ScreenSize.w2);
    	lp_restart.leftMargin=ScreenSize.w1;
 
    	l2.addView(ib_resume,lp_resume);
    	l2.addView(ib_restart,lp_restart);
 
      	LinearLayout l3=new LinearLayout(this);
    	l3.setOrientation(LinearLayout.HORIZONTAL);
    	LinearLayout.LayoutParams lp_l3=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    	lp_l3.gravity=Gravity.CENTER;
    	lp_l3.topMargin=ScreenSize.w1;
         	
    	ib_quit=new ImageButton(this);
    	ib_quit.setBackgroundDrawable(getResources().getDrawable(R.drawable.pause_quit_button));
    	LinearLayout.LayoutParams lp_quit=new LinearLayout.LayoutParams(ScreenSize.w2,ScreenSize.w2);
    	
    	l3.addView(ib_quit,lp_quit);
    	
    	//Animation effect of the button
    	Animation animation_down=AnimationUtils.loadAnimation(this,R.anim.push_up_in);	
    	ib_resume.startAnimation(animation_down);
    	ib_restart.startAnimation(animation_down);
    	ib_quit.startAnimation(animation_down);
    	ib_sound.startAnimation(animation_down);
    	ib_music.startAnimation(animation_down);
    	
    	linearLayout.addView(l1,lp_l1);
    	linearLayout.addView(l2,lp_l2);
    	linearLayout.addView(l3,lp_l3);
    	setContentView(linearLayout);
    }
    
	//Set the image of button sound
    private void setSound()
    {
    	if(sound==true)
    		ib_sound.setImageDrawable(getResources().getDrawable(R.drawable.sound_yes));
    				
       	else
       		ib_sound.setImageDrawable(getResources().getDrawable(R.drawable.sound_no));
    }
    
    //Set the image of button music
    private void setMusic()
    {
    	if(music==true)
    		ib_music.setImageDrawable(getResources().getDrawable(R.drawable.music_yes));
    	
    	else
    		ib_music.setImageDrawable(getResources().getDrawable(R.drawable.music_no)); 
    }
    
    //Set the listener for button
    private void ButtonInit()
    {
    	ib_resume.setOnClickListener(lis_resume);
    	ib_restart.setOnClickListener(lis_restart);
    	ib_sound.setOnClickListener(lis_sound);
    	ib_music.setOnClickListener(lis_music);
    	ib_quit.setOnClickListener(lis_quit);
    }
    
    //Resume game
    private OnClickListener lis_resume=new OnClickListener()
    {
		@Override
		public void onClick(View v) 
		{	
			if(MainActivity.sound==true)
				MainActivity.mSoundPool.play(MainActivity.mSoundPoolMap.get(MainActivity.mSound), MainActivity.mCurrentVol, MainActivity.mCurrentVol, 1, 0, 1f);		
	
			Intent intent = getIntent();    
	        setResult(1,intent);  
	        finish();  
		}
    };
  
    //Restart game
    private OnClickListener lis_restart=new OnClickListener()
    {
		@Override
		public void onClick(View v) 
		{	
			if(MainActivity.sound==true)
				MainActivity.mSoundPool.play(MainActivity.mSoundPoolMap.get(MainActivity.mSound), MainActivity.mCurrentVol, MainActivity.mCurrentVol, 1, 0, 1f);		
	
	        setResult(2);  
	        finish();  		
		}
    };
    
    //Quit game
    private OnClickListener lis_quit=new OnClickListener()
    {
		@Override
		public void onClick(View v) 
		{	
			if(MainActivity.sound==true)
				MainActivity.mSoundPool.play(MainActivity.mSoundPoolMap.get(MainActivity.mSound), MainActivity.mCurrentVol, MainActivity.mCurrentVol, 1, 0, 1f);		
	
	        setResult(3);  
	        finish();  
		}
    };
    
    //Change the state of sound
    private OnClickListener lis_sound=new ImageButton.OnClickListener()
    {
  		@Override
		public void onClick(View v) 
  		{
  			if(MainActivity.sound==true)
				MainActivity.mSoundPool.play(MainActivity.mSoundPoolMap.get(MainActivity.mSound), MainActivity.mCurrentVol, MainActivity.mCurrentVol, 1, 0, 1f);		
	
			sound=settings.getBoolean("sound", false);
			sound=!sound;
			if(sound==true)
	    		ib_sound.setImageDrawable(getResources().getDrawable(R.drawable.sound_yes));
	    				
	       	else
	       		ib_sound.setImageDrawable(getResources().getDrawable(R.drawable.sound_no));
			
			Editor edit=settings.edit();
			edit.putBoolean("sound", sound);
			edit.commit();		 
		}
    };
    
    //Change the state of music
    private OnClickListener lis_music=new ImageButton.OnClickListener()
    {
    	@Override
    	public void onClick(View v)
    	{
    		if(MainActivity.sound==true)
				MainActivity.mSoundPool.play(MainActivity.mSoundPoolMap.get(MainActivity.mSound), MainActivity.mCurrentVol, MainActivity.mCurrentVol, 1, 0, 1f);		
	
    		music=settings.getBoolean("music", false);
    		music=!music;//navigation
    		if(music==true)
    		{
    			ib_music.setImageDrawable(getResources().getDrawable(R.drawable.music_yes));  			
    			NewGameActivity.musicOn();
    		}
    		else
    		{
    			ib_music.setImageDrawable(getResources().getDrawable(R.drawable.music_no));    			
    			NewGameActivity.musicOff();
    		}
    		
    		Editor edit=settings.edit();
			edit.putBoolean("music", music);
			edit.commit();	
    	}
    };
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
        	Log.e("PauseInGame","BACK");
        	Intent intent = getIntent();    
	        setResult(1,intent);  
	        finish();  
	        return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}