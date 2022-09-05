package org.apache.coyote.web.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.coyote.support.ContentType;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpStatus;
import org.apache.coyote.support.Url;
import org.apache.coyote.web.session.Cookie;

public class HttpResponse {

    private final HttpResponseExchange httpResponseExchange;

    private HttpStatus httpStatus;
    private HttpHeaders httpHeaders;
    private List<Cookie> cookies;

    public HttpResponse(final OutputStream outputStream) {
        this.httpResponseExchange = new HttpResponseExchange(outputStream);
        httpStatus = HttpStatus.OK;
        httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        cookies = new ArrayList<>();
    }

    public void addHeader(final String headerName, final String value) {
        httpHeaders.put(headerName, value);
    }

    public void forward(final Url url) {
        try {
            if (url.isDefaultPath()) {
                forwardDefault();
                return;
            }
            ContentType contentType = ContentType.from(url.extractFileExtension());
            String responseBody = url.extractFileLines();
            httpHeaders.setContentType(contentType);
            httpHeaders.setContentLength(responseBody.length());
            httpResponseExchange.response(httpStatus, httpHeaders, responseBody);
        } catch (IOException e) {
            sendError(HttpStatus.NOT_FOUND, Url.createUrl("/404.html"));
        }
    }

    public void forwardDefault() throws IOException {
        String responseBody = "Hello world!";
        httpHeaders.setContentType(ContentType.STRINGS);
        httpHeaders.setContentLength(responseBody.length());
        httpResponseExchange.response(httpStatus, httpHeaders, responseBody);
    }

    public void redirect(final Url url) {
        try {
            httpStatus = HttpStatus.FOUND;
            ContentType contentType = ContentType.from(url.extractFileExtension());
            String responseBody = url.extractFileLines();
            httpHeaders.setLocation(url.getValue());
            httpHeaders.setContentType(contentType);
            httpHeaders.setContentLength(responseBody.length());
            httpResponseExchange.response(httpStatus, httpHeaders, responseBody);
        } catch (IOException e) {
            sendError(HttpStatus.NOT_FOUND, Url.createUrl("/404.html"));
        }
    }

    public void sendError(final HttpStatus httpStatus, final Url url) {
        try {
            setErrorStatus(httpStatus);
            ContentType contentType = ContentType.from(url.extractFileExtension());
            String responseBody = url.extractFileLines();
            httpHeaders.setContentType(contentType);
            httpHeaders.setContentLength(responseBody.length());
            httpResponseExchange.response(httpStatus, httpHeaders, responseBody);
        } catch (IOException e) {
            sendError(HttpStatus.NOT_FOUND, Url.createUrl("/404.html"));
        }
    }

    private void setErrorStatus(final HttpStatus httpStatus) {
        if (!httpStatus.isErrorCode()) {
            throw new IllegalStateException();
        }
        this.httpStatus = httpStatus;
    }

    public void setCookie(final Cookie cookie) {
        cookies.clear();
        cookies.add(cookie);
        httpHeaders.setCookie(cookie);
    }

    public void addCookie(final Cookie cookie) {
        cookies.add(cookie);
        httpHeaders.addCookie(cookie);
    }
}
