package com.bigpanda.commons.eutils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class EHandlerImp implements EHandler {
    private byte[] key;

    public EHandlerImp() {

    }

    public EHandlerImp(String init) throws IOException {
        setKey(init);
    }

    public void setKey(String keyString) throws IOException {
        key = Base64.getDecoder().decode(keyString);
    }

    /* (non-Javadoc)
     * @see com.optionfair.server.util.CryptUtil#encode(java.lang.String)
     */
    @Override
    public String encode(String data) throws Exception {
        byte[] iv = new byte[16];
        Random r = new Random(System.currentTimeMillis());
        for (int i = 0; i < 16; i++)
            iv[i] = (byte) r.nextInt(255);

        SecretKeySpec sks = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, sks, ivParams);

        byte[] binData = data.getBytes();
        if (binData.length % 16 != 0) {
            int paddedLength = ((binData.length / 16) +1) *16;
            binData = Arrays.copyOf(binData, paddedLength);
        }

        byte[] encrypted = cipher.doFinal(binData);
        return Base64.getEncoder().encodeToString(encrypted) + ","+Base64.getEncoder().encodeToString(iv);
    }

    /* (non-Javadoc)
     * @see com.optionfair.server.util.CryptUtil#decode(java.lang.String)
     */
    @Override
    public String decode(String data) throws Exception {
        String[] parts = data.split(",");
        if (parts.length != 2)
            throw new Exception("Bad encryption: "+data);

        SecretKeySpec sks = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        IvParameterSpec ivParams = new IvParameterSpec(Base64.getDecoder().decode(parts[1]));
        cipher.init(Cipher.DECRYPT_MODE, sks, ivParams);

        byte[] decoded = cipher.doFinal(Base64.getDecoder().decode(parts[0]));

        // Find out length to prevent AES null padding to be added to string.
        int i = decoded.length;
        while (decoded[--i] == 0);

        return new String(decoded, 0, i+1);
    }
}
