package com.bigpanda.commons.services;

import com.bigpanda.commons.sql.exceptions.GenericSqlException;
import com.github.mauricio.async.db.postgresql.exceptions.GenericDatabaseException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

import static com.bigpanda.commons.async.SafeAsyncResult.async;

/**
 * Created by erik on 11/14/17.
 */
public class SqlRepositoryWrapper extends AbstractService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    protected SQLClient client;

    public void setSqlClient(SQLClient client) {
        this.client = client;
    }

    /**
     * Suitable for `add`, `exists` operation.
     *
     * @param params        query params
     * @param sql           sql
     * @param resultHandler async result handler
     */
    protected void execute(JsonArray params, String sql, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        client.getConnection(async(connHandler(resultHandler, connection -> {
            execute(connection, params, sql, resultHandler, connectionEvent -> connectionEvent.close());
        })));
    }

    protected void execute(SQLConnection connection, JsonArray params, String sql, Handler<AsyncResult<List<JsonObject>>> resultHandler, Handler<SQLConnection> leaveConnection) {
        connection.queryWithParams(sql + " RETURNING *", params, r -> {

            try {
                if (r.succeeded()) {
                    resultHandler.handle(Future.succeededFuture(r.result().getRows()));
                } else {
                    if (r.cause() instanceof GenericDatabaseException) {
                        resultHandler.handle(GenericSqlException.fail((GenericDatabaseException) r.cause()));
                    } else {
                        resultHandler.handle(Future.failedFuture(r.cause()));
                        logger.error("An error", r.cause());
                    }
                }
            } catch (Exception e) {
                logger.error("Error in handlers", e);
            } finally {
                if (leaveConnection != null) {
                    leaveConnection.handle(connection);
                }
            }
        });
    }

    protected Future<List<JsonObject>> execute(SQLConnection connection, JsonArray params, String sql, Handler<SQLConnection> leaveConnection) {
        Future<List<JsonObject>> future = Future.future();
        execute(connection, params, sql, future.completer(), leaveConnection);
        return future;
    }

    protected <R> void executeNoResult(JsonArray params, String sql, R ret, Handler<AsyncResult<R>> resultHandler) {
        client.getConnection(async(connHandler(resultHandler, connection -> {
            connection.queryWithParams(sql, params, r -> {

                try {
                    if (r.succeeded()) {
                        resultHandler.handle(Future.succeededFuture(ret));
                    } else {
                        if (r.cause() instanceof GenericDatabaseException) {
                            resultHandler.handle(GenericSqlException.fail((GenericDatabaseException) r.cause()));
                        } else {
                            resultHandler.handle(Future.failedFuture(r.cause()));
                            logger.error("An error", r.cause());
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error in handlers", e);
                } finally {
                    connection.close();
                }
            });
        })));
    }


    protected <K> Future<JsonObject> retrieveOne(K param, String sql) {
        Future<JsonObject> future = Future.future();
        if (param instanceof JsonArray) {
            return retrieveOne((JsonArray) param, sql, future);
        }
        return retrieveOne(new JsonArray().add(param), sql, future);
    }

    protected Future<JsonObject> retrieveOne(JsonArray params, String sql, Future<JsonObject> future) {
        return getConnection()
                .compose(connection -> {
                    connection.queryWithParams(sql, params, r -> {

                        try {
                            if (r.succeeded()) {
                                List<JsonObject> resList = r.result().getRows();
                                if (resList == null || resList.isEmpty()) {
                                    future.complete();
                                } else {
                                    future.complete(resList.get(0));
                                }
                            } else {
                                future.fail(r.cause());
                                logger.error("An error", r.cause());
                            }
                        } catch (Exception e) {
                            logger.error("Error in handlers", e);
                        } finally {
                            connection.close();
                        }
                    });
                    return future;
                });
    }

    protected int calcPage(int page, int limit) {
        if (page <= 0)
            return 0;
        return limit * (page - 1);
    }

    protected Future<List<JsonObject>> retrieveByPage(int page, int limit, String sql) {
        JsonArray params = new JsonArray().add(calcPage(page, limit)).add(limit);
        return getConnection().compose(getQueryWithParamResultListFuture(params, sql, connectionEvent -> connectionEvent.close()));
    }

    protected Future<List<JsonObject>> retrieveByPage(SQLConnection sqlConnection, int page, int limit, String sql, Handler<SQLConnection> leaveConnection) {
        JsonArray params = new JsonArray().add(calcPage(page, limit)).add(limit);
        return getQueryWithParamResultListFuture(params, sql, leaveConnection).apply(sqlConnection);
    }

    protected Future<List<JsonObject>> retrieveMany(JsonArray param, String sql) {
        Future<List<JsonObject>> future = Future.future();
        client.getConnection(ar -> {

            if (ar.succeeded()) {
                SQLConnection connection = ar.result();
                connection.queryWithParams(sql, param, r -> {

                    try {
                        if (r.succeeded()) {
                            future.complete(r.result().getRows());
                        } else {
                            future.fail(r.cause());
                            logger.error("An error", r.cause());
                        }
                    } catch (Exception e) {
                        logger.error("Error in handlers", e);
                    } finally {
                        connection.close();
                    }
                });
            } else {
                future.fail(ar.cause());
                logger.error("An error", ar.cause());
            }
        });
        return future;
    }

    protected Future<List<JsonObject>> retrieveMany(SQLConnection sqlConnection, JsonArray param, String sql, Handler<SQLConnection> leaveConnection) {
        return getQueryWithParamResultListFuture(param, sql, leaveConnection).apply(sqlConnection);
    }

    private Function<SQLConnection, Future<List<JsonObject>>> getQueryWithParamResultListFuture(JsonArray param, String sql, Handler<SQLConnection> leaveConnection) {
        return connection -> {
            Future<List<JsonObject>> future = Future.future();
            connection.queryWithParams(sql, param, r -> {

                try {
                    if (r.succeeded()) {
                        future.complete(r.result().getRows());
                    } else {
                        future.fail(r.cause());
                        logger.error("An error", r.cause());
                    }
                } catch (Exception e) {
                    logger.error("Error in handlers", e);
                } finally {
                    if (leaveConnection != null) {
                        leaveConnection.handle(connection);
                    }
                }
            });
            return future;
        };
    }

    protected Future<List<JsonObject>> retrieveAll(String sql) {
        return getConnection().compose(connection -> {
            Future<List<JsonObject>> future = Future.future();
            connection.query(sql, r -> {

                try {
                    if (r.succeeded()) {
                        future.complete(r.result().getRows());
                    } else {
                        future.fail(r.cause());
                        logger.error("An error", r.cause());
                    }
                } catch (Exception e) {
                    logger.error("Error in handlers", e);
                } finally {
                    connection.close();
                }
            });
            return future;
        });
    }

    protected <K> void removeOne(K id, String sql, Handler<AsyncResult<Void>> resultHandler) {
        client.getConnection(connHandler(resultHandler, connection -> {
            JsonArray params = new JsonArray().add(id);
            connection.updateWithParams(sql, params, r -> {

                try {
                    if (r.succeeded()) {
                        resultHandler.handle(Future.succeededFuture());
                    } else {
                        resultHandler.handle(Future.failedFuture(r.cause()));
                        logger.error("An error", r.cause());
                    }
                } catch (Exception e) {
                    logger.error("Error in handlers", e);
                } finally {
                    connection.close();
                }

            });
        }));
    }

    protected void removeAll(String sql, Handler<AsyncResult<Void>> resultHandler) {
        client.getConnection(connHandler(resultHandler, connection -> {
            connection.update(sql, r -> {

                try {
                    if (r.succeeded()) {
                        resultHandler.handle(Future.succeededFuture());
                    } else {
                        resultHandler.handle(Future.failedFuture(r.cause()));
                        logger.error("An error", r.cause());
                    }
                } catch (Exception e) {
                    logger.error("Error in handlers", e);
                } finally {
                    connection.close();
                }
            });
        }));
    }

    protected <R> Handler<AsyncResult<Void>> autoCommitHandler(Handler<AsyncResult<R>> h1, Handler<Void> h2) {
        return conn -> {
            if (conn.succeeded()) {
                h2.handle(null);
            } else {
                h1.handle(Future.failedFuture(conn.cause()));
            }
        };
    }

    /**
     * A helper methods that generates async handler for SQLConnection
     *
     * @return generated handler
     */
    protected <R> Handler<AsyncResult<SQLConnection>> connHandler(Handler<AsyncResult<R>> h1, Handler<SQLConnection> h2) {
        return conn -> {
            if (conn.succeeded()) {
                final SQLConnection connection = conn.result();
                h2.handle(connection);
            } else {
                h1.handle(Future.failedFuture(conn.cause()));
            }
        };
    }

    protected Future<SQLConnection> getConnection() {
        Future<SQLConnection> future = Future.future();
        client.getConnection(future.completer());
        return future;
    }

    protected Future<Void> closeTransactionRollbackIfNeeded(SQLConnection connection, boolean succeeded) {
        Future<Void> future = Future.future();
        if (connection != null) {
            if (succeeded) {
                connection.close(future.completer());
            } else {
                connection.rollback(event -> {
                    connection.close(future.completer());
                });
            }
        } else {
            future.complete();
        }
        return future;
    }

    public void close() {
        client.close();
    }
}
