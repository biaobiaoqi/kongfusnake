package com.snake;

public class ScoreShowWhenGot {
	public static final int NORMAL_SCORE = 0 ;
	public static final int SUPER_SCORE = 1 ;
	public static final int UNBELIEVABLE_SCORE = 2 ;
	public static final int POISONOUS_SCORE = 3 ;
	public static final int ADD_TIME = 4;
	
	// in Block game 
	public static final int BLOCK_SCORE = -6;
	public static final int MOVING_POISONOUS_SCORE = -10;
	
	public Coordinate mPosition;
	protected int mLifeTimeLeft;
	protected int mPic ;
	
	ScoreShowWhenGot(int xPos , int yPos , int p  , int lifeTime){
		mPosition = new Coordinate(xPos,yPos);
		mPic = p;
		mLifeTimeLeft = lifeTime; //SCORE_SHOW_COUNT_MAX
	}
	
	public void lifeTimePast(){
		if(mPic != POISONOUS_SCORE){ //poisonous will immediately take action
			if(mLifeTimeLeft != 0 ){
					mLifeTimeLeft -- ;
			}else{
				mPosition.y -= 2;
			}
		}else{
			if(mLifeTimeLeft != 0 ){
				mLifeTimeLeft -- ;
			}
		}
	}
	
	public int getLifeTime(){
		return mLifeTimeLeft ;
	}
	
	public boolean finished(){
		if(mPic != POISONOUS_SCORE){ //poisonous will immediately take action
			return  (mPosition.y <= GameBasicSurfaceView.mYOffset) ;
		}else{
			return (mLifeTimeLeft <= 0);
		}
	
	}
}