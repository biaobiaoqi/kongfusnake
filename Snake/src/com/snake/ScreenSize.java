package com.snake;

//Record the size of the screen
public class ScreenSize {	
	public static int width,height;
	public static int w1,h1,w2,h2,w8,h8,w4,h4,w075,h075,w001,h001,w6,h6,w05,h05,w3,h3,w9,h9,w15,h15,w025,h025,w5,h5,w12,h12,w07,h07,w375,h375,w125,h125,w7,h7,w35,h35,w01,h01,h175,w175,w04;
	
	public static void Init(int width_in,int height_in)
	{
		width=width_in;
		height=height_in;
		
		Set();
	}
	
	private static void Set()
	{
		w1=(int)(width*0.1);
		h1=(int)(height*0.1);
		
		w2=(int)(width*0.2);
		h2=(int)(height*0.2);
		
		w4=(int)(width*0.4);
		h4=(int)(height*0.4);
		
		w8=(int)(width*0.8);
		h8=(int)(height*0.8);
		
		w075=(int)(width*0.075);
		h075=(int)(height*0.075);
		
		w075=(int)(width*0.075);
		h075=(int)(height*0.075);
		
		w001=(int)(width*0.001);
		h001=(int)(height*0.001);
		
		w6=(int)(width*0.6);
		h6=(int)(height*0.6);
		
		w05=(int)(width*0.05);
		h05=(int)(height*0.05);
		
		w3=(int)(width*0.3);
		h3=(int)(height*0.3);
		
		w9=(int)(width*0.9);
		h9=(int)(height*0.9);
		
		w15=(int)(width*0.15);
		h15=(int)(height*0.15);
		
		w025=(int)(width*0.025);
		h025=(int)(height*0.025);
		
		w5=(int)(width*0.5);
		h5=(int)(height*0.5);
		
		w12=(int)(width*0.12);
		h12=(int)(height*0.12);
		
		w07=(int)(width*0.07);
		h07=(int)(height*0.07);
		
		w375=(int)(width*0.375);
		h375=(int)(height*0.375);
		
		w125=(int)(width*0.125);
		h125=(int)(height*0.125);
		
		w7=(int)(width*0.7);
		h7=(int)(height*0.7);
		
		w35=(int)(width*0.35);
		h35=(int)(height*0.35);
		
		w01=(int)(width*0.01);
		h01=(int)(height*0.01);
		
		h175=(int)(width*0.175);
		w175=(int)(height*0.175);
		
		w04=(int)(width*0.04);
	}
}
