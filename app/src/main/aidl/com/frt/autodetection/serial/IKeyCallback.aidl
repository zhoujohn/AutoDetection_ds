package com.frt.autodetection.serial;

interface IKeyCallback{

	// type: 0： 按键， 1：电机， 2：状态
	void onkey(int type,int key,int status);
}