package com.example.soul.scrollproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.OverScroller;

/**
 * Created by soul on 16-6-17.
 */
public class HOverScrollView extends LinearLayout {

    private static final int INVALID_ID = -1;
    private int mActinvePOinterId = INVALID_ID;
    private EdgeEffectCompat mEdgeEffectTop;
    private EdgeEffectCompat mEdgeEffectBottom;
    private OverScroller mScroller;
    private int mOverscrollDistance;
    private int mOverflingDistance;
    private int mMaximumFlingVelocity;
    private int mMinimumFlingVelocity;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private float mLastY;

    private boolean mIsBEingDragged = false;
    private int mSecondaryPointerId;
    private float mSecondaryLastY;
    private float mMaxFlingSpeed;

    public HOverScrollView(Context context) {
        this(context, null);
    }

    public HOverScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HOverScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        mOverflingDistance = configuration.getScaledOverflingDistance();
        mOverscrollDistance = configuration.getScaledOverscrollDistance();
        mScroller = new OverScroller(context);
        mEdgeEffectBottom = new EdgeEffectCompat(context);
        mEdgeEffectTop = new EdgeEffectCompat(context);

        mOverflingDistance = 50;

        setOverScrollMode(OVER_SCROLL_ALWAYS);

        setWillNotDraw(false);//必须

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mEdgeEffectTop != null) {
            int scrollY = getScrollY();
            if (!mEdgeEffectTop.isFinished()) {
                int count = canvas.save();
                int width = getWidth() - getPaddingLeft() - getPaddingRight();
                canvas.translate(getPaddingLeft(), Math.min(0, scrollY));
                mEdgeEffectTop.setSize(width, getHeight());
                if (mEdgeEffectTop.draw(canvas)) {
                    postInvalidate();
                }
                canvas.restoreToCount(count);
            }
        }

        if (mEdgeEffectBottom != null) {
            int scrollY = getScrollY();
            if (mEdgeEffectBottom.isFinished()) {
                int count = canvas.save();
                int width = getWidth() - getPaddingLeft() - getPaddingRight();
                canvas.translate(-width + getPaddingLeft(), Math.max(getScrollRange(), scrollY + getHeight()));
                canvas.rotate(180, width, 0);
                mEdgeEffectBottom.setSize(width, getHeight());
                if (mEdgeEffectBottom.draw(canvas)) {
                    postInvalidate();
                }
                canvas.restoreToCount(count);
            }

        }

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int index = MotionEventCompat.getActionIndex(event);
                float y = MotionEventCompat.getY(event, index);
                //添加速度判断
                initVelocityTrackerIfNotExist();
                mVelocityTracker.addMovement(event);

                mLastY = y;

                mActinvePOinterId = MotionEventCompat.getPointerId(event, index);

                //分两种情况，一种是初始动作，一个是界面正在滚动，down触摸停止滚动
                mIsBEingDragged = mScroller.isFinished();
                Log.i("Tag", "按下");
                break;
            case MotionEvent.ACTION_POINTER_DOWN://多点触碰
                index = MotionEventCompat.getActionIndex(event);
                mSecondaryPointerId = MotionEventCompat.getPointerId(event, index);
                mSecondaryLastY = MotionEventCompat.getY(event, index);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("Tag", "移动");
                index = MotionEventCompat.findPointerIndex(event, mActinvePOinterId);
                y = MotionEventCompat.getY(event, index);
                float yDIff = Math.abs(y - mLastY);
                if (yDIff > mTouchSlop) {
                    //是滚动状态啦
                    mIsBEingDragged = true;
                    mLastY = y;
                    initVelocityTrackerIfNotExist();
                    mVelocityTracker.addMovement(event);

                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }

                break;
            case MotionEvent.ACTION_POINTER_UP:
                index = MotionEventCompat.getActionIndex(event);
                int curId = MotionEventCompat.getPointerId(event, index);
                if (curId == mActinvePOinterId) {
                    mActinvePOinterId = mSecondaryPointerId;
                    mLastY = mSecondaryLastY;
                    mVelocityTracker.clear();
                } else {
                    mSecondaryPointerId = INVALID_ID;
                    mSecondaryLastY = 0;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBEingDragged = false;
                mActinvePOinterId = INVALID_ID;
                recycleVelocityTracker();

                break;

        }
        return mIsBEingDragged;

    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
