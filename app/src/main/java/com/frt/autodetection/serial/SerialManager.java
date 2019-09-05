package com.frt.autodetection.serial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.frt.autodetection.serial.key.KeyConnection;
import com.frt.autodetection.serial.obd.OBDConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SerialManager {
	private static SerialManager _instance = new SerialManager();
	private boolean restart = false;
	private OBDConnection obd = new OBDConnection();
	private KeyConnection key = new KeyConnection();
	private Intent keyIntent = new Intent("com.horizonx.serial.key.KeyService");
	private Intent obdIntent = new Intent("com.horizonx.serial.obd.OBDService");
	private SerialCallback callback;

	public static SerialManager get() {
		return _instance;
	}

	public void init(Activity activity) {
		boolean ret = false;
		ret = activity.bindService(keyIntent, key, Context.BIND_AUTO_CREATE);
		if (!ret) {
			Log.e("SerialManager", "Cannot bind key service!");
		}
		
	   ret = activity.bindService(obdIntent, obd, Context.BIND_AUTO_CREATE); 
	   if (!ret) { 
		   Log.e("SerialManager", "Cannot bind obd service!"); 
	   }
	}
	
	public void initKey(Activity activity) {
		boolean ret = false;
		ret = activity.bindService(keyIntent, key, Context.BIND_AUTO_CREATE);
		if (!ret) {
			Log.e("SerialManager", "Cannot bind key service!");
		}
	}
	
	public void initObd(Activity activity) {
		boolean ret = false;
		ret = activity.bindService(obdIntent, obd, Context.BIND_AUTO_CREATE);
		if (!ret) {
			Log.e("SerialManager", "Cannot bind obd service!");
		}
	}

	public void destory(Activity activity) {
		activity.stopService(keyIntent);
	}

	private void writePort(String port, byte[] command) {
		FileOutputStream outputStream = null;
		try {
			File file = new File(port);
			outputStream = new FileOutputStream(file);
			outputStream.write(command);
		} catch (IOException ex) {
			Log.e("SerialManager", "Cannot write port " + port, ex);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception ex) {
				}
				outputStream = null;
			}
		}
	}

	public OBDConnection getObd() {
		return obd;
	}

	public KeyConnection getKey() {
		return key;
	}

	public boolean isRestart() {
		return restart;
	}

	public SerialCallback getCallback() {
		return callback;
	}

	public void setCallback(SerialCallback callback) {
		this.callback = callback;
	}
}
