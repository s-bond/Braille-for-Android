package com.example.bharatibraille;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class ShakeEventListener implements SensorEventListener {

	  int MIN_FORCE = 5;
	  int MIN_DIRECTION_CHANGE = 3;
	  int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 200;
	  int MAX_TOTAL_DURATION_OF_SHAKE = 400;
	  long mFirstDirectionChangeTime = 0;
	  long mLastDirectionChangeTime;
	  int mDirectionChangeCount = 0;
	  float lastX = 0;
	  float lastY = 0;
	  float lastZ = 0;
	  OnShakeListener mShakeListener;
	  public interface OnShakeListener {
	    void onShake();
	  }

	  public void setOnShakeListener(OnShakeListener listener) {
	    mShakeListener = listener;
	  }

	  @Override
	  public void onSensorChanged(SensorEvent se) {
	    float x = se.values[0];
	    float y = se.values[1];
	    float z = se.values[2];
	    float totalMovement = Math.abs(x + y + z - lastX - lastY - lastZ);

	    if (totalMovement > MIN_FORCE) {
	      long now = System.currentTimeMillis();
	      if (mFirstDirectionChangeTime == 0) {
	        mFirstDirectionChangeTime = now;
	        mLastDirectionChangeTime = now;
	      }
	      long lastChangeWasAgo = now - mLastDirectionChangeTime;
	      if (lastChangeWasAgo < MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE) {
	        mLastDirectionChangeTime = now;
	        mDirectionChangeCount++; 
	        lastX = x;
	        lastY = y;
	        lastZ = z;
	        if (mDirectionChangeCount >= MIN_DIRECTION_CHANGE) {
	          long totalDuration = now - mFirstDirectionChangeTime;
	          if (totalDuration < MAX_TOTAL_DURATION_OF_SHAKE) {
	            mShakeListener.onShake();
	            resetShakeParameters();
	          }
	        }
	      } else {
	        resetShakeParameters();
	      }
	    }
	  }
	  private void resetShakeParameters() {
	    mFirstDirectionChangeTime = 0;
	    mDirectionChangeCount = 0;
	    mLastDirectionChangeTime = 0;
	    lastX = 0;
	    lastY = 0;
	    lastZ = 0;
	  }

	  @Override
	  public void onAccuracyChanged(Sensor sensor, int accuracy) {
	  }

	}