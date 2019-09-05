package com.frt.autodetection.serial;

/**
 * 串口响应接口
 * 
 * @author Horizonx
 * @link 
 * @Copyright BeiJing Horizonx Tech Co.LTD
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

	/**
	 * type: 1： 按键，2：状态
	 * 
	 * key 1：接听；2：挂断；3：+； 4：-；5：模式；6：配对w
	 * 
	 * key 1: 正常；2：故障
	 * 
	 * status 1：按下；2：抬起；3：长按；
	 */
	public void onkey(int type, int key, int status);

	/**
	 * 车速 speed Km/h 转速 round rpm 每小时油耗 oil L/H 每百公里油耗 oil L/100Km
	 */
	public void onCarStatus(int speed, int round, int oilHour, int oilMiles);
}
