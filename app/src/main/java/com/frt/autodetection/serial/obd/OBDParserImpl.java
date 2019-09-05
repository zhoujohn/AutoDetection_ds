package com.frt.autodetection.serial.obd;

import android.os.RemoteCallbackList;
import android.util.Log;

import com.frt.autodetection.serial.IOBDCallback;
import com.frt.autodetection.serial.SerialStream;


public class OBDParserImpl implements OBDParser {
	private RemoteCallbackList<IOBDCallback> callbacks;

	public OBDParserImpl(RemoteCallbackList<IOBDCallback> callbacks) {
		this.callbacks = callbacks;
	}

	@Override
	public synchronized void onBytes(SerialStream stream, OBDService service) {
		while (stream.getLength() >= LENGTH) {
			int value = stream.nextInt();
			if (value != 0x45) {
				continue;
			}
			value = stream.nextInt();
			if (value != 0x4c) {
				continue;
			}
			value = stream.nextInt();
			if (value != 0x01) {
				continue;
			}
			value = stream.nextInt();
			if (value != 0x18) {
				continue;
			}
			int speed = stream.getInt(9);
			int round = stream.getInt2(3);
			int oilHour = stream.getInt(20);
			int oilMiles = stream.getInt(21);
			int engineStatus = stream.getInt(15);
			if (engineStatus !=0) {
				engineStatus = 1;
			}
			stream.skip(LENGTH - 4);
			sendCode(speed, round, engineStatus, oilHour, oilMiles);
			
			//service.parseCallback(speed, round, engineStatus, oilHour, oilMiles);	
		}
	}

	private void sendCode(int speed, int round, int engineStatus, int oilHour, int oilMiles) {
		int i = callbacks.beginBroadcast();
		while (i > 0) {
			i--;
			IOBDCallback callback = callbacks.getBroadcastItem(i);
			try {
				callback.onCarStatus(speed, round, engineStatus, oilHour, oilMiles);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		callbacks.finishBroadcast();
	}

	public void onBytes(byte[] data) {
		if (data[2] == 0xFA) {
			Log.e("OBDParser", "指令帧头错误应答");
			return;
		}
		if (data[2] == 0xFB) {
			Log.e("OBDParser", "CRC校验错误应答");
			return;
		}
		if (data[2] == 0xFC) {
			Log.e("OBDParser", "命令不识别应答");
			return;
		}
		// if (data[3] == 0x01) {
		// int base = 3;
		// int speed = byte2Int(data, base + 9);
		// int round = byte2Int2(data, base + 3);
		// int oilHour = byte2Int(data, base + 20);
		// int oilMiles = byte2Int(data, base + 21);
		// int i = callbacks.beginBroadcast();
		// while (i > 0) {
		// i--;
		// IOBDCallback callback = callbacks.getBroadcastItem(i);
		// try {
		// callback.onCarStatus(speed, round, oilHour, oilMiles);
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
		// }
		// callbacks.finishBroadcast();
		// }
	}

}
