//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.util;

import android.content.Context;

public class GlobalContext {
    private static Context sAppContext;

    private GlobalContext() {
    }

    public static final void setAppContext(Context appContext) {
        if(appContext != null) {
            sAppContext = appContext.getApplicationContext();
        }

    }

    public static final Context getAppContext() {
        if(sAppContext == null) {
            throw new RuntimeException("全局context为null");
        } else {
            return sAppContext;
        }
    }
}
