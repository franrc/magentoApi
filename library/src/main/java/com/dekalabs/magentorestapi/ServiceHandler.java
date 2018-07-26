package com.dekalabs.magentorestapi;

/**
 * Created by dekalabs
 */
public interface ServiceHandler<ServerType, DBType> {
    void manageResults(ServerType results);
    DBType retrieveFromDatabase();
}
