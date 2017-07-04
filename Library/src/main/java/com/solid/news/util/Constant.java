//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.util;

import android.app.Activity;
import android.os.Bundle;

public class Constant {
    public static boolean DEBUG = true;
    public static final int NEWS_LIKE = 1;
    public static final int NEWS_DISLIKE = 0;
    public static final int GET_NET_NEWS_OVER = 2;
    public static final int GET_NOTIFICATION_NEWS_OVER = 11;
    public static final String NEWS_RECEIVER = "news_receiver";
    public static final String NEWS_URL = "http://newsaggregationapi.com/GetNewsApi_v2.php?token=%s&package=%s&page=%s&pagesize=%s&app_version=%s";
    public static final String AD_CONFIG_URL = "http://aconf.cloudzad.com/apps/news/adposition?packageName=%s";
    public static String pkgName = "";
    public static String token = "";
    public static String facebookAdId = "";
    public static String mopubAdId = "";
    public static Class<?> className;
    public static String bundleKey = "";
    public static Bundle bundle;
    public static final String PUSH_NEWS_RATE = "http://aconf.cloudzad.com/apps/news/pushrate/v2?packageName=%s";
    public static final String CALL_RECORD_PKGNAME = "com.solidunion.callrecorder";
    public static final String WIFI_PKGNAME = "com.zoomy.wifi";
    public static final String FREE_WIFI_PKGNAME = "com.free.wifi.update";
    public static Activity activity;
    public static final String pushNewsDate = "pushNewsDate";
    public static final String preLoadAdsTime = "preLoadAdsTime";
    public static final String is_pro_mode = "is_pro_mode";
    public static final String dontRemind = "dontRemind";
    public static final String isInstant = "isInstant";
    public static final String intoDetailCount = "intoDetailCount";
    public static final String preJumpDetailTime = "preJumpDetailTime";
    public static String news_list_show = "news_list_show";
    public static String news_list_load = "news_list_load";
    public static String push_notification_news_show = "push_notification_news_show";
    public static String push_notification_news_click = "push_notification_news_click";
    public static String New_List_Facebook_Request = "New_List_Facebook_Request";
    public static String New_List_Facebook_Fill = "New_List_Facebook_Fill";
    public static String New_List_Facebook_Impression = "New_List_Facebook_Impression";
    public static String New_List_Facebook_Click = "New_List_Facebook_Click";
    public static String news_list_page = "news_list_page";
    public static String news_list_pull_down = "news_list_pull_down";
    public static String news_article_click = "news_article_click";
    public static String news_detail_show = "news_detail_show";
    public static String news_detail_duration = "news_detail_duration";
    public static String news_detail_instant = "news_detail_instant";
    public static String news_detail_original = "news_detail_original";
    public static String news_instbox_show = "news_instbox_show";
    public static String news_instbox_yes = "news_instbox_yes";
    public static String news_instbox_no = "news_instbox_no";
    public static String news_detail_like = "news_detail_like";
    public static String news_detail_dislike = "news_detail_dislike";
    public static String news_viewsource_click = "news_viewsource_click";
    public static String news_detail_exit = "news_detail_exit";
    public static String news_detail_recommend = "news_detail_recommend";
    public static String news_listb_request_facebook_native_ad = "news_listb_request_facebook_native_ad";
    public static String news_listb_fill_facebook_native_ad = "news_listb_fill_facebook_native_ad";
    public static String news_listb_show_facebook_native_ad = "news_listb_show_facebook_native_ad";
    public static String news_listb_click_facebook_native_ad = "news_listb_click_facebook_native_ad";
    public static String news_quick_view = "news_quick_view";
    public static String news_ref_click_facebook_native_ad = "news_ref_click_facebook_native_ad";
    public static String news_ref_show_facebook_native_ad = "news_ref_show_facebook_native_ad";
    public static final String news_detail = "news_detail";
    public static final String news_top = "news_top";
    public static final String news_lista = "news_lista";

    public Constant() {
    }
}
