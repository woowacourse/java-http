package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private RequestLine requestLine;
    private HttpHeaders headers;
    private String body;

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        requestLine = new RequestLine(bufferedReader.readLine());
        headers = parseHeaders(bufferedReader);
        body = parseBody(bufferedReader);
    }

    private static HttpHeaders parseHeaders(BufferedReader bufferedReader) throws IOException {
        String header;
        List<String> headers = new ArrayList<>();
        while ((header = bufferedReader.readLine()) != null) {
            if (header.isEmpty()) {
                break;
            }
            headers.add(header);
        }
        return new HttpHeaders(headers);
    }

    private String parseBody(BufferedReader bufferedReader) throws IOException {
        int contentLength = headers.getContentLength();
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            bufferedReader.read(bodyChars, 0, contentLength);
            return new String(bodyChars);
        }
        return "";
    }

    public HttpCookie getHttpCookie() {
        return headers.getCookie();
    }

    public String getBody() {
        return body;
    }

    public String getRequestUriPath() {
        return requestLine.getUriPath();
    }

    public String getRequestMethod() {
        return requestLine.getMethod();
    }
}
