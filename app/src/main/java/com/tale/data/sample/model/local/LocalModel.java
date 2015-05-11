package com.tale.data.sample.model.local;

import android.app.Application;

import com.tale.data.Model;
import com.tale.data.sample.model.User;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;

/**
 * Created by tale on 5/11/15.
 */
public class LocalModel implements Model<User> {

    private final Application application;

    public LocalModel(Application application) {
        this.application = application;
    }

    @Override
    public Observable<User> getById(String id) {
        return null;
    }

    @Override
    public Observable<List<User>> getAll() {
        return Observable.defer(new Func0<Observable<List<User>>>() {
            @Override
            public Observable<List<User>> call() {
                return Observable.create(new Observable.OnSubscribe<List<User>>() {
                    @Override
                    public void call(Subscriber<? super List<User>> subscriber) {
                        Realm realm = null;
                        try {
                            List<User> result = null;
                            realm = Realm.getInstance(application);
                            final RealmResults<RealmUser> realmUsers = realm.allObjects(RealmUser.class);
                            if (realmUsers != null && realmUsers.size() > 0) {
                                result = new ArrayList<>(realmUsers.size());
                                for (RealmUser realmUser : realmUsers) {
                                    final String login = realmUser.getLogin();
                                    final String avatar_url = realmUser.getAvatar_url();
                                    final User user = new User(login, avatar_url);
                                    result.add(user);
                                }

                            }
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(result);
                            }
                        } catch (Exception e) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(e);
                            }
                        } finally {
                            if (realm != null) {
                                realm.close();
                            }
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onCompleted();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public Observable<Boolean> save(User data) {
        return null;
    }

    @Override
    public Observable<Boolean> saveAll(final List<User> dataSet) {
        return Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                return Observable.create(new Observable.OnSubscribe<Boolean>() {
                    @Override
                    public void call(Subscriber<? super Boolean> subscriber) {
                        try {
                            final Realm realm = Realm.getInstance(application);
                            final RealmResults<RealmUser> realmUsers = realm.allObjects(RealmUser.class);
                            realm.beginTransaction();
                            if (realmUsers != null) {
                                realmUsers.clear();
                            }
                            if (dataSet != null && dataSet.size() > 0) {
                                for (User user : dataSet) {
                                    final RealmUser realmUser = realm.createObject(RealmUser.class);
                                    realmUser.setLogin(user.login);
                                    realmUser.setAvatar_url(user.avatar_url);
                                }
                            }
                            realm.commitTransaction();
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(e);
                            }
                        } finally {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onCompleted();
                            }
                        }
                    }
                });
            }
        });
    }

}
