package org.apache.coyote.web.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.coyote.file.StaticFileHandler;
import org.apache.coyote.support.ContentType;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpStatus;
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

    public void forward(final String url) throws IOException {
        try {
            if (url.equals("/")) {
                forwardDefault();
                return;
            }
            String extension = url.substring(url.lastIndexOf(".") + 1);
            ContentType contentType = ContentType.from(extension);
            String responseBody = StaticFileHandler.getFileLines(url);
            httpHeaders.setContentType(contentType);
            httpHeaders.setContentLength(responseBody.length());
            httpResponseExchange.response(httpStatus, httpHeaders, responseBody);
        } catch (IOException e) {
            sendError(HttpStatus.NOT_FOUND, "/404.html");
        }
    }

    public void forwardDefault() throws IOException {
        String responseBody = "Hello world!";
        httpHeaders.setContentType(ContentType.STRINGS);
        httpHeaders.setContentLength(responseBody.length());
        httpResponseExchange.response(httpStatus, httpHeaders, responseBody);
    }

    public void redirect(final String url) {
        try {
            httpStatus = HttpStatus.FOUND;
            ContentType contentType = ContentType.from(url.substring(url.lastIndexOf(".") + 1));
            String responseBody = StaticFileHandler.getFileLines(url);
            httpHeaders.setLocation(url);
            httpHeaders.setContentType(contentType);
            httpHeaders.setContentLength(responseBody.length());
            httpResponseExchange.response(httpStatus, httpHeaders, responseBody);
        } catch (IOException e) {
            sendError(HttpStatus.NOT_FOUND, "/404.html");
        }
    }

    public void sendError(final HttpStatus httpStatus, final String url) {
        setErrorStatus(httpStatus);
        ContentType contentType = ContentType.from(url.substring(url.lastIndexOf(".") + 1));
        try {
            String responseBody = StaticFileHandler.getFileLines(url);
            httpHeaders.setContentType(contentType);
            httpHeaders.setLocation(url);
            httpHeaders.setContentLength(responseBody.length());
            httpResponseExchange.response(httpStatus, httpHeaders, responseBody);
        } catch (IOException e) {
            sendError(HttpStatus.NOT_FOUND, "/404.html");
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
