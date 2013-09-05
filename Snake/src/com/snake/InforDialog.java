package com.snake;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InforDialog extends Dialog{
	
	private LinearLayout linearLayout;

	public InforDialog(Context context) {
		super(context,R.style.translucent);
		// TODO Auto-generated constructor stub	
		
		Window mwindow=getWindow(); 
		WindowManager.LayoutParams lp=mwindow.getAttributes(); 
		lp.x=0; 
		lp.y=ScreenSize.h2*(-1); 
		
		mwindow.setAttributes(lp);
	}
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		 
		LayoutInit();
	}
	 
	//Layout of the Guide Dialog
	private void LayoutInit()
	{
		linearLayout=new LinearLayout(getContext());
    	linearLayout.setOrientation(LinearLayout.VERTICAL);
    	linearLayout.setGravity(Gravity.CENTER);
    	linearLayout.setOnTouchListener(lis_linearLayout);
   
    	Typeface mFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/font_guide.ttf");
    	
    	TextView t1=new  TextView(getContext());
    	t1.setTextColor(Color.WHITE);
    	if(ScreenSize.width>320)
    		t1.setTextSize(ScreenSize.w025);
    	else if(ScreenSize.width>240)
    		t1.setTextSize(ScreenSize.w05);
    	else
    		t1.setTextSize(ScreenSize.w075);
    	t1.setBackgroundColor(Color.TRANSPARENT);
    	t1.setTypeface(mFace);
    	t1.setText(R.string.infor_1);
    	t1.setGravity(Gravity.CENTER);
    	LinearLayout.LayoutParams lp_t1=new LinearLayout.LayoutParams(ScreenSize.width,LayoutParams.WRAP_CONTENT);
    	lp_t1.topMargin=0;
    	
    	TextView t2=new  TextView(getContext());
    	t2.setTextColor(Color.WHITE);
    	if(ScreenSize.width>320)
    		t2.setTextSize(ScreenSize.w025);
    	else if(ScreenSize.width>240)
    		t2.setTextSize(ScreenSize.w05);
    	else
    		t2.setTextSize(ScreenSize.w075);
    	t2.setBackgroundColor(Color.TRANSPARENT);
    	t2.setTypeface(mFace);
    	t2.setText(R.string.infor_2);
    	t2.setGravity(Gravity.CENTER);
    	LinearLayout.LayoutParams lp_t2=new LinearLayout.LayoutParams(ScreenSize.width,LayoutParams.WRAP_CONTENT);
    	lp_t2.topMargin=ScreenSize.h01;
    	
    	TextView t3=new  TextView(getContext());
    	t3.setTextColor(Color.WHITE);
    	if(ScreenSize.width>320)
    		t3.setTextSize(ScreenSize.w025);
    	else if(ScreenSize.width>240)
    		t3.setTextSize(ScreenSize.w05);
    	else
    		t3.setTextSize(ScreenSize.w075);
    	t3.setBackgroundColor(Color.TRANSPARENT);
    	t3.setTypeface(mFace);
    	t3.setText(R.string.infor_3);
    	t3.setGravity(Gravity.CENTER);
    	LinearLayout.LayoutParams lp_t3=new LinearLayout.LayoutParams(ScreenSize.width,LayoutParams.WRAP_CONTENT);
    	lp_t3.topMargin=ScreenSize.h01;
    	
    	TextView t4=new  TextView(getContext());
    	t4.setTextColor(Color.WHITE);
    	if(ScreenSize.width>320)
    		t4.setTextSize(ScreenSize.w025);
    	else if(ScreenSize.width>240)
    		t4.setTextSize(ScreenSize.w05);
    	else
    		t4.setTextSize(ScreenSize.w075);
    	t4.setBackgroundColor(Color.TRANSPARENT);
    	t4.setTypeface(mFace);
    	t4.setText(R.string.infor_4);
    	t4.setGravity(Gravity.CENTER);
    	LinearLayout.LayoutParams lp_t4=new LinearLayout.LayoutParams(ScreenSize.width,LayoutParams.WRAP_CONTENT);
    	lp_t4.topMargin=ScreenSize.h01;
    	    	
    	linearLayout.addView(t1,lp_t1);
    	linearLayout.addView(t2,lp_t2);
    	linearLayout.addView(t3,lp_t3);
    	linearLayout.addView(t4,lp_t4);
    	setContentView(linearLayout);
    }
	    
	//If the user touches the dialog, the dialog will dismiss
	private OnTouchListener lis_linearLayout=new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			dismiss();	 
			return false;
		}
	};

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		MainActivity.startAnimation();
		super.dismiss();
	}
}

