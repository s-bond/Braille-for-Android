package com.example.bharatibraille;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import android.os.Environment;
import android.util.Log;

public class HandleFile implements Runnable{
	
	
	File file, root, dir, _check;
	FileInputStream fis;
	FileOutputStream fos;
	BufferedWriter bw;
	BufferedReader br;
	boolean check;
	String rootPath = null;
	String mainPath = null;
	String readPath = null;
	String temp     = null;
	int BUFFER_SIZE = 8192;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		CheckExternalMedia();		
		if(check == true )
			PrepareWrite();
		else
			Log.e("Permission","User Permission Problem");
	}
	
	public void CheckExternalMedia(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state))
			check = true;
		else
			check = false;
	}
	
	public void PrepareWrite(){
		root = Environment.getExternalStorageDirectory();
		rootPath = root.toString();
		dir = new File(root.getAbsolutePath()+"/Braille");
		mainPath = rootPath+"/Braille";
		if(!dir.exists() || !dir.isDirectory()){
			dir.mkdirs();
			}
		}
	
	public void Write(String data, String FileName){
		
		file = new File(dir,FileName+".txt");
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			bw = new BufferedWriter(new OutputStreamWriter((fos),"utf8"),BUFFER_SIZE );			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bw.write(data,0,data.length());
			bw.flush();
			fos.close();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public String Read(String FileName){
		String s = new String ();
		readPath = "";
		readPath = mainPath+"/"+FileName;
		//Log.e("read path", readPath);
		file = new File(readPath);
		
		if(!file.exists())
			return "no";
		
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//Log.e("Read Error", "File Not Found");
			e.printStackTrace();
		}
		try {
			br = new BufferedReader(new InputStreamReader(fis,"utf8"),BUFFER_SIZE);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			//Log.e("utf8", "encoding error");
			e.printStackTrace();
		}
		
		try {
			while((temp=br.readLine())!=null){
				//Log.e("source", temp);
				s += temp;
				//s +="\n";
			}
			br.close();
			fis.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//Log.e("Buffered Reader", "Not opened_1");
			e.printStackTrace();
		}
		
		return s;
	}
}
