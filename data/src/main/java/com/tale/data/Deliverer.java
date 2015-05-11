package com.tale.data;

import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by tale on 5/11/15.
 */
public class Deliverer<T> {

    private final Subject<DataPackage<T>, DataPackage<T>> subject = new SerializedSubject<>(PublishSubject
            .<DataPackage<T>>create());

    public Subject<DataPackage<T>, DataPackage<T>> asObservable() {
        return subject;
    }

    public void send(DataPackage<T> dataPackage) {
        subject.onNext(dataPackage);
    }

}