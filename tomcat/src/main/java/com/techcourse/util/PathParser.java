package com.techcourse.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PathParser {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    public static Map<String, String> parsingQueryString(final String path) {
        Map<String, String> parsedInfo = new HashMap<>();
        String[] splitPath = path.split(Pattern.quote("?"));
        String[] parts = splitPath[1].split("&");
        String account = parts[0].split("=")[1];
        String password = parts[1].split("=")[1];
        parsedInfo.put(ACCOUNT,account);
        parsedInfo.put(PASSWORD,password);
        return parsedInfo;
    }
}
