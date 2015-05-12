package com.tale.data.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tale.data.DataPackage;
import com.tale.data.Repository;
import com.tale.data.Source;
import com.tale.data.sample.adapter.UsersAdapter;
import com.tale.data.sample.model.GitApi;
import com.tale.data.sample.model.RemoteModel;
import com.tale.data.sample.model.User;
import com.tale.data.sample.model.local.LocalModel;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RestAdapter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * A placeholder fragment containing a simple view.
 */
public class UsersFragment extends Fragment {
    public static final String BASE_URL = "https://api.github.com";

    @InjectView(R.id.rvUsers)
    RecyclerView rvUsers;
    private UsersAdapter usersAdapter;
    private GitApi gitApi;
    private Repository<User> userRepository;

    public UsersFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, view);

        rvUsers.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        usersAdapter = new UsersAdapter(inflater);
        rvUsers.setAdapter(usersAdapter);

        gitApi = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .build()
                .create(GitApi.class);


        userRepository = new Repository<>(new LocalModel(getActivity().getApplication()), new RemoteModel(gitApi));

        userRepository.listItemObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DataPackage<List<User>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(DataPackage<List<User>> listDataPackage) {
                        if (listDataPackage.source == Source.Network) {
                            // TODO: handle network call done.
                        }
                        usersAdapter.changeDataSet(listDataPackage.data);
                    }
                });

        userRepository.pull();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
