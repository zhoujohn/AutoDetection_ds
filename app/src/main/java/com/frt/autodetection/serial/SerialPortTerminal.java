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

            if (key == 1) {
                switch (status) {
                    case 1:
                        listener.onDialShortDown();
                        break;
                    case 2:
                        listener.onDialShortUp();
                        break;
                    case 3:
                        listener.onDialLongDown();
                        break;
                    case 4:
                        listener.onDialLongtUp();
                        break;
                    case 5:
                        listener.onDial2LongDown();
                        break;
                }

            } else if (key == 2) {
                switch (status) {
                    case 1:
                        listener.onHangupShortDown();
                        break;
                    case 2:
                        listener.onHangupShortUp();
                        break;
                    case 3:
                        listener.onHangupLongDown();
                        break;
                    case 4:
                        listener.onHangupLongtUp();
                        break;
                    case 5:
                        listener.onHangup2LongDown();
                        break;
                }

            } else if (key == 3) {
                switch (status) {
                    case 1:
                        listener.onPlusShortDown();
                        break;
                    case 2:
                        listener.onPlusShortUp();
                        break;
                    case 3:
                        listener.onPlusLongDown();
                        break;
                    case 4:
                        listener.onPlusLongtUp();
                        break;
                    case 5:
                        listener.onPlus2LongDown();
                        break;
                }

            } else if (key == 4) {
                switch (status) {
                    case 1:
                        listener.onMinusShortDown();
                        break;
                    case 2:
                        listener.onMinusShortUp();
                        break;
                    case 3:
                        listener.onMinusLongDown();
                        break;
                    case 4:
                        listener.onMinusLongtUp();
                        break;
                    case 5:
                        listener.onMinus2LongDown();
                        break;
                }

            } else if (key == 5) {
                switch (status) {
                    case 1:
                        listener.onMKeyShortDown();
                        break;
                    case 2:
                        listener.onMKeyShortUp();
                        break;
                    case 3:
                        listener.onMKeyLongDown();
                        break;
                    case 4:
                        listener.onMKeyLongtUp();
                        break;
                    case 5:
                        listener.onMKey2LongDown();
                        break;
                }
            } else if (key == 6) {
                switch (status) {
                    case 1:
                        listener.onVoiceShortDown();
                        break;
                    case 2:
                        listener.onVoiceShortUp();
                        break;
                    case 3:
                        listener.onVoiceLongDown();
                        break;
                    case 4:
                        listener.onVoiceLongtUp();
                        break;
                    case 5:
                        listener.onVoice2LongDown();
                        break;
                }
            }

        }

        ;
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

    public void white(String cmd) throws RemoteException {
        mService.writeStr(cmd);
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


}
