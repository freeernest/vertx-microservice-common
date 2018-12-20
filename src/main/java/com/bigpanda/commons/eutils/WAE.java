package com.bigpanda.commons.eutils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class WAE implements EHandler {
    public static final String DATA_CS_SEPERATOR = ";";
    private EHandler eHandler = new EHandlerImp("NORHm8Hv+C1Baaw5Ag9S6g==");

    public WAE() throws IOException {
    }

    public void seteHandler(EHandler eHandler) {
        this.eHandler = eHandler;
    }

    @Override
    public String encode(String data) throws Exception {
        return eHandler.encode(data+";"+ csData(data));
    }

    private String csData(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        return Base64.getEncoder().encodeToString(digest.digest(data.getBytes()));
    }

    @Override
    public String decode(String data) throws Exception {
        try {
            String csData = eHandler.decode(data);
            String parts[] = csData.split(DATA_CS_SEPERATOR);
            if (parts.length != 2)
                throw new Exception("Malformed data");

            if (!parts[1].equals(csData(parts[0])))
                throw new Exception();
            return parts[0];
        } catch (Exception e) {
            EHException.ehthrow();
        }

        return null;
    }
}
