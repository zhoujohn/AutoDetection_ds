package com.frt.autodetection.serial.controller;

import android.os.RemoteException;

import com.frt.autodetection.serial.IControllerCallback;
import com.frt.autodetection.serial.SerialCallback;
import com.frt.autodetection.serial.SerialManager;

public class ControllerCallback extends IControllerCallback.Stub {

	/**
	 * type: 1： 按键，2：状态
	 * 
	 * key 1：接听；2：挂断；3：+； 4：-；5：模式；，6:语音；7：配对
	 * 
	 * key 1: 正常；2：故障
	 * 
	 * status 1：按下；2：抬起；3：长按；
	 */
	@Override
	public void onkey(int type, int key, int status) throws RemoteException {
		SerialCallback call = SerialManager.get().getCallback();
		if (call != null) {
			call.oncontroller(type, key, status);
		}
	}

}
