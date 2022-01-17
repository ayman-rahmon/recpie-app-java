package com.tatsujin.recipe.utils;

import android.util.Log;

import com.tatsujin.recipe.models.Recipe;

import java.util.List;

public class Testing {


    public static void printRecipes(List<Recipe> recipes , String TAG) {
        for(Recipe recipe  : recipes){
            Log.d(TAG , "onChanged: " + recipe.getTitle());
        }
    }
}
