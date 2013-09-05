package com.snake;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import com.snake.*;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;


public class BlockGameSurfaceView extends GameBasicSurfaceView {
//BEGIN
//easy to change the configuration of the game:
    //score for each apple
    private static final int NORMAL_SCORE = 6 ;
    private static final int BLOCK_SCORE = - 6;
    private static final int MOVING_POISONOUS_SCORE = -8;
    
    //the speed of moving poisonous apple
    private final static int MOVINGPOISONOUSAPPLE_LOCK = 3;
		

    private static final int APPLE_DISAPPEAR_COUNT_MAX = 18 ;
    private static final int SCORE_SHOW_COUNT_MAX = 30;
    private static final int APPLE_FLICKER_COUNT_MAX = 20 ;
    // normal apples in the game at one time
	private int mNormalAppleCount = 3 ; 
	
	//when eat this count , block apples will be refreshed
	private final static int EATBLOCKUPLIMIT = 4;
	
	//control
	private final static float SENSITIVE = 1.3f ;
	private final static double TOLERANCE = 0.15 ;
//END
	
	private int mEatBlockAppleCount = 0;
	private int mBlockAppleCount ; // count the total number of block apples in the map.(it's different from phone to phone)
	private static Bitmap  mNormalAppleBitmap , mBlockAppleBitmap , 
	mMovingPoisonousAppleBitmap , mPoisonousAppleDisappearBitmap ,
	mAppleDisappearBitmap , mMovingAppleDisappearBitmap;
    private static Bitmap  mNormalAppleScoreBitmap, mBlockAppleScoreBitmap , mMovingPoisonousAppleScoreBitmap ;
    private Paint mDisappearAppleTranspPaint[] = new Paint[APPLE_DISAPPEAR_COUNT_MAX];
	private static final int EAT_NORMAL_APPLE_SOUND = 1; 
	private static final int EAT_BLOCK_APPLE_SOUND = 2; 
	private static final int EAT_MOVING_POISONOUS_APPLE_SOUND = 2;

	private ArrayList<FlickerAppleCoordinate> mNormalAppleList = new ArrayList<FlickerAppleCoordinate>();
	private ArrayList<Coordinate> mBlockAppleList = new ArrayList<Coordinate>();
	private ArrayList<Coordinate> mMovingPoisonousAppleListXPositive = new ArrayList<Coordinate>();
	private ArrayList<Coordinate> mMovingPoisonousAppleListYPositive = new ArrayList<Coordinate>();
	private ArrayList<Coordinate> mMovingPoisonousAppleListXNegative = new ArrayList<Coordinate>();
	private ArrayList<Coordinate> mMovingPoisonousAppleListYNegative = new ArrayList<Coordinate>();
	private ArrayList<DisappearAppleCoordinate> mNormalAppleAnimationList = new ArrayList<DisappearAppleCoordinate>();
	private ArrayList<DisappearAppleCoordinate> mBlockAppleAnimationList = new ArrayList<DisappearAppleCoordinate>();
	private ArrayList<DisappearAppleCoordinate> mMovingAppleAnimationList = new ArrayList<DisappearAppleCoordinate>();

	
	private int mMovingPoisonousAppleMoveLock = 0;  //the lock controlling the move of unbelievable apples
	
	
	public BlockGameSurfaceView(Context context) {
		super(context);
		initSensor();
	}
	
	public BlockGameSurfaceView(Context context, AttributeSet paramAttributeSet) {
		super(context, paramAttributeSet);
		initSensor();
	}
	
	void initSensor(){
		mSensorManager = (SensorManager)this.getContext().getSystemService(Context.SENSOR_SERVICE);
		Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(sensorEventListener, accelerometer,SensorManager.SENSOR_DELAY_GAME);
	}
	
	@Override
	protected void initResourceElse() {
		  //read apples picture 
		Resources r = this.getContext().getResources();
		Drawable drawable = r.getDrawable(R.drawable.food_6);
		mNormalAppleBitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mNormalAppleBitmap);    
        drawable.setBounds(0, 0, TILESIZE, TILESIZE );
        drawable.draw(canvas);	   
	
        drawable = r.getDrawable(R.drawable.food_fu6);
		mBlockAppleBitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(mBlockAppleBitmap);    
        drawable.setBounds(0, 0, TILESIZE, TILESIZE );
        drawable.draw(canvas);	 
        
