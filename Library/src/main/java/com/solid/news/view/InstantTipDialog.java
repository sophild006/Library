//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.solid.news.R.drawable;
import com.solid.news.R.id;
import com.solid.news.R.layout;
import com.solid.news.R.style;
import com.solid.news.logic.LogicSettingMgr;
import com.solid.news.sdk.NewsSdk;
import com.solid.news.util.Constant;

public class InstantTipDialog extends Dialog {
    private View view;
    private Activity activity;
    private LinearLayout llDontRemind;
    private ImageView ivTick;
    private TextView tvNo;
    private TextView tvYes;
    private boolean isDontRemind;

    public InstantTipDialog(Activity activity) {
        this(activity, style.ThemeDialog);
    }

    public InstantTipDialog(Activity activity, int themeResId) {
        super(activity, themeResId);
        this.activity = activity;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.view);
    }

    public void show() {
        if (this.activity != null && !this.activity.isFinishing()) {
            this.view = View.inflate(this.getContext(), layout.news_instant_tip, (ViewGroup) null);
            this.initView();
            super.show();
        }
    }

    private void initView() {
        this.llDontRemind = (LinearLayout) this.view.findViewById(id.llDontRemind);
        this.ivTick = (ImageView) this.view.findViewById(id.ivTick);
        this.tvNo = (TextView) this.view.findViewById(id.tvNo);
        this.tvYes = (TextView) this.view.findViewById(id.tvYes);
        this.llDontRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InstantTipDialog.this.isDontRemind = !InstantTipDialog.this.isDontRemind;
                if (InstantTipDialog.this.isDontRemind) {
                    InstantTipDialog.this.ivTick.setImageResource(drawable.tick_btn_b_28);
                } else {
                    InstantTipDialog.this.ivTick.setImageResource(drawable.tick_btn_g_28);
                }
            }
        });

        this.tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsSdk.getInstance().getReportListener().sendEvent(Constant.news_instbox_no, (String) null, (Long) null);
                if (InstantTipDialog.this.isDontRemind) {
                    LogicSettingMgr.getInstance().setDontRemind(true);
                } else {
                    LogicSettingMgr.getInstance().setDontRemind(false);
                }

                LogicSettingMgr.getInstance().setIsInstant(false);
                InstantTipDialog.this.dismiss();
            }
        });
        this.tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsSdk.getInstance().getReportListener().sendEvent(Constant.news_instbox_yes, (String) null, (Long) null);
                if (InstantTipDialog.this.isDontRemind) {
                    LogicSettingMgr.getInstance().setDontRemind(true);
                } else {
                    LogicSettingMgr.getInstance().setDontRemind(false);
                }

                LogicSettingMgr.getInstance().setIsInstant(true);
                InstantTipDialog.this.dismiss();
            }
        });
    }
}
