//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.util;

import android.util.Log;
import com.solid.news.util.Constant;

public class L {
    private static final String DEFAULT_TAG = "newsSdk";

    public L() {
    }

    public static void d(String tag, Object o) {
        if(Constant.DEBUG) {
            Log.d(tag, String.valueOf(o));
        }

    }

    public static void d(Object o) {
        if(Constant.DEBUG) {
            Log.d("newsSdk", String.valueOf(o));
        }

    }

    public static void e(String tag, Object o) {
        if(Constant.DEBUG) {
            Log.e(tag, String.valueOf(o));
        }

    }

    public static void e(Object o) {
        if(Constant.DEBUG) {
            e("newsSdk", o);
        }

    }

    public static void i(String tag, Object o) {
        if(Constant.DEBUG) {
            Log.i("newsSdk" + tag, String.valueOf(o));
        }

    }

    public static void i(Object o) {
        if(Constant.DEBUG) {
            Log.i("newsSdk", String.valueOf(o));
        }

    }
}
