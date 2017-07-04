//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.logic;

import android.content.Context;

import com.solid.news.sdk.NewsSdk;
import com.solid.news.util.Constant;
import com.solid.news.util.GlobalContext;
import com.solid.news.util.PreferenceHelper;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogicSettingMgr {
    private static LogicSettingMgr instance = new LogicSettingMgr();

    private LogicSettingMgr() {
    }

    public static LogicSettingMgr getInstance() {
        if(instance == null) {
            instance = new LogicSettingMgr();
        }

        return instance;
    }

    public void setPushNewsDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String d = format.format(date);
        PreferenceHelper.setString(GlobalContext.getAppContext(), "pushNewsDate", d);
    }

    public String getPushNewsDate() {
        return PreferenceHelper.getString(GlobalContext.getAppContext(), "pushNewsDate", "");
    }

    public void setLoadAdsTime() {
        PreferenceHelper.setLong(GlobalContext.getAppContext(), "preLoadAdsTime", System.currentTimeMillis());
    }

    public long getLoadAdsTime() {
        return PreferenceHelper.getLong(GlobalContext.getAppContext(), "preLoadAdsTime", 0L);
    }

    public boolean getIsProMode() {
        return PreferenceHelper.getBoolean(GlobalContext.getAppContext(), "is_pro_mode", false);
    }

    public void setIsProMode(boolean proMode) {
        PreferenceHelper.setBoolean(GlobalContext.getAppContext(), "is_pro_mode", proMode);
    }

    public boolean getDontRemind() {
        return PreferenceHelper.getBoolean(GlobalContext.getAppContext(), "dontRemind", false);
    }

    public void setDontRemind(boolean isDontRemind) {
        PreferenceHelper.setBoolean(GlobalContext.getAppContext(), "dontRemind", isDontRemind);
    }

    public boolean getIsInstant() {
        return PreferenceHelper.getBoolean(GlobalContext.getAppContext(), "isInstant", false);
    }

    public void setIsInstant(boolean isUsingQuickView) {
        if(isUsingQuickView) {
            NewsSdk.getInstance().getReportListener().sendEvent(Constant.news_quick_view, "status", Long.valueOf(1L));
        } else {
            NewsSdk.getInstance().getReportListener().sendEvent(Constant.news_quick_view, "status", Long.valueOf(0L));
        }

        PreferenceHelper.setBoolean(GlobalContext.getAppContext(), "isInstant", isUsingQuickView);
    }

    public int getIntoDetailCount() {
        return PreferenceHelper.getInt(GlobalContext.getAppContext(), "intoDetailCount", 0);
    }

    public void setIntoDetailCount() {
        int count = PreferenceHelper.getInt(GlobalContext.getAppContext(), "intoDetailCount", 0);
        if(count == 10) {
            count = 0;
        }

        Context var10000 = GlobalContext.getAppContext();
        ++count;
        PreferenceHelper.setInt(var10000, "intoDetailCount", count);
    }
}
