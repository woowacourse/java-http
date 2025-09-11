package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public class HttpRequest {

    private final HttpRequestStartLine startLine;
    private final HttpHeaders headers;
    private final String body;

    private Map<String, List<String>> queryParameters;
    private Map<String, List<String>> formParameters;
    private Map<String, List<String>> allParameters;
    private HttpCookie cookies;

    public HttpRequest(
            final HttpRequestStartLine startLine,
            final HttpHeaders headers,
            final String body,
            final Map<String, List<String>> queryParameters,
            final Map<String, List<String>> formParameters,
            final Map<String, List<String>> allParameters,
            final HttpCookie cookies
    ) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
        this.queryParameters = queryParameters;
        this.formParameters = formParameters;
        this.allParameters = allParameters;
        this.cookies = cookies;
    }

    public static HttpRequest from(final BufferedReader br) throws IOException {
        String line = br.readLine();
        if (line == null) {
            return null;
        }
        HttpRequestStartLine startLine = HttpRequestStartLine.from(line);
        HttpHeaders headers = HttpHeaders.from(br);
        String body = readBody(br, headers);
        Map<String, List<String>> queryParams = parseQueryString(startLine.getQueryString());
        Map<String, List<String>> formParams = parseFormParameters(headers, body, startLine.getHttpMethod());
        Map<String, List<String>> allParams = mergeAllParams(queryParams, formParams);
        HttpCookie cookies = HttpCookie.parse(headers.getCookieHeader());

        return new HttpRequest(
                startLine,
                headers,
                body,
                queryParams,
                formParams,
                allParams,
                cookies
        );
    }

    private static String readBody(final BufferedReader br, final HttpHeaders headers) throws IOException {
        String contentLengthHeader = headers.get("Content-Length");
        if (contentLengthHeader == null) {
            return "";
        }

        final int contentLength = Integer.parseInt(contentLengthHeader);
        final char[] body = new char[contentLength];
        br.read(body, 0, contentLength);

        return new String(body);
    }

    private static Map<String, List<String>> parseFormParameters(
            final HttpHeaders headers,
            final String body,
            final String method
    ) {
        Map<String, List<String>> out = new LinkedHashMap<>();
        if (!"POST".equalsIgnoreCase(method)) {
            return out;
        }

        String contentType = headers.get("Content-Type");
        if (contentType == null) {
            return out;
        }
        if (!contentType.startsWith("application/x-www-form-urlencoded")) {
            return out;
        }

        if (body == null || body.isEmpty()) {
            return out;
        }

        out.putAll(parseQueryString(body));
        return out;
    }

    private static Map<String, List<String>> mergeAllParams(
            final Map<String, List<String>> queryParams,
            final Map<String, List<String>> formParams
    ) {
        final Map<String, List<String>> merged = new LinkedHashMap<>();
        addInto(merged, queryParams);
        addInto(merged, formParams);
        return merged;
    }

    public Map<String, List<String>> getParameters() {
        return allParameters;
    }

    private static void addInto(
            final Map<String, List<String>> base,
            final Map<String, List<String>> add
    ) {
        if (add != null) {
            add.forEach((k, vs) -> base.computeIfAbsent(k, v -> new ArrayList<>()).addAll(vs));
        }
    }

    private static Map<String, List<String>> parseQueryString(final String qs) {
        Map<String, List<String>> out = new LinkedHashMap<>();
        if (qs == null || qs.isEmpty()) {
            return out;
        }

        String[] pairs = qs.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) {
                continue;
            }
            int eq = pair.indexOf('=');
            String key = (eq >= 0) ? pair.substring(0, eq) : pair;
            String val = (eq >= 0) ? pair.substring(eq + 1) : "";
            out.computeIfAbsent(key, k -> new ArrayList<>()).add(val);
        }
        return out;
    }

    public HttpRequestStartLine getStartLine() {
        return startLine;
    }

    public String getMethod() {
        return startLine.getHttpMethod();
    }

    public String getPath() {
        return startLine.getPath();
    }

    public String getCookie(final String name) {
        return cookies.get(name);
    }

    public Session getSession(boolean create) {
        if (create) {
            return SessionManager.getInstance().createSession();
        }
        String jsessionId = getCookie("JSESSIONID");
        return SessionManager.getInstance().findSession(jsessionId);
    }
}
