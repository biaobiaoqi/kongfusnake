package com.snake;

public class FlickerAppleCoordinate extends Coordinate{
	private static int flickerLifeTime ;
	private static int flickerCondition ;
	private static int max ;
	public FlickerAppleCoordinate(int newX, int newY , int lifeTime) {
		super(newX, newY);
		flickerLifeTime = flickerCondition = max = lifeTime;
	}
	
	public FlickerAppleCoordinate(Coordinate coord , int lifeTime) {
		super(coord.x, coord.y);
		flickerLifeTime = flickerCondition = max= lifeTime ;
	}
		
	public static void changeCondition(){
		if (flickerCondition == 1){
			flickerCondition = max ;
		}else{
			flickerCondition -- ;
		}
		
	}
	
	public static int getCondition(){
		return flickerCondition - 1;
	}
	
	public static void lifeTimePast(){
		if (flickerLifeTime != 0){
			flickerLifeTime -- ;
		}
	}
    
	public static int getFlickerLifeTime(){
		return flickerLifeTime;	
	}
	
	public static boolean isFlickerFinish(){
		return flickerLifeTime == 0;
	}
    	
    
    
	
}