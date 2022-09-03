package org.apache.coyote.http11.response;

import java.io.IOException;
import nextstep.jwp.utils.FileReader;
import org.apache.coyote.http11.request.HttpRequestHeader;

public class HttpResponse {

    private static final String DEFAULT_BODY = "Hello world!";
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String CONTENT_LENGTH = "Content-Length: ";
    private static final String CONTENT_TYPE = "Content-Type: ";

    private final String response;

    public HttpResponse(HttpRequestHeader httpRequestHeader) throws IOException {
        response = getResponse(httpRequestHeader);
    }

    private String getResponse(HttpRequestHeader httpRequestHeader) throws IOException {
        final String body = getBody(httpRequestHeader);
        final String header = getHeader(httpRequestHeader, body);
        return String.join("\r\n", header, body);
    }

    private String getBody(HttpRequestHeader httpRequestHeader) throws IOException {
        String requestUri = httpRequestHeader.getRequestUri();
        return FileReader.readByPath(requestUri)
                .orElseGet(() -> DEFAULT_BODY);
    }

    private String getHeader(HttpRequestHeader httpRequestHeader, String body) {
        String responseLine = HTTP_VERSION + "200 OK ";
        String contentType = getContentType(httpRequestHeader.getRequestUri());
        String contentLength = body.getBytes().length + " ";

        return String.join("\r\n",
                responseLine,
                CONTENT_TYPE + contentType,
                CONTENT_LENGTH + contentLength,
                "");
    }

    private String getContentType(String requestUri) {
        return ContentType.find(requestUri) + ";charset=utf-8 ";
    }

    public byte[] getBytes() {
        return response.getBytes();
    }

    public String getResponse() {
        return response;
    }
}
