package com.frt.autodetection.serial.obd;

import android.os.RemoteException;

import com.frt.autodetection.serial.IOBDCallback;
import com.frt.autodetection.serial.IOBDService;


public class OBDServiceImpl extends IOBDService.Stub {
	private OBDService service;

	public OBDServiceImpl(OBDService service) {
		this.service = service;
	}

	@Override
	public void registerCallback(IOBDCallback callback) throws RemoteException {
		service.registerCallback(callback);
	}

	@Override
	public void unregisterCallback(IOBDCallback callback) throws RemoteException {
		service.unregisterCallback(callback);
	}

	public void write(String str) {
		service.write(str);
	}

	public void write(byte[] command) {
		service.write(command);
	}

	@Override
	public void readStatus() throws RemoteException {
		byte[] command = new byte[8];
		command[0] = 0x54;
		command[1] = 0x6B;
		command[2] = 0x01;
		command[3] = 0x00;
		command[4] = 0x60;
		command[5] = 0x58;
		command[6] = 0x0D;
		command[7] = 0x0A;
		write(command);
	}
}
