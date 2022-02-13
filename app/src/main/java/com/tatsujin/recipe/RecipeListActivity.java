package com.tatsujin.recipe;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tatsujin.recipe.adapters.RecipeRecyclerAdapter;
import com.tatsujin.recipe.adapters.onRecipeListener;
import com.tatsujin.recipe.models.Recipe;
import com.tatsujin.recipe.requests.RecipeApi;
import com.tatsujin.recipe.requests.ServiceGenerator;
import com.tatsujin.recipe.requests.responses.RecipeResponse;
import com.tatsujin.recipe.utils.Constants;
import com.tatsujin.recipe.utils.Resource;
import com.tatsujin.recipe.utils.Testing;
import com.tatsujin.recipe.utils.VerticalSpacingItemDecorator;
import com.tatsujin.recipe.viewmodels.RecipeListViewModel;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListActivity extends BaseActivity implements onRecipeListener {

    private static final String TAG = "RecipeListActivity";
    private RecipeListViewModel mRecipeListViewModel ;
    private RecyclerView mRecyclerView ;
    private RecipeRecyclerAdapter adapter ;
    private SearchView mSearchView ;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecyclerView = findViewById(R.id.recipe_list);
        mRecipeListViewModel = new ViewModelProvider(this).get(RecipeListViewModel.class) ;

        subscribeObservers();
        initRecyclerView();
//        testRecipeSearch();
        initSearchView();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initRecyclerView(){
        adapter = new RecipeRecyclerAdapter(this);
        mRecyclerView.setAdapter(adapter);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {

                if(!mRecyclerView.canScrollVertically(1)){
                    // search next result...
                }



            }
        });
    }

    private void subscribeObservers(){
        mRecipeListViewModel.getRecipes().observe(this, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(Resource<List<Recipe>> listResource) {
                if(listResource != null){
                    Log.d(TAG, "onChanged status : " + listResource.status);

                    if(listResource.data != null){
                        Testing.printRecipes(listResource.data , "data");
                    }
                }
            }
        });


        mRecipeListViewModel.getViewState().observe(this, new Observer<RecipeListViewModel.ViewState>() {
            @Override
            public void onChanged(RecipeListViewModel.ViewState viewState) {
                if(viewState !=null){
                    switch (viewState){
                        case RECIPES:
                            // recipes will show automatically from another observer...
                            break;
                        case CATEGORIES:
                                displaySearchCategories();
                            break;
                    }
                }
            }
        });
    }



    private void searchRecipesAPI(String query) {
        mRecipeListViewModel.searchRecipesAPI(query, 1 );
    }

    private void searchRecipesApi(String query , int pageNo){
    }


    @Override
    public void onBackPressed() {
    }

    private void initSearchView(){
         mSearchView = findViewById(R.id.search_view);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecipesAPI(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
    }


    private void displaySearchCategories(){
        adapter.displayCategories();
    }


    private void testRecipeSearch(){
        searchRecipesApi("chicken breast" ,1 );
    }




    @Override
    public void onRecipeClick(int position) {
        Intent intent = new Intent(this , RecipeActivity.class);
        intent.putExtra("recipe" , adapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category) {
        searchRecipesAPI(category);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu , menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.action_category){
            displaySearchCategories();
        }
        return super.onOptionsItemSelected(item);
    }
}