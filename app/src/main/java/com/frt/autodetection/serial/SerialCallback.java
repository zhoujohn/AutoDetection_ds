package com.frt.autodetection.serial;

/**
 *
 * 
 * @author
 * @link 
 * @Copyright
 */
public interface SerialCallback {

	/**
	 * 状态变化
	 *  
	 * @param mobile
	 */
	public void onStateChanged(int state);

	/**
	 * 获取手机数据
	 * 
	 * @param data
	 */
	public void onGetData(String data);

	public void oncontroller(int type, int key, int status);

}
