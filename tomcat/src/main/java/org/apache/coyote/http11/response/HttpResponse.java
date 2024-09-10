package org.apache.coyote.http11.response;

import java.io.IOException;
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
    }

    public void ok(String fileName) throws IOException {
        String content = ResourceReader.read(fileName);
        this.statusLine = new StatusLine(HttpStatus.OK);
        this.responseHeaders = ResponseHeaders.of(ContentType.findByExtension(fileName), content);
        this.responseBody = new ResponseBody(content);
    }

    public void redirect(String path) {
        this.statusLine = new StatusLine(HttpStatus.FOUND);
        this.responseHeaders = ResponseHeaders.of(HttpHeader.LOCATION, path);
        this.responseBody = ResponseBody.empty();
    }

    public void setCookie(String value) {
        responseHeaders.addHeader(HttpHeader.SET_COOKIE, value);
    }

    public String write() {
        return String.join("\r\n",
                statusLine.write(),
                responseHeaders.write(),
                responseBody.write());
    }
}
