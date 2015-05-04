package com.tale.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by tale on 5/4/15.
 */
public class DataModel<Data, LocalData, RemoteData> implements Model<Data> {

    private static final String TAG = DataModel.class.getSimpleName();

    private final Model<LocalData> localDataModel;
    private final Model<RemoteData> remoteDataModel;
    private final DataConverter<Data, LocalData, RemoteData> converter;
    private final PublishSubject<List<Data>> publishSubject = PublishSubject.create();

    public DataModel(Model<LocalData> localDataModel, Model<RemoteData> remoteDataModel, DataConverter<Data, LocalData, RemoteData> converter) {
        this.localDataModel = localDataModel;
        this.remoteDataModel = remoteDataModel;
        this.converter = converter;
    }

    public Observable<List<Data>> observable() {
        return publishSubject;
    }

    @Override
    public Observable<List<Data>> getAll() {
        final Observable<List<Data>> localStream = localDataModel.getAll()
                .map(new Func1<List<LocalData>, List<Data>>() {
                    @Override
                    public List<Data> call(List<LocalData> localDatas) {
                        return fromLocal(localDatas);
                    }
                });
        final Observable<List<Data>> remoteStream = remoteDataModel.getAll()
                .map(new Func1<List<RemoteData>, List<Data>>() {
                    @Override
                    public List<Data> call(List<RemoteData> remoteDatas) {
                        return fromRemote(remoteDatas);
                    }
                });
        return Observable.mergeDelayError(localStream, remoteStream);
    }

    @Override
    public Observable<Boolean> save(Data item) {
        final LocalData localData = converter.toLocal(item);
        final RemoteData remoteData = converter.toRemote(item);
        remoteDataModel.save(remoteData)
                .subscribeOn(Schedulers.computation())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "network");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "error", e);
                    }

                    @Override
                    public void onNext(Boolean b) {
                        Log.d(TAG, "result => " + b);
                    }
                });
        return localDataModel
                .save(localData)
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean b) {
                        if (b) {
                            notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public Observable<Boolean> delete(Data item) {
        final LocalData localData = converter.toLocal(item);
        final RemoteData remoteData = converter.toRemote(item);
        remoteDataModel.delete(remoteData)
                .subscribeOn(Schedulers.computation())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "network");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "error", e);
                    }

                    @Override
                    public void onNext(Boolean b) {
                        Log.d(TAG, "result => " + b);
                    }
                });
        return localDataModel
                .delete(localData)
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean b) {
                        if (b) {
                            notifyDataSetChanged();
                        }
                    }
                });
    }

    private void notifyDataSetChanged() {
        localDataModel.getAll()
                .compose(RxHelper.<List<LocalData>>applySchedulers())
                .subscribe(new Action1<List<LocalData>>() {
                    @Override
                    public void call(List<LocalData> items) {
                        publishSubject.onNext(fromLocal(items));
                    }
                });
    }

    List<Data> fromLocal(List<LocalData> items) {
        if (items == null || items.size() == 0) {
            return new ArrayList<>();
        }
        final List<Data> result = new ArrayList<>(items.size());
        for (LocalData item : items) {
            final Data data = converter.fromLocal(item);
            result.add(data);
        }
        return result;
    }

    List<Data> fromRemote(List<RemoteData> items) {
        if (items == null || items.size() == 0) {
            return new ArrayList<>();
        }
        final List<Data> result = new ArrayList<>(items.size());
        for (RemoteData item : items) {
            final Data data = converter.fromRemote(item);
            result.add(data);
        }
        return result;
    }
}
