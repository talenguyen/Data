package com.tale.data.sample.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.tale.data.sample.model.User;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Giang Nguyen on 4/2/2015.
 */
public class UserViewHolder extends RecyclerView.ViewHolder {

    @InjectView(android.R.id.text1)
    TextView tvLogin;

    public UserViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    public void bind(User user) {
        tvLogin.setText(user.login);
    }
}
