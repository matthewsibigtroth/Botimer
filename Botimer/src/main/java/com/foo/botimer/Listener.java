package com.foo.botimer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by hilo on 3/29/14.
 */
public class Listener
{

    private ConverserActivity converserActivity;
    public SpeechRecognizer speechRecognizer;
    private RecognitionListenerExtended recognitionListenerExtended;

    public Listener(ConverserActivity converserActivity)
    {
        this.converserActivity = converserActivity;

        this.Init();
    }

    private void Init()
    {
        this.CreateSpeechRecognizer();
    }


    private void CreateSpeechRecognizer()
    {
        this.speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.converserActivity);
        this.recognitionListenerExtended = new RecognitionListenerExtended(this);
        this.speechRecognizer.setRecognitionListener(this.recognitionListenerExtended);
        this.Listen();
    }


    ///////////////////////////
    //callbacks
    ///////////////////////////

    public void OnSpeechRecognized(String speechRecognized)
    {
        this.converserActivity.OnSpeechRecognized(speechRecognized);
    }


    ///////////////////////////
    //utilities
    ///////////////////////////

    public void Listen()
    {
        //this.converserActivity.MuteSystemStream();

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        this.recognitionListenerExtended.shouldContinuoslyListen = true;
        this.speechRecognizer.startListening(intent);
    }

    public void StopListening()
    {
        //this.converserActivity.UnMuteSystemStream();

        this.converserActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                recognitionListenerExtended.shouldContinuoslyListen = false;
                speechRecognizer.stopListening();
            }
        });
    }
}


class RecognitionListenerExtended implements RecognitionListener
{

    private Listener listener;

    public RecognitionListenerExtended(Listener listener)
    {
        this.listener = listener;
    }

    public boolean shouldContinuoslyListen = false;

    public void onReadyForSpeech(Bundle params) {}

    public void onBeginningOfSpeech() {}

    public void onRmsChanged(float rmsdB) {}

    public void onBufferReceived(byte[] buffer) {}

    public void onEndOfSpeech() {}

    public void onError(int error)
    {
        if ((error != 6) && (error!=7) && (error!=5)) {return;}

        if (shouldContinuoslyListen == true) {this.listener.Listen();}
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
            this.listener.OnSpeechRecognized(str);
            return;
        }
    }

    public void onPartialResults(Bundle partialResults) {}

    public void onEvent(int eventType, Bundle params) {}
}
