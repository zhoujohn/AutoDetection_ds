package com.frt.autodetection.serial.obd;

import android.os.RemoteException;

import com.frt.autodetection.serial.IOBDCallback;
import com.frt.autodetection.serial.SerialCallback;
import com.frt.autodetection.serial.SerialManager;


public class OBDCallback extends IOBDCallback.Stub {

	/**
	 * 车速 speed Km/h 转速 round rpm 每小时油耗 oil L/H 每百公里油耗 oil L/100Km
	 */
	@Override
	public void onCarStatus(int speed, int rpm, int engineStatus, int oilHour, int oilMiles) throws RemoteException {
		SerialCallback call = SerialManager.get().getCallback();
		if (call != null) {
			call.onCarStatus(speed, rpm, oilHour, oilMiles);
		}
		
		// send OBD broadcast
		//Intent intent = new Intent("com.horizonx.serial.obd");
		//intent.putExtra("speed", speed);
		//intent.putExtra("rpm", rpm);
		//intent.putExtra("oilperhour", oilHour);
		//intent.putExtra("oilpermiles", oilMiles);
		
		//sendBroadcast(intent);		
	}

}
