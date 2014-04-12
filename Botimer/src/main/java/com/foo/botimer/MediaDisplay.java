package com.foo.botimer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by hilo on 3/29/14.
 */
public class MediaDisplay extends RelativeLayout
{

    private ConverserActivity converserActivity;
    private Random random;

    public MediaDisplay(Context context)
    {
        super(context);

        this.converserActivity = (ConverserActivity) context;

        this.Init();
    }

    private void Init()
    {
        this.random = new Random();

        this.InitLayoutParams();
    }

    private void InitLayoutParams()
    {
        LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.width = this.converserActivity.W_SCREEN;
        layoutParams.height = this.converserActivity.H_SCREEN;
        this.setLayoutParams(layoutParams);
    }







    public void AnimateFreebaseNodeDisplay(FreebaseNodeDisplay freebaseNodeDisplay)
    {
        //RelativeLayout RelativeLayout = (RelativeLayout) this.findViewById(R.id.RelativeLayout_mediaCanvas);

        int w_layout = this.converserActivity.W_SCREEN;
        int h_layout = this.converserActivity.H_SCREEN;
        int w_imageView = freebaseNodeDisplay.ImageView.getDrawable().getIntrinsicWidth();
        int h_imageView = freebaseNodeDisplay.ImageView.getDrawable().getIntrinsicHeight();
        int x_min = 0;
        int x_max = w_layout - w_imageView;
        int y_min = 0;
        int y_max = h_layout - h_imageView;
        int x_start = (int)freebaseNodeDisplay.getX();
        int y_start = (int)freebaseNodeDisplay.getY();
        int x_stop = this.random.nextInt(x_max - x_min + 1) + x_min;
        int y_stop = this.random.nextInt(y_max - y_min + 1) + y_min;
        int rotationZ_start = (int)freebaseNodeDisplay.getRotation();
        int rotationZ_stop = new Random().nextInt(35) - 17;
        int rotationX_start = (int)freebaseNodeDisplay.getRotationX();
        int rotationX_stop = new Random().nextInt(35) - 17;
        int rotationY_start = (int)freebaseNodeDisplay.getRotationY();
        int rotationY_stop = new Random().nextInt(35) - 17;

        int speedFactor = 10;
        ObjectAnimator ObjectAnimator_translateX = ObjectAnimator.ofFloat(freebaseNodeDisplay, "translationX", x_start, x_stop);
        ObjectAnimator_translateX.setDuration(400*speedFactor);
        ObjectAnimator ObjectAnimator_translateY = ObjectAnimator.ofFloat(freebaseNodeDisplay, "translationY", y_start, y_stop);
        ObjectAnimator_translateY.setDuration(600*speedFactor);
        ObjectAnimator ObjectAnimator_rotateZ= ObjectAnimator.ofFloat(freebaseNodeDisplay,  "rotation", rotationZ_start, rotationZ_stop);
        ObjectAnimator_rotateZ.setDuration(500*speedFactor);
        ObjectAnimator ObjectAnimator_rotateX = ObjectAnimator.ofFloat(freebaseNodeDisplay, "rotationX", rotationX_start, rotationX_stop);
        ObjectAnimator_rotateX.setDuration(700*speedFactor);
        ObjectAnimator ObjectAnimator_rotateY = ObjectAnimator.ofFloat(freebaseNodeDisplay, "rotationY", rotationY_start, rotationY_stop);
        ObjectAnimator_rotateY.setDuration(1200*speedFactor);

        ObjectAnimator_rotateY.addListener(AnimatorListener_freebaseNodeDisplay_animate);

        AnimatorSet AnimatorSet = new AnimatorSet();
        AnimatorSet.playTogether(ObjectAnimator_translateX, ObjectAnimator_translateY, ObjectAnimator_rotateZ, ObjectAnimator_rotateX, ObjectAnimator_rotateY);
        AnimatorSet.start();
    }

    private Animator.AnimatorListener AnimatorListener_freebaseNodeDisplay_animate = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationCancel(Animator arg0)
        {
        }

