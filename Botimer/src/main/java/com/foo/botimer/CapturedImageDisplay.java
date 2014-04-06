package com.foo.botimer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by hilo on 4/6/14.
 */

class CapturedImageDisplay extends LinearLayout
{

    public android.widget.ImageView ImageView;
    public android.widget.TextView TextView;
    private ConverserActivity converserActivity;
    private GestureDetector GestureDetector_this;
    public String recognizedObject;

    public CapturedImageDisplay(Context Context, String recognizedObject)
    {
        super(Context);

        this.converserActivity = (ConverserActivity) Context;
        this.recognizedObject = recognizedObject;

        this.Init();
    }

    private void Init()
    {
        this.setOrientation(VERTICAL);
        this.CreateItemImageView();
        this.CreateNameTextView();
        this.CreateGestureDetector();
        this.CreateTouchListener();
    }

    private void CreateItemImageView()
    {
        this.ImageView = new android.widget.ImageView(getContext());
        this.addView(this.ImageView);
    }

    private void CreateNameTextView()
    {
        this.TextView = new android.widget.TextView(getContext());
        this.TextView.setTextColor(Color.parseColor("#cccccc"));
        this.TextView.setTextSize(20);
        this.addView(this.TextView);
    }

    private void CreateGestureDetector()
    {
        this.GestureDetector_this = new GestureDetector(new GestureListener_capturedImageDisplay(this));
    }

    private void CreateTouchListener()
    {
        this.setOnTouchListener(this.OnTouchListener_this);
    }


    ///////////////////////////
    //callbacks
    ///////////////////////////

    private View.OnTouchListener OnTouchListener_this = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(final View view, final MotionEvent event)
        {
            GestureDetector_this.onTouchEvent(event);
            return true;
        }
    };

    public void OnSingleTap()
    {
        //this.converserActivity.OnSingleTap_freebaseNodeDisplay(this);
    }

    public void OnFling()
    {
        this.converserActivity.OnFling_capturedImageDisplay(this);
    }

    public void OnFlingComplete()
    {
        this.converserActivity.OnFlingComplete_capturedImageDisplay(this);
    }

}




class GestureListener_capturedImageDisplay extends GestureDetector.SimpleOnGestureListener
{

    private CapturedImageDisplay capturedImageDisplay;

    public GestureListener_capturedImageDisplay(CapturedImageDisplay capturedImageDisplay)
    {
        this.capturedImageDisplay = capturedImageDisplay;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        //this.freebaseNodeDisplay.OnFling();

        float x_start = this.capturedImageDisplay.getX();
        float y_start = this.capturedImageDisplay.getY();
        float x_stop = x_start + velocityX/2f;
        float y_stop = y_start + velocityY/2f;

        if (Math.sqrt(velocityX*velocityX + velocityY*velocityY) > 2500)
        {
            ObjectAnimator ObjectAnimator_x = ObjectAnimator.ofFloat(this.capturedImageDisplay, "x", x_start, x_stop);
            ObjectAnimator_x.setDuration(400);
            ObjectAnimator ObjectAnimator_y = ObjectAnimator.ofFloat(this.capturedImageDisplay, "y", y_start, y_stop);
            ObjectAnimator_y.setDuration(400);
            ObjectAnimator_y.addListener(this.AnimatorListener_freebaseNodeDisplay_fling);
            AnimatorSet AnimatorSet = new AnimatorSet();
            AnimatorSet.playTogether(ObjectAnimator_x, ObjectAnimator_y);
            AnimatorSet.start();

            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event)
    {
        float scaleX_start = .9f;
        float scaleX_stop = 1f;
        float scaleY_start = .9f;
        float scaleY_stop = 1f;

        ObjectAnimator ObjectAnimator_scaleX = ObjectAnimator.ofFloat(this.capturedImageDisplay, "scaleX", scaleX_start, scaleX_stop);
        ObjectAnimator_scaleX.setDuration(200);
        //ObjectAnimator_scaleX.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator_scaleX.start();

        ObjectAnimator ObjectAnimator_scaleY = ObjectAnimator.ofFloat(this.capturedImageDisplay, "scaleY", scaleY_start, scaleY_stop);
        ObjectAnimator_scaleY.setDuration(200);
        //ObjectAnimator_scaleX.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator_scaleY.start();

        this.capturedImageDisplay.OnSingleTap();

        return false;
    }

    private Animator.AnimatorListener AnimatorListener_freebaseNodeDisplay_fling = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationCancel(Animator arg0) {}

        @Override
        public void onAnimationEnd(Animator Animator)
        {
            capturedImageDisplay.OnFlingComplete();
        }

        @Override
        public void onAnimationRepeat(Animator arg0) {}

        @Override
        public void onAnimationStart(Animator arg0) {}
    };
}
