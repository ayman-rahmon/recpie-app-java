package com.tatsujin.recipe.requests.responses;

import java.io.IOException;

import retrofit2.Response;

public class ApiResponse<T> {


    public ApiResponse<T> create(Throwable error){
        return new ApiErrorResponse<>(!error.getMessage().equals("") ? error.getMessage() : "Unknown error \n Check network connection..");
    }

    public ApiResponse<T> create(Response<T> response){
        if(response.isSuccessful()){
            T body = response.body();
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
