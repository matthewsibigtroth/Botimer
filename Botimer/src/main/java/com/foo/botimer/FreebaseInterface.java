package com.foo.botimer;

/**
 * Created by matthew.sibigtroth on 1/9/14.
 */





import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
        this.ConverserActivity.PrintToDebugOutput("FindFreebaseNodeDataForInputText");
        ConverserActivity.PrintToDebugOutput(" * ");
        ConverserActivity.PrintToDebugOutput(" * ");
        ConverserActivity.PrintToDebugOutput(" * ");
        ConverserActivity.PrintToDebugOutput(" * ");
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
                    JSONObject TopicDatum = new JSONObject(TopicData.get(0).toString());
                    FreebaseNodeData FreebaseNodeData = CreateFreebaseNodeDataForTopicDatum(TopicDatum);
                    OnComplete_findFreebaseNodeDataForInputText(FreebaseNodeData);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void FindRelatedFreebaseNodeDataForInputText(String inputText)
    {
        this.ConverserActivity.PrintToDebugOutput("FindRelatedFreebaseNodeDataForInputText");
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
                    ConverserActivity.PrintToDebugOutput("numTopics:  " + String.valueOf(numTopics) + "   index_rand:   " + String.valueOf(index_rand));
                    JSONObject TopicDatum = new JSONObject(TopicData.get(index_rand).toString());
                    ConverserActivity.PrintToDebugOutput(" topic datum json:   " + TopicData.get(index_rand).toString());
                    ConverserActivity.PrintToDebugOutput(" _ ");
                    ConverserActivity.PrintToDebugOutput(" _ ");
                    ConverserActivity.PrintToDebugOutput(" _ ");
                    ConverserActivity.PrintToDebugOutput(" _ ");
                    //JSONObject TopicDatum = new JSONObject(TopicData.get(1).toString());
                    FreebaseNodeData FreebaseNodeData = CreateFreebaseNodeDataForTopicDatum(TopicDatum);
                    OnComplete_findFreebaseNodeDataForInputText(FreebaseNodeData);
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
        this.ConverserActivity.PrintToDebugOutput("FindTopicDataForInputText");
        HttpTransport httpTransport = new NetHttpTransport();
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();

        String query = inputText.toString();
        GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/search");
        url.put("key", "AIzaSyAhwf40hmgjrTc57ije8rqorJ6x-8hKFXE");
        url.put("query", query);

        HttpRequest request = requestFactory.buildGetRequest(url);
        HttpResponse httpResponse = request.execute();
        String json = httpResponse.parseAsString();

        //this.ConverserActivity.PrintToDebugOutput("topic data json: " + json);
        JSONObject Blob = new org.json.JSONObject(json);
        JSONArray TopicData = Blob.getJSONArray("result");

        for (int i=0; i<TopicData.length(); i++)
        {
            JSONObject TopicDatum = new JSONObject(TopicData.get(i).toString());
            String name = TopicDatum.get("name").toString();
            //Log.d("foo", name);
            //this.ConverserActivity.PrintToDebugOutput("found topic:  " + name);
        }

        return TopicData;
    }

    private FreebaseNodeData CreateFreebaseNodeDataForTopicDatum(JSONObject TopicDatum) throws JSONException, IOException {

        this.ConverserActivity.PrintToDebugOutput("CreateFreebaseNodeDataForTopicDatum");
        //get the topic id
        String id_topic = "";
        try {id_topic = TopicDatum.get("id").toString();}
        catch (Exception e) {id_topic = TopicDatum.get("mid").toString().replace("\\","");}
        //String id_topic = TopicDatum.get("id").toString();

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

    /*
    private String FindImageForTopic(String id_topic) throws IOException, JSONException {
        this.ConverserActivity.PrintToDebugOutput("id:  " + id_topic);
        String url_base = "https://www.googleapis.com/freebase/v1/topic";
        String url_base_withTopicId = url_base + id_topic;
        String filter = "/common/topic/image&limit=10";
        GenericUrl url = new GenericUrl(url_base_withTopicId);
        url.put("key", "AIzaSyAhwf40hmgjrTc57ije8rqorJ6x-8hKFXE");
        url.put("filter", filter);
        HttpTransport httpTransport = new NetHttpTransport();
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
        HttpRequest request= requestFactory.buildGetRequest(url);
        HttpResponse httpResponse = request.execute();
        String json = httpResponse.parseAsString();
        this.ConverserActivity.PrintToDebugOutput(json);
        JSONObject Blob = new org.json.JSONObject(json);
        String id_image = Blob.get("id").toString();
        String url_base_image = "https://usercontent.googleapis.com/freebase/v1/image";
        int maxwidth = 2000;
        int maxheight = 1000;
        String url_params = "?maxwidth=" + String.valueOf(maxwidth) + "&maxheight=" + String.valueOf(maxheight) + "&mode=fillcropmid";
        String url_image = url_base_image + id_image + url_params;
        if (this.DetermineIfImageExists(url_image) == false) {url_image = null;}
        this.ConverserActivity.PrintToDebugOutput("found image:  " + url_image);
        return url_image;
    }
    */

    private String FindImageForTopic(String id_topic) throws IOException, JSONException
    {
        this.ConverserActivity.PrintToDebugOutput("FindImageForTopic");
        //this.ConverserActivity.PrintToDebugOutput("id:  " + id_topic);
        String url_base = "https://www.googleapis.com/freebase/v1/topic";

        String param_key = "key=AIzaSyAhwf40hmgjrTc57ije8rqorJ6x-8hKFXE";
        String param_filter = "filter=/common/topic/image&limit=10";

        String url = url_base + id_topic + "?" + param_key + "&" + param_filter;

        //this.ConverserActivity.PrintToDebugOutput(url);


        GenericUrl GenericUrl = new GenericUrl(url);
        HttpTransport httpTransport = new NetHttpTransport();
        HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
        HttpRequest request= requestFactory.buildGetRequest(GenericUrl);
        HttpResponse httpResponse = request.execute();
        String json = httpResponse.parseAsString();

        //this.ConverserActivity.PrintToDebugOutput(url.toString());
        this.ConverserActivity.PrintToDebugOutput("imagedata json: " + json);


        String url_image = "";
        try
        {
            JSONObject Blob = new org.json.JSONObject(json);
            JSONObject Property = Blob.getJSONObject("property");
            JSONObject Common_topic_image = Property.getJSONObject("/common/topic/image");
            JSONArray Values = Common_topic_image.getJSONArray("values");

            int numImages = Values.length();
            int index_rand = new Random().nextInt(numImages);
            JSONObject Value = Values.getJSONObject(index_rand);
            String id_image = Value.get("id").toString();
            String url_base_image = "https://usercontent.googleapis.com/freebase/v1/image";
            int maxwidth = 1000;
            int maxheight = 500;
            String params = "?maxwidth=" + String.valueOf(maxwidth) + "&maxheight=" + String.valueOf(maxheight) + "&mode=fillcropmid";
            url_image = url_base_image + id_image + params;
            this.ConverserActivity.PrintToDebugOutput("found image:  " + url_image);

            /*
            for (int i=0; i<Values.length(); i++)
            {
                this.ConverserActivity.PrintToDebugOutput("trying image:  " + String.valueOf(i));
                JSONObject Value = Values.getJSONObject(i);
                String id_image = Value.get("id").toString();
                String url_base_image = "https://usercontent.googleapis.com/freebase/v1/image";
                int maxwidth = 2000;
                int maxheight = 1000;
                String params = "?maxwidth=" + String.valueOf(maxwidth) + "&maxheight=" + String.valueOf(maxheight) + "&mode=fillcropmid";
                url_image = url_base_image + id_image + params;
                if (this.DetermineIfImageExists(url_image) == true) {break;}
                this.ConverserActivity.PrintToDebugOutput("found image:  " + url_image);
            }
            */
        }
        catch(Exception e)
        {
            url_image = "";
        }

        this.ConverserActivity.PrintToDebugOutput("url_image:  " + url_image);

        return url_image;
    }

    private boolean DetermineIfImageExists(String url_image) throws IOException
    {
        HashMap<String, Integer> ImageDimensions = this.DetermineImageDimensionsFromUrl(url_image);
        int w_image = ImageDimensions.get("w");
        int h_image = ImageDimensions.get("h");

        int NULL_IMAGE_SIZE = 301;

        if ((w_image == NULL_IMAGE_SIZE) && (h_image == NULL_IMAGE_SIZE)) { return false; }
        else { return true; }
    }

    private HashMap<String, Integer> DetermineImageDimensionsFromUrl(String url) throws IOException {

        InputStream is = (InputStream) new URL(url).getContent();
        Drawable d = Drawable.createFromStream(is, "imagename");
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        int w_image = bitmap.getWidth();
        int h_image = bitmap.getHeight();
        this.ConverserActivity.PrintToDebugOutput("image width:   " + String.valueOf(w_image));
        HashMap<String, Integer> ImageDimensions = new HashMap<String, Integer>();
        ImageDimensions.put("w", w_image);
        ImageDimensions.put("h", h_image);
        return ImageDimensions;
    }

    private String FindTextForTopic(String id_topic) throws IOException, JSONException
    {
        String text = "";
        try
        {
            String url_base = "https://www.googleapis.com/freebase/v1/text";
            String url_base_withTopicId = url_base + id_topic;
            GenericUrl url = new GenericUrl(url_base_withTopicId);
            url.put("key", "AIzaSyAhwf40hmgjrTc57ije8rqorJ6x-8hKFXE");
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            String json = httpResponse.parseAsString();
            JSONObject Blob = new org.json.JSONObject(json);
            text = Blob.get("result").toString();
            //this.ConverserActivity.PrintToDebugOutput("found text:  " + text);
        }
        catch(Exception e)
        {
            this.ConverserActivity.PrintToDebugOutput("error:  FindTextForTopic");
            text = "";
        }
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




