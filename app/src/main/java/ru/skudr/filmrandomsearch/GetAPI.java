package ru.skudr.filmrandomsearch;

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by shaltie on 27/11/16.
 */

public class GetAPI {


    final String CAT_LOG = "GET FILM";

    final String API_URL = "http://film.skudr.ru/index.php";

    public GetAPI(){

        Log.v(CAT_LOG, "GET FILM Starts! YO YO YO YO");

    }


    public JSONObject get(String genres, String withoutGenres, String years, String popularity) throws JSONException, IOException, MalformedURLException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d(CAT_LOG, "get started");

        URL url = new URL(API_URL + "?g=" + genres + "&ng=" + withoutGenres + "&y=" + years + "&p=" + popularity);

        Log.d(CAT_LOG, "urL: " + url.toString());

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.setUseCaches(false);
        urlConnection.setRequestProperty("Content-Type", "application/json");


        try {

            urlConnection.connect();

            int status = urlConnection.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    JSONObject res = new JSONObject(sb.toString());

                    Log.d(CAT_LOG + " res", res.toString());

                    return res;

            }



        } finally {
            urlConnection.disconnect();
        }

        return null;



    }

}
