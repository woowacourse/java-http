package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.StringJoiner;

import org.apache.coyote.http11.request.HttpHeader;

public class HttpResponse {

    private static final String VERSION = "HTTP/1.1";
    private static final String HEADER_PAIR_DELIMITER = ": ";
    private static final String SPACE = " ";
    private static final String EMPTY_STRING = "";
    private static final String HTTP_LINE_BREAK = "\r\n";

    private boolean isBodyEmpty = true;
    private final OutputStream outputStream;
    private final StringJoiner responseJoiner = new StringJoiner(HTTP_LINE_BREAK);

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void addStatusLine(HttpStatusCode statusCode) {
        StringJoiner statusLine = new StringJoiner(SPACE);
        statusLine.add(VERSION)
                .add(String.valueOf(statusCode.getCode()))
                .add(statusCode.getDescription())
                .add(EMPTY_STRING);
        responseJoiner.add(statusLine.toString());
    }

    public void addHeader(HttpHeader header, String value) {
        responseJoiner.add(header.getName() + HEADER_PAIR_DELIMITER + value + SPACE);
    }

    public void addBody(String body) {
        isBodyEmpty = false;
        responseJoiner.add(EMPTY_STRING);
        responseJoiner.add(body);
    }

    public void writeResponse() throws IOException {
        responseJoiner.add(EMPTY_STRING);
        if (isBodyEmpty) {
            responseJoiner.add(EMPTY_STRING);
        }
        outputStream.write(responseJoiner.toString().getBytes());
        outputStream.flush();
    }
}
