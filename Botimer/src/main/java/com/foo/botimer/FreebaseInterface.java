package com.foo.botimer;

/**
 * Created by matthew.sibigtroth on 1/9/14.
 */





import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;


import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;


public class FreebaseInterface
{

    public FreebaseInterface()
    {
        this.Init();
    }

    private void Init()
    {

    }

    public void Foo()
    {

        try
        {
            HttpTransport httpTransport = new NetHttpTransport();
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
            JSONParser parser = new JSONParser();
            String query = "[{\"limit\": 5,\"name\":null,\"type\":\"/medicine/disease\"}]";
            GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/mqlread");
            url.put("key", "AIzaSyAhwf40hmgjrTc57ije8rqorJ6x-8hKFXE");
            url.put("query", query);
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            JSONObject response = (JSONObject)parser.parse(httpResponse.parseAsString());
            JSONArray results = (JSONArray)response.get("result");

            for (int i=0; i<results.length(); i++)
            {
                Log.d("foo", results.get(i).toString());
                //Log.d("foo", result.get("name").toString());
            }

            //for (Object result : results) {
            //    System.out.println(result.get("name").toString());
            //}
        }
        catch(Exception e)
        {
            Log.d("foo", "error: Foo");
        }



    }
}

    /*
    public static void searchTest(String query, String key, String params) throws IOException
    {
        String query_envelope = "{\"query\":" + query + "}";
        String service_url = "https://www.googleapis.com/freebase/v1/search";

        String url = service_url    + "?query=" + URLEncoder.encode(query, "UTF-8")
                + params
                + "&key=" + key;

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(new HttpGet(url));

        JsonParser parser = new JsonParser();
        JsonObject json_data =
                (JsonObject)parser.parse(EntityUtils.toString(response.getEntity()));
        JsonArray results = (JsonArray)json_data.get("result");

        if(results != null)
        {
            for (Object planet : results)
            {
                System.out.println(((JsonObject)planet).get("name"));
            }
        }
    }
    */


