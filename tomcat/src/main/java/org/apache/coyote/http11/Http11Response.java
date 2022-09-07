package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.exception.ResourcePathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Response {

    private static final Logger log = LoggerFactory.getLogger(Http11Response.class);

    private final StatusCode statusCode;
    private final String contentType;
    private final String responseBody;
    private final String resourcePath;

    private Http11Response(StatusCode statusCode, String contentType, String responseBody, String resourcePath) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
        this.resourcePath = resourcePath;
    }

    public static Http11Response of(StatusCode statusCode, String resourcePath) {
        final URL resource = Thread.currentThread()
                .getContextClassLoader()
                .getResource("static" + resourcePath);
        validateResourcePath(resource);

        final String responseBody = getResponseBody(resource);
        final String contentType = resourcePath.split("\\.")[1];
        return new Http11Response(statusCode, contentType, responseBody, resourcePath);
    }

    public static Http11Response withResponseBody(StatusCode statusCode, String contentType, String responseBody) {
        return new Http11Response(statusCode, contentType, responseBody, null);
    }

    private static void validateResourcePath(URL resource) {
        if (resource == null) {
            throw new ResourcePathNotFoundException();
        }
    }

    private static String getResponseBody(URL resource) {
        try {
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException("파일을 여는 동안 I/O 오류가 발생했습니다.");
        }
    }

    public void write(OutputStream outputStream) throws IOException {
        outputStream.write(statusCode.responseToString(this).getBytes());
        outputStream.flush();
    }

    public String getOkResponse() {
        return String.join("\r\n",
                startLineToString(),
                contentTypeToString(),
                contentLengthToString(),
                "",
                responseBody);
    }

    public String getFoundResponse() {
        return String.join("\r\n",
                startLineToString(),
                contentTypeToString(),
                contentLengthToString(),
                locationToString(resourcePath),
                "",
                responseBody);
    }

    private String startLineToString() {
        return String.format("HTTP/1.1 %s ", statusCode.statusCodeToString());
    }

    private String contentTypeToString() {
        return String.format("Content-Type: text/%s;charset=utf-8 ", contentType);
    }

    private String contentLengthToString() {
        return String.format("Content-Length: %s ", responseBody.getBytes().length);
    }

    private CharSequence locationToString(String locationUrl) {
        return String.format("Location: %s ", locationUrl);
    }
}
