package com.snake;

import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;

public class MainActivity extends Activity implements QuitInterface,NewGameInterface{
	private static ImageButton ib_startgame;
	private static ImageButton ib_gameinstr;
	private static ImageButton ib_exit;
	private ImageButton ib_sound, ib_music, ib_infor;
	private LinearLayout linearLayout;
	private SharedPreferences settings;
	private GuideDialog guideDialog;
	private QuitDialog quitDialog;
	private ModeDialog modeDialog;
	private InforDialog inforDialog;
	private ShowSnakeSurfaceView mShowSnakeSurfaceView;
	private AudioManager mAudioManager; 
	public static HashMap<Integer, Integer> mSoundPoolMap;
	public static SoundPool mSoundPool; 
	public static final int mSound=1;
	public static int mCurrentVol;
	private Animation btn_animation3;
	private static Animation btn_animation4;
	public static boolean sound;
	public static boolean music; 
	public static MediaPlayer mediaPlayer;
	public static int Classic_Game=0;
	public static int Block_Game=1;
	public static int Reverse_Game=2;
	public int tempcount=0;
	public static boolean IsGame=false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        InitSoundAndMusic(); //Initialization for music and sound
	    LayoutInit(); //Layout of the Activity
        quitDialog=new QuitDialog(this);
        quitDialog.setExit(this);
        guideDialog=new GuideDialog(this);
        modeDialog=new ModeDialog(this);
        modeDialog.setNewGame(this);
        inforDialog=new InforDialog(this);
       
