package com.tatsujin.recipe.persistence;


import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Converters {

    @TypeConverter
    public static String fromArray(String[] list){
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json ;
    }

    @TypeConverter
    public static String[] fromString(String string){
        Type listType = new TypeToken<String[]>(){}.getType();
        return new Gson().fromJson(string , listType);
    }

}
