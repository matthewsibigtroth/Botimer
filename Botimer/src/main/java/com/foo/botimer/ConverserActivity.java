package com.foo.botimer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import android.speech.tts.TextToSpeech;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.content.Intent;
import android.widget.Button;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;

import java.net.URL;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.net.Uri;
import android.speech.tts.UtteranceProgressListener;
import java.util.HashMap;
import java.util.Random;

import android.media.AudioManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class ConverserActivity extends ActionBarActivity {

    private SpeechRecognizer Listener;
    private TextToSpeech Speaker;
    private HashMap<String, String> TtsUtteranceMap;
    private ArrayList<ImageView> TtsIndicators;
    private boolean shouldAnimateTtsIndicators;
    private FreebaseInterface FreebaseInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converser);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        this.Init();
    }

    private void Init()
    {
        this.TtsIndicators = new ArrayList<ImageView>();
        this.shouldAnimateTtsIndicators = false;

        this.InitStartListeningButton();
        this.CreateListener();
        //this.CreateSpeaker();
        this.CreateTtsIndicators();
        this.CreateFreebaseInterface();
    }

    private void InitStartListeningButton()
    {
        final Button Button = (Button) findViewById(R.id.Button_listen);
        Button.setOnClickListener(OnClick_listenButton);
    }

    private void CreateListener()
    {
        this.Listener = SpeechRecognizer.createSpeechRecognizer(this);
        this.Listener.setRecognitionListener(new RecognitionListenerExtended());
        //disable speechrecognizer beep
        AudioManager AudioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        AudioManager.setStreamMute(AudioManager.VIBRATE_TYPE_NOTIFICATION, true);
    }

    private void CreateSpeaker()
    {
        this.TtsUtteranceMap = new HashMap<String, String>();
        this.Speaker = new TextToSpeech(this, new ttsInitListener());
        this.Speaker.setOnUtteranceProgressListener(new ttsUtteranceListener());
    }

    private void CreateTtsIndicators()
    {
        int numRows = 3;
        int numColumns = 30;
        int w_tile = 40;
        int h_tile = 30;
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
        ImageView TtsIndicator = new ImageView(this);
        TtsIndicator.setImageResource(R.drawable.circuit);
        RelativeLayout RelativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayout_ttsIndicator);
        TtsIndicator.setScaleType(android.widget.ImageView.ScaleType.CENTER);
        RelativeLayout.addView(TtsIndicator);
        int w_layout = RelativeLayout.getWidth();
        int h_layout = RelativeLayout.getHeight();
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

    private void CreateFreebaseInterface()
    {
        this.FreebaseInterface = new FreebaseInterface(this);
    }


    /////////////////////////////////////
    //callbacks
    /////////////////////////////////////

    class ttsInitListener implements TextToSpeech.OnInitListener {

        @Override
        public void onInit(int status) {

            if (status == TextToSpeech.SUCCESS) {
                //tts.setLanguage(Locale.getDefault());

            } else {

            }
        }
    }

    class ttsUtteranceListener extends UtteranceProgressListener {

        @Override
        public void onDone(String utteranceId)
        {
            ConverserActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StopAnimatingTtsIndicators();
                    Listen();
                }
            });
        }

        @Override
        public void onError(String utteranceId) {
        }

        @Override
        public void onStart(String utteranceId)
        {
            ConverserActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StartAnimatingTtsIndicators();
                }
            });
        }
    }

    private View.OnClickListener OnClick_listenButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Listen();
            FreebaseInterface.FindImageForInputText("cat");

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.converser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void OnSpeechRecognized(String speechRecognized)
    {
        Log.d("foo", "heard " + speechRecognized);
        this.SayToBot(speechRecognized);
    }

    public void OnComplete_findImageForInputText(String url_image) throws IOException {
        CreateImageViewFromUrl(url_image);
    }


    ///////////////////////////
    //utilities
    ///////////////////////////

    private void Listen()
    {
        Log.d("foo", "StartListening");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        this.Listener.startListening(intent);
    }

    private void Speak(String textToSpeak)
    {
        this.TtsUtteranceMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        this.Speaker.speak(textToSpeak, TextToSpeech.QUEUE_ADD, this.TtsUtteranceMap);
        //this.AttractAllImageViewsToGivenPosition(200,200);
    }

    private void SayToBot(String textToSpeak)
    {
        final String textToSpeak_ = textToSpeak;

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                //Show UI
            }

            @Override
            protected Void doInBackground(Void... arg0)
            {
                HttpURLConnection connection;
                OutputStreamWriter request = null;

                URL url = null;
                String response = null;

                //String urlString = "http://www.pandorabots.com/pandora/talk-xml?botid=e365655dbe351ac7&input=hello";
                String urlString = "http://www.pandorabots.com/pandora/talk-xml?botid=e365655dbe351ac7&input=" + Uri.encode(textToSpeak_);

                try{url = new URL(urlString);}
                catch(MalformedURLException e){}

                try
                {
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestMethod("POST");
                    request = new OutputStreamWriter(connection.getOutputStream());

                    try
                    {
                        request.flush();
                        request.close();
                    }
                    catch(IOException e){}
                    String line = "";
                    InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    // Response from server after login process will be stored in response variable.
                    response = sb.toString();

                    isr.close();
                    reader.close();

                    int index_start = response.indexOf("<that>") + 6;
                    int index_stop = response.indexOf("</that>");
                    String speechResponse = response.substring(index_start, index_stop);
                    Log.d("foo", speechResponse);
                    Speak(speechResponse);
                }
                catch(IOException e)
                {
                    Log.d("foo", e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                //Show UI (Toast msg here)

            }

        };

        task.execute((Void[])null);
    }

    private void StartAnimatingTtsIndicators()
    {
        this.shouldAnimateTtsIndicators = true;

        for (int i=0; i<this.TtsIndicators.size(); i++)
        {
            ImageView TtsIndicator = this.TtsIndicators.get(i);
            this.ScaleUpTtsIndicator(TtsIndicator);
        }
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

    private Animator.AnimatorListener AnimatorListener_ttsIndicator_scaleUp = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationCancel(Animator arg0)
        {
        }

        @Override
        public void onAnimationEnd(Animator Animator)
        {
            ImageView TtsIndicator = (ImageView) ((ObjectAnimator) Animator).getTarget();
            ScaleDownTtsIndicator(TtsIndicator);
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

    private void StopAnimatingTtsIndicators()
    {
        this.shouldAnimateTtsIndicators = false;
    }

    class RecognitionListenerExtended implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            Log.d("foo", "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
            //Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
            //Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            //Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            Log.d("foo", "onEndofSpeech");
        }
        public void onError(int error)
        {
            //Log.d(TAG,  "error " +  error);
            //mText.setText("error " + error);
        }
        public void onResults(Bundle results)
        {
            String str = new String();
            //Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++)
            {
                //Log.d("foo", "result " + data.get(i));
                str += data.get(i).toString();
                OnSpeechRecognized(str);
                return;

            }
        }
        public void onPartialResults(Bundle partialResults)
        {
            //Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            //Log.d(TAG, "onEvent " + eventType);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_converser, container, false);
            return rootView;
        }
    }

    private void CreateImageViewFromUrl(String url_image) throws IOException {

        URL Url = null;
        try {
            Url = new URL(url_image);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap Bitmap = null;
        try {
            Bitmap = BitmapFactory.decodeStream(Url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Bitmap Bitmap_ = Bitmap;



        ConverserActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ImageView ImageView = new ImageView(ConverserActivity.this);
                //ImageView.setImageResource(R.drawable.frogdesign);
                //ImageView.setOnTouchListener(OnTouchListener_playSpaceImage);
                RelativeLayout RelativeLayout = (RelativeLayout) ConverserActivity.this.findViewById(R.id.RelativeLayout_mediaCanvas);
                int w_layout = RelativeLayout.getWidth();
                int h_layout = RelativeLayout.getHeight();
                int x = new Random().nextInt(w_layout);
                int y = new Random().nextInt(h_layout);
                ImageView.setScaleType(android.widget.ImageView.ScaleType.CENTER);
                RelativeLayout.addView(ImageView);
                ImageView.setX(x);
                ImageView.setY(y);
                ImageView.setScaleX(3);
                ImageView.setScaleY(3);
                //this.ImageViews_playSpace.add(ImageView);
                //return ImageView;
                ImageView.setImageBitmap(Bitmap_);
                AnimateImageView(ImageView);
            }
        });

    }

    private void AnimateImageView(ImageView ImageView)
    {
        RelativeLayout RelativeLayout = (RelativeLayout) this.findViewById(R.id.RelativeLayout_mediaCanvas);
        int w_layout = RelativeLayout.getWidth();
        int h_layout = RelativeLayout.getHeight();
        int w_imageView = ImageView.getDrawable().getIntrinsicWidth();
        int h_imageView = ImageView.getDrawable().getIntrinsicHeight();
        int x_start = (int)ImageView.getX();
        int y_start = (int)ImageView.getY();
        int x_stop = new Random().nextInt(w_layout) - w_imageView/2;
        int y_stop = new Random().nextInt(h_layout) - h_imageView/2;
        int rotationZ_start = (int)ImageView.getRotation();
        int rotationZ_stop = new Random().nextInt(35) - 17;
        int rotationX_start = (int)ImageView.getRotationX();
        int rotationX_stop = new Random().nextInt(35) - 17;
        int rotationY_start = (int)ImageView.getRotationY();
        int rotationY_stop = new Random().nextInt(35) - 17;

        ObjectAnimator ObjectAnimator_translateX = ObjectAnimator.ofFloat(ImageView, "translationX", x_start, x_stop);
        ObjectAnimator_translateX.setDuration(2000);
        ObjectAnimator ObjectAnimator_translateY = ObjectAnimator.ofFloat(ImageView, "translationY", y_start, y_stop);
        ObjectAnimator_translateY.setDuration(3000);
        ObjectAnimator ObjectAnimator_rotateZ= ObjectAnimator.ofFloat(ImageView,  "rotation", rotationZ_start, rotationZ_stop);
        ObjectAnimator_rotateZ.setDuration(2500);
        ObjectAnimator ObjectAnimator_rotateX = ObjectAnimator.ofFloat(ImageView, "rotationX", rotationX_start, rotationX_stop);
        ObjectAnimator_rotateX.setDuration(3500);
        ObjectAnimator ObjectAnimator_rotateY = ObjectAnimator.ofFloat(ImageView, "rotationY", rotationY_start, rotationY_stop);
        ObjectAnimator_rotateY.setDuration(6000);

        ObjectAnimator_rotateY.addListener(AnimatorListener_mediaCanvasImage);

        AnimatorSet AnimatorSet = new AnimatorSet();
        AnimatorSet.playTogether(ObjectAnimator_translateX, ObjectAnimator_translateY, ObjectAnimator_rotateZ, ObjectAnimator_rotateX, ObjectAnimator_rotateY);
        AnimatorSet.start();
    }

    private Animator.AnimatorListener AnimatorListener_mediaCanvasImage = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationCancel(Animator arg0)
        {
        }

        @Override
        public void onAnimationEnd(Animator Animator)
        {
            ImageView ImageView = (ImageView) ((ObjectAnimator) Animator).getTarget();
            AnimateImageView(ImageView);
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
