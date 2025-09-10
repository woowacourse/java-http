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

    public HttpRequest(final HttpRequestStartLine startLine, final HttpHeaders headers, final String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader br) throws IOException {
        String line = br.readLine();
        if (line == null) {
            return null;
        }

        HttpRequestStartLine httpRequestStartLine = HttpRequestStartLine.from(line);
        HttpHeaders httpHeaders = HttpHeaders.from(br);
        String body = readBody(br, httpHeaders);

        return new HttpRequest(httpRequestStartLine, httpHeaders, body);
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

    public HttpRequestStartLine getStartLine() {
        return startLine;
    }

    public String getMethod() {
        return startLine.getHttpMethod();
    }

    public String getUri() {
        return startLine.getUri();
    }

    public Map<String, List<String>> getQueryParameters() {
        if (this.queryParameters != null) {
            return this.queryParameters;
        }
        final String queryString = startLine.getQueryString();
        this.queryParameters = parseQueryString(queryString);
        return this.queryParameters;
    }

    public Map<String, List<String>> getFormParameters() {
        if (this.formParameters != null) {
            return this.formParameters;
        }

        this.formParameters = new LinkedHashMap<>();

        final String method = getMethod();
        final String contentType = headers.get("Content-Type");

        if (!"POST".equalsIgnoreCase(method)) {
            return this.formParameters;
        }
        if (contentType == null) {
            return this.formParameters;
        }
        if (!contentType.startsWith("application/x-www-form-urlencoded")) {
            return this.formParameters;
        }
        if (body == null || body.isEmpty()) {
            return this.formParameters;
        }

        this.formParameters.putAll(parseQueryString(body));
        return this.formParameters;
    }

    public Map<String, List<String>> getParameters() {
        if (allParameters == null) {
            allParameters = new LinkedHashMap<>();
            addInto(allParameters, getQueryParameters());
            addInto(allParameters, getFormParameters());
        }
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

    public String getCookie(final String name) {
        cookies = HttpCookie.parse(headers.getCookieHeader());
        return cookies.get(name);
    }

    public Session getSession(boolean create) {
        if (create) {
            return SessionManager.getInstance().createSession();
        }
        String jsessionId = getCookie("JSESSIONID");
        return SessionManager.getInstance().findSession(jsessionId);
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
}
