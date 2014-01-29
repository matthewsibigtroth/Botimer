package com.foo.botimer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import android.speech.tts.TextToSpeech;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.content.Intent;
import android.view.Window;
import android.widget.ArrayAdapter;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foo.botimer.FreebaseInterface.FreebaseNodeData;


public class ConverserActivity extends Activity {

    private SpeechRecognizer Listener;
    private TextToSpeech Speaker;
    private HashMap<String, String> TtsUtteranceMap;
    private ArrayList<ImageView> TtsIndicators;
    private boolean shouldAnimateTtsIndicators;
    private FreebaseInterface FreebaseInterface;
    private Random Random;
    private GestureDetectorCompat GestureDetectorCompat;
    private LinearLayout AdminView;
    private Button ListenButton;
    private ListView DebugListView;
    private ArrayList<String> DebugOutput;
    private ArrayAdapter<String> DebugArrayAdapter;
    private RecognitionListenerExtended RecognitionListenerExtended;
    private ProgressBar ThinkingIndicator;
    private GestureDetector GestureDetector_freebaseNodeDisplay;
    private GestureDetector GestureDetector_this;
    private FreebaseNodeDisplay FreebaseNodeDisplay_beingTouched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_converser);

        this.Init();
    }

    private void Init()
    {
        this.TtsIndicators = new ArrayList<ImageView>();
        this.shouldAnimateTtsIndicators = false;
        this.Random = new Random();
        this.FreebaseNodeDisplay_beingTouched = null;

        this.CreateAdminView();
        this.CreateListenButton();
        this.CreateDebugListView();
        this.CreateListener();
        this.CreateSpeaker();
        this.CreateThinkingIndicator();
        this.CreateTtsIndicators();
        this.CreateFreebaseInterface();
        this.CreateFreebaseNodeDisplayGestureDetector();
        this.CreateThisGestureDetector();
    }

    private void CreateListener()
    {
        this.Listener = SpeechRecognizer.createSpeechRecognizer(this);
        this.RecognitionListenerExtended = new RecognitionListenerExtended();
        this.Listener.setRecognitionListener(this.RecognitionListenerExtended);
        //disable speechrecognizer beep
        AudioManager AudioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        AudioManager.setStreamMute(AudioManager.VIBRATE_TYPE_NOTIFICATION, true);
        this.Listen();
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
        ImageView TtsIndicator = new ImageView(this);
        TtsIndicator.setImageResource(R.drawable.circle);
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

    private void CreateThinkingIndicator()
    {
        this.ThinkingIndicator = new ProgressBar(this, null, android.R.attr.progressBarStyle);
        RelativeLayout.LayoutParams LayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        LayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        this.ThinkingIndicator.setLayoutParams(LayoutParams);
        this.ThinkingIndicator.getLayoutParams().height = 100;
        this.ThinkingIndicator.getLayoutParams().width = 100;
        this.ThinkingIndicator.setAlpha(0);
        RelativeLayout RelativeLayout_container = new RelativeLayout(this);
        RelativeLayout_container.addView(this.ThinkingIndicator);
        FrameLayout FrameLayout = (FrameLayout) findViewById(R.id.container);
        FrameLayout.addView(RelativeLayout_container);
    }

    private void CreateFreebaseInterface()
    {
        this.FreebaseInterface = new FreebaseInterface(this);
    }


    private void CreateAdminView()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int w_screen = size.x;
        int h_screen = size.y;
        this.AdminView = new LinearLayout(this);
        LinearLayout.LayoutParams LayoutParams =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.AdminView.setLayoutParams(LayoutParams);
        this.AdminView.getLayoutParams().height = h_screen;
        this.AdminView.getLayoutParams().width = (int)((float)w_screen*.8f);
        this.AdminView.setOrientation(LinearLayout.VERTICAL);
        this.AdminView.setBackgroundColor(0xFF444444);
        this.AdminView.setX(-this.AdminView.getLayoutParams().width);
        this.AdminView.setAlpha(.9f);
        FrameLayout FrameLayout = (FrameLayout) findViewById(R.id.container);
        FrameLayout.addView(this.AdminView);
    }

    private void CreateListenButton()
    {
        this.ListenButton = new Button(this);
        this.ListenButton.setText("Listen");
        this.ListenButton.setOnClickListener(OnClick_listenButton);
        LinearLayout.LayoutParams LayoutParams =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.ListenButton.setLayoutParams(LayoutParams);
        this.AdminView.addView(this.ListenButton);
    }

    private void CreateDebugListView()
    {
        this.DebugListView = new ListView(this);
        LinearLayout.LayoutParams LayoutParams =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.DebugListView.setLayoutParams(LayoutParams);
        this.AdminView.addView(this.DebugListView);
        this.DebugOutput = new ArrayList<String>();
        this.DebugArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.DebugOutput);
        this.DebugListView.setAdapter(this.DebugArrayAdapter);
    }

    private void CreateFreebaseNodeDisplayGestureDetector()
    {
        this.GestureDetector_freebaseNodeDisplay = new GestureDetector(new GestureListener_freebaseNodeDisplay());
    }

    private void CreateThisGestureDetector()
    {
        this.GestureDetector_this = new GestureDetector(new GestureListener_this());
        findViewById(R.id.container).setOnTouchListener(this.OnTouchListener_this);
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
            FreebaseInterface.FindFreebaseNodeDataForInputText("cat");
            //Speak("this is a test");
            //dispatchTakePictureIntent();
        }
    };

    /*
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
    */

    private void OnSpeechRecognized(String speechRecognized)
    {
        Log.d("foo", "heard " + speechRecognized);
        this.PrintToDebugOutput("speech recognizer heard:  " + speechRecognized);

        String hotPhrase = "show me";
        if (speechRecognized.contains(hotPhrase))
        {
            int index_tellMeAbout = speechRecognized.indexOf(hotPhrase);
            int index_start = index_tellMeAbout + hotPhrase.length();
            int index_stop = speechRecognized.length();
            String subString = speechRecognized.substring(index_start, index_stop);
            this.PrintToDebugOutput("content subString is:  " + subString);
            this.ShowThinkingIndicator();
            this.FreebaseInterface.FindFreebaseNodeDataForInputText(subString);
        }
        else
        {
            this.SayToBot(speechRecognized);
        }
    }

    public void OnComplete_findFreebaseNodeDataForInputText(FreebaseNodeData FreebaseNodeData)
    {
        this.PrintToDebugOutput("OnComplete_findFreebaseNodeDataForInputText");
        this.HideThinkingIndicator();
        this.CreateImageViewFromFreebaseNodeData(FreebaseNodeData);
        this.SpeakFreebaseNodeText(FreebaseNodeData);
    }





    private class GestureListener_freebaseNodeDisplay extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            Log.d("foo", "onFling_freebaseNodeDisplay");
            //Log.d("foo", String.valueOf(e1.getSource()));

            float x_start = FreebaseNodeDisplay_beingTouched.getX();
            float y_start = FreebaseNodeDisplay_beingTouched.getY();
            float x_stop = x_start + velocityX/2f;
            float y_stop = y_start + velocityY/2f;

            Log.d("foo", String.valueOf(Math.sqrt(velocityX*velocityX + velocityY*velocityY) ));

            if (Math.sqrt(velocityX*velocityX + velocityY*velocityY) > 2500)
            {
                Log.d("foo", "velocityX:   " + String.valueOf(velocityX));
                Log.d("foo", "velocityY:   " + String.valueOf(velocityY));

                ObjectAnimator ObjectAnimator_x = ObjectAnimator.ofFloat(FreebaseNodeDisplay_beingTouched, "x", x_start, x_stop);
                ObjectAnimator_x.setDuration(400);
                ObjectAnimator ObjectAnimator_y = ObjectAnimator.ofFloat(FreebaseNodeDisplay_beingTouched, "y", y_start, y_stop);
                ObjectAnimator_y.setDuration(400);
                ObjectAnimator_y.addListener(AnimatorListener_freebaseNodeDisplay_fling);
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

            Log.d("foo", "onSingleTapConfirmed");


            String name = FreebaseNodeDisplay_beingTouched.FreebaseNodeData.name.toString();
            PrintToDebugOutput("onTouch freebaseNodeDisplay:  " + name);
            ShowThinkingIndicator();
            ConverserActivity.this.FreebaseInterface.FindRelatedFreebaseNodeDataForInputText(name);
            FadeOutFreebaseNodeDisplay(FreebaseNodeDisplay_beingTouched);

            return false;
        }
    }

    private class GestureListener_this extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            if (velocityX < 0) { OnFlingLeft_this(); }
            else { OnFlingRight_this(); }
            return false;
        }
    }

    private void OnFlingLeft_this()
    {
        this.HideAdminView();
    }

    private void OnFlingRight_this()
    {
        this.ShowAdminView();
    }

    private void OnError_recognitionListener()
    {
        this.Listen();
    }

    private View.OnTouchListener OnTouchListener_freebaseNodeDisplay = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(final View view, final MotionEvent event) {
            FreebaseNodeDisplay_beingTouched = (FreebaseNodeDisplay)view;
            GestureDetector_freebaseNodeDisplay.onTouchEvent(event);
            return true;
        }
    };

    private View.OnTouchListener OnTouchListener_this = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(final View view, final MotionEvent event) {
            GestureDetector_this.onTouchEvent(event);
            return true;
        }
    };

    private Animator.AnimatorListener AnimatorListener_freebaseNodeDisplay_fling = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationCancel(Animator arg0)
        {
        }

        @Override
        public void onAnimationEnd(Animator Animator)
        {
            //ImageView TtsIndicator = (ImageView) ((ObjectAnimator) Animator).getTarget();
            //ScaleDownTtsIndicator(TtsIndicator);

            FreebaseNodeDisplay FreebaseNodeDisplay = (FreebaseNodeDisplay) ((ObjectAnimator) Animator).getTarget();
            RelativeLayout RelativeLayout = (RelativeLayout) ConverserActivity.this.findViewById(R.id.RelativeLayout_mediaCanvas);
            RelativeLayout.removeView(FreebaseNodeDisplay);
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

    ///////////////////////////
    //utilities
    ///////////////////////////

    public void PrintToDebugOutput(String textToPrint)
    {
        final String textToPrint_ = textToPrint;
        ConverserActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DebugOutput.add(textToPrint_.toString());
                DebugArrayAdapter.notifyDataSetChanged();
                DebugListView.setSelection(DebugArrayAdapter.getCount() - 1);
            }
        });
    }

    private void Listen()
    {
        Log.d("foo", "StartListening");
        //this.PrintToDebugOutput("listening...");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        this.RecognitionListenerExtended.shouldContinuoslyListen = true;
        this.Listener.startListening(intent);
    }

    private void StopListening()
    {
        Log.d("foo", "manual stop of speech reco listening");
        this.PrintToDebugOutput("manual stop of speech reco listening");
        this.RecognitionListenerExtended.shouldContinuoslyListen = false;
        this.Listener.stopListening();
    }

    private void Speak(String textToSpeak)
    {
        Log.d("foo", "speak:    " + textToSpeak);
        this.PrintToDebugOutput("speaking tts:  " + textToSpeak);
        this.TtsUtteranceMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        this.Speaker.speak(textToSpeak, TextToSpeech.QUEUE_ADD, this.TtsUtteranceMap);
    }

    private void SayToBot(String textToSpeak)
    {
        this.StopListening();

        final String textToSpeak_ = textToSpeak;
        this.ShowThinkingIndicator();
        this.PrintToDebugOutput("saying to pandorabot:  " + textToSpeak);

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
                HideThinkingIndicator();
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
            float randNum = this.Random.nextFloat();
            if (randNum < .25)
            {
                ImageView TtsIndicator = this.TtsIndicators.get(i);
                this.ScaleUpTtsIndicator(TtsIndicator);
            }
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

        public boolean shouldContinuoslyListen = false;

        public void onReadyForSpeech(Bundle params)
        {
            Log.d("foo", "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
            Log.d("foo", "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
            //Log.d("foo", "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            Log.d("foo", "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            Log.d("foo", "onEndofSpeech");
        }
        public void onError(int error)
        {
            Log.d("foo",  "error " +  error);
            //mText.setText("error " + error);

            if (error != 6) {return;}

            //DebugOutput("stopped listening from no audio input");
            if (shouldContinuoslyListen == true) {Listen();}
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

    private void CreateImageViewFromFreebaseNodeData(FreebaseNodeData FreebaseNodeData)
    {
        if (FreebaseNodeData.url_image != "")
        {
            URL Url = null;
            try {
                Url = new URL(FreebaseNodeData.url_image);
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
            final FreebaseNodeData FreebaseNodeData_ = FreebaseNodeData;


            ConverserActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    FreebaseNodeDisplay FreebaseNodeDisplay = new FreebaseNodeDisplay(ConverserActivity.this, FreebaseNodeData_);
                    FreebaseNodeDisplay.setOnTouchListener(OnTouchListener_freebaseNodeDisplay);
                    RelativeLayout RelativeLayout = (RelativeLayout) ConverserActivity.this.findViewById(R.id.RelativeLayout_mediaCanvas);
                    int w_layout = RelativeLayout.getWidth();
                    int h_layout = RelativeLayout.getHeight();
                    int x = new Random().nextInt(w_layout);
                    int y = new Random().nextInt(h_layout);
                    FreebaseNodeDisplay.ImageView.setScaleType(android.widget.ImageView.ScaleType.CENTER);
                    RelativeLayout.addView(FreebaseNodeDisplay);
                    FreebaseNodeDisplay.setX(x);
                    FreebaseNodeDisplay.setY(y);
                    Log.d("foo", "imageview id   " + FreebaseNodeDisplay.getId());
                    FreebaseNodeDisplay.ImageView.setImageBitmap(Bitmap_);
                    FreebaseNodeDisplay.TextView.setText(FreebaseNodeData_.name);
                    AnimateFreebaseNodeDisplay(FreebaseNodeDisplay);
                }
            });
        }
        else
        {

        }
    }

    class FreebaseNodeDisplay extends LinearLayout
    {

        public FreebaseNodeData FreebaseNodeData;
        public ImageView ImageView;
        public TextView TextView;

        public FreebaseNodeDisplay(Context Context, FreebaseNodeData FreebaseNodeData)
        {
            super(Context);

            this.FreebaseNodeData = FreebaseNodeData;

            this.Init();
        }

        private void Init()
        {
            this.setOrientation(VERTICAL);
            this.CreateItemImageView();
            this.CreateNameTextView();
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
    }

    private void AnimateFreebaseNodeDisplay(FreebaseNodeDisplay FreebaseNodeDisplay)
    {
        RelativeLayout RelativeLayout = (RelativeLayout) this.findViewById(R.id.RelativeLayout_mediaCanvas);
        int w_layout = RelativeLayout.getWidth();
        int h_layout = RelativeLayout.getHeight();
        int w_imageView = FreebaseNodeDisplay.ImageView.getDrawable().getIntrinsicWidth();
        int h_imageView = FreebaseNodeDisplay.ImageView.getDrawable().getIntrinsicHeight();
        int x_start = (int)FreebaseNodeDisplay.ImageView.getX();
        int y_start = (int)FreebaseNodeDisplay.ImageView.getY();
        int x_stop = new Random().nextInt(w_layout) - w_imageView/2;
        int y_stop = new Random().nextInt(h_layout) - h_imageView/2;
        int rotationZ_start = (int)FreebaseNodeDisplay.ImageView.getRotation();
        int rotationZ_stop = new Random().nextInt(35) - 17;
        int rotationX_start = (int)FreebaseNodeDisplay.ImageView.getRotationX();
        int rotationX_stop = new Random().nextInt(35) - 17;
        int rotationY_start = (int)FreebaseNodeDisplay.ImageView.getRotationY();
        int rotationY_stop = new Random().nextInt(35) - 17;

        ObjectAnimator ObjectAnimator_translateX = ObjectAnimator.ofFloat(FreebaseNodeDisplay, "translationX", x_start, x_stop);
        ObjectAnimator_translateX.setDuration(40000);
        ObjectAnimator ObjectAnimator_translateY = ObjectAnimator.ofFloat(FreebaseNodeDisplay, "translationY", y_start, y_stop);
        ObjectAnimator_translateY.setDuration(60000);
        ObjectAnimator ObjectAnimator_rotateZ= ObjectAnimator.ofFloat(FreebaseNodeDisplay,  "rotation", rotationZ_start, rotationZ_stop);
        ObjectAnimator_rotateZ.setDuration(50000);
        ObjectAnimator ObjectAnimator_rotateX = ObjectAnimator.ofFloat(FreebaseNodeDisplay, "rotationX", rotationX_start, rotationX_stop);
        ObjectAnimator_rotateX.setDuration(70000);
        ObjectAnimator ObjectAnimator_rotateY = ObjectAnimator.ofFloat(FreebaseNodeDisplay, "rotationY", rotationY_start, rotationY_stop);
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

    private void FadeOutFreebaseNodeDisplay(FreebaseNodeDisplay FreebaseNodeDisplay)
    {
        float alpha_start = (int)FreebaseNodeDisplay.getAlpha();
        float alpha_stop = 0;

        ObjectAnimator ObjectAnimator_alpha = ObjectAnimator.ofFloat(FreebaseNodeDisplay, "alpha", alpha_start, alpha_stop);
        ObjectAnimator_alpha.setDuration(5000);

        ObjectAnimator_alpha.addListener(AnimatorListener_mediaCanvasImage_fadeOut);

        AnimatorSet AnimatorSet = new AnimatorSet();
        AnimatorSet.playTogether(ObjectAnimator_alpha);
        AnimatorSet.start();
    }

    private Animator.AnimatorListener AnimatorListener_mediaCanvasImage_fadeOut = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationCancel(Animator arg0)
        {
        }

        @Override
        public void onAnimationEnd(Animator Animator)
        {
            FreebaseNodeDisplay FreebaseNodeDisplay = (FreebaseNodeDisplay) ((ObjectAnimator) Animator).getTarget();
            RelativeLayout RelativeLayout = (RelativeLayout) ConverserActivity.this.findViewById(R.id.RelativeLayout_mediaCanvas);
            RelativeLayout.removeView(FreebaseNodeDisplay);
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


    private void HideAdminView()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int w_screen = size.x;

        LinearLayout.LayoutParams LayoutParams =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        int duration  = 500;
        float x_start = this.AdminView.getX();
        float x_stop = -this.AdminView.getLayoutParams().width;
        ObjectAnimator ObjectAnimator_x = ObjectAnimator.ofFloat(this.AdminView, "x", x_start, x_stop);
        ObjectAnimator_x.setDuration(duration);
        ObjectAnimator_x.start();
    }

    private void ShowAdminView()
    {
        int duration  = 500;
        float x_start = this.AdminView.getX();
        float x_stop = 0;
        ObjectAnimator ObjectAnimator_x = ObjectAnimator.ofFloat(this.AdminView, "x", x_start, x_stop);
        ObjectAnimator_x.setDuration(duration);
        ObjectAnimator_x.start();
    }

    private void SpeakFreebaseNodeText(FreebaseNodeData FreebaseNodeData)
    {
        String text = FreebaseNodeData.text;
        String[] Sentences = text.split("\\.");
        String firstSentence = Sentences[0];
        this.Speak(firstSentence);
    }

    private void ShowThinkingIndicator()
    {
        ConverserActivity.this.runOnUiThread(new Runnable() {
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

    private void HideThinkingIndicator()
    {
        ConverserActivity.this.runOnUiThread(new Runnable() {
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

    private void dispatchTakePictureIntent()
    {
        int REQUEST_IMAGE_CAPTURE = 1;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        int REQUEST_IMAGE_CAPTURE = 1;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");


            ImageView ImageView = new ImageView(ConverserActivity.this);
            RelativeLayout RelativeLayout = (RelativeLayout) ConverserActivity.this.findViewById(R.id.RelativeLayout_mediaCanvas);
            int w_layout = RelativeLayout.getWidth();
            int h_layout = RelativeLayout.getHeight();
            int x = new Random().nextInt(w_layout);
            int y = new Random().nextInt(h_layout);
            ImageView.setScaleType(android.widget.ImageView.ScaleType.CENTER);
            RelativeLayout.addView(ImageView);
            ImageView.setX(x);
            ImageView.setY(y);
            ImageView.setImageBitmap(imageBitmap);
            //AnimateImageView(ImageView);
        }
    }
}
