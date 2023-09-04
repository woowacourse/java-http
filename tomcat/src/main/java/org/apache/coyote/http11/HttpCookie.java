package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final int FIELD_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> parametersMap;

    public HttpCookie(Map<String, String> parametersMap) {
        this.parametersMap = parametersMap;
    }

    public static HttpCookie from(String cookieContent) {
        if (cookieContent == null) {
            return null;
        }
        Map<String, String> parametersMap = Arrays.stream(cookieContent.split("&"))
                .map(parameter -> parameter.split("="))
                .collect(Collectors.toMap(
                        fieldAndValue -> fieldAndValue[FIELD_INDEX],
                        fieldAndValue -> fieldAndValue[VALUE_INDEX]
                ));
        return new HttpCookie(parametersMap);
    }

    public String get(String key) {
        return parametersMap.get(key);
    }

    public Map<String, String> cookieMap() {
        return new HashMap<>(parametersMap);
    }
}
