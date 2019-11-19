package com.frt.autodetection.serial.controller;


import com.frt.autodetection.serial.SerialStream;

public interface ControllerParser {
	public static final int LENGTH = 6;

	public void onBytes(SerialStream stream);
}
