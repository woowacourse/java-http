package org.apache.coyote.http11.httpmessage.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.httpmessage.common.Headers;
import org.apache.coyote.http11.httpmessage.request.requestline.RequestLine;
import org.apache.coyote.http11.httpmessage.request.requestline.RequestUri;
import org.apache.coyote.http11.httpmessage.request.requestbody.RequestBody;

public class Request {

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestBody requestBody;

    public Request(RequestLine requestLine, Headers headers, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static Request of(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        final RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        final Headers headers = new Headers(bufferedReader);

        if (headers.getContentLength() == 0) {
            return new Request(requestLine, headers, null);
        }
        final RequestBody requestBody = RequestBody.from(bufferedReader, headers.getContentLength());
        return new Request(requestLine, headers, requestBody);
    }

    public boolean isGetMethod() {
        return requestLine.isGetMethod();
    }

    public boolean isPostMethod() {
        return requestLine.isPostMethod();
    }

    public RequestUri getUri() {
        return requestLine.getRequestUri();
    }

    public boolean isMatchUri(String requestUri) {
        return requestLine.isMatchUri(requestUri);
    }

    public boolean isFileRequest() {
        return requestLine.getRequestUri().isFileRequest();
    }

    public boolean hasQueryString() {
        return requestLine.getRequestUri().hasQueryStrings();
    }

    public String getBody() {
        return requestBody.getRequestBody();
    }
}
