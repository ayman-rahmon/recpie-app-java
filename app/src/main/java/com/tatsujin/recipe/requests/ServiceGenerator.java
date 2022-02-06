package com.tatsujin.recipe.requests;

import com.tatsujin.recipe.utils.Constants;
import com.tatsujin.recipe.utils.LiveDataCallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {


    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder().baseUrl(Constants.BASE_URL).addCallAdapterFactory(new LiveDataCallAdapterFactory()).addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit = retrofitBuilder.build();
    private static RecipeApi recipeApi = retrofit.create(RecipeApi.class);

    public static RecipeApi getRecipeApi(){
        return recipeApi;
    }


}
