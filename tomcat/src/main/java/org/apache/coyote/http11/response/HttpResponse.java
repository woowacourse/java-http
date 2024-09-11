package org.apache.coyote.http11.response;

import java.io.IOException;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.ResourceReader;
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
        this.responseHeaders = new ResponseHeaders();
        this.responseBody = new ResponseBody();
    }

    public void ok(String fileName) throws IOException {
        String content = ResourceReader.read(fileName);
        this.statusLine = new StatusLine(HttpStatus.OK);
        this.responseHeaders.addContentHeaders(ContentType.findByExtension(fileName), content);
        this.responseBody = new ResponseBody(content);
    }

    public void redirect(String path) {
        this.statusLine = new StatusLine(HttpStatus.FOUND);
        this.responseHeaders.addHeader(HttpHeader.LOCATION, path);
        this.responseBody = new ResponseBody();
    }

    public void addCookie(Cookies cookies) {
        responseHeaders.addHeader(HttpHeader.SET_COOKIE, cookies.toCookieHeader());
    }

    public String toMessage() {
        return String.join("\r\n",
                statusLine.toMessage(),
                responseHeaders.toMessage(),
                responseBody.toMessage());
    }
}
