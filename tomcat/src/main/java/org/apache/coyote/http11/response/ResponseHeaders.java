package org.apache.coyote.http11.response;

import org.apache.coyote.http11.cookie.HttpCookie;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeaders {

    private static final String CRLF = " \r\n";
    private static final String DELIMITER = ": ";
    private final Map<String, String> headers;

    private ResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders of(ResponseBody responseBody) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(Constant.CONTENT_TYPE, responseBody.getContentTypeName());
        headers.put(Constant.CONTENT_LENGTH, String.valueOf(responseBody.getLength()));
        return new ResponseHeaders(headers);
    }

    public void addCookie(HttpCookie cookie) {
        headers.put(Constant.SET_COOKIE, cookie.toString());
    }

    public void setLocation(String location) {
        headers.put(Constant.LOCATION, location);
    }

    public String parse() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> headersEntry : headers.entrySet()) {
            stringBuilder.append(headersEntry.getKey())
                    .append(DELIMITER)
                    .append(headersEntry.getValue())
                    .append(CRLF);
        }
        return stringBuilder.toString();
    }

    public void addContent(ResponseBody responseBody) {
        headers.put(Constant.CONTENT_TYPE, responseBody.getContentTypeName());
        headers.put(Constant.CONTENT_LENGTH, String.valueOf(responseBody.getLength()));
    }
}
