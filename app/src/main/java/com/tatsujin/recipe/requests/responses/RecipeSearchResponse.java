package com.tatsujin.recipe.requests.responses;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tatsujin.recipe.models.Recipe;

import java.util.List;

public class RecipeSearchResponse {

    @SerializedName("count")
    @Expose()
    private int count ;
    @SerializedName("recipes")
    @Expose()
    private List<Recipe> recipes ;

    @SerializedName("error")
    @Expose()
    private String error;



    public int getCount() {
        return count ;
    }

    public List<Recipe> getRecipes(){
        return recipes ;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "RecipeSearchResponse{" +
                "count=" + count +
                ", recipes=" + recipes +
                ", error='" + error + '\'' +
                '}';
    }
}
