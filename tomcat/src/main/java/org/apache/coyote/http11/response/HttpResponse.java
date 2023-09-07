package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class HttpResponse {

    private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String BLANK = "";
    private static final String SPACE = " ";
    private static final String CRLF = "\r\n";

    private HttpStatus status;
    private final ResponseHeaders headers;
    private String responseBody;

    public HttpResponse() {
        headers = new ResponseHeaders();
    }

    public void addHeader(final String headerName, final String value) {
        headers.addHeader(headerName, value);
    }

    public HttpResponse hostingPage(final String filePath) {
        responseBody = readStaticFile(filePath);

        headers.setContentType(getContentType(filePath));
        headers.setContentLength(responseBody.getBytes().length);

        status = HttpStatus.OK;
        return this;
    }

    private String readStaticFile(final String fileName) {
        final String filePath = "static/" + fileName;
        final URL res = CLASS_LOADER.getResource(filePath);

        try {
            return new String(Files.readAllBytes(new File(res.getFile()).toPath()));
        } catch (IOException e) {
            return "Hello world!";
        }
    }

    private String getContentType(final String filePath) {
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

    public HttpResponse redirectTo(final String redirectUrl) {
        headers.setLocation(redirectUrl);
        status = HttpStatus.FOUND;
        responseBody = null;
        return this;
    }

    public HttpResponse methodNotAllowed() {
        status = HttpStatus.METHOD_NOT_ALLOWED;
        responseBody = null;
        return this;
    }

    public void setCookie(final String cookieName, final String value) {
        headers.setCookie(cookieName, value);
    }

    @Override
    public String toString() {
        return String.join(CRLF,
                HTTP_VERSION + status.getCode() + SPACE,
                headers.toString(),
                (responseBody != null) ? responseBody : BLANK
        );
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }
}
