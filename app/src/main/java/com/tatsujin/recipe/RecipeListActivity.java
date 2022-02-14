package com.tatsujin.recipe;


import static com.tatsujin.recipe.viewmodels.RecipeListViewModel.QUERY_EXHAUSTED;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
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
        ViewPreloadSizeProvider<String> viewPreloadSizeProvider = new ViewPreloadSizeProvider<>();
        adapter = new RecipeRecyclerAdapter(this , initGlide() , viewPreloadSizeProvider);
        mRecyclerView.setAdapter(adapter);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewPreloader<String> preloader = new RecyclerViewPreloader<String>(Glide.with(this) ,adapter , viewPreloadSizeProvider , 30);

        mRecyclerView.addOnScrollListener(preloader);

        mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {

                if(!mRecyclerView.canScrollVertically(1) && mRecipeListViewModel.getViewState().getValue() == RecipeListViewModel.ViewState.RECIPES){
                    // search next result...
                    mRecipeListViewModel.searchNextPage();

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
                        switch(listResource.status){
                            case LOADING : {
                                if(mRecipeListViewModel.getPageNo() > 1){
                                    adapter.displayLoading();
                                }else{
                                    adapter.displayOnlyLoading();
                                }
                                break ;
                            }
                            case  ERROR: {
                                Log.e(TAG , "onChanged : cannot refresh the cache...");
                                Log.e(TAG , "onChanged : ERROR Message" + listResource.message);
                                Log.e(TAG , "onChanged : status: ERROR , Recipes : " + listResource.data.size());
                                adapter.hideLoading();
                                adapter.setRecipes(listResource.data);
                                Toast.makeText(RecipeListActivity.this, listResource.message , Toast.LENGTH_SHORT).show();

                                if(listResource.message.equals(QUERY_EXHAUSTED)){
                                    adapter.setQueryEhausted();
                                }
                                break ;
                            }
                            case SUCCESS : {
                                Log.d(TAG , "onChanged: cache has been refreshed..");
                                Log.d(TAG , "onChanged: status : SUCCESS , Recipes : " + listResource.data.size());
                                adapter.hideLoading();
                                adapter.setRecipes(listResource.data);
                                break;
                            }

                        }
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



    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions().placeholder(R.drawable.white_background).error(R.drawable.white_background);
        return Glide.with(this).setDefaultRequestOptions(options);
    }

    private void searchRecipesAPI(String query){
        mRecyclerView.smoothScrollToPosition(0);
        mRecipeListViewModel.searchRecipesAPI(query, 1 );
        mSearchView.clearFocus();
    }

    private void searchRecipesApi(String query , int pageNo){
    }


    @Override
    public void onBackPressed() {
        if(mRecipeListViewModel.getViewState().getValue() == RecipeListViewModel.ViewState.CATEGORIES){
            super.onBackPressed();
        }else{
            mRecipeListViewModel.cancelSearchRequest();
            mRecipeListViewModel.setViewCategories();
        }

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