package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestUri;

public class HttpResponseBody {
    private final String body;

    private HttpResponseBody(final String body) {
        this.body = body;
    }

    public static HttpResponseBody from(final String body) {
        return new HttpResponseBody(body);
    }

    public static HttpResponseBody from(final HttpRequest request) throws IOException {
        HttpRequestUri httpRequestUri = request.getStartLine().getHttpRequestUri();
        String requestUri = httpRequestUri.getUri();

        final URL resource = HttpResponseBody.class.getClassLoader().getResource("static" + requestUri);

        if (resource == null) {
            return new HttpResponseBody("");
        }

        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        return new HttpResponseBody(responseBody);
    }

    @Override
    public String toString() {
        return body;
    }
}
