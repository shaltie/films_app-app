package ru.skudr.filmrandomsearch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SearchBuilder extends Service {

    final String LOG_TAG = "Search builder service";

    String searchRequest;

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    public void setSearchRequest(String request){

        searchRequest = request;

    }

    public String getSearchRequest(){

        return searchRequest;

    }

    public SearchBuilder() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
