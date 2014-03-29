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

    public MediaDisplay(Context context)
    {
        super(context);

        this.converserActivity = (ConverserActivity) context;

        this.Init();
    }

    private void Init()
    {
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

        int w_layout = this.getWidth();
        int h_layout = this.getHeight();
        int w_imageView = freebaseNodeDisplay.ImageView.getDrawable().getIntrinsicWidth();
        int h_imageView = freebaseNodeDisplay.ImageView.getDrawable().getIntrinsicHeight();
        int x_start = (int)freebaseNodeDisplay.getX();
        int y_start = (int)freebaseNodeDisplay.getY();
        int x_stop = new Random().nextInt(w_layout) - w_imageView/2;
        int y_stop = new Random().nextInt(h_layout) - h_imageView/2;
        int rotationZ_start = (int)freebaseNodeDisplay.getRotation();
        int rotationZ_stop = new Random().nextInt(35) - 17;
        int rotationX_start = (int)freebaseNodeDisplay.getRotationX();
        int rotationX_stop = new Random().nextInt(35) - 17;
        int rotationY_start = (int)freebaseNodeDisplay.getRotationY();
        int rotationY_stop = new Random().nextInt(35) - 17;

        ObjectAnimator ObjectAnimator_translateX = ObjectAnimator.ofFloat(freebaseNodeDisplay, "translationX", x_start, x_stop);
        ObjectAnimator_translateX.setDuration(40000);
        ObjectAnimator ObjectAnimator_translateY = ObjectAnimator.ofFloat(freebaseNodeDisplay, "translationY", y_start, y_stop);
        ObjectAnimator_translateY.setDuration(60000);
        ObjectAnimator ObjectAnimator_rotateZ= ObjectAnimator.ofFloat(freebaseNodeDisplay,  "rotation", rotationZ_start, rotationZ_stop);
        ObjectAnimator_rotateZ.setDuration(50000);
        ObjectAnimator ObjectAnimator_rotateX = ObjectAnimator.ofFloat(freebaseNodeDisplay, "rotationX", rotationX_start, rotationX_stop);
        ObjectAnimator_rotateX.setDuration(70000);
        ObjectAnimator ObjectAnimator_rotateY = ObjectAnimator.ofFloat(freebaseNodeDisplay, "rotationY", rotationY_start, rotationY_stop);
        ObjectAnimator_rotateY.setDuration(120000);

        ObjectAnimator_rotateY.addListener(AnimatorListener_mediaCanvasImage_animate);

        AnimatorSet AnimatorSet = new AnimatorSet();
        AnimatorSet.playTogether(ObjectAnimator_translateX, ObjectAnimator_translateY, ObjectAnimator_rotateZ, ObjectAnimator_rotateX, ObjectAnimator_rotateY);
        AnimatorSet.start();
    }

    private Animator.AnimatorListener AnimatorListener_mediaCanvasImage_animate = new Animator.AnimatorListener()
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

}
