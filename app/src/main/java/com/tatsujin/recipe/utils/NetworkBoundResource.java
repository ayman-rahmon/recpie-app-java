package com.tatsujin.recipe.utils;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.tatsujin.recipe.requests.responses.ApiResponse;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.xml.transform.Result;

import okhttp3.Cache;

// CacheObject : Type for the Resource data. (database cache)...
// RequestObject : Type for the API response. (network request)...
public abstract class NetworkBoundResource<CacheObject , RequestObject> {
    private static final String TAG = "NetworkBoundResource";
    private AppExecutors appExecutors ;
    private MediatorLiveData<Resource<CacheObject>> results = new MediatorLiveData<>();


    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        init();
    }

    private void init(){
        // update livedata for loading status...
        results.setValue((Resource<CacheObject>) Resource.loading(null));
        // observe livedata source from local database...
        final LiveData<CacheObject> dbSource = loadFromDB() ;

        results.addSource(dbSource, new Observer<CacheObject>() {
            @Override
            public void onChanged(CacheObject cacheObject) {
                // data in the cache has changed...
                results.removeSource(dbSource); // stop observer from keep listening...
                if(shouldFetch(cacheObject)){
                    // get data from the network...
                    fetchFromNetwork(dbSource);
                }else {
                    results.addSource(dbSource, new Observer<CacheObject>() {
                        @Override
                        public void onChanged(CacheObject cacheObject) {
                            setValue(Resource.success(cacheObject));
                        }
                    });
                }
            }
        });
    }
/*
     1) observe local DB ...
     2) if the condition  then query the network...
     3) stop observing the local db ...
     4) insert new data into local db...
     5) begin observing local db again to see the refreshed data from the network...
*/
    private void fetchFromNetwork(final LiveData<CacheObject> dbSource){
        Log.d(TAG , "update database from the network : (fetchFromNetwork: called)");
        results.addSource(dbSource, new Observer<CacheObject>() {
            @Override
            public void onChanged(CacheObject cacheObject) {
                setValue(Resource.loading(cacheObject));
            }
        });

        final LiveData<ApiResponse<RequestObject>> apiResponse = createCall() ;
        results.addSource(apiResponse, new Observer<ApiResponse<RequestObject>>() {
            @Override
            public void onChanged(ApiResponse<RequestObject> requestObjectApiResponse) {
                results.removeSource(dbSource);
                results.removeSource(apiResponse);
                /*
                3 cases:
                  1- ApiSuccessResponse
                  2- ApiErrorResponse
                  3- ApiEmptyResponse
                 */
                if(requestObjectApiResponse instanceof ApiResponse.ApiSuccessResponse){
                    Log.d(TAG , "onChanged: ApiSuccessResponse");
                    appExecutors.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            // save the response to local db...
                            saveCallResult((RequestObject) processResponse((ApiResponse.ApiSuccessResponse) requestObjectApiResponse));
                            appExecutors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    results.addSource(loadFromDB(), new Observer<CacheObject>() {
                                        @Override
                                        public void onChanged(CacheObject cacheObject) {
                                            setValue(Resource.success(cacheObject));
                                        }
                                    });
                                }
                            });
                        }
                    });
                }else if(requestObjectApiResponse instanceof ApiResponse.ApiEmptyResponse) {
                    Log.d(TAG , "onChanged: ApiEmptyResponse");
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            results.addSource(loadFromDB(), new Observer<CacheObject>() {
                                @Override
                                public void onChanged(CacheObject cacheObject) {
                                    setValue(Resource.success(cacheObject));
                                }
                            });
                        }
                    });


                }else if(requestObjectApiResponse instanceof  ApiResponse.ApiErrorResponse) {
                    Log.d(TAG , "onChanged: Api Error Response");
                    results.addSource(dbSource, new Observer<CacheObject>() {
                        @Override
                        public void onChanged(CacheObject cacheObject) {
                            setValue(Resource.error(((ApiResponse.ApiErrorResponse)requestObjectApiResponse).getErrorMessage() , cacheObject));
                        }
                    });

                }
            }
        });

    }

    private CacheObject processResponse(ApiResponse.ApiSuccessResponse responst){
        return (CacheObject) responst.getBody() ;
    }


    private void setValue(Resource<CacheObject> newValue) {
        if(results.getValue() != newValue){
            results.setValue(newValue);
        }
    }

    // called to save the result of the api response into the database...
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestObject item);
    // called with the data in the database to decide whether to fetch ..
    // potentially updated data from the network ...
    @MainThread
    protected abstract boolean shouldFetch(@Nullable CacheObject data);// determine the data refresh rate and such things...

    // called to get the cached data from the DB...
    @NonNull @MainThread
    protected abstract LiveData<CacheObject> loadFromDB();

    // Called to create calls to the API...
    // automatically returns live data from the network as opposed to actually having to run calls in new Threads...
    @NotNull @MainThread
    protected abstract LiveData<ApiResponse<RequestObject>> createCall();

    // return data to the UI...
    public final LiveData<Resource<CacheObject>> getAsLiveData(){
        return results ;
    };



}
