package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class HttpRequest {

    private static final String HEADER_BODY_DELIMITER = "";
    private static final String PATH_QUERY_STRING_DELIMITER = "\\?";

    private final HttpMethod method;
    private final String path;
    private final HttpHeaders headers;
    private final QueryStrings queryStrings;
    private final String body;

    private HttpRequest(HttpMethod method, String path, HttpHeaders headers, QueryStrings queryStrings, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.queryStrings = queryStrings;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final var request = new ArrayList<String>();
        String line;
        var body = "";
        var contentLength = 0;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith(HttpHeaders.CONTENT_LENGTH)) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
            request.add(line);
        }
        request.add(HEADER_BODY_DELIMITER);

        if (contentLength > 0) {
            var bodyChars = new char[contentLength];
            reader.read(bodyChars, 0, contentLength);
            body = new String(bodyChars);
        }

        final String[] uri = request.get(0).split(" ");
        final var method = HttpMethod.of(uri[0]);
        final var fullPath = uri[1];

        if (fullPath.contains("?")) {
            final String[] pathAndQueryParams = fullPath.split(PATH_QUERY_STRING_DELIMITER);
            final var path = pathAndQueryParams[0].trim();
            final var queryStrings = new QueryStrings(pathAndQueryParams[1]);
            return new HttpRequest(method, path, HttpHeaders.from(request), queryStrings, body);
        }

        return new HttpRequest(method, fullPath, HttpHeaders.from(request), null, body);
    }

    public boolean hasQueryStrings() {
        return queryStrings != null;
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

    public String getPath() {
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }
}
