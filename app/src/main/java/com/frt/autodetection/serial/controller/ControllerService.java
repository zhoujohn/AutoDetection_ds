package com.frt.autodetection.serial.controller;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.util.Log;

import com.frt.autodetection.serial.IControllerCallback;
import com.frt.autodetection.serial.SerialManager;
import com.frt.autodetection.serial.SerialPort;
import com.frt.autodetection.serial.SerialStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ControllerService extends Service {
	public static final String TAG = "ControllerService";
	public static final int MSG_START_SERIAL = 1;
	public static final int MSG_SERIAL_RECEIVED = 2;
	private static final int RESTART_DELAY = 1200; // ms

	private SerialThread serialThread = null;
	private volatile boolean running = false;
	private RemoteCallbackList<IControllerCallback> callbacks = null;
	private Handler handler = null;
	private ControllerParser parser = null;

	@Override
	public void onCreate() {
		super.onCreate();
		start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int ret = super.onStartCommand(intent, flags, startId);
		start();
		return ret;
	}

	private void start() {
		if (callbacks != null) {
			return;
		}
		handler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == MSG_START_SERIAL) {
					serialThread = new SerialThread();
					serialThread.start();
				} else if (msg.what == MSG_SERIAL_RECEIVED) {
					SerialStream stream = (SerialStream) msg.obj;
					parser.onBytes(stream);
				}
			};
		};
		callbacks = new RemoteCallbackList<IControllerCallback>();
		parser = new ControllerParserImpl(callbacks);
		handler.sendEmptyMessage(MSG_START_SERIAL);
		running = true;
	}

	@Override
	public void onDestroy() {
		running = false;
		if (callbacks != null) {
			callbacks.kill();
			callbacks = null;
		}
		handler = null;
		parser = null;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("进入了onBind方法");
		return new ControllerServiceImpl(this);
	}

	public void registerCallback(IControllerCallback callback) {
		Log.d("Controller", "ControllerService registerCallback");
		callbacks.register(callback);
	}

	public void unregisterCallback(IControllerCallback callback) {
		Log.d("Controller", "ControllerService unregisterCallback");
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

	public void sendRecvMsg(SerialStream stream) {
		if (handler != null) {
			Message msg = new Message();
			msg.what = MSG_SERIAL_RECEIVED;
			msg.obj = stream;
			handler.sendMessage(msg);
		}
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
				serial = new SerialPort(new File("/dev/ttyS4"), 19200, 0); // : Controller through RS232
				inputStream = serial.getInputStream();
				outputStream = serial.getOutputStream();
				while (running) {
					if (inputStream.available() > 0) {
						n = inputStream.read(buffer);
						if (n < 0) {
							throw new IOException("n == -1");
						}
						if (n > 0) {
							stream.put(buffer, n);
							if (stream.getLength() >= ControllerParser.LENGTH) {
								sendRecvMsg(stream);
							}
						}
					} else {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
						}
					}
				}
			} catch (IOException ex) {
				if (running && SerialManager.get().isRestart())
					handler.sendEmptyMessageDelayed(MSG_START_SERIAL, RESTART_DELAY);
			} finally {
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
