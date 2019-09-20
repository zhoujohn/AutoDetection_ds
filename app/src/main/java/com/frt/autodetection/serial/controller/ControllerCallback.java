package com.frt.autodetection.serial.controller;

import android.os.RemoteException;

import com.frt.autodetection.serial.IControllerCallback;
import com.frt.autodetection.serial.SerialCallback;
import com.frt.autodetection.serial.SerialManager;

public class ControllerCallback extends IControllerCallback.Stub {


	@Override
	public void onkey(int type, int key, int status) throws RemoteException {
		SerialCallback call = SerialManager.get().getCallback();
		if (call != null) {
			call.oncontroller(type, key, status);
		}
	}

}
