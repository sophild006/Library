//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.solid.news.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Build.VERSION;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.solid.news.util.L;

@SuppressLint({"ClickableViewAccessibility"})
public class SuperSwipeRefreshLayout extends ViewGroup {
    private static final String TAG = "CustomeSwipeRefreshLayout";
    private static final int HEADER_VIEW_HEIGHT = 50;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2.0F;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = 0.5F;
    private static final int SCALE_DOWN_DURATION = 150;
    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
    private static final int ANIMATE_TO_START_DURATION = 200;
    private static final int DEFAULT_CIRCLE_TARGET = 64;
    private View mTarget;
    private SuperSwipeRefreshLayout.OnPullRefreshListener mListener;
    private SuperSwipeRefreshLayout.OnPushLoadMoreListener mOnPushLoadMoreListener;
    private boolean mRefreshing;
    private boolean mLoadMore;
    private int mTouchSlop;
    private float mTotalDragDistance;
    private int mMediumAnimationDuration;
    private int mCurrentTargetOffsetTop;
    private boolean mOriginalOffsetCalculated;
    private float mInitialMotionY;
    private boolean mIsBeingDragged;
    private int mActivePointerId;
    private boolean mScale;
    private boolean mReturningToStart;
    private final DecelerateInterpolator mDecelerateInterpolator;
    private static final int[] LAYOUT_ATTRS = new int[]{16842766};
    private SuperSwipeRefreshLayout.HeadViewContainer mHeadViewContainer;
    private RelativeLayout mFooterViewContainer;
    private int mHeaderViewIndex;
    private int mFooterViewIndex;
    protected int mFrom;
    private float mStartingScale;
    protected int mOriginalOffsetTop;
    private Animation mScaleAnimation;
    private Animation mScaleDownAnimation;
    private Animation mScaleDownToStartAnimation;
    private float mSpinnerFinalOffset;
    private boolean mNotify;
    private int mHeaderViewWidth;
    private int mFooterViewWidth;
    private int mHeaderViewHeight;
    private int mFooterViewHeight;
    private boolean mUsingCustomStart;
    private boolean targetScrollWithLayout;
    private int pushDistance;
    private SuperSwipeRefreshLayout.CircleProgressView defaultProgressView;
    private boolean usingDefaultHeader;
    private float density;
    private boolean isProgressEnable;
    private AnimationListener mRefreshListener;
    private final Animation mAnimateToCorrectPosition;
    private final Animation mAnimateToStartPosition;

    private void updateListenerCallBack() {
        int distance = this.mCurrentTargetOffsetTop + this.mHeadViewContainer.getHeight();
        if (this.mListener != null) {
            this.mListener.onPullDistance(distance);
        }

        if (this.usingDefaultHeader && this.isProgressEnable) {
            this.defaultProgressView.setPullDistance(distance);
        }

    }

    public void setHeaderView(View child) {
        if (child != null) {
            if (this.mHeadViewContainer != null) {
                this.usingDefaultHeader = false;
                this.mHeadViewContainer.removeAllViews();
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(this.mHeaderViewWidth, this.mHeaderViewHeight);
                layoutParams.addRule(12);
                this.mHeadViewContainer.addView(child, layoutParams);
            }
        }
    }

    public void setFooterView(View child) {
        if (child != null) {
            if (this.mFooterViewContainer != null) {
                this.mFooterViewContainer.removeAllViews();
                LayoutParams layoutParams = new LayoutParams(this.mFooterViewWidth, this.mFooterViewHeight);
                this.mFooterViewContainer.addView(child, layoutParams);
            }
        }
    }

    public SuperSwipeRefreshLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public SuperSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRefreshing = false;
        this.mLoadMore = false;
        this.mTotalDragDistance = -1.0F;
        this.mOriginalOffsetCalculated = false;
        this.mActivePointerId = -1;
        this.mHeaderViewIndex = -1;
        this.mFooterViewIndex = -1;
        this.targetScrollWithLayout = true;
        this.pushDistance = 0;
        this.defaultProgressView = null;
        this.usingDefaultHeader = true;
        this.density = 1.0F;
        this.isProgressEnable = true;
        this.mRefreshListener = new AnimationListener() {
            public void onAnimationStart(Animation animation) {
                SuperSwipeRefreshLayout.this.isProgressEnable = false;
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                SuperSwipeRefreshLayout.this.isProgressEnable = true;
                if (SuperSwipeRefreshLayout.this.mRefreshing) {
                    if (SuperSwipeRefreshLayout.this.mNotify) {
                        if (SuperSwipeRefreshLayout.this.usingDefaultHeader) {
                            ViewCompat.setAlpha(SuperSwipeRefreshLayout.this.defaultProgressView, 1.0F);
                            SuperSwipeRefreshLayout.this.defaultProgressView.setOnDraw(true);
                            (new Thread(SuperSwipeRefreshLayout.this.defaultProgressView)).start();
                        }

                        if (SuperSwipeRefreshLayout.this.mListener != null) {
                            SuperSwipeRefreshLayout.this.mListener.onRefresh();
                        }
                    }
                } else {
                    SuperSwipeRefreshLayout.this.mHeadViewContainer.setVisibility(View.GONE);
                    if (SuperSwipeRefreshLayout.this.mScale) {
                        SuperSwipeRefreshLayout.this.setAnimationProgress(0.0F);
                    } else {
                        SuperSwipeRefreshLayout.this.setTargetOffsetTopAndBottom(SuperSwipeRefreshLayout.this.mOriginalOffsetTop - SuperSwipeRefreshLayout.this.mCurrentTargetOffsetTop, true);
                    }
                }

                SuperSwipeRefreshLayout.this.mCurrentTargetOffsetTop = SuperSwipeRefreshLayout.this.mHeadViewContainer.getTop();
                SuperSwipeRefreshLayout.this.updateListenerCallBack();
            }
        };
        this.mAnimateToCorrectPosition = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                boolean targetTop = false;
                boolean endTarget = false;
                int endTarget1;
                if (!SuperSwipeRefreshLayout.this.mUsingCustomStart) {
                    endTarget1 = (int) (SuperSwipeRefreshLayout.this.mSpinnerFinalOffset - (float) Math.abs(SuperSwipeRefreshLayout.this.mOriginalOffsetTop));
                } else {
                    endTarget1 = (int) SuperSwipeRefreshLayout.this.mSpinnerFinalOffset;
                }

