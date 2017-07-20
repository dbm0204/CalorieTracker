package com.example.dbm0204.caltracker;

import android.app.DialogFragment;
import android.view.View;

import java.util.List;
import java.util.Map;

/**
 * Created by dbm0204 on 7/20/17.
 */

public class FoodDensityFragment extends DialogFragment {
    public FoodItem food;
    private View view;
    private Map<String, Double> matches;
    private List<String> StringList;

    public static FoodDensityFragment newInstance(FoodItem food, Map<String,Double> matches){
        FoodDensityFragment fragment = new FoodDensityFragment();
        fragment.food=food;
        fragment.matches=matches;
        return fragment;
    }
    public interface FoodDensityDialogListener{
        public void onDensityDialogPositiveClick(DialogFragment dialog,String name, Double value);
        public void onDensityDiaLogNeutralClick(DialogFragment dialog);
    }

}
