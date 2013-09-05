package com.snake;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;

public class NormalGameSurfaceView extends GameBasicSurfaceView {
//BEGIN
	//these are configurations for this game , change it by yourself
    //SCORE
    private static final int NORMAL_SCORE = 2;
    private static final int SUPER_SCORE = 4;
    private static final int POISONOUS_SCORE = -5;
    private static final int UNBELIEVABLE_SCORE = 6 ;
    private static final int TIME_APPLE_TIME = 5000 ; //the time added when eat a time apple
    
    private static final int NORMAL_APPLE_COUNT_IN_ROUND_1 = 3 ;
    private static final int NORMAL_APPLE_COUNT_IN_ROUND_2 = 4 ;
    private static final int NORMAL_APPLE_COUTN_IN_ROUND_3 = 5 ;
    private static final int NORMAL_APPLE_COUNT_IN_ROUND_4 = 6 ;

    private static final int SUPER_APPLE_COUNT_IN_ROUND_1 = 2 ;
    private static final int SUPER_APPLE_COUNT_IN_ROUND_2 = 3 ;
    private static final int SUPER_APPLE_COUTN_IN_ROUND_3 = 4 ;
    private static final int SUPER_APPLE_COUNT_IN_ROUND_4 = 5 ;
    
    private static final int EAT_APPLE_COUNT_IN_ROUND_1 = 3 ;
    private static final int EAT_APPLE_COUNT_IN_ROUND_2 = 4 ;
    private static final int EAT_APPLE_COUTN_IN_ROUND_3 = 5 ;
    private static final int EAT_APPLE_COUNT_IN_ROUND_4 = 6 ;
    
    private static final int APPLE_DISAPPEAR_COUNT_MAX = 18 ;
    private static final int SCORE_SHOW_COUNT_MAX = 30;
    private static final int APPLE_FLICKER_COUNT_MAX = 20 ;
    
//END
    
    private static final int EAT_APPLE_SOUND = 1 ;
    private static final int EAT_POISONOUS_APPLE_SOUND = 2;
    private static final int EAT_UNBELIEVABLE_APPLE_SOUND = 3;

    private static Bitmap  mAppleBitmap , mPoisonousAppleBitmap , 
																		mSuperAppleBitmap ,mUnbelievableAppleBitmap , 
																		mTimeAppleBitmap , mPoisonousAppleDisappearBitmap ,
																		mAppleDisappearBitmap, mSuperAppleDisappearBitmap ,
																		mUnbelievableAppleDisappearBitmap , mTimeAppleDisappearBitmap;
    Bitmap[] mScoreBitmaps = new Bitmap[5];
	
    /*  private static Bitmap mNormalScoreBitmap , mSuperScoreBitmap ,
																	mUnbelievableScoreBitmap , mPoisonousScoreBitmap ,
																	mAddTimeBitmap;*/
    Paint mDisappearAppleTranspPaint[] = new Paint[APPLE_DISAPPEAR_COUNT_MAX];
    Paint mScoreTranspPaint[] = new Paint[SCORE_SHOW_COUNT_MAX];
    Paint mFlickerAppleTranspPaint[] = new Paint[APPLE_FLICKER_COUNT_MAX];
    
    //the number of normal apples , super apples and the apple can be eaten in a round. 
    private int mNormalAppleCount = 3 ; 
    private int mSuperAppleCount  = 2 ; 
    private int mRoundAppleCount = 3;
    //counter in for apple round
    private int mEatenAppleCount = 0;

    //Coordinate in apple list is relative coordinate for tiles
    private ArrayList<FlickerAppleCoordinate> mNormalAppleList = new ArrayList<FlickerAppleCoordinate>();
    private ArrayList<FlickerAppleCoordinate> mSuperAppleList = new ArrayList<FlickerAppleCoordinate>();
    private ArrayList<FlickerAppleCoordinate> mPoisonousAppleList = new ArrayList<FlickerAppleCoordinate>();
    	
    private ArrayList<Coordinate> mUnbelievableAppleList1 = new ArrayList<Coordinate>();
    private ArrayList<Coordinate> mUnbelievableAppleList2 = new ArrayList<Coordinate>();
    
    private ArrayList<DisappearAppleCoordinate> mUnbelievableAppleAnimationList = new ArrayList<DisappearAppleCoordinate>();
    private ArrayList<DisappearAppleCoordinate> mNormalAppleAnimationList = new ArrayList<DisappearAppleCoordinate>();
    private ArrayList<DisappearAppleCoordinate> mSuperAppleAnimationList = new ArrayList<DisappearAppleCoordinate>();
    private ArrayList<DisappearAppleCoordinate> mPoisonousAppleAnimationList = new ArrayList<DisappearAppleCoordinate>();
    private ArrayList<DisappearAppleCoordinate> mTimeAppleAnimationList = new ArrayList<DisappearAppleCoordinate>();
    
   
    
    private Coordinate mTimeApple1 = new Coordinate(-1,-1);
    private Coordinate mTimeApple2 =  new Coordinate(-1,-1);
    
    //record the possible positions for poisonous apple arround a super apple
    private static final int[][] mPoisonedAppleRandomArray = 
    	new int[][]   {{-1,-1} , { 0,-1} , { 1,-1},
    				   {-1, 0} ,           { 1, 0},
				   	   {-1, 1} , { 0, 1} , { 1,1}};   
    
    
    //record the start time of unbelievable apple
    private int[][] mRandomUnbelievableApple ;
    private int[][] mRandomTimeApple ;


    
    private int mUnbelievableAppleMoveLock = 0;  //the lock controlling the move of unbelievable apples
    private int mTimeAppleMoveLock = 0 ;
    
