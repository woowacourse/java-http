package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.catalina.SessionManager;

public class HttpRequestParser {

    private static HttpRequestParser requestParser;
    private final SessionManager sessionManager;

    private HttpRequestParser() {
        this.sessionManager = SessionManager.getInstance();
    }

    public static HttpRequestParser getInstance() {
        if (requestParser == null) {
            requestParser = new HttpRequestParser();
        }
        return requestParser;
    }

    public HttpRequest parseInput(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String[] tokens = parseRequestLine(bufferedReader);
        HttpRequest request = new HttpRequest();

        request.setMethod(HttpMethod.valueOf(tokens[0]));
        request.setPath(parsePath(tokens[1]));
        request.setProtocolVersion(tokens[2]);
        request.setQueryString(parseQueryString(tokens[1]));
        Map<String, String> headers = parseHeaders(bufferedReader);
        HttpCookie httpCookie = parseCookie(headers);
        request.setHeaders(headers);
        request.setHttpCookie(httpCookie);
        request.setSession(parseSession(httpCookie));
        request.setBody(parseBody(bufferedReader, headers));
        return request;
    }

    private String[] parseRequestLine(BufferedReader bufferedReader) throws IOException {
        String tokens[] = bufferedReader.readLine()
                .split(" ");
        if (tokens.length != 3) {
            throw new RuntimeException();
        }
        return tokens;
    }

    private String parsePath(String token) {
        int separatorIndex = token.indexOf('?');
        if (separatorIndex == -1) {
            return token;
        }
        return token.substring(0, separatorIndex);
    }

    private Map<String, String> parseQueryString(String token) {
        Map<String, String> queryString = new HashMap<>();
        int separatorIndex = token.indexOf('?');
        if (separatorIndex == -1) {
            return queryString;
        }
        Stream.of(token.substring(separatorIndex + 1)
                        .split("&"))
                .forEach(data -> parseData(data, queryString));
        return queryString;
    }

    private void parseData(String s, Map<String, String> queryString) {
        String data[] = s.split("=");
        if (data.length == 2) {
            queryString.put(data[0], data[1]);
        }
    }

    private Map<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        Map<String, String> headers = new LinkedHashMap<>();

        while (!"".equals(line)) {
            if (line == null) {
                return headers;
            }
            int index = line.indexOf(':');
            String key = line.substring(0, index);
            String value = line.substring(index + 2);
            headers.put(key, value);
            line = bufferedReader.readLine();
        }
        return headers;
    }

    private HttpCookie parseCookie(Map<String, String> headers) {
        Map<String, String> cookies = new HashMap<>();
        if (!headers.containsKey("Cookie")) {
            return new HttpCookie(cookies);
        }
        String data = headers.get("Cookie");
        Stream.of(data.split(";"))
                .map(String::trim)
                .forEach(s -> {
                    int separatorIndex = s.indexOf("=");
                    if (separatorIndex != -1) {
                        cookies.put(s.substring(0, separatorIndex), s.substring(separatorIndex + 1));
                    }
                });
        return new HttpCookie(cookies);
    }

    private Session parseSession(HttpCookie httpCookie) {
        String sessionId = httpCookie.getValue("JSESSIONID");
        Session found = sessionManager.findSession(sessionId);
        if (found == null && sessionId != null) {
            Session session = new Session(sessionId);
            sessionManager.add(new Session(sessionId));
            return session;
        }
        return found;
    }

    private String parseBody(BufferedReader bufferedReader, Map<String, String> headers) throws IOException {
        String length = headers.get("Content-Length");
        if (length == null) {
            return "";
        }
        int contentLength = Integer.parseInt(length);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
