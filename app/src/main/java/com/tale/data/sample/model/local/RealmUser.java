package com.tale.data.sample.model.local;

import io.realm.RealmObject;

/**
 * Created by tale on 5/11/15.
 */
public class RealmUser extends RealmObject {

    private String login;//": "octocat",
    private String avatar_url;//": "https://github.com/images/error/octocat_happy.gif",

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
}
