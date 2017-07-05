package com.free.wifi.update;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.facebook.ads.AdSettings;
import com.solid.ad.AdSdk;
import com.solid.analytics.Analytics;
import com.solid.analytics.AnalyticsUtil;
import com.solid.common.CommonSdk;
import com.solid.news.sdk.NewsSdk;
import com.solid.news.util.L;
import com.solid.news.util.Util;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/5.
 */

public class MyApp extends MultiDexApplication implements Analytics.Interceptor {
    @Override
    public void onCreate() {
        super.onCreate();
        initAdSdk();
        initTracking();
        initNewsSdk();
    }
    private void initTracking() {
        try {
            Analytics.shared(this)
                    .setDebugMode(false)
                    .init(new Analytics.Configuration.Builder()
                            .setAnalyticsUrl(" ")
                            .setChannel("gp")
                            .setUmengAppKey(" ")
                            .setBuglyAppId(" ")
                            .setFirebaseEnable(true)
                            .setGoogleAnalyticsTrackingId("UA- ")
                            .setBuglyDebugMode(false)
                            .setAnalyticInterceptor(this)
                            .setUploadAppsInfo(false)
                            .setCategoryCanBeEmpty(true)
                            .setActionCanBeEmpty(false)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initAdSdk() {

        CommonSdk.init(this);


        AdSdk.shared(this).init(new AdSdk.Configuration.Builder()
                .setAnalyticsProvider(new AdSdk.AnalyticsProvider() {
                    @Override
                    public void onEvent(String action, Map<String, Object> params) {
                        L.d("tracking_test", action);
                        AnalyticsUtil.sendEventSimple(action, null, null, params);
                    }
                })
                .setConfigUrl("http://config.cloudzad.com/v1/config")
                .setConfigPubid("free_wifi")
                .build());

        AdSdk.shared(this).setPlacementAutoCacheAdRequest(new AdSdk.AdRequest.Builder(this, "start_page")
                .setSize(Util.getScreenWidthInDp(this) - 30, 250)
                .build()
        );

        AdSdk.shared(this).setPlacementAutoCacheAdRequest(new AdSdk.AdRequest.Builder(this, "clean")
                .setSize(Util.getScreenWidthInDp(this) >= 324 ? 324 : 320, 250)
                .build()
        );

        AdSdk.shared(this).setPlacementAutoCacheAdRequest(new AdSdk.AdRequest.Builder(this, "news_lock")
                .setSize(Util.getScreenWidthInDp(this) >= 324 ? 324 : 320, 250)
                .build()
        );

        AdSdk.shared(this).setPlacementAutoCacheAdRequest(new AdSdk.AdRequest.Builder(this, "wifi_optmize")
                .setSize(Util.getScreenWidthInDp(this), 250)
                .build()
        );

        AdSettings.addTestDevice("4a52b07aabbb60cd4aa5e63588e14920");
    }
    private void initNewsSdk() {


        NewsSdk.getInstance()
                .initContext(this)
                .log(false)
                .reportListener(new NewsSdk.ReportListener() {
                    @Override
                    public void sendEvent(String event, String label, Long value) {
                    }
                })
                .token("58cb808e-1b90-7c91-05c5-d1eb474b0e5d")
                .reportListener(new NewsSdk.ReportListener() {
                    @Override
                    public void sendEvent(String s, String s1, Long aLong) {

                    }
                })
                .className(MainActivity.class)
                .bundleKey(null)
                .bundle(null);
    }
    public static void onsendEvent(String event, String label, Long value) {
        if (value == null) {
            AnalyticsUtil.sendEventSimple(event);
            L.d("tracking_test", "send event:" + event);
        } else {
            L.d("tracking_test", "send event:" + event + "      value:" + value);
            AnalyticsUtil.sendEventSimple(event, label, value);
        }
    }

    @Override
    public boolean sendEvent(String s, String s1, Map<String, Object> map) {
        return false;
    }

    @Override
    public boolean onPageBegin(Object o, String s) {
        return false;
    }

    @Override
    public boolean onPageEnd(Object o) {
        return false;
    }

    @Override
    public boolean onInterfaceBegin(String s) {
        return false;
    }
}
