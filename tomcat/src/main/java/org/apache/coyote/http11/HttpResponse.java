package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponse {
    private final Map<String, String> header;
    private final String body;

    private HttpResponse(final Map<String, String> header, final String body) {
        this.header = header;
        this.body = body;
    }

    public static HttpResponse from(final HttpRequest request) throws IOException {
        final Map<String, String> header = new HashMap<>();
        final String requestUri = request.getUri();
        header.put("Content-Type", getContentType(requestUri));

        final String responseBody = getResponseBody(requestUri);

        header.put("Content-Length", String.valueOf(responseBody.getBytes().length));
        return new HttpResponse(header, responseBody);
    }

    private static String getContentType(final String uri) {
        if (uri.contains(".css")) {
            return "text/css";
        }

        if (uri.contains(".js")) {
            return "text/javascript";
        }

        return "text/html";
    }

    private static String getResponseBody(String requestUri) throws IOException {
        if (requestUri.equals("/")) {
            return "Hello world!";
        }

        if (!requestUri.contains(".")) {
            requestUri += ".html";
        }

        final URL resourceUrl = HttpResponse.class.getClassLoader().getResource("static" + requestUri);
        return readContext(resourceUrl);
    }

    private static String readContext(final URL resourceUrl) throws IOException {
        final File resourceFile = new File(resourceUrl.getFile());
        final Path resourcePath = resourceFile.toPath();
        final byte[] resourceContents = Files.readAllBytes(resourcePath);
        return new String(resourceContents);
    }

    public String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + header.get("Content-Type") + ";charset=utf-8 ",
                "Content-Length: " + header.get("Content-Length") + " ",
                "",
                body);
    }
}
