//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.sdk;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.facebook.ads.NativeAdsManager.Listener;
import com.solid.ad.Ad;
import com.solid.ad.AdListener;
import com.solid.ad.AdListenerBase;
import com.solid.ad.AdSdk;
import com.solid.ad.AdSdk.AdRequest;
import com.solid.ad.AdSdk.AdRequest.Builder;
import com.solid.ad.protocol.Placement;
import com.solid.ad.protocol.Unit;
import com.solid.ad.view.AdNativeViewIdBuilder;
import com.solid.news.R.drawable;
import com.solid.news.R.id;
import com.solid.news.R.layout;
import com.solid.news.R.string;
import com.solid.news.activity.NewsDetailActivity;
import com.solid.news.bean.Content;
import com.solid.news.bean.NewsData;
import com.solid.news.db.NewsDBUtils;
import com.solid.news.logic.ConfigCacheMgr;
import com.solid.news.logic.LogicSettingMgr;
import com.solid.news.logic.NewsCacheMgr;
import com.solid.news.logic.NewsNetMgr;
import com.solid.news.logic.NewsNetMgr.NewsCallBack;
import com.solid.news.receiver.DataChangeReceiver;
import com.solid.news.util.BitmapUtils;
import com.solid.news.util.Constant;
import com.solid.news.util.GlobalContext;
import com.solid.news.util.L;
import com.solid.news.util.ThreadManager;
import com.solid.news.util.Util;
import com.solid.news.view.SuperSwipeRefreshLayout;
import com.solid.news.view.SuperSwipeRefreshLayout.OnPullRefreshListener;
import com.solid.news.view.SuperSwipeRefreshLayout.OnPushLoadMoreListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

public class NewsSdk {
    private static NewsSdk instance;
    private View rootView;
    private RelativeLayout rlRoot;
    private TextView tvUpdateNews;
    private SuperSwipeRefreshLayout refresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private NewsSdk.NewsAdapter adapter;
    private LinearLayout llTipRoot;
    private ArrayList<NewsData> newsData = new ArrayList();
    private ArrayList<NewsData> newsAndADData = new ArrayList();
    public ArrayList<Integer> adPosition = new ArrayList();
    private ArrayList<String> newsOkList = new ArrayList();
    private Handler handler = new Handler();
    private Random random = new Random();
    private DataChangeReceiver dataChangeReceiver;
    private static final int ITEM_NEWS_ONE = 0;
    private static final int ITEM_NEWS_THREE = 1;
    private static final int ITEM_SMALL_AD = 2;
    private static final int ITEM_BIG_AD = 3;
    private static ArrayList<HashMap<String, NativeAd>> ads;
    private ImageView ivRefresh;
    private boolean isFromCache = false;
    private boolean isFirstAdRegister = false;
    private NewsSdk.ReportListener reportListener;
    private NewsSdk.LoadNewsListener loadNewsListener;
    private NewsSdk.JumpDetailListener jumpDetailListener;
    private NewsSdk.ScrollListener scrollListener;
    private ValueAnimator refreshAnimator;
    private boolean isUseCache;
    private boolean isLoadADing;
    private NativeAd nativeAd;
    private boolean loadSucc;
    private boolean isFacebookManagerLoading;
    private boolean isRefreshing = false;

    private NewsSdk() {
    }

    public static NewsSdk getInstance() {
        if(instance == null) {
            instance = new NewsSdk();
        }

        return instance;
    }

    public NewsSdk initContext(Context context) {
        if(context != null) {
            GlobalContext.setAppContext(context);
            Constant.pkgName = context.getPackageName();
            return instance;
        } else {
            throw new RuntimeException("传入的context不能为空");
        }
    }

    public NewsSdk log(boolean isShowLog) {
        Constant.DEBUG = isShowLog;
        return instance;
    }

    public NewsSdk token(String token) {
        if(TextUtils.isEmpty(token)) {
            throw new RuntimeException("token不能为空");
        } else {
            Constant.token = token;
            return instance;
        }
    }

    public NewsSdk className(Class<?> className) {
        if(className == null) {
            throw new RuntimeException("className不能为空");
        } else {
            Constant.className = className;
            return instance;
        }
    }

    public NewsSdk bundleKey(String bundleKey) {
        Constant.bundleKey = bundleKey;
        return instance;
    }

    public NewsSdk reportListener(NewsSdk.ReportListener listener) {
        this.reportListener = listener;
        return instance;
    }

    public NewsSdk.ReportListener getReportListener() {
        if(this.reportListener == null) {
            throw new RuntimeException(" reportListener 为空  请先初始化");
        } else {
            return this.reportListener;
        }
    }

    public NewsSdk bundle(Bundle bundle) {
        Constant.bundle = bundle;
        return instance;
    }

    public void notifyData() {
        if(this.llTipRoot != null) {
            this.llTipRoot.setVisibility(8);
            ArrayList newsData = NewsCacheMgr.getSingle().getNewsCache(-1);
            if(newsData != null) {
                this.addAdData(newsData);
            }
        }

    }

    public void setIsUsingQuickView(boolean isUsingQuickView) {
        LogicSettingMgr.getInstance().setIsInstant(isUsingQuickView);
    }

    public boolean getIsUsingQuickView() {
        return LogicSettingMgr.getInstance().getIsInstant();
    }

    public View getNewsView(NewsSdk.LoadNewsListener loadNewsListener, NewsSdk.JumpDetailListener jumpDetailListener, NewsSdk.ScrollListener scrollListener) {
        this.getReportListener().sendEvent(Constant.news_list_show, (String)null, (Long)null);
        L.i("qglclicktracking", "新闻列表展示次数");
        this.rootView = View.inflate(GlobalContext.getAppContext(), layout.news_news_view, (ViewGroup)null);
        this.initCallBackListener(loadNewsListener, jumpDetailListener, scrollListener);
        NewsCacheMgr.firstAd = null;
        this.getFirstBigAD();
        this.initView();
        this.initListener();
        this.getNewsData();
        this.registReciver();
        return this.rootView;
    }

