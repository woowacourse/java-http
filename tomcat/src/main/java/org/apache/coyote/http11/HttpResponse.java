package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

    private final String version;
    private final String httpStatusCode;
    private final String contentType;
    private final String resource;
    private final Cookie cookie;

    public HttpResponse(final String version,
                        final String httpStatusCode,
                        final String contentType,
                        final String resourceUri) throws IOException {
        this.version = version;
        this.httpStatusCode = httpStatusCode;
        this.contentType = contentType;
        this.resource = makeResource(resourceUri);
        this.cookie = null;
    }

    public HttpResponse(final String version,
                        final String httpStatusCode,
                        final String contentType,
                        final String resourceUri,
                        final Cookie cookie) throws IOException {
        this.version = version;
        this.httpStatusCode = httpStatusCode;
        this.contentType = contentType;
        this.resource = makeResource(resourceUri);
        this.cookie = cookie;
    }

    private String makeResource(final String resourceUri) throws IOException {
        final InputStream staticResource = ResponseGenerator.class.getClassLoader()
                .getResourceAsStream("static" + resourceUri);
        return new String(staticResource.readAllBytes());
    }

    public String makeResponse() {
        List<String> responseHeaders = new ArrayList<>();
        responseHeaders.add(version + " " + httpStatusCode + " ");

        if (cookie != null) {
            responseHeaders.add("Set-Cookie: " + cookie.getValue() + " ");
        }

        responseHeaders.add("Content-Type: " + contentType + "; charset=utf-8 ");
        responseHeaders.add("Content-Length: " + resource.getBytes().length + " ");
        responseHeaders.add("");

        return String.join("\r\n", responseHeaders) + "\r\n" + resource;
    }
}
