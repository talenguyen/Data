package com.tale.data;

/**
 * Created by tale on 5/11/15.
 */
public class DataPackage<T> {
    public final Source source;
    public final T data;

    public DataPackage(Source source, T data) {
        this.source = source;
        this.data = data;
    }
}
