package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final int FIELD_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String COOKIE_DELIMITER = ";";
    private static final String FIELD_VALUE_DELIMITER = "=";

    private final Map<String, String> parametersMap;

    public HttpCookie(Map<String, String> parametersMap) {
        this.parametersMap = parametersMap;
    }

    public static HttpCookie from(String cookieContent) {
        Map<String, String> parametersMap = Arrays.stream(cookieContent.split(COOKIE_DELIMITER))
                .map(parameter -> parameter.split(FIELD_VALUE_DELIMITER))
                .collect(Collectors.toMap(
                        fieldAndValue -> fieldAndValue[FIELD_INDEX].trim(),
                        fieldAndValue -> fieldAndValue[VALUE_INDEX].trim()
                ));
        return new HttpCookie(parametersMap);
    }

    public boolean containsCookie(String field) {
        return parametersMap.containsKey(field);
    }

    public String get(String key) {
        return parametersMap.get(key);
    }

    public Map<String, String> parametersMap() {
        return new HashMap<>(parametersMap);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpCookie that = (HttpCookie) o;
        return Objects.equals(parametersMap, that.parametersMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parametersMap);
    }

    @Override
    public String toString() {
        return "HttpCookie{" +
                "parametersMap=" + parametersMap +
                '}';
    }
}
