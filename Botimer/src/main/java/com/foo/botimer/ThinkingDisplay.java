package com.foo.botimer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by hilo on 3/29/14.
 */
public class ThinkingDisplay extends RelativeLayout
{

    private ConverserActivity converserActivity;
    private ArrayList<ImageView> TtsIndicators;
    private boolean shouldAnimateTtsIndicators;
    private ProgressBar ThinkingIndicator;
    private Random random;
    private ProgressBar progressIndicator;
    private CountDownTimer progressCountDownTimer;

    public ThinkingDisplay(Context context)
    {
        super(context);

        this.converserActivity = (ConverserActivity) context;

        this.Init();
    }

    private void Init()
    {
        this.TtsIndicators = new ArrayList<ImageView>();
        this.shouldAnimateTtsIndicators = false;
        this.random = new Random();

        this.InitLayoutParams();
        this.CreateThinkingIndicator();
        this.CreateProgressIndicator();
        this.CreateProgressCountDownTimer();
        this.CreateTtsIndicators();
    }

    private void InitLayoutParams()
    {
        LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.width = this.converserActivity.W_SCREEN;
        layoutParams.height = this.converserActivity.H_SCREEN;
        this.setLayoutParams(layoutParams);
    }

    private void CreateThinkingIndicator()
    {
        this.ThinkingIndicator = new ProgressBar(this.getContext(), null, android.R.attr.progressBarStyle);
        this.ThinkingIndicator.setAlpha(0);
        float x = this.converserActivity.W_SCREEN/2 - 35;
        this.ThinkingIndicator.setX(x);
        this.addView(this.ThinkingIndicator);
    }

    private void CreateProgressIndicator()
    {
        this.progressIndicator = new ProgressBar(this.getContext(), null, android.R.attr.progressBarStyleHorizontal);
        //this.progressIndicator.setAlpha(0);
        float x = this.converserActivity.W_SCREEN/2 - 17;
        this.progressIndicator.setX(x);
        this.progressIndicator.setY(33);
        this.addView(this.progressIndicator);
        this.progressIndicator.setProgress(0);
        LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.width = 60;
        this.progressIndicator.setLayoutParams(layoutParams);


    }

    private void CreateProgressCountDownTimer()
    {
        final long countDownDuration = 16000;
        long tick = 100;
        this.progressCountDownTimer = new CountDownTimer(countDownDuration, tick)
        {
            @Override
            public void onTick(long timeRemaining)
            {
                long timeProgressed = countDownDuration - timeRemaining;
                float normalizedTimeProgressed = timeProgressed / (float) countDownDuration;
                int progress = (int)(normalizedTimeProgressed * 100f);
                progressIndicator.setProgress(progress);
            }

            @Override
            public void onFinish()
            {
                Log.d("foo", "progress count down finishted");
                HideProgressIndicator();
            }
        };
    }

    public void StartProgressCountDownTimer()
    {
        Log.d("foo", "StartProgressCountDownTimer");

        this.ShowProgressIndicator();
        this.progressCountDownTimer.start();
    }

    private void ShowProgressIndicator()
    {
        this.progressIndicator.setAlpha(1);
    }

    private void HideProgressIndicator()
    {
        ObjectAnimator ObjectAnimator_alpha = ObjectAnimator.ofFloat(this.progressIndicator, "alpha", this.progressIndicator.getAlpha(), 0);
        ObjectAnimator_alpha.setDuration(500);
        ObjectAnimator_alpha.start();
    }

    private void CreateTtsIndicators()
    {
        int numRows = 3;
        int numColumns = 30;
        int w_tile = 40;
        int h_tile = 40;
        int offsetX = w_tile/2;
        int offsetY = h_tile/2 + 150;
        for (int i=0; i<numRows; i++)
        {
            for (int j=0; j<numColumns; j++)
            {
                float x = j*w_tile;
                float y = i*h_tile;
                x += offsetX;
                y += offsetY;
                this.CreateTtsIndicator(x, y);
            }
        }
    }

    private void CreateTtsIndicator(float x, float y)
    {
        ImageView TtsIndicator = new ImageView(this.getContext());
        TtsIndicator.setImageResource(R.drawable.circle);
        TtsIndicator.setScaleType(android.widget.ImageView.ScaleType.CENTER);
        this.addView(TtsIndicator);
        int w_imageView = TtsIndicator.getDrawable().getIntrinsicWidth();
        int h_imageView = TtsIndicator.getDrawable().getIntrinsicHeight();
        float x_imageView = x - w_imageView/2;
        float y_imageView = y - h_imageView/2;
        TtsIndicator.setX(x_imageView);
        TtsIndicator.setY(y_imageView);
        TtsIndicator.setAlpha(.5f);
        TtsIndicator.setScaleX(.3f);
        TtsIndicator.setScaleY(.3f);
        this.TtsIndicators.add(TtsIndicator);
    }

