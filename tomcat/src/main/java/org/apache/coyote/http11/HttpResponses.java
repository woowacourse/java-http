package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class HttpResponses {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";

    private static final String STATIC = "static";
    private static final String NOT_FOUND_PAGE = "static/404.html";
    private static final String SERVER_ERROR_PAGE = "static/500.html";

    private HttpResponses() {
    }

    public static HttpResponse render(final String path) throws IOException {
        final URL resource = HttpResponses.class.getClassLoader().getResource(STATIC + path);

        if (resource == null) {
            return notFound();
        }

        return readResource(resource, new HttpStatusLine(HTTP_VERSION, 200, "OK"));
    }

    public static HttpResponse ok(final String body, final String contentType) {
        final byte[] responseBody = body.getBytes(StandardCharsets.UTF_8);

        final Map<String, String> headers = new LinkedHashMap<>();
        headers.put(CONTENT_TYPE, contentType);
        headers.put(CONTENT_LENGTH, String.valueOf(responseBody.length));

        return new HttpResponse(new HttpStatusLine(HTTP_VERSION, 200, "OK"), headers, responseBody);
    }

    public static HttpResponse redirect(final String location) {
        return redirect(location, Map.of());
    }

    public static HttpResponse redirect(final String location, final Map<String, String> headers) {
        final Map<String, String> allHeaders = new LinkedHashMap<>(headers);
        allHeaders.put(LOCATION, location);

        return new HttpResponse(
                new HttpStatusLine(HTTP_VERSION, 302, "Found"),
                allHeaders,
                new byte[0]
        );
    }

    public static HttpResponse notFound() throws IOException {
        return readResource(requiredResource(NOT_FOUND_PAGE),
                new HttpStatusLine(HTTP_VERSION, 404, "Not Found"));
    }

    public static HttpResponse methodNotAllowed() {
        return statusOnly(new HttpStatusLine(HTTP_VERSION, 405, "Method Not Allowed"));
    }

    public static HttpResponse serverError() {
        final HttpStatusLine statusLine = new HttpStatusLine(HTTP_VERSION, 500, "Internal Server Error");

        try {
            return readResource(requiredResource(SERVER_ERROR_PAGE), statusLine);
        } catch (IOException | RuntimeException e) {
            return statusOnly(statusLine);
        }
    }

    private static HttpResponse statusOnly(final HttpStatusLine statusLine) {
        return new HttpResponse(statusLine, Map.of(CONTENT_LENGTH, "0"), new byte[0]);
    }

    private static URL requiredResource(final String path) {
        return Objects.requireNonNull(
                HttpResponses.class.getClassLoader().getResource(path),
                path + "이 존재하지 않습니다."
        );
    }

    private static HttpResponse readResource(final URL resource, final HttpStatusLine statusLine) throws IOException {
        try (InputStream inputStream = resource.openStream()) {
            final byte[] responseBody = inputStream.readAllBytes();

            final Map<String, String> headers = new LinkedHashMap<>();
            headers.put(CONTENT_TYPE, contentTypeOf(resource.getPath()));
            headers.put(CONTENT_LENGTH, String.valueOf(responseBody.length));

            return new HttpResponse(statusLine, headers, responseBody);
        }
    }

    private static String contentTypeOf(final String path) {
        final String contentType = URLConnection.guessContentTypeFromName(path);

        if (contentType == null) {
            return "application/octet-stream";
        }

        if (contentType.startsWith("text/")) {
            return contentType + ";charset=utf-8";
        }

        return contentType;
    }
}
