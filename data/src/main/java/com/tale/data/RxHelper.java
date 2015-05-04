package com.tale.data;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Giang Nguyen on 4/2/2015.
 */
public class RxHelper {

    private static final Observable.Transformer SCHEDULERS_TRANSFORMER = new Observable.Transformer<Observable<Object>, Observable<Object>>() {
        @Override
        public Observable<Observable<Object>> call(Observable<Observable<Object>> observable) {
            return observable.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return (Observable.Transformer<T, T>) SCHEDULERS_TRANSFORMER;
    }

}
