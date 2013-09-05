package com.snake;

import java.util.Timer;
import java.util.TimerTask;
import com.snake.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class ResultActivity extends Activity {
	private ImageButton ib_tryagain,ib_stopgame;
	private LinearLayout linearLayout;
	private SharedPreferences settings;
	private int mode;
	private TextView iv_juanzhou;
	private int userScore;
	private TimerTask mTimerTask;
	private Handler mHandler;
	private Animation btn_animation;
	private String strResult="More Practice!";
	private boolean IsHome=true;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);	
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		LayoutInit();
					
	}
	
	//Layout of the activity
	private void LayoutInit()
	{
		linearLayout=new LinearLayout(this);	
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setGravity(Gravity.BOTTOM);
		linearLayout.setBackgroundResource(R.drawable.main_background);
		
		userScore = this.getIntent().getIntExtra("score",10);
		settings = getSharedPreferences("settings", 0);
		mode=settings.getInt("mode", MainActivity.Classic_Game);
		
		int highestScore;
		if(mode==MainActivity.Classic_Game)
		{
			highestScore = settings.getInt("record_classic", 10);
		
			//According to the user's score, set the different background
			if(highestScore<userScore)
			{
				Editor editor = settings.edit();
				editor.putInt("record_classic", userScore);
				editor.commit();
				strResult="New Record!";
			}
		}
	
		else if(mode==MainActivity.Block_Game)
		{
			highestScore=settings.getInt("record_block", 10);
			//According to the user's score, set the different background
			if(highestScore<userScore)
			{
				Editor editor = settings.edit();
				editor.putInt("record_block", userScore);
				editor.commit();
				strResult="New Record!";
			}
		}
		
		else
		{
			highestScore=settings.getInt("record_reverse", 10);
			//According to the user's score, set the different background
			if(highestScore<userScore)
			{
				Editor editor = settings.edit();
				editor.putInt("record_reverse", userScore);
				editor.commit();
				strResult="New Record!";
			}
		}
		
		Typeface mFace = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/font_result.ttf");
		iv_juanzhou=new TextView(this);
		LinearLayout.LayoutParams lp_juanzhou=new LinearLayout.LayoutParams(ScreenSize.width,ScreenSize.h3);
		iv_juanzhou.setBackgroundResource(R.drawable.juanzhou_animation);
		iv_juanzhou.setGravity(Gravity.CENTER);
		iv_juanzhou.setTypeface(mFace);
		if(ScreenSize.width>320)
			iv_juanzhou.setTextSize(ScreenSize.w05);
		else
			iv_juanzhou.setTextSize(ScreenSize.w1);
		iv_juanzhou.setTextColor(Color.BLACK);
			
		LinearLayout l1=new LinearLayout(this);
		l1.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams lp_l1=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lp_l1.gravity=Gravity.CENTER;
		lp_l1.bottomMargin=ScreenSize.h1;
		lp_l1.topMargin=ScreenSize.h2;
		
		ib_tryagain=new ImageButton(this);
		ib_tryagain.setBackgroundDrawable(getResources().getDrawable(R.drawable.result_try_again_button));
		ib_tryagain.setScaleType(ScaleType.FIT_CENTER);
		LinearLayout.LayoutParams lp_tryagain=new LinearLayout.LayoutParams(ScreenSize.h2,ScreenSize.h2);
		
		ib_stopgame=new ImageButton(this);
		ib_stopgame.setBackgroundDrawable(getResources().getDrawable(R.drawable.result_quit_button));
		ib_stopgame.setScaleType(ScaleType.FIT_CENTER);
		LinearLayout.LayoutParams lp_stopgame=new LinearLayout.LayoutParams(ScreenSize.h2,ScreenSize.h2);
		lp_stopgame.leftMargin=ScreenSize.w1;
	
		l1.addView(ib_tryagain,lp_tryagain);
		l1.addView(ib_stopgame,lp_stopgame);
		
		btn_animation=AnimationUtils.loadAnimation(this,R.anim.fade);

		linearLayout.addView(iv_juanzhou,lp_juanzhou);
		linearLayout.addView(l1,lp_l1);
			
		ib_tryagain.setOnClickListener(lis_tryagain);//Set the listener for button
		ib_stopgame.setOnClickListener(lis_stopgame);
		
		setContentView(linearLayout);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {		
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
    		return false;
		}
		return super.onKeyDown(keyCode, event);
	}
    
	//Click try again button, restart the game
	private OnClickListener lis_tryagain=new ImageButton.OnClickListener()
    {
  		@Override
		public void onClick(View v) 
  		{
  			if(MainActivity.sound==true)
				MainActivity.mSoundPool.play(MainActivity.mSoundPoolMap.get(MainActivity.mSound), MainActivity.mCurrentVol, MainActivity.mCurrentVol, 1, 0, 1f);		
	
  			IsHome=false;
  			ib_tryagain.startAnimation(btn_animation);
   			Intent intent =new Intent(ResultActivity.this,NewGameActivity.class);
  			startActivity(intent); 
			ResultActivity.this.finish();
		}
    };
    
    //Click stop button, quit the game
    private OnClickListener lis_stopgame=new ImageButton.OnClickListener()
    {
  		@Override
		public void onClick(View v) 
  		{	
  			if(MainActivity.sound==true)
				MainActivity.mSoundPool.play(MainActivity.mSoundPoolMap.get(MainActivity.mSound), MainActivity.mCurrentVol, MainActivity.mCurrentVol, 1, 0, 1f);		
  			
  			IsHome=false;
  			ib_stopgame.startAnimation(btn_animation);
  			ResultActivity.this.finish();
		}
    }; 	
    
    @Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		AnimationDrawable anim = null;
        Object ob = iv_juanzhou.getBackground();
        anim = (AnimationDrawable) ob;
      
		if(hasFocus==true)	
	        anim.start();
		else
			anim.stop();
		
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if (MainActivity.mediaPlayer.isPlaying()){
        	if(IsHome==true)
        		MainActivity.mediaPlayer.pause();
        	IsHome=false;
        }
		
		super.onStop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(MainActivity.music==true)
    	{
    		//Log.e("SnakeActivity","setMusicPicOn");
    		if(!MainActivity.mediaPlayer.isPlaying()) ;
    			 MainActivity.mediaPlayer.start();	 
    	}
		
		mTimerTask = new TimerTask() 
		{
			@Override
			public void run() 
			{
				Message message = new Message();
				message.what = 1;

				mHandler.sendMessage(message);
			}
		};
	
		mHandler = new Handler() 
		{
			public void handleMessage(Message msg) 
			{
				switch (msg.what) 
				{
					case 1:
					// 关闭定时器
						iv_juanzhou.setText(strResult+"\n"+"Score:"+String.valueOf(userScore));
						Log.i("result", "test");
						mTimerTask.cancel();
						break;
				}
			}
		};
		
		Timer timer=new Timer();
		timer.schedule(mTimerTask,1000,1000);  //参数分别是delay（多长时间后执行），duration（执行间隔）
		
		super.onResume();
	}
}