	public NormalGameSurfaceView(Context context,
			AttributeSet paramAttributeSet) {
		super(context, paramAttributeSet);
	}

	public NormalGameSurfaceView(Context context) {
		super(context);
	}
	
	@Override
	public void initNewGameElse() {
	    mUnbelievableAppleMoveLock = -1 ; //lock the Unbelievable apple。
        mTimeAppleMoveLock = -1;
	}

	@Override
	protected void initApple() {
		mNormalAppleCount = 3 ; 
        mSuperAppleCount  = 2 ; 
        mRoundAppleCount = 3;
        mUnbelievableAppleList1.clear(); //X方向
        mUnbelievableAppleList2.clear(); //Y方向
        
        //when x == 0 ,it's the record of first time of unbelievable apple, x==1 is the second time.
        // y == 0 keeps the time of unbelievable apples' coming
        //y == 1 and y == 2 notes y of coordinate the apples moving horizon 
        // y == 3 and y ==4 notes x of coordinate the apples moving vertical
        mRandomUnbelievableApple = 
        	new int[][] { { RNG.nextInt(15000)+40000 , RNG.nextInt(mYTileCount-1)+1 , 
        								RNG.nextInt(mYTileCount-1)+1 , RNG.nextInt(mXTileCount-1)+1 ,
        								RNG.nextInt(mXTileCount-1)+1}, 
        				  { RNG.nextInt(15000)+10000 , RNG.nextInt(mYTileCount-1)+1 , 
        								RNG.nextInt(mYTileCount-1)+1 , RNG.nextInt(mXTileCount-1)+1 ,
        								RNG.nextInt(mXTileCount-1)+1},
        				};	
        
        mRandomTimeApple = 
        	new int[][] { { RNG.nextInt(15000)+40000 , RNG.nextInt(mYTileCount-1)+1 , 
        								RNG.nextInt(mXTileCount-1)+1}, 
        				  { RNG.nextInt(15000)+10000 , RNG.nextInt(mYTileCount-1)+1 , 
        								RNG.nextInt(mXTileCount-1)+1},
        				};	

        initAppleRound();
	}


	private void initAppleRound(){
		mNormalAppleList.clear();
		mSuperAppleList.clear();
		mPoisonousAppleList.clear();
		
		int tempNormalAppleCount = mNormalAppleCount , tempSuperAppleCount = mSuperAppleCount;
		for (int i = 0 ; i != tempNormalAppleCount ; i ++){
		   addRandomApple();
        }
		for (int i = 0 ; i != tempSuperAppleCount ; i ++){
			addRandomSuperAppleWithPoisonedOne();
        }
	}

