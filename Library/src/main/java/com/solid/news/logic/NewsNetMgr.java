//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.logic;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.solid.news.bean.AdConfig;
import com.solid.news.bean.NewsBean;
import com.solid.news.bean.NewsData;
import com.solid.news.bean.PushConfig;
import com.solid.news.bean.PushNewsBean;
import com.solid.news.bean.ResponseResult;
import com.solid.news.logic.ConfigCacheMgr;
import com.solid.news.logic.LogicPushNewsMgr;
import com.solid.news.logic.LogicSettingMgr;
import com.solid.news.logic.NewsCacheMgr;
import com.solid.news.logic.NewsNotificationMgr;
import com.solid.news.logic.NewsNotificationMgr.NewsNotificationReceiver;
import com.solid.news.sdk.NewsSdk;
import com.solid.news.util.ConfigUtils;
import com.solid.news.util.Constant;
import com.solid.news.util.GlobalContext;
import com.solid.news.util.L;
import com.solid.news.util.ThreadManager;
import com.solid.news.util.Util;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Request.Builder;

public class NewsNetMgr {
    public static final int CONNECT_TIMEOUT = 60;
    public static final int READ_TIMEOUT = 100;
    public static final int WRITE_TIMEOUT = 60;
    private static Random random = new Random();
    private static NewsNetMgr single = new NewsNetMgr();
    private static Object lock = new Object();
    private static final OkHttpClient mOkHttpClient;
    private NewsNetMgr.NewsCallBack newsListener;
    private NewsNetMgr.NotificationNewsCallBack notificationNewsListener;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(NewsNetMgr.this.newsListener != null && msg.what == 2) {
                ResponseResult data = (ResponseResult)msg.obj;
                NewsNetMgr.this.isNetNewsSending = false;
                NewsNetMgr.this.newsListener.getNetNewsOver(data.isMore, data.isSucc, data.newCount, data.isLoadMore);
            }

