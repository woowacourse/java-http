package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestLine {

    private final HttpMethod httpMethod;
    private final String url;
    private final Map<String, String> queryString;
    private final HttpVersion httpVersion;

    public HttpRequestLine(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        String[] elements = requestLine.split(" ");
        validateRequestLineElementsCount(elements);
        this.httpMethod = HttpMethod.convertFrom(elements[0].trim());

        String[] urlAndQueryString = elements[1].trim().split("\\?");
        validateUrlAndQueryStringLength(urlAndQueryString);
        this.url = urlAndQueryString[0];

        this.queryString = parseQueryString(urlAndQueryString);

        String[] httpVersion = elements[2].split("/");
        validateHttpVersionFormat(httpVersion);
        this.httpVersion = HttpVersion.convertFrom(httpVersion[1]);
    }

    private void validateRequestLineElementsCount(String[] elements) {
        if (elements.length != 3) {
            throw new HttpFormatException();
        }
    }

    private void validateUrlAndQueryStringLength(String[] urlAndQueryString) {
        if (urlAndQueryString.length > 2) {
            throw new HttpFormatException();
        }
    }

    private Map<String, String> parseQueryString(String[] urlAndQueryString) {
        Map<String, String> queryString = new HashMap<>();
        if (urlAndQueryString.length == 2) {
            String[] queryStrings = urlAndQueryString[1].split("&");
            for (String singleQueryString : queryStrings) {
                String[] keyValue = singleQueryString.split("=");
                if (keyValue.length != 2) {
                    throw new HttpFormatException();
                }
                queryString.put(keyValue[0], keyValue[1]);
            }
        }
        return queryString;
    }

    private void validateHttpVersionFormat(String[] httpVersion) {
        if (httpVersion.length != 2 && !httpVersion[0].equals("HTTP")) {
            throw new HttpFormatException();
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
