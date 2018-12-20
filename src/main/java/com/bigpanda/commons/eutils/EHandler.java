package com.bigpanda.commons.eutils;

public interface EHandler {
    /* (non-Javadoc)
     * @see com.optionfair.server.util.CryptUtil#encode(java.lang.String)
     */
    String encode(String data) throws Exception;

    /* (non-Javadoc)
     * @see com.optionfair.server.util.CryptUtil#decode(java.lang.String)
     */
    String decode(String data) throws Exception;
}
