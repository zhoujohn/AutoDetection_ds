package com.frt.autodetection.serial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.frt.autodetection.serial.controller.ControllerConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SerialManager {
	private static SerialManager _instance = new SerialManager();
	private boolean restart = false;
	private ControllerConnection controller = new ControllerConnection();
	private Intent controllerIntent = new Intent("com.frt.autodetection.serial.controller.ControllerService");
	private SerialCallback callback;

	public static SerialManager get() {
		return _instance;
	}

	public void init(Activity activity) {
		boolean ret = false;
		ret = activity.bindService(controllerIntent, controller, Context.BIND_AUTO_CREATE);
		if (!ret) {
			Log.e("SerialManager", "Cannot bind controller service!");
		}

	}
	
	public void initController(Activity activity) {
		boolean ret = false;
		ret = activity.bindService(controllerIntent, controller, Context.BIND_AUTO_CREATE);
		if (!ret) {
			Log.e("SerialManager", "Cannot bind controller service!");
		}
	}

	public void destory(Activity activity) {
		activity.stopService(controllerIntent);
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

	public ControllerConnection getController() {
		return controller;
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
