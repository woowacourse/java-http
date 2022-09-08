package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.exception.NotFoundResourcePathException;
import org.apache.coyote.http11.HttpCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Response {

    private static final Logger log = LoggerFactory.getLogger(Http11Response.class);

    private final StatusCode statusCode;
    private final ResponseHeaders responseHeaders;
    private final String responseBody;

    private Http11Response(StatusCode statusCode, ResponseHeaders responseHeaders, String responseBody) {
        this.statusCode = statusCode;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public static Http11Response withResponseBody(final StatusCode statusCode, final String contentType,
                                                  final String responseBody) {
        return new Http11Response(statusCode, ResponseHeaders.fromContentType(contentType), responseBody);
    }

    public static Http11Response of(final StatusCode statusCode, final String resourcePath) {
        final URL resource = getResource(resourcePath);
        validateResourcePath(resource);

        final String responseBody = getResponseBody(resource);
        return new Http11Response(statusCode, ResponseHeaders.fromResourcePath(resourcePath), responseBody);
    }

    public static Http11Response withLocation(final StatusCode statusCode, final String resourcePath, String location) {
        final URL resource = getResource(resourcePath);
        validateResourcePath(resource);

        final String responseBody = getResponseBody(resource);
        return new Http11Response(statusCode, ResponseHeaders.withLocation(resourcePath, location), responseBody);
    }

    public static Http11Response withLocationAndSetCookie(final StatusCode statusCode,
                                                          final String resourcePath,
                                                          final String location,
                                                          final HttpCookie cookie,
                                                          final String cookieName) {
        final URL resource = getResource(resourcePath);
        validateResourcePath(resource);

        final String responseBody = getResponseBody(resource);
        return new Http11Response(statusCode,
                ResponseHeaders.withLocationAndSetCookie(resourcePath, location, cookie, cookieName), responseBody);
    }

    private static URL getResource(String resourcePath) {
        return Thread.currentThread()
                .getContextClassLoader()
                .getResource("static" + resourcePath);
    }

    private static void validateResourcePath(final URL resource) {
        if (resource == null) {
            throw new NotFoundResourcePathException();
        }
    }

    private static String getResponseBody(final URL resource) {
        try {
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException("파일을 여는 동안 I/O 오류가 발생했습니다.");
        }
    }

    public void write(final OutputStream outputStream) throws IOException {
        outputStream.write(statusCode.responseToString(this).getBytes());
        outputStream.flush();
    }

    public boolean hasSetCookieHeader() {
        return responseHeaders.hasSetCookieHeader();
    }

    public String getOkResponse() {
        return String.join("\r\n",
                startLineToString(),
                responseHeaders.contentTypeToString(),
                contentLengthToString(),
                "",
                responseBody);
    }

    public String getFoundResponse() {
        return String.join("\r\n",
                startLineToString(),
                responseHeaders.contentTypeToString(),
                contentLengthToString(),
                responseHeaders.locationToString(),
                "",
                responseBody);
    }

    public String getFoundResponseWithSetCookie() {
        return String.join("\r\n",
                startLineToString(),
                responseHeaders.contentTypeToString(),
                contentLengthToString(),
                responseHeaders.locationToString(),
                responseHeaders.setCookieToString(),
                "",
                responseBody);
    }

    private String startLineToString() {
        return String.format("HTTP/1.1 %s ", statusCode.statusCodeToString());
    }

    private String contentLengthToString() {
        return String.format("Content-Length: %s ", responseBody.getBytes().length);
    }
}
