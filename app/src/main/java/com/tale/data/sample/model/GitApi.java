package com.tale.data.sample.model;

import java.util.List;

import retrofit.http.GET;
import rx.Observable;

/**
 * Created by tale on 5/11/15.
 */
public interface GitApi {

    @GET("/users")
    Observable<List<User>> getAll();

}
