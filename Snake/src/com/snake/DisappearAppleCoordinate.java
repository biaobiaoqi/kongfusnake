package com.snake;


public class DisappearAppleCoordinate{
		int x ; //x pos
		int y ; // y pos
		int lifeTime ;

		public DisappearAppleCoordinate(int x , int y , int lifeTime){
				this.x = x ;
				this.y = y;
				this.lifeTime = lifeTime;
		}

		/**
	* @return corresponds to the image array , eg. mAnimationNormalAppleBitmaps
		 */
		public int getLifeTime(){
				return lifeTime-1;   
		}
		
		public boolean isDeath(){
			return ( lifeTime ==0);
		}
		
		public void lifeTimePast(){
			lifeTime -- ;
		}
}
