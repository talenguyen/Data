package com.tale.data.sample.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tale.data.sample.model.User;
import com.tale.data.sample.viewholder.UserViewHolder;

/**
 * Created by Giang Nguyen on 4/2/2015.
 */
public class UsersAdapter extends RecyclerTypedAdapter<User, UserViewHolder> {

    private final LayoutInflater layoutInflater;

    public UsersAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