        ButtonInit(); //Set the click listener for the button
        initData(); //Set the initial data when the application is run for the first time
    }

    private void initData()
    {
    	Editor editor = settings.edit();
    	int tmpInt = settings.getInt("record_classic", -1);
    	if(tmpInt == -1){
    		editor.putInt("record_classic", 10);
    		editor.putInt("record_block", 10);
    		editor.putBoolean("music", true);
    		editor.putBoolean("sound", true);
    		editor.putBoolean("home", false); //for HOME problem
    		editor.putInt("type", 0);
    		editor.putInt("mode", 0);
    		editor.putBoolean("biansu", true);
    		editor.putInt("speed", 3);
    		editor.commit();
    		//Log.e("SnakeActivity","initData");
    	}
    }
    
    private void LayoutInit()
    {
    	//Get the width and height of the screen
    	DisplayMetrics displayMetrics = new DisplayMetrics(); 
    	getWindowManager().getDefaultDisplay().getMetrics(displayMetrics); 
    	ScreenSize.Init(displayMetrics.widthPixels,displayMetrics.heightPixels);
    	
    	linearLayout=new LinearLayout(this);
    	linearLayout.setOrientation(LinearLayout.VERTICAL);
    	linearLayout.setBackgroundResource(R.drawable.main_background);
    	
    	LinearLayout l1=new LinearLayout(this);
    	l1.setOrientation(LinearLayout.HORIZONTAL);
    	LinearLayout.LayoutParams lp_l1=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,ScreenSize.h1);
    	lp_l1.gravity=Gravity.RIGHT;
    	l1.setGravity(Gravity.BOTTOM);
    	
      	ib_sound=new ImageButton(this); 	 
       	LinearLayout.LayoutParams lp_sound;
       	if(ScreenSize.width<320)
       		lp_sound=new LinearLayout.LayoutParams(ScreenSize.w125,ScreenSize.w125);
       	else
       		lp_sound=new LinearLayout.LayoutParams(ScreenSize.w12,ScreenSize.w12);
       	ib_sound.setBackgroundColor(Color.TRANSPARENT);
       	ib_sound.setScaleType(ScaleType.CENTER);
       	settings = getSharedPreferences("settings", PreferenceActivity.MODE_PRIVATE); 
      
       	ib_music=new ImageButton(this);
       	LinearLayout.LayoutParams lp_music;
       	if(ScreenSize.width<320)
       		lp_music=new LinearLayout.LayoutParams(ScreenSize.w125,ScreenSize.w125);
       	else
       		lp_music=new LinearLayout.LayoutParams(ScreenSize.w12,ScreenSize.w12);
       	ib_music.setBackgroundColor(Color.TRANSPARENT);
        ib_music.setScaleType(ScaleType.CENTER);
       	lp_music.leftMargin=ScreenSize.h001;
       	//lp_music.rightMargin=ScreenSize.h025;
       	ib_music.setBackgroundColor(Color.TRANSPARENT);

       	l1.addView(ib_sound,lp_sound);
       	l1.addView(ib_music,lp_music);
       	
     	mShowSnakeSurfaceView=new ShowSnakeSurfaceView(getBaseContext());
       	LinearLayout.LayoutParams lp_cover=new LinearLayout.LayoutParams(ScreenSize.width,ScreenSize.h4);
       	lp_cover.gravity=Gravity.CENTER;
       	
     	LinearLayout l2=new LinearLayout(this);
    	l2.setOrientation(LinearLayout.VERTICAL);
    	LinearLayout.LayoutParams lp_l2=new LinearLayout.LayoutParams(ScreenSize.width,(ScreenSize.h375));
    	lp_l2.gravity=Gravity.CENTER;
    	lp_l2.topMargin=ScreenSize.h05;
    	
    	LinearLayout l3=new LinearLayout(this);
    	l3.setOrientation(LinearLayout.HORIZONTAL);
    	LinearLayout.LayoutParams lp_l3=new LinearLayout.LayoutParams(ScreenSize.width,ScreenSize.h2);
     	l3.setGravity(Gravity.CENTER);
       	
    	ib_startgame=new ImageButton(this);
    	ib_startgame.setImageResource(R.drawable.main_new_game_button);
    	ib_startgame.setBackgroundColor(Color.TRANSPARENT);
    	LinearLayout.LayoutParams lp_startgame=new LinearLayout.LayoutParams(ScreenSize.h2,ScreenSize.h2);
    	lp_startgame.gravity=Gravity.CENTER;
    	ib_startgame.setScaleType(ScaleType.FIT_CENTER);
    	
    	l3.addView(ib_startgame,lp_startgame);
    	
    	LinearLayout l4=new LinearLayout(this);
    	l4.setOrientation(LinearLayout.HORIZONTAL);
    	LinearLayout.LayoutParams lp_l4=new LinearLayout.LayoutParams(ScreenSize.width,ScreenSize.h15);
    	l4.setGravity(Gravity.CENTER);
    	
    	ib_exit=new ImageButton(this);
    	ib_exit.setImageResource(R.drawable.main_quit_button);
    	ib_exit.setBackgroundColor(Color.TRANSPARENT);
    	LinearLayout.LayoutParams lp_exit=new LinearLayout.LayoutParams(ScreenSize.h15,ScreenSize.h15);
    	lp_exit.leftMargin=ScreenSize.h05;
    	ib_exit.setScaleType(ScaleType.FIT_CENTER);
    	
    	ib_gameinstr=new ImageButton(this);
    	ib_gameinstr.setImageResource(R.drawable.main_guide_button);
    	ib_gameinstr.setBackgroundColor(Color.TRANSPARENT);
    	LinearLayout.LayoutParams lp_gameinstr=new LinearLayout.LayoutParams(ScreenSize.h15,ScreenSize.h15);//1
    	ib_gameinstr.setScaleType(ScaleType.FIT_CENTER);
    
    	l4.addView(ib_gameinstr,lp_gameinstr);    	
    	l4.addView(ib_exit,lp_exit);
    	
    	l2.addView(l3,lp_l3);
    	l2.addView(l4,lp_l4);
    	    	
     	LinearLayout l5=new LinearLayout(this);
     	l5.setOrientation(LinearLayout.HORIZONTAL);
     	LinearLayout.LayoutParams lp_l5=new LinearLayout.LayoutParams(ScreenSize.width,(ScreenSize.height-ScreenSize.h4-ScreenSize.h375-ScreenSize.h05-ScreenSize.h1));
     	
     	ib_infor=new ImageButton(this);
     	ib_infor.setImageResource(R.drawable.main_infor_button);
     	ib_infor.setBackgroundColor(Color.TRANSPARENT);
     	LinearLayout.LayoutParams lp_infor=new LinearLayout.LayoutParams(ScreenSize.w075,ScreenSize.w075);
     	ib_infor.setScaleType(ScaleType.CENTER_CROP);
     	lp_infor.leftMargin=ScreenSize.w001;
     	lp_infor.gravity=Gravity.BOTTOM;
     	
     	l5.addView(ib_infor,lp_infor);
  
     	btn_animation3=AnimationUtils.loadAnimation(this,R.anim.fade);
    	btn_animation4 = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
    	btn_animation4.setDuration(1250); // duration - half a second
    	btn_animation4.setInterpolator(new LinearInterpolator()); // do not alter animation rate
    	btn_animation4.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
    	btn_animation4.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
   
    	startAnimation();
    	linearLayout.addView(l1,lp_l1);
    	linearLayout.addView(mShowSnakeSurfaceView,lp_cover);
    	linearLayout.addView(l2,lp_l2);
    	linearLayout.addView(l5,lp_l5);
   	
    	setContentView(linearLayout);
    }
    
    //Set the image of button sound
    private void setSound()
    {
    	if(sound==true){
    		//Log.e("SnakeActivity","setSoundPicOn");
    		ib_sound.setImageDrawable(getResources().getDrawable(R.drawable.sound_yes));
    	}
       	else
       		ib_sound.setImageDrawable(getResources().getDrawable(R.drawable.sound_no));  	   	
    }
    
    private void InitSoundAndMusic()
    {
    	//Initialization for SoundPool
       	mAudioManager = (AudioManager) MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
       	mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
       	mSoundPoolMap = new HashMap<Integer, Integer>();

       	mSoundPoolMap.put(mSound, mSoundPool.load(this,R.raw.button_sound, 1));  	
       	MainActivity.this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
       	mCurrentVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
       	
       	//Initialization for MediaPlayer
       	mediaPlayer=MediaPlayer.create(getBaseContext(), R.raw.background_music);
		mediaPlayer.setLooping(true);
    }
    
    //Set the image of button music
    private void setMusic()
    {		
    	if(music==true)
    	{
    		//Log.e("SnakeActivity","setMusicPicOn");
    		ib_music.setImageDrawable(getResources().getDrawable(R.drawable.music_yes));
    		if(!mediaPlayer.isPlaying()) ;
    			 mediaPlayer.start();
    	}
    	
    	else
    		ib_music.setImageDrawable(getResources().getDrawable(R.drawable.music_no)); 
    }
    
    private void ButtonInit()
    {
    	ib_startgame.setOnClickListener(lis_startgame);
    	ib_gameinstr.setOnClickListener(lis_gameinstr);
     	ib_exit.setOnClickListener(lis_exit);
    	ib_sound.setOnClickListener(lis_sound);
    	ib_music.setOnClickListener(lis_music);
    	ib_infor.setOnClickListener(lis_infor);
    }
      
    //Click listener for new game
    private OnClickListener lis_startgame=new OnClickListener()
    {
		@Override
		public void onClick(View v) 
		{		
			//Sound effect
			if(sound==true)
				mSoundPool.play(mSoundPoolMap.get(mSound), mCurrentVol, mCurrentVol, 1, 0, 1f);		
			//IsGame=true;
			
			ib_startgame.clearAnimation();
			
			modeDialog.show();
			ib_startgame.setVisibility(View.INVISIBLE);
			ib_exit.setVisibility(View.INVISIBLE);
			ib_gameinstr.setVisibility(View.INVISIBLE);
		}
    };
    
    //Click listener for guide of the game 
    private OnClickListener lis_gameinstr=new OnClickListener()
    { 
		@Override
		public void onClick(View v) 
		{
			if(sound==true)
				mSoundPool.play(mSoundPoolMap.get(mSound), mCurrentVol, mCurrentVol, 1, 0, 1f);
			
			ib_gameinstr.startAnimation(btn_animation3);
			ib_startgame.clearAnimation();

			guideDialog.show();
		}
    };
    
    //Click listener for about
    private OnClickListener lis_infor=new OnClickListener()
    {
		@Override
		public void onClick(View v) 
		{
			if(sound==true)
				mSoundPool.play(mSoundPoolMap.get(mSound), mCurrentVol, mCurrentVol, 1, 0, 1f);
			
			ib_startgame.clearAnimation();
			inforDialog.show();
		}   	
    };
    
    //Click listener for quit
    private OnClickListener lis_exit=new OnClickListener()
    {   
		@Override
		public void onClick(View v) 
		{	
			if(sound==true)
				mSoundPool.play(mSoundPoolMap.get(mSound), mCurrentVol, mCurrentVol, 1, 0, 1f);
			
			ib_exit.startAnimation(btn_animation3);
			ib_startgame.clearAnimation();
			
			quitDialog.show();
		}
    };
    
    //Click listener for sound
    private OnClickListener lis_sound=new ImageButton.OnClickListener()
    {
  		@Override
		public void onClick(View v) 
  		{	
  			if(MainActivity.sound==true)
				MainActivity.mSoundPool.play(MainActivity.mSoundPoolMap.get(MainActivity.mSound), MainActivity.mCurrentVol, MainActivity.mCurrentVol, 1, 0, 1f);		
	
			sound=settings.getBoolean("sound", false);
			sound=!sound;
			if(sound==true)//Enable sound effect
	    		ib_sound.setImageDrawable(getResources().getDrawable(R.drawable.sound_yes));
	    				
	       	else//Disable sound effect
	       		ib_sound.setImageDrawable(getResources().getDrawable(R.drawable.sound_no));
			
			//Record by SharedPreferences
			Editor edit=settings.edit();
			edit.putBoolean("sound", sound);
			edit.commit();		 
		}
    };
    
    //Click listener for music
    private OnClickListener lis_music=new ImageButton.OnClickListener()
    {
    	@Override
    	public void onClick(View v)
    	{
    		if(MainActivity.sound==true)
				MainActivity.mSoundPool.play(MainActivity.mSoundPoolMap.get(MainActivity.mSound), MainActivity.mCurrentVol, MainActivity.mCurrentVol, 1, 0, 1f);		
	
    		music=settings.getBoolean("music", false);
    		music=!music;
    		if(music==true)//Play music
    		{
    			ib_music.setImageDrawable(getResources().getDrawable(R.drawable.music_yes));
    			if(!mediaPlayer.isPlaying())
    				 mediaPlayer.start();
    		}

    		else//Pause
    		{
    			ib_music.setImageDrawable(getResources().getDrawable(R.drawable.music_no));
    			if(mediaPlayer.isPlaying())
    				 mediaPlayer.pause();
    		}
 		
    		Editor edit=settings.edit();
			edit.putBoolean("music", music);
			edit.commit();	
    	}
    };
            
    //If user touches the Back Key, Quit Dialog will show to confirm
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
        	quitDialog.show();
        	return false ;
		}
		return super.onKeyDown(keyCode, event);	
	}
    
    //Stop music and quit
	@Override
    public void exit()
    {
    	if(mediaPlayer.isPlaying())
 		   mediaPlayer.stop();
    	MainActivity.this.finish();
    }
    
	@Override
    protected void onResume()
    {
    	super.onResume();
    	onWindowFocusChanged(true);
    	
    	settings=getSharedPreferences("settings", 0);
		music=settings.getBoolean("music", false);//Update after resume
		sound=settings.getBoolean("sound",false);
		//Log.e("SnakeActivity","music = "+music);
		setSound();
    	setMusic();
    	startAnimation();
    	if(modeDialog.isShowing())
    		modeDialog.dismiss();
    	if(quitDialog.isShowing())
    		ib_startgame.clearAnimation();
    	if(guideDialog.isShowing())
    		ib_startgame.clearAnimation();
    	if(inforDialog.isShowing())
    		ib_startgame.clearAnimation();
    }

	@Override
	protected void onStop() 
	{
		if(mediaPlayer.isPlaying())
			if(IsGame==false)
				mediaPlayer.pause();
		
		IsGame=false;
		super.onPause();
		super.onStop();
	}
	
	public void newGame()
	{
		IsGame=true;
		Intent intent =new Intent(MainActivity.this,NewGameActivity.class); //应锟矫匡拷始
		startActivity(intent);	
	}
	
	public static void startAnimation() {
		// TODO Auto-generated method stub
    	ib_startgame.setVisibility(View.VISIBLE);
		ib_exit.setVisibility(View.VISIBLE);
		ib_gameinstr.setVisibility(View.VISIBLE);
		ib_startgame.startAnimation(btn_animation4);
	}
}