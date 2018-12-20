package com.bigpanda.commons.json.refs;

/**
 * Created by erik on 10/26/17.
 */
class JsonRef {
    private String path;

    public JsonRef(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
