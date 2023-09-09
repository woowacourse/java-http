package org.apache.coyote.http11.response;

import org.apache.coyote.http11.exception.HostingFileNotFoundException;

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

    public void hostingPage(final String filePath) throws IOException {
        responseBody = readStaticFile(filePath);

        headers.setContentType(getContentType(filePath));
        headers.setContentLength(responseBody.getBytes().length);

        status = HttpStatus.OK;
    }

    private String readStaticFile(final String fileName) throws IOException {
        final String filePath = "static/" + fileName;
        final URL res = CLASS_LOADER.getResource(filePath);

        if (fileName.equals("/")) {
            return "Hello world!";
        }
        try {
            return new String(Files.readAllBytes(new File(res.getFile()).toPath()));
        } catch (NullPointerException e) {
            throw new HostingFileNotFoundException();
        }
    }

    private String getContentType(final String filePath) {
        final HttpContentType contentType = HttpContentType.getByFilePath(filePath);
        return contentType.getHeaderString();
    }

    public void redirectTo(final String redirectUrl) {
        headers.setLocation(redirectUrl);
        status = HttpStatus.FOUND;
        responseBody = null;
    }

    public void methodNotAllowed() {
        status = HttpStatus.METHOD_NOT_ALLOWED;
        responseBody = null;
    }

    public void badRequest(final String message) {
        status = HttpStatus.BAD_REQUEST;
        headers.setContentType(HttpContentType.PLAIN_TEXT.getHeaderString());
        responseBody = message;
    }

    public void setCookie(final String cookieName, final String value) {
        headers.setCookie(cookieName, value);
    }

    public String getResponseString() {
        return String.join(CRLF,
                HTTP_VERSION + status.getCode() + SPACE,
                headers.getResponseString(),
                (responseBody != null) ? responseBody : BLANK
        );
    }

    public byte[] getBytes() {
        return getResponseString().getBytes();
    }

    public void setStatus(final HttpStatus httpStatus) {
        this.status = httpStatus;
    }
}
