package com.tale.data.sample.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tale on 5/11/15.
 */
public class User {
    @SerializedName("login")
    public String login;//": "octocat",
    @SerializedName("avatar_url")
    public String avatar_url;//": "https://github.com/images/error/octocat_happy.gif",

    public User(String login, String avatar_url) {
        this.login = login;
        this.avatar_url = avatar_url;
    }
}
