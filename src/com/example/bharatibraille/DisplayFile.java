package com.example.bharatibraille;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayFile extends Activity implements OnGesturePerformedListener, OnInitListener{
	
	Intent in;
	TextView tv;
	GestureLibrary GLib;
	GestureOverlayView GView;
	SensorManager MySensorManager;
	ShakeEventListener MySensorListener;
	TextToSpeech tts;
	HandleFile bfile;
	Thread th_file;
	File getList;
	String FileList[];
	int _counter, N;
	boolean seq_check;
	//android:background="#FF438BED" for editText
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		HandleAll();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onGesturePerformed(GestureOverlayView gv, Gesture g) {
		// TODO Auto-generated method stub
		ArrayList<Prediction>predictions = GLib.recognize(g);
		if(predictions.size()>0){
			Prediction p = predictions.get(0);
			if(p.score > 1.0){
				
				if(p.name.equals("Forward") && N>0){
					if(_counter+1 > N)
						_counter = 0;
					tv.setText(bfile.Read(FileList[_counter++]));
					tts.speak(bfile.Read(FileList[_counter++]), TextToSpeech.QUEUE_FLUSH, null);
				}
				else if(p.name.equals("Backward") && N>0){
					if(_counter-2<0)
						_counter = N;
					tv.setText(bfile.Read(FileList[_counter-2]));
					tts.speak(bfile.Read(FileList[_counter-1]), TextToSpeech.QUEUE_FLUSH, null);
					_counter -= 2;
				}
				if(N == 0){
					tts.speak("no files to show", TextToSpeech.QUEUE_ADD, null);
				}
				
			}
		}
	}

	public void HandleAll(){
		setContentView(R.layout.display_file);
		///////////////////////////////////////
		//in = this.getIntent();
		//seq_check = in.getBooleanExtra("seq", false);
		///////////////////////////////////////
		tv = (TextView) findViewById(R.id.tv_display);
		///////////////////////////////////////
		tts = new TextToSpeech(this,this);
		tts.setLanguage(Locale.ENGLISH);
		tts.setPitch((float)1.0);
		tts.setSpeechRate((float)0.90);
		///////////////////////////////////////
		GLib = GestureLibraries.fromRawResource(this, R.raw.gestures);//Context,ID		
		if(!GLib.load()){
			Log.e("Gesture","Library Not Found");
			finish();
			}
		GView = (GestureOverlayView) findViewById(R.id.filegesture);
		GView.addOnGesturePerformedListener(this);
		////////////////////////////////////////
		bfile 	= new HandleFile();
		th_file = new Thread(bfile);
		th_file.start();
		////////////////////////////////////////
		_counter = 0; N = 0;
		////////////////////////////////////////
		getList = new File(Environment.getExternalStorageDirectory()+"/Braille");
		FileList = getList.list();
		N = FileList.length;
		////////////////////////////////////////
		///////////////////////////////////////
		MySensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		MySensorListener = new ShakeEventListener();
		MySensorManager.registerListener(MySensorListener, MySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI);
		MySensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
			
			@Override
			public void onShake() {
				// 	TODO Auto-generated method stub
				//	Toast.makeText(MainActivity.this, "Shaked!", Toast.LENGTH_SHORT).show();
				tts.speak("writing area", TextToSpeech.QUEUE_ADD, null);
				finish();
			}
		});	
		////////////////////////////////////////
	}

	@Override
	public void onInit(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
