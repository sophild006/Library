//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;
import com.solid.news.util.L;

public class YDirectionWebView extends WebView {
    private float starty;
    private float endy;

    public YDirectionWebView(Context context) {
        super(context);
    }

    public YDirectionWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YDirectionWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch(ev.getAction()) {
        case 0:
            this.starty = ev.getRawY();
            break;
        case 2:
            this.endy = ev.getRawY();
            float distanceY = Math.abs(this.endy - this.starty);
            if(distanceY > 50.0F) {
                this.getParent().requestDisallowInterceptTouchEvent(true);
                L.i("qglwebview  请求不要拦截");
            } else {
                this.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }

        return super.dispatchTouchEvent(ev);
    }
}
