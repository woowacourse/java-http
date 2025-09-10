package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeader {

    private final Map<String, String> headerMap = new HashMap<>();

    public HttpHeader(List<String> headerMap) {
        for (String header : headerMap) {
            String[] headerArr = header.split(":");
            this.headerMap.put(headerArr[0].trim(), headerArr[1].trim());
        }
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public Map<String, String> getCookie() {
        Map<String, String> cookieKeyValueMap = new HashMap<>();
        String cookieString = headerMap.getOrDefault("Cookie", "");
        String[] cookieKeyValues = cookieString.split(";");

        if (cookieKeyValues.length != 2) {
            return cookieKeyValueMap;
        }

        for (String cookieKeyValue : cookieKeyValues) {
            String[] splitedKeyValue = cookieKeyValue.split("=");
            if (splitedKeyValue.length == 2) {
                cookieKeyValueMap.put(splitedKeyValue[0], splitedKeyValue[1]);
            }
        }

        return cookieKeyValueMap;
    }
}
