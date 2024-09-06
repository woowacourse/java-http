package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpHeaders;

public class HttpResponse {

    private final OutputStream outputStream;
    private final StatusLine statusLine;
    private final HttpHeaders headers;
    private String body;

    public HttpResponse(OutputStream outputStream) {
        this(outputStream, new StatusLine(), new HttpHeaders(), "");
    }

    public HttpResponse(
            OutputStream outputStream,
            StatusLine statusLine,
            HttpHeaders headers,
            String body
    ) {
        this.outputStream = outputStream;
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public String buildHttpResponse() {
        String statusLineResponse = this.statusLine.buildStatusLineResponse();
        String headersResponse = this.headers.buildHttpHeadersResponse();

        return String.join("\r\n",
                statusLineResponse,
                headersResponse,
                "",
                this.body
        );
    }

    public void sendRedirect(String path) {
        setHttpStatus(HttpStatus.FOUND);
        setHeader(HttpHeaders.LOCATION, path);
    }

    public void addCookie(Cookie cookie) {
        headers.addHeader(HttpHeaders.SET_COOKIE, cookie.getCookieString());
    }

    public void write() {
        String contentLength = String.valueOf(body.getBytes().length);
        headers.addHeader(HttpHeaders.CONTENT_LENGTH, contentLength);

        String response = buildHttpResponse();

        try {
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            throw new IllegalArgumentException("쓰기에 실패했습니다.", e);
        }
    }

    public void setStaticResourceResponse(String pathWithExtension) throws IOException {
        Path path = buildPath(pathWithExtension);
        String responseBody = new String(Files.readAllBytes(path));
        String extension = pathWithExtension.substring(pathWithExtension.lastIndexOf(".") + 1);
        String contentType = createContentType(extension);
        headers.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
        this.body = responseBody;
    }

    private Path buildPath(String pathWithExtension) {
        String resourcePath = String.format("static%s", pathWithExtension);
        if (getClass().getClassLoader().getResource(resourcePath) == null) {
            resourcePath = "static/404.html";
        }
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        return Paths.get(resource.getPath());
    }

    private String createContentType(String fileExtension) {
        return switch (fileExtension) {
            case "html" -> "text/html;charset=utf-8";
            case "css" -> "text/css;charset=utf-8";
            case "js" -> "text/javascript;charset=utf-8";
            case "svg" -> "image/svg+xml;charset=utf-8";
            default -> "text/plain;charset=utf-8";
        };
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.statusLine.setHttpStatus(httpStatus);
    }

    public void setHeader(String key, String value) {
        this.headers.addHeader(key, value);
    }

    public void setResponseBody(String body) {
        this.body = body;
    }
}
