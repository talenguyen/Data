package com.tale.data;

import android.util.Log;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.Subject;

/**
 * Created by tale on 5/11/15.
 */
public class Repository<T> {

    private static final String TAG = Repository.class.getSimpleName();

    private final Model<T> cacheModel;
    private final Model<T> remoteModel;

    private final Deliverer<T> singleItemDeliver = new Deliverer<>();
    private final Deliverer<List<T>> listItemDeliver = new Deliverer<>();

    private final Action1<Boolean> cacheAction = new Action1<Boolean>() {
        @Override
        public void call(Boolean aBoolean) {
            Log.d(TAG, "cache result " + aBoolean);
        }
    };

    public Repository(Model<T> cacheModel, Model<T> remoteModel) {
        this.cacheModel = cacheModel;
        this.remoteModel = remoteModel;
    }

    public Subject<DataPackage<T>, DataPackage<T>> singleItemObservable() {
        return singleItemDeliver.asObservable();
    }

    public Subject<DataPackage<List<T>>, DataPackage<List<T>>> listItemObservable() {
        return listItemDeliver.asObservable();
    }

    public void fetchItem(String id) {
        final Observable<DataPackage<T>> remoteStream = remoteModel.getById(id)
                .doOnNext(new Action1<T>() {
                    @Override
                    public void call(T t) {
                        cacheModel.save(t)
                                .subscribeOn(Schedulers.io())
                                .subscribe(cacheAction);
                    }
                })
                .map(new Func1<T, DataPackage<T>>() {
                    @Override
                    public DataPackage<T> call(T t) {
                        return new DataPackage<T>(Source.Network, t);
                    }
                });
        final Observable<DataPackage<T>> localStream = cacheModel.getById(id)
                .map(new Func1<T, DataPackage<T>>() {
                    @Override
                    public DataPackage<T> call(T t) {
                        return new DataPackage<T>(Source.Cache, t);
                    }
                });
        Observable.mergeDelayError(remoteStream, localStream)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DataPackage<T>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        singleItemDeliver.send(null);
                    }

                    @Override
                    public void onNext(DataPackage<T> dataPackage) {
                        singleItemDeliver.send(dataPackage);
                    }
                });
    }

    public void pull() {
        final Observable<DataPackage<List<T>>> remoteStream = remoteModel.getAll()
                .doOnNext(new Action1<List<T>>() {
                    @Override
                    public void call(List<T> ts) {
                        cacheModel.saveAll(ts)
                                .subscribeOn(Schedulers.io())
                                .subscribe(cacheAction);
                    }
                })
                .map(new Func1<List<T>, DataPackage<List<T>>>() {
                    @Override
                    public DataPackage<List<T>> call(List<T> ts) {
                        return new DataPackage<>(Source.Network, ts);
                    }
                });
        final Observable<DataPackage<List<T>>> localStream = cacheModel.getAll()
                .map(new Func1<List<T>, DataPackage<List<T>>>() {
                    @Override
                    public DataPackage<List<T>> call(List<T> ts) {
                        return new DataPackage<List<T>>(Source.Cache, ts);
                    }
                });
        Observable.mergeDelayError(remoteStream, localStream)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DataPackage<List<T>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        listItemDeliver.send(null);
                    }

                    @Override
                    public void onNext(DataPackage<List<T>> listDataPackage) {
                        listItemDeliver.send(listDataPackage);
                    }
                });
    }

}