    private void initCallBackListener(NewsSdk.LoadNewsListener loadNewsListener, NewsSdk.JumpDetailListener jumpDetailListener, NewsSdk.ScrollListener scrollListener) {
        this.loadNewsListener = loadNewsListener;
        this.jumpDetailListener = jumpDetailListener;
        this.scrollListener = scrollListener;
    }

    public void removeAdsCache() {
        L.i(" 清空了 广告缓存");
        Context context = GlobalContext.getAppContext();
        AdSdk.shared(context).preloadAd(context, this.createPreLoadAdRequest("news_lista"), (AdListener)null);
        NewsCacheMgr.getSingle().removeRegistAdsCache();
        NewsCacheMgr.firstAd = null;
    }

    public void setProMode(boolean isPro) {
        LogicSettingMgr.getInstance().setIsProMode(isPro);
    }

    public boolean getProMode() {
        return LogicSettingMgr.getInstance().getIsProMode();
    }

    private void registReciver() {
        try {
            GlobalContext.getAppContext().unregisterReceiver(this.dataChangeReceiver);
        } catch (Exception var2) {
            ;
        }

        if(this.dataChangeReceiver == null) {
            this.dataChangeReceiver = new DataChangeReceiver();
        }

        IntentFilter datetFilter = new IntentFilter();
        datetFilter.addAction("android.intent.action.TIME_TICK");
        datetFilter.addAction("android.intent.action.TIME_SET");
        datetFilter.setPriority(2147483647);
        GlobalContext.getAppContext().registerReceiver(this.dataChangeReceiver, datetFilter);
    }

    private void initView() {
        this.rlRoot = (RelativeLayout)this.rootView.findViewById(id.rlRoot);
        this.tvUpdateNews = (TextView)this.rootView.findViewById(id.tvUpdateNews);
        this.refresh = (SuperSwipeRefreshLayout)this.rootView.findViewById(id.refresh);
        this.recyclerView = (RecyclerView)this.rootView.findViewById(id.recyclerView);
        this.linearLayoutManager = new LinearLayoutManager(GlobalContext.getAppContext(), 1, false);
        this.adapter = new NewsSdk.NewsAdapter(null);
        this.recyclerView.setLayoutManager(this.linearLayoutManager);
        this.recyclerView.setAdapter(this.adapter);
        this.llTipRoot = (LinearLayout)this.rootView.findViewById(id.llTipRoot);
    }

    private View createHeaderView() {
        View view = View.inflate(GlobalContext.getAppContext(), layout.news_head, (ViewGroup)null);
        this.ivRefresh = (ImageView)view.findViewById(id.ivRefresh);
        return view;
    }

