package com.foo.botimer;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by hilo on 3/29/14.
 */
public class Speaker
{

    private ConverserActivity converserActivity;
    private TextToSpeech textToSpeech;
    private HashMap<String, String> TtsUtteranceMap;

    public Speaker(ConverserActivity converserActivity)
    {
        this.converserActivity = converserActivity;

        this.Init();
    }

    private void Init()
    {
        this.CreateTextToSpeech();
    }

    private void CreateTextToSpeech()
    {
        this.TtsUtteranceMap = new HashMap<String, String>();
        this.textToSpeech = new TextToSpeech(this.converserActivity, new TtsInitListener(this));
        this.textToSpeech.setOnUtteranceProgressListener(new TtsUtteranceListener(this));
    }

    public void Speak(String textToSpeak)
    {
        Log.d("foo", "speak:    " + textToSpeak);
        this.TtsUtteranceMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        this.textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_ADD, this.TtsUtteranceMap);
    }

    public void OnStart_ttsSpeak()
    {
        this.converserActivity.OnStart_ttsSpeak();
    }

    public void OnDone_ttsSpeak()
    {
        this.converserActivity.OnDone_ttsSpeak();
    }
}

class TtsInitListener implements TextToSpeech.OnInitListener
{

    private Speaker speaker;

    public TtsInitListener(Speaker speaker)
    {
        this.speaker = speaker;
    }

    @Override
    public void onInit(int status)
    {

        if (status == TextToSpeech.SUCCESS) {}
        else {}
    }
}

class TtsUtteranceListener extends UtteranceProgressListener
{

    private Speaker speaker;

    public TtsUtteranceListener(Speaker speaker)
    {
        this.speaker = speaker;
    }

    @Override
    public void onDone(String utteranceId)
    {
        this.speaker.OnDone_ttsSpeak();
    }

    @Override
    public void onError(String utteranceId) {
    }

    @Override
    public void onStart(String utteranceId)
    {
        this.speaker.OnStart_ttsSpeak();
    }
}

