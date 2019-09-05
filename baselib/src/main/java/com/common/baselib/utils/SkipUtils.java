package com.common.baselib.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;

/**
 * (跳转工具类)
 *
 * @author Administrator
 */
public class SkipUtils {

    public static final String PARAMS = "params";

    public static class Params1<A> implements Serializable {
        public final A param1;

        public Params1(A param1) {
            this.param1 = param1;
        }

        public static <A> Params1<A> getInstance(A param1) {
            return new Params1<A>(param1);
        }
    }

    public static class Params2<A, B> extends Params1<A> {

        public final B param2;

        public Params2(A param1, B param2) {
            super(param1);
            this.param2 = param2;
        }

        public static <A, B> Params2<A, B> getInstance(A param1, B param2) {
            return new Params2<A, B>(param1, param2);
        }
    }

    public static class Params3<A, B, C> extends Params2<A, B> {

        public final C param3;

        public Params3(A param1, B param2, C param3) {
            super(param1, param2);
            this.param3 = param3;
        }

        public static <A, B, C> Params3<A, B, C> getInstance(A param1, B param2, C param3) {
            return new Params3<A, B, C>(param1, param2, param3);
        }
    }

    public static class Params4<A, B, C, D> extends Params3<A, B, C> {
        public final D param4;

        public Params4(A param1, B param2, C param3, D param4) {
            super(param1, param2, param3);
            this.param4 = param4;
        }

        public static <A, B, C, D> Params4<A, B, C, D> getInstance(A param1, B param2, C param3, D param4) {
            return new Params4<A, B, C, D>(param1, param2, param3, param4);
        }
    }

    public static class Params5<A, B, C, D, E> extends Params4<A, B, C, D> {
        public final E param5;

        public Params5(A param1, B param2, C param3, D param4, E param5) {
            super(param1, param2, param3, param4);
            this.param5 = param5;
        }

        public static <A, B, C, D, E> Params5<A, B, C, D, E> getInstance(A param1, B param2, C param3, D param4, E param5) {
            return new Params5<A, B, C, D, E>(param1, param2, param3, param4, param5);
        }
    }

    public static class Params6<A, B, C, D, E, F> extends Params5<A, B, C, D, E> {
        public final F param6;

        public Params6(A param1, B param2, C param3, D param4, E param5, F param6) {
            super(param1, param2, param3, param4, param5);
            this.param6 = param6;
        }

        public static <A, B, C, D, E,F> Params6<A, B, C, D, E,F> getInstance(A param1, B param2, C param3, D param4, E param5, F param6) {
            return new Params6<A, B, C, D, E, F>(param1, param2, param3, param4, param5, param6);
        }
    }


    /**
     * 获取参数
     *
     * @param intent
     * @return
     */
    public static Serializable getParams(Intent intent) {
        return intent.getSerializableExtra(PARAMS);
    }

    /**
     * 放入参数
     *
     * @param ser
     * @return
     */
    public static Intent putParams(Serializable ser) {
        return putParams(ser, false);
    }

    public static Intent putParams(Serializable ser, boolean isNewTask) {
        Intent it = new Intent();
        if (isNewTask) {
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        it.putExtra(PARAMS, ser);
        return it;
    }


    /**
     * 放入bundle 参数
     *
     * @param ser
     * @return
     */
    public static Bundle putBundleParams(Serializable ser) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAMS, ser);
        return bundle;
    }

    /**
     * 获取bundle参数
     *
     * @return
     */
    public static Serializable getBundleParams(Bundle bundle) {
        if (bundle != null) {
            return bundle.getSerializable(PARAMS);
        } else {
            return null;
        }
    }


    /**
     * 页面跳转
     *
     * @param context
     * @param clazz
     */
    public static void skip(Context context, Class clazz, Bundle extras) {
        Intent it = new Intent(context, clazz);
        if (extras != null) {
            it.putExtras(extras);
        }
        context.startActivity(it);
    }

    public static void skip(Context context, Class clazz) {
        skip(context, clazz, null);
    }

    public static void skipForResult(Activity context, Fragment fragment, Class clazz, Bundle extras, int requestCode) {
        Intent it = new Intent(context, clazz);
        if (extras != null) {
            it.putExtras(extras);
        }
        if (fragment == null) {
            context.startActivityForResult(it, requestCode);
        } else {
            fragment.startActivityForResult(it, requestCode);
        }
    }

    public static void skipForResult(Activity context, Fragment fragment, Class clazz, int requestCode) {
        skipForResult(context, fragment, clazz, null, requestCode);
    }

    public static void skipForResult(Activity context, Class clazz, Bundle extras, int requestCode) {
        skipForResult(context, null, clazz, extras, requestCode);
    }

    public static void skipForResult(Activity context, Class clazz, int requestCode) {
        skipForResult(context, clazz, null, requestCode);
    }

    /**
     * 开启activity
     *
     * @param context
     * @param calzz
     * @param ser
     */
    public static void skipActivity(Context context, Class calzz, Serializable ser) {
        skipActivity(context, null, calzz, ser);
    }

    public static void skipActivity(Context context, Fragment fragment, Class calzz, Serializable ser) {
        Intent it = new Intent(context, calzz);
        it.putExtra(PARAMS, ser);
        if (fragment != null) {
            fragment.startActivity(it);
        } else {
            context.startActivity(it);
        }
    }

    public static void skipActivityForResult(Activity context, Fragment fragment, Class calzz, Serializable ser, int requestCode) {
        Intent it = new Intent(context, calzz);
        it.putExtra(PARAMS, ser);

        if (fragment != null) {
            fragment.startActivityForResult(it, requestCode);
        } else {
            context.startActivityForResult(it, requestCode);
        }
    }

    public static void skipActivityForResult(Activity context, Class calzz, Serializable ser, int requestCode) {
        skipActivityForResult(context, null, calzz, ser, requestCode);
    }

    public static void skipActivityForResult(Fragment context, Class calzz, Serializable ser, int requestCode) {
        skipActivityForResult(context.getActivity(), context, calzz, ser, requestCode);
    }


}
