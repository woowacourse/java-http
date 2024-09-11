package com.techcourse.controller;

import java.util.HashMap;
import java.util.Map;

public class BodyParser {
    public static Map<String, String> parseValues(String body) {
        String tokens[] = body.split("&");
        Map<String, String> values = new HashMap<>();
        for (String token : tokens) {
            int separatorIndex = token.indexOf('=');
            if (separatorIndex == -1) {
                throw new IllegalArgumentException("데이터 형식이 잘못되었습니다.");
            }
            values.put(token.substring(0, separatorIndex), token.substring(separatorIndex + 1));
        }
        return values;
    }
}
