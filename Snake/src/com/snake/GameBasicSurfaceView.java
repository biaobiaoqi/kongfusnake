package com.snake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.snake.R;
import com.snake.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public abstract class GameBasicSurfaceView extends SurfaceView implements Callback, Runnable {
	protected long timeRecord = 0;  // for test
	
	protected boolean mSynLock = true ;
	protected Context mContext;
	protected OnEndOfGameInterface mOnEndOfGame ; //callback interface
    protected static final String TAG = "SnakeSurfaceView";
    
	protected SurfaceHolder mSurfaceHolder;    
    protected Thread mThread; 
    protected boolean mIsRunning ; //control the thread
    protected SensorManager mSensorManager; //sensor manager for gravity 
    
    //Music and Sound 
	protected boolean mSoundOn ;
	protected boolean   mEndSoundFlag = true;
	protected boolean[] mCountDownFlag = new boolean[] {true ,true ,true,true ,true};
	protected int mCurrentVol ;
	protected AudioManager mAudioManager; 
    protected HashMap<Integer, Integer> mSoundPoolMap;
    protected SoundPool mSoundPool; 
    protected static final int CountDownSound = 4;
    protected static final int EndGameSound = 5;
    protected static final int BreakRecordSound = 6;
    protected Vibrator mVibrator;
    
    //Draw
    protected Canvas mCanvas;    
    protected Paint mPaint;   
    protected Typeface mFace;
    protected Path    mWallPath; //mX1WallPath ,mX2WallPath,mY1WallPath ,mY2WallPath;
    protected PathEffect mWallPathEffect;
    protected static Bitmap mEndBitmap , mBackgroundBitmap , mGameMapBitmap;
    
    //Snake
    protected int mSnakeLength ;
    protected Path mSnakePath , mSnakeHeadPath; 
    protected PathEffect mSnakePathEffect , mSnakeHeadPathEffect;
    
    //information about the phone screen
    protected int mWidth , mHeight ; //像素
    protected static int mXOffset , mYOffset ; //原点坐标的偏移量。
    protected static int TILESIZE , SNAKELENGT ; 
    protected int mXTileCount , mYTileCount ; //整个屏幕的主体部分(能放置苹果部分，不包括围墙)X 、Y 方向的方块总数。
    
    protected int mWallGap , mSnakeGap ;   //the distance between wallpath and Tile boundries ; snake for the same.
    protected int mShowGap ; //the height of show line in the top of screen
    protected int mTextSize ; //text size in show line.
    protected int mTopGap , mBottomGap ; 
 
    //sensibility of eating apple : pixels
    protected static final int mEatingAppleSensibility = 0;
    
    //mode of the game
    public int mMode = RUNNING;
    public static final int PAUSE = 0; //游戏画面和Dialog同时呈现，暂停。
    public static final int RUNNING = 1; //游戏运行中     
    
    //directions
    protected int mDirection ;
    protected static final int NORTH = 1;
    protected static final int SOUTH = -1;
    protected static final int EAST = 2;
    protected static final int WEST = -2;
    
    //velocities from sensor event
    protected float mXVelocity = 0;
    protected float mYVelocity = 0;
    private int mSpeed ;
		 private boolean biansuMode;
	
    //Coordinate in SnakeTrail and ScoreShow list is the absolute coordinate
    protected ArrayList<Coordinate> mSnakeTrail = new ArrayList<Coordinate>();
    protected ArrayList<ScoreShowWhenGot> mScoreShowList = new ArrayList<ScoreShowWhenGot>();
    //snake trail in tile coordinates in convenient for checking corruption.
   // protected ArrayList<Coordinate> mSnakeTile = new ArrayList<Coordinate>();
     
    //for random numbers
    protected static final Random RNG = new Random();
    
   
    //showed in the top of screen when gaming
    protected int mScore = 0; 
    protected int mLastMiliTime = 60000;    
    protected int mHighestScore ;
    protected boolean mJustBreakRecord = false;

    
    
	public GameBasicSurfaceView(Context context,AttributeSet paramAttributeSet) {
		super(context, paramAttributeSet);
		mContext = context ;
		init();
	}
	
	public GameBasicSurfaceView(Context context) {
		super(context);
		mContext = context ;
		init();
	}
	
	//initiate some members in the GameBasicSurfaceView class. 
	protected void init(){	
		mSurfaceHolder = this.getHolder();    
		mSurfaceHolder.addCallback(this);  
		mPaint = new Paint();    
		mPaint.setAntiAlias(true);    
		mPaint.setColor(Color.RED);    
		this.setKeepScreenOn(true);
		
		String vibratorService = Context.VIBRATOR_SERVICE;
        mVibrator = (Vibrator)this.getContext().getSystemService(vibratorService);
	}
	
	@Override
	public final void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mWidth = width ;
		mHeight = height ;	
	}

	@Override
	public final void surfaceCreated(SurfaceHolder holder) {	
		Log.e(TAG,"SurfacenCreated");
		this.setFocusable(true);
	  
		//get the information of screen
		mWidth = this.getWidth();  
		mHeight = this.getHeight(); 
		
		if (mWidth < 250 ){
			TILESIZE = 14 ;
			mSnakeLength = 70 ;
		}else if (mWidth < 350){
			TILESIZE = 19 ;
			mSnakeLength = 90 ;
		}
		else{
			TILESIZE = 28 ;
			mSnakeLength = 140 ;
		}
		
		//conclusion of screen size
		mShowGap = (int)Math.floor(mHeight * 0.1);  //the height for showing score and time and so on.
		mTopGap = 0;  // the height for juanzhou on the top 
		mBottomGap = 0 ;  // juanzhou space on the bottom.
		mTextSize = (int)mWidth/13 ;
		mXTileCount = (int) Math.floor((mWidth *0.90 )/ TILESIZE) ;
		mYTileCount = (int) Math.floor((mHeight - mTopGap - mBottomGap - mShowGap) / TILESIZE) ;		
		mXOffset = ( mWidth - (TILESIZE * mXTileCount))/2; 
		mYOffset = mTopGap + mShowGap ;
		
		//if it's a new game
		if (mLastMiliTime == 60000){
			//this two should be after getting the screen size , as  in the surfaceCreated()
			initResource();
			initNewGame();
		} //else , it's a pause game back

		// keep a lock for two threads : one is construction of surfaceView , one is the thread for running
		mSynLock = false;
	}
	
	
	protected void initResource(){
		//read background picture 
		Resources r = this.getContext().getResources();
		Drawable drawable = r.getDrawable(R.drawable.game_background);
		mBackgroundBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBackgroundBitmap);    
        drawable.setBounds(0, 0, mWidth , mHeight  );
        drawable.draw(canvas);
	
      
        //read end of game picture
        drawable = r.getDrawable(R.drawable.game_end);
        mEndBitmap = Bitmap.createBitmap(mXTileCount * TILESIZE *3/4, mYTileCount * TILESIZE *3/8, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(mEndBitmap);
        drawable.setBounds(0, 0,mXTileCount * TILESIZE *3/4, mYTileCount * TILESIZE *3/8);
        drawable.draw(canvas);	
        
        //read game map picture 
        drawable = r.getDrawable(R.drawable.game_map);
        mGameMapBitmap = Bitmap.createBitmap(TILESIZE * mXTileCount, TILESIZE * mYTileCount , Bitmap.Config.ARGB_8888);
        canvas = new Canvas(mGameMapBitmap);
        drawable.setBounds(0, 0, TILESIZE * mXTileCount, TILESIZE * mYTileCount );
        drawable.draw(canvas);
        
        //set fonts
        mFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/font_quit.ttf");
        
        //initiate sound and music 
    	mAudioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);
    	
    	mSoundPoolMap = new HashMap<Integer, Integer>();
    	mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
    	SoundPool tempSoundPoole = mSoundPool;
    	HashMap<Integer, Integer> tempSoundPoolMap = mSoundPoolMap ;    
    	
    	
    	tempSoundPoolMap.put(CountDownSound, tempSoundPoole.load(mContext,
				R.raw.count_down, 1));
		tempSoundPoolMap.put(EndGameSound, tempSoundPoole.load(mContext,
				R.raw.end_sound, 1));
		tempSoundPoolMap.put(BreakRecordSound, tempSoundPoole.load(mContext,
				R.raw.break_record, 1));
		
	//	mContext.setVolumeControlStream(AudioManager.STREAM_MUSIC); //bound with phone volume
		
		 SharedPreferences sp = this.getContext().getSharedPreferences("settings", 0);
		 mSpeed = sp.getInt("speed",5 );
		 biansuMode =sp.getBoolean("biansu", true);
		
		initResourceElse();
	}
	
	protected abstract void initResourceElse();
	
	//initiate game data
	public void initNewGame() {
		initSnake();
        initApple();
        initWall();
        mLastMiliTime = 60000;   
        mScore = 0; 
              
        //got the highest score
        SharedPreferences sp = this.getContext().getSharedPreferences("settings", 0);
        int mode=sp.getInt("mode", MainActivity.Classic_Game);
        if(mode==MainActivity.Classic_Game)
        	mHighestScore = sp.getInt("record_classic", 10);
        else if (mode == MainActivity.Reverse_Game){
        	mHighestScore = sp.getInt("record_reverse", 10);
        }else{
        	mHighestScore = sp.getInt("record_block", 10);
        }
        mSoundOn = sp.getBoolean("sound", true);
        mCurrentVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        initNewGameElse();
        
	}
	
	protected abstract void initNewGameElse();
	
	protected abstract void initApple();
	
	protected void initSnake(){
		//got the surface info of snake in shared preference
		SharedPreferences sp = this.getContext().getSharedPreferences("settings", 0);
        int type = sp.getInt("type", 0);
        
		//initiate the snake surface.
        mSnakePathEffect = BuildSnakeSurface.makeSnakePathEffect(type);
        mSnakeHeadPathEffect = BuildSnakeSurface.makeSnakeHeadPathEffect(type);
        mSnakeGap = BuildSnakeSurface.mSnakeGap;
		mSnakeTrail.clear();
    //	mSnakeTile.clear();
    	
    	int tempSnakeLength = mSnakeLength ;
        ArrayList<Coordinate> tempSnakeTrail = mSnakeTrail;
     //   ArrayList<Coordinate> tempSnakeTile = mSnakeTile;
    	int tempTileSize = TILESIZE , tempXOffset = mXOffset , tempYOffset = mYOffset;
        int tileXPos , tileYPos ,tmpTile = 0;
        tileYPos = (int)  Math.floor((300-mYOffset)/tempTileSize);
    	
    	for (int i = 0 ; i != tempSnakeLength ;i ++ ){
    		tempSnakeTrail.add(new Coordinate(4*TILESIZE+ tempXOffset  + mSnakeGap +i, 5*TILESIZE+ tempYOffset+ mSnakeGap)); 
    	    
    		
    		// transfer to tile coordinate
    		tileXPos = 5;
            if (tileXPos != tmpTile){
         //   	tempSnakeTile.add(new Coordinate(tileXPos,tileYPos));
            }
            tmpTile = tileXPos;
    	}
        mDirection = EAST ;
	}
	
	protected void initWall(){
		mWallPathEffect = new CornerPathEffect(6);
		mWallGap = 6 ;
		//construct the wall path
		mWallPath = new Path();
		mWallPath.moveTo(mXOffset - mWallGap/2, mYOffset - mWallGap/2);
		mWallPath.lineTo(mXOffset + mXTileCount * TILESIZE + mWallGap/2, mYOffset- mWallGap/2 );
		mWallPath.lineTo(mXOffset + mXTileCount * TILESIZE + mWallGap/2, mYOffset+ mYTileCount * TILESIZE + mWallGap/2);
		mWallPath.lineTo(mXOffset, mYOffset+ mYTileCount * TILESIZE + mWallGap/2);
		mWallPath.lineTo(mXOffset - mWallGap/2, mYOffset - mWallGap/2);
		mWallPath.close();
	}


	//transfer coordinate into int array to save
    protected int[] coordArrayListToArray(ArrayList<Coordinate> cvec) {
        int count = cvec.size();
        int[] rawArray = new int[count * 2];
        for (int index = 0; index < count; index++) {
            Coordinate c = cvec.get(index);
            rawArray[2 * index] = c.x;
            rawArray[2 * index + 1] = c.y;
        }
        return rawArray;
    }
  
 
  
    public Bundle saveState() {
        Bundle map = new Bundle();
      
        map.putInt("mDirection", Integer.valueOf(mDirection));
        map.putInt("mLastMiliTime", Integer.valueOf(mLastMiliTime));
        map.putInt("mScore", Integer.valueOf(mScore));
        map.putIntArray("mSnakeTrail", coordArrayListToArray(mSnakeTrail));
        return map;
    }
    
    //transfer int array to coordinate to restore
    protected ArrayList<Coordinate> coordArrayToArrayList(int[] rawArray) {
        ArrayList<Coordinate> coordArrayList = new ArrayList<Coordinate>();
        int coordCount = rawArray.length;
        for (int index = 0; index < coordCount; index += 2) {
            Coordinate c = new Coordinate(rawArray[index], rawArray[index + 1]);
            coordArrayList.add(c);
        }
        return coordArrayList;
    }
    
    public void restoreState(Bundle icicle) {
     
        mDirection = icicle.getInt("mDirection");
        mLastMiliTime = icicle.getInt("mLastMiliTime");
        mScore = icicle.getInt("mScore");
        mSnakeTrail = coordArrayToArrayList(icicle.getIntArray("mSnakeTrail"));
    }

    
	@Override
	public final void surfaceDestroyed(SurfaceHolder holder) {
	}

	
	@Override
	public void run() {
		long beforeExecTime , afterExecTime ;
		while(mSynLock){
			//do nothing but wait fir 
		}
		
		mCurrentVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		while (mIsRunning) {  
			beforeExecTime = System.currentTimeMillis();			
			//count down in the last 5 seconds
		 	boolean[] tempCountDownFlag = mCountDownFlag; 
			if(mLastMiliTime <= 5000 && tempCountDownFlag[0] && mSoundOn){
				tempCountDownFlag[0] = false ;
		 		mSoundPool.play(mSoundPoolMap.get(CountDownSound),
		 				mCurrentVol, mCurrentVol, 1, 0, 1f);
		 	}
		 	if(mLastMiliTime <= 4000 && tempCountDownFlag[1] && mSoundOn){
		 		tempCountDownFlag[1] = false ;
		 		mSoundPool.play(mSoundPoolMap.get(CountDownSound),
		 				mCurrentVol, mCurrentVol, 1, 0, 1f);
		 	}
		 	if(mLastMiliTime <= 3000 && tempCountDownFlag[2] && mSoundOn){
		 		tempCountDownFlag[2] = false ;
		 		mSoundPool.play(mSoundPoolMap.get(CountDownSound),
		 				mCurrentVol, mCurrentVol, 1, 0, 1f);
		 	}
		 	if(mLastMiliTime <= 2000 && tempCountDownFlag[3] && mSoundOn){
		 		tempCountDownFlag[3] = false ;
		 		mSoundPool.play(mSoundPoolMap.get(CountDownSound),
		 				mCurrentVol, mCurrentVol, 1, 0, 1f);
		 	}
		 	if(mLastMiliTime <= 1000 && tempCountDownFlag[4] && mSoundOn){
		 		tempCountDownFlag[4] = false ;
		 		mSoundPool.play(mSoundPoolMap.get(CountDownSound),
		 				mCurrentVol, mCurrentVol, 1, 0, 1f);
		 	}
			
		 	// game is over 
			if (mLastMiliTime <= 0){ 
				if (mSoundOn && mEndSoundFlag){
					mSoundPool.play(mSoundPoolMap.get(EndGameSound), mCurrentVol, mCurrentVol, 1, 0, 1f);
					mEndSoundFlag = false;
				}
				draw();
		 		if (mLastMiliTime <= -3000){ //after that , turn into result activity 
			 		Log.e("性能测试，总计处理时间：", timeRecord + "ms" );
		 			mIsRunning = false ; //kill the thread
			 		mMode = PAUSE; //for check in the onPause() in superSnake activity
			 		Intent intent = new Intent();             
			 		intent.setClass(this.getContext(), ResultActivity.class);         
			 		intent.putExtra("score", mScore);
			 		this.getContext().startActivity(intent);
			 		
			 		mOnEndOfGame.onEndOfGame(); //end SuperSnake activity
			 	}
				//control the loop time in 50ms
		 		afterExecTime =  System.currentTimeMillis();
		        int execTime = (int) (afterExecTime - beforeExecTime);  
		        if (execTime < 50 ){
			        mLastMiliTime -= 50 ;
		          	try {
			               Thread.sleep(50 -execTime);   
			        } catch (InterruptedException e) {    
			               e.printStackTrace();    
			        }   
		        }else {
		           	mLastMiliTime -= execTime ;
		        }

		 		continue;
		 	}
		 	
		 	update();
		 	draw();
		 	
	        afterExecTime =  System.currentTimeMillis();
	        
	        int execTime = (int) (afterExecTime - beforeExecTime);	    
	        timeRecord += execTime ;
	        if (execTime < 50 ){
		        mLastMiliTime -= 50 ;
	          	try {    
		               Thread.sleep(50 -execTime);   
	          	} catch (InterruptedException e) {    
		               e.printStackTrace();    
	          	}   
	        }else {
	           	mLastMiliTime -= execTime ;
	        }
		} 
	}
	
    protected void update() {
   	 	updateSnake();
   	 	updateApples();
   	 	updateScore();  //the score showing in the map when eating an apple
    }
	
    protected  void updateSnake() {    	
      	 int tempVelocity ;
      	 ArrayList<Coordinate> tempSnakeTrail = mSnakeTrail;
      	 
    	//get the larger one in x and y direction and velocity
    	if ( Math.abs(mXVelocity) >=  Math.abs(mYVelocity) ){
    		mDirection = (mXVelocity >0 ? WEST : EAST );
    		if(biansuMode){
    			tempVelocity = Math.abs((int)mXVelocity) * mSpeed;
    		}else{
    			tempVelocity = mSpeed * 3;
    			}
    	}
    	else{
    		mDirection = (mYVelocity >0 ? SOUTH : NORTH );
    		
    		if(biansuMode){
        tempVelocity = Math.abs((int)mYVelocity)* mSpeed;
    		}else{
    			tempVelocity = mSpeed * 3;
    			}
    	}
      	 
      
    	
    	Coordinate headCoord = tempSnakeTrail.get(mSnakeLength-1); //the head of snake
   	
    	// move the snake
    	out:
    	for (int i = 0 ; i != tempVelocity ; i ++){
    		switch(mDirection){
    			case NORTH:
    				if (headCoord.y - 1 > mYOffset +  mSnakeGap ){
    					tempSnakeTrail.add(new Coordinate(headCoord.x , headCoord.y - 1));
    					tempSnakeTrail.remove(0);
    				}
    				else{
    					break out;
    				}	
    				break;
    			case SOUTH:
    				if (headCoord.y + 1 < mYOffset + mYTileCount * TILESIZE - mSnakeGap ){
    					tempSnakeTrail.add(new Coordinate(headCoord.x , headCoord.y + 1));
    					tempSnakeTrail.remove(0);
    				}
    				else{
    					break out;
    				}	
    				break;
    			case WEST:
    				if (headCoord.x - 1 > mXOffset +mSnakeGap ){
    					tempSnakeTrail.add(new Coordinate(headCoord.x -1 , headCoord.y));
    					tempSnakeTrail.remove(0);
    				}
    				else{
    					break out;
    				}	
    				break;
    			case EAST:
    				if (headCoord.x + 1 < mXOffset + mXTileCount * TILESIZE - mSnakeGap ){
    					tempSnakeTrail.add(new Coordinate(headCoord.x + 1, headCoord.y));
    					tempSnakeTrail.remove(0);
    				}
    				else{
    					break out;
    				}	
    				break;
    		}
    		headCoord = tempSnakeTrail.get(mSnakeLength-1); //got new head of snake
    		
    		eatingApplesCheck(tempSnakeTrail.get(mSnakeLength -1));
    	}	
    	
    	//refresh snake path
    	Path tempSnakePath = new Path();
    	Path tempSnakeHeadPath = new Path();
    	int tempSnakeLength = mSnakeLength ;
    	if (tempSnakeLength == 140 ){
			for (int index = 0; index < tempSnakeLength; index++) {
	            if (index == 0){
	            	tempSnakePath.moveTo(tempSnakeTrail.get(index).x, tempSnakeTrail.get(index).y);
	            }else if(index < tempSnakeLength - 15){
	            	tempSnakePath.lineTo(tempSnakeTrail.get(index).x, tempSnakeTrail.get(index).y);
	            }else if(index == tempSnakeLength - 15){
	            	index += 12;//turn into the a curtain position to get a SNAKE_HEAD
	            }else if(index == tempSnakeLength - 2){
	            	tempSnakeHeadPath.moveTo(tempSnakeTrail.get(index).x, tempSnakeTrail.get(index).y);
	            	index ++ ;
	            	tempSnakeHeadPath.lineTo(tempSnakeTrail.get(index).x, tempSnakeTrail.get(index).y);
	            	//Log.e("Head","OK");
	            } 		
	        }
    	}else{
	    	for (int index = 0; index < tempSnakeLength; index++) {
	            if (index == 0){
	            	tempSnakePath.moveTo(tempSnakeTrail.get(index).x, tempSnakeTrail.get(index).y);
	            }else if(index < tempSnakeLength - 5){
	            	tempSnakePath.lineTo(tempSnakeTrail.get(index).x, tempSnakeTrail.get(index).y);
	            }else if(index == tempSnakeLength - 5){
	            	index += 2;//turn into the a curtain position to get a SNAKE_HEAD
	            }else if(index == tempSnakeLength - 2){
	            	tempSnakeHeadPath.moveTo(tempSnakeTrail.get(index).x, tempSnakeTrail.get(index).y);
	            	index ++ ;
	            	tempSnakeHeadPath.lineTo(tempSnakeTrail.get(index).x, tempSnakeTrail.get(index).y);
	            	//Log.e("Head","OK");
	            } 		
	        }
    	}
        mSnakeHeadPath = tempSnakeHeadPath;
    	mSnakePath= tempSnakePath;
    }
    
    
    protected abstract void eatingApplesCheck(Coordinate headCoord);
    
    protected boolean IsEatingApple(Coordinate headCoord , Coordinate appleCoord){
    	switch(mDirection){
    		case NORTH :
    		case SOUTH :   // mEatingAppleSensibility is for bluring
    			if (isMatch(new Coordinate(headCoord.x+mSnakeGap -mEatingAppleSensibility , 
    						headCoord.y), appleCoord)){
    				return true ;
    			}
    			else if(isMatch(new Coordinate(headCoord.x-mSnakeGap + mEatingAppleSensibility,
    						headCoord.y), appleCoord)){	
    				return true ;
    			}else if(isMatch(headCoord , appleCoord)){	
    				return true ;
    			}
    			break;
    		case EAST :
    		case WEST :
    			if (isMatch(new Coordinate(headCoord.x , 
    					headCoord.y+mSnakeGap -mEatingAppleSensibility), appleCoord)){
    				return true ;
    			}
    			else if(isMatch(new Coordinate(headCoord.x , 
    					headCoord.y-mSnakeGap + mEatingAppleSensibility), appleCoord)){	
    				return true ;
    			}else if(isMatch(headCoord , appleCoord)){	
    				return true ;
    			}
    			break;
    	} 	
    	return false ;
    }
        
	protected boolean isMatch(Coordinate pixelCoord , Coordinate tileCoord){
		if ( (pixelCoord.x - mXOffset)/TILESIZE == tileCoord.x ){
    		if ((pixelCoord.y - mYOffset)/TILESIZE == tileCoord.y ){
    			return true ;
    		}
    	}
    	return false ;
    }

    
    protected abstract void updateApples();
    
    
    
    protected abstract void updateScore();
    

    
    
	public void draw() {
		try {			
	        mCanvas = mSurfaceHolder.lockCanvas(); // important in surfaceview
	        mCanvas.drawColor(Color.BLACK);
	        
	        mCanvas.drawBitmap(mBackgroundBitmap, 0,0, mPaint);
	        mCanvas.drawBitmap(mGameMapBitmap, mXOffset,mYOffset, mPaint);
	        
	        //********************WALL*********************
			mPaint.setPathEffect(mWallPathEffect);
			mPaint.setColor(Color.GRAY);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(mWallGap);
			mCanvas.drawPath(mWallPath, mPaint);
			
	        //*************SNAKE**************************
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setPathEffect(mSnakePathEffect);
	        mPaint.setColor(Color.BLACK);
			mCanvas.drawPath(mSnakePath, mPaint);				
			mPaint.setPathEffect(mSnakeHeadPathEffect);
	        mPaint.setColor(Color.BLACK);
			mCanvas.drawPath(mSnakeHeadPath, mPaint);	
			
			
	    	//draw the end pic
			if (mLastMiliTime < 0){
				mCanvas.drawBitmap(mEndBitmap, (mXOffset +mXTileCount*TILESIZE /8), 
								mYOffset + mYTileCount*TILESIZE /4 , mPaint);
			}
			
			
			drawApplesAndScores();
			
			

			//**************TIME + SCORE + RECORD ********************
			
			mPaint.setTextSize((int)(mTextSize));//text size 
			mPaint.setPathEffect(null);
			mPaint.setColor(Color.BLACK);
			mPaint.setTypeface(mFace);
			mCanvas.drawText(mContext.getString(R.string.score_show_in_game),
						(int)(mWidth*0.04) , mTopGap + (int)(mShowGap*0.4), mPaint);
			mPaint.setColor(Color.RED);
			mCanvas.drawText(""+mScore,(int)(mWidth*0.3) , mTopGap + (int)(mShowGap*0.4), mPaint);
			mPaint.setColor(Color.BLACK);
			mCanvas.drawText(mContext.getString(R.string.time_show_in_game)+
								((mLastMiliTime>0)?(mLastMiliTime/1000):0)+"s",
								(int)(mWidth*0.65), mTopGap + (int)(mShowGap*0.4) , mPaint);
			
			mPaint.setTextSize((int)(mTextSize*0.65));
			mCanvas.drawText(mContext.getString(R.string.highest_score_show_in_game)+
								mHighestScore, (int)(mWidth*0.04) ,mTopGap + (int)(mShowGap*0.8), mPaint);
			
		} catch (Exception ex) {    
			Log.e("BASIC：Draw",ex.toString());
		} finally {   
            if (mCanvas != null)    
            	mSurfaceHolder.unlockCanvasAndPost(mCanvas);      
        }    
	}
	
	protected abstract void drawApplesAndScores();
	
    
	

	
    //set mode of running game
    public void setMode(int newMode) {
    	Log.e(TAG,"setMode(" + newMode + ")");
    	int oldMode = mMode;
    	mMode = newMode;
    	
    	if (newMode == PAUSE ){
    		Log.d(TAG, "PAUSE");
    		if (oldMode == RUNNING){  //pause from running
    			mIsRunning = false; //kill thread. 
    		}
    	}else if(newMode == RUNNING){
    		draw();
    		SharedPreferences sp = this.getContext().getSharedPreferences("settings", 0);
    	    mSoundOn = sp.getBoolean("sound", true);
    		if (!mIsRunning){
	    		this.setFocusable(true);
	    		mThread = new Thread(this);  //run a game thread
				mIsRunning = true ;
				mThread.start();
    		}
    	}
    }
   

	public void setOnEndOfGame(OnEndOfGameInterface xOnEndOfGame){
		mOnEndOfGame = xOnEndOfGame;
	}
	
	public boolean canQuit(){
		return (mLastMiliTime>0)?true:false;
	}
	
	
	
	
}

