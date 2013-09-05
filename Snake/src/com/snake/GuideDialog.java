package com.snake;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;

public class GuideDialog extends Dialog{

	private LinearLayout linearLayout;

	public GuideDialog(Context context) {
		super(context,R.style.translucent);
		// TODO Auto-generated constructor stub	
		Window mwindow=getWindow(); 
		WindowManager.LayoutParams lp=mwindow.getAttributes(); 
		lp.x=0; 
		lp.y=(ScreenSize.h2-ScreenSize.h025)*(-1); 
		
		mwindow.setAttributes(lp);
	}
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
			
		linearLayout=new LinearLayout(getContext());
    	linearLayout.setOrientation(LinearLayout.VERTICAL);
    	linearLayout.setGravity(Gravity.CENTER);
  
    	LayoutInit();
	}
	 
	//Layout of the Guide Dialog
	private void LayoutInit()
	{	
		linearLayout=new LinearLayout(getContext());
    	linearLayout.setOrientation(LinearLayout.VERTICAL);
    	linearLayout.setGravity(Gravity.CENTER);
    	linearLayout.setOnTouchListener(lis_linearLayout);
    	
    	ImageView v1=new ImageView(getContext());
    	v1.setBackgroundResource(R.drawable.guide_1);
    	v1.setScaleType(ScaleType.FIT_CENTER);
    	LinearLayout.LayoutParams lp_l1=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(ScreenSize.h4+ScreenSize.h05));

    	linearLayout.addView(v1,lp_l1);
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
