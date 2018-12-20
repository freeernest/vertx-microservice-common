package com.bigpanda.commons.sql;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.*;
import org.springframework.beans.factory.FactoryBean;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by erik on 4/26/18.
 */
public class PostgreSQLClientFactoryObject implements FactoryBean<SQLClient> {

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
        return PostgreSQLClient.createShared(vertx, dataSourceConfig);
    }

    @Override
    public Class<?> getObjectType() {
        return SQLClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private Map<String, StackTraceElement[]> map = new ConcurrentHashMap<>();

    public class MetricsSQLClient implements SQLClient {

        private SQLClient sqlClient;

        public MetricsSQLClient(SQLClient sqlClient) {
            this.sqlClient = sqlClient;
        }

        @Override
        @Fluent
        public SQLClient getConnection(Handler<AsyncResult<SQLConnection>> handler) {
            final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            Map<String, StackTraceElement[]> localMap = map;
            return sqlClient.getConnection(event -> {
                if (event.succeeded()) {
                    String uuid = UUID.randomUUID().toString();
                    localMap.put(uuid, stackTraceElements);
                    handler.handle(Future.succeededFuture(new MetricsSQLConnection(event.result(), uuid)));
                } else {
                    handler.handle(Future.failedFuture(event.cause()));
                }
            });
        }

        @Override
        public void close(Handler<AsyncResult<Void>> handler) {
            sqlClient.close(handler);
        }

        @Override
        public void close() {
            close(ar -> {});
        }

        @Override
        @Fluent
        public SQLClient query(String sql, Handler<AsyncResult<ResultSet>> handler) {
            return sqlClient.query(sql, handler);
        }

        @Override
        @Fluent
        public SQLClient queryWithParams(String sql, JsonArray arguments, Handler<AsyncResult<ResultSet>> handler) {
            return sqlClient.queryWithParams(sql, arguments, handler);
        }

        @Override
        @Fluent
        public SQLClient update(String sql, Handler<AsyncResult<UpdateResult>> handler) {
            return sqlClient.update(sql, handler);
        }

        @Override
        @Fluent
        public SQLClient updateWithParams(String sql, JsonArray params, Handler<AsyncResult<UpdateResult>> handler) {
            return sqlClient.updateWithParams(sql, params, handler);
        }

        @Override
        @Fluent
        public SQLClient call(String sql, Handler<AsyncResult<ResultSet>> handler) {
            return sqlClient.call(sql, handler);
        }

        @Override
        @Fluent
        public SQLClient callWithParams(String sql, JsonArray params, JsonArray outputs, Handler<AsyncResult<ResultSet>> handler) {
            return sqlClient.callWithParams(sql, params, outputs, handler);
        }

        @Override
        @Fluent
        public SQLOperations querySingle(String sql, Handler<AsyncResult<JsonArray>> handler) {
            return sqlClient.querySingle(sql, handler);
        }

        @Override
        @Fluent
        public SQLOperations querySingleWithParams(String sql, JsonArray arguments, Handler<AsyncResult<JsonArray>> handler) {
            return sqlClient.querySingleWithParams(sql, arguments, handler);
        }

    }

    public class MetricsSQLConnection implements SQLConnection {

        private final String id;
        private final SQLConnection sqlConnection;

        public MetricsSQLConnection(SQLConnection sqlConnection, String id) {
            this.sqlConnection = sqlConnection;
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        @Fluent
        public SQLConnection setOptions(SQLOptions options) {
            return sqlConnection.setOptions(options);
        }

        @Override
        @Fluent
        public SQLConnection setAutoCommit(boolean autoCommit, Handler<AsyncResult<Void>> resultHandler) {
            return sqlConnection.setAutoCommit(autoCommit, resultHandler);
        }

        @Override
        @Fluent
        public SQLConnection execute(String sql, Handler<AsyncResult<Void>> resultHandler) {
            return sqlConnection.execute(sql, resultHandler);
        }

        @Override
        @Fluent
        public SQLConnection query(String sql, Handler<AsyncResult<ResultSet>> resultHandler) {
            return sqlConnection.query(sql, resultHandler);
        }

        @Override
        @Fluent
        public SQLConnection queryStream(String sql, Handler<AsyncResult<SQLRowStream>> handler) {
            return sqlConnection.queryStream(sql, handler);
        }

        @Override
        @Fluent
        public SQLConnection queryWithParams(String sql, JsonArray params, Handler<AsyncResult<ResultSet>> resultHandler) {
            return sqlConnection.queryWithParams(sql, params, resultHandler);
        }

        @Override
        @Fluent
        public SQLConnection queryStreamWithParams(String sql, JsonArray params, Handler<AsyncResult<SQLRowStream>> handler) {
            return sqlConnection.queryStreamWithParams(sql, params, handler);
        }

        @Override
        @Fluent
        public SQLConnection update(String sql, Handler<AsyncResult<UpdateResult>> resultHandler) {
            return sqlConnection.update(sql, resultHandler);
        }

        @Override
        @Fluent
        public SQLConnection updateWithParams(String sql, JsonArray params, Handler<AsyncResult<UpdateResult>> resultHandler) {
            return sqlConnection.updateWithParams(sql, params, resultHandler);
        }

        @Override
        @Fluent
        public SQLConnection call(String sql, Handler<AsyncResult<ResultSet>> resultHandler) {
            return sqlConnection.call(sql, resultHandler);
        }

        @Override
        @Fluent
        public SQLConnection callWithParams(String sql, JsonArray params, JsonArray outputs, Handler<AsyncResult<ResultSet>> resultHandler) {
            return sqlConnection.callWithParams(sql, params, outputs, resultHandler);
        }

        @Override
        public void close(Handler<AsyncResult<Void>> handler) {
            Map<String, StackTraceElement[]> localMap = map;
            sqlConnection.close(event -> {
                if (event.succeeded()) {
                    localMap.remove(id);
                    handler.handle(Future.succeededFuture());
                } else {
                    handler.handle(Future.failedFuture(event.cause()));
                }
            });
        }

        @Override
        public void close() {
            close(ar -> {});
        }

        @Override
        @Fluent
        public SQLConnection commit(Handler<AsyncResult<Void>> handler) {
            return sqlConnection.commit(handler);
        }

        @Override
        @Fluent
        public SQLConnection rollback(Handler<AsyncResult<Void>> handler) {
            return sqlConnection.rollback(handler);
        }

        @Override
        @Deprecated
        @Fluent
        public SQLConnection setQueryTimeout(int timeoutInSeconds) {
            return sqlConnection.setQueryTimeout(timeoutInSeconds);
        }

        @Override
        @Fluent
        public SQLConnection batch(List<String> sqlStatements, Handler<AsyncResult<List<Integer>>> handler) {
            return sqlConnection.batch(sqlStatements, handler);
        }

        @Override
        @Fluent
        public SQLConnection batchWithParams(String sqlStatement, List<JsonArray> args, Handler<AsyncResult<List<Integer>>> handler) {
            return sqlConnection.batchWithParams(sqlStatement, args, handler);
        }

        @Override
        @Fluent
        public SQLConnection batchCallableWithParams(String sqlStatement, List<JsonArray> inArgs, List<JsonArray> outArgs, Handler<AsyncResult<List<Integer>>> handler) {
            return sqlConnection.batchCallableWithParams(sqlStatement, inArgs, outArgs, handler);
        }

        @Override
        @Fluent
        public SQLConnection setTransactionIsolation(TransactionIsolation isolation, Handler<AsyncResult<Void>> handler) {
            return sqlConnection.setTransactionIsolation(isolation, handler);
        }

        @Override
        @Fluent
        public SQLConnection getTransactionIsolation(Handler<AsyncResult<TransactionIsolation>> handler) {
            return sqlConnection.getTransactionIsolation(handler);
        }

        @Override
        @GenIgnore
        public <N> N unwrap() {
            return sqlConnection.unwrap();
        }

        @Override
        @Fluent
        public SQLOperations querySingle(String sql, Handler<AsyncResult<JsonArray>> handler) {
            return sqlConnection.querySingle(sql, handler);
        }

        @Override
        @Fluent
        public SQLOperations querySingleWithParams(String sql, JsonArray arguments, Handler<AsyncResult<JsonArray>> handler) {
            return sqlConnection.querySingleWithParams(sql, arguments, handler);
        }

    }
}
