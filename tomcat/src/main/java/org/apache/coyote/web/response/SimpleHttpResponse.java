package org.apache.coyote.web.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.coyote.file.StaticFileHandler;
import org.apache.coyote.support.ContentType;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.web.session.Cookie;

public class SimpleHttpResponse {

    private final HttpResponseExchange httpResponseExchange;

    private HttpHeaders httpHeaders;
    private List<Cookie> cookies;

    public SimpleHttpResponse(final OutputStream outputStream) {
        this.httpResponseExchange = new HttpResponseExchange(outputStream);
        httpHeaders = new HttpHeaders(new LinkedHashMap<>());
        cookies = new ArrayList<>();
    }

    public void addHeader(final String headerName, final String value) {
        httpHeaders.put(headerName, value);
    }

    public void forward(final String url) throws IOException {
        if (url.equals("/")) {
            forwardDefault();
            return;
        }
        String extension = url.substring(url.lastIndexOf(".") + 1);
        ContentType contentType = ContentType.from(extension);
        try {
            String responseBody = StaticFileHandler.getFileLines(url);
            httpHeaders.setContentType(contentType);
            httpHeaders.setContentLength(responseBody.length());
            httpResponseExchange.response200(httpHeaders, responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void forwardDefault() throws IOException {
        String responseBody = "Hello world!";
        httpHeaders.setContentType(ContentType.STRINGS);
        httpHeaders.setContentLength(responseBody.length());
        httpResponseExchange.response200(httpHeaders, responseBody);
    }

    public void redirect(final String url) {
        ContentType contentType = ContentType.from(url.substring(url.lastIndexOf(".") + 1));
        try {
            String responseBody = StaticFileHandler.getFileLines(url);
            httpHeaders.setContentType(contentType);
            httpHeaders.setLocation(url);
            httpHeaders.setContentLength(responseBody.length());
            httpResponseExchange.response302(httpHeaders, responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
