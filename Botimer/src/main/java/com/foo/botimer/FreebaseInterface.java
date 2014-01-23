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
import java.util.Random;

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


    private void OnComplete_findFreebaseNodeDataForInputText(FreebaseNodeData FreebaseNodeData)
    {
        this.ConverserActivity.OnComplete_findFreebaseNodeDataForInputText(FreebaseNodeData);
    }


    /////////////////////////////////////
    //utilities
    /////////////////////////////////////

    public void FindFreebaseNodeDataForInputText(String inputText)
    {
        this.ConverserActivity.PrintToDebugOutput("finding freebase node for:  " + inputText);
        final String inputText_ = inputText.replace(" ", "_").toString();
        new Thread(new Runnable(){

            String inputText__ = inputText_;

            @Override
            public void run()
            {
                try
                {
                    JSONArray TopicData = FindTopicDataForInputText(inputText__);
                    int numTopics = TopicData.length();
                    int index_rand = new Random().nextInt(numTopics);
                    JSONObject TopicDatum = new JSONObject(TopicData.get(index_rand).toString());
                    FreebaseNodeData FreebaseNodeData = CreateFreebaseNodeDataForTopicDatum(TopicDatum);
                    OnComplete_findFreebaseNodeDataForInputText(FreebaseNodeData);

                    //for (int i=0; i<numTopics; i++)
                    //{
                    //    JSONObject TopicDatum = new JSONObject(TopicData.get(i).toString());
                    //    FreebaseNodeData FreebaseNodeData = CreateFreebaseNodeDataForTopicDatum(TopicDatum);
                    //    OnComplete_findFreebaseNodeDataForInputText(FreebaseNodeData);
                    //}

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
            this.ConverserActivity.PrintToDebugOutput("found topic:  " + name);
        }

        return TopicData;
    }

    private FreebaseNodeData CreateFreebaseNodeDataForTopicDatum(JSONObject TopicDatum) throws JSONException, IOException {

        //get the topic id
        String id_topic = TopicDatum.get("id").toString();

        //get the topic name
        String name = TopicDatum.get("name").toString();

        //get an image for this topic
        String url_image = this.FindImageForTopic(id_topic);
        Log.d("foo", url_image);

        //get the article text for this topic
        String text = this.FindTextForTopic(id_topic);

        //package the data
        FreebaseNodeData FreebaseNodeData = new FreebaseNodeData(name, id_topic, url_image, text);

        return FreebaseNodeData;
    }

    private String FindImageForTopic(String id_topic) throws IOException, JSONException {
        String url_base = "https://www.googleapis.com/freebase/v1/topic";
        String url_base_withTopicId = url_base + id_topic;
        String filter = "/common/topic/image&limit=1";
        GenericUrl url = new GenericUrl(url_base_withTopicId);
        url.put("key", "AIzaSyAhwf40hmgjrTc57ije8rqorJ6x-8hKFXE");
        url.put("filter", filter);
        HttpTransport httpTransport = new NetHttpTransport();
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
        HttpRequest request= requestFactory.buildGetRequest(url);
        HttpResponse httpResponse = request.execute();
        String json = httpResponse.parseAsString();
        JSONObject Blob = new org.json.JSONObject(json);
        String id_image = Blob.get("id").toString();
        String url_base_image = "https://usercontent.googleapis.com/freebase/v1/image";
        String url_image = url_base_image + id_image;
        this.ConverserActivity.PrintToDebugOutput("found image:  " + url_image);
        return url_image;
    }

    private String FindTextForTopic(String id_topic) throws IOException, JSONException {
        String url_base = "https://www.googleapis.com/freebase/v1/text";
        String url_base_withTopicId = url_base + id_topic;
        GenericUrl url = new GenericUrl(url_base_withTopicId);
        url.put("key", "AIzaSyAhwf40hmgjrTc57ije8rqorJ6x-8hKFXE");
        HttpTransport httpTransport = new NetHttpTransport();
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(url);
        HttpResponse httpResponse_image = request.execute();
        String json = httpResponse_image.parseAsString();
        JSONObject Blob = new org.json.JSONObject(json);
        String text = Blob.get("result").toString();
        this.ConverserActivity.PrintToDebugOutput("found text:  " + text);
        return text;
    }

    class FreebaseNodeData
    {
        public String name;
        public String id_topic;
        public String url_image;
        public String text;

        public FreebaseNodeData(String name, String id_topic, String url_image, String text)
        {
            this.name = name;
            this.id_topic = id_topic;
            this.url_image = url_image;
            this.text = text;
        }
    }

}




