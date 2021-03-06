package com.frt.autodetection.serial.controller;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.view.KeyEvent;

import com.frt.autodetection.serial.IControllerCallback;
import com.frt.autodetection.serial.SerialStream;
import com.frt.autodetection.serial.SerialPortTerminal;
import android.os.RemoteException;

import java.io.IOException;
import java.io.OutputStream;


public class ControllerParserImpl implements ControllerParser {
	private RemoteCallbackList<IControllerCallback> callbacks;
	private OutputStream os;

	public ControllerParserImpl(RemoteCallbackList<IControllerCallback> callbacks) {
		this.callbacks = callbacks;
	}

	@Override
	public synchronized void onBytes(SerialStream stream) {
		stream.setOffset(1);
		while (stream.getLength() >= LENGTH) {
			int value = stream.nextInt(); //stream.nextByte();

			if (value != 0xA5) {
				continue;
			}
			//value = stream.getInt(1);
			//data[0] = (byte)value;
			//value = stream.getInt(2);
			//data[1] = (byte)value;
			value = stream.getInt(5);
			//data[2] = (byte)value;
			//try {
			//	SerialPortTerminal.getInstance().whiteByte(data);
			//} catch (RemoteException e) {
			//	e.printStackTrace();
			//}
			if (value != 0x16) {
				stream.skip(LENGTH - 1);
				continue;
			}
			//try {
			//	SerialPortTerminal.getInstance().whiteByte(data);
			//} catch (RemoteException e) {
			//	e.printStackTrace();
			//}
			int fcode = stream.getInt(1);
			int kcode = stream.getInt(2);
			stream.skip(LENGTH - 1);
			sendCode(fcode, kcode, 0);
			//dispatchKey(fcode, kcode, kstatus);
		}
	}
	
	private void dispatchKey(int fcode, int kcode, int kstatus) {
		String keycode = "input keyevent ";
		int keyvalue = 0;
		
		if (fcode == 1) {
			if (kstatus == 0x01) {
				switch (kcode) {
				case 0x01:
					keyvalue = KeyEvent.KEYCODE_0;
					break;
				case 0x02:
					keyvalue = KeyEvent.KEYCODE_1;
					break;
				case 0x03:
					keyvalue = KeyEvent.KEYCODE_2;
					break;
				case 0x04:
					keyvalue = KeyEvent.KEYCODE_3;
					break;
				case 0x05:
					keyvalue = KeyEvent.KEYCODE_4;
					break;
				case 0x06:
					keyvalue = KeyEvent.KEYCODE_5;
					break;
				case 0x07:
					keyvalue = KeyEvent.KEYCODE_6;
					break;
				}
				
			} else if (kstatus == 0x02) {
				switch (kcode) {
				case 0x01:
					keyvalue = KeyEvent.KEYCODE_A;
					break;
				case 0x02:
					keyvalue = KeyEvent.KEYCODE_B;
					break;
				case 0x03:
					keyvalue = KeyEvent.KEYCODE_C;
					break;
				case 0x04:
					keyvalue = KeyEvent.KEYCODE_D;
					break;
				case 0x05:
					keyvalue = KeyEvent.KEYCODE_E;
					break;
				case 0x06:
					keyvalue = KeyEvent.KEYCODE_F;
					break;
				case 0x07:
					keyvalue = KeyEvent.KEYCODE_G;
					break;
				}
				
			} else if (kstatus == 0x03) {
				switch (kcode) {
				case 0x01:
					keyvalue = KeyEvent.KEYCODE_H;
					break;
				case 0x02:
					keyvalue = KeyEvent.KEYCODE_I;
					break;
				case 0x03:
					keyvalue = KeyEvent.KEYCODE_J;
					break;
				case 0x04:
					keyvalue = KeyEvent.KEYCODE_K;
					break;
				case 0x05:
					keyvalue = KeyEvent.KEYCODE_L;
					break;
				case 0x06:
					keyvalue = KeyEvent.KEYCODE_M;
					break;
				case 0x07:
					keyvalue = KeyEvent.KEYCODE_N;
					break;
				}
			}
		} else if (fcode == 2) {
			
		}
		
		if (keyvalue == 0) {
			return;
		}
		
		/* 
		 * run shell command
		 */
		try {
			if (os == null) {
				os = Runtime.getRuntime().exec("su").getOutputStream();
			}
			keycode = keycode + keyvalue + "\n";
			os.write(keycode.getBytes());
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void dispatchKey1(int fcode, int kcode, int kstatus) {
		String keyCommand = "input keyevent" + KeyEvent.KEYCODE_MENU;
		Runtime runtime = Runtime.getRuntime();
		try {
			Process proc = runtime.exec(keyCommand);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private void sendCode(int type, int kcode, int kstatus) {
		int i = callbacks.beginBroadcast();
		while (i > 0) {
			i--;
			IControllerCallback callback = callbacks.getBroadcastItem(i);
			try {
				callback.onkey(type, kcode, kstatus);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		callbacks.finishBroadcast();
	}

}
