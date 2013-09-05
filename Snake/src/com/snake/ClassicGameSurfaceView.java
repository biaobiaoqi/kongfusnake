package com.snake;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;

public class ClassicGameSurfaceView extends NormalGameSurfaceView {    
//BEGIN
	//these are configurations for this game , change it by yourself
	//control
	private final static float SENSITIVE = 1.3f ;
	private final static double TOLERANCE = 0.15 ;
//END	

	
	public ClassicGameSurfaceView(Context context,
			AttributeSet paramAttributeSet) {
		super(context, paramAttributeSet);
		mSensorManager = (SensorManager)this.getContext().getSystemService(Context.SENSOR_SERVICE);
		Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(sensorEventListener, accelerometer,SensorManager.SENSOR_DELAY_GAME);
	
	}

	public ClassicGameSurfaceView(Context context) {
		super(context);
		mSensorManager = (SensorManager)this.getContext().getSystemService(Context.SENSOR_SERVICE);
		Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(sensorEventListener, accelerometer,SensorManager.SENSOR_DELAY_GAME);
	
	}
	
	protected  SensorEventListener sensorEventListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int accuracy) { }
		public void onSensorChanged(SensorEvent event) {
			if(event.values[0]>TOLERANCE ||event.values[0]< -TOLERANCE ){
				mXVelocity = SENSITIVE * event.values[0];  //乘以2，让用户操作时，动作不用太大。
			}
			if(event.values[1]>TOLERANCE ||event.values[1]< -TOLERANCE){
				mYVelocity =  SENSITIVE * event.values[1];
			}
		}
	};
}
