package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.Constants.CRLF;

import java.io.IOException;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.response.body.ResponseBody;
import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.ResponseHeaders;
import org.apache.coyote.http11.response.startLine.HttpStatus;
import org.apache.coyote.http11.response.startLine.StatusLine;

public class HttpResponse {

    private StatusLine statusLine;
    private ResponseHeaders responseHeaders;
    private ResponseBody responseBody;

    public HttpResponse(HttpStatus httpStatus, ResponseHeaders responseHeaders, ResponseBody responseBody) {
        this.statusLine = new StatusLine(httpStatus);
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public HttpResponse() {
        this(HttpStatus.OK, new ResponseHeaders(), new ResponseBody());
    }

    public void ok(String fileName) throws IOException {
        statusLine = new StatusLine(HttpStatus.OK);
        responseBody = new ResponseBody(ResourceReader.read(fileName));
        responseHeaders.addHeader(HttpHeader.CONTENT_TYPE, ContentType.findByExtension(fileName).value());
        responseHeaders.addHeader(HttpHeader.CONTENT_LENGTH, responseBody.getContentLength());
    }

    public void redirect(String path) {
        statusLine = new StatusLine(HttpStatus.FOUND);
        responseHeaders.addHeader(HttpHeader.LOCATION, path);
        responseBody = new ResponseBody();
    }

    public void setBody(String content) {
        responseBody = new ResponseBody(content);
        responseHeaders.addHeader(HttpHeader.CONTENT_LENGTH, responseBody.getContentLength());
    }

    public void setContentType(ContentType contentType) {
        responseHeaders.addHeader(HttpHeader.CONTENT_TYPE, contentType.value());
    }

    public void addHeader(HttpHeader httpHeader, String value) {
        responseHeaders.addHeader(httpHeader, value);
    }

    public void addCookie(Cookie cookie) {
        responseHeaders.addHeader(HttpHeader.SET_COOKIE, cookie.toCookieHeader());
    }

    public String toMessage() {
        return String.join(CRLF,
                statusLine.toMessage(),
                responseHeaders.toMessage(),
                responseBody.toMessage());
    }
}
