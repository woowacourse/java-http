package org.apache.coyote.http11.utils;

import static org.apache.coyote.http11.response.ContentType.TEXT_HTML;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlParser {
    private static final Logger log = LoggerFactory.getLogger(UrlParser.class);
    private static final String REQUEST_STANDARD = "&";
    private static final String DATA_STANDARD = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    public static HttpRequest extractRequest(String query) {
        String[] dataMap = query.split(REQUEST_STANDARD);
        Map<String, String> mp = new HashMap<>();
        for (String data : dataMap) {
            mp.put(getKey(data), getValue(data));
        }
        return new HttpRequest(mp);
    }

    private static String getKey(String dataMap) {
        return dataMap.split(DATA_STANDARD)[KEY_INDEX];
    }

    private static String getValue(String dataMap) {
        return dataMap.split(DATA_STANDARD)[VALUE_INDEX];
    }

    public static HttpMethod extractMethod(final String httpRequest) {
        String method = httpRequest.split(" ")[0];
        return HttpMethod.valueOf(method);
    }

    public static String extractUri(final String httpRequest) {
        return httpRequest.split(" ")[1];
    }

    public static String convertEmptyToHtml(String url) {
        String resource = url;
        int index = url.indexOf(".");
        if (index == -1) {
            resource = url + "." + TEXT_HTML.getExtension();
        }
        return resource;
    }
}
