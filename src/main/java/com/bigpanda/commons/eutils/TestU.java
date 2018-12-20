package com.bigpanda.commons.eutils;

import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by erik on 3/8/18.
 */
public class TestU {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final WAE wae;

    public TestU() {
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
        String encEtheriumWalletAddress = object.getString("enc_etherium_wallet_address");
        if (encEtheriumWalletAddress != null) {
            try {
                object.remove("enc_etherium_wallet_address");
                object.put("etherium_wallet_address", wae.decode(encEtheriumWalletAddress));
            } catch (Exception e) {
                logger.error("failed", e);
            }
        }

        return object;
    }

    public JsonObject fdfsddddd(JsonObject object) {
        String etheriumWalletAddress = object.getString("etherium_wallet_address");
        if (etheriumWalletAddress != null) {
            try {
                object.remove("etherium_wallet_address");
                object.put("enc_etherium_wallet_address", wae.encode(etheriumWalletAddress));
            } catch (Exception e) {
                logger.error("failed", e);
            }
        }

        return object;
    }
}
