package org.apache.coyote.http11.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.request.Request;

public class RequestReader {

    private final BufferedReader reader;

    public RequestReader(BufferedReader reader) {
        this.reader = reader;
    }


    public Request read() throws IOException {
        String requestHead = reader.readLine();
        String[] head = requestHead.split(" ");
        String method = head[0];
        String uri = head[1];

        Headers headers = readHeaders();
        String body = readBody(headers);

        return Request.from(
                method,
                uri,
                headers,
                body
        );
    }

    private Headers readHeaders() throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while (!"".equals((line = reader.readLine()))) {
            String[] header = line.split(": ");
            String key = header[0];
            String value = header[1].trim();
            headers.put(key, value);
        }

        return new Headers(headers);
    }

    private String readBody(Headers headers) throws IOException {
        String body = "";
        if (headers.hasContentLength()) {
            int contentLength = Integer.parseInt(headers.find("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            body = new String(buffer);
        }
        return body;
    }
}
