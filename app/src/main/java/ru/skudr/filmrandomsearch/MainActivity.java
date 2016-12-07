package ru.skudr.filmrandomsearch;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    final String CAT_LOG = "MainActivity";
    GetAPI getAPI;

    Button chooseGenresBtn;
    ImageButton goSearchBtn,
            backToSelectBtn;
    SeekBar popularityBar;

    CrystalRangeSeekbar yearsRangeBar;
    TextView yearMinText;
    TextView yearMaxText;
    TextView outputInfoTitle,
            outputInfoOverview,
            outputInfoRelease,
            outputInfoPopularity,
            outputInfoGenres,
            outputInfoVote;

    ImageView outputInfoPoster;
    RelativeLayout searchLayout,
        outputLayout;


    //ImageButton genreComedyBtn;

    GridLayout genresDialog;

    List<String> GenresList;
    List<String> GenresIdList;
    Map<String, String> GenresMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAPI = new GetAPI();

        goSearchBtn = (ImageButton) findViewById(R.id.goSearchBtn);
        backToSelectBtn = (ImageButton) findViewById(R.id.backToSelectBtn);
        chooseGenresBtn = (Button) findViewById(R.id.chooseGenresBtn);
        popularityBar = (SeekBar) findViewById(R.id.popularityBar);
        goSearchBtn.setOnClickListener(this);
        backToSelectBtn.setOnClickListener(this);
        chooseGenresBtn.setOnClickListener(this);

        // get seekbar from view
        yearsRangeBar = (CrystalRangeSeekbar) findViewById(R.id.yearsRangeBar);

        yearMinText = (TextView) findViewById(R.id.yearMinText);
        yearMaxText = (TextView) findViewById(R.id.yearMaxText);
        outputInfoTitle = (TextView) findViewById(R.id.outputInfoTitle);
        outputInfoOverview = (TextView) findViewById(R.id.outputInfoOverview);
        outputInfoRelease = (TextView) findViewById(R.id.outputInfoRelease);
        outputInfoPopularity = (TextView) findViewById(R.id.outputInfoPopularity);
        outputInfoGenres = (TextView) findViewById(R.id.outputInfoGenres);
        outputInfoVote = (TextView) findViewById(R.id.outputInfoVote);

        outputInfoPoster = (ImageView) findViewById(R.id.outputInfoPoster);

        searchLayout = (RelativeLayout) findViewById(R.id.searchLayout);
        outputLayout = (RelativeLayout) findViewById(R.id.outputLayout);

        // set listener
        yearsRangeBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                yearMinText.setText(String.valueOf(minValue));
                yearMaxText.setText(String.valueOf(maxValue));
            }
        });

        GenresList = Arrays.asList(getResources().getStringArray(R.array.genres_list));
        GenresIdList = Arrays.asList(getResources().getStringArray(R.array.genres_id_list));
        GenresMap = new HashMap<String, String>();

        for(int i = 0; i < GenresList.size(); i++) {

            GenresMap.put(String.valueOf(GenresList.get(i)), "0");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void search(){

        Log.d(CAT_LOG, "search run");

        Log.d(CAT_LOG, "pop: " + String.valueOf(popularityBar.getProgress()));
        Log.d(CAT_LOG, "years: " + yearMinText.getText().toString() + "-" + yearMaxText.getText().toString());

        String years = yearMinText.getText().toString() + "-" + yearMaxText.getText().toString();

        ArrayList<String> addedGenres = new ArrayList<String>();
        ArrayList<String> excludedGenres = new ArrayList<String>();


        for(int i = 0; i < GenresList.size(); i++) {
            int v = parseInt(GenresMap.get(GenresList.get(i).toString()));
            if(v == 1){
                addedGenres.add(GenresIdList.get(i).toString());
            }else if(v == -1){
                excludedGenres.add(GenresIdList.get(i).toString());
            }
        }
        String addedGenresString = addedGenres.toString().replace("[", "").replace("]", "").replace(" ","").trim();
        String excludedGenresString = excludedGenres.toString().replace("[", "").replace("]", "").replace(" ","").trim();
        Log.d(CAT_LOG, "added: " + addedGenresString);
        Log.d(CAT_LOG, "prohi: " + excludedGenresString);

        try{
            JSONObject res = getAPI.get(addedGenresString, excludedGenresString, years, String.valueOf(popularityBar.getProgress()));

            searchLayout.setVisibility(View.GONE);
            outputLayout.setVisibility(View.VISIBLE);
            Log.d(CAT_LOG + " res", res.toString());
            outputInfoTitle.setText(res.getString("original_title"));
            outputInfoOverview.setText(res.getString("overview"));
            outputInfoRelease.setText(res.getString("release_date"));
            outputInfoPopularity.setText(res.getString("popularity"));

            List<String> genresList = new ArrayList<String>(Arrays.asList(res.getString("genre_ids").replace("[","").replace("]","").replace(" ","").split(",")));
            String genresOutPut = "";
            for(String id : genresList){

                genresOutPut += GenresList.get(GenresIdList.indexOf(id)).toString().replace("genre","") + ", ";
            }

            Log.d(CAT_LOG, genresOutPut);

            outputInfoGenres.setText(genresOutPut);
            outputInfoVote.setText(res.getString("vote_average"));

            Picasso.with(this)
                    .load("https://image.tmdb.org/t/p/w600_and_h900_bestv2" + res.getString("poster_path"))
                    .into(outputInfoPoster);

        }catch(JSONException e){
            e.printStackTrace();
        }catch(IOException ex){
            ex.printStackTrace();
        }


    }

    void toSelect(){

        outputLayout.setVisibility(View.GONE);
        searchLayout.setVisibility(View.VISIBLE);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goSearchBtn:
                search();
                break;
            case R.id.backToSelectBtn:
                toSelect();
                break;
            case R.id.chooseGenresBtn:
                showDialog();

            default:
                break;
        }
    }


    public void tapGenre(ImageButton btn, String id){

        int value = parseInt(btn.getTag().toString());
        if(value == 0)
            GenresMap.put(id, "1");
        else if(value == 1)
            GenresMap.put(id, "-1");
        else if(value == -1)
            GenresMap.put(id, "0");

        loadGenre(btn, id);

    }

    public void loadGenre(ImageButton btn, String id){

        //int value = Integer.parseInt(btn.getTag().toString());

        int v = parseInt(GenresMap.get(id).toString());

        String src = id.replace("genre", "").toLowerCase();

        Log.d(CAT_LOG, id);

        int resActive = getResources().getIdentifier(src + "_active", "drawable", getPackageName());
        int resInactive = getResources().getIdentifier(src + "_inactive", "drawable", getPackageName());
        int resDisable = getResources().getIdentifier(src + "_disable", "drawable", getPackageName());

        if(v == 1){
            btn.setImageResource(resActive);
        }else if(v == 0){
            btn.setImageResource(resInactive);
        }else if(v == -1){
            btn.setImageResource(resDisable);
        }

        btn.setTag(String.valueOf(GenresMap.get(id).toString()));
    }


    public void showDialog() {


        // custom dialog
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.genres_dialog);
        dialog.setTitle("Choose genres");

        final ImageButton[] genreButtons = new ImageButton[GenresList.size()];

        for(int i = 0; i < GenresList.size(); i++) {

            final String id = String.valueOf(GenresList.get(i));


            int resID = getResources().getIdentifier(id + "Btn", "id", getPackageName());
            genreButtons[i] = (ImageButton) dialog.findViewById(resID);
            final ImageButton finalButton = genreButtons[i];
            //final ImageButton genreComedyBtn = (ImageButton) dialog.findViewById(resID);

            loadGenre(genreButtons[i], id);
            // if button is clicked, close the custom dialog
            genreButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tapGenre(finalButton, id);

                }
            });
        }



        dialog.show();


    }
}
