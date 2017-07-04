//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.logic;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.widget.RemoteViews;
import com.solid.news.R.id;
import com.solid.news.R.layout;
import com.solid.news.bean.NewsData;
import com.solid.news.logic.LogicPushNewsMgr;
import com.solid.news.logic.NewsNetMgr;
import com.solid.news.logic.NewsNetMgr.NotificationNewsCallBack;
import com.solid.news.sdk.NewsSdk;
import com.solid.news.util.Constant;
import com.solid.news.util.GlobalContext;
import com.solid.news.util.L;
import com.solid.news.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Picasso.LoadedFrom;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsNotificationMgr {
    private static final String TAG = "qglnotification";
    private static long oldTime = 0L;

    public NewsNotificationMgr() {
    }

    public static void removeNotification() {
        L.d("qglnotification", "removeNotification");
        Context context = GlobalContext.getAppContext();
        Intent intent = new Intent(context, NewsNotificationMgr.NewsNotificationReceiver.class);
        intent.setAction("news_receiver");
        isExist(context, intent);
    }

    public static void setNewsNotification() {
        L.d("qglnotification", "setNewsNotification");
        Context context = GlobalContext.getAppContext();
        Intent intent = new Intent(context, NewsNotificationMgr.NewsNotificationReceiver.class);
        intent.setAction("news_receiver");
        isExist(context, intent);
        long intervalMillis = (long)('\uea60' * LogicPushNewsMgr.interval);
        long t = 0L;
        t = LogicPushNewsMgr.startTime;
        PendingIntent operation = PendingIntent.getBroadcast(context, 1, intent, 0);
        AlarmManager am = (AlarmManager)context.getSystemService("alarm");
        am.cancel(operation);
        if(Constant.DEBUG) {
            am.setRepeating(0, t, intervalMillis, operation);
        } else {
            am.setRepeating(3, t, intervalMillis, operation);
        }

    }

    private static final void isExist(Context context, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 536870912);
        if(pendingIntent != null) {
            L.i("qglnotification", "alarm已存在  取消");
            pendingIntent.cancel();
        }

    }

    public static class NewsNotificationToNewsReceiver extends BroadcastReceiver {
        public NewsNotificationToNewsReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            L.i("qglclicktracking", "通知栏news点击");
            NewsSdk.getInstance().getReportListener().sendEvent(Constant.push_notification_news_click, (String)null, (Long)null);
            Intent newsIntent = new Intent(GlobalContext.getAppContext(), Constant.className);
            newsIntent.addFlags(268435456);
            if(!TextUtils.isEmpty(Constant.bundleKey)) {
                newsIntent.putExtra(Constant.bundleKey, Constant.bundle);
            }

            GlobalContext.getAppContext().startActivity(newsIntent);
        }
    }

    public static class NewsNotificationReceiver extends BroadcastReceiver {
        public NewsNotificationReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            boolean isContain = false;

            try {
                String currDataEndHour = LogicPushNewsMgr.preResult.push_config[LogicPushNewsMgr.configIndex].push_end_time;
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HHmm");
                Date currEndDate = format.parse(Util.getDate() + " " + currDataEndHour);
                Date now = new Date(System.currentTimeMillis());
                if(now.getTime() < currEndDate.getTime()) {
                    isContain = true;
                } else {
                    isContain = false;
                }
            } catch (Exception var8) {
                ;
            }

            if(LogicPushNewsMgr.currentTimes >= LogicPushNewsMgr.times) {
                L.i("qglnotification", "次数超过了");
            } else if(Math.abs(System.currentTimeMillis() - NewsNotificationMgr.oldTime) < 1200000L) {
                L.i("qglnotification", "小于二十分钟间隔");
            } else if(!isContain) {
                L.i("qglnotification", "不在时间段内");
            } else {
                NewsNotificationMgr.oldTime = System.currentTimeMillis();
                NewsNetMgr.getInstance().setNotificationNewsCallBack(new NotificationNewsCallBack() {
                    public void getNotificationNewsOver(final NewsData newsData) {
                        if(newsData != null) {
                            Picasso.with(GlobalContext.getAppContext()).load(newsData.news_img).resize(Util.dip2px(87.0F), Util.dip2px(59.0F)).into(new Target() {
                                public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
                                    NewsNotificationReceiver.this.setNotification(bitmap, newsData.news_title, newsData.source);
                                }

                                public void onBitmapFailed(Drawable errorDrawable) {
                                }

                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                }
                            });
                        }

                    }
                });
                NewsNetMgr.getInstance().getNotificationNews();
            }
        }

        private void setNotification(Bitmap img, String title, String source) {
            L.i("qglnotification", "title=" + title + " source=" + source);
            L.i("qglclicktracking", "通知栏news展示");
            NewsSdk.getInstance().getReportListener().sendEvent(Constant.push_notification_news_show, (String)null, (Long)null);
            ++LogicPushNewsMgr.currentTimes;
            Context context = GlobalContext.getAppContext();
            NotificationManager notificationManager = (NotificationManager)context.getSystemService("notification");
            Builder mBuilder = new Builder(context);
            mBuilder.setSmallIcon(17170445);
            Notification notification = mBuilder.build();
            RemoteViews contentView = new RemoteViews(context.getPackageName(), layout.news_notification_news);
            contentView.setImageViewBitmap(id.ivImg, img);
            contentView.setTextViewText(id.tvTitle, title);
            contentView.setTextViewText(id.tvSource, source);
            notification.contentView = contentView;
            notification.defaults = 7;
            notification.flags = 16;
            notification.when = System.currentTimeMillis();
            Intent intent = new Intent(context, NewsNotificationMgr.NewsNotificationToNewsReceiver.class);
            PendingIntent pendingBoostIntent = PendingIntent.getBroadcast(GlobalContext.getAppContext(), 5, intent, 134217728);
            notification.contentIntent = pendingBoostIntent;
            notificationManager.notify(0, notification);
        }
    }
}
