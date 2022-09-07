package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String TEXT_HTML = "text/html";
    private static final String NEW_LINE = "\r\n";

    private final ResponseHeader header;
    private final Cookie cookie;
    private HttpStatusCode statusCode;
    private String responseBody;

    private HttpResponse(final HttpStatusCode statusCode, final ResponseHeader header, final Cookie cookie) {
        this.statusCode = statusCode;
        this.header = header;
        this.cookie = cookie;
        this.responseBody = "";
    }

    public static HttpResponse init(final HttpStatusCode statusCode) {
        return new HttpResponse(statusCode, new ResponseHeader(), Cookie.init());
    }

    public HttpResponse setBody(final String responseBody) {
        this.responseBody = responseBody;
        addContentTypeAndLength(TEXT_HTML);
        return this;
    }

    private void addContentTypeAndLength(final String mimeType) {
        header.addContentType(mimeType);
        header.addContentLength(responseBody.getBytes().length);
    }

    public HttpResponse setBodyByPath(final String path) {
        try (final BufferedReader reader = toReaderByPath(path)) {
            final String body = reader.lines()
                    .collect(Collectors.joining("\n"));

            this.responseBody = body + "\n";
            addContentTypeAndLength(toMimeType(path));
            return this;
        } catch (final Exception e) {
            e.printStackTrace();
            changeStatusCode(HttpStatusCode.NOT_FOUND);
            return setBodyByPath("/404.html");
        }
    }

    private BufferedReader toReaderByPath(final String path) throws FileNotFoundException {
        final String resourcePath = toResourcePath(path);
        final File file = new File(resourcePath);

        return new BufferedReader(new FileReader(file));
    }

    private String toResourcePath(final String path) {
        final String resourcePath = "static/" + path;

        return HttpResponse.class.getClassLoader()
                .getResource(resourcePath)
                .getPath();
    }

    private String toMimeType(final String path) throws IOException {
        final Path resourcePath = Path.of(toResourcePath(path));

        return Files.probeContentType(resourcePath);
    }

    private void changeStatusCode(final HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public HttpResponse setLocationAsHome() {
        header.addLocation("/index.html");
        return this;
    }

    public HttpResponse addCookie(final String key, final String value) {
        cookie.add(key, value);
        return this;
    }

    public byte[] toResponseBytes() {
        return new StringBuilder()
                .append(toStartLineString())
                .append(toHeaderString())
                .append(NEW_LINE)
                .append(responseBody)
                .toString()
                .getBytes();
    }

    private String toStartLineString() {
        return statusCode.getResponseStartLine() + " " + NEW_LINE;
    }

    private String toHeaderString() {
        if (cookie.hasValue()) {
            header.addCookie(cookie);
        }

        return header.toHeaderString();
    }
}
