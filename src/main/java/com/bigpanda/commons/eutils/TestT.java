package com.bigpanda.commons.eutils;

import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by erik on 3/8/18.
 */
public class TestT {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final WAE wae;

    public TestT() {
        WAE wae;
        try {
            wae = new WAE();
        } catch (IOException e) {
            logger.error("An error", e);
            wae = null;
        }
        this.wae = wae;
    }

    public JsonObject fdfsd(JsonObject object) {
        String encToAddress = object.getString("enc_to_address");
        if (encToAddress != null) {
            try {
                object.remove("enc_to_address");
                object.put("to_address", wae.decode(encToAddress));
            } catch (Exception e) {
                logger.error("failed", e);
            }
        }

        return object;
    }

    public JsonObject fdfsddddd(JsonObject object) {
        String toAddress = object.getString("to_address");
        if (toAddress != null) {
            try {
                object.remove("to_address");
                object.put("enc_to_address", wae.encode(toAddress));
            } catch (Exception e) {
                logger.error("failed", e);
            }
        }

        return object;
    }
}
