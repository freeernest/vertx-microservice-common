package com.bigpanda.commons.audit;

import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Audit {
    private static Logger logger = LoggerFactory.getLogger("audit");

    public static void audit(JsonObject data) {
        logger.info(data.toString());
    }

    public static JsonObject createAuditObject(JsonObject data, String action) {
        String user = data.getString("crmUser");
        data.remove("crmUser");
        JsonObject audit = new JsonObject().put("timestamp", System.currentTimeMillis())
                .put("action", action)
                .put("user", user)
                .put("data",  data.copy());
        return audit;
    }
}
