package com.bigpanda.commons.sql.helpers;

public enum QueryCriteria {
    EQUAL("=")
    , NOT_EQUAL("!=")
    , GREATER(">")
    , LESS("<")
    , GREATER_EQUAL(">=")
    , LESS_EQUAL("<=")
    , LIKE("");

    QueryCriteria(String criteria) {
        this.criteria = criteria;
    }

    private final String criteria;

    public String getCriteria() {
        return criteria;
    }
}
