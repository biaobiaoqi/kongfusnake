package com.snake;

import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.util.Log;

public class BuildSnakeSurface {   // construct path effect for snake
	public static int mSnakeGap;
	
	public static PathEffect makeSnakePathEffect(int type ){
		return new PathDashPathEffect(makeSnakePathDash(type), 12,
				0,PathDashPathEffect.Style.ROTATE);

	}
	
	public static PathEffect makeSnakeHeadPathEffect(int type){
		return  new PathDashPathEffect(makeSnakeHeadPath(type ), 12,
				0,PathDashPathEffect.Style.ROTATE);
	}
	

	//snake surface
    public static Path makeSnakePathDash(int type ) {
    	Path p = new Path();
       if (GameBasicSurfaceView.TILESIZE == 28){
		        	p.moveTo(6, 0);
		            p.lineTo(-6, -8);
		            p.lineTo(10, 0);
		            p.lineTo(-6, 8);
		            p.lineTo(6, 0);
		        	p.moveTo(0, 0);
		            p.lineTo(-6, -8);
		            p.lineTo(2, 0);
		            p.lineTo(-6, 8);
		            p.lineTo(0, 0);
		        	p.close();
		        
       }else if(GameBasicSurfaceView.TILESIZE == 19){
		        	p.moveTo(4, 0);
		            p.lineTo(-4, -5);
		            p.lineTo(7, 0);
		            p.lineTo(-4, 5);
		            p.lineTo(4, 0);
		        	p.moveTo(0, 0);
		            p.lineTo(-4, -5);
		            p.lineTo(2, 0);
		            p.lineTo(-4, 5);
		            p.lineTo(0, 0);
		        	p.close();
       }else if(GameBasicSurfaceView.TILESIZE == 14){
		        	p.moveTo(4, 0);
		            p.lineTo(-4, -5);
		            p.lineTo(6, 0);
		            p.lineTo(-4, 5);
		            p.lineTo(4, 0);
		        	p.moveTo(0, 0);
		            p.lineTo(-4, -5);
		            p.lineTo(2, 0);
		            p.lineTo(-4, 5);
		            p.lineTo(0, 0);
		        	p.close();
	   }
        return p;
    }
    
    public static Path makeSnakeHeadPath(int type){
    	Path p = new Path();
    	
	    if(GameBasicSurfaceView.TILESIZE == 28){
	    	p.moveTo(6, 0);
            p.lineTo(3, -8);
            p.lineTo(10, 0);
            p.lineTo(3, 8);
            p.lineTo(7, 0);
            p.lineTo(3, -8);
            p.close();
    	 	/*
            		p.moveTo(-9, 10);
            		p.lineTo(-2, 2);
            		p.lineTo(2, 2);
            		p.lineTo(-9, 10);
            		p.close();
            		

            		p.moveTo(-9, -10);
            		p.lineTo(-2, -2);
            		p.lineTo(2, -2);
            		p.lineTo(-9, -10);
            		p.close();
*/

    		p.moveTo(-9, 8);
    		p.lineTo(-2, 6);
    		p.lineTo(2, 1);
    		p.lineTo(-9, 8);
    		p.close();
    		

    		p.moveTo(-9, -8);
    		p.lineTo(-2, -6);
    		p.lineTo(2, -1);
    		p.lineTo(-9, -8);
    		p.close();

		            mSnakeGap = 12;
	    }else if(GameBasicSurfaceView.TILESIZE == 19){
	    	p.moveTo(4, 0);
            p.lineTo(2, -5);
            p.lineTo(7, 0);
            p.lineTo(2, 5);
            p.lineTo(4, 0);
            p.lineTo(2, -5);
            p.close();

    		p.moveTo(-6, 5);
    		p.lineTo(-1, 4);
    		p.lineTo(1, 0);
    		p.lineTo(-6, 5);
    		p.close();
    		

    		p.moveTo(-6, -5);
    		p.lineTo(-1, -4);
    		p.lineTo(1, -0);
    		p.lineTo(-6, -5);
    		p.close();
		            mSnakeGap = 9;
	    }else if(GameBasicSurfaceView.TILESIZE == 14){
	     	p.moveTo(4, 0);
            p.lineTo(2, -5);
            p.lineTo(7, 0);
            p.lineTo(2, 5);
            p.lineTo(4, 0);
            p.lineTo(2, -5);
            p.close();

    		p.moveTo(-6, 5);
    		p.lineTo(-1, 4);
    		p.lineTo(1, 0);
    		p.lineTo(-6, 5);
    		p.close();
    		

    		p.moveTo(-6, -5);
    		p.lineTo(-1, -4);
    		p.lineTo(1, -0);
    		p.lineTo(-6, -5);
    		p.close();
		            mSnakeGap = 7;
	    }
        return p;      
    }
    
}