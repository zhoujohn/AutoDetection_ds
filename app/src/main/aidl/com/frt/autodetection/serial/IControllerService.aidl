package com.frt.autodetection.serial;

import com.frt.autodetection.serial.IControllerCallback;

interface IControllerService {
	void registerCallback(IControllerCallback callback);
	void unregisterCallback(IControllerCallback callback);
	
	void write(boolean open);
}