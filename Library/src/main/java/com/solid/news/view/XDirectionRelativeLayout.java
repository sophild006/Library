//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import com.solid.news.util.L;

public class XDirectionRelativeLayout extends RelativeLayout {
    private float startx;
    private float endx;

    public XDirectionRelativeLayout(Context context) {
        super(context);
    }

    public XDirectionRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XDirectionRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch(ev.getAction()) {
        case 0:
            this.startx = ev.getRawX();
            break;
        case 2:
            this.endx = ev.getRawX();
            float distanceX = this.endx - this.startx;
            if(distanceX > 100.0F) {
                L.i("qglwebview  XDirectionRelativeLayout拦截");
                return true;
            }
        }

        return super.onInterceptTouchEvent(ev);
    }
}
