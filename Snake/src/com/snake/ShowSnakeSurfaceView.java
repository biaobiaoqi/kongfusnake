package com.snake;

import java.util.ArrayList;

import java.util.Random;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import com.snake.*;
public class ShowSnakeSurfaceView extends SurfaceView  implements Callback ,Runnable{
	private SurfaceHolder mSurfaceHolder;    
    private Thread mThread;
    private Boolean mIsRunning = true ;
    private Canvas mCanvas;    
    private Paint mPaint;  
    private static Bitmap mBackgroundBitmap ;
    
    private int mSnakeLength;
    private Path mSnakePath  , mSnakeHeadPath; 
    private PathEffect mSnakePathEffect , mSnakeHeadPathEffect;
    
    //记录屏幕参数,单位为pixel
    private int mWidth = 0 , mHeight = 0 ; //像素
    private int mXOffset , mYOffset ,mBackgroundWidth , mBackgroundHeight; //原点坐标的偏移量。
    private int mSnakeGap ;
    
    
    //前进方向
    private int mDirection = EAST;
    private static final int NORTH = 1;
    private static final int SOUTH = -1;
    private static final int EAST = 2;
    private static final int WEST = -2;
    
    private ArrayList<Coordinate> mSnakeTrail = new ArrayList<Coordinate>();
    private ArrayList<Coordinate> mKeyPoints = new ArrayList<Coordinate>();
    private int optionDirectionList[][] =  new int[][]   {{EAST , SOUTH , EAST , SOUTH}, 
    		 											  {WEST , EAST , SOUTH , SOUTH}, 
    		 											  {WEST , SOUTH , WEST , SOUTH},
    		 											  {NORTH , EAST , NORTH , EAST}, 
    		 											  {WEST , EAST , NORTH , EAST },
    		 											  {WEST , NORTH , WEST , NORTH}};   
    private boolean mSynLock = false ;
    
    //用于随机数生成
    private static final Random RNG = new Random();
    private static final int[] mSpeedArray = 
    		new int[] { 4 , 4 , 4 , 4 , 4 , 6 , 6 , 6 ,  10 , 10 , 
    					10 , 10 , 10 , 10 , 11 , 11 , 12 , 12 , 12 , 12 ,
    					13 , 13 , 13 , 14 , 14 , 14 , 15 , 15 , 15 , 15 ,
    					15 , 15 , 15 , 15 , 15 , 15 , 15 , 15 , 15 , 15 ,
    					14 , 14 , 14 , 14 , 14 , 14 , 14 , 14 , 14 , 14 ,
    					13 , 13 , 13 , 13 , 13 , 13 , 12 , 12 , 12 , 12 ,
    					12 , 12 , 12 , 12 , 11 , 11 , 10 , 10 , 10 , 10 ,
    					 6 , 6 , 6 , 6 , 6 , 6 , 6 , 6 , 6 , 6 , 
    					 4 , 4 , 4 , 4 , 4 , 4 , 4 , 4 , 4 , 4 ,
    					 };
    private int mSpeedIndex = 0;
    
	public ShowSnakeSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public ShowSnakeSurfaceView(Context context) {
		super(context);
		init();
	}
    
	//对部分成员变量初始化。
	private void init(){
		
		mSurfaceHolder = this.getHolder();    
		mSurfaceHolder.addCallback(this);  
		mPaint = new Paint();    
		mPaint.setAntiAlias(true);    
		mPaint.setColor(Color.RED);        
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {	
		this.setFocusable(true);
		mWidth = this.getWidth();  
		mHeight = this.getHeight(); 
		mSnakeGap = 10 ;
		mXOffset = mSnakeGap; 
		mYOffset = mSnakeGap;
		mBackgroundWidth = mWidth - mSnakeGap * 2 ;
		mBackgroundHeight = mHeight - mSnakeGap * 2;
		if (mWidth < 250 ){
			GameBasicSurfaceView.TILESIZE = 14 ;
			mSnakeLength = 70 ;
		}else if (mWidth < 350){
			GameBasicSurfaceView.TILESIZE = 19 ;
			mSnakeLength = 90 ;
		}
		else{
			GameBasicSurfaceView.TILESIZE = 28 ;
			mSnakeLength = 140 ;
		}
		
		
		
		ArrayList<Coordinate> tempPoints = mKeyPoints;
		tempPoints.add(new Coordinate(mXOffset,mYOffset));
		tempPoints.add(new Coordinate(mXOffset + mBackgroundWidth/2,mYOffset));
		tempPoints.add(new Coordinate(mXOffset + mBackgroundWidth,mYOffset));
		tempPoints.add(new Coordinate(mXOffset,mYOffset + mBackgroundHeight));
		tempPoints.add(new Coordinate(mXOffset + mBackgroundWidth/2,mYOffset + mBackgroundHeight));
		tempPoints.add(new Coordinate(mXOffset + mBackgroundWidth,mYOffset + mBackgroundHeight));
		
		initResource();
		initSnake();
		mSynLock = false;
		
		mThread = new Thread(this);  //进程运行完成以后，不能再次start()，只好重新初始化一个线程。
		mIsRunning = true ;
		mThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mSynLock = true ;
		mIsRunning = false ;
	}
	
	
	private void initResource(){
		Resources r = this.getContext().getResources();
		Drawable drawable = r.getDrawable(R.drawable.main_middle_background);
		mBackgroundBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBackgroundBitmap);
        drawable.setBounds(0, 0, mWidth , mHeight  );
        drawable.draw(canvas);
	} 

	
	private void initSnake(){
		SharedPreferences sp = this.getContext().getSharedPreferences("settings", 0);
        int type = sp.getInt("type", 0);
		
		mSnakeTrail.clear();
		
		mSnakePathEffect = BuildSnakeSurface.makeSnakePathEffect(type);
		mSnakeHeadPathEffect = BuildSnakeSurface.makeSnakeHeadPathEffect(type);
		mSnakeGap = BuildSnakeSurface.mSnakeGap;
		
		/*mSnakePathEffect =new PathDashPathEffect(makeSnakePathDash(type), 12,
						0,PathDashPathEffect.Style.ROTATE);
    	
    	mSnakeHeadPathEffect = new PathDashPathEffect(makeSnakeHeadPath(type), 12,
				0,PathDashPathEffect.Style.ROTATE);
    	*///initiate snaketrail data structure
    	int tempCount = mSnakeLength;
    	ArrayList<Coordinate> tempArray = mSnakeTrail;
    	for (int i = 0 ; i != tempCount ;i ++ ){
    		tempArray.add(new Coordinate(i + mXOffset, mYOffset)); 
    	}
        mDirection = EAST ;
	}
	
