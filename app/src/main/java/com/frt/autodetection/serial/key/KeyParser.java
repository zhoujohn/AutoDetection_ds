package com.frt.autodetection.serial.key;


import com.frt.autodetection.serial.SerialStream;

public interface KeyParser {
	public static final int LENGTH = 14;

	public void onBytes(SerialStream stream);
}
