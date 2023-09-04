package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class HttpResponse {

    private static final ClassLoader classLoader = HttpResponse.class.getClassLoader();
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String CONTENT_TYPE = "Content-Type: ";
    private static final String CONTENT_LENGTH = "Content-Length: ";
    private static final String BLANK = "";
    private static final String SPACE = " ";
    private static final String CRLF = "\r\n";

    private final String responseStatus;
    private final String contentType;
    private final String responseBody;

    public HttpResponse(
            final String responseStatus,
            final String contentType,
            final String responseBody
    ) {
        this.responseStatus = responseStatus;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(final String status, final String filePath) {
        return new HttpResponse(status, getContentType(filePath), readStaticFile(filePath));
    }

    private static String getContentType(final String filePath) {
        final String[] fileNameSplit = filePath.split("\\.");
        final String fileType = fileNameSplit[fileNameSplit.length - 1];

        if (fileType.equals("html")) {
            return "text/html;charset=utf-8";
        }
        if (fileType.equals("css")) {
            return "text/css;charset=utf-8";
        }
        if (fileType.equals("js")) {
            return "application/javascript";
        }

        return null;
    }

    private static String readStaticFile(final String fileName) {
        final String filePath = "static/" + fileName;
        final URL res = classLoader.getResource(filePath);

        try {
            return new String(Files.readAllBytes(new File(res.getFile()).toPath()));
        } catch (IOException e) {
            return "Hello world!";
        }
    }

    @Override
    public String toString() {
        return String.join(CRLF,
                HTTP_VERSION + responseStatus + SPACE,
                CONTENT_TYPE + contentType + SPACE,
                CONTENT_LENGTH + responseBody.getBytes().length + SPACE,
                BLANK,
                responseBody
                );
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }
}