        drawable = r.getDrawable(R.drawable.food_fu8);
        mMovingPoisonousAppleBitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(mMovingPoisonousAppleBitmap);    
        drawable.setBounds(0, 0, TILESIZE, TILESIZE );
        drawable.draw(canvas);	 
       
        
        int tempBigTileSize =(int) 1.8 * TILESIZE ;
        //read ScoreShow picture         
        drawable = r.getDrawable(R.drawable.score_6);
        mNormalAppleScoreBitmap = Bitmap.createBitmap(tempBigTileSize, tempBigTileSize, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(mNormalAppleScoreBitmap);    
        drawable.setBounds(0, 0, tempBigTileSize, tempBigTileSize );
        drawable.draw(canvas);	
        
        drawable = r.getDrawable(R.drawable.score_fu6);
        mBlockAppleScoreBitmap = Bitmap.createBitmap(tempBigTileSize, tempBigTileSize, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(mBlockAppleScoreBitmap);    
        drawable.setBounds(0, 0,tempBigTileSize, tempBigTileSize);
        drawable.draw(canvas);	

        drawable = r.getDrawable(R.drawable.score_fu8);
        mMovingPoisonousAppleScoreBitmap = Bitmap.createBitmap(tempBigTileSize, tempBigTileSize, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(mMovingPoisonousAppleScoreBitmap);    
        drawable.setBounds(0, 0,tempBigTileSize, tempBigTileSize);
        drawable.draw(canvas);	
        
        //TODO
		drawable = r.getDrawable(R.drawable.food_fu6_eaten); //TODO
		mPoisonousAppleDisappearBitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mPoisonousAppleDisappearBitmap);    
		drawable.setBounds(0, 0, TILESIZE, TILESIZE );
		drawable.draw(canvas);	 
		
		drawable = r.getDrawable(R.drawable.food_6_eaten); //TODO
		mAppleDisappearBitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mAppleDisappearBitmap);    	
		drawable.setBounds(0, 0, TILESIZE, TILESIZE );
		drawable.draw(canvas);	 
		
		drawable = r.getDrawable(R.drawable.food_fu8_eaten); //TODO
		mMovingAppleDisappearBitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mMovingAppleDisappearBitmap);    	
		drawable.setBounds(0, 0, TILESIZE, TILESIZE );
		drawable.draw(canvas);	 

		

        for(int i= 0 ; i !=APPLE_DISAPPEAR_COUNT_MAX; i++){
			mDisappearAppleTranspPaint[i] = new Paint();
			mDisappearAppleTranspPaint[i].setStyle(Paint.Style.STROKE);
			mDisappearAppleTranspPaint[i].setAlpha(100+(150/APPLE_DISAPPEAR_COUNT_MAX)*i);
		}
        
