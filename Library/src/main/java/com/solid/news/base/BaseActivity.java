//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.view.View;
import android.view.Window;

public class BaseActivity extends Activity {
    public BaseActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(1);
        this.setTransparentStatusBar();
    }

    protected void setTransparentStatusBar() {
        if(VERSION.SDK_INT >= 19) {
            try {
                Window window = this.getWindow();
                if(VERSION.SDK_INT < 21) {
                    window.addFlags(67108864);
                    return;
                }

                View decorView = this.getWindow().getDecorView();
                short option = 1280;
                decorView.setSystemUiVisibility(option);
                this.getWindow().setStatusBarColor(0);
            } catch (Exception var4) {
                ;
            }
        }

    }
}
