package com.foo.botimer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by hilo on 3/29/14.
 */
class FreebaseNodeDisplay extends LinearLayout
{

    public FreebaseInterface.FreebaseNodeData FreebaseNodeData;
    public android.widget.ImageView ImageView;
    public android.widget.TextView TextView;
    private ConverserActivity converserActivity;
    private GestureDetector GestureDetector_this;

    public FreebaseNodeDisplay(Context Context, FreebaseInterface.FreebaseNodeData FreebaseNodeData)
    {
        super(Context);

        this.converserActivity = (ConverserActivity) Context;
        this.FreebaseNodeData = FreebaseNodeData;

        this.Init();
    }

    private void Init()
    {
        this.setOrientation(VERTICAL);
        this.CreateItemImageView();
        this.CreateNameTextView();
        this.CreateGestureDetector();
        this.CreateTouchListener();

        this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    private void CreateItemImageView()
    {
        this.ImageView = new ImageView(getContext());
        this.addView(this.ImageView);
    }

    private void CreateNameTextView()
    {
        this.TextView = new TextView(getContext());
        this.TextView.setTextColor(Color.parseColor("#cccccc"));
        this.TextView.setTextSize(20);
        this.addView(this.TextView);
    }

    private void CreateGestureDetector()
    {
        this.GestureDetector_this = new GestureDetector(new GestureListener_freebaseNodeDisplay(this));
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
        this.converserActivity.OnSingleTap_freebaseNodeDisplay(this);
    }

    public void OnFling()
    {
        this.converserActivity.OnFling_freebaseNodeDisplay(this);
    }

    public void OnFlingComplete()
    {
        this.converserActivity.OnFlingComplete_freebaseNodeDisplay(this);
    }

}




class GestureListener_freebaseNodeDisplay extends GestureDetector.SimpleOnGestureListener
{

    private FreebaseNodeDisplay freebaseNodeDisplay;

    public GestureListener_freebaseNodeDisplay(FreebaseNodeDisplay freebaseNodeDisplay)
    {
        this.freebaseNodeDisplay = freebaseNodeDisplay;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        this.freebaseNodeDisplay.OnFling();

        float x_start = this.freebaseNodeDisplay.getX();
        float y_start = this.freebaseNodeDisplay.getY();
        float x_stop = x_start + velocityX/2f;
        float y_stop = y_start + velocityY/2f;

        if (Math.sqrt(velocityX*velocityX + velocityY*velocityY) > 2500)
        {
            ObjectAnimator ObjectAnimator_x = ObjectAnimator.ofFloat(this.freebaseNodeDisplay, "x", x_start, x_stop);
            ObjectAnimator_x.setDuration(400);
            ObjectAnimator ObjectAnimator_y = ObjectAnimator.ofFloat(this.freebaseNodeDisplay, "y", y_start, y_stop);
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

        ObjectAnimator ObjectAnimator_scaleX = ObjectAnimator.ofFloat(this.freebaseNodeDisplay, "scaleX", scaleX_start, scaleX_stop);
        ObjectAnimator_scaleX.setDuration(200);
        //ObjectAnimator_scaleX.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator_scaleX.start();

        ObjectAnimator ObjectAnimator_scaleY = ObjectAnimator.ofFloat(this.freebaseNodeDisplay, "scaleY", scaleY_start, scaleY_stop);
        ObjectAnimator_scaleY.setDuration(200);
        //ObjectAnimator_scaleX.setInterpolator(new DecelerateInterpolator());
        ObjectAnimator_scaleY.start();

        this.freebaseNodeDisplay.OnSingleTap();

        return false;
    }

    private Animator.AnimatorListener AnimatorListener_freebaseNodeDisplay_fling = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationCancel(Animator arg0) {}

        @Override
        public void onAnimationEnd(Animator Animator)
        {
            freebaseNodeDisplay.OnFlingComplete();
        }

        @Override
        public void onAnimationRepeat(Animator arg0) {}

        @Override
        public void onAnimationStart(Animator arg0) {}
    };
}