	@Override
	protected void eatingApplesCheck(Coordinate headCoord) {
				//normal apple
        int applecount = mNormalAppleList.size();
        for (int appleindex = 0; appleindex < applecount; appleindex++) {
        	Coordinate c = mNormalAppleList.get(appleindex);
            if (IsEatingApple(headCoord,c)) {
            		mNormalAppleAnimationList.add(new DisappearAppleCoordinate(c.x,c.y ,APPLE_DISAPPEAR_COUNT_MAX));
            		if (mSoundOn){
	            		mSoundPool.play(mSoundPoolMap.get(EAT_APPLE_SOUND), 
	            											mCurrentVol, mCurrentVol, 1, 0, 1f);
	            						}
	            	mScoreShowList.add(new ScoreShowWhenGot( headCoord.x, 
	            							headCoord.y ,ScoreShowWhenGot.NORMAL_SCORE , SCORE_SHOW_COUNT_MAX));  
	            	mNormalAppleList.remove(c);
	             if (mEatenAppleCount == mRoundAppleCount - 1){
	                	initAppleRound();
	                  	mEatenAppleCount = 0;
	                					}
	             else{
	                  	mEatenAppleCount ++ ;
	             						}
	             break; //这句话没加，让我找了好久的错误！ ArrayList 慎用remove(),remove后，整个ArrayList就变化了。
	            					}
        				}
        // Super Apple
	    applecount = mSuperAppleList.size();
	    for (int appleindex = 0; appleindex < applecount; appleindex++) {
	    	Coordinate c = mSuperAppleList.get(appleindex);
	    	if (IsEatingApple(headCoord,c)) {
	    		mSuperAppleAnimationList.add(new DisappearAppleCoordinate(c.x,c.y ,APPLE_DISAPPEAR_COUNT_MAX));
	    		if (mSoundOn){ 
	    			mSoundPool.play(mSoundPoolMap.get(EAT_APPLE_SOUND), mCurrentVol,
	    					mCurrentVol, 1, 0, 1f);
	    		}
	    		mScoreShowList.add(new ScoreShowWhenGot(headCoord.x , headCoord.y , 
	    					ScoreShowWhenGot.SUPER_SCORE ,  SCORE_SHOW_COUNT_MAX));
	    		mSuperAppleList.remove(c);
	    		if (mEatenAppleCount == mRoundAppleCount - 1){
	    			initAppleRound();
	    			mEatenAppleCount = 0;
	    		}
	    		else{
	    			mEatenAppleCount ++ ;
	    		}
	    		break; //这句话没加，让我找了好久的错误！ ArrayList 慎用remove(),remove后，整个ArrayList就变化了。
	    	}
	    }
	  
	    // Poisonous Apple
	    applecount = mPoisonousAppleList.size();
	    for (int appleindex = 0; appleindex < applecount; appleindex++) {
	    	Coordinate c = mPoisonousAppleList.get(appleindex);
	    	if (IsEatingApple(headCoord,c)) {
	    		mPoisonousAppleAnimationList.add(new DisappearAppleCoordinate(c.x,c.y,APPLE_DISAPPEAR_COUNT_MAX ));
	    		if (mSoundOn){
	    			mSoundPool.play(mSoundPoolMap.get(EAT_POISONOUS_APPLE_SOUND), mCurrentVol,
	    					mCurrentVol, 1, 0, 1f);
	    			mVibrator.vibrate(500);
	    		}
	    		mScoreShowList.add(new ScoreShowWhenGot(headCoord.x , headCoord.y , 
	    				ScoreShowWhenGot.POISONOUS_SCORE, SCORE_SHOW_COUNT_MAX));
	    		mPoisonousAppleList.remove(c);
	    		mEatenAppleCount = 0;
	    		mScore += POISONOUS_SCORE;
	    		
	    		//avoid the pseudo highest score occusion.
	            SharedPreferences sp = this.getContext().getSharedPreferences("settings", 0);
	            int mode=sp.getInt("mode", MainActivity.Classic_Game);
	            int preRecord ;
	            if (mode == MainActivity.Reverse_Game){
	            	preRecord = sp.getInt("record_reverse", 10);
	            }else{
	            	preRecord = sp.getInt("record_classic", 10);
	            }
	            
	            if (preRecord >= mHighestScore){
	    			mHighestScore = preRecord ;
	    		}else if(preRecord >= mScore){
	    			mHighestScore = preRecord; 
	    		}else{
	    			mHighestScore = mScore ;
	    		}
	    		initAppleRound();
	    		break; //if eat one apple in this time , we don't have to 
	    				// check for all other poisonous apples, it's a <practical issue>
	    			// BE CAUTIOS when remove members from a array list
	    	}
	    }
	      
	    // Unbelievable Apple
	    applecount = mUnbelievableAppleList1.size();
	    for (int appleindex = 0; appleindex < applecount; appleindex++) {
	    	Coordinate c = mUnbelievableAppleList1.get(appleindex);
	    	if (IsEatingApple(headCoord,c)) {
	    		mUnbelievableAppleAnimationList.add(new DisappearAppleCoordinate(c.x,c.y,APPLE_DISAPPEAR_COUNT_MAX ));
	    		if (mSoundOn){
	    			mSoundPool.play(mSoundPoolMap.get(EAT_UNBELIEVABLE_APPLE_SOUND), 
	    						mCurrentVol, mCurrentVol, 1, 0, 1f);
	    		}
	    		mUnbelievableAppleList1.remove(c);
	    		break; 
	    	}
	    }
	    applecount = mUnbelievableAppleList2.size();
	    for (int appleindex = 0; appleindex < applecount; appleindex++) {
	    	Coordinate c = mUnbelievableAppleList2.get(appleindex);
	    	if (IsEatingApple(headCoord,c)) {
	    		mUnbelievableAppleAnimationList.add(new DisappearAppleCoordinate(c.x,c.y ,APPLE_DISAPPEAR_COUNT_MAX));
	    		if (mSoundOn){
	    			mSoundPool.play(mSoundPoolMap.get(EAT_UNBELIEVABLE_APPLE_SOUND), mCurrentVol, mCurrentVol, 1, 0, 1f);
	    		}	  
	    		mScoreShowList.add(new ScoreShowWhenGot(headCoord.x , headCoord.y , 
	    						ScoreShowWhenGot.UNBELIEVABLE_SCORE, SCORE_SHOW_COUNT_MAX));
	    		mUnbelievableAppleList2.remove(c);
	    		mScore += UNBELIEVABLE_SCORE;
	    		if (mScore > mHighestScore) {
                	mHighestScore = mScore ; 
                	if (!mJustBreakRecord){ // the time just break the record
                		mJustBreakRecord = true ;
                		if (mSoundOn){
                			mSoundPool.play(mSoundPoolMap.get(BreakRecordSound), 
            						mCurrentVol, mCurrentVol, 1, 0, 1f);
                		}
                	}
                }
	    		break;
	    	}
	    }
	    // time apple
	    if (mTimeApple1.x != -1){
	    	if (IsEatingApple(headCoord, mTimeApple1)){
	    		mTimeAppleAnimationList.add(new DisappearAppleCoordinate(mTimeApple1.x,mTimeApple1.y ,APPLE_DISAPPEAR_COUNT_MAX));
	    		
	    		if (mSoundOn){
	    			mSoundPool.play(mSoundPoolMap.get(EAT_UNBELIEVABLE_APPLE_SOUND), mCurrentVol, mCurrentVol, 1, 0, 1f);
	    		}	  
	    		mScoreShowList.add(new ScoreShowWhenGot(headCoord.x , headCoord.y , 	
	    					ScoreShowWhenGot.ADD_TIME, SCORE_SHOW_COUNT_MAX));
	  //  		mLastMiliTime += TIME_APPLE_TIME;
	    		mTimeApple1.x = -1;
	    		mTimeApple1.y = -1;
	    	}
	    }
	    if (mTimeApple2.y != -1){
	    	if (IsEatingApple(headCoord, mTimeApple2)){
	    		mTimeAppleAnimationList.add(new DisappearAppleCoordinate(mTimeApple2.x,mTimeApple2.y,APPLE_DISAPPEAR_COUNT_MAX));
	    		if (mSoundOn){
	    			mSoundPool.play(mSoundPoolMap.get(EAT_UNBELIEVABLE_APPLE_SOUND), mCurrentVol, mCurrentVol, 1, 0, 1f);
	    		}	  
	    		mScoreShowList.add(new ScoreShowWhenGot(headCoord.x , headCoord.y ,
	    					ScoreShowWhenGot.ADD_TIME, SCORE_SHOW_COUNT_MAX));
	//    		mLastMiliTime += TIME_APPLE_TIME;
	    		mTimeApple2.x = -1;
	    		mTimeApple2.y = -1;
	    	}
	    }
	}
	
