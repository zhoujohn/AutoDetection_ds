package com.frt.autodetection.serial.controller;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.frt.autodetection.serial.IControllerService;


public class ControllerConnection implements ServiceConnection {
	public ControllerCallback controllerCallback = new ControllerCallback();
	public IControllerService controllerService = null;

	@Override
	public void onServiceConnected(ComponentName name, IBinder serv) {
		controllerService = IControllerService.Stub.asInterface(serv);
		try {
			controllerService.registerCallback(controllerCallback);
			Log.d("Controller Service", "onServiceConnected !!!!!!!!!!!!!!!!!!!!! ");
		} catch (RemoteException ex) {
			Log.e("Controller Service", "onServiceConnected failed! ", ex);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.d("Controller Service", "onServiceDisconnected !!!!!!!!!!!!!!!!!!!!! ");
		controllerService = null;
	}
}
