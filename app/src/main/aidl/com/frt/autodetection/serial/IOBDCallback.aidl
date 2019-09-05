package com.frt.autodetection.serial;

interface IOBDCallback{

	void onCarStatus(int speed,int round,int engineStatus,int oilHour,int oilMiles);
}