	@Override
	public void run() {
		while(mSynLock){
			//do nothing
		}
		while(mIsRunning){
			updateSnake();
		 	draw();
          	try {    
	               Thread.sleep(50);   
	        } catch (InterruptedException e) {    
	               e.printStackTrace();    
	        }   
		}
	}
	
	void draw(){
		try {		
			mCanvas = mSurfaceHolder.lockCanvas(); 
		    mCanvas.drawBitmap(mBackgroundBitmap, 0 , 0 , mPaint);
		    mPaint.setPathEffect(mSnakePathEffect);
		    mPaint.setColor(Color.BLACK);
			mCanvas.drawPath(mSnakePath, mPaint);
			
			mPaint.setPathEffect(mSnakeHeadPathEffect);
	        mPaint.setColor(Color.BLACK);
			mCanvas.drawPath(mSnakeHeadPath, mPaint);	
		} catch (Exception ex) {    
        } finally {   
            if (mCanvas != null)    
            	mSurfaceHolder.unlockCanvasAndPost(mCanvas);      
        }    	
	}
	
	
	void updateSnake(){
		ArrayList<Coordinate> tempSnakeTrail = mSnakeTrail;
		int tempSnakeLength = mSnakeLength ;
		
		if (tempSnakeTrail.size() != tempSnakeLength ){
			Log.e("ShowSnakeSurfaceView","no wrong!");
			return ;
		}
		
    	Coordinate headCoord = tempSnakeTrail.get(tempSnakeLength-1); //原来的蛇头
    	mSpeedIndex = (++ mSpeedIndex)%90 ;
    	int tempCount = mSpeedArray[mSpeedIndex];
    	
    	for (int i = 0 ; i != tempCount ; i ++){
    		switch(mDirection){
    			case NORTH:
    				if (reachKeyPoints(headCoord.x,headCoord.y - 1)){
    					mDirection = responseDirection(headCoord.x,headCoord.y - 1);
    				}
    				tempSnakeTrail.add(new Coordinate(headCoord.x , headCoord.y - 1));
    				tempSnakeTrail.remove(0);
    				break;
    			case SOUTH:
    				if (reachKeyPoints(headCoord.x,headCoord.y + 1)){
    					mDirection = responseDirection(headCoord.x,headCoord.y + 1);
    				}
    				tempSnakeTrail.add(new Coordinate(headCoord.x , headCoord.y + 1));
    				tempSnakeTrail.remove(0);
    				break;
    			case WEST:
    				if (reachKeyPoints(headCoord.x - 1 , headCoord.y)){
    					mDirection = responseDirection(headCoord.x - 1 , headCoord.y);
    				}
    				tempSnakeTrail.add(new Coordinate(headCoord.x - 1, headCoord.y ));
    				tempSnakeTrail.remove(0);
    				break;
    			case EAST:
    				if (reachKeyPoints(headCoord.x + 1 , headCoord.y)){
    					mDirection = responseDirection(headCoord.x + 1 ,headCoord.y);
    				}
    				tempSnakeTrail.add(new Coordinate(headCoord.x + 1  , headCoord.y));
    				tempSnakeTrail.remove(0);
    				break;
    		}
    		headCoord = tempSnakeTrail.get(tempSnakeLength-1); //取得新的蛇头
    	}	

    	//refresh snake path
    	Path tempSnakePath = new Path();
    	Path tempSnakeHeadPath = new Path();
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
	
	boolean reachKeyPoints(int x , int y){
		ArrayList<Coordinate> tempKeyPoints = mKeyPoints;
		for (int i = 0 ; i != 6 ; i ++){
			if (tempKeyPoints.get(i).x == x && tempKeyPoints.get(i).y == y){
				return true ;
			}
		}
		return false;
	}
	
	int responseDirection(int x , int y){
		ArrayList<Coordinate> tempKeyPoints = mKeyPoints;
		for (int i = 0 ; i != 6 ; i ++){
			if (tempKeyPoints.get(i).x == x && tempKeyPoints.get(i).y == y){
				return optionDirectionList[i][RNG.nextInt(4)];				 
			}
		}
		return NORTH ;
	}
	
	
	
	class Coordinate {
	    public int x;
	    public int y;

	    public Coordinate(int newX, int newY) {
	        x = newX;
	        y = newY;
	    }


	    public boolean equals(Coordinate other) {
	        if (x == other.x && y == other.y) {
	            return true;
	        }
	        return false;
	    }

	    
	    @Override
	    public String toString() {
	        return "Coordinate: [" + x + "," + y + "]";
	    }
	}
}