    private void startRefresh() {
        if(this.ivRefresh != null) {
            this.refreshAnimator = ValueAnimator.ofInt(new int[]{0, 360}).setDuration(500L);
            this.refreshAnimator.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = ((Integer)animation.getAnimatedValue()).intValue();
                    NewsSdk.this.ivRefresh.setRotation((float)value);
                }
            });
            this.refreshAnimator.setInterpolator(new LinearInterpolator());
            this.refreshAnimator.setRepeatMode(1);
            this.refreshAnimator.setRepeatCount(-1);
            this.refreshAnimator.start();
        }

    }

    private void endRefresh() {
        if(this.ivRefresh != null && this.refreshAnimator != null) {
            this.refreshAnimator.end();
            this.refreshAnimator = null;
        }

    }

    private View createFooterView() {
        View view = View.inflate(GlobalContext.getAppContext(), layout.news_foot, (ViewGroup)null);
        return view;
    }

    private void initListener() {
        this.refresh.setHeaderView(this.createHeaderView());
        this.refresh.setOnPullRefreshListener(new OnPullRefreshListener() {
            public void onRefresh() {
                NewsSdk.this.startRefresh();
                NewsSdk.this.getFirstBigAD();
                NewsNetMgr.getInstance().getNetNews(false, NewsSdk.this.isUseCache, true, false);
                L.i(" refresh  onRefresh  ");
            }

            public void onPullDistance(int distance) {
            }

            public void onPullEnable(boolean enable) {
            }
        });
        this.refresh.setFooterView(this.createFooterView());
        this.refresh.setOnPushLoadMoreListener(new OnPushLoadMoreListener() {
            public void onLoadMore() {
                NewsNetMgr.getInstance().getNetNews(false, NewsSdk.this.isUseCache, false, true);
            }

            public void onPushEnable(boolean enable) {
            }

            public void onPushDistance(int distance) {
            }
        });
        this.recyclerView.addOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isBottom = NewsSdk.this.isBottom();
                if(NewsSdk.this.scrollListener != null) {
                    NewsSdk.this.scrollListener.scrollListener(dx, dy, isBottom);
                }

            }
        });
        this.recyclerView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return NewsSdk.this.isRefreshing;
            }
        });
    }

    private void getNewsData() {
        this.newsOkList.clear();
        NewsNetMgr.getInstance().setNewsListener(new NewsCallBack() {
            public void getNetNewsOver(boolean isMore, boolean isSucc, int pullRefreshNewsCount, boolean isLoadMore) {
                NewsSdk.this.refresh.setRefreshing(false);
                NewsSdk.this.endRefresh();
                NewsSdk.this.refresh.setLoadMore(false);
                if(isSucc) {
                    NewsSdk.this.llTipRoot.setVisibility(8);
                    ArrayList newsData = NewsCacheMgr.getSingle().getNewsCache(-1);
                    if(newsData != null) {
                        NewsSdk.this.addAdData(newsData);
                    }

                    NewsSdk.this.getReportListener().sendEvent(Constant.news_list_load, "status", Long.valueOf(1L));
                    L.i("qglclicktracking", "新闻列表load 成功");
                    if(NewsSdk.this.loadNewsListener != null) {
                        NewsSdk.this.loadNewsListener.loadNewsSucc();
                    }

                    if(pullRefreshNewsCount > 0) {
                        NewsSdk.this.tvUpdateNews.setText(pullRefreshNewsCount + " " + GlobalContext.getAppContext().getResources().getString(string.news_update));
                        NewsSdk.this.tvUpdateNews.setVisibility(0);
                        NewsSdk.this.handler.postDelayed(new Runnable() {
                            public void run() {
                                NewsSdk.this.tvUpdateNews.setVisibility(4);
                            }
                        }, 1000L);
                    }

                    if(isLoadMore) {
                        NewsSdk.this.recyclerView.scrollBy(0, Util.dip2px(50.0F));
                    }
                } else {
                    NewsSdk.this.getReportListener().sendEvent(Constant.news_list_load, "status", Long.valueOf(0L));
                    L.i("qglclicktracking", "新闻列表load 失败");
                    if(NewsSdk.this.loadNewsListener != null) {
                        NewsSdk.this.loadNewsListener.loadNewsError();
                    }
                }

            }
        });
        NewsCacheMgr.adRegistedList.clear();
        ArrayList cacheNews = NewsCacheMgr.getSingle().getNewsCache(20);
        if(cacheNews.size() > 0) {
            if(this.loadNewsListener != null) {
                this.loadNewsListener.loadNewsSucc();
            }

            this.getReportListener().sendEvent(Constant.news_list_load, "status", Long.valueOf(1L));
            L.i("qglclicktracking", "新闻列表load 成功");
            this.isUseCache = true;
            this.llTipRoot.setVisibility(8);
            this.addAdData(cacheNews);
        } else {
            NewsNetMgr.getInstance().getNetNews(true, false, false, false);
        }

        if(this.newsData != null) {
            this.newsData.clear();
        }

        if(this.newsAndADData != null) {
            this.newsAndADData.clear();
        }

        this.addAdData(cacheNews);
    }

    private void getFirstBigAD() {
        if(!LogicSettingMgr.getInstance().getIsProMode()) {
            if(!this.isLoadADing) {
                this.isLoadADing = true;
                Context context = GlobalContext.getAppContext();
                final boolean cached = AdSdk.shared(context).adCached("news_lista");
                AdSdk.shared(context).loadAd(context, this.createDelayAdRequest(), new AdListenerBase() {
                    public void onLoaded(Ad ad) {
                        L.i("getfirst ad  onLoaded");
                        NewsCacheMgr.firstAd = ad;
                        NewsSdk.this.isLoadADing = false;
                        NewsSdk.getInstance().getReportListener().sendEvent("news_lista_show_facebook_native_ad", "status", Long.valueOf(cached?1L:2L));
                    }

                    public void onClicked(Ad ad) {
                    }

                    public void onFailed(Ad ad, int code, String msg, Object err) {
                        super.onFailed(ad, code, msg, err);
                        L.i("getfirst ad    load onFailed  code=" + code + " msg=" + msg);
                        NewsSdk.this.isLoadADing = false;
                    }
                });
            }
        }
    }

    private void loadFaceBookAd() {
        this.loadSucc = false;
        this.nativeAd = new NativeAd(GlobalContext.getAppContext(), "676566999170980_765317376962608");
        this.nativeAd.setAdListener(new AbstractAdListener() {
            public void onAdLoaded(com.facebook.ads.Ad ad) {
                super.onAdLoaded(ad);
                L.i("qgl facebookad", "获取成功了");
                NewsSdk.this.loadSucc = true;
                NewsSdk.this.notifyData();
            }

            public void onError(com.facebook.ads.Ad ad, AdError adError) {
                super.onError(ad, adError);
                L.i("qgl facebookad", "获取失败了  " + adError.getErrorMessage());
                NewsSdk.this.loadSucc = false;
            }

            public void onAdClicked(com.facebook.ads.Ad ad) {
                super.onAdClicked(ad);
                NewsSdk.this.loadSucc = false;
            }
        });
        this.nativeAd.loadAd();
        L.i("qgl facebookad", " 开始拉取facebook广告了");
    }

    public void loadAdsCache(boolean isFirstEnter) {
        if(ConfigCacheMgr.adConfig == null) {
            NewsNetMgr.getInstance().getAdConfig(isFirstEnter);
        } else {
            String facebookId = this.getFacebookManagerId();
            if(TextUtils.isEmpty(facebookId)) {
                L.i(" manager id 为空  return");
            } else {
                int adCacheSize = NewsCacheMgr.getSingle().getAdsCacheSizeByFacebookId(facebookId);
                if(!isFirstEnter || adCacheSize < ConfigCacheMgr.adConfig.news_per_page_ad_num) {
                    long preTime = LogicSettingMgr.getInstance().getLoadAdsTime();
                    long second = (System.currentTimeMillis() - preTime) / 1000L;
                    if(!this.isFacebookManagerLoading && second >= 15L) {
                        this.isFacebookManagerLoading = true;
                        this.loadFaceBookManagerAd(facebookId);
                        this.getReportListener().sendEvent(Constant.New_List_Facebook_Request, (String)null, (Long)null);
                    } else {
                        L.i(" 正在load广告 或者 距离上次load的时间小于15秒 return");
                    }
                }
            }
        }
    }

    public void loadFaceBookManagerAd(final String facebookId) {
        if(!LogicSettingMgr.getInstance().getIsProMode()) {
            final NativeAdsManager manager = new NativeAdsManager(GlobalContext.getAppContext(), facebookId, ConfigCacheMgr.adConfig.news_list_request_ad_num);
            manager.setListener(new Listener() {
                public void onAdsLoaded() {
                    NewsSdk.this.getReportListener().sendEvent(Constant.news_listb_request_facebook_native_ad, "status", Long.valueOf(1L));
                    NewsSdk.this.getReportListener().sendEvent(Constant.news_listb_fill_facebook_native_ad, (String)null, (Long)null);
                    NewsSdk.this.isFacebookManagerLoading = false;
                    LogicSettingMgr.getInstance().setLoadAdsTime();
                    L.i("qglads onAdsLoaded ok ");
                    NewsSdk.this.getReportListener().sendEvent(Constant.New_List_Facebook_Fill, (String)null, (Long)null);
                    int count = manager.getUniqueNativeAdCount();

                    for(int cloneData = 0; cloneData < count; ++cloneData) {
                        NativeAd ad = manager.nextNativeAd();
                        NewsCacheMgr.getSingle().addAdsCache(facebookId, ad);
                    }

                    ArrayList var4 = (ArrayList)NewsSdk.this.newsData.clone();
                    NewsSdk.this.addAdData(var4);
                }

                public void onAdError(AdError adError) {
                    NewsSdk.this.isFacebookManagerLoading = false;
                    NewsSdk.this.getReportListener().sendEvent(Constant.news_listb_request_facebook_native_ad, "status", Long.valueOf(0L));
                    NewsSdk.this.getReportListener().sendEvent("Facebook_Load_Fail", (String)null, (Long)null);
                    if(adError.getErrorCode() == 1000) {
                        NewsSdk.this.getReportListener().sendEvent("Facebook_Load_Fail_Network_Error", (String)null, (Long)null);
                    } else if(adError.getErrorCode() == 1001) {
                        NewsSdk.this.getReportListener().sendEvent("Facebook_Load_Fail_No_Fill", (String)null, (Long)null);
                    } else if(adError.getErrorCode() == 1002) {
                        NewsSdk.this.getReportListener().sendEvent("Facebook_Load_Fail_Load_Too_Frequently", (String)null, (Long)null);
                    }

                    L.i("qglads error=" + adError.getErrorMessage());
                }
            });
            L.i("qglads  开始load广告了");
            manager.loadAds();
        }
    }

    private void loadMopubAd(int startPosition) {
    }

    private void addAdData(ArrayList<NewsData> data) {
        if(data != null && data.size() != 0) {
            this.isRefreshing = true;

            try {
                this.newsData.clear();
                this.newsData.addAll(data);
                int e = this.newsData.size() / 4;
                if(ConfigCacheMgr.adConfig != null && ConfigCacheMgr.adConfig.news_list_ad_interval != 0) {
                    e = this.newsData.size() / ConfigCacheMgr.adConfig.news_list_ad_interval;
                }

                ads = NewsCacheMgr.getSingle().getAdsCacheByCount(e);
                this.isFromCache = NewsCacheMgr.getSingle().cacheEnough();
                ArrayList adsBean = new ArrayList();
                boolean isHaveFirstAd = false;
                if(NewsCacheMgr.firstAd != null) {
                    NewsData intervel = new NewsData();
                    intervel.isAD = true;
                    intervel.isFirstAD = true;
                    adsBean.add(intervel);
                    isHaveFirstAd = true;
                }

                NewsData adapterData;
                int var11;
                for(var11 = 0; var11 < ads.size(); ++var11) {
                    adapterData = new NewsData();
                    HashMap newsAdData = (HashMap)ads.get(var11);
                    String index = newsAdData.keySet().toArray()[0].toString();
                    NativeAd ad = (NativeAd)newsAdData.get(index);
                    adapterData.title = ad.getAdTitle();
                    adapterData.subTitle = ad.getAdSubtitle();
                    adapterData.iconUrl = ad.getAdIcon().getUrl();
                    adapterData.bigImageUrl = ad.getAdCoverImage().getUrl();
                    adapterData.choicesIconcUrl = ad.getAdChoicesIcon().getUrl();
                    adapterData.des = ad.getAdCallToAction();
                    adapterData.isAD = true;
                    adapterData.facebookId = index;
                    adsBean.add(adapterData);
                }

                this.newsAndADData.clear();
                Iterator var12 = this.newsData.iterator();

                while(var12.hasNext()) {
                    adapterData = (NewsData)var12.next();
                    this.newsAndADData.add(adapterData);
                }

                this.adPosition.clear();
                var11 = 4;
                if(ConfigCacheMgr.adConfig != null) {
                    var11 = ConfigCacheMgr.adConfig.news_list_ad_interval;
                }

                if(isHaveFirstAd) {
                    this.adPosition.add(Integer.valueOf(var11 + 1));
                    L.i(" 有第一个广告");
                } else {
                    this.adPosition.add(Integer.valueOf(var11));
                    L.i(" 没有第一个广告");
                }

                int var13;
                for(var13 = 0; var13 < 1000; ++var13) {
                    this.adPosition.add(Integer.valueOf(((Integer)this.adPosition.get(this.adPosition.size() - 1)).intValue() + var11 + 1));
                }

                if(adsBean.size() > 0) {
                    for(var13 = 0; var13 < adsBean.size(); ++var13) {
                        NewsData var14 = (NewsData)adsBean.get(var13);
                        int var16 = var13;
                        if(isHaveFirstAd) {
                            var16 = var13 - 1;
                        }

                        if(var14.isFirstAD) {
                            this.newsAndADData.add(0, adsBean.get(var13));
                        } else if(var16 < this.adPosition.size() && ((Integer)this.adPosition.get(var16)).intValue() < this.newsAndADData.size()) {
                            this.newsAndADData.add(((Integer)this.adPosition.get(var16)).intValue(), var14);
                        }
                    }
                }

                ArrayList var15 = (ArrayList)this.newsAndADData.clone();
                this.adapter.setData(var15);
                L.i("qgl facebookad", "addAdData   set adapter 数据源的size=" + var15.size());
                this.adapter.notifyDataSetChanged();
            } catch (Exception var10) {
                L.i("qgl addadView出错了 " + var10.getMessage());
            }

            this.isRefreshing = false;
        }
    }

    private boolean isBottom() {
        int lastVisiablePosition = this.linearLayoutManager.findLastCompletelyVisibleItemPosition();
        int lastPosition = this.recyclerView.getAdapter().getItemCount() - 2;
        return lastVisiablePosition == lastPosition;
    }

    private String getFacebookManagerId() {
        Placement thread_detail2 = AdSdk.shared(GlobalContext.getAppContext()).findPlacement("news_lista");
        if(thread_detail2 != null) {
            Vector units = thread_detail2.getUnits();
            if(units != null) {
                for(int i = 0; i < units.size(); ++i) {
                    Vector vec = (Vector)units.get(i);

                    for(int j = 0; j < vec.size(); ++j) {
                        Unit unit = (Unit)vec.get(j);
                        if(unit.getPlatform().equals("facebook") && unit.getType().equals("native")) {
                            String ad_id = unit.getAd_id();
                            return ad_id;
                        }
                    }
                }
            }
        }

        return "";
    }

    private AdRequest createPreLoadAdRequest(String placementId) {
        AdRequest adRequest = (new Builder(GlobalContext.getAppContext(), placementId)).setDestroyOnDetach(false).setSize(Util.getScreenWidthDP() - 13, 250).build();
        return adRequest;
    }

    AdRequest createDelayAdRequest() {
        Context context = GlobalContext.getAppContext();
        AdRequest adRequest = (new Builder(context, "news_lista")).setShowInterstitialOnLoaded(false).setShowRewardedVideoOnLoaded(false).setAddBannerToParentOnLoaded(false).setAddNativeToParentOnLoaded(false).setDestroyOnDetach(false).setSize(Util.getScreenWidthDP() - 13, 250).build();
        return adRequest;
    }

    private AdRequest setAdaRequest(ViewGroup group) {
        Context context = GlobalContext.getAppContext();
        Builder home_board = new Builder(context, "news_lista");
        home_board.setClinkCustomOnClick(true).setSize(Util.getScreenWidthDP() - 13, 250).setContainer(group).setDestroyOnDetach(false).setAddBannerToParentOnLoaded(true).setClearParentOnAdd(false).setAddNativeToParentOnLoaded(true).setMakeParentVisibleOnAdd(true).setAdNativeViewBuilder((new AdNativeViewIdBuilder(context)).setLayoutId(layout.news_ad_item).setIconViewId(id.ad_icon_view).setTitleViewId(id.ad_title_text).setBodyViewId(id.ad_body_text).setCallToActionViewId(id.ad_call_to_action_text).setImageViewId(id.ad_image_view).setFacebookMediaViewId(id.ad_media_view_facebook).setPrivacyViewId(id.ad_privacy_view)).build();
        return home_board.build();
    }

    private class NewsAdapter extends Adapter<ViewHolder> {
        private ArrayList<NewsData> data;

        private NewsAdapter() {
        }

        public void setData(ArrayList<NewsData> data) {
            this.data = data;
        }

        public int getItemViewType(int position) {
            if(this.data == null) {
                return -1;
            } else {
                NewsData newsData = (NewsData)this.data.get(position);
                if(newsData.isAD) {
                    if(newsData.isFirstAD) {
                        return 3;
                    } else {
                        for(int i = 0; i < NewsSdk.this.adPosition.size(); ++i) {
                            if(position == ((Integer)NewsSdk.this.adPosition.get(i)).intValue()) {
                                if(i % 2 == 0) {
                                    return 2;
                                }

                                return 3;
                            }
                        }

                        return 3;
                    }
                } else {
                    return newsData.images_count <= 5?0:1;
                }
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if(viewType == 3) {
                view = LayoutInflater.from(GlobalContext.getAppContext()).inflate(layout.news_ad_item, parent, false);
                return new NewsSdk.NewsAdapter.ADHolder(view);
            } else if(viewType == 2) {
                view = LayoutInflater.from(GlobalContext.getAppContext()).inflate(layout.news_small_ad, parent, false);
                return new NewsSdk.NewsAdapter.SmallADHolder(view);
            } else if(viewType == 0) {
                view = LayoutInflater.from(GlobalContext.getAppContext()).inflate(layout.news_item_one, parent, false);
                return new NewsSdk.NewsAdapter.NewsOneHolder(view);
            } else {
                view = LayoutInflater.from(GlobalContext.getAppContext()).inflate(layout.news_item_three, parent, false);
                return new NewsSdk.NewsAdapter.NewsThreeHolder(view);
            }
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            final NewsData newsThreeHolder;
            int width;
            int height;
            if(holder instanceof NewsSdk.NewsAdapter.NewsOneHolder) {
                newsThreeHolder = (NewsData)this.data.get(position);
                final NewsSdk.NewsAdapter.NewsOneHolder newsBean = (NewsSdk.NewsAdapter.NewsOneHolder)holder;
                if(newsThreeHolder.rate == 0.0D) {
                    newsThreeHolder.rate = 1.78D;
                }

                width = Util.dip2px(118.0F);
                height = Util.dip2px(66.0F);
                if(NewsSdk.this.newsOkList.contains(newsThreeHolder.news_img)) {
                    newsBean.rlTip.setVisibility(8);
                } else {
                    newsBean.rlTip.setVisibility(0);
                }

                try {
                    if(!TextUtils.isEmpty(newsThreeHolder.news_img.trim())) {
                        Picasso.with(GlobalContext.getAppContext()).load(newsThreeHolder.news_img).resize(width / 2, height / 2).into(newsBean.ivImage, new Callback() {
                            public void onSuccess() {
                                if(!NewsSdk.this.newsOkList.contains(newsThreeHolder.news_img)) {
                                    NewsSdk.this.newsOkList.add(newsThreeHolder.news_img);
                                }

                                newsBean.rlTip.setVisibility(8);
                            }

                            public void onError() {
                            }
                        });
                    }
                } catch (Exception var24) {
                    ;
                }

                newsBean.tvTitle.setText(newsThreeHolder.news_title);
                newsBean.tvSource.setText(newsThreeHolder.source);
                if(newsThreeHolder.recomCount == 0) {
                    int iv1Params = NewsSdk.this.random.nextInt(4950) + 50;
                    NewsDBUtils.getInstance().setNewsRecomCount(newsThreeHolder.id, iv1Params);
                    newsThreeHolder.recomCount = iv1Params;
                }

                newsBean.tvRecomCount.setText(newsThreeHolder.recomCount + "");
                newsBean.ivRecom.setImageResource(drawable.heart_ico_28_l);
                newsBean.rlRoot.setEnabled(true);
                newsBean.rlRoot.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if(!TextUtils.isEmpty(newsThreeHolder.link)) {
                            L.i("qglnewsclick", " 点击新闻了");
                            NewsAdapter.this.jumpDetail(newsThreeHolder);
                        }

                    }
                });
            } else {
                HashMap var30;
                NativeAd var31;
                if(holder instanceof NewsSdk.NewsAdapter.ADHolder) {
                    newsThreeHolder = (NewsData)this.data.get(position);
                    final NewsSdk.NewsAdapter.ADHolder var26 = (NewsSdk.NewsAdapter.ADHolder)holder;
                    if(newsThreeHolder.isFirstAD && NewsCacheMgr.firstAd != null && position == 0) {
                        Context var29 = GlobalContext.getAppContext();
                        L.i("applyAd:" + AdSdk.fetchAd(NewsCacheMgr.firstAd));
                        var26.rlRoot.removeAllViews();
                        AdSdk.applyAd(var29, AdSdk.fetchAd(NewsCacheMgr.firstAd), NewsSdk.this.setAdaRequest(var26.rlRoot));
                        return;
                    }

                    try {
                        if(!TextUtils.isEmpty(newsThreeHolder.bigImageUrl.trim())) {
                            Picasso.with(GlobalContext.getAppContext()).load(newsThreeHolder.bigImageUrl).into(var26.ad_image_view);
                        }
                    } catch (Exception var23) {
                        ;
                    }

                    try {
                        if(!TextUtils.isEmpty(newsThreeHolder.choicesIconcUrl.trim())) {
                            Picasso.with(GlobalContext.getAppContext()).load(newsThreeHolder.choicesIconcUrl).into(var26.ad_privacy_view);
                        }
                    } catch (Exception var22) {
                        ;
                    }

                    try {
                        if(!TextUtils.isEmpty(newsThreeHolder.iconUrl.trim())) {
                            ThreadManager.executeInBackground(new Runnable() {
                                public void run() {
                                    try {
                                        Bitmap bitmap = Picasso.with(GlobalContext.getAppContext()).load(newsThreeHolder.iconUrl).get();
                                        final Bitmap result = BitmapUtils.getCornerBitamp(bitmap, Util.dip2px(48.0F), Util.dip2px(4.0F));
                                        NewsSdk.this.handler.post(new Runnable() {
                                            public void run() {
                                                var26.ad_icon_view.setImageBitmap(result);
                                            }
                                        });
                                    } catch (Exception var3) {
                                        ;
                                    }

                                }
                            });
                        }
                    } catch (Exception var21) {
                        ;
                    }

                    var26.ad_title_text.setText(newsThreeHolder.title);
                    var26.ad_body_text.setText(newsThreeHolder.subTitle);

                    for(width = 0; width < NewsSdk.this.adPosition.size(); ++width) {
                        if(position == ((Integer)NewsSdk.this.adPosition.get(width)).intValue() && width < NewsSdk.ads.size()) {
                            var30 = (HashMap)NewsSdk.ads.get(width);
                            var31 = (NativeAd)var30.get(var30.keySet().toArray()[0]);
                            if(var31 != null) {
                                L.i("adposition=" + width);
                                var31.unregisterView();
                                NewsSdk.this.getReportListener().sendEvent(Constant.New_List_Facebook_Impression, (String)null, (Long)null);
                                var31.registerViewForInteraction(var26.rlRoot);
                                var31.setAdListener(new com.facebook.ads.AdListener() {
                                    public void onError(com.facebook.ads.Ad ad, AdError adError) {
                                    }

                                    public void onAdLoaded(com.facebook.ads.Ad ad) {
                                    }

                                    public void onAdClicked(com.facebook.ads.Ad ad) {
                                        L.i("qglclicktracking", "列表页facebook点击");
                                        NewsSdk.this.getReportListener().sendEvent(Constant.news_listb_click_facebook_native_ad, (String)null, (Long)null);
                                    }

                                    public void onLoggingImpression(com.facebook.ads.Ad ad) {
                                    }
                                });
                                NewsSdk.this.getReportListener().sendEvent(Constant.news_listb_show_facebook_native_ad, "status", Long.valueOf(NewsSdk.this.isFromCache?1L:2L));
                                NewsCacheMgr.adRegistedList.add(var30);
                            }
                        }
                    }
                } else if(holder instanceof NewsSdk.NewsAdapter.SmallADHolder) {
                    newsThreeHolder = (NewsData)this.data.get(position);
                    final NewsSdk.NewsAdapter.SmallADHolder var27 = (NewsSdk.NewsAdapter.SmallADHolder)holder;

                    try {
                        if(!TextUtils.isEmpty(newsThreeHolder.choicesIconcUrl.trim())) {
                            Picasso.with(GlobalContext.getAppContext()).load(newsThreeHolder.choicesIconcUrl).into(var27.ivPrivacyImage);
                        }
                    } catch (Exception var20) {
                        ;
                    }

                    try {
                        if(!TextUtils.isEmpty(newsThreeHolder.iconUrl.trim())) {
                            ThreadManager.executeInBackground(new Runnable() {
                                public void run() {
                                    try {
                                        Bitmap e = Picasso.with(GlobalContext.getAppContext()).load(newsThreeHolder.iconUrl).get();
                                        final Bitmap result = BitmapUtils.getCornerBitamp(e, Util.dip2px(50.0F), Util.dip2px(4.0F));
                                        NewsSdk.this.handler.post(new Runnable() {
                                            public void run() {
                                                var27.ivIcon.setImageBitmap(result);
                                            }
                                        });
                                    } catch (Exception var3) {
                                        L.i("  获取小广告的icon出错了 " + var3.getMessage());
                                    }

                                }
                            });
                        }
                    } catch (Exception var19) {
                        ;
                    }

                    var27.tvTitle.setText(newsThreeHolder.title);
                    var27.tvSubTitle.setText(newsThreeHolder.subTitle);

                    for(width = 0; width < NewsSdk.this.adPosition.size(); ++width) {
                        if(position == ((Integer)NewsSdk.this.adPosition.get(width)).intValue() && width < NewsSdk.ads.size()) {
                            var30 = (HashMap)NewsSdk.ads.get(width);
                            var31 = (NativeAd)var30.get(var30.keySet().toArray()[0]);
                            if(var31 != null) {
                                L.i("adposition=" + width);
                                var31.unregisterView();
                                NewsSdk.this.getReportListener().sendEvent(Constant.New_List_Facebook_Impression, (String)null, (Long)null);
                                var31.registerViewForInteraction(var27.rlRoot);
                                var31.setAdListener(new com.facebook.ads.AdListener() {
                                    public void onError(com.facebook.ads.Ad ad, AdError adError) {
                                    }

                                    public void onAdLoaded(com.facebook.ads.Ad ad) {
                                    }

                                    public void onAdClicked(com.facebook.ads.Ad ad) {
                                        L.i("qglclicktracking", "列表页facebook点击");
                                        NewsSdk.this.getReportListener().sendEvent(Constant.news_listb_click_facebook_native_ad, (String)null, (Long)null);
                                    }

                                    public void onLoggingImpression(com.facebook.ads.Ad ad) {
                                    }
                                });
                                NewsSdk.this.getReportListener().sendEvent(Constant.news_listb_show_facebook_native_ad, "status", Long.valueOf(NewsSdk.this.isFromCache?1L:2L));
                                NewsCacheMgr.adRegistedList.add(var30);
                            }
                        }
                    }
                } else {
                    final NewsSdk.NewsAdapter.NewsThreeHolder var25 = (NewsSdk.NewsAdapter.NewsThreeHolder)holder;
                    final NewsData var28 = (NewsData)this.data.get(position);
                    var25.tvTitle.setText(var28.news_title);
                    width = (Util.getScreenWidth() - Util.dip2px(6.0F) * 2 - Util.dip2px(24.0F)) / 3;
                    height = (int)((float)width / 1.78F);
                    LayoutParams var32 = (LayoutParams)var25.ivImage1.getLayoutParams();
                    var32.width = width;
                    var32.height = height;
                    var25.ivImage1.setLayoutParams(var32);
                    LayoutParams iv2Params = (LayoutParams)var25.ivImage2.getLayoutParams();
                    iv2Params.width = width;
                    iv2Params.height = height;
                    var25.ivImage2.setLayoutParams(iv2Params);
                    LayoutParams iv3Params = (LayoutParams)var25.ivImage3.getLayoutParams();
                    iv3Params.width = width;
                    iv3Params.height = height;
                    var25.ivImage3.setLayoutParams(iv3Params);
                    int count = 0;
                    final String url1 = "";
                    final String url2 = "";
                    final String url3 = "";
                    Content[] content = var28.news_content;

                    for(int finalUrl1 = 0; finalUrl1 < content.length; ++finalUrl1) {
                        if(content[finalUrl1].type.equals("img")) {
                            if(count == 3) {
                                break;
                            }

                            if(count == 0) {
                                url1 = content[finalUrl1].src;
                            } else if(count == 1) {
                                url2 = content[finalUrl1].src;
                            } else if(count == 2) {
                                url3 = content[finalUrl1].src;
                            }

                            ++count;
                        }
                    }

                    if(NewsSdk.this.newsOkList.contains(url1)) {
                        var25.rlTip1.setVisibility(8);
                    } else {
                        var25.rlTip1.setVisibility(0);
                    }

                    if(NewsSdk.this.newsOkList.contains(url2)) {
                        var25.rlTip2.setVisibility(8);
                    } else {
                        var25.rlTip2.setVisibility(0);
                    }

                    if(NewsSdk.this.newsOkList.contains(url3)) {
                        var25.rlTip3.setVisibility(8);
                    } else {
                        var25.rlTip3.setVisibility(0);
                    }

                    Picasso.with(GlobalContext.getAppContext()).load(url1).into(var25.ivImage1, new Callback() {
                        public void onSuccess() {
                            if(!NewsSdk.this.newsOkList.contains(url1)) {
                                NewsSdk.this.newsOkList.add(url1);
                            }

                            var25.rlTip1.setVisibility(8);
                        }

                        public void onError() {
                        }
                    });
                    Picasso.with(GlobalContext.getAppContext()).load(url2).into(var25.ivImage2, new Callback() {
                        public void onSuccess() {
                            if(!NewsSdk.this.newsOkList.contains(url2)) {
                                NewsSdk.this.newsOkList.add(url2);
                            }

                            var25.rlTip2.setVisibility(8);
                        }

                        public void onError() {
                        }
                    });
                    Picasso.with(GlobalContext.getAppContext()).load(url3).into(var25.ivImage3, new Callback() {
                        public void onSuccess() {
                            if(!NewsSdk.this.newsOkList.contains(url3)) {
                                NewsSdk.this.newsOkList.add(url3);
                            }

                            var25.rlTip3.setVisibility(8);
                        }

                        public void onError() {
                        }
                    });
                    var25.tvSource.setText(var28.source);
                    int recomCount = NewsDBUtils.getInstance().getNewsRecomCount(var28.id);
                    if(recomCount == 0) {
                        recomCount = NewsSdk.this.random.nextInt(4950) + 50;
                        NewsDBUtils.getInstance().setNewsRecomCount(var28.id, recomCount);
                    }

                    var28.recomCount = recomCount;
                    var25.tvRecom.setText(recomCount + "");
                    var25.rlRoot.setEnabled(true);
                    var25.rlRoot.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            if(!TextUtils.isEmpty(var28.link)) {
                                L.i("qglnewsclick", " 点击新闻了");
                                NewsAdapter.this.jumpDetail(var28);
                            }

                        }
                    });
                }
            }

        }

        private void jumpDetail(NewsData newsBean) {
            if(NewsSdk.this.jumpDetailListener != null) {
                NewsSdk.this.jumpDetailListener.jumpDetail();
            }

            NewsSdk.this.getReportListener().sendEvent(Constant.news_article_click, (String)null, (Long)null);
            Intent intent = new Intent(GlobalContext.getAppContext(), NewsDetailActivity.class);
            intent.putExtra("url", newsBean.link);
            intent.putExtra("newsData", newsBean);
            intent.addFlags(268435456);
            GlobalContext.getAppContext().startActivity(intent);
        }

        public int getItemCount() {
            return this.data == null?0:this.data.size();
        }

        public void onViewRecycled(ViewHolder holder) {
            if(holder instanceof NewsSdk.NewsAdapter.ADHolder) {
                NewsSdk.NewsAdapter.ADHolder adHolder = (NewsSdk.NewsAdapter.ADHolder)holder;
                adHolder.rlRoot.removeAllViews();
            }

            super.onViewRecycled(holder);
        }

        private class NewsThreeHolder extends ViewHolder {
            private RelativeLayout rlRoot;
            private TextView tvTitle;
            private ImageView ivImage1;
            private ImageView ivImage2;
            private ImageView ivImage3;
            private TextView tvSource;
            private TextView tvRecom;
            private RelativeLayout rlTip1;
            private RelativeLayout rlTip2;
            private RelativeLayout rlTip3;

            public NewsThreeHolder(View itemView) {
                super(itemView);
                this.rlRoot = (RelativeLayout)itemView.findViewById(id.rlRoot);
                this.tvTitle = (TextView)itemView.findViewById(id.tvTitle);
                this.ivImage1 = (ImageView)itemView.findViewById(id.ivImage1);
                this.ivImage2 = (ImageView)itemView.findViewById(id.ivImage2);
                this.ivImage3 = (ImageView)itemView.findViewById(id.ivImage3);
                this.tvSource = (TextView)itemView.findViewById(id.tvSource);
                this.tvRecom = (TextView)itemView.findViewById(id.tvRecom);
                this.rlTip1 = (RelativeLayout)itemView.findViewById(id.rlTip1);
                this.rlTip2 = (RelativeLayout)itemView.findViewById(id.rlTip2);
                this.rlTip3 = (RelativeLayout)itemView.findViewById(id.rlTip3);
            }
        }

        private class SmallADHolder extends ViewHolder {
            private RelativeLayout rlRoot;
            private ImageView ivPrivacyImage;
            private TextView tvTitle;
            private TextView tvSubTitle;
            private ImageView ivIcon;

            public SmallADHolder(View itemView) {
                super(itemView);
                this.rlRoot = (RelativeLayout)itemView.findViewById(id.rlRoot);
                this.ivPrivacyImage = (ImageView)itemView.findViewById(id.ivPrivacyImage);
                this.tvTitle = (TextView)itemView.findViewById(id.tvTitle);
                this.tvSubTitle = (TextView)itemView.findViewById(id.tvSubTitle);
                this.ivIcon = (ImageView)itemView.findViewById(id.ivIcon);
            }
        }

        private class ADHolder extends ViewHolder {
            private RelativeLayout rlRoot;
            private ImageView ad_image_view;
            private ImageView ad_privacy_view;
            private TextView ad_title_text;
            private TextView ad_body_text;
            private ImageView ad_icon_view;

            public ADHolder(View itemView) {
                super(itemView);
                this.rlRoot = (RelativeLayout)itemView.findViewById(id.rlRoot);
                this.ad_image_view = (ImageView)itemView.findViewById(id.ad_image_view);
                this.ad_privacy_view = (ImageView)itemView.findViewById(id.ad_privacy_view);
                this.ad_title_text = (TextView)itemView.findViewById(id.ad_title_text);
                this.ad_body_text = (TextView)itemView.findViewById(id.ad_body_text);
                this.ad_icon_view = (ImageView)itemView.findViewById(id.ad_icon_view);
            }
        }

        private class NewsOneHolder extends ViewHolder {
            private RelativeLayout rlRoot;
            private ImageView ivImage;
            private TextView tvTitle;
            private TextView tvSource;
            private TextView tvRecomCount;
            private ImageView ivRecom;
            private RelativeLayout rlTip;

            public NewsOneHolder(View itemView) {
                super(itemView);
                this.rlRoot = (RelativeLayout)itemView.findViewById(id.rlRoot);
                this.ivImage = (ImageView)itemView.findViewById(id.ivImage);
                this.tvTitle = (TextView)itemView.findViewById(id.tvTitle);
                this.tvSource = (TextView)itemView.findViewById(id.tvSource);
                this.tvRecomCount = (TextView)itemView.findViewById(id.tvRecomCount);
                this.ivRecom = (ImageView)itemView.findViewById(id.ivRecom);
                this.rlTip = (RelativeLayout)itemView.findViewById(id.rlTip);
            }
        }
    }

    public interface ScrollListener {
        void scrollListener(int var1, int var2, boolean var3);
    }

    public interface JumpDetailListener {
        void jumpDetail();
    }

    public interface LoadNewsListener {
        void loadNewsSucc();

        void loadNewsError();
    }

    public interface ReportListener {
        void sendEvent(String var1, String var2, Long var3);
    }
}
