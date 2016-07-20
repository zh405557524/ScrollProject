package com.example.soul.scrollproject.wight;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import java.util.IllegalFormatException;

/**
 * Created by soul on 16-7-20.
 */
public class PullDownView extends FrameLayout {

    private final Scroller mScroller;
    private View mContenView;
    private float downY;
    private int expansionHeight;
    private float moveOffset;
    private int duration = 500;
    private VelocityTracker mVelocityTracker;

    public PullDownView(Context context) {
        this(context, null);
    }

    public PullDownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(getContext());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount < 1) {
            throw new IllegalStateException("必须有一个子类view");
        }
        mContenView = getChildAt(0);
        mContenView.scrollTo(0, getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下
                downY = event.getY();
                expansionHeight = mScroller.getCurrY();
                initVelocityTracker();

                trackMovement(event);

                mScroller.abortAnimation();

                break;
            case MotionEvent.ACTION_MOVE:
                //移动
                trackMovement(event);
                moveOffset = event.getY() - downY;

                float heigt = expansionHeight + moveOffset;

                int i = (int) (getHeight() - heigt);
                Log.d("BD-TAG", "Height:" + i);
                if (heigt < getHeight()) {
                    mContenView.scrollTo(0, i);
                } else {
                    mContenView.scrollTo(0, 0);
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //松开

                moveOffset = (int) event.getY() - downY;
                expansionHeight = (int) (expansionHeight + moveOffset);
                // LogUtil.getInstance().d("getHeight:" + getHeight());
                Log.d("BD-TAG", "ACTION_UP:y" + event.getY());

                Log.d("BD-TAG", "getHeight:" + getHeight());
                trackMovement(event);

                // mVelocityTracker.computeCurrentVelocity(1000);
                // // LogUtil.getInstance().d("getCurrentVelocity:" + mVelocityTracker.getYVelocity());
                // Log.d("BD-TAG","getCurrentVelocity:" + mVelocityTracker.getYVelocity());

                if (getCurrentVelocity() > 100) {
                    if (expansionHeight < getHeight() || expansionHeight == getHeight()) {

                        mScroller.startScroll(0, (int) expansionHeight, 0, ((getHeight() - (int) (expansionHeight + 0.5f))), duration);
                    }
                    expansionHeight = getHeight();

                } else if (getCurrentVelocity() < -100) {

                    mScroller.startScroll(0, (int) expansionHeight, 0, (int) ((0 - expansionHeight)), duration);
                    expansionHeight = 0;


                } else if (expansionHeight > getHeight() / 2) {

                    Log.d("BD-TAG", "expansionHeight:" + expansionHeight);
                    mScroller.startScroll(0, (int) expansionHeight, 0, (int) ((getHeight() - expansionHeight + 1)), duration);

                    expansionHeight = getHeight();

                } else if (expansionHeight <= getHeight() / 2) {
                    mScroller.startScroll(0, (int) expansionHeight, 0, (int) (0 - expansionHeight), duration);
                    expansionHeight = 0;

                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }


                break;
        }

        return true;
    }

    private void initVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
        }
        mVelocityTracker = VelocityTracker.obtain();
    }


    private void trackMovement(MotionEvent event) {
        if (mVelocityTracker != null)
            mVelocityTracker.addMovement(event);
    }

    private float getCurrentVelocity() {
        if (mVelocityTracker == null) {
            return 0;
        }
        mVelocityTracker.computeCurrentVelocity(100);
        return mVelocityTracker.getYVelocity();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
//            contentView.setBottom(mScroller.getCurrY());
//            mNotificationview.setOverScrollBy(0,mScroller.getCurrY(),
//                                                0,getScrollY(),
//                                                0,0,0,0,true);
            mContenView.scrollTo(0, getHeight() - mScroller.getCurrY());
            // LogUtil.getInstance().d("getCurrY:"+mScroller.getCurrY());
            Log.d("BD-TAG", "getCurrY:" + mScroller.getCurrY());
            postInvalidate();
        }
    }

}
