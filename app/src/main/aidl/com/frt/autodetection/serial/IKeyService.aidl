package com.frt.autodetection.serial;

import com.frt.autodetection.serial.IKeyCallback;

interface IKeyService {
	void registerCallback(IKeyCallback callback);
	void unregisterCallback(IKeyCallback callback);
	
	void write(boolean open);
}