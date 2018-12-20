package com.bigpanda.commons.sql.helpers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by erik on 11/14/17.
 */
public class JsonToSqlHelper {

    public static String insert(JsonObject object, JsonArray params, String table) {
        return insert(object, params, table, null);
    }

    public static String insert(JsonObject object, JsonArray params, String table, List<String> model) {
        String columns = "";
        String values = "";

        for (Map.Entry<String, Object> entry : object.getMap().entrySet()) {

            if (model != null && !model.contains(entry.getKey())) {
                continue;
            }

            columns += "\"" + entry.getKey() + "\",";
            values += "?,";

            addEntryParam(params, entry);
        }

        columns = columns.substring(0, columns.length() - 1);
        values = values.substring(0, values.length() - 1);

        return new StringBuilder("insert into \"")
                .append(table)
                .append("\" (")
                .append(columns)
                .append(") values (")
                .append(values)
                .append(")")
                .toString();
    }

    private static void addEntryParam(JsonArray params, Map.Entry<String, Object> entry) {
        if (entry.getValue() != null) {

            if (entry.getValue() instanceof JsonObject) {
                params.add(entry.getValue().toString());
            } else {
                params.add(entry.getValue());
            }
        } else {
            params.addNull();
        }
    }

    public static String upsert(JsonObject object, JsonArray params, String table, String pk) {
        String insert = insert(object, params, table);
        String update = "";

        for (Map.Entry<String, Object> entry : object.getMap().entrySet()) {

            update += "\"" + entry.getKey() + "\"=?,";

            addEntryParam(params, entry);
        }

        update = update.substring(0, update.length() - 1);

        return new StringBuilder(insert)
                .append(" ON CONFLICT (")
                .append(pk)
                .append(") DO UPDATE SET ")
                .append(update)
                .toString();
    }

    public static String update(JsonObject object, JsonArray params, String table) {
        String columns = "";

        for (Map.Entry<String, Object> entry : object.getMap().entrySet()) {

            columns += "\"" + entry.getKey() + "\" = ?,";

            addEntryParam(params, entry);
        }

        columns = columns.substring(0, columns.length() - 1);

        return new StringBuilder("UPDATE \"")
                .append(table)
                .append("\" SET ")
                .append(columns)
                .toString();
    }

    public static String delete(JsonObject object, JsonArray params, String table, List<String> pk) {
        String columns = "";

        for (Map.Entry<String, Object> entry : object.getMap().entrySet()) {
            String key = entry.getKey();

            if (pk.contains(key)) {
                columns += "\"" + key + "\" = ? AND ";
                addEntryParam(params, entry);
            }
        }

        columns = columns.substring(0, columns.length() - 5);

        return new StringBuilder("DELETE FROM \"")
                .append(table)
                .append("\" WHERE ")
                .append(columns)
                .toString();
    }

    public static String increment(String field, String table) {
        return new StringBuilder("UPDATE \"")
                .append(table)
                .append("\" SET ")
                .append(field + " = " + field + " + 1")
                .toString();
    }

    public static String decrement(String field, String table) {
        return new StringBuilder("UPDATE \"")
                .append(table)
                .append("\" SET ")
                .append(field + " = " + field + " - 1")
                .toString();
    }

    public static String select(String table, List<String> model) {
        String columns = "";

        for (String s : model) {
            columns += "" + s + ",";
        }

        columns = columns.substring(0, columns.length() - 1);

        return new StringBuilder("select ")
                .append(columns)
                .append(" from \"")
                .append(table)
                .append("\"")
                .toString();
    }
}