        HashMap<Integer, Integer> tempSoundPoolMap = mSoundPoolMap ;    
    	tempSoundPoolMap.put(EAT_NORMAL_APPLE_SOUND, mSoundPool.load(NewGameActivity.mContext,
				R.raw.eat_apple, 1));
    	tempSoundPoolMap.put(EAT_BLOCK_APPLE_SOUND, mSoundPool.load(NewGameActivity.mContext,
				R.raw.eat_poinsonous_apple, 1));
		
		
	}
	
	@Override
	protected void initNewGameElse() {
		mMovingPoisonousAppleMoveLock = -1;
	}
	
    private int[][] mRandomMovingPoisonousApple ;
    
	@Override
	protected void initApple() {
		buildBlock(1);
		buildNormalApple();
		mMovingPoisonousAppleListXPositive.clear(); 
		mMovingPoisonousAppleListYPositive.clear(); 
		mMovingPoisonousAppleListXNegative.clear(); 
		mMovingPoisonousAppleListYNegative.clear(); 
	
		mRandomMovingPoisonousApple = 
        	new int[][] { {  RNG.nextInt(10000)+45000 , 
        					RNG.nextInt(mYTileCount/2-1)+1 ,
							RNG.nextInt(mYTileCount/2-1) + mYTileCount/2 -1, 
							RNG.nextInt(mYTileCount/2-1)+1 ,
							RNG.nextInt(mYTileCount/2-1)+mYTileCount/2 -1,
							RNG.nextInt(mXTileCount/2-1)+mXTileCount/2 -1, 
						RNG.nextInt(mXTileCount/2-1)+1 , 
							RNG.nextInt(mXTileCount/2-1)+mXTileCount/2 -1, 
							RNG.nextInt(mXTileCount/2-1)+1 },
        				  { RNG.nextInt(10000)+30000 , 
        					RNG.nextInt(mYTileCount/2-1)+1 ,
							RNG.nextInt(mYTileCount/2-1)+mYTileCount/2 -1, 
							RNG.nextInt(mYTileCount/2-1)+1 ,
							RNG.nextInt(mYTileCount/2-1)+mYTileCount/2 -1,
							RNG.nextInt(mXTileCount/2-1)+mXTileCount/2 -1, 
							RNG.nextInt(mXTileCount/2-1)+1 , 
							RNG.nextInt(mXTileCount/2-1)+mXTileCount/2 -1, 
							RNG.nextInt(mXTileCount/2-1)+1 },
						  { RNG.nextInt(10000)+15000 , 
							RNG.nextInt(mYTileCount/2-1)+1 ,
							RNG.nextInt(mYTileCount/2-1)+mYTileCount/2 -1, 
							RNG.nextInt(mYTileCount/2-1)+1 ,
							RNG.nextInt(mYTileCount/2-1)+mYTileCount/2 -1,
							RNG.nextInt(mXTileCount/2-1)+mXTileCount/2 -1, 
							RNG.nextInt(mXTileCount/2-1)+1 , 
							RNG.nextInt(mXTileCount/2-1)+mXTileCount/2 -1, 
							RNG.nextInt(mXTileCount/2-1)+1 }
        				};	
			
	}
	
	private void buildBlock(int type){
		mEatBlockAppleCount = 0;
		mBlockAppleList.clear();
		 
		switch(type){
			case 1 :
				for (int i = 1 ; i < mXTileCount -1 ; i += 4){
					for (int j = 1 ; j < mYTileCount -1; j += 9){
						mBlockAppleList.add(new Coordinate(i,j));
						mBlockAppleCount ++ ;
					}
				}
				for (int i = 2 ; i < mXTileCount -1 ; i += 4){
					for (int j = 4 ; j < mYTileCount -1 ; j += 9){
						mBlockAppleList.add(new Coordinate(i,j));
						mBlockAppleCount ++ ;
					}
				}
				for (int i = 3 ; i < mXTileCount -1 ; i += 4){
					for (int j = 7 ; j < mYTileCount -1 ; j += 9){
						mBlockAppleList.add(new Coordinate(i,j));
						mBlockAppleCount ++ ;
					}
				}
				break;
			case 2 :
				for (int i = 1 ; i < mXTileCount ; i += 4){
					for (int j = 1 ; j < mYTileCount ; j += 4){
						mBlockAppleList.add(new Coordinate(i,j));
						mBlockAppleCount ++ ;
					}
				}
				for (int i = 3 ; i < mXTileCount ; i += 4){
					for (int j = 3 ; j < mYTileCount ; j += 4){
						mBlockAppleList.add(new Coordinate(i,j));
						mBlockAppleCount ++ ;
					}
				}
				
				break;
			default:
		}
	}
	
	private void buildNormalApple(){
		mNormalAppleCount = 3;
		mNormalAppleList.clear();
		for (int i = 0 ; i!= mNormalAppleCount ; i++){
			addRandomApple();
		}
	}
	
	protected void addRandomApple() {
 	   Coordinate newCoord = null;
        boolean found = false;
     
        while (!found) {
            // Choose a new location for our apple
            int newX = 1 + RNG.nextInt(mXTileCount-1);
            int newY = 1 + RNG.nextInt(mYTileCount-1);
            newCoord = new Coordinate(newX, newY);
            
            //check for collision
            boolean collision = false;
            try{	  
                for (Coordinate c : mNormalAppleList){
               	 	if (c.equals(newCoord)){
               	 		collision = true;
               	 	}
                }
                for (Coordinate c : mBlockAppleList){
               	 	if (c.equals(newCoord)){
               	 		collision = true;
               	 	}
                }
                found = !collision;
            }catch(ConcurrentModificationException e){
           	 Log.e("ConcurrentModificationException",e.toString()+"CON");
            }
    
        }
        if (newCoord == null) {
     
        }
        mNormalAppleList.add(new FlickerAppleCoordinate(newCoord.x, newCoord.y, 
        					APPLE_FLICKER_COUNT_MAX));
 }


	@Override
	protected void eatingApplesCheck(Coordinate headCoord) {
        int applecount = mNormalAppleList.size();
        for (int appleindex = 0; appleindex < applecount; appleindex++) {
        	Coordinate c = mNormalAppleList.get(appleindex);
            if (IsEatingApple(headCoord,c)) {
        				mNormalAppleAnimationList.add(new DisappearAppleCoordinate(c.x,c.y ,APPLE_DISAPPEAR_COUNT_MAX));
            	if (mSoundOn){
            		mSoundPool.play(mSoundPoolMap.get(EAT_NORMAL_APPLE_SOUND), 
            						mCurrentVol, mCurrentVol, 1, 0, 1f);
            	}
            	mScoreShowList.add(new ScoreShowWhenGot( headCoord.x, 
            			headCoord.y ,ScoreShowWhenGot.NORMAL_SCORE , SCORE_SHOW_COUNT_MAX));  
            	mNormalAppleList.remove(c);
            	addRandomApple();
                break;
            }
        }
        
        // Block Apple
	    applecount = mBlockAppleList.size();
	    for (int appleindex = 0; appleindex < applecount; appleindex++) {
	    	Coordinate c = mBlockAppleList.get(appleindex);
	    	if (IsEatingApple(headCoord,c)) {
	    		mBlockAppleAnimationList.add(new DisappearAppleCoordinate(c.x,c.y ,APPLE_DISAPPEAR_COUNT_MAX));
	    		if (mSoundOn){ 
	    			mSoundPool.play(mSoundPoolMap.get(EAT_BLOCK_APPLE_SOUND), mCurrentVol,
	    					mCurrentVol, 1, 0, 1f);
	    		}
	    		mScoreShowList.add(new ScoreShowWhenGot(headCoord.x , headCoord.y , 
	    					ScoreShowWhenGot.BLOCK_SCORE , SCORE_SHOW_COUNT_MAX));
	    		mBlockAppleList.remove(c);
	    		
	    		
	    		break;
	    	}
	    }
	    
	
	}
	
	private boolean isCollisionWithSnake(Coordinate apple){
		for (int i = 0 ; i!= mSnakeLength ; i++){
			if (isMatch(mSnakeTrail.get(i) , apple)){
				return true ;
			}
		}
		return false;
	}
	
	@Override
	protected void updateApples() {
		//flicker
			FlickerAppleCoordinate.changeCondition();
		
		//check for UnbelievableApple 
    	int[][] tempRandomMovingPoisonousApple = mRandomMovingPoisonousApple;
    	
    	//check the time of moving poisonous apples
    	int tempMovingPoisonousAppleMoveLock = mMovingPoisonousAppleMoveLock ;
    	for (int i = 0 ; i != tempRandomMovingPoisonousApple.length ; i ++){
    		if (mLastMiliTime < tempRandomMovingPoisonousApple[i][0]){
    			mMovingPoisonousAppleListXPositive.add(
    					new Coordinate(0,tempRandomMovingPoisonousApple[i][1]));
    			mMovingPoisonousAppleListXPositive.add(
    					new Coordinate(0,tempRandomMovingPoisonousApple[i][2]));
    			mMovingPoisonousAppleListXNegative.add(
    					new Coordinate(mXTileCount -1,tempRandomMovingPoisonousApple[i][3]));
    			mMovingPoisonousAppleListXNegative.add(
    					new Coordinate(mXTileCount -1,tempRandomMovingPoisonousApple[i][4]));
    			mMovingPoisonousAppleListYPositive.add(
    					new Coordinate(tempRandomMovingPoisonousApple[i][5],0));
    			mMovingPoisonousAppleListYPositive.add(
    					new Coordinate(tempRandomMovingPoisonousApple[i][6],0));
    			mMovingPoisonousAppleListYNegative.add(
    					new Coordinate(tempRandomMovingPoisonousApple[i][7],mYTileCount -1));
    			mMovingPoisonousAppleListYNegative.add(
    					new Coordinate(tempRandomMovingPoisonousApple[i][8],mYTileCount -1));
    			tempRandomMovingPoisonousApple[i][0] = -1 ;//this is the time when unbelievable apple come out
                tempMovingPoisonousAppleMoveLock = 0 ;
    			break;
    		}
    	}
    	
    	if (tempMovingPoisonousAppleMoveLock >= 0 ){  //unbelievable apple is running
    		tempMovingPoisonousAppleMoveLock ++ ;
   
    		// out of boundary
    		if (tempMovingPoisonousAppleMoveLock == MOVINGPOISONOUSAPPLE_LOCK){
    			tempMovingPoisonousAppleMoveLock = 0; //do not move apple until mUnbelievableAppleMoveLock is 10
    			//X Positive Direction
    		
    			int unbelievableAppleCount = mMovingPoisonousAppleListXPositive.size();
    			for (int i = 0 ; i < unbelievableAppleCount ; i ++){
    		
    				if ( ++(mMovingPoisonousAppleListXPositive.get(i).x) >= mXTileCount ){
    					mMovingPoisonousAppleListXPositive.clear(); 
    					break ;
    				}
    			}
    			//X Negative Directoin
    			unbelievableAppleCount = mMovingPoisonousAppleListXNegative.size();
    			for (int i = 0 ; i < unbelievableAppleCount ; i ++){
    				if ( --(mMovingPoisonousAppleListXNegative.get(i).x) <= 0){
    					mMovingPoisonousAppleListXNegative.clear(); 
    					break ; 
    				}
    			}
    			//Y Positive Directoin
    			unbelievableAppleCount = mMovingPoisonousAppleListYPositive.size();
    			for (int i = 0 ; i < unbelievableAppleCount ; i ++){
    				if ( ++(mMovingPoisonousAppleListYPositive.get(i).y) >= mYTileCount ){
    					mMovingPoisonousAppleListYPositive.clear(); 
    					mMovingPoisonousAppleMoveLock = -1 ; //mean that unbelievable apple is not running
    					break ; 
    				}
    			}
    			//Y Negative Directoin
    			unbelievableAppleCount = mMovingPoisonousAppleListYNegative.size();
    			for (int i = 0 ; i < unbelievableAppleCount ; i ++){
    				if ( --(mMovingPoisonousAppleListYNegative.get(i).y) <= 0){
    					mMovingPoisonousAppleListYNegative.clear(); 
    					mMovingPoisonousAppleMoveLock = -1 ; //mean that unbelievable apple is not running
    					break ; 
    				}
    			}
    		}
    	}
    	mMovingPoisonousAppleMoveLock = tempMovingPoisonousAppleMoveLock;
    	
	    // check collision with snake	
	    int applecount = mMovingPoisonousAppleListXPositive.size();
	    for (int appleindex = 0; appleindex < applecount; appleindex++) {
	    	Coordinate c = mMovingPoisonousAppleListXPositive.get(appleindex);
	    	if (isCollisionWithSnake(c)) {
	    		mMovingAppleAnimationList.add(new DisappearAppleCoordinate(c.x , c.y ,APPLE_DISAPPEAR_COUNT_MAX));
	    		if (mSoundOn){
	    			mSoundPool.play(mSoundPoolMap.get(EAT_MOVING_POISONOUS_APPLE_SOUND), 
	    						mCurrentVol, mCurrentVol, 1, 0, 1f);
	    		}
	    		mScoreShowList.add(new ScoreShowWhenGot( c.x * TILESIZE + mXOffset, 
	    						c.y * TILESIZE + mYOffset ,ScoreShowWhenGot.MOVING_POISONOUS_SCORE , SCORE_SHOW_COUNT_MAX));  
	    		mMovingPoisonousAppleListXPositive.remove(c);
	    		mScore += MOVING_POISONOUS_SCORE;
	    		//avoid the pseudo highest score occasion.
	            SharedPreferences sp = this.getContext().getSharedPreferences("settings", 0);
	            int tempRecord = sp.getInt("record_block", 10);
	            if (tempRecord >= mHighestScore){
	    			mHighestScore = tempRecord ;
	    		}else{
	    			mHighestScore = mScore ;
	    		}
	    		break; 
	    	}
	    }
	    applecount = mMovingPoisonousAppleListXNegative.size();
	    for (int appleindex = 0; appleindex < applecount; appleindex++) {
	    	Coordinate c = mMovingPoisonousAppleListXNegative.get(appleindex);
	    	if (isCollisionWithSnake(c)) {
	    		mMovingAppleAnimationList.add(new DisappearAppleCoordinate(c.x , c.y ,APPLE_DISAPPEAR_COUNT_MAX));
	    		if (mSoundOn){
	    			mSoundPool.play(mSoundPoolMap.get(EAT_MOVING_POISONOUS_APPLE_SOUND), mCurrentVol, mCurrentVol, 1, 0, 1f);
	    		}
	    		mScoreShowList.add(new ScoreShowWhenGot( c.x * TILESIZE + mXOffset, 
						c.y * TILESIZE + mYOffset ,ScoreShowWhenGot.MOVING_POISONOUS_SCORE , SCORE_SHOW_COUNT_MAX));  
	    		mMovingPoisonousAppleListXNegative.remove(c);
	    		mScore += ScoreShowWhenGot.MOVING_POISONOUS_SCORE;
	    		//avoid the pseudo highest score occosion.
	            SharedPreferences sp = this.getContext().getSharedPreferences("settings", 0);
	            int tempHighestScore = sp.getInt("record_block", 10);
	            if (tempHighestScore >= mHighestScore){
	    			mHighestScore = tempHighestScore ;
	    		}else{
	    			mHighestScore = mScore ;
	    		}
	    		break;
	    	}
	    }
	    applecount =mMovingPoisonousAppleListYPositive.size();
	    for (int appleindex = 0; appleindex < applecount; appleindex++) {
	    	Coordinate c = mMovingPoisonousAppleListYPositive.get(appleindex);
	    	if (isCollisionWithSnake(c)) {
	    		mMovingAppleAnimationList.add(new DisappearAppleCoordinate(c.x , c.y ,APPLE_DISAPPEAR_COUNT_MAX));
	    		if (mSoundOn){
	    			mSoundPool.play(mSoundPoolMap.get(EAT_MOVING_POISONOUS_APPLE_SOUND), mCurrentVol, mCurrentVol, 1, 0, 1f);
	    		}
	    		mScoreShowList.add(new ScoreShowWhenGot( c.x * TILESIZE + mXOffset, 
						c.y * TILESIZE + mYOffset ,ScoreShowWhenGot.MOVING_POISONOUS_SCORE, SCORE_SHOW_COUNT_MAX));  
	    		mMovingPoisonousAppleListYPositive.remove(c);
	    		mScore += ScoreShowWhenGot.MOVING_POISONOUS_SCORE;
	    		//avoid the pseudo highest score occosion.
	            SharedPreferences sp = this.getContext().getSharedPreferences("settings", 0);
	            int tempHighestScore = sp.getInt("record_block", 10);
	            if (tempHighestScore >= mHighestScore){
	    			mHighestScore = tempHighestScore ;
	    		}else{
	    			mHighestScore = mScore ;
	    		}
	    		break;
	    	}
	    }
	    applecount = mMovingPoisonousAppleListYNegative.size();
	    for (int appleindex = 0; appleindex < applecount; appleindex++) {
	    	Coordinate c = mMovingPoisonousAppleListYNegative.get(appleindex);
	    	if (isCollisionWithSnake(c)) {
	    		mMovingAppleAnimationList.add(new DisappearAppleCoordinate(c.x , c.y ,APPLE_DISAPPEAR_COUNT_MAX));
	    		if (mSoundOn){
	    			mSoundPool.play(mSoundPoolMap.get(EAT_MOVING_POISONOUS_APPLE_SOUND), mCurrentVol, mCurrentVol, 1, 0, 1f);
	    		}
	    		mScoreShowList.add(new ScoreShowWhenGot( c.x * TILESIZE + mXOffset, 
						c.y * TILESIZE + mYOffset ,ScoreShowWhenGot.MOVING_POISONOUS_SCORE, SCORE_SHOW_COUNT_MAX));  
	    		mMovingPoisonousAppleListYNegative.remove(c);
	    		mScore += ScoreShowWhenGot.MOVING_POISONOUS_SCORE;
	    		//avoid the pseudo highest score occosion.
	            SharedPreferences sp = this.getContext().getSharedPreferences("settings", 0);
	            int tempHighestScore = sp.getInt("record_block", 10);
	            if (tempHighestScore >= mHighestScore){
	    			mHighestScore = tempHighestScore ;
	    		}else{
	    			mHighestScore = mScore ;
	    		}
	    		break;
	    	}
	    }
	    
	    //update apples disappearing ******************
	    //normal apple
	    for(int i = 0 ; i!= mNormalAppleAnimationList.size() ; i++){
			  	mNormalAppleAnimationList.get(i).lifeTimePast();
		    	if(mNormalAppleAnimationList.get(i).isDeath()){
		    			mNormalAppleAnimationList.remove(i);
		    			break;
		    		}
				}
	    
	   for(int i = 0 ; i!= mBlockAppleAnimationList.size() ; i++){
	    	mBlockAppleAnimationList.get(i).lifeTimePast();
	    	if(mBlockAppleAnimationList.get(i).isDeath()){
	    			mBlockAppleAnimationList.remove(i);
	    			break;
	    		}
			}
    	
	   for(int i = 0 ; i!= mMovingAppleAnimationList.size() ; i++){
	    	mMovingAppleAnimationList.get(i).lifeTimePast();
	    	if(mMovingAppleAnimationList.get(i).isDeath()){
	    			mMovingAppleAnimationList.remove(i);
	    			break;
	    		}
			}
	}
	
	@Override
	protected void updateScore() {
		ScoreShowWhenGot tempScoreShow ;
		int count = mScoreShowList.size() ;
		for(int i = 0; i!= count ; i++){
				tempScoreShow = mScoreShowList.get(i);
				tempScoreShow.lifeTimePast();
    		if (!tempScoreShow.finished()){
					tempScoreShow.lifeTimePast();
    		}else{
    				switch(tempScoreShow.mPic){
    					case ScoreShowWhenGot.NORMAL_SCORE:
    						 mScore += NORMAL_SCORE;
    			                
    			                if (mScore > mHighestScore) { 
    			                	mHighestScore = mScore ; 
    			                	if (!mJustBreakRecord){ 
    			                		mJustBreakRecord = true ;
    			                		if (mSoundOn){
    			                			mSoundPool.play(mSoundPoolMap.get(BreakRecordSound), 
    			            						mCurrentVol, mCurrentVol, 1, 0, 1f);
    			                								}
    			                							}
    			                						}
		    						mScoreShowList.remove(i);
										i -- ;
										count -- ;
										break;
										
    					case ScoreShowWhenGot.BLOCK_SCORE :
    						mScore += BLOCK_SCORE;
    			    		
    			    		mEatBlockAppleCount ++ ;
    			    		if (mEatBlockAppleCount == EATBLOCKUPLIMIT ){ //rebuild the blocks
    			    			buildBlock(1);
    			    		}
    			    		
    			    		
    			    		//avoid the pseudo highest score occusion.
    			            SharedPreferences sp = this.getContext().getSharedPreferences("settings", 0);
    			            int mode=sp.getInt("mode", MainActivity.Classic_Game);
    			            int preRecord = sp.getInt("record_block", 10);
    			            if (preRecord >= mHighestScore){
    			    			mHighestScore = preRecord ;
    			    		}else if(preRecord >= mScore){
    			    			mHighestScore = preRecord; 
    			    		}else{
    			    			mHighestScore = mScore ;
    			    		}
    						
    						mScoreShowList.remove(i);
										i -- ;
										count -- ;
										break;
    				
    					default:
    								break;
    					}			
    				
    			}
    	}
		
	}

	@Override
	protected void drawApplesAndScores() {
		//**************APPLE******************** 
		Coordinate  tempCoord ;
		int drawCount = mNormalAppleList.size();
		for (int i = 0 ; i < drawCount ; i++){
			tempCoord = mNormalAppleList.get(i);
				Log.e("i",""+i);
				Log.e("APPLE",""+((FlickerAppleCoordinate.getCondition()+i*7)%APPLE_DISAPPEAR_COUNT_MAX));
			mCanvas.drawBitmap(mNormalAppleBitmap, mXOffset + tempCoord.x * TILESIZE, 
						mYOffset + tempCoord.y * TILESIZE , 
								mDisappearAppleTranspPaint[(FlickerAppleCoordinate.getCondition()+i*7)%APPLE_DISAPPEAR_COUNT_MAX]);
			
			}
		
		
		drawCount = mBlockAppleList.size();
		for (int i = 0 ; i < drawCount ; i++){
			tempCoord = mBlockAppleList.get(i);
		
			mCanvas.drawBitmap(mBlockAppleBitmap, mXOffset + tempCoord.x * TILESIZE, 
						mYOffset + tempCoord.y * TILESIZE , mPaint);
		}
		
		drawCount = mMovingPoisonousAppleListYNegative.size();
		for (int i = 0 ; i < drawCount ; i++){
			tempCoord = mMovingPoisonousAppleListYNegative.get(i);
			mCanvas.drawBitmap(mMovingPoisonousAppleBitmap,mXOffset + tempCoord.x * TILESIZE, 
						mYOffset + tempCoord.y * TILESIZE , mPaint);
		}
		drawCount = mMovingPoisonousAppleListXNegative.size();
		for (int i = 0 ; i < drawCount ; i++){
			tempCoord = mMovingPoisonousAppleListXNegative.get(i);
			mCanvas.drawBitmap(mMovingPoisonousAppleBitmap, mXOffset + tempCoord.x * TILESIZE, 
						mYOffset + tempCoord.y * TILESIZE , mPaint);
		}
		drawCount = mMovingPoisonousAppleListYPositive.size();
		for (int i = 0 ; i < drawCount ; i++){
			tempCoord = mMovingPoisonousAppleListYPositive.get(i);
			mCanvas.drawBitmap(mMovingPoisonousAppleBitmap,mXOffset + tempCoord.x * TILESIZE, 
						mYOffset + tempCoord.y * TILESIZE , mPaint);
		}
		drawCount = mMovingPoisonousAppleListXPositive.size();
		for (int i = 0 ; i < drawCount ; i++){
			tempCoord = mMovingPoisonousAppleListXPositive.get(i);
			mCanvas.drawBitmap(mMovingPoisonousAppleBitmap, mXOffset + tempCoord.x * TILESIZE, 
						mYOffset + tempCoord.y * TILESIZE , mPaint);
		}
		
		// disappearing apples ****************
		// normal apple
		drawCount = mNormalAppleAnimationList.size();
		for (int i = 0 ; i < drawCount ; i++){
			mCanvas.drawBitmap(mAppleDisappearBitmap,
					mXOffset + mNormalAppleAnimationList.get(i).x * TILESIZE, 
					mYOffset + mNormalAppleAnimationList.get(i).y * TILESIZE , 
					mDisappearAppleTranspPaint[mNormalAppleAnimationList.get(i).getLifeTime()]); 
		 
		}
		// block apples
		drawCount = mBlockAppleAnimationList.size();
		for (int i = 0 ; i < drawCount ; i++){
			mCanvas.drawBitmap(mBlockAppleBitmap, mXOffset + mBlockAppleAnimationList.get(i).x * TILESIZE, 
						mYOffset + mBlockAppleAnimationList.get(i).y * TILESIZE ,
						mDisappearAppleTranspPaint[mBlockAppleAnimationList.get(i).getLifeTime()]);
			mCanvas.drawBitmap(mPoisonousAppleDisappearBitmap,
					mXOffset + mBlockAppleAnimationList.get(i).x * TILESIZE, 
					mYOffset + mBlockAppleAnimationList.get(i).y * TILESIZE , 
					mDisappearAppleTranspPaint[mBlockAppleAnimationList.get(i).getLifeTime()]); 
		 
		}
		
		// moving apples
		drawCount = mMovingAppleAnimationList.size();
		for (int i = 0 ; i < drawCount ; i++){
			mCanvas.drawBitmap(mMovingAppleDisappearBitmap,
					mXOffset + mMovingAppleAnimationList.get(i).x * TILESIZE, 
					mYOffset + mMovingAppleAnimationList.get(i).y * TILESIZE , 
					mDisappearAppleTranspPaint[mMovingAppleAnimationList.get(i).getLifeTime()]); 
		}
		
		
		//********************GOT SCORE*******************
		drawCount = mScoreShowList.size();
		ScoreShowWhenGot tempScoreShow;
		for (int i = 0 ; i < drawCount ; i++){
			tempScoreShow = mScoreShowList.get(i);
			if (!tempScoreShow.finished()){
				switch(tempScoreShow.mPic){
				case ScoreShowWhenGot.NORMAL_SCORE :
					drawScore(mNormalAppleScoreBitmap ,tempScoreShow.mPosition.x , tempScoreShow.mPosition.y);
					break;
				case ScoreShowWhenGot.BLOCK_SCORE : 
					drawScore(mBlockAppleScoreBitmap ,tempScoreShow.mPosition.x , tempScoreShow.mPosition.y);
					break;
				case ScoreShowWhenGot.MOVING_POISONOUS_SCORE :
					drawScore(mMovingPoisonousAppleScoreBitmap ,tempScoreShow.mPosition.x , tempScoreShow.mPosition.y);	
					break;
				default:
					break;
				}
			}
		}
	}

	private void drawScore(Bitmap bitmap , int xPos , int yPos){
		mCanvas.drawBitmap(bitmap, 
				(xPos > (mWidth - 6 * TILESIZE))?  (xPos  - TILESIZE) : xPos  ,
				(yPos > (mHeight - 6 * TILESIZE))? (yPos  - TILESIZE) : yPos  ,
				mPaint);	
	}

	protected  SensorEventListener sensorEventListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int accuracy) { }
		public void onSensorChanged(SensorEvent event) {
			if(event.values[0]>TOLERANCE ||event.values[0]< -TOLERANCE ){
				mXVelocity = SENSITIVE * event.values[0];  
			}
			if(event.values[1]>TOLERANCE||event.values[1]< -TOLERANCE){
				mYVelocity =  SENSITIVE * event.values[1];
			}
		}
	};
}