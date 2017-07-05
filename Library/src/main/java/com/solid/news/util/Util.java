//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.solid.news.util.GlobalContext;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public Util() {
    }

    public static int dip2px(float dipValue) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5F);
    }

    public static int getScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager)GlobalContext.getAppContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager)GlobalContext.getAppContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getScreenWidthDP() {
        float scale = GlobalContext.getAppContext().getResources().getDisplayMetrics().density;
        return (int)((float)getScreenWidth() / scale + 0.5F);
    }

    public static int getStatusBarHeight() {
        if(VERSION.SDK_INT >= 19) {
            int statusBarHeight = 0;
            int resourceId = GlobalContext.getAppContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
            if(resourceId > 0) {
                statusBarHeight = GlobalContext.getAppContext().getResources().getDimensionPixelSize(resourceId);
            }

            return statusBarHeight;
        } else {
            return 0;
        }
    }

    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String d = format.format(date);
        return d;
    }

    public static int differentSecondByMillisecond(Date date1) {
        int second = (int)(System.currentTimeMillis() - date1.getTime()) / 1000;
        return second;
    }
    public static int getScreenWidthInDp(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dpScreenWidth = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return dpScreenWidth;
    }
}