	@Override
	protected void updateApples() {
		//check for UnbelievableApple  ****************************
    	int[][] tempRandomUnbelievableApple = mRandomUnbelievableApple;
    	int[][] tempRandomTimeApple = mRandomTimeApple;
    	ArrayList<Coordinate> tempUnbelievableAppleList1 = mUnbelievableAppleList1 ;
    	ArrayList<Coordinate> tempUnbelievableAppleList2 = mUnbelievableAppleList2 ;
    	
    	int tempUnbelievableAppleMoveClock = mUnbelievableAppleMoveLock ;
    	for (int i = 0 ; i < 2 ; i ++){
    		if (mLastMiliTime < tempRandomUnbelievableApple[i][0]){
    			tempUnbelievableAppleList1.add(new Coordinate(1,tempRandomUnbelievableApple[i][1]));
    			tempUnbelievableAppleList1.add(new Coordinate(1,tempRandomUnbelievableApple[i][2]));
    			tempUnbelievableAppleList2.add(new Coordinate(tempRandomUnbelievableApple[i][3],1));   	
    			tempUnbelievableAppleList2.add(new Coordinate(tempRandomUnbelievableApple[i][4],1));
        		tempRandomUnbelievableApple[i][0] = -1 ;//this is the time when unbelievable apple come out
        		tempUnbelievableAppleMoveClock = 0 ; 
        		break;
    		}
    	}
    	if (tempUnbelievableAppleMoveClock >= 0 ){  //unbelievable apple is running
    		tempUnbelievableAppleMoveClock ++ ;
    		if (tempUnbelievableAppleMoveClock == 8){
    			tempUnbelievableAppleMoveClock = 0; //do not move apple until mUnbelievableAppleMoveLock is 10
    			//X direction
    			int unbelievableAppleCount = tempUnbelievableAppleList1.size();
    			for (int i = 0 ; i < unbelievableAppleCount ; i ++){
    				if ( ++(tempUnbelievableAppleList1.get(i).x) >= mXTileCount){
    					tempUnbelievableAppleList1.clear(); 
    					break ;
    				}
    			}
    			//Y direction
    			unbelievableAppleCount = tempUnbelievableAppleList2.size();
    			for (int i = 0 ; i < unbelievableAppleCount ; i ++){
    				if ( ++(tempUnbelievableAppleList2.get(i).y) >= mYTileCount){
    					tempUnbelievableAppleList2.clear(); 
    					tempUnbelievableAppleMoveClock = -1 ; //mean that unbelievable apple is not running
    					break ; 
    				}
    			}
    		}
    	}
    	mUnbelievableAppleMoveLock = tempUnbelievableAppleMoveClock;
    	
    	//update time apples ****************************
    	int tempTimeAppleMoveClock = mTimeAppleMoveLock ;
    	for (int i = 0 ; i < 2 ; i ++){
    		if (mLastMiliTime < tempRandomTimeApple[i][0]){
    			mTimeApple1 = new Coordinate(1,tempRandomTimeApple[i][1]);   //move in y direction.
    			mTimeApple2 = new Coordinate(tempRandomTimeApple[i][2], 1);  //move in x direction.
    			tempRandomTimeApple[i][0] = -1 ;//this is the time when unbelievable apple come out
        		tempTimeAppleMoveClock = 0 ; 
        		break;
    			}
    		}
    	if (tempTimeAppleMoveClock >= 0 ){  //unbelievable apple is running
    		tempTimeAppleMoveClock ++ ;
    		if (tempTimeAppleMoveClock == 4){
    			tempTimeAppleMoveClock = 0; //do not move apple until mUnbelievableAppleMoveLock is 10
    			if (mTimeApple1.x != -1){  // mTimeApple1 exist 
	    			if ((++mTimeApple1.x) >= mXTileCount){  // when out of bound , it disappeared
	    				mTimeApple1.x = -1 ;  //means TimeApple1 is not exist any more
	    				mTimeApple1.y = -1 ;
	    				}
    				}
	    		if (mTimeApple2.y != -1){
	    			if ((++mTimeApple2.y) >= mYTileCount){
	    				mTimeApple2.y = -1 ;
	    				mTimeApple2.x = -1 ;
	    				tempTimeAppleMoveClock = -1;
	    				}
	    			}	
    			}
    		}
    	 mTimeAppleMoveLock = tempTimeAppleMoveClock;
    	 		
    	 // flicker apple ****************************
    	 FlickerAppleCoordinate.lifeTimePast();
    	 
    	 //apple disappear when eaten ****************************
    	//normal
		  for(int i = 0 ; i!= mNormalAppleAnimationList.size() ; i++){
			  mNormalAppleAnimationList.get(i).lifeTimePast();
		    		if(mNormalAppleAnimationList.get(i).isDeath()){
		    			mNormalAppleAnimationList.remove(i);
		    			break;
		    				}
						}
		  
		    // super
		  for(int i = 0 ; i!= mSuperAppleAnimationList.size() ; i++){
			  mSuperAppleAnimationList.get(i).lifeTimePast();
	    		if(mSuperAppleAnimationList.get(i).isDeath()){
	    			mSuperAppleAnimationList.remove(i);
	    			break;
	    			}
				}
		  // poisonous
			for(int i = 0 ; i!= mPoisonousAppleAnimationList.size() ; i++){
					mPoisonousAppleAnimationList.get(i).lifeTimePast();
					if(mPoisonousAppleAnimationList.get(i).isDeath()){
						mPoisonousAppleAnimationList.remove(i);
						break;
					}
			}
		  //unbelievable
			for(int i = 0 ; i!= mUnbelievableAppleAnimationList.size() ; i++){
					mUnbelievableAppleAnimationList.get(i).lifeTimePast();
					if(mUnbelievableAppleAnimationList.get(i).isDeath()){
						mUnbelievableAppleAnimationList.remove(i);
						break;
					}
			}
			//time
	    for(int i = 0 ; i!= mTimeAppleAnimationList.size() ; i++){
	    		mTimeAppleAnimationList.get(i).lifeTimePast();
	    		if(mTimeAppleAnimationList.get(i).isDeath()){
	    			mTimeAppleAnimationList.remove(i);
	    			break;
	    			}
				}
			  	
		 
    	 
    	//check apple round
    	boolean flag = false ; 
    	if (mScore < 30  && mNormalAppleCount != NORMAL_APPLE_COUNT_IN_ROUND_1){
            mNormalAppleCount = NORMAL_APPLE_COUNT_IN_ROUND_1 ; 
            mSuperAppleCount  = SUPER_APPLE_COUNT_IN_ROUND_1 ; 
            mRoundAppleCount = EAT_APPLE_COUNT_IN_ROUND_1 ;
            flag = true ;
    	}else if (mScore >= 30 && mScore < 60 && mNormalAppleCount != NORMAL_APPLE_COUNT_IN_ROUND_2){
            mNormalAppleCount = NORMAL_APPLE_COUNT_IN_ROUND_2 ; 
            mSuperAppleCount  = SUPER_APPLE_COUNT_IN_ROUND_2 ; 
            mRoundAppleCount = EAT_APPLE_COUNT_IN_ROUND_2;
            flag = true ;
    	}else if (mScore >=60 && mScore < 90 && mNormalAppleCount != NORMAL_APPLE_COUTN_IN_ROUND_3){
    		  mNormalAppleCount = NORMAL_APPLE_COUTN_IN_ROUND_3 ; 
              mSuperAppleCount  = SUPER_APPLE_COUTN_IN_ROUND_3; 
              mRoundAppleCount = EAT_APPLE_COUTN_IN_ROUND_3;
            flag = true ;
    	}else if (mScore >= 90 && mNormalAppleCount != NORMAL_APPLE_COUNT_IN_ROUND_4){
    		  mNormalAppleCount = NORMAL_APPLE_COUNT_IN_ROUND_4 ; 
              mSuperAppleCount  = SUPER_APPLE_COUNT_IN_ROUND_4 ; 
              mRoundAppleCount = EAT_APPLE_COUNT_IN_ROUND_4;
            flag = true ;
    	}
    	
    	//restart the round
    	if (flag){  
    		initAppleRound();
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
	    					case ScoreShowWhenGot.SUPER_SCORE :
	    								mScore += SUPER_SCORE;
	    								if (mScore > mHighestScore) {
	    									mHighestScore = mScore ; 
	    									if (!mJustBreakRecord){ // the time just break the record
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
	    					case ScoreShowWhenGot.UNBELIEVABLE_SCORE:
	    								mScore += UNBELIEVABLE_SCORE;
	    								if (mScore > mHighestScore) {
	    									mHighestScore = mScore ; 
	    									if (!mJustBreakRecord){ // the time just break the record
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
	    					case ScoreShowWhenGot.POISONOUS_SCORE :
			    						mScoreShowList.remove(i);
											i -- ;
											count -- ;
											break;
	    					case ScoreShowWhenGot.ADD_TIME:
	    			    			mLastMiliTime += TIME_APPLE_TIME;
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
		
		//**************normal apples******************** 
		Coordinate  tempCoord ;
		int drawCount ;
		
		if(FlickerAppleCoordinate.isFlickerFinish()){
			drawCount = mNormalAppleList.size();
			for (int i = 0 ; i < drawCount ; i++){
				tempCoord = mNormalAppleList.get(i);
				mCanvas.drawBitmap(mAppleBitmap, mXOffset + tempCoord.x * TILESIZE, 
							mYOffset + tempCoord.y * TILESIZE , mPaint);
			}
			
			drawCount = mSuperAppleList.size();
			for (int i = 0 ; i < drawCount ; i++){
				tempCoord = mSuperAppleList.get(i);
				mCanvas.drawBitmap(mSuperAppleBitmap, mXOffset + tempCoord.x * TILESIZE,
							mYOffset + tempCoord.y * TILESIZE , mPaint);
			}
			
			drawCount = mPoisonousAppleList.size();
			for (int i = 0 ; i < drawCount ; i++){
				tempCoord = mPoisonousAppleList.get(i);
				mCanvas.drawBitmap(mPoisonousAppleBitmap, mXOffset + tempCoord.x * TILESIZE, 
							mYOffset + tempCoord.y * TILESIZE , mPaint);
			}
		}else{
			drawCount = mNormalAppleList.size();
			for (int i = 0 ; i < drawCount ; i++){
				tempCoord = mNormalAppleList.get(i);
				mCanvas.drawBitmap(mAppleBitmap, mXOffset + tempCoord.x * TILESIZE, 
							mYOffset + tempCoord.y * TILESIZE ,
							mFlickerAppleTranspPaint[FlickerAppleCoordinate.getFlickerLifeTime()]);
			}
			
			drawCount = mSuperAppleList.size();
			for (int i = 0 ; i < drawCount ; i++){
				tempCoord = mSuperAppleList.get(i);
				mCanvas.drawBitmap(mSuperAppleBitmap, mXOffset + tempCoord.x * TILESIZE,
							mYOffset + tempCoord.y * TILESIZE , 
							mFlickerAppleTranspPaint[FlickerAppleCoordinate.getFlickerLifeTime()]);
			}
			
			drawCount = mPoisonousAppleList.size();
			for (int i = 0 ; i < drawCount ; i++){
				tempCoord = mPoisonousAppleList.get(i);
				mCanvas.drawBitmap(mPoisonousAppleBitmap, mXOffset + tempCoord.x * TILESIZE, 
							mYOffset + tempCoord.y * TILESIZE ,
							mFlickerAppleTranspPaint[FlickerAppleCoordinate.getFlickerLifeTime()]);
			}
		}
		
		// moving unbelievable apples
		drawCount = mUnbelievableAppleList1.size();
		for (int i = 0 ; i < drawCount ; i++){
			tempCoord = mUnbelievableAppleList1.get(i);
			mCanvas.drawBitmap(mUnbelievableAppleBitmap,mXOffset + tempCoord.x * TILESIZE, 
						mYOffset + tempCoord.y * TILESIZE , mPaint);
		}
		drawCount = mUnbelievableAppleList2.size();
		for (int i = 0 ; i < drawCount ; i++){
			tempCoord = mUnbelievableAppleList2.get(i);
			mCanvas.drawBitmap(mUnbelievableAppleBitmap, mXOffset + tempCoord.x * TILESIZE, 
						mYOffset + tempCoord.y * TILESIZE , mPaint);
		}
		
		// moving time apples
		if (mTimeApple1.x != -1){
			mCanvas.drawBitmap(mTimeAppleBitmap,mXOffset + mTimeApple1.x * TILESIZE, 
					mYOffset + mTimeApple1.y * TILESIZE , mPaint);
		}
		if (mTimeApple2.y != -1){
			mCanvas.drawBitmap(mTimeAppleBitmap,mXOffset + mTimeApple2.x * TILESIZE, 
					mYOffset + mTimeApple2.y * TILESIZE , mPaint);
		}
		
	
		
		//show SCORE *********************
		
		drawCount = mScoreShowList.size();
		ScoreShowWhenGot tempScoreShow;
		for (int i = 0 ; i < drawCount ; i++){
			tempScoreShow = mScoreShowList.get(i);
					drawScore(tempScoreShow.mPic,
											tempScoreShow.mPosition.x ,tempScoreShow.mPosition.y ,
											tempScoreShow.getLifeTime() );
		}
		
		//********************* animation of apple disappear***********
		for(int i = 0 ; i!= mNormalAppleAnimationList.size() ; i++){
			mCanvas.drawBitmap(mAppleDisappearBitmap,
						mXOffset + mNormalAppleAnimationList.get(i).x * TILESIZE, 
						mYOffset + mNormalAppleAnimationList.get(i).y * TILESIZE , 
						mDisappearAppleTranspPaint[mNormalAppleAnimationList.get(i).getLifeTime()]); 
			 
		 	}
		
		 
		 
		 
	  	//super
	  	for(int i = 0 ; i!= mSuperAppleAnimationList.size() ; i++){
	  		mCanvas.drawBitmap(mSuperAppleDisappearBitmap,
					mXOffset + mSuperAppleAnimationList.get(i).x * TILESIZE, 
					mYOffset + mSuperAppleAnimationList.get(i).y * TILESIZE , 
					mDisappearAppleTranspPaint[mSuperAppleAnimationList.get(i).getLifeTime()]); 
		 
	  		}
	  	 
	  	// poisonous
			for(int i = 0 ; i!= mPoisonousAppleAnimationList.size() ; i++){
		
				mCanvas.drawBitmap(mPoisonousAppleBitmap,
										mXOffset + mPoisonousAppleAnimationList.get(i).x * TILESIZE, 
										mYOffset + mPoisonousAppleAnimationList.get(i).y * TILESIZE ,
										mDisappearAppleTranspPaint[mPoisonousAppleAnimationList.get(i).getLifeTime()]);
				mCanvas.drawBitmap(mPoisonousAppleDisappearBitmap,
						mXOffset + mPoisonousAppleAnimationList.get(i).x * TILESIZE, 
						mYOffset + mPoisonousAppleAnimationList.get(i).y * TILESIZE , 
						mDisappearAppleTranspPaint[mPoisonousAppleAnimationList.get(i).getLifeTime()]); 
			 
				
			}
	  	   
	  	  //unbelievable
			for(int i = 0 ; i!= mUnbelievableAppleAnimationList.size() ; i++){
				mCanvas.drawBitmap(mUnbelievableAppleDisappearBitmap,
						mXOffset + mUnbelievableAppleAnimationList.get(i).x * TILESIZE, 
						mYOffset + mUnbelievableAppleAnimationList.get(i).y * TILESIZE , 
						mDisappearAppleTranspPaint[mUnbelievableAppleAnimationList.get(i).getLifeTime()]); 
			 
			}
	  	 //add time apple
			for(int i = 0 ; i!= mTimeAppleAnimationList.size() ; i++){
					mCanvas.drawBitmap(mTimeAppleDisappearBitmap,
						mXOffset + mTimeAppleAnimationList.get(i).x * TILESIZE, 
						mYOffset + mTimeAppleAnimationList.get(i).y * TILESIZE , 
						mDisappearAppleTranspPaint[mTimeAppleAnimationList.get(i).getLifeTime()]); 
			 
			}

	}
	
	private void drawScore(int bitmapIndex , int xPos , int yPos , int lifeTime){
		mCanvas.drawBitmap(mScoreBitmaps[bitmapIndex], 
				(xPos > (mWidth - 6 * TILESIZE))?
						 (xPos  - TILESIZE) : (xPos  ) ,
				(yPos > (mHeight - 6 * TILESIZE))?
						 (yPos  - TILESIZE) : (yPos ) ,
						 mScoreTranspPaint[lifeTime]);	
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
		      found = !collision;
			   }catch(ConcurrentModificationException e){
				   	Log.e("ConcurrentModificationException",e.toString()+"CON");
			   		}
		        }
		   if (newCoord == null) {
		   		}
		   mNormalAppleList.add(new FlickerAppleCoordinate(newCoord,APPLE_FLICKER_COUNT_MAX));
    }
    
    protected void addRandomSuperAppleWithPoisonedOne(){
    	 Coordinate newCoord = null;
    	 boolean collision = false; //check for collision
    	 boolean found = false; //用来控制随机生成的循环。
    	 
         while (!found) {
        	 //随机生成x，y两坐标
             int newX = 1 + RNG.nextInt(mXTileCount-2 );
             int newY = 1 + RNG.nextInt(mYTileCount-2 );
             newCoord = new Coordinate(newX, newY);
             collision = false;        
             try{	  
                 for (Coordinate c : mNormalAppleList){
                	 		if (c.equals(newCoord)){
                	 			collision = true;
                	 								}
                 							}
                 for (Coordinate c : mSuperAppleList){
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
         mSuperAppleList.add(new FlickerAppleCoordinate(newCoord,APPLE_FLICKER_COUNT_MAX));
        
         found = false;
         while (!found) {
        	 int Pos = RNG.nextInt(8);
             int newX = mPoisonedAppleRandomArray[Pos][0];
             int newY = mPoisonedAppleRandomArray[Pos][1];
             newCoord = new Coordinate(newCoord.x + newX, newCoord.y + newY);
             collision = false;     
             try{
	             for (Coordinate c : mNormalAppleList){
	            	 if (c.equals(newCoord)){
	            		 collision = true;
	            	 						}
	             						}  
	             for (Coordinate c : mSuperAppleList){
	            	 if (c.equals(newCoord)){
	            		 collision = true;
	            	 						}
	             						}
             }catch(ConcurrentModificationException e){
            	 		Log.e("ConcurrentModificationException",e.toString()+"CON");
             						}
             //overstep the boundry
             if (newCoord.x < 1 || newCoord.x >= mXTileCount || 
            		 newCoord.y < 1 || newCoord.y >= mYTileCount ){
            	 		collision = true ;
             						}	 
             found = !collision;
         				}
         if (newCoord == null) {
         				}
         
         mPoisonousAppleList.add(new FlickerAppleCoordinate(newCoord,APPLE_FLICKER_COUNT_MAX));
    }

	@Override
	protected void initResourceElse() {
		  //read apples picture 
		Resources r = this.getContext().getResources();
		
		Drawable drawable = r.getDrawable(R.drawable.food_2);
		mAppleBitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mAppleBitmap);    
		drawable.setBounds(0, 0, TILESIZE, TILESIZE );
		drawable.draw(canvas);	   
	
		drawable = r.getDrawable(R.drawable.food_4);
		mSuperAppleBitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mSuperAppleBitmap);    
		drawable.setBounds(0, 0, TILESIZE, TILESIZE );
		drawable.draw(canvas);	 
        
		drawable = r.getDrawable(R.drawable.food_fu5);
		mPoisonousAppleBitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mPoisonousAppleBitmap);    
		drawable.setBounds(0, 0, TILESIZE, TILESIZE );
		drawable.draw(canvas);	 
		
		drawable = r.getDrawable(R.drawable.food_6);
			mUnbelievableAppleBitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mUnbelievableAppleBitmap);    
		drawable.setBounds(0, 0, TILESIZE, TILESIZE );
		drawable.draw(canvas);	 
     
		drawable = r.getDrawable(R.drawable.food_time5);
			mTimeAppleBitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mTimeAppleBitmap);    
		drawable.setBounds(0, 0, TILESIZE, TILESIZE );
		drawable.draw(canvas);	 
		
		drawable = r.getDrawable(R.drawable.food_fu5_eaten); //TODO
		mPoisonousAppleDisappearBitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mPoisonousAppleDisappearBitmap);    
		drawable.setBounds(0, 0, TILESIZE, TILESIZE );
		drawable.draw(canvas);	 
		
		drawable = r.getDrawable(R.drawable.food_2_eaten ); //TODO
		mAppleDisappearBitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mAppleDisappearBitmap);    	
		drawable.setBounds(0, 0, TILESIZE, TILESIZE );
		drawable.draw(canvas);	 

