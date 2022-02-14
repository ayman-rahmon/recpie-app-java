package com.tatsujin.recipe.requests;

import static com.tatsujin.recipe.utils.Constants.CONNECTION_TIMEOUT;
import static com.tatsujin.recipe.utils.Constants.READ_TIMEOUT;
import static com.tatsujin.recipe.utils.Constants.WRITE_TIMEOUT;

import com.tatsujin.recipe.utils.Constants;
import com.tatsujin.recipe.utils.LiveDataCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static OkHttpClient client = new OkHttpClient.Builder()
            // establish a connection with the server (handshake timeout)...
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            // time between each bite read from the server...
            .readTimeout(READ_TIMEOUT , TimeUnit.SECONDS)
            // time between each bite sent to the server...
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false).build();


    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder().baseUrl(Constants.BASE_URL).client(client).addCallAdapterFactory(new LiveDataCallAdapterFactory()).addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit = retrofitBuilder.build();
    private static RecipeApi recipeApi = retrofit.create(RecipeApi.class);

    public static RecipeApi getRecipeApi(){
        return recipeApi;
    }


}
