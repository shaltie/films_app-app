package ru.skudr.filmrandomsearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "Main activity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Log.d(LOG_TAG, "MainActivity onCreate");
    }

    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "MainActivity onStart");
    }

    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "MainActivity onResume");
    }

    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "MainActivity onPause");
    }

    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "MainActivity onStop");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "MainActivity onDestroy");
    }

}