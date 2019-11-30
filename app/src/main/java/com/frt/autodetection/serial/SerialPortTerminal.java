package com.frt.autodetection.serial;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.frt.autodetection.App;
import com.frt.autodetection.serial.controller.ControllerCallback;
import com.frt.autodetection.serial.controller.ControllerService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ================================================
 * 包名：com.frt.autodetection.serial
 * 创建人：sws
 * 创建时间：2019/9/21  下午 02:52
 * 描述：
 * ================================================
 */
public class SerialPortTerminal {

    // 权限

    private ServiceConnection conn;
    private IControllerService mService;
    private static SerialPortTerminal instance;
    private HashMap<Activity, OnKeyEventReceiveListener> eventTask = new HashMap<Activity, OnKeyEventReceiveListener>();
    private OnKeyEventReceiveListener listener;

    private ControllerCallback myCallback = new ControllerCallback() {
        @Override
        public void onkey(int type, int key, int status) {
            checkForegroundPager();

            if (listener == null) {
                return;
            }

            if (type == 0x87) {
                listener.onDialShortDown(key);
            }
        }
    };

    private SerialPortTerminal() {
        Context context = App.getAppContext();
        // 绑定服务
        Intent intent = new Intent(context, ControllerService.class);
        intent.setAction("com.frt.autodetection.serial.ControllerService");

        conn = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                mService = IControllerService.Stub.asInterface(service);
                //服务绑定成功 通知调用串口初始化方法
                if (mOnSerialServiceBindListener != null) {
                    mOnSerialServiceBindListener.OnSerialServiceBind();
                }

                try {
                    mService.registerCallback(myCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);

    }

    private OnSerialServiceBindListener mOnSerialServiceBindListener;

    public void setOnSerialServiceBindListener(OnSerialServiceBindListener listener) {
        mOnSerialServiceBindListener = listener;
    }

    public interface OnSerialServiceBindListener {
        void OnSerialServiceBind();
    }

    public void white(String cmd) throws RemoteException {
        mService.writeStr(cmd);
    }

    public void whiteByte(byte[] cmd) throws RemoteException {
        mService.writeByte(cmd);
    }


    private void checkForegroundPager() {
        Iterator<Map.Entry<Activity, OnKeyEventReceiveListener>> iterator = eventTask
                .entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Activity, OnKeyEventReceiveListener> entry = iterator
                    .next();
            Activity activity = entry.getKey();
            if (isForeground(activity.getClass().getName())) {
                listener = entry.getValue();
                return;
            }
        }
        listener = null;
    }

    public static SerialPortTerminal getInstance() {
        if (instance == null) {
            synchronized (SerialPortTerminal.class) {
                if (instance == null) {
                    instance = new SerialPortTerminal();
                }
            }
        }
        return instance;
    }

