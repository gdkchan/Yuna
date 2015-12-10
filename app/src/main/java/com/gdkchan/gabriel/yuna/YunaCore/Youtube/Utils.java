package com.gdkchan.gabriel.yuna.YunaCore.Youtube;

/**
 * Created by gabriel on 10/10/2015.
 */
public class Utils {
    public static String IdFromUrl(String URL) {
        URL = URL.substring(URL.indexOf("?") + 1);
        String[] Args = URL.split("&");
        for (int i = 0; i < Args.length; i++) if (Args[i].startsWith("v=")) return Args[i].substring(Args[i].indexOf("v=") + 2);
        return null;
    }
}
