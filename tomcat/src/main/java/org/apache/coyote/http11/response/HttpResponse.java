package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.ContentMimeType;
import org.apache.coyote.http11.HttpHeaderKey;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.cookie.Cookie;

public class HttpResponse {

    private static final String CHARSET_UTF8 = ";charset=utf-8";
    private static final String LINE_SEPARATOR = "\r\n";
    private static final String STATIC_FILES_BASE_PATH = "static";
    private static final String FILE_EXTENSION_DELIMITER = ".";

    private final OutputStream outputStream;
    private final ResponseHeaders headers;
    private StatusLine statusLine;
    private ResponseBody body;

    private HttpResponse(
            OutputStream outputStream,
            HttpStatus status,
            String protocolVersion,
            ResponseHeaders headers,
            ResponseBody body
    ) {
        this.outputStream = outputStream;
        this.statusLine = new StatusLine(protocolVersion, status);
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse(OutputStream outputStream) {
        this(outputStream, null, null, new ResponseHeaders(), null);
    }

    public void setCookie(Cookie cookie) {
        headers.add(HttpHeaderKey.SET_COOKIE, cookie.getCookieString());
    }

    public void setBody(ResponseBody body) {
        this.body = body;
        headers.add(HttpHeaderKey.CONTENT_TYPE, body.getContentType() + CHARSET_UTF8);
        headers.add(HttpHeaderKey.CONTENT_LENGTH, body.getContentLength());
    }

    public void sendStaticResourceResponse(String urlWithExtension) throws IOException {
        this.statusLine = new StatusLine(HttpStatus.OK);
        int index = urlWithExtension.lastIndexOf(FILE_EXTENSION_DELIMITER);
        String url = urlWithExtension.substring(0, index);
        String extension = urlWithExtension.substring(index + 1);
        String responseBody = readFileContent(url, extension);
        setBody(new ResponseBody(ContentMimeType.getMimeByExtension(extension), responseBody));
        outputStream.write(getResponse().getBytes());
        outputStream.flush();
    }

    public void send200Response() throws IOException {
        this.statusLine = new StatusLine(HttpStatus.OK);
        outputStream.write(getResponse().getBytes());
        outputStream.flush();
    }

    public void sendRedirect(String location) throws IOException {
        this.statusLine = new StatusLine(HttpStatus.FOUND);
        headers.add(HttpHeaderKey.LOCATION, location);
        outputStream.write(getResponse().getBytes());
        outputStream.flush();
    }

    private String readFileContent(String url, String extension) throws IllegalArgumentException, IOException {
        URL resource = getClass().getClassLoader()
                .getResource(STATIC_FILES_BASE_PATH + url + FILE_EXTENSION_DELIMITER + extension);
        if (resource == null) {
            throw new IllegalArgumentException(STATIC_FILES_BASE_PATH + url + FILE_EXTENSION_DELIMITER + extension + "이 존재하지 않습니다.");
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String getResponse() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(statusLine.formatStatusLine()).append(LINE_SEPARATOR);
        responseBuilder.append(headers.getHeaderResponse()).append(LINE_SEPARATOR);
        responseBuilder.append(LINE_SEPARATOR);

        if (body != null) {
            responseBuilder.append(body.getValue());
        }
        return responseBuilder.toString();
    }
}
