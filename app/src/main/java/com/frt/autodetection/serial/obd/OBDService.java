package com.frt.autodetection.serial.obd;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.util.Log;

import com.frt.autodetection.serial.IOBDCallback;
import com.frt.autodetection.serial.SerialManager;
import com.frt.autodetection.serial.SerialPort;
import com.frt.autodetection.serial.SerialStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OBDService extends Service {
	public static final String TAG = "OBDService";
	public static final int MSG_START_SERIAL = 1;
	public static final int MSG_SERIAL_RECEIVED = 2;
	private static final int RESTART_DELAY = 1200; // ms

	private SerialThread serialThread = null;
	private volatile boolean running = true;
	private RemoteCallbackList<IOBDCallback> callbacks;
	private OBDParser parser = null;

	@Override
	public void onCreate() {
		super.onCreate();
		callbacks = new RemoteCallbackList<IOBDCallback>();
		parser = new OBDParserImpl(callbacks);
		handler.sendEmptyMessage(MSG_START_SERIAL);
	}

	@Override
	public void onDestroy() {
		running = false;
		callbacks.kill();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new OBDServiceImpl(this);
	}

	public void registerCallback(IOBDCallback callback) {
		Log.d("obd", "OBDService registerCallback");
		callbacks.register(callback);
	}

	public void unregisterCallback(IOBDCallback callback) {
		Log.d("obd", "OBDService unregisterCallback");
		callbacks.unregister(callback);
	}

	public void write(String str) {
		if (serialThread != null) {
			serialThread.write((str + "\r\n").getBytes());
		}
	}

	public void write(byte[] command) {
		if (serialThread != null) {
			serialThread.write(command);
		}
	}

	private Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_START_SERIAL) {
				serialThread = new SerialThread();
				serialThread.start();
			} else if (msg.what == MSG_SERIAL_RECEIVED) {
				// byte[] data = (byte[]) msg.obj;
				// parser.onBytes(data);
				SerialStream stream = (SerialStream) msg.obj;
				parser.onBytes(stream, OBDService.this);
			}
		};
	};
	
	public void parseCallback(int speed, int round, int engineStatus, int oilHour, int oilMiles) {
		// send OBD broadcast
		Intent intent = new Intent("com.horizonx.serial.obd");
		intent.putExtra("speed", 1);
		intent.putExtra("rpm", 1);
		intent.putExtra("enginestatus", 1);
		intent.putExtra("oilperhour", 1);
		intent.putExtra("oilpermiles", 1);
		
		sendBroadcast(intent);
	}

	public void sendRecvMsg(SerialStream stream) {
		Message msg = new Message();
		msg.what = MSG_SERIAL_RECEIVED;
		msg.obj = stream;
		handler.sendMessage(msg);
		
	}

	private class SerialThread extends Thread {
		private InputStream inputStream;
		private OutputStream outputStream = null;
		private byte[] buffer = new byte[32];
		private SerialStream stream = new SerialStream();

		public SerialThread() {
		}

		public void write(byte[] buf) {
			if (outputStream != null) {
				try {
					outputStream.write(buf);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		@Override
		public void run() {
			int n;
			SerialPort serial = null;
			try {
				serial = new SerialPort(new File("/dev/ttyMT1"), 9600, 0); // : OBD 115200
				inputStream = serial.getInputStream();
				outputStream = serial.getOutputStream();
				while (running) {
					n = inputStream.read(buffer);
					if (n < 0) {
						throw new IOException("n==-1");
					}
					if (n > 0) {
						stream.put(buffer, n);
						if (stream.getLength() >= OBDParser.LENGTH) {
							sendRecvMsg(stream);
						}
					}
					// byte[] data = new byte[n];
					// System.arraycopy(buffer, 0, data, 0, n);
					// handler.sendMessage(handler.obtainMessage(MSG_SERIAL_RECEIVED, data));
				}
			} catch (IOException ex) {
				if (running && SerialManager.get().isRestart())
					handler.sendEmptyMessageDelayed(MSG_START_SERIAL, RESTART_DELAY);
			}  finally {
				if (serial != null) {
					try {
						serial.close();
					} catch (Exception ex) {
					}
					serial = null;
				}
			}
		}
	}
}