                int targetTop1 = SuperSwipeRefreshLayout.this.mFrom + (int) ((float) (endTarget1 - SuperSwipeRefreshLayout.this.mFrom) * interpolatedTime);
                int offset = targetTop1 - SuperSwipeRefreshLayout.this.mHeadViewContainer.getTop();
                SuperSwipeRefreshLayout.this.setTargetOffsetTopAndBottom(offset, false);
            }

            public void setAnimationListener(AnimationListener listener) {
                super.setAnimationListener(listener);
            }
        };
        this.mAnimateToStartPosition = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                SuperSwipeRefreshLayout.this.moveToStart(interpolatedTime);
            }
        };
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mMediumAnimationDuration = this.getResources().getInteger(17694721);
        this.setWillNotDraw(false);
        this.mDecelerateInterpolator = new DecelerateInterpolator(2.0F);
        TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        this.setEnabled(a.getBoolean(0, true));
        a.recycle();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        this.mHeaderViewWidth = display.getWidth();
        this.mFooterViewWidth = display.getWidth();
        this.mHeaderViewHeight = (int) (50.0F * metrics.density);
        this.mFooterViewHeight = (int) (50.0F * metrics.density);
        this.defaultProgressView = new SuperSwipeRefreshLayout.CircleProgressView(this.getContext());
        this.createHeaderViewContainer();
        this.createFooterViewContainer();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        this.mSpinnerFinalOffset = 64.0F * metrics.density;
        this.density = metrics.density;
        this.mTotalDragDistance = this.mSpinnerFinalOffset;
    }

    protected int getChildDrawingOrder(int childCount, int i) {
        if (this.mHeaderViewIndex < 0 && this.mFooterViewIndex < 0) {
            return i;
        } else if (i == childCount - 2) {
            return this.mHeaderViewIndex;
        } else if (i == childCount - 1) {
            return this.mFooterViewIndex;
        } else {
            int bigIndex = this.mFooterViewIndex > this.mHeaderViewIndex ? this.mFooterViewIndex : this.mHeaderViewIndex;
            int smallIndex = this.mFooterViewIndex < this.mHeaderViewIndex ? this.mFooterViewIndex : this.mHeaderViewIndex;
            return i >= smallIndex && i < bigIndex - 1 ? i + 1 : (i < bigIndex && i != bigIndex - 1 ? i : i + 2);
        }
    }

    private void createHeaderViewContainer() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) ((double) this.mHeaderViewHeight * 0.8D), (int) ((double) this.mHeaderViewHeight * 0.8D));
        layoutParams.addRule(14);
        layoutParams.addRule(12);
        this.mHeadViewContainer = new SuperSwipeRefreshLayout.HeadViewContainer(this.getContext());
        this.mHeadViewContainer.setVisibility(View.GONE);
        this.defaultProgressView.setVisibility(View.VISIBLE);
        this.defaultProgressView.setOnDraw(false);
        this.mHeadViewContainer.addView(this.defaultProgressView, layoutParams);
        this.addView(this.mHeadViewContainer);
    }

    private void createFooterViewContainer() {
        this.mFooterViewContainer = new RelativeLayout(this.getContext());
        this.mFooterViewContainer.setVisibility(View.GONE);
        this.addView(this.mFooterViewContainer);
    }

    public void setOnPullRefreshListener(SuperSwipeRefreshLayout.OnPullRefreshListener listener) {
        this.mListener = listener;
    }

    public void setHeaderViewBackgroundColor(int color) {
        this.mHeadViewContainer.setBackgroundColor(color);
    }

    public void setOnPushLoadMoreListener(SuperSwipeRefreshLayout.OnPushLoadMoreListener onPushLoadMoreListener) {
        this.mOnPushLoadMoreListener = onPushLoadMoreListener;
    }

    public void setRefreshing(boolean refreshing) {
        if (refreshing && this.mRefreshing != refreshing) {
            this.mRefreshing = refreshing;
            boolean endTarget = false;
            int endTarget1;
            if (!this.mUsingCustomStart) {
                endTarget1 = (int) (this.mSpinnerFinalOffset + (float) this.mOriginalOffsetTop);
            } else {
                endTarget1 = (int) this.mSpinnerFinalOffset;
            }

            this.setTargetOffsetTopAndBottom(endTarget1 - this.mCurrentTargetOffsetTop, true);
            this.mNotify = false;
            this.startScaleUpAnimation(this.mRefreshListener);
        } else {
            this.setRefreshing(refreshing, false);
            if (this.usingDefaultHeader) {
                this.defaultProgressView.setOnDraw(false);
            }
        }

    }

    private void startScaleUpAnimation(AnimationListener listener) {
        this.mHeadViewContainer.setVisibility(View.VISIBLE);
        this.mScaleAnimation = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                SuperSwipeRefreshLayout.this.setAnimationProgress(interpolatedTime);
            }
        };
        this.mScaleAnimation.setDuration((long) this.mMediumAnimationDuration);
        if (listener != null) {
            this.mHeadViewContainer.setAnimationListener(listener);
        }

        this.mHeadViewContainer.clearAnimation();
        this.mHeadViewContainer.startAnimation(this.mScaleAnimation);
    }

    private void setAnimationProgress(float progress) {
        if (!this.usingDefaultHeader) {
            progress = 1.0F;
        }

        ViewCompat.setScaleX(this.mHeadViewContainer, progress);
        ViewCompat.setScaleY(this.mHeadViewContainer, progress);
    }

    private void setRefreshing(boolean refreshing, boolean notify) {
        if (this.mRefreshing != refreshing) {
            this.mNotify = notify;
            this.ensureTarget();
            this.mRefreshing = refreshing;
            if (this.mRefreshing) {
                this.animateOffsetToCorrectPosition(this.mCurrentTargetOffsetTop, this.mRefreshListener);
            } else {
                this.animateOffsetToStartPosition(this.mCurrentTargetOffsetTop, this.mRefreshListener);
            }
        }

    }

    private void startScaleDownAnimation(AnimationListener listener) {
        this.mScaleDownAnimation = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                SuperSwipeRefreshLayout.this.setAnimationProgress(1.0F - interpolatedTime);
            }
        };
        this.mScaleDownAnimation.setDuration(150L);
        this.mHeadViewContainer.setAnimationListener(listener);
        this.mHeadViewContainer.clearAnimation();
        this.mHeadViewContainer.startAnimation(this.mScaleDownAnimation);
    }

    public boolean isRefreshing() {
        return this.mRefreshing;
    }

    private void ensureTarget() {
        if (this.mTarget == null) {
            for (int i = 0; i < this.getChildCount(); ++i) {
                View child = this.getChildAt(i);
                if (!child.equals(this.mHeadViewContainer) && !child.equals(this.mFooterViewContainer)) {
                    this.mTarget = child;
                    break;
                }
            }
        }

    }

    public void setDistanceToTriggerSync(int distance) {
        this.mTotalDragDistance = (float) distance;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = this.getMeasuredWidth();
        int height = this.getMeasuredHeight();
        if (this.getChildCount() != 0) {
            if (this.mTarget == null) {
                this.ensureTarget();
            }

            if (this.mTarget != null) {
                int distance = this.mCurrentTargetOffsetTop + this.mHeadViewContainer.getMeasuredHeight();
                if (!this.targetScrollWithLayout) {
                    distance = 0;
                }

                View child = this.mTarget;
                int childLeft = this.getPaddingLeft();
                int childTop = this.getPaddingTop() + distance - this.pushDistance;
                int childWidth = width - this.getPaddingLeft() - this.getPaddingRight();
                int childHeight = height - this.getPaddingTop() - this.getPaddingBottom();
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
                int headViewWidth = this.mHeadViewContainer.getMeasuredWidth();
                int headViewHeight = this.mHeadViewContainer.getMeasuredHeight();
                this.mHeadViewContainer.layout(width / 2 - headViewWidth / 2, this.mCurrentTargetOffsetTop, width / 2 + headViewWidth / 2, this.mCurrentTargetOffsetTop + headViewHeight);
                int footViewWidth = this.mFooterViewContainer.getMeasuredWidth();
                int footViewHeight = this.mFooterViewContainer.getMeasuredHeight();
                this.mFooterViewContainer.layout(width / 2 - footViewWidth / 2, height - this.pushDistance, width / 2 + footViewWidth / 2, height + footViewHeight - this.pushDistance);
            }
        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mTarget == null) {
            this.ensureTarget();
        }

        if (this.mTarget != null) {
            this.mTarget.measure(MeasureSpec.makeMeasureSpec(this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom(), 1073741824));
            this.mHeadViewContainer.measure(MeasureSpec.makeMeasureSpec(this.mHeaderViewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(3 * this.mHeaderViewHeight, MeasureSpec.EXACTLY));
            this.mFooterViewContainer.measure(MeasureSpec.makeMeasureSpec(this.mFooterViewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(this.mFooterViewHeight, MeasureSpec.EXACTLY));
            if (!this.mUsingCustomStart && !this.mOriginalOffsetCalculated) {
                this.mOriginalOffsetCalculated = true;
                this.mCurrentTargetOffsetTop = this.mOriginalOffsetTop = -this.mHeadViewContainer.getMeasuredHeight();
                this.updateListenerCallBack();
            }

            this.mHeaderViewIndex = -1;

            int index;
            for (index = 0; index < this.getChildCount(); ++index) {
                if (this.getChildAt(index) == this.mHeadViewContainer) {
                    this.mHeaderViewIndex = index;
                    break;
                }
            }

            this.mFooterViewIndex = -1;

            for (index = 0; index < this.getChildCount(); ++index) {
                if (this.getChildAt(index) == this.mFooterViewContainer) {
                    this.mFooterViewIndex = index;
                    break;
                }
            }

        }
    }

    public boolean isChildScrollToTop() {
        if (VERSION.SDK_INT < 14) {
            if (!(this.mTarget instanceof AbsListView)) {
                return this.mTarget.getScrollY() <= 0;
            } else {
                AbsListView absListView = (AbsListView) this.mTarget;
                return absListView.getChildCount() <= 0 || absListView.getFirstVisiblePosition() <= 0 && absListView.getChildAt(0).getTop() >= absListView.getPaddingTop();
            }
        } else {
            return !ViewCompat.canScrollVertically(this.mTarget, -1);
        }
    }

    public boolean isChildScrollToBottom() {
        if (this.isChildScrollToTop()) {
            return false;
        } else {
            int diff;
            if (!(this.mTarget instanceof RecyclerView)) {
                if (this.mTarget instanceof AbsListView) {
                    AbsListView nestedScrollView3 = (AbsListView) this.mTarget;
                    int view2 = ((ListAdapter) nestedScrollView3.getAdapter()).getCount();
                    diff = nestedScrollView3.getFirstVisiblePosition();
                    if (diff == 0 && nestedScrollView3.getChildAt(0).getTop() >= nestedScrollView3.getPaddingTop()) {
                        return false;
                    } else {
                        int lastPos2 = nestedScrollView3.getLastVisiblePosition();
                        return lastPos2 > 0 && view2 > 0 && lastPos2 == view2 - 1;
                    }
                } else {
                    View view1;
                    if (this.mTarget instanceof ScrollView) {
                        ScrollView nestedScrollView1 = (ScrollView) this.mTarget;
                        view1 = nestedScrollView1.getChildAt(nestedScrollView1.getChildCount() - 1);
                        if (view1 != null) {
                            diff = view1.getBottom() - (nestedScrollView1.getHeight() + nestedScrollView1.getScrollY());
                            if (diff == 0) {
                                return true;
                            }
                        }
                    } else if (this.mTarget instanceof NestedScrollView) {
                        NestedScrollView nestedScrollView2 = (NestedScrollView) this.mTarget;
                        view1 = nestedScrollView2.getChildAt(nestedScrollView2.getChildCount() - 1);
                        if (view1 != null) {
                            diff = view1.getBottom() - (nestedScrollView2.getHeight() + nestedScrollView2.getScrollY());
                            if (diff == 0) {
                                return true;
                            }
                        }
                    }

                    return false;
                }
            } else {
                RecyclerView nestedScrollView = (RecyclerView) this.mTarget;
                LayoutManager view = nestedScrollView.getLayoutManager();
                diff = nestedScrollView.getAdapter().getItemCount();
                if (view instanceof LinearLayoutManager && diff > 0) {
                    LinearLayoutManager lastPos1 = (LinearLayoutManager) view;
                    if (lastPos1.findLastCompletelyVisibleItemPosition() == diff - 1) {
                        return true;
                    }
                } else if (view instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager lastPos = (StaggeredGridLayoutManager) view;
                    int[] lastItems = new int[2];
                    lastPos.findLastCompletelyVisibleItemPositions(lastItems);
                    int lastItem = Math.max(lastItems[0], lastItems[1]);
                    if (lastItem == diff - 1) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        this.ensureTarget();
        int action = MotionEventCompat.getActionMasked(ev);
        if (this.mReturningToStart && action == 0) {
            this.mReturningToStart = false;
        }

        if (this.isEnabled() && !this.mReturningToStart && !this.mRefreshing && !this.mLoadMore && (this.isChildScrollToTop() || this.isChildScrollToBottom())) {
            switch (action) {
                case 0:
                    this.setTargetOffsetTopAndBottom(this.mOriginalOffsetTop - this.mHeadViewContainer.getTop(), true);
                    this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                    this.mIsBeingDragged = false;
                    float initialMotionY = this.getMotionEventY(ev, this.mActivePointerId);
                    if (initialMotionY == -1.0F) {
                        return false;
                    }

                    this.mInitialMotionY = initialMotionY;
                case 2:
                    if (this.mActivePointerId == -1) {
                        L.i("CustomeSwipeRefreshLayout", "Got ACTION_MOVE event but don\'t have an active pointer id.");
                        return false;
                    }

                    float y = this.getMotionEventY(ev, this.mActivePointerId);
                    if (y == -1.0F) {
                        return false;
                    }

                    float yDiff = 0.0F;
                    if (this.isChildScrollToBottom()) {
                        yDiff = this.mInitialMotionY - y;
                        if (yDiff > (float) this.mTouchSlop && !this.mIsBeingDragged) {
                            this.mIsBeingDragged = true;
                        }
                    } else {
                        yDiff = y - this.mInitialMotionY;
                        if (yDiff > (float) this.mTouchSlop && !this.mIsBeingDragged) {
                            this.mIsBeingDragged = true;
                        }
                    }
                    break;
                case 1:
                case 3:
                    this.mIsBeingDragged = false;
                    this.mActivePointerId = -1;
                case 4:
                case 5:
                default:
                    break;
                case 6:
                    this.onSecondaryPointerUp(ev);
            }

            return this.mIsBeingDragged;
        } else {
            return false;
        }
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        return index < 0 ? -1.0F : MotionEventCompat.getY(ev, index);
    }

    public void requestDisallowInterceptTouchEvent(boolean b) {
    }

    public boolean onTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (this.mReturningToStart && action == 0) {
            this.mReturningToStart = false;
        }

        return !this.isEnabled() || this.mReturningToStart || !this.isChildScrollToTop() && !this.isChildScrollToBottom() ? false : (this.isChildScrollToBottom() ? this.handlerPushTouchEvent(ev, action) : this.handlerPullTouchEvent(ev, action));
    }

    private boolean handlerPullTouchEvent(MotionEvent ev, int action) {
        int pointerIndex;
        float y;
        float overscrollTop;
        switch (action) {
            case 0:
                this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                this.mIsBeingDragged = false;
                break;
            case 1:
            case 3:
                if (this.mActivePointerId == -1) {
                    if (action == 1) {
                        L.i("CustomeSwipeRefreshLayout", "Got ACTION_UP event but don\'t have an active pointer id.");
                    }

                    return false;
                }

                pointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                y = MotionEventCompat.getY(ev, pointerIndex);
                overscrollTop = (y - this.mInitialMotionY) * 0.5F;
                this.mIsBeingDragged = false;
                if (overscrollTop > this.mTotalDragDistance) {
                    this.setRefreshing(true, true);
                } else {
                    this.mRefreshing = false;
                    AnimationListener listener1 = null;
                    if (!this.mScale) {
                        listener1 = new AnimationListener() {
                            public void onAnimationStart(Animation animation) {
                            }

                            public void onAnimationEnd(Animation animation) {
                                if (!SuperSwipeRefreshLayout.this.mScale) {
                                    SuperSwipeRefreshLayout.this.startScaleDownAnimation((AnimationListener) null);
                                }

                            }

                            public void onAnimationRepeat(Animation animation) {
                            }
                        };
                    }

                    this.animateOffsetToStartPosition(this.mCurrentTargetOffsetTop, listener1);
                }

                this.mActivePointerId = -1;
                return false;
            case 2:
                pointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                if (pointerIndex < 0) {
                    L.i("CustomeSwipeRefreshLayout", "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                y = MotionEventCompat.getY(ev, pointerIndex);
                overscrollTop = (y - this.mInitialMotionY) * 0.5F;
                if (this.mIsBeingDragged) {
                    float listener = overscrollTop / this.mTotalDragDistance;
                    if (listener < 0.0F) {
                        return false;
                    }

                    float dragPercent = Math.min(1.0F, Math.abs(listener));
                    float extraOS = Math.abs(overscrollTop) - this.mTotalDragDistance;
                    float slingshotDist = this.mUsingCustomStart ? this.mSpinnerFinalOffset - (float) this.mOriginalOffsetTop : this.mSpinnerFinalOffset;
                    float tensionSlingshotPercent = Math.max(0.0F, Math.min(extraOS, slingshotDist * 2.0F) / slingshotDist);
                    float tensionPercent = (float) ((double) (tensionSlingshotPercent / 4.0F) - Math.pow((double) (tensionSlingshotPercent / 4.0F), 2.0D)) * 2.0F;
                    float extraMove = slingshotDist * tensionPercent * 2.0F;
                    int targetY = this.mOriginalOffsetTop + (int) (slingshotDist * dragPercent + extraMove);
                    if (this.mHeadViewContainer.getVisibility() != View.VISIBLE) {
                        this.mHeadViewContainer.setVisibility(View.VISIBLE);
                    }

                    if (!this.mScale) {
                        ViewCompat.setScaleX(this.mHeadViewContainer, 1.0F);
                        ViewCompat.setScaleY(this.mHeadViewContainer, 1.0F);
                    }

                    if (this.usingDefaultHeader) {
                        float alpha = overscrollTop / this.mTotalDragDistance;
                        if (alpha >= 1.0F) {
                            alpha = 1.0F;
                        }

                        ViewCompat.setScaleX(this.defaultProgressView, alpha);
                        ViewCompat.setScaleY(this.defaultProgressView, alpha);
                        ViewCompat.setAlpha(this.defaultProgressView, alpha);
                    }

                    if (overscrollTop < this.mTotalDragDistance) {
                        if (this.mScale) {
                            this.setAnimationProgress(overscrollTop / this.mTotalDragDistance);
                        }

                        if (this.mListener != null) {
                            this.mListener.onPullEnable(false);
                        }
                    } else if (this.mListener != null) {
                        this.mListener.onPullEnable(true);
                    }

                    this.setTargetOffsetTopAndBottom(targetY - this.mCurrentTargetOffsetTop, true);
                }
            case 4:
            default:
                break;
            case 5:
                pointerIndex = MotionEventCompat.getActionIndex(ev);
                this.mActivePointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                break;
            case 6:
                this.onSecondaryPointerUp(ev);
        }

        return true;
    }

    private boolean handlerPushTouchEvent(MotionEvent ev, int action) {
        int pointerIndex;
        float y;
        float overscrollBottom;
        switch (action) {
            case 0:
                this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                this.mIsBeingDragged = false;
                L.i("CustomeSwipeRefreshLayout", "debug:onTouchEvent ACTION_DOWN");
                break;
            case 1:
            case 3:
                if (this.mActivePointerId == -1) {
                    if (action == 1) {
                        L.i("CustomeSwipeRefreshLayout", "Got ACTION_UP event but don\'t have an active pointer id.");
                    }

                    return false;
                }

                pointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                y = MotionEventCompat.getY(ev, pointerIndex);
                overscrollBottom = (this.mInitialMotionY - y) * 0.5F;
                this.mIsBeingDragged = false;
                this.mActivePointerId = -1;
                if (overscrollBottom >= (float) this.mFooterViewHeight && this.mOnPushLoadMoreListener != null) {
                    this.pushDistance = this.mFooterViewHeight;
                } else {
                    this.pushDistance = 0;
                }

                if (VERSION.SDK_INT < 11) {
                    this.updateFooterViewPosition();
                    if (this.pushDistance == this.mFooterViewHeight && this.mOnPushLoadMoreListener != null) {
                        this.mLoadMore = true;
                        this.mOnPushLoadMoreListener.onLoadMore();
                    }
                } else {
                    this.animatorFooterToBottom((int) overscrollBottom, this.pushDistance);
                }

                return false;
            case 2:
                pointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                if (pointerIndex < 0) {
                    L.i("CustomeSwipeRefreshLayout", "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                y = MotionEventCompat.getY(ev, pointerIndex);
                overscrollBottom = (this.mInitialMotionY - y) * 0.5F;
                if (this.mIsBeingDragged) {
                    this.pushDistance = (int) overscrollBottom;
                    this.updateFooterViewPosition();
                    if (this.mOnPushLoadMoreListener != null) {
                        this.mOnPushLoadMoreListener.onPushEnable(this.pushDistance >= this.mFooterViewHeight);
                    }
                }
            case 4:
            default:
                break;
            case 5:
                pointerIndex = MotionEventCompat.getActionIndex(ev);
                this.mActivePointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                break;
            case 6:
                this.onSecondaryPointerUp(ev);
        }

        return true;
    }

    @TargetApi(11)
    private void animatorFooterToBottom(int start, final int end) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(new int[]{start, end});
        valueAnimator.setDuration(150L);
        valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                SuperSwipeRefreshLayout.this.pushDistance = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                SuperSwipeRefreshLayout.this.updateFooterViewPosition();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (end > 0 && SuperSwipeRefreshLayout.this.mOnPushLoadMoreListener != null) {
                    SuperSwipeRefreshLayout.this.mLoadMore = true;
                    SuperSwipeRefreshLayout.this.mOnPushLoadMoreListener.onLoadMore();
                } else {
                    SuperSwipeRefreshLayout.this.resetTargetLayout();
                    SuperSwipeRefreshLayout.this.mLoadMore = false;
                }

            }
        });
        valueAnimator.setInterpolator(this.mDecelerateInterpolator);
        valueAnimator.start();
    }

    public void setLoadMore(boolean loadMore) {
        if (!loadMore && this.mLoadMore) {
            if (VERSION.SDK_INT < 11) {
                this.mLoadMore = false;
                this.pushDistance = 0;
                this.updateFooterViewPosition();
            } else {
                this.animatorFooterToBottom(this.mFooterViewHeight, 0);
            }
        }

    }

    private void animateOffsetToCorrectPosition(int from, AnimationListener listener) {
        this.mFrom = from;
        this.mAnimateToCorrectPosition.reset();
        this.mAnimateToCorrectPosition.setDuration(200L);
        this.mAnimateToCorrectPosition.setInterpolator(this.mDecelerateInterpolator);
        if (listener != null) {
            this.mHeadViewContainer.setAnimationListener(listener);
        }

        this.mHeadViewContainer.clearAnimation();
        this.mHeadViewContainer.startAnimation(this.mAnimateToCorrectPosition);
    }

    private void animateOffsetToStartPosition(int from, AnimationListener listener) {
        if (this.mScale) {
            this.startScaleDownReturnToStartAnimation(from, listener);
        } else {
            this.mFrom = from;
            this.mAnimateToStartPosition.reset();
            this.mAnimateToStartPosition.setDuration(200L);
            this.mAnimateToStartPosition.setInterpolator(this.mDecelerateInterpolator);
            if (listener != null) {
                this.mHeadViewContainer.setAnimationListener(listener);
            }

            this.mHeadViewContainer.clearAnimation();
            this.mHeadViewContainer.startAnimation(this.mAnimateToStartPosition);
        }

        this.resetTargetLayoutDelay(200);
    }

    public void resetTargetLayoutDelay(int delay) {
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                SuperSwipeRefreshLayout.this.resetTargetLayout();
            }
        }, (long) delay);
    }

    public void resetTargetLayout() {
        int width = this.getMeasuredWidth();
        int height = this.getMeasuredHeight();
        View child = this.mTarget;
        int childLeft = this.getPaddingLeft();
        int childTop = this.getPaddingTop();
        int childWidth = child.getWidth() - this.getPaddingLeft() - this.getPaddingRight();
        int childHeight = child.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        int headViewWidth = this.mHeadViewContainer.getMeasuredWidth();
        int headViewHeight = this.mHeadViewContainer.getMeasuredHeight();
        this.mHeadViewContainer.layout(width / 2 - headViewWidth / 2, -headViewHeight, width / 2 + headViewWidth / 2, 0);
        int footViewWidth = this.mFooterViewContainer.getMeasuredWidth();
        int footViewHeight = this.mFooterViewContainer.getMeasuredHeight();
        this.mFooterViewContainer.layout(width / 2 - footViewWidth / 2, height, width / 2 + footViewWidth / 2, height + footViewHeight);
    }

    private void moveToStart(float interpolatedTime) {
        boolean targetTop = false;
        int targetTop1 = this.mFrom + (int) ((float) (this.mOriginalOffsetTop - this.mFrom) * interpolatedTime);
        int offset = targetTop1 - this.mHeadViewContainer.getTop();
        this.setTargetOffsetTopAndBottom(offset, false);
    }

    private void startScaleDownReturnToStartAnimation(int from, AnimationListener listener) {
        this.mFrom = from;
        this.mStartingScale = ViewCompat.getScaleX(this.mHeadViewContainer);
        this.mScaleDownToStartAnimation = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                float targetScale = SuperSwipeRefreshLayout.this.mStartingScale + -SuperSwipeRefreshLayout.this.mStartingScale * interpolatedTime;
                SuperSwipeRefreshLayout.this.setAnimationProgress(targetScale);
                SuperSwipeRefreshLayout.this.moveToStart(interpolatedTime);
            }
        };
        this.mScaleDownToStartAnimation.setDuration(150L);
        if (listener != null) {
            this.mHeadViewContainer.setAnimationListener(listener);
        }

        this.mHeadViewContainer.clearAnimation();
        this.mHeadViewContainer.startAnimation(this.mScaleDownToStartAnimation);
    }

    private void setTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
        this.mHeadViewContainer.bringToFront();
        this.mHeadViewContainer.offsetTopAndBottom(offset);
        this.mCurrentTargetOffsetTop = this.mHeadViewContainer.getTop();
        if (requiresUpdate && VERSION.SDK_INT < 11) {
            this.invalidate();
        }

        this.updateListenerCallBack();
    }

    private void updateFooterViewPosition() {
        this.mFooterViewContainer.setVisibility(View.VISIBLE);
        this.mFooterViewContainer.bringToFront();
        if (VERSION.SDK_INT < 19) {
            this.mFooterViewContainer.getParent().requestLayout();
        }

        this.mFooterViewContainer.offsetTopAndBottom(-this.pushDistance);
        this.updatePushDistanceListener();
    }

    private void updatePushDistanceListener() {
        if (this.mOnPushLoadMoreListener != null) {
            this.mOnPushLoadMoreListener.onPushDistance(this.pushDistance);
        }

    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = MotionEventCompat.getActionIndex(ev);
        int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            this.mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }

    }

    public boolean isTargetScrollWithLayout() {
        return this.targetScrollWithLayout;
    }

    public void setTargetScrollWithLayout(boolean targetScrollWithLayout) {
        this.targetScrollWithLayout = targetScrollWithLayout;
    }

    public void setDefaultCircleProgressColor(int color) {
        if (this.usingDefaultHeader) {
            this.defaultProgressView.setProgressColor(color);
        }

    }

    public void setDefaultCircleBackgroundColor(int color) {
        if (this.usingDefaultHeader) {
            this.defaultProgressView.setCircleBackgroundColor(color);
        }

    }

    public void setDefaultCircleShadowColor(int color) {
        if (this.usingDefaultHeader) {
            this.defaultProgressView.setShadowColor(color);
        }

    }

    public class CircleProgressView extends View implements Runnable {
        private static final int PEROID = 16;
        private Paint progressPaint;
        private Paint bgPaint;
        private int width;
        private int height;
        private boolean isOnDraw = false;
        private boolean isRunning = false;
        private int startAngle = 0;
        private int speed = 8;
        private RectF ovalRect = null;
        private RectF bgRect = null;
        private int swipeAngle;
        private int progressColor = -3355444;
        private int circleBackgroundColor = -1;
        private int shadowColor = -6710887;

        public CircleProgressView(Context context) {
            super(context);
        }

        public CircleProgressView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawArc(this.getBgRect(), 0.0F, 360.0F, false, this.createBgPaint());
            int index = this.startAngle / 360;
            if (index % 2 == 0) {
                this.swipeAngle = this.startAngle % 720 / 2;
            } else {
                this.swipeAngle = 360 - this.startAngle % 720 / 2;
            }

            canvas.drawArc(this.getOvalRect(), (float) this.startAngle, (float) this.swipeAngle, false, this.createPaint());
        }

        private RectF getBgRect() {
            this.width = this.getWidth();
            this.height = this.getHeight();
            if (this.bgRect == null) {
                int offset = (int) (SuperSwipeRefreshLayout.this.density * 2.0F);
                this.bgRect = new RectF((float) offset, (float) offset, (float) (this.width - offset), (float) (this.height - offset));
            }

            return this.bgRect;
        }

        private RectF getOvalRect() {
            this.width = this.getWidth();
            this.height = this.getHeight();
            if (this.ovalRect == null) {
                int offset = (int) (SuperSwipeRefreshLayout.this.density * 8.0F);
                this.ovalRect = new RectF((float) offset, (float) offset, (float) (this.width - offset), (float) (this.height - offset));
            }

            return this.ovalRect;
        }

        public void setProgressColor(int progressColor) {
            this.progressColor = progressColor;
        }

        public void setCircleBackgroundColor(int circleBackgroundColor) {
            this.circleBackgroundColor = circleBackgroundColor;
        }

        public void setShadowColor(int shadowColor) {
            this.shadowColor = shadowColor;
        }

        private Paint createPaint() {
            if (this.progressPaint == null) {
                this.progressPaint = new Paint();
                this.progressPaint.setStrokeWidth((float) ((int) (SuperSwipeRefreshLayout.this.density * 3.0F)));
                this.progressPaint.setStyle(Style.STROKE);
                this.progressPaint.setAntiAlias(true);
            }

            this.progressPaint.setColor(this.progressColor);
            return this.progressPaint;
        }

        private Paint createBgPaint() {
            if (this.bgPaint == null) {
                this.bgPaint = new Paint();
                this.bgPaint.setColor(this.circleBackgroundColor);
                this.bgPaint.setStyle(Style.FILL);
                this.bgPaint.setAntiAlias(true);
                if (VERSION.SDK_INT >= 11) {
                    this.setLayerType(1, this.bgPaint);
                }

                this.bgPaint.setShadowLayer(4.0F, 0.0F, 2.0F, this.shadowColor);
            }

            return this.bgPaint;
        }

        public void setPullDistance(int distance) {
            this.startAngle = distance * 2;
            this.postInvalidate();
        }

        public void run() {
            while (this.isOnDraw) {
                this.isRunning = true;
                long startTime = System.currentTimeMillis();
                this.startAngle += this.speed;
                this.postInvalidate();
                long time = System.currentTimeMillis() - startTime;
                if (time < 16L) {
                    try {
                        Thread.sleep(16L - time);
                    } catch (InterruptedException var6) {
                        var6.printStackTrace();
                    }
                }
            }

        }

        public void setOnDraw(boolean isOnDraw) {
            this.isOnDraw = isOnDraw;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        public boolean isRunning() {
            return this.isRunning;
        }

        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
        }

        protected void onDetachedFromWindow() {
            this.isOnDraw = false;
            super.onDetachedFromWindow();
        }
    }

    public class OnPushLoadMoreListenerAdapter implements SuperSwipeRefreshLayout.OnPushLoadMoreListener {
        public OnPushLoadMoreListenerAdapter() {
        }

        public void onLoadMore() {
        }

        public void onPushDistance(int distance) {
        }

        public void onPushEnable(boolean enable) {
        }
    }

    public class OnPullRefreshListenerAdapter implements SuperSwipeRefreshLayout.OnPullRefreshListener {
        public OnPullRefreshListenerAdapter() {
        }

        public void onRefresh() {
        }

        public void onPullDistance(int distance) {
        }

        public void onPullEnable(boolean enable) {
        }
    }

    public interface OnPushLoadMoreListener {
        void onLoadMore();

        void onPushDistance(int var1);

        void onPushEnable(boolean var1);
    }

    public interface OnPullRefreshListener {
        void onRefresh();

        void onPullDistance(int var1);

        void onPullEnable(boolean var1);
    }

    private class HeadViewContainer extends RelativeLayout {
        private AnimationListener mListener;

        public HeadViewContainer(Context context) {
            super(context);
        }

        public void setAnimationListener(AnimationListener listener) {
            this.mListener = listener;
        }

        public void onAnimationStart() {
            super.onAnimationStart();
            if (this.mListener != null) {
                this.mListener.onAnimationStart(this.getAnimation());
            }

        }

        public void onAnimationEnd() {
            super.onAnimationEnd();
            if (this.mListener != null) {
                this.mListener.onAnimationEnd(this.getAnimation());
            }

        }
    }
}
