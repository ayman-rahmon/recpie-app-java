package com.tatsujin.recipe.utils;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.tatsujin.recipe.requests.responses.ApiResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class LiveDataCallAdapterFactory extends CallAdapter.Factory {


    /*
    * This Method Performs a number of checks and then returns a response type for the retrofit requests....
    * (@bodyType is the ResponseType . it can be RecipeResponse or RecipeSearchResponse)
    *
    * CHECKS :
    * 1 - return Type returns LiveData
    * 2- Type LiveData<T> is of ApiResponse.class
    * 3- make sure ApiResponse is parametrized. AKA : ApiResponse<T> exists.
    * */
    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        // Check (1)
        // make sure the callAdapter is returning a type of LiveData...
        if(CallAdapter.Factory.getRawType(returnType) != LiveData.class){
            return null ;
        }
        // Check (2)
        // Type that LiveData is Wrapping...
        Type observableType = CallAdapter.Factory.getParameterUpperBound(0,(ParameterizedType) returnType);
        Type rawObservableType = CallAdapter.Factory.getRawType(observableType);
        if(rawObservableType != ApiResponse.class){
            throw new IllegalArgumentException("Type must be a defined Resource...");
        }

        // check (3)
        // check if ApiResponse is parametrized... AKA : Does ApiResponse<T> exist? (Must wrap around T)... T is one of the responses we previously defined...
        if(!(observableType instanceof ParameterizedType))
            throw new IllegalArgumentException("resource must be parametrized...");


        Type bodyType = CallAdapter.Factory.getParameterUpperBound(0 , (ParameterizedType) observableType);
        return new LiveDataCallAdapter<Type>(bodyType);
    }
}
