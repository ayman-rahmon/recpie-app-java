package com.tatsujin.recipe.requests.responses;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tatsujin.recipe.models.Recipe;

public class RecipeResponse {
    @SerializedName("recipe")
    @Expose()
    private Recipe recipe ;


    public Recipe getRecipe(){
        return recipe ;
    }

    @NonNull
    @Override
    public String toString() {
        return "RecipeResponse{"+ "recipe=" + recipe + "}";
    }
}
