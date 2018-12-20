package com.bigpanda.commons.sql.helpers;

import io.vertx.core.json.JsonArray;

/**
 * Created by erik on 7/25/17.
 */
public class QueryBuilder {

    private final QueryCriteriaBuilder queryCriteriaBuilder;
    private int pageIndex = -1;
    private int pageSize = -1;
    private Object sorting;

    private QueryBuilder(String sql, String sqlCount) {
        queryCriteriaBuilder = QueryCriteriaBuilder.create(sql, sqlCount);
    }

    public static QueryBuilder create(String sql) {
        return new QueryBuilder(sql, null);
    }

    public static QueryBuilder create(String sql, String sqlCount) {
        return new QueryBuilder(sql, sqlCount);
    }

    public QueryBuilder applyPagination(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        return this;
    }

    public QueryBuilder applySorting(Object sorting) {
        this.sorting = sorting;
        return this;
    }

    public String buildQuery() {
        final StringBuilder builder = new StringBuilder(queryCriteriaBuilder.buildQuery());
        if (sorting != null) {
            builder.append(" order by ");
            // TODO think how to prevent sql injection in sorting

            if (sorting instanceof JsonArray) {
                ((JsonArray) sorting).forEach(o -> builder.append(buildFieldSortQuery(o.toString())).append(","));

                builder.deleteCharAt(builder.length() - 1);// remove trailing comma
            } else {
                builder.append(buildFieldSortQuery(sorting.toString()));
            }
        }
        if (pageIndex != -1 && pageSize != -1) {
            builder.append(" limit ? offset ?");
        }
        return builder.toString();
    }

    private String buildFieldSortQuery(String sort) {
        StringBuilder builder = new StringBuilder();
        builder.append(sort.replace("-", "")); // remove sort direction.
        builder.append(" ");
        builder.append(sort.startsWith("-") ? "desc" : "asc");
        return builder.toString();
    }

    public JsonArray buildParams() {
        JsonArray params = queryCriteriaBuilder.buildParams();
        if (pageIndex != -1 && pageSize != -1) {
            params.add(pageSize).add(pageIndex * pageSize);
        }

        return params;
    }

    public String buildCountQuery() {
        return queryCriteriaBuilder.buildCountQuery();
    }

    public JsonArray buildCountParams() {
        return queryCriteriaBuilder.buildParams();
    }

    public QueryCriteriaBuilder criteriaBuilder() {
        return queryCriteriaBuilder;
    }
}