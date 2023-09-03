package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.request.Body;
import org.apache.coyote.http11.request.Header;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.StartLine;

public class HttpRequestParser {

    public HttpRequest parse(BufferedReader reader) {
        StartLine startLine = StartLine.from(readLine(reader));
        List<Header> headers = parseHeaders(reader);
        Body body = parseBody(reader, contentLength(headers));
        return HttpRequest.builder()
                .startLine(startLine)
                .headers(headers)
                .body(body)
                .build();
    }

    private String readLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    private List<Header> parseHeaders(BufferedReader reader) {
        List<Header> headers = new ArrayList<>();
        String line;
        while ((line = readLine(reader)) != null) {
            if (line.isEmpty()) {
                break;
            }
            headers.add(Header.from(line));
        }
        return headers;
    }

    private Integer contentLength(List<Header> headers) {
        return headers.stream()
                .filter(it -> it.name().equals("Content-Length"))
                .map(it -> Integer.parseInt(it.value()))
                .findAny()
                .orElse(null);
    }

    private Body parseBody(BufferedReader reader, Integer length) {
        if (length == null) {
            return new Body(null);
        }
        char[] chars = new char[length];
        try {
            reader.read(chars);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Body(new String(chars));
    }
}
