package com.example.bharatibraille;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.widget.ImageView;

public class Points implements Runnable {
	static final int num = 8;
	int i, j, k;
	float d1, d2, d3;
	float xc[] = new float[8];
	float yc[] = new float[8];
	Paint p;
	ImageView v;
	Bitmap bm;
	Canvas c;
	
	
	@Override
	public void run() {		
	}
	
	Points (float a[], float b[]){
		for(i=0;i<num;i++){
			xc[i] = a[i];
			yc[i] = b[i];
		}
		for(i=0;i<num;i++){
			for(j=i+1;j<num;j++){
				if( xc[i] > xc[j] )
					swap(i,j);
			}
		}
		p = new Paint();				
		p.setAntiAlias(true);		
	}
	
	void swap(int a, int b){
		float t;
		
		t = xc[a];
		xc[a] = xc[b];
		xc[b] = t;
		
		t = yc[a];
		yc[a] = yc[b];
		yc[b] = t;
	}
	
	public boolean[] click(float x[], float y[]){
		boolean ans[] = new boolean [num];
		for(i=0;i<num;i++) ans[i] = false;
		for(i=0;i<num;i++){
			for(j=0;j<num;j++){
				d1 = Math.abs(xc[j]-x[i]);
				d2 = Math.abs(yc[j]-y[i]);
				d3 = d1+d2;
				if( d3 <= (float)105 ){
					ans[j] = true;
					break;
				}
			}
		}
		return ans;
	}

	public void press(boolean push[], Activity A){
		v = (ImageView)A.findViewById(R.id.image1);
		bm = Bitmap.createBitmap(v.getWidth(),v.getHeight(),Config.ARGB_8888);		
		c = new Canvas(bm);			
		for(i=0;i<Braille.num;i++){
			if(push[i]==true){
				p.setStyle(Paint.Style.FILL);
				p.setColor(Color.rgb(128, 0, 20));
				c.drawCircle(xc[i], yc[i], 50, p);
			}
			else{
				p.setStyle(Paint.Style.FILL);
				p.setColor(Color.rgb(101, 50, 50));
				c.drawCircle(xc[i], yc[i], 50, p);
				p.setStyle(Paint.Style.STROKE);
				p.setStrokeWidth(7);
				p.setColor(Color.rgb(255, 0, 0));
				c.drawCircle(xc[i], yc[i], 63, p);
			}
		}
		
		v.setImageBitmap(bm);
	}
	
	public void release(Activity A){
		v = (ImageView)A.findViewById(R.id.image1);
		bm = Bitmap.createBitmap(v.getWidth(),v.getHeight(),Config.ARGB_8888);		
		c = new Canvas(bm);
		p.setColor(Color.rgb(101, 50, 50));
		p.setStyle(Paint.Style.FILL);		
		for(i=0;i<Braille.num;i++){
			c.drawCircle(xc[i], yc[i], 50, p);
		}
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(7);
		p.setColor(Color.rgb(255, 0, 0));
		for(i=0;i<Braille.num;i++){
			c.drawCircle(xc[i], yc[i], 63, p);
		}
		v.setImageBitmap(bm);
	}
}
