package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringJoiner;

class HttpMessage {

    private static final String EMPTY_VALUE = "";

    private final BufferedReader reader;

    private String requestLine = EMPTY_VALUE;
    private String headers = EMPTY_VALUE;
    private String messageBody = EMPTY_VALUE;

    HttpMessage(final InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        this.reader = new BufferedReader(inputStreamReader);
        readHttpRequest();
    }

    private void readHttpRequest() {
        this.requestLine = readLine();
        final String contentLength = readHeaders();

        if (!contentLength.isEmpty()) {
            this.messageBody = readByLength(Integer.parseInt(contentLength));
        }
    }

    /**
     * Read Http Headers and return Content-Length in String type to Read Message Body.
     * @return Content-Length in String type. if the request doesn't contain Content-Length Header, return "";
     */
    private String readHeaders() {
        String contentLength = EMPTY_VALUE;
        final StringJoiner joiner = new StringJoiner("\r\n");
        String line;
        while (!(line = readLine()).isEmpty()) {
            if (line.startsWith("Content-Length")) {
                contentLength = line.split(":")[1].trim();
            }
            joiner.add(line);
        }
        this.headers = joiner.toString();
        return contentLength;
    }

    private String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid byte read");
        }
    }

    private String readByLength(final int length) {
        final char[] buffer = new char[length];
        try {
            reader.read(buffer, 0, length);
            return new String(buffer);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid byte read");
        }
    }

    public String getRequestLine() {
        return requestLine;
    }

    public String getHeaders() {
        return headers;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
