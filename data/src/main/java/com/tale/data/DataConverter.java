package com.tale.data;

/**
 * Created by tale on 5/4/15.
 */
public interface DataConverter<Data, LocalData, RemoteData> {

    Data fromLocal(LocalData localData);

    Data fromRemote(RemoteData remoteData);

    LocalData toLocal(Data data);

    RemoteData toRemote(Data data);

}