//            mVelocityTracker.recycle();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mScroller == null) {
            return false;
        }
        initVelocityTrackerIfNotExist();
        int action = MotionEventCompat.getActionMasked(event);
        int index = -1;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsBEingDragged = mScroller.isFinished();

                if (mIsBEingDragged) {

                }
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                index = MotionEventCompat.getActionIndex(event);
                mActinvePOinterId = MotionEventCompat.getPointerId(event, index);
                mLastY = MotionEventCompat.getY(event, index);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActinvePOinterId == INVALID_ID) {
                    break;
                }
                index = MotionEventCompat.findPointerIndex(event, mActinvePOinterId);
                if (index == -1) {
                    break;
                }
                float y = MotionEventCompat.getY(event, index);
                float deltaY = mLastY - y;

                if (!mIsBEingDragged && Math.abs(deltaY) > mTouchSlop) {
                    requestPanrentDisallowInterceptTouchEvent();
                    mIsBEingDragged = true;
                    //减少滑动的距离
                    if (deltaY > 0) {
                        deltaY -= mTouchSlop;
                    } else {
                        deltaY += mTouchSlop;
                    }
                }

                if (mIsBEingDragged) {

                    //直接滑动
                    overScrollBy(0, (int) deltaY, 0, getScrollY(), 0, getScrollRange(), 0, mOverscrollDistance, true);

                    int pulledToy = (int) (getScaleY() + deltaY);

                    mLastY = y;
                    if (pulledToy < 0) {
                        mEdgeEffectTop.onPull(deltaY / getHeight(), event.getX(mActinvePOinterId) / getWidth());
                        if (!mEdgeEffectBottom.isFinished()) {
                            mEdgeEffectBottom.onRelease();
                        }
                    } else if (pulledToy > getScrollRange()) {
                        mEdgeEffectBottom.onPull(deltaY / getHeight(), 1.0f - event.getX(mActinvePOinterId) / getWidth());
                        if (!mEdgeEffectTop.isFinished()) {
                            mEdgeEffectTop.onRelease();
                        }
                    }

                    if (mEdgeEffectTop != null && mEdgeEffectBottom != null && (!mEdgeEffectTop.isFinished()
                            || !mEdgeEffectBottom.isFinished())) {
                        postInvalidate();
                    }
                    if (mSecondaryPointerId != INVALID_ID) {
                        index = MotionEventCompat.findPointerIndex(event, mSecondaryPointerId);
                        mSecondaryLastY = MotionEventCompat.getY(event, index);
                    }

                }
                break;
            case MotionEvent.ACTION_CANCEL:
                endDrag();
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBEingDragged) {
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                    int initialVelocity = (int) mVelocityTracker.getYVelocity(mActinvePOinterId);

                    if (Math.abs(initialVelocity) > mMinimumFlingVelocity) {
                        doFling(-initialVelocity);
                    }
                    endDrag();
                }

                break;


        }
        if (mVelocityTracker!=null){
            mVelocityTracker.addMovement(event);
        }
        return true;

    }

    private void doFling(int speed) {
        if (mScroller == null) {
            return;
        }
        mScroller.fling(0, getScrollY(), 0, speed, 0, 0, -500, 10000);
        invalidate();

    }

    private void endDrag() {
        mIsBEingDragged = false;
        recycleVelocityTracker();
        mActinvePOinterId = INVALID_ID;
        mLastY = 0;

    }

    private void requestPanrentDisallowInterceptTouchEvent() {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }

    }

    private void initVelocityTrackerIfNotExist() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }


    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (!mScroller.isFinished()) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            scrollTo(scrollX, scrollY);
            onScrollChanged(scrollX, scrollY, oldX, oldY);
            if (clampedY) {
                mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange());
            }
        } else {
            super.scrollTo(scrollX, scrollY);
        }

    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            int range = getScrollRange();

            if (oldX != x || oldX != y) {
                overScrollBy(x - oldX, y - oldY, oldX, oldY, 0, range, 0, mOverflingDistance, false);
            }

            int overScrollMode = getOverScrollMode();
            boolean canOverScroll = overScrollMode == OVER_SCROLL_ALWAYS || (overScrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS
                    && range > 0);
            if (canOverScroll) {
                if (y < 0 && oldY >= 0) {
                    mEdgeEffectTop.onAbsorb((int) mScroller.getCurrVelocity());
                } else {
                    mEdgeEffectBottom.onAbsorb((int) mScroller.getCurrVelocity());
                }
            }


        }


    }

    private int getScrollRange() {
        int scrollRange = 0;
        if (getChildCount() > 0) {
            int totalHeight = 0;
            if (getChildCount() > 0) {
                for (int i = 0; i < getChildCount(); i++) {
                    totalHeight += getChildAt(i).getHeight();
                }
            }
            Log.i("Range", "Randge:" + scrollRange);
            scrollRange = Math.max(0, totalHeight - getHeight());
        }

        return scrollRange;
    }
}
