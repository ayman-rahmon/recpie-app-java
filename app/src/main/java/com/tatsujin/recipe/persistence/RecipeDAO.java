package com.tatsujin.recipe.persistence;


import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.Query;

import com.tatsujin.recipe.models.Recipe;

import java.util.List;

@Dao
public interface RecipeDAO {

    @Insert(onConflict = IGNORE)
    long[] insertRecipes(Recipe... recipe); // {returns ids of inserted recipes... and -1 on conflicts} ...


    @Insert(onConflict = REPLACE)
    void insertRecipe(Recipe recipe) ;


    @Query("UPDATE recipes SET title= :title , publisher= :publisher , image_url=:image_url , social_rank = :social_rank WHERE recipe_id = :recipe_id")
    void updateRecipe(String recipe_id , String title  , String publisher , String image_url , float social_rank);


    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query ||'%'   OR  ingredients LIKE '%'||:query ||'%'  ORDER BY social_rank DESC LIMIT(:pageNumber * 30 )")
    LiveData<List<Recipe>> searchRecipe(String query , int pageNumber);


    @Query("SELECT * FROM recipes WHERE recipe_id = :id")
    LiveData<Recipe> getRecipe(String id);



}
