package com.frt.autodetection.serial;

import com.frt.autodetection.serial.IOBDCallback;

interface IOBDService {
	void registerCallback(IOBDCallback callback);
	void unregisterCallback(IOBDCallback callback);
	
//setting
	void readStatus();
}