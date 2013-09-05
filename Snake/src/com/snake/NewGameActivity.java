package com.snake;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.snake.*;

public class NewGameActivity extends Activity implements OnEndOfGameInterface   {
    private GameBasicSurfaceView mGameSurfaceView;
    private boolean mMusicOn;
	//private static MediaPlayer  mMediaPlayer;
	public static Activity mInstance;
	public static Context mContext;
	private Bundle saveData;
	private SharedPreferences sp;
	private boolean IsHome=false;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
     
    	getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,       
    	        WindowManager.LayoutParams. FLAG_FULLSCREEN);
   
		mInstance = this;
		mContext=this;
		
		sp = this.getSharedPreferences("settings", 0);
		int mode=sp.getInt("mode", MainActivity.Classic_Game);
        
		if (mode==MainActivity.Reverse_Game){
			//mGameSurfaceView = new ClassicGameSurfaceView(this);
			setContentView(R.layout.reverse_game);
			mGameSurfaceView = (ReverseGameSurfaceView) findViewById(R.id.reverse_game_surface_view);
			mGameSurfaceView.setOnEndOfGame(this);
			mGameSurfaceView.setMode(GameBasicSurfaceView.RUNNING);
		}
		else if(mode==MainActivity.Block_Game){
			//mGameSurfaceView = new BlockGameSurfaceView(this); 
			setContentView(R.layout.block_game);
			mGameSurfaceView = (BlockGameSurfaceView) findViewById(R.id.block_game_surface_view);
			mGameSurfaceView.setOnEndOfGame(this);
			mGameSurfaceView.setMode(GameBasicSurfaceView.RUNNING);
		}
		
		else{
			setContentView(R.layout.classic_game);
			mGameSurfaceView = (ClassicGameSurfaceView) findViewById(R.id.classic_game_surface_view);
			mGameSurfaceView.setOnEndOfGame(this);
			mGameSurfaceView.setMode(GameBasicSurfaceView.RUNNING);
		}
			
      
        //mMediaPlayer = MediaPlayer.create(NewGameActivity.mContext, R.raw.background_music); 
		//mMediaPlayer.setLooping(true);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
        Log.e("SuperSnake","OnCreate");
    }
    
    /*
    @Override
	protected void onDestroy() {
		if (mMediaPlayer.isPlaying()){
			mMediaPlayer.stop();
		}
		Log.e("SuperSnake","OnDestroy");
    	super.onDestroy();
	}*/

	//NOTICE:after entering HOME button：onSaveInstanceState => onPause => onStop 。 
    @Override
    protected void onPause() {
        super.onPause(); 
        if (mGameSurfaceView.mMode == GameBasicSurfaceView.RUNNING){  //set mode if necessary
        	Log.e("SuperSnake","OnPause:   Directly HOME");
        	Editor editor = sp.edit();
    	    editor.putBoolean("home", true);
    	    editor.commit();
    	    IsHome=true;
        	mGameSurfaceView.setMode(GameBasicSurfaceView.PAUSE);   
	        saveData = mGameSurfaceView.saveState();
			Intent intent =new Intent(NewGameActivity.this,PauseInGameActivity.class); 
			intent.putExtras(saveData);
			startActivityForResult(intent, 1);
        }
        Log.e("SuperSnake","OnPause");
    }
    
	@Override
	protected void onStart() {
		mMusicOn = sp.getBoolean("music", true);
			
        //播放音乐
        if (mMusicOn){
        	if (!MainActivity.mediaPlayer.isPlaying()){
        		MainActivity.mediaPlayer.start();
        	}
        }else{
        	if (MainActivity.mediaPlayer.isPlaying()){
        		MainActivity.mediaPlayer.pause();
    		}
        }
		super.onStart();
	}

	@Override
	protected void onStop() {
		 //pause music
        if (MainActivity.mediaPlayer.isPlaying()){
        	if(IsHome==true)
        		MainActivity.mediaPlayer.pause();
        	IsHome=false;
        }
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		onWindowFocusChanged(true);
		mMusicOn = sp.getBoolean("music", false);
		Log.e("SuperSnake","OnResume");
        if (mMusicOn){ //music control
        	MainActivity.mediaPlayer.start();
        }else{
        	if (MainActivity.mediaPlayer.isPlaying()){
        		MainActivity.mediaPlayer.pause();
    		}
        }
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//if can quit hte game , bring out the quit activity
		//when time is over , there will be about 2 seconds that user can not quit
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			if(mGameSurfaceView.canQuit()){
				Log.e("SuperSnake","canQuit:BACK");
				mGameSurfaceView.setMode(GameBasicSurfaceView.PAUSE);   
		        Bundle saveData = mGameSurfaceView.saveState();
				Intent intent =new Intent(NewGameActivity.this,PauseInGameActivity.class); 
				intent.putExtras(saveData);
				startActivityForResult(intent, 1);
				
			}
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(resultCode){
			case 1 :   //resume
				Bundle saveData = data.getExtras();
				mGameSurfaceView.restoreState(saveData);
				mGameSurfaceView.setMode(GameBasicSurfaceView.RUNNING);
				break ;
			case 2 :  //restart
				mGameSurfaceView.initNewGame();
				mGameSurfaceView.setMode(GameBasicSurfaceView.RUNNING);
				break ;
			case 3 :  //quit
				finish();
				break ;
			case 4 :  //HOME.
				saveData = data.getExtras();
				mGameSurfaceView.restoreState(saveData);
				mGameSurfaceView.draw();
				//saveData = mGameSurfaceView.saveState();
				Intent intent =new Intent(NewGameActivity.this,PauseInGameActivity.class); 
				intent.putExtras(saveData);
				startActivityForResult(intent, 1);
				break ;
			default :
				break;
		}
	}

	// static method for PauseInGame activity can control music 
	public static void musicOff() {
		if (MainActivity.mediaPlayer.isPlaying()){
			MainActivity.mediaPlayer.pause();
		}
	}
	 
	public static void musicOn() {
		if (!MainActivity.mediaPlayer.isPlaying()){
			MainActivity.mediaPlayer.start();
		}
	}
	
	@Override
	public void onEndOfGame() {		
		this.finish();
	}

}