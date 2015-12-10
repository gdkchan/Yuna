package com.gdkchan.gabriel.yuna.YunaCore;

import java.io.*;
import java.net.*;

/**
 * Created by gabriel on 06/10/2015.
 */
public class HttpUtils {
    static final String UserAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1";

    public static String Get(String URL) throws Exception {
        URLConnection Connection = new URL(URL).openConnection();
        Connection.setRequestProperty("User-Agent", UserAgent);
        Connection.setRequestProperty("Accept-Charset", "UTF-8");
        BufferedReader Reader = new BufferedReader(new InputStreamReader(Connection.getInputStream()));
        StringBuilder OutStr = new StringBuilder();
        String Line = null;
        while ((Line = Reader.readLine()) != null) OutStr.append(Line);
        Reader.close();
        return OutStr.toString();
    }

    public static String Post(String URL, String PostData) throws Exception {
        URLConnection Connection = new URL(URL).openConnection();
        Connection.setDoOutput(true);
        Connection.setRequestProperty("User-Agent", UserAgent);
        Connection.setRequestProperty("Accept-Charset", "UTF-8");
        Connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        OutputStream Output = Connection.getOutputStream();
        Output.write(PostData.getBytes("UTF-8"));
        Output.close();
        BufferedReader Reader = new BufferedReader(new InputStreamReader(Connection.getInputStream()));
        StringBuilder OutStr = new StringBuilder();
        String Line = null;
        while ((Line = Reader.readLine()) != null) OutStr.append(Line);
        Reader.close();
        return OutStr.toString();
    }
}
