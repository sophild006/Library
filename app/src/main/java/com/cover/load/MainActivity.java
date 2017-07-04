package com.cover.load;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.solid.news.sdk.NewsSdk;

public class MainActivity extends AppCompatActivity implements NewsSdk.JumpDetailListener, NewsSdk.LoadNewsListener {
    private RelativeLayout viewContainer, unNetView, loadFiledLayout;
    private RelativeLayout newsTitleRl;
    private ImageView newsNetStatusIv, retryIv;
    private TextView ssidTv, descTv;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intiView();
    }

    private void intiView() {
        unNetView = (RelativeLayout) findViewById(R.id.wifi_closed_layout);
        viewContainer = (RelativeLayout) findViewById(R.id.viewContainer);
        loadFiledLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        newsNetStatusIv = (ImageView) findViewById(R.id.news_wifi_status_iv);
        retryIv = (ImageView) findViewById(R.id.load_failed_iv);
        newsTitleRl = (RelativeLayout) findViewById(R.id.news_title_layout);

        ssidTv = (TextView) findViewById(R.id.news_wifi_ssid);
        descTv = (TextView) findViewById(R.id.news_wifi_desc);
        Bundle bundle = new Bundle();
        bundle.putString("key", "");
        v = NewsSdk.getInstance().getNewsView(this, this, null);
        viewContainer.removeAllViews();
        viewContainer.addView(v);

        retryIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public void jumpDetail() {

    }

    @Override
    public void loadNewsSucc() {

    }

    @Override
    public void loadNewsError() {

    }
}
