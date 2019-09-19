package com.frt.autodetection.serial.controller;

import android.os.RemoteException;

import com.frt.autodetection.serial.IControllerCallback;
import com.frt.autodetection.serial.IControllerService;


public class ControllerServiceImpl extends IControllerService.Stub {
	private ControllerService service;

	public ControllerServiceImpl(ControllerService service) {
		this.service = service;
	}

	@Override
	public void registerCallback(IControllerCallback callback) throws RemoteException {
		service.registerCallback(callback);
	}

	@Override
	public void unregisterCallback(IControllerCallback callback) throws RemoteException {
		service.unregisterCallback(callback);
	}

	public void write(String str) {
		service.write(str);
	}

	public void write(byte[] command) {
		service.write(command);
	}

	public void write(boolean open) {
		int check = 0;
		int pos = 0;
		byte[] command = new byte[14];
		command[pos++] = 0x68;
		command[pos++] = 0;
		command[pos++] = 0;
		command[pos++] = 0;
		command[pos++] = 0;
		command[pos++] = 0;
		command[pos++] = 0;
		command[pos++] = 0;
		command[pos++] = 0;
		command[pos++] = 0x01;
		command[pos++] = (byte) (open ? 0x01 : 0x02);
		command[pos++] = 0;
		for (int i = 0; i < pos; i++) {
			int val = command[i];
			val = val & 0xff;
			check += val;
		}
		command[pos++] = (byte) (check & 0xff);
		command[pos++] = 0x16;
		service.write(command);
	}
}
