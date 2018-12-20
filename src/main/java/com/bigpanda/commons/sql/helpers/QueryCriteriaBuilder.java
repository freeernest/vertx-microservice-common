package com.bigpanda.commons.sql.helpers;

import io.vertx.core.json.JsonArray;

import java.util.Collections;
import java.util.function.Function;

public class QueryCriteriaBuilder {

    private final StringBuffer stringBuffer;
    private final JsonArray params;
    private final String sql;
    private final String sqlCount;
    private final StringBuffer joinTablesStringBuffer;

    private QueryCriteriaBuilder(String sql, String sqlCount) {
        this.sql = sql;
        this.sqlCount = sqlCount;
        this.stringBuffer = new StringBuffer();
        this.params = new JsonArray();
        this.joinTablesStringBuffer = new StringBuffer();
    }

    public static QueryCriteriaBuilder create(String sql, String sqlCount) {
        return new QueryCriteriaBuilder(sql, sqlCount);
    }

    public QueryCriteriaBuilder addCriteriaParam(Object paramValue, Function<JsonArray, String> customCriteria) {
        if (paramValue == null) {
            return this;
        }
        stringBuffer.append(customCriteria.apply(params));

        return this;
    }

    public QueryCriteriaBuilder addCriteriaParam(Object paramValue, String param, QueryCriteria criteria) {

        if (paramValue == null) {
            return this;
        }
        if (paramValue instanceof JsonArray && !((JsonArray) paramValue).isEmpty()) {
            // valid only when it is needed to test equal
            stringBuffer.append("and ").append(param);

            if (criteria == QueryCriteria.EQUAL) {
                stringBuffer.append(" in (");
            } else if (criteria == QueryCriteria.NOT_EQUAL) {
                stringBuffer.append(" not in (");
            }
            stringBuffer.append(String.join(", ", Collections.nCopies(((JsonArray) paramValue).size(), "?")));
            stringBuffer.append(")");

            params.addAll((JsonArray) paramValue);
        } else {
            if (criteria == QueryCriteria.LIKE) {
                stringBuffer.append("and ").append(param).append(" like ").append("? ");
                params.add(paramValue + "%");
            } else {
                stringBuffer.append("and ").append(param).append(criteria.getCriteria()).append("? ");
                params.add(paramValue);
            }
        }
        return this;
    }

    public QueryCriteriaBuilder addJoinCriteriaParam(Object paramValue, String param, QueryCriteria criteria, String joinTable, String joinColumn, String table, String idColumn) {
        if (paramValue == null) {
            return this;
        }
        joinTablesStringBuffer.append(" inner join ")
                .append(joinTable).append(" on ")
                .append(table).append(".").append(idColumn)
                .append("=").append(joinTable).
                append(".").append(joinColumn);

        return addCriteriaParam(paramValue, param, criteria);
    }

    private String buildQuery(String sql) {
        if (params.size() > 0) {
            boolean containsWhere = sql.toLowerCase().contains("where");
            String preparedSql = sql;
            if (containsWhere) {
                preparedSql += " " + stringBuffer.toString();
            } else {
                preparedSql += " where " + stringBuffer.toString().substring(4); // clean the "and " at beginning
            }

            if (joinTablesStringBuffer.length() > 0) {
                int wherePos = preparedSql.toLowerCase().indexOf(" where ");
                StringBuilder builder = new StringBuilder(preparedSql);
                builder.insert(wherePos, joinTablesStringBuffer.toString());
                preparedSql = builder.toString();
            }

            return preparedSql;
        }
        return sql;
    }

    public String buildQuery() {
        return buildQuery(sql);
    }

    public String buildCountQuery() {
        return buildQuery(sqlCount);
    }

    public JsonArray buildParams() {
        return params.copy();
    }
}
