package com.techcourse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormBodyParser {

    public static Map<String, List<String>> parse(byte[] requestBody) throws UnsupportedEncodingException {
        Map<String, List<String>> partsMap = new HashMap<>();

        if (requestBody == null) {
            return partsMap;
        }

        String body = new String(requestBody, StandardCharsets.UTF_8);

        String[] parts = body.split("&");
        for (String part : parts) {
            String[] keyValue = part.split("=", 2);
            if (keyValue.length != 2) {
                continue;
            }

            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            String decodedKey = URLDecoder.decode(key, "UTF-8");
            String decodedValue = URLDecoder.decode(value, "UTF-8");

            partsMap.computeIfAbsent(decodedKey, ignored -> new ArrayList<>()).add(decodedValue);
        }
        return partsMap;
    }
}