    //判断前台的activity
    private boolean isForeground(String className) {
        if (TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) App.getAppContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;

            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void registerEvent(Activity activity,
                              OnKeyEventReceiveListener listener) {
        eventTask.put(activity, listener);
    }

    public void unRegisterEvent(Activity activity,
                                OnKeyEventReceiveListener listener) {
        eventTask.remove(activity);
    }

    public OnKeyEventReceiveListener setOnKeyEventReceiveListener(
            OnKeyEventReceiveListener listener) {
        return listener;
    }

    public void releaseControllerServicesResources() {
        Context context = App.getAppContext();
        context.unbindService(conn);
        Intent intent = new Intent(context, ControllerService.class);
        context.stopService(intent);
        conn = null;
        mService = null;
    }

    /**
     * 纠偏控制指令：A5  83  X1  00  00  16
     * X1= 1  手动
     * X1=2   自动
     * X1=3   回中
     * X1=4   回中结束
     * X1=5   手动IN
     * X1=6   手动OUT
     * 无应答
     */

    public void whiteBtn1() {
        try {
            //X1=5   手动IN
            byte[] cmd = new byte[6];
            cmd[0] = (byte) 0xA5;
            cmd[1] = (byte) 0x83;
            cmd[2] = (byte) 0x05;
            cmd[3] = (byte) 0x00;
            cmd[4] = (byte) 0x00;
            cmd[5] = (byte) 0x16;
            whiteByte(cmd);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void whiteBtn2() {
        try {
            //X1=6   手动OUT
            byte[] cmd = new byte[6];
            cmd[0] = (byte) 0xA5;
            cmd[1] = (byte) 0x83;
            cmd[2] = (byte) 0x06;
            cmd[3] = (byte) 0x00;
            cmd[4] = (byte) 0x00;
            cmd[5] = (byte) 0x16;

            whiteByte(cmd);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void whiteBtn3() {
        try {
            //X1= 1  手动
            byte[] cmd = new byte[6];
            cmd[0] = (byte) 0xA5;
            cmd[1] = (byte) 0x83;
            cmd[2] = (byte) 0x01;
            cmd[3] = (byte) 0x00;
            cmd[4] = (byte) 0x00;
            cmd[5] = (byte) 0x16;

            whiteByte(cmd);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void whiteBtn4() {
        try {
            //X1=2   自动
            byte[] cmd = new byte[6];
            cmd[0] = (byte) 0xA5;
            cmd[1] = (byte) 0x83;
            cmd[2] = (byte) 0x02;
            cmd[3] = (byte) 0x00;
            cmd[4] = (byte) 0x00;
            cmd[5] = (byte) 0x16;

            whiteByte(cmd);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void whiteBtn5() {
        try {
            //X1=3   回中
            byte[] cmd = new byte[6];
            cmd[0] = (byte) 0xA5;
            cmd[1] = (byte) 0x83;
            cmd[2] = (byte) 0x03;
            cmd[3] = (byte) 0x00;
            cmd[4] = (byte) 0x00;
            cmd[5] = (byte) 0x16;

            whiteByte(cmd);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //光源强度调节指令：A5  82  X1  00  00  16
    public void writeBrightness(int brightness) {
        try {
            byte[] cmd = new byte[6];
            cmd[0] = (byte) 0xA5;
            cmd[1] = (byte) 0x82;
            cmd[2] = (byte) brightness;
            cmd[3] = (byte) 0x00;
            cmd[4] = (byte) 0x00;
            cmd[5] = (byte) 0x16;

            whiteByte(cmd);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void writeDeviation(int devi) {
        try {
            byte[] cmd = new byte[6];
            cmd[0] = (byte) 0xA5;
            cmd[1] = (byte) 0x81;
            if (devi == 1000) {
                cmd[2] = 0;
                cmd[3] = 0;
                cmd[4] = (byte)0xFF;
            } else {
                cmd[2] = (byte) (devi/256);
                cmd[3] = (byte) (devi%256);
                cmd[4] = (byte) 0x00;
            }
            cmd[5] = (byte) 0x16;

            whiteByte(cmd);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    //修改数据输出极性指令：A5  84  X1  00  00  16 （x1范围 0~1）
    public void writePolarity(int polarity) {
        try {
            byte[] cmd = new byte[6];
            cmd[0] = (byte) 0xA5;
            cmd[1] = (byte) 0x84;
            cmd[2] = (byte) polarity;
            cmd[3] = (byte) 0x00;
            cmd[4] = (byte) 0x00;
            cmd[5] = (byte) 0x16;

            whiteByte(cmd);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    //修改模块通信地址指令：A5  84  X1  00  00  16 （x1范围 1~2）
    public void writeAddress(int address) {
        try {
            byte[] cmd = new byte[6];
            cmd[0] = (byte) 0xA5;
            cmd[1] = (byte) 0x82;
            cmd[2] = (byte) address;
            cmd[3] = (byte) 0x00;
            cmd[4] = (byte) 0x00;
            cmd[5] = (byte) 0x16;

            whiteByte(cmd);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