    private void ScaleUpTtsIndicator(ImageView TtsIndicator)
    {
        int delay = (new Random()).nextInt(350);
        int duration  = (new Random()).nextInt(500);
        float scale_start = .3f;
        float scale_stop = .6f;
        ObjectAnimator ObjectAnimator_scaleX = ObjectAnimator.ofFloat(TtsIndicator, "scaleX", scale_start, scale_stop);
        ObjectAnimator_scaleX.setDuration(duration);
        ObjectAnimator_scaleX.setStartDelay(delay);
        ObjectAnimator ObjectAnimator_scaleY = ObjectAnimator.ofFloat(TtsIndicator, "scaleY", scale_start, scale_stop);
        ObjectAnimator_scaleY.setDuration(duration);
        ObjectAnimator_scaleY.setStartDelay(delay);
        ObjectAnimator_scaleY.addListener(AnimatorListener_ttsIndicator_scaleUp);
        AnimatorSet AnimatorSet = new AnimatorSet();
        AnimatorSet.playTogether(ObjectAnimator_scaleX, ObjectAnimator_scaleY);
        AnimatorSet.start();
    }

    private void ScaleDownTtsIndicator(ImageView TtsIndicator)
    {
        int delay = (new Random()).nextInt(150);
        int duration  = (new Random()).nextInt(500);
        float scale_start = .6f;
        float scale_stop = .3f;
        ObjectAnimator ObjectAnimator_scaleX = ObjectAnimator.ofFloat(TtsIndicator, "scaleX", scale_start, scale_stop);
        ObjectAnimator_scaleX.setDuration(duration);
        ObjectAnimator_scaleX.setStartDelay(delay);
        ObjectAnimator ObjectAnimator_scaleY = ObjectAnimator.ofFloat(TtsIndicator, "scaleY", scale_start, scale_stop);
        ObjectAnimator_scaleY.setDuration(duration);
        ObjectAnimator_scaleY.setStartDelay(delay);
        if (this.shouldAnimateTtsIndicators == true) {ObjectAnimator_scaleY.addListener(AnimatorListener_ttsIndicator_scaleUp);}
        AnimatorSet AnimatorSet = new AnimatorSet();
        AnimatorSet.playTogether(ObjectAnimator_scaleX, ObjectAnimator_scaleY);
        AnimatorSet.start();
    }


    ///////////////////////////
    //callbacks
    ///////////////////////////

    private Animator.AnimatorListener AnimatorListener_ttsIndicator_scaleUp = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationCancel(Animator arg0) {}

        @Override
        public void onAnimationEnd(Animator Animator)
        {
            ImageView TtsIndicator = (ImageView) ((ObjectAnimator) Animator).getTarget();
            ScaleDownTtsIndicator(TtsIndicator);
        }

        @Override
        public void onAnimationRepeat(Animator arg0) {}

        @Override
        public void onAnimationStart(Animator arg0) {}
    };


    ///////////////////////////
    //utilities
    ///////////////////////////

    public void StartAnimatingTtsIndicators()
    {
        this.shouldAnimateTtsIndicators = true;

        for (int i=0; i<this.TtsIndicators.size(); i++)
        {
            float randNum = this.random.nextFloat();
            if (randNum < .25)
            {
                ImageView TtsIndicator = this.TtsIndicators.get(i);
                this.ScaleUpTtsIndicator(TtsIndicator);
            }
        }
    }

    public void StopAnimatingTtsIndicators()
    {
        this.shouldAnimateTtsIndicators = false;
    }

    public void ShowThinkingIndicator()
    {
        this.converserActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int duration = 500;
                float alpha_start = ThinkingIndicator.getAlpha();
                float alpha_stop = 1;
                ObjectAnimator ObjectAnimator_alpha = ObjectAnimator.ofFloat(ThinkingIndicator, "alpha", alpha_start, alpha_stop);
                ObjectAnimator_alpha.setDuration(duration);
                ObjectAnimator_alpha.start();
            }
        });
    }

    public void HideThinkingIndicator()
    {
        this.converserActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int duration = 500;
                float alpha_start = ThinkingIndicator.getAlpha();
                float alpha_stop = 0;
                ObjectAnimator ObjectAnimator_alpha = ObjectAnimator.ofFloat(ThinkingIndicator, "alpha", alpha_start, alpha_stop);
                ObjectAnimator_alpha.setDuration(duration);
                ObjectAnimator_alpha.start();
            }
        });
    }
}



