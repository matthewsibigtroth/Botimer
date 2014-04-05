package com.foo.botimer;




/*

TODO:
other bot brains
on touch down for nodedisplays allow them to drag, then reanimate on touch up
photo object recognition ("what do you see?")
palette extraction and display ("what color is this?") (maybe even hex to text?)
external sensors

 */


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.speech.tts.TextToSpeech;
import android.content.Intent;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
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
import com.foo.botimer.FreebaseInterface.FreebaseNodeData;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import android.media.MediaPlayer;


public class ConverserActivity extends Activity
{

    public FreebaseInterface freebaseInterface;
    private Random Random;
    private LinearLayout AdminView;
    private Button ListenButton;
    private ListView DebugListView;
    private ArrayList<String> DebugOutput;
    private ArrayAdapter<String> DebugArrayAdapter;
    private GestureDetector GestureDetector_this;
    private MediaPlayer MediaPlayer;
    private Listener listener;
    private Speaker speaker;
    private ThinkingDisplay thinkingDisplay;
    private RelativeLayout topContainer;
    public int W_SCREEN;
    public int H_SCREEN;
    private MediaDisplay mediaDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_converser);

        this.Init();
    }

    private void Init()
    {
        this.Random = new Random();
        this.topContainer = (RelativeLayout) this.findViewById(R.id.topContainer);
        this.W_SCREEN = getWindowManager().getDefaultDisplay().getWidth();
        this.H_SCREEN = getWindowManager().getDefaultDisplay().getHeight();

        this.CreateAdminView();
        this.CreateListenButton();
        this.CreateDebugListView();
        this.CreateFreebaseInterface();
        this.CreateThisGestureDetector();
        this.CreateSpeaker();
        this.CreateListener();
        this.CreateThinkingDisplay();
        this.CreateMediaDisplay();
    }

    private void CreateFreebaseInterface()
    {
        this.freebaseInterface = new FreebaseInterface(this);
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
        this.topContainer.addView(this.AdminView);
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

    private void CreateThisGestureDetector()
    {
        this.GestureDetector_this = new GestureDetector(new GestureListener_this());
        findViewById(R.id.topContainer).setOnTouchListener(this.OnTouchListener_this);
    }

    private void CreateSpeaker()
    {
        this.speaker = new Speaker(this);
    }

    private void CreateListener()
    {
        this.listener = new Listener(this);
    }

    private void CreateThinkingDisplay()
    {
        this.thinkingDisplay = new ThinkingDisplay(this);
        this.topContainer.addView(this.thinkingDisplay);
    }

    private void CreateMediaDisplay()
    {
        this.mediaDisplay = new MediaDisplay(this);
        this.topContainer.addView(this.mediaDisplay);
    }


    /////////////////////////////////////
    //callbacks
    /////////////////////////////////////


    private View.OnClickListener OnClick_listenButton = new View.OnClickListener()
    {
        @Override
        public void onClick(View view) {
            //listener.Listen();
            dispatchTakePictureIntent();
        }
    };

    public void OnComplete_findFreebaseNodeDataForInputText(FreebaseNodeData freebaseNodeData, String inputText)
    {
        if (freebaseNodeData != null)
        {
            this.PrintToDebugOutput("OnComplete_findFreebaseNodeDataForInputText:  found data!");
            this.thinkingDisplay.HideThinkingIndicator();
            this.CreateFreebaseNodeDisplayFromFreebaseNodeData(freebaseNodeData);
            this.SpeakFreebaseNodeText(freebaseNodeData);
        }
        else
        {
            this.PrintToDebugOutput("OnComplete_findFreebaseNodeDataForInputText:  no data found");
            this.thinkingDisplay.HideThinkingIndicator();
            this.SayToBot("Show me " + inputText);
            //this.SayToBot("What do you know about " + inputText);
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

    private View.OnTouchListener OnTouchListener_this = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(final View view, final MotionEvent event) {
            GestureDetector_this.onTouchEvent(event);
            return true;
        }
    };

    public void OnSingleTap_freebaseNodeDisplay(FreebaseNodeDisplay freebaseNodeDisplay)
    {
        this.thinkingDisplay.ShowThinkingIndicator();

        try
        {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.beep_2);
            PlaySound(uri, 0);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        String name = freebaseNodeDisplay.FreebaseNodeData.name.toString();

        this.freebaseInterface.FindRelatedFreebaseNodeDataForInputText(name);
    }

    public void OnFling_freebaseNodeDisplay(FreebaseNodeDisplay freebaseNodeDisplay)
    {
        try
        {
            Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.whip);
            this.PlaySound(uri, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void OnFlingComplete_freebaseNodeDisplay(FreebaseNodeDisplay freebaseNodeDisplay)
    {
        //RelativeLayout RelativeLayout = (RelativeLayout) ConverserActivity.this.findViewById(R.id.RelativeLayout_mediaCanvas);
        //RelativeLayout.removeView(freebaseNodeDisplay);
        this.mediaDisplay.removeView(freebaseNodeDisplay);
    }

    public void OnSpeechRecognized(String recognizedSpeech)
    {
        String hotPhrase = "show me";
        if (recognizedSpeech.contains(hotPhrase))
        {
            int index_tellMeAbout = recognizedSpeech.indexOf(hotPhrase);
            int index_start = index_tellMeAbout + hotPhrase.length();
            int index_stop = recognizedSpeech.length();
            String subString = recognizedSpeech.substring(index_start, index_stop);

            this.thinkingDisplay.ShowThinkingIndicator();
            this.freebaseInterface.FindFreebaseNodeDataForInputText(subString);
        }
        else
        {
            this.SayToBot(recognizedSpeech);
        }
    }

    public void OnStart_ttsSpeak()
    {
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                //UnMuteSystemStream();
                thinkingDisplay.StartAnimatingTtsIndicators();
            }
        });
    }

    public void OnDone_ttsSpeak()
    {
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                thinkingDisplay.StopAnimatingTtsIndicators();
                listener.Listen();
            }
        });
    }


    ///////////////////////////
    //utilities
    ///////////////////////////

    public void PrintToDebugOutput(String textToPrint)
    {

        final String textToPrint_ = textToPrint;
        ConverserActivity.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                DebugOutput.add(textToPrint_.toString());
                DebugArrayAdapter.notifyDataSetChanged();
                DebugListView.setSelection(DebugArrayAdapter.getCount() - 1);
            }
        });

    }

    private void MuteSystemStream()
    {
        AudioManager AudioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        AudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
    }

    private void UnMuteSystemStream()
    {
        AudioManager AudioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        AudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, AudioManager.FLAG_PLAY_SOUND);
    }

    public void SayToBot(String textToSpeak)
    {
        this.listener.StopListening();

        final String textToSpeak_ = textToSpeak;
        this.thinkingDisplay.ShowThinkingIndicator();
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

                    speaker.Speak(speechResponse);
                }
                catch(IOException e)
                {
                    Log.d("foo", e.getMessage());
                }
                thinkingDisplay.HideThinkingIndicator();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                //Show UI (Toast msg here)
            }

        };

        task.execute((Void[])null);
    }

    private void CreateFreebaseNodeDisplayFromFreebaseNodeData(FreebaseNodeData freebaseNodeData)
    {
        if (freebaseNodeData.url_image != "")
        {
            try
            {
                Uri Uri_ = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.space_gun);
                PlaySound(Uri_, 1000);
            } catch (IOException e) {
                e.printStackTrace();
            }

            URL Url = null;
            try {
                Url = new URL(freebaseNodeData.url_image);
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
            final FreebaseNodeData freebaseNodeData_ = freebaseNodeData;


            ConverserActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    FreebaseNodeDisplay freebaseNodeDisplay = new FreebaseNodeDisplay(ConverserActivity.this, freebaseNodeData_);

                    int w_layout = W_SCREEN;
                    int h_layout = H_SCREEN;
                    int padding = 200;
                    int x_min = padding;
                    int x_max = w_layout - padding;
                    int y_min = padding;
                    int y_max = h_layout - padding;
                    int x = new Random().nextInt(x_max - x_min + 1) + x_min;
                    int y = new Random().nextInt(y_max - y_min + 1) + y_min;
                    freebaseNodeDisplay.ImageView.setScaleType(android.widget.ImageView.ScaleType.CENTER);
                    mediaDisplay.addView(freebaseNodeDisplay);
                    freebaseNodeDisplay.setX(x);
                    freebaseNodeDisplay.setY(y);

                    freebaseNodeDisplay.ImageView.setImageBitmap(Bitmap_);
                    freebaseNodeDisplay.TextView.setText(freebaseNodeData_.name);
                    mediaDisplay.AnimateFreebaseNodeDisplay(freebaseNodeDisplay);

                    float scaleX_start = .01f;
                    float scaleX_stop = 1f;
                    float scaleY_start = .01f;
                    float scaleY_stop = 1f;

                    freebaseNodeDisplay.setScaleX(scaleX_start);
                    freebaseNodeDisplay.setScaleY(scaleY_start);

                    ObjectAnimator ObjectAnimator_scaleX = ObjectAnimator.ofFloat(freebaseNodeDisplay, "scaleX", scaleX_start, scaleX_stop);
                    ObjectAnimator_scaleX.setDuration(300);
                    ObjectAnimator_scaleX.setStartDelay(250);
                    ObjectAnimator_scaleX.setInterpolator(new DecelerateInterpolator());
                    ObjectAnimator_scaleX.start();

                    ObjectAnimator ObjectAnimator_scaleY = ObjectAnimator.ofFloat(freebaseNodeDisplay, "scaleY", scaleY_start, scaleY_stop);
                    ObjectAnimator_scaleY.setDuration(200);
                    ObjectAnimator_scaleY.start();

                    //AnimatorSet AnimatorSet = new AnimatorSet();
                    //AnimatorSet.playTogether(ObjectAnimator_scaleX);
                    //AnimatorSet.start();
                }
            });
        }
        else
        {
            this.PrintToDebugOutput("url_image was empty");
        }
    }

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

    private void SpeakFreebaseNodeText(FreebaseNodeData freebaseNodeData)
    {
        String text = freebaseNodeData.text;
        String[] Sentences = text.split("\\.");
        String firstSentence = Sentences[0];
        this.speaker.Speak(firstSentence);
    }







    private void Foo()
    {
        //File file = new File("/Users/hilo/Desktop/bike.jpg");
        //Log.d("foo", String.valueOf(file.exists()));

        /*
        try {
            HttpResponse<JsonNode> request = Unirest.post("https://camfind.p.mashape.com/image_requests")
                    .header("X-Mashape-Authorization", "YMQQG7yJ4LsBWIrmnzS19ErBtWOTMHlW")
                    .field("image_request[locale]", "en_US")
                    .field("image_request[image]", file)
                    .asJson();

            int a = 1;


        } catch (UnirestException e) {
            e.printStackTrace();
        }
        */


    }


    private void dispatchTakePictureIntent()
    {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/botimer/images/myImage.jpg");
        Uri outputFileUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, 1);

        //this.Foo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        Uri u;
        //u = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), fi.getAbsolutePath(), null, null));
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/botimer/images/myImage.jpg");
        Log.d("foo", String.valueOf(file.exists()));


    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        int REQUEST_IMAGE_CAPTURE = 1;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");


            ImageView ImageView = new ImageView(ConverserActivity.this);
            //RelativeLayout RelativeLayout = (RelativeLayout) ConverserActivity.this.findViewById(R.id.RelativeLayout_mediaCanvas);

            int w_layout = this.mediaDisplay.getWidth();
            int h_layout = this.mediaDisplay.getHeight();
            int x = new Random().nextInt(w_layout);
            int y = new Random().nextInt(h_layout);
            ImageView.setScaleType(android.widget.ImageView.ScaleType.CENTER);
            this.mediaDisplay.addView(ImageView);
            ImageView.setX(x);
            ImageView.setY(y);
            ImageView.setImageBitmap(imageBitmap);
            //AnimateImageView(ImageView);
        }

    }
    */










    private void PlaySound(Uri Uri, int delay) throws IOException
    {
        final Uri Uri_ = Uri;
        final int delay_ = delay;
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                //UnMuteSystemStream();

                try {
                    Thread.sleep(delay_);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MediaPlayer = new MediaPlayer();
                MediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    //MediaPlayer.setDataSource(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.whip));
                    MediaPlayer.setDataSource(getApplicationContext(), Uri_);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    MediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MediaPlayer.start();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
    }
}
