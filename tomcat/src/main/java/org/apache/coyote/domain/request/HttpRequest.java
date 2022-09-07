package org.apache.coyote.domain.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.domain.HttpCookie;
import org.apache.coyote.domain.request.requestline.RequestLine;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

public class HttpRequest {

    private static final String HEADER_DELIMITER = " ";

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;
    private final HttpCookie httpCookie;

    private HttpRequest(RequestLine requestLine, RequestHeader requestHeader,
                        RequestBody requestBody, HttpCookie httpCookie) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
        this.httpCookie = httpCookie;
    }

    public static HttpRequest from(BufferedReader inputReader) {
        try {
            String startLine = inputReader.readLine();
            RequestLine requestLine = RequestLine.from(startLine);
            RequestHeader requestHeader = RequestHeader.from(inputReader);
            HttpCookie httpCookie = HttpCookie.from(requestHeader.getCookies());
            RequestBody requestBody = RequestBody.of(inputReader, requestHeader.getContentLength());
            return new HttpRequest(requestLine, requestHeader, requestBody, httpCookie);
        } catch (IOException e) {
            throw new IllegalArgumentException("");
        }
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

    public Session getSession() {
        if (httpCookie.hasJSESSIONID()) {
            return SessionManager.findSession(httpCookie.getJSESSIONID())
                    .orElse(new Session(UUID.randomUUID().toString()));
        }
        return new Session(UUID.randomUUID().toString());
    }

    public boolean checkSession() {
        Optional<Session> session = SessionManager.findSession(httpCookie.getJSESSIONID());
        if (session.isEmpty()) {
            return false;
        }
        Optional<Object> user = session.get().getAttribute("user");
        return user.isPresent();
    }
}
