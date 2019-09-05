package com.frt.autodetection.serial;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {

	private static final String TAG = "SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {

		/* Check access permission */
		/* Missing read/write permission, trying to chmod the file */
		/*
		 * if (!device.canRead() || !device.canWrite()) { try { Process su = Runtime.getRuntime().exec("/system/bin/su"); if (su != null) { String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
		 * + "exit\n"; su.getOutputStream().write(cmd.getBytes()); if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) { throw new SecurityException(); } } } catch (Exception ex) {
		 * Log.e(TAG, "native run su fail " + device, ex); throw new SecurityException(); } }
		 */
		System.out.println("进入SerialPort的构造方法");
		mFd = open(device.getPath(), baudrate, flags);
		System.out.println("open方法之后");
		if (mFd == null) {
			Log.e(TAG, "native open returns null, device=" + device);
			System.out.println("为null了");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
		System.out.println("获取数据的交换流");
	}

	// Getters and setters
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	// JNI
	private native static FileDescriptor open(String path, int baudrate, int flags);

	public native void close();

	static {
		System.loadLibrary("serial_port");
	}
}
