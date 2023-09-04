package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.catalina.manager.Session;
import org.apache.catalina.manager.SessionManager;

public class HttpRequest {

    private static final String HEADER_BODY_DELIMITER = "";
    private static final String PATH_QUERY_STRING_DELIMITER = "\\?";
    private static final String INDEX_HTML = "/index.html";

    private final HttpMethod method;
    private final String path;
    private final HttpHeaders headers;
    private final QueryStrings queryStrings;
    private final String body;
    private final JsonProperties jsonProperties;

    private HttpRequest(HttpMethod method, String path, HttpHeaders headers, QueryStrings queryStrings, String body, JsonProperties jsonProperties) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.queryStrings = queryStrings;
        this.body = body;
        this.jsonProperties = jsonProperties;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final var request = new ArrayList<String>();
        String line;
        var body = "";
        JsonProperties properties = null;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            request.add(line);
        }
        request.add(HEADER_BODY_DELIMITER);
        final HttpHeaders header = HttpHeaders.createBasicRequestHeadersFrom(request);

        if (header.getContentLength() > 0) {
            var bodyChars = new char[header.getContentLength()];
            reader.read(bodyChars, 0, bodyChars.length);
            body = new String(bodyChars);
            properties = JsonProperties.from(body.trim(), header);
        }

        final String[] uri = request.get(0).split(" ");
        final var method = HttpMethod.of(uri[0]);
        final var fullPath = uri[1];

        if (fullPath.contains("?")) {
            final String[] pathAndQueryParams = fullPath.split(PATH_QUERY_STRING_DELIMITER);
            final var path = pathAndQueryParams[0].trim();
            final var queryStrings = new QueryStrings(pathAndQueryParams[1].trim());
            return new HttpRequest(method, path, header, queryStrings, body, properties);
        }

        return new HttpRequest(method, fullPath, header, null, body, properties);
    }

    public boolean hasQueryStrings() {
        return queryStrings != null;
    }

    public Session getSession() {
        return SessionManager.getInstance().findSession(headers.getCookie("JSESSIONID"));
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getQueryString(String key) {
        return queryStrings.getValue(key);
    }

    public String getJsonProperty(String key) {
        return jsonProperties.getValue(key);
    }

    public String getPath() {
        if (path.equals("/")) {
            return INDEX_HTML;
        }
        return path;
    }

    public boolean hasCookie(String key) {
        return headers.hasCookie(key);
    }

}
