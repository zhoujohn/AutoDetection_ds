package com.frt.autodetection.serial.obd;


import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.frt.autodetection.serial.IOBDService;

public class OBDConnection implements ServiceConnection {
	public OBDCallback obdCallback = new OBDCallback();
	public IOBDService obdService = null;

	@Override
	public void onServiceConnected(ComponentName name, IBinder serv) {
		obdService = IOBDService.Stub.asInterface(serv);
		try {
			obdService.registerCallback(obdCallback);
			Log.d("OBD Service", "onServiceConnected !!!!!!!!!!!!!!!!!!!!! ");
		} catch (RemoteException ex) {
			Log.e("OBD Service", "onServiceConnected failed! ", ex);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.d("OBD Service", "onServiceDisconnected !!!!!!!!!!!!!!!!!!!!! ");
		obdService = null;
	}
}
