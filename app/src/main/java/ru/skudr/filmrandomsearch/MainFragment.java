package ru.skudr.filmrandomsearch;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.parseInt;

public class MainFragment extends Fragment implements View.OnClickListener, GenresAlertDialogFragment.GenresAlertDialogListener{

    final String CAT_LOG = "MainFragment";
    GetAPI getAPI;


    Button chooseGenresBtn;
    ImageButton goSearchBtn,
            reGoSearchBtn,
            backToSelectBtn;
    CrystalSeekbar popularityBar;

    CrystalRangeSeekbar yearsRangeBar;
    TextView yearMinText;
    TextView yearMaxText;
    TextView outputInfoTitle,
            outputInfoOverview,
            outputInfoRelease,
            outputInfoPopularity,
            outputInfoGenres,
            popularityValue,
            outputInfoVote;

    ImageView outputInfoPoster;
    RelativeLayout searchLayout,
        outputLayout;


    //ImageButton genreComedyBtn;

    GridLayout genresDialog;

    List<String> GenresList;
    List<String> GenresIdList;
    HashMap<String, String> GenresMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main, container, false);
        //setContentView(R.layout.activity_main);


        getAPI = new GetAPI();

        goSearchBtn = (ImageButton) v.findViewById(R.id.goSearchBtn);
        reGoSearchBtn = (ImageButton) v.findViewById(R.id.reGoSearchBtn);
        backToSelectBtn = (ImageButton) v.findViewById(R.id.backToSelectBtn);
        chooseGenresBtn = (Button) v.findViewById(R.id.chooseGenresBtn);
        popularityBar = (CrystalSeekbar) v.findViewById(R.id.popularityBar);

        goSearchBtn.setOnClickListener(this);
        reGoSearchBtn.setOnClickListener(this);
        backToSelectBtn.setOnClickListener(this);
        chooseGenresBtn.setOnClickListener(this);

        // get seekbar from view
        yearsRangeBar = (CrystalRangeSeekbar) v.findViewById(R.id.yearsRangeBar);

        yearMinText = (TextView) v.findViewById(R.id.yearMinText);
        yearMaxText = (TextView) v.findViewById(R.id.yearMaxText);
        outputInfoTitle = (TextView) v.findViewById(R.id.outputInfoTitle);
        outputInfoOverview = (TextView) v.findViewById(R.id.outputInfoOverview);
        outputInfoRelease = (TextView) v.findViewById(R.id.outputInfoRelease);
        outputInfoPopularity = (TextView) v.findViewById(R.id.outputInfoPopularity);
        outputInfoGenres = (TextView) v.findViewById(R.id.outputInfoGenres);
        outputInfoVote = (TextView) v.findViewById(R.id.outputInfoVote);
        popularityValue = (TextView) v.findViewById(R.id.popularityValue);

        outputInfoPoster = (ImageView) v.findViewById(R.id.outputInfoPoster);

        searchLayout = (RelativeLayout) v.findViewById(R.id.searchLayout);
        outputLayout = (RelativeLayout) v.findViewById(R.id.outputLayout);

        // set listener
        yearsRangeBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                yearMinText.setText(String.valueOf(minValue));
                yearMaxText.setText(String.valueOf(maxValue));
            }
        });

        // set listener
        popularityBar.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue) {
                popularityValue.setText(String.valueOf(minValue));
            }
        });

        GenresList = Arrays.asList(getResources().getStringArray(R.array.genres_list));
        GenresIdList = Arrays.asList(getResources().getStringArray(R.array.genres_id_list));
        GenresMap = new HashMap<String, String>();

        for(int i = 0; i < GenresList.size(); i++) {

            GenresMap.put(String.valueOf(GenresList.get(i)), "0");
        }


        return v;



    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

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

        Log.d(CAT_LOG, "pop: " + String.valueOf(popularityValue.getText()));
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
            JSONObject res = getAPI.get(addedGenresString, excludedGenresString, years, String.valueOf(popularityValue.getText()));

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

                if(GenresIdList.indexOf(id)>-1)
                    genresOutPut += GenresList.get(GenresIdList.indexOf(id)).toString().replace("genre","") + ", ";
            }

            Log.d(CAT_LOG, genresOutPut);

            outputInfoGenres.setText(genresOutPut);
            outputInfoVote.setText(res.getString("vote_average"));

            Picasso.with(getActivity())
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
            case R.id.reGoSearchBtn:
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


    /*public void tapGenre(ImageButton btn, String id){

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
    }*/


    public void showDialog() {


        // custom dialog
        /*final Dialog dialog = new Dialog(MainFragment.this);
        dialog.setContentView(R.layout.genres_dialog);
        dialog.setTitle("Choose genres");*/

        Bundle args = new Bundle();
        args.putSerializable("genresMap", GenresMap);
        GenresAlertDialogFragment alertDialog = GenresAlertDialogFragment.newInstance("");
        alertDialog.setArguments(args);
        alertDialog.setTargetFragment(this, 0);

        alertDialog.show(getActivity().getFragmentManager(), "fragment_genres_alert_dialog");

        //dialog.show();


    }

    // This is called when the dialog is completed and the results have been passed
    @Override
    public void onFinishEditDialog(String genresMap) {

        Log.d(CAT_LOG, "on finished");

        Gson gson = new Gson();
        Type typeOfHashMap = new TypeToken<HashMap<String, String>>() { }.getType();
        GenresMap = (HashMap<String,String>) gson.fromJson(genresMap, typeOfHashMap);


    }
}
