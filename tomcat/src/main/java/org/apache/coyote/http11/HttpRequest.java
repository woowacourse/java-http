package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.cookie.Cookies;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

//TODO: getAccept에서 text/html 등 리턴하도록 만들
public class HttpRequest {
    private HttpMethod method;
    private HttpProtocol protocol;
    private String httpVersion;
    private String path;
    private Map<String, String> queries;
    private Map<String, String> headers;
    private Cookies cookies;
    private Map<String, String> body;

    public static HttpRequest from(BufferedReader request) {
        try {
            String[] requestLine = request.readLine().split(" ");
            HttpMethod method = HttpMethod.findByValue(requestLine[0]);
            String httpVersion = requestLine[2];
            Map<String, String> queries = getQueries(requestLine);
            String path = requestLine[1];
            String protocol = requestLine[2].split("/")[0];
            String version = requestLine[2].split("/")[1];
            HttpProtocol httpProtocol = new HttpProtocol(protocol, version);
            Map<String, String> headers = getHttpHeaders(request);
            Map<String, String> body = getHttpBody(request, headers);
            Cookies cookies = getHttpCookies(headers);

            return new HttpRequest(method, httpProtocol, httpVersion, path, queries, headers, body, cookies);

        } catch (IOException e) {
            throw new IllegalArgumentException("HTTP 요청 정보를 파싱하는 중에 에러가 발생했습니다.", e);
        }
    }

    private static Cookies getHttpCookies(Map<String, String> headers) {
        if (headers.containsKey("Cookie")) {
            String[] cookieLine = headers.get("Cookie").split("; ");
            return new Cookies(cookieLine);
        }
        return new Cookies();
    }

    private static Map<String, String> getQueries(String[] requestFirstLine) {
        Map<String, String> requestQueries = new HashMap<>();
        if (requestFirstLine[1].contains("?")) {
            int queryStartIndex = requestFirstLine[1].indexOf("?") + 1;
            String requestQueryString = requestFirstLine[1].substring(queryStartIndex);
            String[] queries = requestQueryString.split("&");

            for (String query : queries) {
                String[] q = query.split("=");
                String key = q[0];
                String value = q[1];
                requestQueries.put(key, value);
            }
            deleteQueryInPath(requestFirstLine);
        }
        return requestQueries;
    }

    private static void deleteQueryInPath(String[] requestFirstLine) {
        int queryIndex = requestFirstLine[1].indexOf("?");
        requestFirstLine[1] = requestFirstLine[1].substring(0, queryIndex);
    }

    private static Map<String, String> getHttpHeaders(BufferedReader input) throws IOException {
        Map<String, String> httpHeader = new HashMap<>();

        String line = input.readLine();
        while (!line.isEmpty()) {
            String[] header = line.split(": ");
            httpHeader.put(header[0], header[1]);

            line = input.readLine();
        }

        return httpHeader;
    }

    private static Map<String, String> getHttpBody(BufferedReader request, Map<String, String> httpHeaders)
            throws IOException {
        Map<String, String> httpBody = new HashMap<>();

        if (!httpHeaders.containsKey("Content-Length")) {
            return httpBody;
        }

        int contentLength = Integer.parseInt(httpHeaders.get("Content-Length"));

        char[] buffer = new char[1024];
        int bytesRead;
        int totalBytesRead = 0;

        StringBuilder sb = new StringBuilder();
        while (totalBytesRead < contentLength
                && (bytesRead = request.read(buffer, 0, Math.min(buffer.length, contentLength - totalBytesRead)))
                != -1) {
            sb.append(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;
        }
        for (String body : sb.toString().split("&")) {
            String[] keyValue = body.split("=");
            httpBody.put(keyValue[0], keyValue[1]);
        }
        return httpBody;
    }

    public HttpRequest(HttpMethod method, HttpProtocol protocol, String httpVersion, String path,
                       Map<String, String> queries,
                       Map<String, String> headers, Map<String, String> body, Cookies cookies) {
        this.method = method;
        this.protocol = protocol;
        this.httpVersion = httpVersion;
        this.path = path;
        this.queries = queries;
        this.headers = headers;
        this.body = body;
        this.cookies = cookies;
    }

    public boolean hasCookie(String name) {
        return cookies.hasName(name);
    }

    public boolean hasBody(String key) {
        return body.entrySet().stream()
                .anyMatch(body -> body.getKey().equals(key));
    }

    public String getQueryValue(String key) {
        return queries.entrySet().stream()
                .filter(queryKey -> queryKey.getKey().equals(key))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Key(%s) 값이 존재하지 않습니다.".formatted(key)))
                .getValue();
    }

    public String getBodyValue(String key) {
        return body.entrySet().stream()
                .filter(queryKey -> queryKey.getKey().equals(key))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Key(%s) 값이 존재하지 않습니다.".formatted(key)))
                .getValue();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpProtocol getProtocol() {
        return protocol;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public String getHeader(String header) {
        try {
            return headers.get(header);
        } catch (Exception e) {
            throw new IllegalArgumentException("'%s' Header가 존재하지 않습니다.".formatted(header));
        }
    }

    public Session getSession(boolean create) {
        boolean hasCookie = hasCookie("JSESSIONID");
        if (hasCookie) {
            Cookie sessionCookie = cookies.getCookie("JSESSIONID");
            String jSessionId = sessionCookie.getValue();
            Session session = SessionManager.getInstance().findSession(jSessionId);

            if (session != null) {
                return session;
            } else {
                if(!create) {
                    return null;
                }
                Session newSession = new Session();
                SessionManager.getInstance().add(newSession);
                return newSession;
            }
        } else {
            if(!create) {
                return null;
            }
            Session newSession = new Session();
            SessionManager.getInstance().add(newSession);
            return newSession;
        }
    }


    public Cookie getCookie(String name) {
        return cookies.getCookie(name);
    }
}
