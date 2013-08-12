package com.example.bharatibraille;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class Words implements Runnable{
	
	Context context;
	String Hindi[][]=new String [64][2];
    InputStream is, is_num;
    BufferedReader br, br_num;
    int i;
    
	Words(String LangName, Activity A){
		 int ID     = 0;
		 int ID_num = 0;
		 /*if(LangName.equals("hindi"))
			 ID = R.raw.hindi;*/		 
		 context = A.getApplicationContext();
		 ID		= A.getResources().getIdentifier("raw/"+LangName, null, context.getPackageName());
		 ID_num = A.getResources().getIdentifier("raw/"+LangName+"_num", null, context.getPackageName());
		 is 	= A.getResources().openRawResource(ID);
		 is_num = A.getResources().openRawResource(ID_num);
			try {
				br = new BufferedReader( new InputStreamReader(is,"UTF8"));			
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				Log.e("UTF8","Encoding Problem");
				e1.printStackTrace();
			}
		 	br_num = new BufferedReader( new InputStreamReader(is_num));
			//Log.e("Load","File Loaded");
			//Log.e("Language",LangName);
			String read		= new String();
			String read_num = new String();
			int y 			= 0;
			try {
				while((read_num = br_num.readLine()) != null){
					y    		= Integer.parseInt(read_num);
					//Log.e("reading num",Integer.toString(y));
					read 		= br.readLine();
					read 		= br.readLine();
					//Log.e("reading",read);
					Hindi[y][0] = read;
					read 		= br.readLine();
					Hindi[y][1] = read;
					//Log.e("reading",read);
				}
				is.close(); is_num.close();
				br.close(); br_num.close();
				Log.e("Closed", "Language Successfully Loaded");
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 
	 }
	 
	 public String []get_Word(boolean v[]) 
	 {
		 int i=1,j=32,k=0;
		 String s[] = new String[2];
		 s[0] = "-1"; s[1] = "-1";
		 while(i<7)
		 {
			 if(v[i]==true)
				 k+=j;
			 j/=2;
			 i++;
        
		 }
		 if(Hindi[k][0]==null)
			 return s;
		 return Hindi[k];
	 }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	

	}