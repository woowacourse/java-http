package org.apache.coyote.http11.response;

import org.apache.coyote.http11.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private final StatusLine statusLine;
    private final Header header;
    private final Body body;

    public HttpResponse(StatusLine statusLine, Header header, Body body) {
        this.statusLine = statusLine;
        this.header = header;
        this.body = body;
    }

    public HttpResponse(StatusLine statusLine, Header header) {
        this.statusLine = statusLine;
        this.header = header;
        this.body = Body.EMPTY;
    }

    public static HttpResponse createStaticResponseByPath(HttpVersion httpVersion, HttpStatus httpStatus, String filePath) throws IOException {
        StatusLine statusLine = new StatusLine(httpVersion, httpStatus);
        File file = new File(HttpResponse.class
                .getClassLoader()
                .getResource(filePath)
                .getFile());

        String bodyMessage = new String(Files.readAllBytes(file.toPath()));
        Body body = new Body(bodyMessage);

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(HttpHeader.CONTENT_TYPE.value(), ContentType.findContentTypeByFilename(filePath).value());
        headers.put(HttpHeader.CONTENT_LENGTH.value(), String.valueOf(bodyMessage.getBytes().length));
        Header header = new Header(headers);

        return new HttpResponse(statusLine, header, body);
    }

    public HttpVersion httpVersion() {
        return statusLine.httpVersion();
    }

    public HttpStatus httpStatus() {
        return statusLine.httpStatus();
    }

    public Header header() {
        return this.header;
    }

    public Body body() {
        return this.body;
    }
}