            if(NewsNetMgr.this.notificationNewsListener != null && msg.what == 11 && msg.obj != null) {
                NewsNetMgr.this.isGetNotificationing = false;
                NewsData data1 = (NewsData)msg.obj;
                NewsNetMgr.this.notificationNewsListener.getNotificationNewsOver(data1);
            }

        }
    };
    private boolean isNetNewsSending = false;
    public int newsPageIndex = 1;
    private boolean isGetPushNewsRateing;
    private boolean isGetNotificationing;
    private boolean isGetAdConfiging;

    private NewsNetMgr() {
    }

    public static NewsNetMgr getInstance() {
        if(single == null) {
            Object var0 = lock;
            synchronized(lock) {
                if(single == null) {
                    single = new NewsNetMgr();
                }
            }
        }

        return single;
    }

    public void setNewsListener(NewsNetMgr.NewsCallBack listener) {
        this.newsListener = listener;
    }

    public void setNotificationNewsCallBack(NewsNetMgr.NotificationNewsCallBack listener) {
        this.notificationNewsListener = listener;
    }

    public void getNetNews(boolean isFirst, boolean isUseCache, final boolean isPullRefresh, final boolean isLoadMore) {
        if(!this.isNetNewsSending) {
            this.isNetNewsSending = true;
            if(isFirst) {
                this.newsPageIndex = 1;
            }

            if(isUseCache) {
                ++this.newsPageIndex;
            }

            if(isPullRefresh) {
                NewsSdk.getInstance().getReportListener().sendEvent(Constant.news_list_pull_down, (String)null, (Long)null);
            }

            Runnable getNews = new Runnable() {
                public void run() {
                    int versionCode = 0;

                    try {
                        PackageInfo request = GlobalContext.getAppContext().getPackageManager().getPackageInfo(GlobalContext.getAppContext().getPackageName(), 0);
                        versionCode = request.versionCode;
                    } catch (Exception var4) {
                        var4.printStackTrace();
                    }

                    Request request1 = (new Builder()).url(String.format("http://newsaggregationapi.com/GetNewsApi_v2.php?token=%s&package=%s&page=%s&pagesize=%s&app_version=%s", new Object[]{Constant.token, Constant.pkgName, NewsNetMgr.this.newsPageIndex + "", isPullRefresh?"10":"20", Integer.valueOf(versionCode)})).cacheControl((new okhttp3.CacheControl.Builder()).noStore().build()).build();
                    Call call = NewsNetMgr.mOkHttpClient.newCall(request1);
                    call.enqueue(new Callback() {
                        public void onFailure(Call call, IOException e) {
                            L.i("qgl", "请求新闻失败啦  " + e.getMessage());
                            NewsNetMgr.this.isNetNewsSending = false;
                            Message msg = NewsNetMgr.this.handler.obtainMessage();
                            ResponseResult result = new ResponseResult();
                            result.isMore = false;
                            result.isSucc = false;
                            msg.obj = result;
                            msg.what = 2;
                            NewsNetMgr.this.handler.sendMessage(msg);
                        }

                        public void onResponse(Call call, Response response) {
                            L.i("qgl", "请求新闻返回的code=" + response.code());
                            if(response.code() == 200) {
                                try {
                                    String e = response.body().string();
                                    if(!TextUtils.isEmpty(e)) {
                                        Gson var13 = new Gson();
                                        NewsBean var14 = (NewsBean)var13.fromJson(e, NewsBean.class);
                                        if(NewsNetMgr.this.newsPageIndex == 1) {
                                            NewsCacheMgr.getSingle().clearNewsCache();
                                        }

                                        NewsSdk.getInstance().getReportListener().sendEvent(Constant.news_list_page, "page", Long.valueOf((long)NewsNetMgr.this.newsPageIndex));
                                        ++NewsNetMgr.this.newsPageIndex;
                                        List newsdata = Arrays.asList(var14.data);
                                        ResponseResult result1 = new ResponseResult();
                                        int count = 0;
                                        if(isPullRefresh) {
                                            ArrayList msg1 = NewsCacheMgr.getSingle().getNewsCache(-1);
                                            if(msg1.size() == 0) {
                                                result1.newCount = newsdata.size();
                                            } else {
                                                Iterator var10 = newsdata.iterator();

                                                while(var10.hasNext()) {
                                                    NewsData newsData = (NewsData)var10.next();
                                                    if(!msg1.contains(newsData)) {
                                                        ++count;
                                                    }
                                                }

                                                result1.newCount = count;
                                            }

                                            NewsCacheMgr.getSingle().clearNewsCache(result1.newCount);
                                        }

                                        NewsCacheMgr.getSingle().addNewsCache(newsdata, isPullRefresh);
                                        Message var15 = NewsNetMgr.this.handler.obtainMessage();
                                        result1.isMore = true;
                                        result1.isSucc = true;
                                        result1.isLoadMore = isLoadMore;
                                        var15.obj = result1;
                                        var15.what = 2;
                                        NewsNetMgr.this.handler.sendMessage(var15);
                                    } else {
                                        NewsNetMgr.this.isNetNewsSending = false;
                                    }
                                } catch (Exception var12) {
                                    L.i("qgl", "返回的新闻出错了 " + var12.getMessage());
                                    NewsNetMgr.this.isNetNewsSending = false;
                                    Message msg = NewsNetMgr.this.handler.obtainMessage();
                                    ResponseResult result = new ResponseResult();
                                    result.isMore = false;
                                    result.isSucc = false;
                                    msg.obj = result;
                                    msg.what = 2;
                                    NewsNetMgr.this.handler.sendMessage(msg);
                                }
                            }

                        }
                    });
                }
            };
            ThreadManager.executeInBackground(getNews);
        }
    }

    public void getPushNewsRate() {
        String oldDate = LogicSettingMgr.getInstance().getPushNewsDate();
        String nowDate = Util.getDate();
        if(!this.isGetPushNewsRateing) {
            if(!oldDate.equals(nowDate)) {
                LogicSettingMgr.getInstance().setPushNewsDate();
                this.isGetPushNewsRateing = true;
                Runnable getPushNewsRate1 = new Runnable() {
                    public void run() {
                        Request request = (new Builder()).url(String.format("http://aconf.cloudzad.com/apps/news/pushrate/v2?packageName=%s", new Object[]{Constant.pkgName})).cacheControl((new okhttp3.CacheControl.Builder()).noStore().build()).build();
                        Call call = NewsNetMgr.mOkHttpClient.newCall(request);
                        call.enqueue(new Callback() {
                            public void onFailure(Call call, IOException e) {
                                NewsNetMgr.this.isGetPushNewsRateing = false;
                            }

                            public void onResponse(Call call, Response response) {
                                if(response.code() == 200) {
                                    try {
                                        String e = response.body().string();
                                        L.i("qgl", "新闻推送返回的结果：" + e);
                                        Gson gson = new Gson();
                                        PushNewsBean responseData = (PushNewsBean)gson.fromJson(e, PushNewsBean.class);
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HHmm");
                                        PushConfig config = responseData.push_config[0];
                                        LogicPushNewsMgr.interval = config.interval;
                                        Date startDate = format.parse(Util.getDate() + " " + config.push_start_time);
                                        LogicPushNewsMgr.startTime = startDate.getTime();
                                        LogicPushNewsMgr.currentTimes = 0;
                                        LogicPushNewsMgr.configIndex = 0;
                                        LogicPushNewsMgr.times = config.times;
                                        LogicPushNewsMgr.preResult = responseData;
                                        NewsNotificationMgr.setNewsNotification();
                                        NewsNetMgr.this.isGetPushNewsRateing = false;
                                    } catch (Exception var9) {
                                        NewsNetMgr.this.isGetPushNewsRateing = false;
                                    }
                                } else {
                                    NewsNetMgr.this.isGetPushNewsRateing = false;
                                }

                            }
                        });
                    }
                };
                ThreadManager.executeInBackground(getPushNewsRate1);
            } else {
                String getPushNewsRate = "";
                String currDateEnd = "";
                String nextDateStart = "";
                String nextDateEnd = "";
                if(LogicPushNewsMgr.preResult != null && LogicPushNewsMgr.configIndex < LogicPushNewsMgr.preResult.push_config.length) {
                    PushConfig format = LogicPushNewsMgr.preResult.push_config[LogicPushNewsMgr.configIndex];
                    getPushNewsRate = format.push_start_time;
                    currDateEnd = format.push_end_time;

                    try {
                        PushConfig e = LogicPushNewsMgr.preResult.push_config[LogicPushNewsMgr.configIndex + 1];
                        nextDateStart = e.push_start_time;
                        nextDateEnd = e.push_end_time;
                    } catch (Exception var17) {
                        L.i("qglnotification", " 越界 " + var17.getMessage());
                    }

                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HHmm");

                    try {
                        Date e1 = format1.parse(Util.getDate() + " " + getPushNewsRate);
                        Date currEndDate = format1.parse(Util.getDate() + " " + currDateEnd);
                        Date nextStartDate = null;
                        Object nextEndDate = null;
                        boolean isLast = false;
                        if(TextUtils.isEmpty(nextDateStart) && TextUtils.isEmpty(nextDateEnd)) {
                            isLast = true;
                        } else {
                            nextStartDate = format1.parse(Util.getDate() + " " + nextDateStart);
                            format1.parse(Util.getDate() + " " + nextDateEnd);
                        }

                        Date now = new Date(System.currentTimeMillis());
                        if(now.getTime() > e1.getTime() && now.getTime() < currEndDate.getTime()) {
                            L.i("qglnotification", "在时间段之内");
                            Context config1 = GlobalContext.getAppContext();
                            Intent startDate1 = new Intent(config1, NewsNotificationReceiver.class);
                            startDate1.setAction("news_receiver");
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(config1, 1, startDate1, 536870912);
                            if(pendingIntent == null) {
                                L.i("qglnotification", "在时间段之内   AlarmManager 不存在  重新设置");
                                NewsNotificationMgr.setNewsNotification();
                            }
                        } else if(isLast) {
                            if(now.getTime() > currEndDate.getTime()) {
                                L.i("qglnotification", " 超出了最后一个的结束时间  空闲时间 removeNotification");
                                NewsNotificationMgr.removeNotification();
                            }
                        } else if(now.getTime() > nextStartDate.getTime()) {
                            ++LogicPushNewsMgr.configIndex;
                            PushConfig[] config = LogicPushNewsMgr.preResult.push_config;
                            LogicPushNewsMgr.interval = config[LogicPushNewsMgr.configIndex].interval;
                            Date startDate = format1.parse(Util.getDate() + " " + config[LogicPushNewsMgr.configIndex].push_start_time);
                            LogicPushNewsMgr.startTime = startDate.getTime();
                            LogicPushNewsMgr.currentTimes = 0;
                            LogicPushNewsMgr.times = config[LogicPushNewsMgr.configIndex].times;
                            NewsNotificationMgr.setNewsNotification();
                        } else {
                            L.i("qglnotification", "  空闲时间 removeNotification");
                            NewsNotificationMgr.removeNotification();
                        }
                    } catch (Exception var18) {
                        L.i("qglnotification", var18.getMessage());
                    }

                } else {
                    L.i("qglnotification", "LogicPushNewsMgr.preResult == null  return");
                }
            }
        }
    }

    public void getNotificationNews() {
        if(!this.isGetNotificationing) {
            this.isGetNotificationing = true;
            Runnable getNews = new Runnable() {
                public void run() {
                    int versionCode = 0;

                    try {
                        PackageInfo request = GlobalContext.getAppContext().getPackageManager().getPackageInfo(GlobalContext.getAppContext().getPackageName(), 0);
                        versionCode = request.versionCode;
                    } catch (Exception var4) {
                        NewsNetMgr.this.isGetNotificationing = false;
                        var4.printStackTrace();
                    }

                    Request request1 = (new Builder()).url(String.format("http://newsaggregationapi.com/GetNewsApi_v2.php?token=%s&package=%s&page=%s&pagesize=%s&app_version=%s", new Object[]{Constant.token, Constant.pkgName, "1", "1", Integer.valueOf(versionCode)})).cacheControl((new okhttp3.CacheControl.Builder()).noStore().build()).build();
                    Call call = NewsNetMgr.mOkHttpClient.newCall(request1);
                    call.enqueue(new Callback() {
                        public void onFailure(Call call, IOException e) {
                            L.i("qgl", "请求新闻失败啦  " + e.getMessage());
                            NewsNetMgr.this.isGetNotificationing = false;
                            Message msg = NewsNetMgr.this.handler.obtainMessage();
                            msg.obj = null;
                            msg.what = 11;
                            NewsNetMgr.this.handler.sendMessage(msg);
                        }

                        public void onResponse(Call call, Response response) {
                            L.i("qgl", "请求新闻返回的code=" + response.code());
                            if(response.code() == 200) {
                                try {
                                    String e = response.body().string();
                                    if(!TextUtils.isEmpty(e)) {
                                        Gson msg2 = new Gson();
                                        NewsBean responseData = (NewsBean)msg2.fromJson(e, NewsBean.class);
                                        NewsData newsData = responseData.data[0];
                                        Message msg1 = NewsNetMgr.this.handler.obtainMessage();
                                        msg1.obj = newsData;
                                        msg1.what = 11;
                                        NewsNetMgr.this.handler.sendMessage(msg1);
                                    } else {
                                        NewsNetMgr.this.isGetNotificationing = false;
                                    }
                                } catch (Exception var8) {
                                    L.i("qgl", "返回的新闻出错了 " + var8.getMessage());
                                    NewsNetMgr.this.isGetNotificationing = false;
                                    Message msg = NewsNetMgr.this.handler.obtainMessage();
                                    msg.obj = null;
                                    msg.what = 11;
                                    NewsNetMgr.this.handler.sendMessage(msg);
                                }
                            }

                        }
                    });
                }
            };
            ThreadManager.executeInBackground(getNews);
        }
    }

    public void getAdConfig(final boolean isFirstEnter) {
        if(!this.isGetAdConfiging) {
            this.isGetAdConfiging = true;
            Runnable getAdConfig = new Runnable() {
                public void run() {
                    Request request = (new Builder()).url(String.format("http://aconf.cloudzad.com/apps/news/adposition?packageName=%s", new Object[]{GlobalContext.getAppContext().getPackageName()})).cacheControl((new okhttp3.CacheControl.Builder()).noStore().build()).build();
                    Call call = NewsNetMgr.mOkHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        public void onFailure(Call call, IOException e) {
                            L.i("qgl", "请求广告配置失败啦  " + e.getMessage());
                            NewsNetMgr.this.getDefaultAdConfig(isFirstEnter);
                        }

                        public void onResponse(Call call, Response response) {
                            L.i("qgl", "请求广告配置返回的code=" + response.code());
                            if(response.code() == 200) {
                                try {
                                    String e = response.body().string();
                                    if(!TextUtils.isEmpty(e)) {
                                        L.i("  广告的配置=" + e);
                                        Gson gson = new Gson();
                                        AdConfig adConfig = (AdConfig)gson.fromJson(e, AdConfig.class);
                                        if(adConfig.ret == 0) {
                                            ConfigCacheMgr.adConfig = adConfig;
                                            if(isFirstEnter) {
                                                L.i("  获取网络adConfig成功 isFirstEnter=" + isFirstEnter + "  重新拉广告");
                                                NewsNetMgr.this.handler.post(new Runnable() {
                                                    public void run() {
                                                        NewsSdk.getInstance().loadAdsCache(false);
                                                    }
                                                });
                                            }
                                        } else {
                                            L.i(" ret 不等于0  获取默认的配置");
                                            NewsNetMgr.this.getDefaultAdConfig(isFirstEnter);
                                        }
                                    } else {
                                        NewsNetMgr.this.getDefaultAdConfig(isFirstEnter);
                                    }

                                    NewsNetMgr.this.isGetAdConfiging = false;
                                } catch (Exception var6) {
                                    L.i("qgl", "返回的广告出错了 " + var6.getMessage());
                                    NewsNetMgr.this.getDefaultAdConfig(isFirstEnter);
                                }
                            } else {
                                L.i("qgl", "返回的广告code不是200");
                                NewsNetMgr.this.getDefaultAdConfig(isFirstEnter);
                            }

                        }
                    });
                }
            };
            ThreadManager.executeInBackground(getAdConfig);
        }
    }

    private void getDefaultAdConfig(boolean isFirstEnter) {
        this.isGetAdConfiging = false;
        L.i("  获取本地adConfig缓存");
        String defaultConfig = ConfigUtils.getDefaultConfig();
        Gson gson = new Gson();
        AdConfig adConfig = (AdConfig)gson.fromJson(defaultConfig, AdConfig.class);
        ConfigCacheMgr.adConfig = adConfig;
        if(isFirstEnter) {
            L.i("  获取本地adConfig缓存 isFirstEnter=" + isFirstEnter + "  重新拉广告");
            this.handler.post(new Runnable() {
                public void run() {
                    NewsSdk.getInstance().loadAdsCache(false);
                }
            });
        }

    }

    static {
        mOkHttpClient = (new OkHttpClient()).newBuilder().readTimeout(100L, TimeUnit.SECONDS).writeTimeout(60L, TimeUnit.SECONDS).connectTimeout(60L, TimeUnit.SECONDS).build();
    }

    public interface NotificationNewsCallBack {
        void getNotificationNewsOver(NewsData var1);
    }

    public interface NewsCallBack {
        void getNetNewsOver(boolean var1, boolean var2, int var3, boolean var4);
    }
}
