package com.common.baselib.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.common.baselib.AppConfig;

import java.io.File;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Double.parseDouble;

/**
 * 工具类
 */
public class CommonUtils {


    /**
     * 设置et不可编辑
     *
     * @param editText
     */

    public static void setEtEditMode(EditText editText, boolean canEdit) {
        editText.setFocusable(canEdit);
        editText.setFocusableInTouchMode(canEdit);
    }
    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 复制到粘贴板
     * @param context
     * @param content
     */
    public static void copyToClipboard(Context context, CharSequence content) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText(null, content));//参数一：标签，可为空，参数二：要复制到剪贴板的文本
            if (clipboard.hasPrimaryClip()) {
                clipboard.getPrimaryClip().getItemAt(0).getText();
            }
        }
    }
    public static void checkEditLength(
            final EditText editText, final int maxLength, final String tips) {
        editText.addTextChangedListener(
                new TextWatcher() {

                    /** 需要截取的index */
                    int subIndex;
                    /** 是否需要截取 */
                    boolean isNeedReSet;
                    /** 用户输入的字符 */
                    CharSequence ch;

                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s != null && s.length() > 0) {
                            ch = s;
                            subIndex = maxLength;
                            isNeedReSet = subIndex < s.length();

                            LogUtil.log("ll:" + subIndex + "---s.length():" + s.length());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        LogUtil.log("isNeedReSet:" + isNeedReSet);

                        if (isNeedReSet) {
                            if (s != null && s.length() > 0) {
                                editText.setText(ch.subSequence(0, subIndex));
                                editText.setSelection(editText.length());
                                RxToast.showToast(MessageFormat.format(tips, maxLength));
                            }
                        }
                    }
                });
    }


    /**
     * 列表删除 符合条件的匹配项
     *
     * @param matchList
     * @param targetList
     * @param listener
     * @param <U>
     * @param <T>
     */
    public static <U, T> void listRemove(
            Collection<U> matchList, Collection<T> targetList, OnMatchCallback<U, T> listener) {

        if (isListNull(matchList) || isListNull(targetList)) {
            return;
        }
        Iterator<T> iterator = targetList.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            for (U u : matchList) {
                if (listener.onMatch(u, t)) {
                    iterator.remove();
                }
            }
        }
    }

    public static String getStrForId(int id) {
        return AppConfig.getInstance().mAppContext.getResources().getString(id);
    }

    public static int getColorForId(int id) {
        return AppConfig.getInstance().mAppContext.getResources().getColor(id);
    }
    public static Drawable getDrawableForId(int id) {
        return AppConfig.getInstance().mAppContext.getResources().getDrawable(id);
    }

    public static <U, T> T find(
            Collection<U> matchList, Collection<T> targetList, OnMatchCallback<U, T> listener) {
        if (isListNull(matchList) || isListNull(targetList)) {
            return null;
        }
        Iterator<T> iterator = targetList.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            for (U u : matchList) {
                if (listener.onMatch(u, t)) {
                    return t;
                }
            }
        }
        return null;
    }

    public static <U, T> T find(
            U matchBean, Collection<T> targetList, OnMatchCallback<U, T> listener) {
        if (matchBean == null || isListNull(targetList)) {
            return null;
        }
        Iterator<T> iterator = targetList.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (listener.onMatch(matchBean, t)) {
                return t;
            }
        }
        return null;
    }

    public static <U, T> void listRemove(
            U matchBean, Collection<T> targetList, OnMatchCallback<U, T> listener) {
        if (matchBean == null || isListNull(targetList)) {
            return;
        }
        Iterator<T> iterator = targetList.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (listener.onMatch(matchBean, t)) {
                iterator.remove();
            }
        }
    }

    public static <U, T> void listDeal(
            U matchBean, Collection<T> targetList, OnDealCallback<U, T> listener) {
        listDeal(matchBean, targetList, listener, false);
    }

    public static <U, T> void listDeal(
            U matchBean, Collection<T> targetList, OnDealCallback<U, T> listener, boolean isBreak) {
        if (matchBean == null || isListNull(targetList)) {
            return;
        }
        Iterator<T> iterator = targetList.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (listener.onMatch(matchBean, t)) {
                listener.onDeal(matchBean, t);
                if (isBreak) {
                    break;
                }
            }
        }
    }


    public static interface OnMatchCallback<U, T> {
        boolean onMatch(U matchBean, T target);
    }

    public static interface OnDealCallback<U, T> extends OnMatchCallback<U, T> {
        boolean onMatch(U matchBean, T target);

        void onDeal(U matchBean, T target);
    }

    /**
     * 获取版本名
     *
     * @return 当前应用的版本号
     */
    public static String getVersion() {
        try {
            PackageManager manager = AppConfig.getInstance().mAppContext.getPackageManager();
            PackageInfo info =
                    manager.getPackageInfo(AppConfig.getInstance().mAppContext.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static int getVersionCode() {
        try {
            PackageManager manager = AppConfig.getInstance().mAppContext.getPackageManager();
            PackageInfo info =
                    manager.getPackageInfo(AppConfig.getInstance().mAppContext.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getVersionName() {
        PackageManager packageManager = AppConfig.getInstance().mAppContext.getPackageManager();
        String name = "";
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(AppConfig.getInstance().mAppContext.getPackageName(), 0);
            name = String.valueOf(packInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static <T> boolean isListNull(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static <T> boolean isListNotNull(Collection<T> collection) {
        return !isListNull(collection);
    }

    public static <T> boolean isListNotNull(Map map) {
        return !isListNull(map);
    }

    public static <T> boolean isListNull(Map map) {
        if (map == null || map.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static String getFileName(String extraStr, boolean isNeedTime, boolean isShow) {
        return getFileName(extraStr, isNeedTime, isShow, ".jpg");
    }

    /**
     * @param extraStr   文件名
     * @param isNeedTime 是否需要时间
     * @param isShow     是否需要后缀名
     * @param type       后缀名
     * @return
     */
    public static String getFileName(
            String extraStr, boolean isNeedTime, boolean isShow, String type) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        if (isNeedTime) {
            if (isShow) {
                return format.format(date) + "_" + extraStr + type;
            } else {
                return format.format(date) + "_" + extraStr;
            }
        } else {
            if (isShow) {
                return extraStr + type;
            } else {
                return extraStr;
            }
        }
    }

    // 获取系统版本号显示
    public static String getSystemVersionName(Context context) {
        String versionName = "";
        try {
            versionName =
                    context.getPackageManager()
                            .getPackageInfo(context.getApplicationInfo().packageName, 0)
                            .versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 检测String是否全是中文
     *
     * @param name
     * @return true=全是中文
     */
    public static boolean checkNameChese(String name) {
        boolean res = true;
        char[] cTemp = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            if (!isChinese(cTemp[i])) {
                res = false;
                break;
            }
        }
        return res;
    }

    /**
     * 判定输入汉字
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 防止按钮重复被点击
     */
    public static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static boolean isFastClick(int clickTime) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < clickTime) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 防止按钮重复被点击
     */
    private static int lastClickId = 0;

    /**
     * 防止按钮重复被点击
     *
     * @param clickId 被点击view的id
     * @return
     */
    public static boolean isFastDoubleClick(int clickId) {
        if (clickId == lastClickId) {
            return isFastDoubleClick();
        }

        lastClickTime = System.currentTimeMillis();
        lastClickId = clickId;
        return false;
    }

    /**
     * 防止按钮重复被点击
     */
    private static View lastClickView = null;

    /**
     * 防止按钮重复被点击
     *
     * @param view 被点击view
     * @return
     */
    public static boolean isFastDoubleClick(View view) {
        if (view == lastClickView) {
            return isFastDoubleClick();
        }

        lastClickTime = System.currentTimeMillis();
        lastClickView = view;
        return false;
    }

    /**
     * 校验银行卡卡号
     *
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        if (TextUtils.isEmpty(cardId)) {
            return false;
        }

        cardId = cardId.replaceAll(" ", "");
        String nonCheckCodeCardId = cardId.substring(0, cardId.length() - 1);
        if (!nonCheckCodeCardId.matches("\\d+")) {
            return false;
        }

        if (nonCheckCodeCardId == null
                || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            return false;
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return ((((luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0'))
                == (cardId.charAt(cardId.length() - 1)))
                ? true
                : false);
    }

    public static void openSoftKeyBoard(Context context) {
        if (context != null) {
            // Configuration initLayoutConfig = context.getResources().getConfiguration();
            InputMethodManager inputMethodManager =
                    (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(
                    InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static boolean isKeyBoardVisiable(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        return params.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
    }

    public static void closeSoftKeyBoard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(
                        activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public static void closeSoftKeyBoard(View view) {
        if (view != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager)
                            AppConfig.getInstance()
                                    .mAppContext
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void closeSoftKeyBoard(Window window, Context context) {
        if (window != null && context != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && window.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(
                        window.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public static Pattern getUrlPattern() {
        return Pattern.compile(
                "^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$",
                Pattern.CASE_INSENSITIVE);
    }


    public static String getIfEmptyStr(String s) {
        return StringUtils.isNotNull(s) ? s : "";
    }

    public static String getZeroStr(String s) {
        return StringUtils.isNotNull(s) ? s : "0";
    }

    public static String getVideoTimeStr(String s) {
        String howLongAgoStr = "";
        try {

            int howLong = Integer.parseInt(s);

            if (howLong <= 0) {
                howLongAgoStr = "0秒";
            } else if (howLong < 60) {
                howLongAgoStr = howLong + "秒";
            } else if (howLong < 60 * 60) {
                //小于一小时
                howLongAgoStr = howLong / 60 + "分" + howLong % 60 + "秒";
            } else if (howLong < 60 * 60 * 24) {
                //小于一天
                howLongAgoStr = howLong / 60 / 60 + "时" + howLong % 3600 / 60 + "分" + howLong % 3600 % 60 + "秒";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return howLongAgoStr;
    }

    public static String getNicknameRemark(String nickname, String remark) {
        return StringUtils.isNotNull(remark) ? remark : nickname;
    }

    /**
     * 小数点两位
     *
     * @param str
     * @return
     */
    public static String castDoubleTwo(String str) {
        if (StringUtils.isNull(str)) str = "0.00";
        String encStr = "";
        try {
            encStr = new DecimalFormat("0.00").format(parseDouble(str));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return encStr;
    }


    /**
     * 更新系统相册
     *
     * @param activity
     */
    public static void notifySystemGrally(Activity activity) {
        try {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri =
                    Uri.fromFile(
                            Environment.getExternalStorageDirectory()); // out is your output file
            mediaScanIntent.setData(contentUri);
            activity.sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isImage(String path) {
        File file = new File(path);
        if (file.length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static String getAppVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        String versionCode = "";
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static int getAppVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        int versionCode = 1;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    @SuppressLint("MissingPermission")
    public static String getImei(Context context, String imei) {
        String ret = null;
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            ret = telephonyManager.getDeviceId();
        } catch (Exception e) {
            LogUtil.log(CommonUtils.class.getSimpleName(), e.getMessage());
        }
        if (StringUtils.isNotNull(ret)) {
            return ret;
        } else {
            return imei;
        }
    }


    public static <T> void dealAsync(ObservableOnSubscribe<T> onSubscribe, Observer<T> observerWrapper) {
        Observable.create(onSubscribe)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerWrapper);
    }
}
