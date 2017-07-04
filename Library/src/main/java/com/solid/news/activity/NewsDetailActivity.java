//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

import com.solid.ad.Ad;
import com.solid.ad.AdListenerBase;
import com.solid.ad.AdSdk;
import com.solid.ad.AdSdk.AdRequest;
import com.solid.ad.AdSdk.AdRequest.Builder;
import com.solid.ad.view.AdNativeViewIdBuilder;
import com.solid.news.R.color;
import com.solid.news.R.drawable;
import com.solid.news.R.id;
import com.solid.news.R.layout;
import com.solid.news.R.string;
import com.solid.news.base.BaseActivity;
import com.solid.news.bean.Content;
import com.solid.news.bean.NewsData;
import com.solid.news.db.NewsDBUtils;
import com.solid.news.logic.LogicSettingMgr;
import com.solid.news.logic.NewsCacheMgr;
import com.solid.news.sdk.NewsSdk;
import com.solid.news.util.Constant;
import com.solid.news.util.GlobalContext;
import com.solid.news.util.L;
import com.solid.news.util.Util;
import com.solid.news.view.InstantTipDialog;
import com.solid.news.view.ScrollerLayoutNews;
import com.solid.news.view.YDirectionWebView;
import com.solid.news.view.ScrollerLayoutNews.OnPageChangeListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsDetailActivity extends BaseActivity {
    private LinearLayout llContain;
    private long startTime;
    private static final Context context = GlobalContext.getAppContext();
    private boolean isShowTipDialog;
    private boolean isRecommed;
    boolean isResume = false;
    private View rootView;
    private RelativeLayout rlRoot;
    private RelativeLayout rlTitle;
    private ScrollerLayoutNews scrollerLayout;
    private LinearLayout llBack;
    private RelativeLayout rlQuickView;
    private ImageView ivSwitch;
    private LinearLayout llTipRoot;
    private LinearLayout llTip;
    private ImageView ivQuick;
    private RelativeLayout rlLine;
    private YDirectionWebView webView;
    private String url;
    private NewsData newsData;
    private static final int TYPE_ORIGIN = 0;
    private static final int TYPE_INSTANT = 1;
    private int currentType;
    private boolean isRedirected;
    private boolean isStartLine;
    private boolean isExist;
    private boolean isChanged;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (NewsDetailActivity.this.isStartLine && NewsDetailActivity.this.currentType == 0) {
                NewsDetailActivity.this.startLineAnimator();
            } else {
                NewsDetailActivity.this.rlLine.setVisibility(8);
            }

        }
    };
    private boolean isDownLeft;
    private boolean isLoading = true;
    private boolean isLoadSucc;
    private LinearLayout adContain;
    private LinearLayout adSmallContain;
    private LinearLayout topAdSmallContain;
    private Typeface titleTypeFace;
    private Typeface contentTypeFace;

    public NewsDetailActivity() {
        this.titleTypeFace = Typeface.createFromAsset(context.getAssets(), "MinionPro-Regular.otf");
        this.contentTypeFace = Typeface.createFromAsset(context.getAssets(), "phagspa.ttf");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.news_activity_news_detail);
        this.llContain = (LinearLayout) this.findViewById(id.llContain);
        this.getData();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
        this.getData();
    }

    private void getData() {
        Intent intent = this.getIntent();
        if (intent == null) {
            this.finish();
        } else {
            NewsSdk.getInstance().getReportListener().sendEvent(Constant.news_detail_show, (String) null, (Long) null);
            LogicSettingMgr.getInstance().setIntoDetailCount();
            String url = intent.getStringExtra("url");
            NewsData newsData = (NewsData) intent.getSerializableExtra("newsData");
            LayoutParams params = new LayoutParams(-1, -1);
            params.topMargin = Util.getStatusBarHeight();
            this.llContain.addView(this.showDetails(url, newsData), params);
            this.startTime = System.currentTimeMillis();
        }

    }

    protected void onResume() {
        super.onResume();
        this.isResume = true;
    }

    protected void onPause() {
        super.onPause();
        this.isResume = false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && this.isResume) {
            this.exitDetail();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        L.i("qglclicktracking", "详情页 退出");
        NewsSdk.getInstance().getReportListener().sendEvent(Constant.news_detail_exit, (String) null, (Long) null);
        long duration = (System.currentTimeMillis() - this.startTime) / 1000L;
        L.i("qglclicktracking", "详情页时间=" + duration);
        NewsSdk.getInstance().getReportListener().sendEvent(Constant.news_detail_duration, "duration", Long.valueOf(duration));
    }

    public View showDetails(String url, NewsData newsData) {
        L.i("qglnewsshowdetails", "start");
        this.url = url;
        this.newsData = newsData;
        this.isExist = false;
        this.isRedirected = false;
        this.isStartLine = false;
        this.initView();
        L.i("qglnewsshowdetails", "add");
        LayoutParams childParams = new LayoutParams(-1, -1);
        this.scrollerLayout.addView(this.getWebView(), childParams);
        this.scrollerLayout.addView(this.getInstantView(), childParams);
        boolean isInstant = LogicSettingMgr.getInstance().getIsInstant();
        if (isInstant) {
            this.currentType = 1;
            this.ivSwitch.setImageResource(drawable.original_ico_w_42);
            this.scrollerLayout.setCurrentItem(1);
        } else {
            this.currentType = 0;
            this.isStartLine = true;
            this.startLineAnimator();
            this.ivSwitch.setImageResource(drawable.quick_ico_w_42);
            this.scrollerLayout.setCurrentItem(0);
            this.handler.postDelayed(new Runnable() {
                public void run() {
                    if (!NewsDetailActivity.this.isChanged) {
                        NewsDetailActivity.this.changeTab(1);
                    }

                }
            }, 5000L);
        }

        this.initListener();
        L.i("qglnewsshowdetails", "end");
        return this.rootView;
    }

    public void UpdateNews(String url, NewsData newsData) {
        this.url = url;
        this.newsData = newsData;
        this.isExist = false;
        this.isRedirected = false;
        this.isStartLine = false;
        this.isChanged = false;
        LayoutParams params = new LayoutParams(-1, -1);
        this.scrollerLayout.removeAllViews();
        this.scrollerLayout.addView(this.getWebView(), params);
        this.scrollerLayout.addView(this.getInstantView(), params);
        boolean isInstant = LogicSettingMgr.getInstance().getIsInstant();
        if (isInstant) {
            this.currentType = 1;
            this.ivSwitch.setImageResource(drawable.original_ico_w_42);
            this.scrollerLayout.setCurrentItem(1);
        } else {
            this.currentType = 0;
            this.rlLine.setVisibility(View.VISIBLE);
            this.isStartLine = true;
            this.startLineAnimator();
            this.ivSwitch.setImageResource(drawable.quick_ico_w_42);
            this.scrollerLayout.setCurrentItem(0);
            this.handler.postDelayed(new Runnable() {
                public void run() {
                    if (!NewsDetailActivity.this.isChanged) {
                        NewsDetailActivity.this.changeTab(1);
                    }

                }
            }, 5000L);
        }

        this.initListener();
    }

    private void initView() {
        this.rootView = View.inflate(context, layout.news_news_detail, (ViewGroup) null);
        this.rlRoot = (RelativeLayout) this.rootView.findViewById(id.rlRoot);
        this.rlTitle = (RelativeLayout) this.rootView.findViewById(id.rlTitle);
        if (Constant.pkgName.equals("com.solidunion.callrecorder")) {
            this.rlTitle.setBackgroundResource(color.callRecordColor);
        } else if (Constant.pkgName.equals("com.zoomy.wifi")) {
            this.rlTitle.setBackgroundResource(color.wifiColor);
        } else if (Constant.pkgName.equals("com.free.wifi.update")) {
            this.rlTitle.setBackgroundResource(color.freeWifiColor);
        }

        this.scrollerLayout = (ScrollerLayoutNews) this.rootView.findViewById(id.scrollerLayout);
        this.rlLine = (RelativeLayout) this.rootView.findViewById(id.rlLine);
        this.llBack = (LinearLayout) this.rootView.findViewById(id.llBack);
        this.rlQuickView = (RelativeLayout) this.rootView.findViewById(id.rlQuickView);
        this.ivSwitch = (ImageView) this.rootView.findViewById(id.ivSwitch);
        this.rlLine.setVisibility(View.VISIBLE);
    }

    private void initListener() {
        this.scrollerLayout.addOnPageChangeListener(new OnPageChangeListener() {
            public void onTouchDown(float startx) {
                if (startx > (float) (Util.getScreenWidth() / 4)) {
                    NewsDetailActivity.this.isDownLeft = true;
                } else {
                    NewsDetailActivity.this.isDownLeft = true;
                }

            }

            public void onPageScrolled(int position, float distanceX, int scrollX) {
                if (distanceX > 0.0F && !NewsDetailActivity.this.isExist && NewsDetailActivity.this.isDownLeft) {
                    NewsDetailActivity.this.isExist = true;
                    NewsDetailActivity.this.exitDetail();
                }

            }

            public void onPageSelected(int position) {
                if (position == 0) {
                    NewsDetailActivity.this.changeTab(0);
                } else {
                    NewsDetailActivity.this.changeTab(1);
                }

            }

            public void onLayoutOver() {
            }
        });
        this.llTip.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (NewsDetailActivity.this.scrollerLayout.getChildCount() == 2) {
                    NewsDetailActivity.this.changeTab(1);
                }

            }
        });
        this.llBack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NewsDetailActivity.this.exitDetail();
            }
        });
        this.rlQuickView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (NewsDetailActivity.this.currentType == 1) {
                    NewsDetailActivity.this.changeTab(0);
                } else {
                    NewsDetailActivity.this.changeTab(1);
                }

            }
        });
    }

    private void exitDetail() {
        float screenWidth = (float) Util.getScreenWidth();
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0F, screenWidth}).setDuration(300L);
        animator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                NewsDetailActivity.this.rootView.setX(((Float) animation.getAnimatedValue()).floatValue());
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                NewsDetailActivity.this.removeDetail();
            }
        });
        animator.start();
    }

    private void backDetail() {
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{this.rootView.getX(), 0.0F}).setDuration(200L);
        animator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                NewsDetailActivity.this.rootView.setX(((Float) animation.getAnimatedValue()).floatValue());
            }
        });
        animator.start();
    }

    private void removeDetail() {
        if (this.rootView != null) {
            this.finish();
        }

    }

    private void changeTab(int type) {
        this.currentType = type;
        this.isChanged = true;
        if (type == 0) {
            this.ivSwitch.setImageResource(drawable.quick_ico_w_42);
            this.scrollerLayout.setCurrentItem(0);
            if (this.isLoading && !this.isLoadSucc) {
                this.llTipRoot.setVisibility(View.VISIBLE);
                this.rlLine.setVisibility(View.VISIBLE);
                this.isStartLine = true;
                this.startLineAnimator();
            }

            if (!this.isLoading && this.isLoadSucc) {
                this.isStartLine = false;
                this.llTipRoot.setVisibility(View.GONE);
                this.rlLine.setVisibility(View.GONE);
            }

            if (this.isStartLine) {
                this.llTipRoot.setVisibility(View.VISIBLE);
                this.rlLine.setVisibility(View.VISIBLE);
                this.handler.sendEmptyMessage(0);
            }
        } else {
            this.ivSwitch.setImageResource(drawable.original_ico_w_42);
            boolean isInstant = LogicSettingMgr.getInstance().getIsInstant();
            int count = LogicSettingMgr.getInstance().getIntoDetailCount();
            boolean isDontRemind = LogicSettingMgr.getInstance().getDontRemind();
            L.i(" newsDetail       谈框的条件是  !isInstant && count == 1 && !isDontRemind && !isShowTipDialog");
            L.i(" newsDetail        isInstant=" + isInstant + "  count=" + count + " isDontRemind=" + isDontRemind + " isShowTipDialog=" + this.isShowTipDialog);
            if (!isInstant && count == 1 && !isDontRemind && !this.isShowTipDialog) {
                this.isShowTipDialog = true;
                InstantTipDialog dialog = new InstantTipDialog(this);
                NewsSdk.getInstance().getReportListener().sendEvent(Constant.news_instbox_show, (String) null, (Long) null);
                dialog.show();
            }

            NewsSdk.getInstance().getReportListener().sendEvent(Constant.news_detail_instant, (String) null, (Long) null);
            this.scrollerLayout.setCurrentItem(1);
            this.rlLine.setVisibility(8);
        }

    }

    private View getWebView() {
        View view = View.inflate(context, layout.news_webview_layout, (ViewGroup) null);
        this.webView = (YDirectionWebView) view.findViewById(id.webView);
        this.llTipRoot = (LinearLayout) view.findViewById(id.llTipRoot);
        this.llTip = (LinearLayout) view.findViewById(id.llTip);
        this.ivQuick = (ImageView) view.findViewById(id.ivQuick);
        ProgressBar prb = (ProgressBar) view.findViewById(id.prb);
        if (Constant.pkgName.equals("com.solidunion.callrecorder")) {
            this.ivQuick.setImageResource(drawable.quick_label_b_348_call_record);
        } else if (Constant.pkgName.equals("com.zoomy.wifi")) {
            this.ivQuick.setImageResource(drawable.quick_label_o_348_wifi);
        } else if (Constant.pkgName.equals("com.free.wifi.update")) {
            this.ivQuick.setImageResource(drawable.quick_label_b_348_free_wifi);
            prb.setIndeterminateDrawable(ContextCompat.getDrawable(GlobalContext.getAppContext(), drawable.shape_prob_free_wifi));
        }

        WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(2);
        this.webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                NewsDetailActivity.this.isRedirected = true;
                return true;
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!NewsDetailActivity.this.isRedirected) {
                    L.i("qglwebviewpage", "onPageStart");
                    NewsDetailActivity.this.isLoading = true;
                    NewsDetailActivity.this.isLoadSucc = false;
                }

                NewsDetailActivity.this.isRedirected = false;
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!NewsDetailActivity.this.isRedirected) {
                    L.i("qglwebviewpage", "onPageFinished");
                    NewsDetailActivity.this.isLoading = false;
                    NewsDetailActivity.this.isLoadSucc = true;
                    NewsDetailActivity.this.llTipRoot.setVisibility(8);
                    NewsDetailActivity.this.rlLine.setVisibility(8);
                    boolean b = System.currentTimeMillis() - NewsDetailActivity.this.startTime < 5000L;
                    boolean isInstant = LogicSettingMgr.getInstance().getIsInstant();
                    if (!isInstant && !NewsDetailActivity.this.isChanged && b) {
                        NewsSdk.getInstance().getReportListener().sendEvent(Constant.news_detail_original, (String) null, (Long) null);
                    }
                }

            }
        });
        this.webView.loadUrl(this.url);
        return view;
    }

    private void startLineAnimator() {
        this.startLine();
        this.handler.sendEmptyMessageDelayed(0, 450L);
    }

    private void startLine() {
        L.i("qglstartline", "startline");
        final View lineView = new View(context);
        lineView.setBackgroundColor(Color.parseColor("#7BC8BD"));
        if (Constant.pkgName.equals("com.free.wifi.update")) {
            lineView.setBackgroundColor(Color.parseColor("#a9daff"));
        }

        this.rlLine.addView(lineView, Util.getScreenWidth() / 4, Util.dip2px(2.0F));
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{(float) (-Util.getScreenWidth() / 4), (float) Util.getScreenWidth()}).setDuration(2000L);
        animator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = ((Float) animation.getAnimatedValue()).floatValue();
                lineView.setX(value);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                NewsDetailActivity.this.rlLine.removeView(lineView);
            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    private View getInstantView() {
        View instantView = View.inflate(context, layout.news_news_detail_instant, (ViewGroup) null);
        LinearLayout llContain = (LinearLayout) instantView.findViewById(id.llContain);
        LinearLayout llTitle = (LinearLayout) instantView.findViewById(id.llTitle);
        boolean isHaveImage = false;

        int textLeftDistance;
        for (textLeftDistance = 0; textLeftDistance < this.newsData.news_content.length; ++textLeftDistance) {
            if (this.newsData.news_content[textLeftDistance].type.equals("img")) {
                isHaveImage = true;
            }
        }

        textLeftDistance = Util.dip2px(24.0F);
        int contentImageLeftDistance = Util.dip2px(7.0F);
        int adLeftDistance = Util.dip2px(9.0F);
        int lineLeftMargin = Util.dip2px(7.0F);
        LayoutParams titleParams = new LayoutParams(-1, -1);
        titleParams.leftMargin = textLeftDistance;
        titleParams.rightMargin = textLeftDistance;
        titleParams.topMargin = Util.dip2px(18.0F);
        TextView tvTitle = new TextView(context);
        tvTitle.setTextColor(Color.parseColor("#333333"));
        tvTitle.setTextSize(23.0F);
        tvTitle.setText(this.newsData.news_title);
        tvTitle.setTypeface(this.titleTypeFace);
        llTitle.addView(tvTitle, titleParams);
        LayoutParams timeParams = new LayoutParams(-1, -1);
        timeParams.topMargin = Util.dip2px(16.0F);
        timeParams.leftMargin = textLeftDistance;
        timeParams.rightMargin = textLeftDistance;
        View timeView = View.inflate(context, layout.news_news_time, (ViewGroup) null);
        TextView tvTime = (TextView) timeView.findViewById(id.tvTime);
        tvTime.setTypeface(this.contentTypeFace);
        tvTime.setText(this.newsData.pub_time.substring(2, this.newsData.pub_time.length()));
        TextView tvSource = (TextView) timeView.findViewById(id.tvSource);
        tvSource.setText(this.newsData.source);
        tvSource.setTypeface(this.contentTypeFace);
        llTitle.addView(timeView, timeParams);
        LayoutParams titleLineParams = new LayoutParams(-1, Util.dip2px(1.0F));
        View titleBottomLine = new View(context);
        titleBottomLine.setBackgroundColor(Color.parseColor("#e2e2e2"));
        llTitle.addView(titleBottomLine, titleLineParams);
        LayoutParams lineParams = new LayoutParams(-1, -2);
        lineParams.leftMargin = lineLeftMargin;
        lineParams.rightMargin = lineLeftMargin;
        this.topAdSmallContain = new LinearLayout(context);
        LayoutParams topAdSmallContainParams = new LayoutParams(-1, -2);
        llContain.addView(this.topAdSmallContain, topAdSmallContainParams);
        this.getNewsTopSmallAd();
        int sourceView;
        LayoutParams tvViewSource;
        if (!isHaveImage) {
            sourceView = Util.getScreenWidth() - contentImageLeftDistance * 2;
            if (this.newsData.rate == 0.0D) {
                this.newsData.rate = 1.78D;
            }

            int sourceViewParams = (int) ((double) sourceView / this.newsData.rate);
            tvViewSource = new LayoutParams(sourceView, sourceViewParams);
            tvViewSource.topMargin = Util.dip2px(20.0F);
            tvViewSource.leftMargin = contentImageLeftDistance;
            ImageView rlLike = new ImageView(context);
            rlLike.setScaleType(ScaleType.FIT_XY);

            try {
                if (!TextUtils.isEmpty(this.newsData.news_img.trim())) {
                    Picasso.with(context).load(this.newsData.news_img).resize(sourceView / 4, sourceViewParams / 4).into(rlLike);
                }
            } catch (Exception var46) {
                ;
            }

            llContain.addView(rlLike, tvViewSource);
        }

        ImageView ivLike;
        for (sourceView = 0; sourceView < this.newsData.news_content.length; ++sourceView) {
            Content var48 = this.newsData.news_content[sourceView];
            if (var48.type.equals("img")) {
                int var50 = Util.getScreenWidth() - contentImageLeftDistance * 2;
                if (this.newsData.rate == 0.0D) {
                    this.newsData.rate = 1.78D;
                }

                int var52 = (int) ((double) var50 / this.newsData.rate);
                LayoutParams rlDisLike = new LayoutParams(var50, var52);
                rlDisLike.topMargin = Util.dip2px(20.0F);
                rlDisLike.leftMargin = contentImageLeftDistance;
                ivLike = new ImageView(context);
                ivLike.setScaleType(ScaleType.FIT_XY);

                try {
                    if (!TextUtils.isEmpty(var48.src.trim())) {
                        Picasso.with(context).load(var48.src).resize(var50 / 4, var52 / 4).into(ivLike);
                    }
                } catch (Exception var45) {
                    ;
                }

                llContain.addView(ivLike, rlDisLike);
            } else {
                tvViewSource = new LayoutParams(-1, -1);
                tvViewSource.leftMargin = textLeftDistance;
                tvViewSource.rightMargin = textLeftDistance;
                tvViewSource.topMargin = Util.dip2px(20.0F);
                TextView var53 = new TextView(context);
                if (var48.bold != null) {
                    var53.getPaint().setFakeBoldText(true);
                }

                var53.setTextColor(Color.parseColor("#333333"));
                var53.setTextSize(15.0F);
                var53.setText(var48.content);
                var53.setTypeface(this.contentTypeFace);
                llContain.addView(var53, tvViewSource);
            }
        }

        View var47 = View.inflate(context, layout.news_islike, (ViewGroup) null);
        LayoutParams var49 = new LayoutParams(-1, -2);
        var49.leftMargin = textLeftDistance;
        var49.rightMargin = textLeftDistance;
        var49.topMargin = Util.dip2px(34.0F);
        llContain.addView(var47, var49);
        TextView var51 = (TextView) var47.findViewById(id.tvViewSource);
        var51.setTypeface(this.contentTypeFace);
        var51.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NewsSdk.getInstance().getReportListener().sendEvent(Constant.news_viewsource_click, (String) null, (Long) null);
                NewsDetailActivity.this.changeTab(0);
            }
        });
        RelativeLayout var54 = (RelativeLayout) var47.findViewById(id.rlLike);
        RelativeLayout var55 = (RelativeLayout) var47.findViewById(id.rlDisLike);
        ivLike = (ImageView) var47.findViewById(id.ivLike);
        final TextView tvLikeCount = (TextView) var47.findViewById(id.tvLikeCount);
        final ImageView ivDislike = (ImageView) var47.findViewById(id.ivDislike);
        final LinearLayout llLikeAnimator = (LinearLayout) var47.findViewById(id.llLikeAnimator);
        this.changeExpress(ivLike, ivDislike, tvLikeCount, false, false, llLikeAnimator);
        var54.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NewsDetailActivity.this.changeExpress(ivLike, ivDislike, tvLikeCount, true, true, llLikeAnimator);
            }
        });
        var55.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                NewsDetailActivity.this.changeExpress(ivLike, ivDislike, tvLikeCount, true, false, llLikeAnimator);
            }
        });
        this.adContain = new LinearLayout(context);
        LayoutParams adParams = new LayoutParams(-1, -2);
        adParams.leftMargin = adLeftDistance;
        adParams.rightMargin = adLeftDistance;
        adParams.topMargin = Util.dip2px(46.0F);
        llContain.addView(this.adContain, adParams);
        this.getNewsAd();
        View relatedReading = View.inflate(context, layout.news_related_reading, (ViewGroup) null);
        LayoutParams relatedParams = new LayoutParams(-1, -2);
        TextView tvRelated = (TextView) relatedReading.findViewById(id.tvRelated);
        LayoutParams tvRelatedParams = (LayoutParams) tvRelated.getLayoutParams();
        tvRelatedParams.width = Util.getScreenWidth() - Util.dip2px(204.0F);
        tvRelated.setLayoutParams(tvRelatedParams);
        llContain.addView(relatedReading, relatedParams);
        LayoutParams mayLikeParams = new LayoutParams(-1, -2);
        ArrayList fourNews = NewsCacheMgr.getSingle().getFourNews(this.newsData);

        for (int i = 0; i < fourNews.size(); ++i) {
            View mayLikeItem = View.inflate(context, layout.news_may_like_item, (ViewGroup) null);
            ImageView ivImage = (ImageView) mayLikeItem.findViewById(id.ivMayLikeItemImage);
            final RelativeLayout rlTip = (RelativeLayout) mayLikeItem.findViewById(id.rlMayLikeItemTip);
            TextView tvMayLikeTitle = (TextView) mayLikeItem.findViewById(id.tvMayLikeItemTitle);
            TextView tvMayLikeSource = (TextView) mayLikeItem.findViewById(id.tvMayLikeItemSource);
            TextView tvRecomCount = (TextView) mayLikeItem.findViewById(id.tvMayLikeItemRecomCount);
            if (i == 0) {
                mayLikeParams.topMargin = Util.dip2px(29.0F);
            } else {
                mayLikeParams.topMargin = 0;
            }

            llContain.addView(mayLikeItem, mayLikeParams);
            final NewsData mayLikeNews = (NewsData) fourNews.get(i);
            tvMayLikeSource.setText(mayLikeNews.source);
            L.i(" mayliketitle=" + mayLikeNews.news_title);
            tvMayLikeTitle.setTypeface(this.contentTypeFace);
            tvMayLikeTitle.setText(mayLikeNews.news_title);
            tvRecomCount.setText(mayLikeNews.recomCount + "");

            try {
                if (!TextUtils.isEmpty(mayLikeNews.news_img.trim())) {
                    Picasso.with(context).load(mayLikeNews.news_img).into(ivImage, new Callback() {
                        public void onSuccess() {
                            rlTip.setVisibility(8);
                        }

                        public void onError() {
                        }
                    });
                }
            } catch (Exception var44) {
                ;
            }

            mayLikeItem.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    L.i("qglclicktracking", "点击了 推荐的新闻");
                    NewsSdk.getInstance().getReportListener().sendEvent(Constant.news_detail_recommend, (String) null, (Long) null);
                    NewsDetailActivity.this.UpdateNews(mayLikeNews.link, mayLikeNews);
                }
            });
            View lineView = View.inflate(context, layout.news_line, (ViewGroup) null);
            llContain.addView(lineView, lineParams);
        }

        return instantView;
    }

    public void getNewsAd() {
        if (!LogicSettingMgr.getInstance().getIsProMode()) {
            L.i("qgl", "开始获取新闻大广告了");
            final boolean isCache = AdSdk.shared(this).adCached("news_detail");
            AdSdk.shared(this).loadAd(this,this.createBigAdRequest(this.adContain),new AdListenerBase<Ad>(){
                @Override
                public void onLoaded(Ad ad) {
                    super.onLoaded(ad);
                    NewsSdk.getInstance().getReportListener().sendEvent("news_detail_show_facebook_native_ad", "status", Long.valueOf(isCache ? 1L : 2L));
                }

                @Override
                public void onFailed(Ad ad, int code, String msg, Object err) {
                    super.onFailed(ad, code, msg, err);
                    L.i("getNewsAd   load onFailed  code=" + code + " msg=" + msg);
                }
            });
//            AdSdk.shared(this).loadAd(this, this.createBigAdRequest(this.adContain), new AdListenerBase() {
//                public void onLoaded(Ad ad) {
//                    L.i("getNewsAd onLoaded");
//
//                }
//
//                public void onClicked(Ad ad) {
//                }
////
//                public void onFailed(Ad ad, int code, String msg, Object err) {
//                    super.onFailed(ad, code, msg, err);
//                    L.i("getNewsAd   load onFailed  code=" + code + " msg=" + msg);
//                }
//            });
        }
    }

    public void getNewsTopSmallAd() {
        if (!LogicSettingMgr.getInstance().getIsProMode()) {
            L.i("qgl", "开始获取新闻 顶部 广告了");
            final boolean cached = AdSdk.shared(this).adCached("news_top");
            AdSdk.shared(this).loadAd(this, this.createTopAdRequest(this.topAdSmallContain), new AdListenerBase() {
                public void onLoaded(Ad ad) {
                    L.i("getNewsTopSmallAd onLoaded");
                    NewsSdk.getInstance().getReportListener().sendEvent("news_top_show_facebook_native_ad", "status", Long.valueOf(cached ? 1L : 2L));
                }

                public void onClicked(Ad ad) {
                }

                public void onFailed(Ad ad, int code, String msg, Object err) {
                    super.onFailed(ad, code, msg, err);
                    L.i("getNewsTopSmallAd   load onFailed  code=" + code + " msg=" + msg);
                }
            });
        }
    }

    private void changeExpress(ImageView likeImageView, ImageView disLikeImageView, TextView tvLikeCount, boolean isClick, boolean isLike, final LinearLayout llLikeAnimator) {
        boolean isExpress = NewsDBUtils.getInstance().isExpress(this.newsData.id);
        int likeCount = NewsDBUtils.getInstance().getNewsRecomCount(this.newsData.id);
        if (isClick) {
            if (isExpress) {
                Toast.makeText(context, context.getResources().getString(string.expressed), 0).show();
                return;
            }

            if (isLike) {
                NewsSdk.getInstance().getReportListener().sendEvent(Constant.news_detail_like, (String) null, (Long) null);
                likeImageView.setImageResource(drawable.like_ico_42_b);
                NewsDBUtils.getInstance().express(this.newsData.id, 1);
                L.i("qglclicktracking", "详情页 点赞");
                NewsDBUtils.getInstance().setNewsRecomCount(this.newsData.id, likeCount + 1);
                tvLikeCount.setText("(" + (likeCount + 1) + ")");
                NewsCacheMgr.getSingle().changeNewsRecommed(this.newsData.id, this.newsData.recomCount + 1);
                NewsSdk.getInstance().notifyData();
                this.isRecommed = true;
                ValueAnimator type = ValueAnimator.ofFloat(new float[]{llLikeAnimator.getY(), (float) Util.dip2px(10.0F)}).setDuration(300L);
                type.addUpdateListener(new AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = ((Float) animation.getAnimatedValue()).floatValue();
                        llLikeAnimator.setY(value);
                    }
                });
                type.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        llLikeAnimator.setVisibility(0);
                    }

                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        llLikeAnimator.setVisibility(4);
                    }
                });
                type.start();
            } else {
                NewsSdk.getInstance().getReportListener().sendEvent(Constant.news_detail_dislike, (String) null, (Long) null);
                disLikeImageView.setImageResource(drawable.dislike_ico_42_r);
                NewsDBUtils.getInstance().express(this.newsData.id, 0);
                Toast.makeText(context, context.getResources().getString(string.click_dislike), 0).show();
            }
        } else {
            if (isExpress) {
                int type1 = NewsDBUtils.getInstance().getExpress(this.newsData.id);
                if (type1 == 1) {
                    likeImageView.setImageResource(drawable.like_ico_42_b);
                } else {
                    disLikeImageView.setImageResource(drawable.dislike_ico_42_r);
                }
            }

            tvLikeCount.setText("(" + likeCount + ")");
        }

    }

    AdRequest createTopAdRequest(LinearLayout llADContain) {
        AdRequest adRequest = (new Builder(this, "news_top")).setContainer(llADContain).setAdNativeViewBuilder((new AdNativeViewIdBuilder(this)).setLayoutId(layout.news_instant_top_small_ad).setIconViewId(id.ad_icon_view).setTitleViewId(id.ad_title_text).setBodyViewId(id.ad_body_text).setCallToActionViewId(id.ad_call_to_action_text).setPrivacyViewId(id.ad_privacy_view)).build();
        return adRequest;
    }

    AdRequest createBigAdRequest(LinearLayout llADContain) {
        AdRequest adRequest = (new Builder(this, "news_detail")).setContainer(llADContain).setAdNativeViewBuilder((new AdNativeViewIdBuilder(this)).setLayoutId(layout.news_news_detail_big_ad).setIconViewId(id.ad_icon_view).setTitleViewId(id.ad_title_text).setBodyViewId(id.ad_body_text).setCallToActionViewId(id.ad_call_to_action_text).setImageViewId(id.ad_image_view).setFacebookMediaViewId(id.ad_media_view_facebook).setPrivacyViewId(id.ad_privacy_view)).build();
        return adRequest;
    }
}
