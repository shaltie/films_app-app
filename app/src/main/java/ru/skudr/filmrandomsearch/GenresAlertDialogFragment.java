package ru.skudr.filmrandomsearch;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import static java.lang.Integer.parseInt;

public class GenresAlertDialogFragment extends DialogFragment {

    final String CAT_LOG = "Genres Fragment";


    List<String> GenresList;
    HashMap GenresMap;

    public GenresAlertDialogFragment() {
        // Empty constructor required for DialogFragment


    }

    public static GenresAlertDialogFragment newInstance(String title) {
        GenresAlertDialogFragment frag = new GenresAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        GenresMap = (HashMap<String,List<String>>)this.getArguments().getSerializable("genresMap");

        GenresList = Arrays.asList(getResources().getStringArray(R.array.genres_list));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Choose genres");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_genres_alert_dialog, null);
        //alertDialogBuilder.setView(view);
        /*alertDialogBuilder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //отправляем результат обратно
                    Intent intent = new Intent();
                    intent.putExtra(TAG_WEIGHT_SELECTED, mNpWeight.getValue());
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                }
                // on success
                //sendBackResult();
                // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
                Intent intent = new Intent();
                GenresAlertDialogListener listener = (GenresAlertDialogListener) getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);;

                Gson gson = new Gson();
                String json = gson.toJson(GenresMap);
                listener.onFinishEditDialog(json);
                dismiss();
            }
        });*/

        alertDialogBuilder.setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //отправляем результат обратно
                        Intent intent = new Intent();
                        //intent.putExtra(TAG_WEIGHT_SELECTED, mNpWeight.getValue());
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    }
                });


        final ImageButton[] genreButtons = new ImageButton[GenresList.size()];

        for(int i = 0; i < GenresList.size(); i++) {

            final String id = String.valueOf(GenresList.get(i));


            int resID = getResources().getIdentifier(id + "Btn", "id", getActivity().getPackageName());
            genreButtons[i] = (ImageButton) view.findViewById(resID);
            final ImageButton finalButton = genreButtons[i];
            //final ImageButton genreComedyBtn = (ImageButton) alertDialog.findViewById(resID);

            loadGenre(genreButtons[i], id);
            // if button is clicked, close the custom alertDialog
            genreButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tapGenre(finalButton, id);

                }
            });
        }

        return alertDialogBuilder.create();
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

        int resActive = getResources().getIdentifier(src + "_active", "drawable", getActivity().getPackageName());
        int resInactive = getResources().getIdentifier(src + "_inactive", "drawable", getActivity().getPackageName());
        int resDisable = getResources().getIdentifier(src + "_disable", "drawable", getActivity().getPackageName());

        if(v == 1){
            btn.setImageResource(resActive);
        }else if(v == 0){
            btn.setImageResource(resInactive);
        }else if(v == -1){
            btn.setImageResource(resDisable);
        }

        btn.setTag(String.valueOf(GenresMap.get(id).toString()));
    }

    // Defines the listener interface
    public interface GenresAlertDialogListener {
        void onFinishEditDialog(String inputText);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult() {

    }
}