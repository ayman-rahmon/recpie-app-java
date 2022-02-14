package com.tatsujin.recipe.requests.responses;

import com.tatsujin.recipe.models.Recipe;

import java.io.IOException;

import retrofit2.Response;

public class ApiResponse<T> {


    public ApiResponse<T> create(Throwable error){
        return new ApiErrorResponse<>(!error.getMessage().equals("") ? error.getMessage() : "Unknown error \n Check network connection..");
    }

    public ApiResponse<T> create(Response<T> response){
        if(response.isSuccessful()){
            T body = response.body();
            // checking for api key invalid error... and throwing it for all responses...
            if(body instanceof RecipeSearchResponse){
                if(!CheckRecipeApiKey.isRecipeApiKeyValid((RecipeSearchResponse) body)){
                    String errorMsg = "Api key is invalid or expired.";
                    return new ApiErrorResponse<>(errorMsg);
                }
            }

            if(body instanceof  RecipeResponse){
                if(!CheckRecipeApiKey.isRecipeApiValid((RecipeResponse) body)){
                    String errorMsg = "Api key is invalid or expired.";
                    return new ApiErrorResponse<>(errorMsg);
                }
            }


            // 204 is empty response code...
            if(body == null || response.code() == 204){
                return new ApiEmptyResponse<>();
            }else {
                return new ApiSuccessResponse<T>(body);
            }
        }else {
            String errorMsg = "" ;
            try{
                errorMsg = response.errorBody().string();
            }catch(IOException e){
                e.printStackTrace();
                errorMsg = response.message();
            }
            return new ApiErrorResponse<>(errorMsg);
        }
    }



    public class ApiSuccessResponse<T> extends ApiResponse<T> {
        private T body ;

        ApiSuccessResponse(T body){
            this.body = body ;
        }

        public T getBody(){
            return this.body ;
        }
    }

    public class ApiErrorResponse<T> extends ApiResponse<T> {
        private String errorMessage ;
        ApiErrorResponse(String errorMessage){
            this.errorMessage = errorMessage ;
        }
        public String getErrorMessage(){
            return this.errorMessage ;
        }

    }

    public class ApiEmptyResponse<T> extends ApiResponse<T>{
    }

}
