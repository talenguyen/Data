package com.tale.data;

import java.util.List;

import rx.Observable;

/**
 * Created by tale on 5/11/15.
 */
public interface Model<T> {

    Observable<T> getById(String id);

    Observable<List<T>> getAll();

    Observable<Boolean> save(T data);

    Observable<Boolean> saveAll(List<T> dataSet);

}
