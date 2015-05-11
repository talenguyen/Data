package com.tale.data;

import android.util.Log;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.Subject;

/**
 * Created by tale on 5/11/15.
 */
public class Repository<T> implements Model<T> {

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


    @Override
    public Observable<T> getById(String id) {
        remoteModel.getById(id)
                .subscribeOn(Schedulers.computation())
                .subscribe(new Observer<T>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        singleItemDeliver.send(new DataPackage<T>(Source.Network, null));
                    }

                    @Override
                    public void onNext(final T t) {
                        singleItemDeliver.send(new DataPackage<T>(Source.Network, t));
                        cacheModel.save(t)
                                .subscribeOn(Schedulers.io())
                                .subscribe(cacheAction);
                    }
                });
        return cacheModel.getById(id);
    }

    @Override
    public Observable<List<T>> getAll() {
        remoteModel.getAll()
                .subscribeOn(Schedulers.computation())
                .subscribe(new Observer<List<T>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listItemDeliver.send(new DataPackage<List<T>>(Source.Network, null));
                    }

                    @Override
                    public void onNext(List<T> ts) {
                        listItemDeliver.send(new DataPackage<>(Source.Network, ts));
                        cacheModel.saveAll(ts)
                                .subscribeOn(Schedulers.io())
                                .subscribe(cacheAction);
                    }
                });
        return cacheModel.getAll();
    }

    @Override
    public Observable<Boolean> save(T data) {
        return remoteModel.save(data);
    }

    @Override
    public Observable<Boolean> saveAll(List<T> dataSet) {
        return remoteModel.saveAll(dataSet);
    }
}
