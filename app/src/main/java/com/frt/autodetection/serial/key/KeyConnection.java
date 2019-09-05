package com.frt.autodetection.serial.key;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.frt.autodetection.serial.IKeyService;


public class KeyConnection implements ServiceConnection {
	public KeyCallback keyCallback = new KeyCallback();
	public IKeyService keyService = null;

	@Override
	public void onServiceConnected(ComponentName name, IBinder serv) {
		keyService = IKeyService.Stub.asInterface(serv);
		try {
			keyService.registerCallback(keyCallback);
			Log.d("Key Service", "onServiceConnected !!!!!!!!!!!!!!!!!!!!! ");
		} catch (RemoteException ex) {
			Log.e("Key Service", "onServiceConnected failed! ", ex);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.d("Key Service", "onServiceDisconnected !!!!!!!!!!!!!!!!!!!!! ");
		keyService = null;
	}
}
