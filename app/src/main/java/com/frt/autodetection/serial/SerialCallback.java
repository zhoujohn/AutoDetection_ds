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

	// type 1: 控制器中作状态状态 --> key(0:回中; 1:自动; 2:手动)
    // type 2: TBD
	public void oncontroller(int type, int key, int status);

}
