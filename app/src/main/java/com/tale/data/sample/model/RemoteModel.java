package com.tale.data.sample.model;

import com.tale.data.Model;

import java.util.List;

import rx.Observable;

/**
 * Created by tale on 5/11/15.
 */
public class RemoteModel implements Model<User> {

    private final GitApi gitApi;

    public RemoteModel(GitApi gitApi) {
        this.gitApi = gitApi;
    }

    @Override
    public Observable<User> getById(String id) {
        return null;
    }

    @Override
    public Observable<List<User>> getAll() {
        return gitApi.getAll();
    }

    @Override
    public Observable<Boolean> save(User data) {
        return null;
    }

    @Override
    public Observable<Boolean> saveAll(List<User> dataSet) {
        return null;
    }
}
