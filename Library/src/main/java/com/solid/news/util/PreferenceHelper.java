//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.util;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Set;

public class PreferenceHelper {
    public PreferenceHelper() {
    }

    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences("newsdk", 0);
    }

    public static String getString(Context context, String key, String defValue) {
        return getSharedPreference(context).getString(key, defValue);
    }

    public static int getInt(Context context, String key, int defValue) {
        return getSharedPreference(context).getInt(key, defValue);
    }

    public static long getLong(Context context, String key, long defValue) {
        return getSharedPreference(context).getLong(key, defValue);
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return getSharedPreference(context).getBoolean(key, defValue);
    }

    public static float getFloat(Context context, String key, float defValue) {
        return getSharedPreference(context).getFloat(key, defValue);
    }

    public static void setString(Context context, String key, String value) {
        getSharedPreference(context).edit().putString(key, value).apply();
    }

    public static void setInt(Context context, String key, int value) {
        getSharedPreference(context).edit().putInt(key, value).apply();
    }

    public static void setLong(Context context, String key, long value) {
        getSharedPreference(context).edit().putLong(key, value).apply();
    }

    public static void setFloat(Context context, String key, float value) {
        getSharedPreference(context).edit().putFloat(key, value).apply();
    }

    public static void setBoolean(Context context, String key, boolean value) {
        getSharedPreference(context).edit().putBoolean(key, value).apply();
    }

    public static void setStringSet(Context context, String key, Set<String> value) {
        getSharedPreference(context).edit().putStringSet(key, value).apply();
    }

    public static Set<String> getStringSet(Context context, String key, Set<String> value) {
        return getSharedPreference(context).getStringSet(key, value);
    }
}
