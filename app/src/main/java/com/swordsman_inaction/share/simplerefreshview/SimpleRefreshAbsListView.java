package com.swordsman_inaction.share.simplerefreshview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public abstract class SimpleRefreshAbsListView extends LinearLayout {

    private AbsListView mAbsListView;
    private ImageView mHeaderView, mFooterView;

    private Animation mHeaderAnimation, mFooterAnimation;

    //for touch event
    static int mTouchSlop = 8;
    private int mActivePointerId = -1;
    private float mPosition = -1;
    private float mStartPosition = -1;

    //for pull constants
    private int mMaxPullDownPixels = 150;
    private int mMaxPullUpPixels = 150;
    private int mMinPullDownPixels = 50;
    private int mMinPullUpPixels = 50;
    private boolean mIsTouching = false;

    //for header and footer
    private int mHeaderHeight = 55;
    private int mFooterHeight = 50;


    //for recording pixels when pulling
    private int mPullDownPixelsCount = 0;
    private int mPullUpPixelsCount = 0;

    //for pulling status
    private boolean mCanPullDown, mCanPullUp;
    private boolean mIsHeaderShowed, mIsFooterShowed;
    private boolean mIsPullDownLoading, mIsPullUpLoading;

    //listeners
    private OnPullDownLoadListener mOnPullDownLoadListener;
    private OnPullUpLoadListener mOnPullUpLoadListener;

    //Animator
    private ValueAnimator mAnimator;

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;

    public SimpleRefreshAbsListView(Context context) {
        super(context);
        initUI();
    }

    public SimpleRefreshAbsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    public SimpleRefreshAbsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    public AbsListView getAbsListView(){
        return mAbsListView;
    }

    public void setOnPullDownLoadListener(OnPullDownLoadListener listener){
        mOnPullDownLoadListener = listener;
    }

    public void setOnPullUpLoadListener(OnPullUpLoadListener listener){
        mOnPullUpLoadListener = listener;
    }

    public void setPullDownLoadingFinished(){
        if (mIsHeaderShowed && !mIsTouching){
            moveAnimator(TYPE_HEADER, 0, new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mIsPullDownLoading = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }else {
            mIsPullDownLoading = false;
        }
    }

    public void setPullUpLoadingFinished(){
        if (mIsFooterShowed && !mIsTouching){
            moveAnimator(TYPE_FOOTER, 0, new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mIsPullUpLoading = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }else {
            mIsPullUpLoading = false;
        }
    }

    private void initUI(){
        setOrientation(LinearLayout.VERTICAL);

        inflateLayout();

        mAbsListView = (AbsListView) findViewById(R.id.abslistview);
        mHeaderView = (ImageView) findViewById(R.id.headerView);
        mFooterView = (ImageView) findViewById(R.id.footerView);

        mHeaderView.setImageResource(R.drawable.pulldown);
        mFooterView.setImageResource(R.drawable.pullup);

        mHeaderAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        mFooterAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);

        mAbsListView.setOnScrollListener(mListScrollListener);

        mMaxPullDownPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMaxPullDownPixels, getResources().getDisplayMetrics());
        mMaxPullUpPixels = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMaxPullUpPixels, getResources().getDisplayMetrics());
        mMinPullDownPixels = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMinPullDownPixels, getResources().getDisplayMetrics());
        mMinPullUpPixels = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMinPullUpPixels, getResources().getDisplayMetrics());
        mHeaderHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mHeaderHeight, getResources().getDisplayMetrics());
        mFooterHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mFooterHeight, getResources().getDisplayMetrics());
    }

    //subclass must inflate layout like simple_refresh_abslistview.xml
    protected abstract void inflateLayout();

    private AbsListView.OnScrollListener mListScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            //use the function to update can pull state

            if (totalItemCount <= 0 || view.getChildCount() <= 0){
                mCanPullDown = true;
                mCanPullUp = false;
                return;
            }

            mCanPullDown = firstVisibleItem == 0 && view.getChildAt(0).getTop() >= 0;

            mCanPullUp = (firstVisibleItem + visibleItemCount == totalItemCount) && view.getChildAt(view.getChildCount() - 1).getBottom() <= view.getMeasuredHeight();
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result;

        result = mIsHeaderShowed || mIsFooterShowed || super.dispatchTouchEvent(ev);

        switch (ev.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                mIsTouching = true;
                mActivePointerId = ev.getPointerId(ev.getActionIndex());
                mStartPosition = ev.getY();

                if (mAnimator != null){
                    mAnimator.cancel();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int index = -1;

                for (int i = 0; i < ev.getPointerCount(); i++){
                    if (ev.getPointerId(i) == mActivePointerId){
                        index = i;
                        break;
                    }
                }
                if (index != -1){
                    if (mPosition == -1){
                        //init first pullMove
                        if (Math.abs(ev.getY(index) - mStartPosition) > mTouchSlop){
                            mStartPosition = -1;
                            mPosition = ev.getY(index) + getAbsoluteY();
                        }
                        return result;
                    }

                    float oldPosition = mPosition;
                    mPosition = ev.getY(index) + getAbsoluteY();

                    if (!mCanPullUp && !mCanPullDown) {
                        break;
                    }

                    int distance = (int) (mPosition - oldPosition);

                    pullMove(false, distance);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mStartPosition = -1;
                mPosition = -1;
                mActivePointerId = -1;

                if (mIsHeaderShowed){
                    if (mIsPullDownLoading) {
                        if (mPullDownPixelsCount > mHeaderHeight) {
                            moveAnimator(TYPE_HEADER, mHeaderHeight, null);
                        }else {
                            moveAnimator(TYPE_HEADER, 0, null);
                        }
                    }else {
                        if (mPullDownPixelsCount <= mHeaderHeight){
                            // push header back
                            moveAnimator(TYPE_HEADER, 0, null);
                        }else {
                            // push header back and start loading
                            moveAnimator(TYPE_HEADER, mHeaderHeight, new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    startPullDownLoading();
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });
                        }
                    }

                }

                if (mIsFooterShowed){
                    if (mIsPullUpLoading) {
                        if (mPullUpPixelsCount > mFooterHeight) {
                            moveAnimator(TYPE_FOOTER, mFooterHeight, null);
                        }else {
                            moveAnimator(TYPE_FOOTER, 0, null);
                        }
                    }else {
                        if (mPullUpPixelsCount <= mFooterHeight) {
                            moveAnimator(TYPE_FOOTER, 0, null);
                        }else {
                            moveAnimator(TYPE_FOOTER, mFooterHeight, new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    startPullUpLoading();
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });
                        }
                    }

                }

                mIsTouching = false;
        }

        return result;
    }

    private void pullMove(boolean forceMove, int distance){

        boolean shouldMoveHeader;
        boolean shouldMoveFooter;

        shouldMoveHeader = (mCanPullDown || forceMove) && !mIsFooterShowed;
        shouldMoveFooter = (mCanPullUp || forceMove) && !mIsHeaderShowed;

        if (shouldMoveHeader) {
            if (mPullDownPixelsCount == 0 && distance <= 0) {
                shouldMoveHeader = false;
            }
        }

        if (shouldMoveFooter) {
            if (mPullUpPixelsCount == 0 && distance >= 0) {
                shouldMoveFooter = false;
            }
        }

        if (shouldMoveHeader) {
            moveHeader(distance);
        }

        if (shouldMoveFooter) {
            moveFooter(distance);
        }

    }

    private void moveHeader(int distance){
        int move = 0;

        if (mPullDownPixelsCount == 0){
            if (distance > 0){
                mIsHeaderShowed = true;
                showHeaderView(true);
                move = distance + mPullDownPixelsCount > mMaxPullDownPixels ? mMaxPullDownPixels : distance;
            }
        }else {
            if (distance > 0){
                move = distance + mPullDownPixelsCount > mMaxPullDownPixels ? mMaxPullDownPixels - mPullDownPixelsCount : distance;
            }else {
                move = distance + mPullDownPixelsCount < 0 ? -1 * mPullDownPixelsCount : distance;
            }
        }

        //pullMove
        if (move != 0){
            mPullDownPixelsCount += move;

            scrollTo(0, mPullDownPixelsCount * -1);

            if (mPullDownPixelsCount == 0){
                showHeaderView(false);
                mIsHeaderShowed = false;
            }
        }
    }

    private void moveFooter(int distance){
        int move = 0;
        distance *= -1;
        if (mPullUpPixelsCount == 0){
            if (distance > 0){
                mIsFooterShowed = true;
                showFooterView(true);
                move = distance + mPullUpPixelsCount > mMaxPullUpPixels ? mMaxPullUpPixels : distance;
            }
        }else {
            if (distance > 0){
                move = distance + mPullUpPixelsCount > mMaxPullUpPixels ? mMaxPullUpPixels - mPullUpPixelsCount : distance;
            }else {
                move = distance + mPullUpPixelsCount < 0 ? -1 * mPullUpPixelsCount : distance;
            }
        }

        //pullMove
        if (move != 0){
            mPullUpPixelsCount += move;

            scrollTo(0, mPullUpPixelsCount);

            if (mPullUpPixelsCount == 0){
                showFooterView(false);
                mIsFooterShowed = false;
            }
        }
    }

    private void moveAnimator(final int viewType, int endValue, Animator.AnimatorListener listener){
        int startValue = 0;
        if (viewType == TYPE_HEADER){
            startValue = mPullDownPixelsCount;
        }else if (viewType == TYPE_FOOTER){
            startValue = mPullUpPixelsCount;
        }
        mAnimator = ValueAnimator.ofInt(startValue, endValue).setDuration(300);
        mAnimator.setInterpolator(new LinearInterpolator());

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (viewType == TYPE_HEADER) {
                    int distance = (Integer) valueAnimator.getAnimatedValue() - mPullDownPixelsCount;
                    pullMove(true, distance);
                } else if (viewType == TYPE_FOOTER) {
                    int distance = mPullUpPixelsCount - (Integer) valueAnimator.getAnimatedValue();
                    pullMove(true, distance);
                }
            }
        });

        if (listener != null) {
            mAnimator.addListener(listener);
        }
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        mAnimator.start();
    }

    private void startPullDownLoading(){
        if (mIsPullUpLoading) {
            setPullDownLoadingFinished();
            return;
        }
        mIsPullDownLoading = true;
        if (mOnPullDownLoadListener != null){
            mOnPullDownLoadListener.onPullDownLoad();
        }
    }

    private void startPullUpLoading(){
        if (mIsPullDownLoading) {
            setPullUpLoadingFinished();
        }
        mIsPullUpLoading = true;
        if (mOnPullUpLoadListener != null){
            mOnPullUpLoadListener.onPullUpLoad();
        }
    }

    private void showHeaderView(boolean state){
        if (state){
            mHeaderView.setVisibility(View.VISIBLE);
            mHeaderView.startAnimation(mHeaderAnimation);
        }else{
            mHeaderView.clearAnimation();
            mHeaderView.setVisibility(View.INVISIBLE);
        }
    }

    private void showFooterView(boolean state){
        if (state){
            mFooterView.setVisibility(View.VISIBLE);
            mFooterView.startAnimation(mFooterAnimation);
        }else{
            mFooterView.clearAnimation();
            mFooterView.setVisibility(View.INVISIBLE);
        }
    }

    private int getAbsoluteY(){
        int[] xx = new int[2];
        getLocationOnScreen(xx);
        return xx[1];
    }

    public interface OnPullDownLoadListener{
        void onPullDownLoad();
    }

    public interface OnPullUpLoadListener{
        void onPullUpLoad();
    }
}
