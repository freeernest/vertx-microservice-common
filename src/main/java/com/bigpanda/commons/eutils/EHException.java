package com.bigpanda.commons.eutils;

public class EHException extends Exception {
    public static void ehthrow() throws Exception{
        throw new EHException();
    }

    public EHException() {
        super("Malformed address");
    }
}
