package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.http11.common.Cookie;
import org.apache.coyote.http11.common.HttpHeaders;

public class HttpResponse {

    private final OutputStream outputStream;
    private final StatusLine statusLine;
    private final HttpHeaders headers;
    private String body;

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

    public static HttpResponse ok(OutputStream outputStream) {
        return new HttpResponse(outputStream, StatusLine.ok(), HttpHeaders.empty(), "");
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

    private String buildHttpResponse() {
        String statusLineResponse = this.statusLine.buildStatusLineResponse();
        String headersResponse = this.headers.buildHttpHeadersResponse();

        return String.join("\r\n",
                statusLineResponse,
                headersResponse,
                "",
                this.body
        );
    }

    public void setStaticResourceResponse(String pathWithExtension) throws IOException {
        StaticResourceResponseFactory factory = new StaticResourceResponseFactory(pathWithExtension);
        String contentType = factory.createContentType();
        String responseBody = factory.createResponseBody();

        this.headers.addHeader(HttpHeaders.CONTENT_TYPE, contentType);
        this.body = responseBody;
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
