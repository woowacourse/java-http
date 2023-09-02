package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class HttpRequest {

    private static final String HEADER_BODY_DELIMITER = "";

    private final HttpMethod method;
    private final String path;
    private final HttpHeaders headers;
    private final String body;

    private HttpRequest(HttpMethod method, String path, HttpHeaders headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
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

        final var method = HttpMethod.of(request.get(0).split(" ")[0]);
        final var path = request.get(0).split(" ")[1];

        return new HttpRequest(method, path, HttpHeaders.from(request), body);
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }
}