		drawable = r.getDrawable(R.drawable.food_4_eaten); //TODO
		mSuperAppleDisappearBitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mSuperAppleDisappearBitmap);    	
		drawable.setBounds(0, 0, TILESIZE, TILESIZE );
		drawable.draw(canvas);	 

		drawable = r.getDrawable(R.drawable.food_6_eaten); //TODO
		mUnbelievableAppleDisappearBitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mUnbelievableAppleDisappearBitmap);    	
		drawable.setBounds(0, 0, TILESIZE, TILESIZE );
		drawable.draw(canvas);	 

		drawable = r.getDrawable(R.drawable.food_time5_eaten); //TODO
		mTimeAppleDisappearBitmap = Bitmap.createBitmap(TILESIZE, TILESIZE, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mTimeAppleDisappearBitmap);    	
		drawable.setBounds(0, 0, TILESIZE, TILESIZE );
		drawable.draw(canvas);	 


   int tempBigTileSize =(int) 1.8 * TILESIZE ;
        

		//normal score pic**********
		drawable = r.getDrawable(R.drawable.score_2);
		mScoreBitmaps[ScoreShowWhenGot.NORMAL_SCORE] = Bitmap.createBitmap(tempBigTileSize, tempBigTileSize, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mScoreBitmaps[ScoreShowWhenGot.NORMAL_SCORE]);    
		drawable.setBounds(0, 0, tempBigTileSize, tempBigTileSize );
		drawable.draw(canvas);	
		
		//super score pic**********
		drawable = r.getDrawable(R.drawable.score_4);
		mScoreBitmaps[ScoreShowWhenGot.SUPER_SCORE] = Bitmap.createBitmap(tempBigTileSize, tempBigTileSize, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mScoreBitmaps[ScoreShowWhenGot.SUPER_SCORE]);    
		drawable.setBounds(0, 0,tempBigTileSize, tempBigTileSize);
		drawable.draw(canvas);	
	
		// unbelievable score pic**********
		drawable = r.getDrawable(R.drawable.score_6);
		mScoreBitmaps[ScoreShowWhenGot.UNBELIEVABLE_SCORE] = Bitmap.createBitmap(tempBigTileSize, tempBigTileSize, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mScoreBitmaps[ScoreShowWhenGot.UNBELIEVABLE_SCORE]);    
		drawable.setBounds(0, 0, tempBigTileSize, tempBigTileSize);
		drawable.draw(canvas);	
		

		
		//poisonous score pic**********
		drawable = r.getDrawable(R.drawable.score_fu5);
		mScoreBitmaps[ScoreShowWhenGot.POISONOUS_SCORE] = Bitmap.createBitmap(tempBigTileSize, tempBigTileSize, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mScoreBitmaps[ScoreShowWhenGot.POISONOUS_SCORE]);    
		drawable.setBounds(0, 0, tempBigTileSize, tempBigTileSize);
		drawable.draw(canvas);	
		

		//add time picture
		drawable = r.getDrawable(R.drawable.time_5);
		mScoreBitmaps[ScoreShowWhenGot.ADD_TIME] = Bitmap.createBitmap( tempBigTileSize , tempBigTileSize, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(mScoreBitmaps[ScoreShowWhenGot.ADD_TIME]);
		drawable.setBounds(0, 0, tempBigTileSize ,tempBigTileSize );
		drawable.draw(canvas);	
		
		HashMap<Integer, Integer> tempSoundPoolMap = mSoundPoolMap ;    
		tempSoundPoolMap.put(EAT_APPLE_SOUND, mSoundPool.load(mContext,
					R.raw.eat_apple, 1));
		tempSoundPoolMap.put(EAT_POISONOUS_APPLE_SOUND, mSoundPool.load(mContext,
					R.raw.eat_poinsonous_apple, 1));
		tempSoundPoolMap.put(EAT_UNBELIEVABLE_APPLE_SOUND, mSoundPool.load(mContext,
					R.raw.eat_unbelievable_apple, 1));
	
		
		
		// alpha == 255 ==> not tramsparent  ; alpha == 0 ==> transparent
		for(int i= 0 ; i !=APPLE_DISAPPEAR_COUNT_MAX; i++){
			mDisappearAppleTranspPaint[i] = new Paint();
			mDisappearAppleTranspPaint[i].setStyle(Paint.Style.STROKE);
			mDisappearAppleTranspPaint[i].setAlpha((150/APPLE_DISAPPEAR_COUNT_MAX)*i);
		}

		for(int i= 0 ; i !=APPLE_FLICKER_COUNT_MAX; i++){
			mFlickerAppleTranspPaint[i] = new Paint();
			mFlickerAppleTranspPaint[i].setStyle(Paint.Style.STROKE);
			mFlickerAppleTranspPaint[i].setAlpha(255-(255/APPLE_FLICKER_COUNT_MAX)*i);
		}

		
		int tempI = 0;
		for(int i= 0 ; i !=SCORE_SHOW_COUNT_MAX ; i++ , tempI ++){
			if( tempI == 20){
				tempI = 0;
			}
			mScoreTranspPaint[i] = new Paint();
			mScoreTranspPaint[i].setStyle(Paint.Style.STROKE);
			mScoreTranspPaint[i].setAlpha(((150+10*tempI>255)?(350-5*tempI):(150+10*tempI)));
		}
		
	}


}
