package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class HttpResponse {

    private static final ClassLoader classLoader = HttpResponse.class.getClassLoader();
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String BLANK = "";
    private static final String SPACE = " ";
    private static final String CRLF = "\r\n";

    private final String responseStatus;
    private final ResponseHeaders headers;
    private final String responseBody;

    public HttpResponse(
            final String responseStatus,
            final ResponseHeaders headers,
            final String responseBody
    ) {
        this.responseStatus = responseStatus;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(final HttpStatus status, final String filePath) {
        final ResponseHeaders headers = new ResponseHeaders();

        if (status == HttpStatus.FOUND) {
            headers.setLocation(filePath);
            return new HttpResponse(status.getCode(), headers, null);
        }

        final String body = readStaticFile(filePath);
        headers.setContentType(getContentType(filePath));
        headers.setContentLength(body.getBytes().length);

        return new HttpResponse(status.getCode(), headers, body);
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

        if (filePath.equals("/")) {
            return "text/html;charset=utf-8";
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

    public void setCookie(final String cookieName, final String value) {
        headers.setCookie(cookieName, value);
    }

    @Override
    public String toString() {
        return String.join(CRLF,
                HTTP_VERSION + responseStatus + SPACE,
                headers.toString(),
                (responseBody != null) ? responseBody : BLANK
        );
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }
}
