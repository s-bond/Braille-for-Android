package com.example.bharatibraille;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import com.example.bharatibraille.Points;
import com.example.bharatibraille.ShakeEventListener;
import com.example.bharatibraille.Words;

import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class Braille extends Activity implements OnInitListener, OnGesturePerformedListener {

	//  Variables //
	Canvas c;
	Paint p;
	ImageView v;
	Bitmap bm;
	EditText et;
	DisplayMetrics metrics;
	Points P;
	TextToSpeech tts;
	Vibrator vib;
	SensorManager MySensorManager;
	ShakeEventListener MySensorListener;
	Thread th_words, th_points, th_file;
	GestureLibrary GLib;
	GestureOverlayView GView;
	Intent in;
	Words word;
	HandleFile Bfile;
	boolean global, check_save;
	boolean touch[];
	float dp = 120f;//70
	float fpixels;
	static final int num = 8;
	float xc[] = new float[num];
	float yc[] = new float[num];	
	String Local[], All, Speak, Local_speak, Languages[], Save, SaveSpeak, FileName, SString[];
	int NumberOfLanguages, _counter, SaveCount, _del, Sback;	

	//////////////////////////////////////////

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Load_Description();
		SetAll();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.braille, menu);
		return true;
	}

	public void _make_button(MotionEvent e){
		int action = (e.getAction() & MotionEvent.ACTION_MASK);
		int count = e.getPointerCount();
		if( count == num && (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) ){			
			bm = Bitmap.createBitmap(v.getWidth(),v.getHeight(),Config.ARGB_8888);		
			c = new Canvas(bm);
			for(int k=0;k<count;k++){
				int _id = e.getPointerId(k);
				int _idx = e.getPointerId(_id);
				float x = e.getX(_idx); float y = e.getY(_idx) - fpixels;
				xc[k] = x; yc[k] = y;
				c.drawCircle(x, y, 63, p);				
			}
			p.setStyle(Paint.Style.FILL);
			p.setColor(Color.rgb(101, 50, 50));
			for(int k=0;k<num;k++)
				c.drawCircle(xc[k], yc[k], 50, p);
			P = new Points(xc,yc);
			th_points = new Thread(P);
			v.setImageBitmap(bm);			
			tts.speak("ready", TextToSpeech.QUEUE_FLUSH, null);
			global = true;
		}
		else if(count < num && action==MotionEvent.ACTION_POINTER_UP){
			tts.speak("Please Retry", TextToSpeech.QUEUE_FLUSH, null);
			tts.speak("eight fingers are required", TextToSpeech.QUEUE_ADD, null);
		}

	}

	public void _reset_button(){
		global = false;
		bm = Bitmap.createBitmap(v.getWidth(),v.getHeight(),Config.ARGB_8888);		
		c = new Canvas(bm);		
		c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		v.setImageBitmap(bm);
		tts.speak("area cleared", TextToSpeech.QUEUE_FLUSH, null);
		tts.speak("ready for re draw", TextToSpeech.QUEUE_ADD, null);
	}

	public void handle_button(MotionEvent e){
		int count = e.getPointerCount();
		int action = (e.getAction() & MotionEvent.ACTION_MASK);		
		if( (action==MotionEvent.ACTION_DOWN||action == MotionEvent.ACTION_POINTER_DOWN) && count <= num){
			int k;
			for(k=0;k<num;k++) {
				touch[k] = false;xc[k]=yc[k]=(float)0;
			}
			for(k=0;k<count;k++){
				int _id = e.getPointerId(k);
				int _idx = e.getPointerId(_id);
				float x = e.getX(_idx); float y = e.getY(_idx) - fpixels;
				xc[k] = x; yc[k] = y;
			}

			touch = P.click(xc, yc);
			P.press(touch,this);
		}
		else if(action == MotionEvent.ACTION_UP){
			P.release(this);
			if(touch[0] == true && touch[7] == true){

			}
			else if(touch[0] == true){
				All += " ";
				tts.speak(Speak, TextToSpeech.QUEUE_FLUSH, null);
				Speak = "";
				SString[++Sback] = " ";
			}
			else if(touch[7] == true){
				_del = All.length()-1;
				//Log.i("Sback", Integer.toString(Sback));
				
				if(_del>=0){
					if(All.charAt(_del)!=' ')
						tts.speak(SString[Sback]+" deleted", TextToSpeech.QUEUE_FLUSH, null);
					else
						tts.speak("deleted", TextToSpeech.QUEUE_FLUSH, null);
					All = All.substring(0, All.length()-1);
					Sback --;
				}				
				
				//Log.i("Sback", Integer.toString(Sback));
				if(Speak.length()>1)
					Speak = Speak.substring(0, Speak.length()-2);
				else
					Speak = "";
			}
			else{				
				Local = word.get_Word(touch);
				if(!Local[0].equals("-1")){
					All += Local[0];
					tts.speak(Local[1], TextToSpeech.QUEUE_FLUSH, null);
					Speak += Local[1];
					if(SaveCount!=1)
						SString[++Sback] = Local[1];
				}
				int k;
				for(k=0;k<num;k++) {
					touch[k] = false;xc[k]=yc[k]=(float)0;
				}

			}
			vib.vibrate(99);
			et.setText("");
			et.setText(All);
			et.setSelection(All.length());
		}
		
	}

	public boolean onTouchEvent(MotionEvent event){
		if(global==false){
			p.setColor(Color.RED);
			p.setStyle(Paint.Style.STROKE);
			_make_button(event);
		}
		else
			handle_button(event);

		return true;
	}

	@Override
	public void onInit(int arg0) {
	}

	void Load_Description(){
		int Id = R.raw.description;
		InputStream is = this.getResources().openRawResource(Id);
		BufferedReader br = new BufferedReader( new InputStreamReader(is));		
		String read = null;
		int i = 0;
		try {
			while((read = br.readLine()) != null){				
				if(i==0){
					NumberOfLanguages = Integer.parseInt(read);
					Languages = new String[ NumberOfLanguages ];
					i ++;
				}
				else{
					Languages[i-1] = read;					
					i ++;
				}
			}
			//Log.e("Load","Description Loaded");
			is.close();
			br.close();

		} catch (NumberFormatException e) {
			//Log.e("at description lodaer","format exception");
			e.printStackTrace();
		} catch (IOException e) {
			//Log.e("at description loader","IO exception");
			e.printStackTrace();
		}
	}

	@Override
	public void onGesturePerformed(GestureOverlayView gv, Gesture g) {
		ArrayList<Prediction>predictions = GLib.recognize(g);
		if(predictions.size()>0){
			Prediction p = predictions.get(0);
			if(p.score > 1.0){
				//@Bond My methods are called Here :)
				if(p.name.equals("Arc")){
					Toast.makeText(this, Languages[_counter], Toast.LENGTH_SHORT).show();
					tts.speak(Languages[_counter], TextToSpeech.QUEUE_ADD, null);
					word 	 = new Words(Languages[_counter], this);					
					th_words = new Thread(word);
					th_words.start();
					_counter++;
					if(_counter==NumberOfLanguages)
						_counter = 0;					
				}
				else if(p.name.equals("BArc")){
					//Toast.makeText(this, p.name, Toast.LENGTH_SHORT).show();
				}
				else if(p.name.equals("Forward") || p.name.equals("Backward")){
					Toast.makeText(this, "File mode", Toast.LENGTH_SHORT).show();
					tts.speak("file mode", TextToSpeech.QUEUE_ADD, null);
					in = new Intent(this,DisplayFile.class);
					startActivity(in);
					//Log.e("Activity","DisplayFile started");					
				}
				else if(p.name.equals("Save")){
					if(SaveCount == 0 && All.length()>0 && All.charAt(0)!=' ' ){
						tts.speak("enter file name",TextToSpeech.QUEUE_FLUSH,null);
						Toast.makeText(this,"Enter File Name", Toast.LENGTH_SHORT).show();
						Save = All;
						All = "";
						et.setText("");
						SaveCount++;
					}
					else if(SaveCount == 1 && All.length()>0 && All.charAt(0)!=' '){
						FileName  = All;
						SaveSpeak = "";
						for(int i=1;i<Sback;i++)
							SaveSpeak += SString[i];
						Bfile.Write(Save, FileName);
						Bfile.Write(SaveSpeak, FileName+"_speak");
						SaveCount = 0;						
						tts.speak(FileName +" saved", TextToSpeech.QUEUE_FLUSH, null);
						Toast.makeText(this, FileName+" saved", Toast.LENGTH_SHORT).show();
						et.setText("");
						All=""; Save=""; SaveSpeak="";Speak=""; Sback = 0;
					}
					else{
						tts.speak("empty field", TextToSpeech.QUEUE_FLUSH, null);
						Toast.makeText(this, "Empty Field", Toast.LENGTH_SHORT).show();
					}						
				}
			}
		}
	}

	public void SetAll(){
		setContentView(R.layout.activity_braille);
		///////////////////////////////////////
		et = (EditText)findViewById(R.id.et1);
		et.setInputType(InputType.TYPE_NULL);
		et.setTextSize((float)34);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		///////////////////////////////////////
		tts = new TextToSpeech(this,this);
		tts.setLanguage(Locale.ENGLISH);
		tts.setPitch((float)1.0);
		tts.setSpeechRate((float)0.80);
		//////////////////////////////////////
		v = (ImageView)findViewById(R.id.image1);		
		p = new Paint();				
		p.setAntiAlias(true);
		p.setStrokeWidth(7);
		//////////////////////////////////////
		metrics = getBaseContext().getResources().getDisplayMetrics();
		///////////////////////////////////////
		global 	  = false;
		touch 	  = new boolean[num];
		fpixels   = metrics.density * dp;
		All 	  = new String("");
		Speak 	  = new String("");
		Save	  = new String("");
		SaveSpeak = new String("");
		Local 	  = new String[2];
		SString	  = new String[1001];
		_counter  = 0;
		SaveCount = 0;
		Sback     = 0;
		///////////////////////////////////////
		MySensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		MySensorListener = new ShakeEventListener();
		MySensorManager.registerListener(MySensorListener, MySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI);
		MySensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

			@Override
			public void onShake() {
				Toast.makeText(Braille.this, "Reset", Toast.LENGTH_SHORT).show();
				_reset_button();
			}
		});	
		////////////////////////////////////////
		tts.speak("Welcome", TextToSpeech.QUEUE_ADD, null);
		Toast.makeText(Braille.this, "स्वागत है", Toast.LENGTH_SHORT).show();
		////////////////////////////////////////
		GLib = GestureLibraries.fromRawResource(this, R.raw.gestures);//Context,ID		
		if(!GLib.load()){
			Log.e("Gesture","Library Not Found");
			finish();
		}
		GView = (GestureOverlayView) findViewById(R.id.gestures);
		GView.addOnGesturePerformedListener(this);
		////////////////////////////////////////
		vib = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
		////////////////////////////////////////
		Bfile = new HandleFile();
		th_file = new Thread(Bfile);
		th_file.start();
		////////////////////////////////////////
		word 	 = new Words(Languages[_counter], this);					
		th_words = new Thread(word);
		th_words.start();
		_counter++;
		////////////////////////////////////////
	}

	public boolean onOptionsItemSelected(MenuItem item){
		String set_this = "We are Enthusiastic Programmers @IIIT Allahabad\n\n";
		set_this +="Siddharth Maloo(s_bond,sidhs) sidhs.m1@gmail.com\n";
		set_this +="Pulkit Tandon(explorepulkit) explorepulkit@gmail.com\n";
		set_this +="Prakhar Agrawal(prakhar3agrwal) prakhar.agrawal31291@gmail.com\n\n";		
		set_this += "Comments and suggestions are most welcome :)";
		// In manifest : android:theme="@android:style/Theme.NoTitleBar.Fullscreen"
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setMessage(set_this);
		dialogBuilder.setCancelable(true);
		dialogBuilder.setTitle("About Us");
		dialogBuilder.setInverseBackgroundForced(true);
		dialogBuilder.setIcon(R.drawable.about_us);
		dialogBuilder.setPositiveButton("Got it", null);
		dialogBuilder.create().show();
		return true;		
	}
	
	
}