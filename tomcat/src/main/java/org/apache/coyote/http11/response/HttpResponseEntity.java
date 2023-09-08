package org.apache.coyote.http11.response;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class HttpResponseEntity {
    private final Map<String, String> header;
    private final String body;

    private HttpResponseEntity(final Map<String, String> header, final String body) {
        this.header = header;
        this.body = body;
    }

    public static HttpResponseEntity ok(final String path, final String body) {
        final Map<String, String> requestHeader = new LinkedHashMap<>();
        requestHeader.put("HTTP/1.1 ", HttpStatusCode.OK.message());
        requestHeader.put("Content-Type: ", makeContentType(path) + ";charset=utf-8 ");
        requestHeader.put("Content-Length: ", body.getBytes().length + " ");
        return new HttpResponseEntity(requestHeader, body);
    }

    private static String makeContentType(final String path) {
        if (path.endsWith("css")) {
            return "text/css";
        }
        return "text/html";
    }

    public static HttpResponseEntity found(final String path) {
        final Map<String, String> requestHeader = new LinkedHashMap<>();
        requestHeader.put("HTTP/1.1 ", HttpStatusCode.FOUND.message());
        requestHeader.put("Location: ", path);
        return new HttpResponseEntity(requestHeader, StringUtils.EMPTY);
    }

    public HttpResponseEntity setCookie(final String jsessionid) {
        header.put("Set-Cookie: JSESSIONID=", jsessionid);
        return this;
    }

    public String makeResponse() {
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        for (final Map.Entry<String, String> entry : header.entrySet()) {
            stringJoiner.add(entry.getKey() + entry.getValue());
        }
        stringJoiner.add("");
        stringJoiner.add(body);
        return stringJoiner.toString();
    }
}
