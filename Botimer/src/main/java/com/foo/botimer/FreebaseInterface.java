package com.foo.botimer;

/**
 * Created by matthew.sibigtroth on 1/9/14.
 */





import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;


import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.support.v7.app.ActionBarActivity;

public class FreebaseInterface
{

    private ConverserActivity ConverserActivity;

    public FreebaseInterface(ConverserActivity ParentActivity)
    {
        this.ConverserActivity = ParentActivity;

        this.Init();
    }

    private void Init()
    {

    }

    /////////////////////////////////////
    //callbacks
    /////////////////////////////////////

    private void OnComplete_findImageForInputText(String url_image) throws IOException {
        this.ConverserActivity.OnComplete_findImageForInputText(url_image);
    }


    /////////////////////////////////////
    //utilities
    /////////////////////////////////////

    public void FindImageForInputText(String inputText)
    {
        final String inputText_ = inputText.toString();
        new Thread(new Runnable(){

            String inputText__ = inputText_;

            @Override
            public void run()
            {
                try
                {
                    JSONArray TopicData = FindTopicDataForInputText(inputText__);
                    JSONObject TopicDatum = new JSONObject(TopicData.get(0).toString());
                    String url_image = FindImageForTopicDatum(TopicDatum);
                    OnComplete_findImageForInputText(url_image);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private JSONArray FindTopicDataForInputText(String inputText) throws IOException, JSONException
    {
        HttpTransport httpTransport = new NetHttpTransport();
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();

        String query = inputText.toString();
        GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/search");
        url.put("key", "AIzaSyAhwf40hmgjrTc57ije8rqorJ6x-8hKFXE");
        url.put("query", query);

        HttpRequest request = requestFactory.buildGetRequest(url);
        HttpResponse httpResponse = request.execute();
        String json = httpResponse.parseAsString();

        JSONObject Blob = new org.json.JSONObject(json);
        JSONArray TopicData = Blob.getJSONArray("result");

        for (int i=0; i<TopicData.length(); i++)
        {
            JSONObject TopicDatum = new JSONObject(TopicData.get(i).toString());
            String name = TopicDatum.get("name").toString();
            Log.d("foo", name);
        }

        return TopicData;
    }

    private String FindImageForTopicDatum(JSONObject TopicDatum) throws JSONException, IOException
    {
        String id_topic = TopicDatum.get("id").toString();

        HttpTransport httpTransport = new NetHttpTransport();
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();

        String url_base = "https://www.googleapis.com/freebase/v1/topic";
        String url_base_withTopicId = url_base + id_topic;
        String filter = "/common/topic/image&limit=1";

        GenericUrl url = new GenericUrl(url_base_withTopicId);
        url.put("key", "AIzaSyAhwf40hmgjrTc57ije8rqorJ6x-8hKFXE");
        url.put("filter", filter);

        //Log.d("foo", url.toString());

        HttpRequest request = requestFactory.buildGetRequest(url);
        HttpResponse httpResponse = request.execute();
        String json = httpResponse.parseAsString();

        JSONObject Blob = new org.json.JSONObject(json);
        String id_image = Blob.get("id").toString();

        String url_base_image = "https://usercontent.googleapis.com/freebase/v1/image";
        String url_image = url_base_image + id_image;

        return url_image;
    }
}




