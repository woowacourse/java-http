package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.exception.Http11ParseException;
import org.apache.coyote.http11.exception.ParseError;

public class Http11Request {

    private Http11Method method;
    private String uri;
    private String version;
    private String path;
    private Map<String, String> params;
    private Map<String, String> headers;
    private String body;
    private Http11Cookie cookie;
    private Http11Session session;

    public Http11Request(final InputStream inputStream) throws IOException, Http11ParseException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        parseRequestLine(reader);
        this.headers = parseHeaders(reader);
        this.body = parseBody(reader);
        this.params = parseParams();
        this.cookie = parseCookies();
    }

    private void parseRequestLine(final BufferedReader reader) throws IOException, Http11ParseException {
        final String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new Http11ParseException(ParseError.INVALID_REQUEST_LINE);
        }

        final String[] requestLineParts = requestLine.split(" ");
        if (requestLineParts.length != 3) {
            throw new Http11ParseException(ParseError.INVALID_REQUEST_LINE);
        }

        this.method = Http11Method.from(requestLineParts[0]);
        this.uri = requestLineParts[1];
        this.version = requestLineParts[2];
        this.path = extractPath(uri);
    }

    private String extractPath(final String uri) {
        int queryIndex = uri.indexOf("?");
        if (queryIndex != -1) {
            return uri.substring(0, queryIndex);
        }
        return uri;
    }

    private Map<String, String> parseHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new LinkedHashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            final String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                headers.put(parts[0].trim(), parts[1].trim());
            }
        }
        return headers;
    }

    private String parseBody(final BufferedReader reader) throws IOException {
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return "";
    }

    private Map<String, String> parseParams() {
        final Map<String, String> params = new HashMap<>();
        addParamsFromQueryString(params);
        addParamsFromBody(params);
        return Collections.unmodifiableMap(params);
    }

    private void addParamsFromQueryString(final Map<String, String> params) {
        int queryIndex = uri.indexOf("?");
        if (queryIndex != -1) {
            final String queryString = uri.substring(queryIndex + 1);
            parseQueryString(queryString, params);
        }
    }

    private void addParamsFromBody(final Map<String, String> params) {
        if (isPost() && body != null && !body.isEmpty()) {
            parseQueryString(body, params);
        }
    }

    private void parseQueryString(final String queryString, final Map<String, String> params) {
        final String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            final String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                final String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                final String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
    }

    private Http11Cookie parseCookies() {
        return new Http11Cookie(headers.getOrDefault("Cookie", ""));
    }

    public boolean isGet() {
        return method == Http11Method.GET;
    }

    public boolean isPost() {
        return method == Http11Method.POST;
    }

    public String getPath() {
        return path;
    }

    public String getParam(String name) {
        return params.get(name);
    }

    public String getBody() {
        return body;
    }

    public Http11Cookie getCookie() {
        return cookie;
    }
}
