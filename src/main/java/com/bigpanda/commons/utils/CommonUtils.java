package com.bigpanda.commons.utils;

import java.util.List;

public class CommonUtils {
    public static boolean containsCaseInsensitive(String s, List<String> l){
        return l.stream().anyMatch(x -> x.equalsIgnoreCase(s));
    }
}
