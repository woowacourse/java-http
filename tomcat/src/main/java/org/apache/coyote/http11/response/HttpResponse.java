package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;

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

    public void setResponseBodyFile(HttpRequest httpRequest) throws IOException {
        Path path = buildPath(httpRequest);
        String responseBody = new String(Files.readAllBytes(path));
        String contentType = createContentType(httpRequest.getExtension());
        headers.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
        this.body = responseBody;
    }

    private Path buildPath(HttpRequest httpRequest) {
        String resourcePath = String.format("static%s.%s", httpRequest.getPath(), httpRequest.getExtension());
        if (getClass().getClassLoader().getResource(resourcePath) == null) {
            resourcePath = "static/404.html";
        }
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        return Paths.get(resource.getPath());
    }

    private String createContentType(String fileExtension) {
        String contentType;
        if (fileExtension.endsWith("html")) {
            contentType = "text/html;charset=utf-8";
        } else if (fileExtension.endsWith("css")) {
            contentType = "text/css;charset=utf-8";
        } else {
            contentType = "text/plain;charset=utf-8";
        }
        return contentType;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.statusLine.setHttpStatus(httpStatus);
    }

    public void addHeader(String key, String value) {
        this.headers.addHeader(key, value);
    }

    public void setResponseBody(String body) {
        this.body = body;
    }

    public void sendRedirect(String path) {
        setHttpStatus(HttpStatus.FOUND);
        addHeader(HttpHeaders.LOCATION, path);
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
}
