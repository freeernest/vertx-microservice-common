package com.bigpanda.commons.sql.exceptions;

import com.bigpanda.commons.validations.InputValidator;
import com.bigpanda.commons.web.exceptions.InputValidationException;
import com.github.mauricio.async.db.postgresql.exceptions.GenericDatabaseException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

/**
 * Created by erik on 7/11/18.
 */
public class GenericSqlException extends InputValidationException {

    public GenericSqlException(GenericDatabaseException e) {
        super(parseException(e));
    }

    public static <T> AsyncResult<T> fail(GenericDatabaseException e) {
        return Future.failedFuture(new GenericSqlException(e));
    }

    private static JsonArray parseException(GenericDatabaseException e) {
        if (e.getMessage().contains("violates foreign key constraint")) {

            return InputValidator.addError(parseColumnName(e), "the value not exists");
        } else if (e.getMessage().contains("duplicate key value")) {

            return InputValidator.addError(parseColumnName(e), "the value already exists");
        }
        return null;
    }

    private static String parseColumnName(GenericDatabaseException e) {
        try {
            int columnNameStartPos = e.getMessage().indexOf("Key (") + 5;
            int columnNameEndPos = e.getMessage().indexOf(")", columnNameStartPos);
            return e.getMessage().substring(columnNameStartPos, columnNameEndPos);
        } catch (Exception e1) {}

        return null;
    }
}
