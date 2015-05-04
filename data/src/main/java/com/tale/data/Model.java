package com.tale.data;

import java.util.List;

import rx.Observable;

/**
 * Created by tale on 5/4/15.
 */
public interface Model<T> {

    Observable<List<T>> getAll();

    Observable<Boolean> save(T item);

    Observable<Boolean> delete(T item);

}
