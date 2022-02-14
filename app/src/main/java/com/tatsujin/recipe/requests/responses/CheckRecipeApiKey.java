package com.tatsujin.recipe.requests.responses;

public class CheckRecipeApiKey {


    protected static boolean isRecipeApiKeyValid(RecipeSearchResponse response){
        return response.getError() == null ;
    }

    protected static boolean isRecipeApiValid(RecipeResponse response){
        return response.getError() == null ;
    }


}
