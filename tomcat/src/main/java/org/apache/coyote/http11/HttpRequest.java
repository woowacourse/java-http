package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private final String requestUrl;
    private final Map<String, String> requestParams;

    private HttpRequest(final String requestUrl, final Map<String, String> requestParams) {
        this.requestUrl = requestUrl;
        this.requestParams = requestParams;
    }

    public static HttpRequest from(final String requestHeader) {
        return new HttpRequest(parseUrl(requestHeader), parseRequestParams(requestHeader));
    }

    private static String parseUrl(final String requestHeader) {
        return requestHeader.split(" ")[1]
                .split("\\?")[0];
    }

    private static Map<String, String> parseRequestParams(final String requestHeader) {
        String url = requestHeader.split(" ")[1];

        if (!url.contains("?")) {
            return Map.of();
        }

        String params = url.split("\\?")[1];

        return Arrays.stream(params.split("&"))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public Map<String, String> getRequestParams() {
        return requestParams;
    }
}
