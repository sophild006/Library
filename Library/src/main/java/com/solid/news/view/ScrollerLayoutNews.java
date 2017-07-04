//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.view;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;
import com.solid.news.util.L;
import com.solid.news.util.Util;

public class ScrollerLayoutNews extends LinearLayout {
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private float mXDown;
    private float mXMove;
    private float mXLastMove;
    private int leftBorder;
    private int rightBorder;
    private int targetIndex;
    private boolean isUp;
    float startx;
    float endx;
    private static final int screenWidth = Util.getScreenWidth();
    private ScrollerLayoutNews.OnPageChangeListener listener;
    private boolean notTouch;

    public ScrollerLayoutNews(Context context) {
        this(context, (AttributeSet)null);
    }

    public ScrollerLayoutNews(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.startx = 0.0F;
        this.endx = 0.0F;
        this.mScroller = new Scroller(context, new DecelerateInterpolator(1.2F));
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        this.setOrientation(0);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = this.getChildCount();

        int width;
        for(width = 0; width < childCount; ++width) {
            View height = this.getChildAt(width);
            this.measureChild(height, widthMeasureSpec, heightMeasureSpec);
        }

        width = getDefaultSize(this.getSuggestedMinimumWidth(), widthMeasureSpec);
        int var6 = getDefaultSize(this.getSuggestedMinimumWidth(), heightMeasureSpec);
        this.setMeasuredDimension(width, var6);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed) {
            if(this.getChildAt(0) != null) {
                this.leftBorder = this.getChildAt(0).getLeft();
            }

            this.rightBorder = this.getChildCount() * this.getWidth();
            this.scrollTo(this.getWidth() * this.targetIndex, 0);
        }

        if(this.listener != null) {
            this.listener.onLayoutOver();
        }

    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch(ev.getAction()) {
        case 0:
            this.isUp = false;
            this.mXDown = ev.getRawX();
            this.mXLastMove = this.mXDown;
            if(this.listener != null) {
                this.listener.onTouchDown(this.mXDown);
            }
            break;
        case 2:
            this.mXMove = ev.getRawX();
            float diff = Math.abs(this.mXMove - this.mXDown);
            this.mXLastMove = this.mXMove;
            if(diff > 100.0F) {
                L.i("qgl", "ScrollerLayout 拦截了");
                this.notTouch = false;
                return true;
            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if(this.notTouch) {
            return true;
        } else {
            if(this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }

            this.mVelocityTracker.addMovement(event);
            switch(event.getAction()) {
            case 0:
                this.isUp = false;
                this.startx = event.getX();
                break;
            case 1:
                this.mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = this.mVelocityTracker.getXVelocity();
                this.endx = event.getX();
                this.isUp = true;
                if(Math.abs(xVelocity) > 300.0F) {
                    if(Math.abs(this.mXLastMove - this.mXDown) > (float)(this.getWidth() / 8)) {
                        if(this.mXLastMove < this.mXDown) {
                            ++this.targetIndex;
                        } else {
                            --this.targetIndex;
                        }
                    }
                } else if(Math.abs(this.mXLastMove - this.mXDown) > (float)(this.getWidth() * 3 / 5)) {
                    if(this.mXLastMove < this.mXDown) {
                        ++this.targetIndex;
                    } else {
                        --this.targetIndex;
                    }
                }

                if(this.targetIndex >= this.getChildCount()) {
                    this.targetIndex = this.getChildCount() - 1;
                }

                if(this.targetIndex < 0) {
                    this.targetIndex = 0;
                }

                int dx = this.targetIndex * this.getWidth() - this.getScrollX();
                this.mScroller.startScroll(this.getScrollX(), 0, dx, 0, 300);
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
                this.invalidate();
                break;
            case 2:
                this.mXMove = event.getRawX();
                float distanceX = this.mXMove - this.mXDown;
                this.listener.onPageScrolled(this.targetIndex, distanceX, this.getScrollX());
                if(distanceX >= 0.0F && this.mXDown < (float)(screenWidth / 4)) {
                    this.notTouch = true;
                    return false;
                }

                int scrolledX = (int)(this.mXLastMove - this.mXMove);
                if(this.getScrollX() + scrolledX < this.leftBorder) {
                    this.scrollTo(this.leftBorder, 0);
                    return true;
                }

                if(this.getScrollX() + this.getWidth() + scrolledX > this.rightBorder) {
                    this.scrollTo(this.rightBorder - this.getWidth(), 0);
                    return true;
                }

                this.scrollBy(scrolledX, 0);
                this.mXLastMove = this.mXMove;
            }

            return super.onTouchEvent(event);
        }
    }

    public void computeScroll() {
        if(this.mScroller.computeScrollOffset()) {
            this.listener.onPageScrolled(this.targetIndex, 0.0F, this.getScrollX());
            this.scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            this.invalidate();
        } else if(this.isUp) {
            L.i("qgl", "ScrollerLayout onPageSelected 执行了");
            this.listener.onPageSelected(this.targetIndex);
            this.isUp = false;
        }

    }

    public void addOnPageChangeListener(ScrollerLayoutNews.OnPageChangeListener listener) {
        this.listener = listener;
    }

    public void setCurrentItem(int position) {
        L.i("qgl", "setCurrentItem getWidth=" + this.getWidth() + "position=" + position);
        this.scrollTo(this.getWidth() * position, 0);
        this.targetIndex = position;
    }

    public void setNotTouch() {
        this.notTouch = true;
    }

    public interface OnPageChangeListener {
        void onTouchDown(float var1);

        void onPageScrolled(int var1, float var2, int var3);

        void onPageSelected(int var1);

        void onLayoutOver();
    }
}
