package com.frt.autodetection.serial.obd;


import com.frt.autodetection.serial.SerialStream;

public interface OBDParser {
	public static final int LENGTH = 24;

	public void onBytes(SerialStream stream, OBDService service);

}