        @Override
        public void onAnimationEnd(Animator Animator)
        {
            FreebaseNodeDisplay FreebaseNodeDisplay = (FreebaseNodeDisplay) ((ObjectAnimator) Animator).getTarget();
            AnimateFreebaseNodeDisplay(FreebaseNodeDisplay);
        }

        @Override
        public void onAnimationRepeat(Animator arg0)
        {
        }

        @Override
        public void onAnimationStart(Animator arg0)
        {
        }
    };











    public void AnimateCapturedImageeDisplay(CapturedImageDisplay capturedImageDisplay)
    {
        //RelativeLayout RelativeLayout = (RelativeLayout) this.findViewById(R.id.RelativeLayout_mediaCanvas);

        int w_layout = this.converserActivity.W_SCREEN;
        int h_layout = this.converserActivity.H_SCREEN;
        int w_imageView = capturedImageDisplay.ImageView.getDrawable().getIntrinsicWidth();
        int h_imageView = capturedImageDisplay.ImageView.getDrawable().getIntrinsicHeight();
        int x_min = 0;
        int x_max = w_layout - w_imageView;
        int y_min = 0;
        int y_max = h_layout - h_imageView;
        int x_start = (int)capturedImageDisplay.getX();
        int y_start = (int)capturedImageDisplay.getY();
        int x_stop = this.random.nextInt(x_max - x_min + 1) + x_min;
        int y_stop = this.random.nextInt(y_max - y_min + 1) + y_min;
        int rotationZ_start = (int)capturedImageDisplay.getRotation();
        int rotationZ_stop = new Random().nextInt(35) - 17;
        int rotationX_start = (int)capturedImageDisplay.getRotationX();
        int rotationX_stop = new Random().nextInt(35) - 17;
        int rotationY_start = (int)capturedImageDisplay.getRotationY();
        int rotationY_stop = new Random().nextInt(35) - 17;

        int speedFactor = 10;
        ObjectAnimator ObjectAnimator_translateX = ObjectAnimator.ofFloat(capturedImageDisplay, "translationX", x_start, x_stop);
        ObjectAnimator_translateX.setDuration(400*speedFactor);
        ObjectAnimator ObjectAnimator_translateY = ObjectAnimator.ofFloat(capturedImageDisplay, "translationY", y_start, y_stop);
        ObjectAnimator_translateY.setDuration(600*speedFactor);
        ObjectAnimator ObjectAnimator_rotateZ= ObjectAnimator.ofFloat(capturedImageDisplay,  "rotation", rotationZ_start, rotationZ_stop);
        ObjectAnimator_rotateZ.setDuration(500*speedFactor);
        ObjectAnimator ObjectAnimator_rotateX = ObjectAnimator.ofFloat(capturedImageDisplay, "rotationX", rotationX_start, rotationX_stop);
        ObjectAnimator_rotateX.setDuration(700*speedFactor);
        ObjectAnimator ObjectAnimator_rotateY = ObjectAnimator.ofFloat(capturedImageDisplay, "rotationY", rotationY_start, rotationY_stop);
        ObjectAnimator_rotateY.setDuration(1200*speedFactor);

        ObjectAnimator_rotateY.addListener(AnimatorListener_capturedImageDisplay_animate);

        AnimatorSet AnimatorSet = new AnimatorSet();
        AnimatorSet.playTogether(ObjectAnimator_translateX, ObjectAnimator_translateY, ObjectAnimator_rotateZ, ObjectAnimator_rotateX, ObjectAnimator_rotateY);
        AnimatorSet.start();
    }

    private Animator.AnimatorListener AnimatorListener_capturedImageDisplay_animate = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationCancel(Animator arg0)
        {
        }

        @Override
        public void onAnimationEnd(Animator Animator)
        {
            CapturedImageDisplay capturedImageDisplay = (CapturedImageDisplay) ((ObjectAnimator) Animator).getTarget();
            AnimateCapturedImageeDisplay(capturedImageDisplay);
        }

        @Override
        public void onAnimationRepeat(Animator arg0)
        {
        }

        @Override
        public void onAnimationStart(Animator arg0)
        {
        }
    };

}
