package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.exception.NotFoundResourcePathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Response {

    private static final Logger log = LoggerFactory.getLogger(Http11Response.class);

    private final StatusCode statusCode;
    private final ResponseHeaders responseHeaders;
    private final String responseBody;

    public Http11Response(StatusCode statusCode, ResponseHeaders responseHeaders, String responseBody) {
        this.statusCode = statusCode;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public static Http11Response of(final StatusCode statusCode, final String resourcePath) {
        final URL resource = getResource(resourcePath);
        validateResourcePath(resource);

        final String contentType = resourcePath.split("\\.")[1];
        final ResponseHeaders responseHeaders = ResponseHeaders.initEmpty()
                .addHeader("Content-Type", contentType);

        final String responseBody = getResponseBody(resource);
        return new Http11Response(statusCode, responseHeaders, responseBody);
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

    public Http11Response addHeader(String key, String value) {
        ResponseHeaders responseHeaders = this.responseHeaders.addHeader(key, value);
        return new Http11Response(statusCode, responseHeaders, responseBody);
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
                responseHeaders.headerToString("Content-Type"),
                contentLengthToString(),
                "",
                responseBody);
    }

    public String getFoundResponse() {
        return String.join("\r\n",
                startLineToString(),
                responseHeaders.headerToString("Content-Type"),
                contentLengthToString(),
                responseHeaders.headerToString("Location"),
                "",
                responseBody);
    }

    public String getFoundResponseWithSetCookie() {
        return String.join("\r\n",
                startLineToString(),
                responseHeaders.headerToString("Content-Type"),
                contentLengthToString(),
                responseHeaders.headerToString("Location"),
                responseHeaders.headerToString("Set-Cookie"),
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
