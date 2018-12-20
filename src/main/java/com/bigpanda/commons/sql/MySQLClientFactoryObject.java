package com.bigpanda.commons.sql;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.SQLClient;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by erik on 4/26/18.
 */
public class MySQLClientFactoryObject implements FactoryBean<SQLClient> {

    private Vertx vertx;
    private JsonObject dataSourceConfig;

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    public void setDataSourceConfig(JsonObject dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    @Override
    public SQLClient getObject() throws Exception {
        return MySQLClient.createShared(vertx, dataSourceConfig);
    }

    @Override
    public Class<?> getObjectType() {
        return SQLClient.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
