package com.snake;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

//The dialog is displayed when the user wants to quit
public class QuitDialog extends Dialog {	
	private QuitInterface iExit;
	private LinearLayout linearLayout;
	private TextView textview;

	public QuitDialog(Context context) {
		super(context,R.style.translucent);	
		
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
	 
	 //The layout of exit dialog
	 private void LayoutInit()
	 {
		linearLayout=new LinearLayout(getContext());
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setGravity(Gravity.CENTER);
		linearLayout.setBackgroundResource(R.drawable.main_middle_background);
		LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ScreenSize.width,ScreenSize.h4);
    	   
	    textview=new TextView(getContext());
	    textview.setBackgroundColor(Color.TRANSPARENT);
	    textview.setTextColor(Color.BLACK);
	    if(ScreenSize.width>320)
	    	textview.setTextSize(ScreenSize.w1);
	    else if(ScreenSize.width>240)
	    	textview.setTextSize(ScreenSize.w15);
	    else
	    	textview.setTextSize(ScreenSize.w2);
	    Typeface mFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/font_quit.ttf");
	    textview.getPaint().setFakeBoldText(true);
	    textview.setTypeface(mFace);
	    textview.setText(R.string.exit);
	    LinearLayout.LayoutParams lp_textview=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	    	    
	    LinearLayout l1=new LinearLayout(getContext());
	    l1.setOrientation(LinearLayout.HORIZONTAL);
	    LinearLayout.LayoutParams lp_l1=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	    lp_l1.topMargin=ScreenSize.h001;
	      	  
	    ImageButton ib_yes=new ImageButton(getContext());
	    ib_yes.setImageResource(R.drawable.quit_yes_button);
	    ib_yes.setBackgroundColor(Color.TRANSPARENT);
	    LinearLayout.LayoutParams lp_ib_yes=new LinearLayout.LayoutParams(ScreenSize.w2,ScreenSize.w2);
	    ib_yes.setScaleType(ScaleType.FIT_CENTER);
	    
	    ImageButton ib_no=new ImageButton(getContext());
	    ib_no.setImageResource(R.drawable.quit_no_button);
	    ib_no.setBackgroundColor(Color.TRANSPARENT);
	    LinearLayout.LayoutParams lp_ib_no=new LinearLayout.LayoutParams(ScreenSize.w2,ScreenSize.w2);
	    lp_ib_no.leftMargin=ScreenSize.w1;
	    ib_no.setScaleType(ScaleType.FIT_CENTER);
	    
	    ib_yes.setOnClickListener(new ImageButton.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(MainActivity.sound==true)
					MainActivity.mSoundPool.play(MainActivity.mSoundPoolMap.get(MainActivity.mSound), MainActivity.mCurrentVol, MainActivity.mCurrentVol, 1, 0, 1f);		
		
				dismiss();
				iExit.exit();  //Call the function exit() of MainActivity to finish the application
			}
	    });
	    
	    ib_no.setOnClickListener(new ImageButton.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(MainActivity.sound==true)
					MainActivity.mSoundPool.play(MainActivity.mSoundPoolMap.get(MainActivity.mSound), MainActivity.mCurrentVol, MainActivity.mCurrentVol, 1, 0, 1f);		
		
				dismiss();
			}
	    });
	    
	    l1.addView(ib_yes,lp_ib_yes);
	    l1.addView(ib_no,lp_ib_no);
	        	
	    linearLayout.addView(textview,lp_textview);
	    linearLayout.addView(l1,lp_l1);
	    setContentView(linearLayout,lp);	 
	 }
	 
	 //Use the MainActivity to finish the application which implements the ExitInterface
	 public void setExit(QuitInterface iExit)
	 {
		 this.iExit=iExit;
	 }

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		MainActivity.startAnimation();
		super.dismiss();
	}
}