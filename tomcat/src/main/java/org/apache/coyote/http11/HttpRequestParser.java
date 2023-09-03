package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    private final BufferedReader reader;

    public HttpRequestParser(final InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public RequestLine parseRequestLine() throws IOException {
        final String line = reader.readLine();
        String[] s = line.split(" ");
        return RequestLine.from(s[0], s[1], s[2]);
    }

    public Map<String, String> parseRequestHeader() throws IOException {
        // TODO: MultiValueMap
        final Map<String, String> header = new HashMap<>();
        String line;
        while (!"".equals(line = reader.readLine())) {
            String[] value = line.split(": ");
            header.put(value[0], value[1]);
        }
        return header;
    }

    public Map<String, String> parseRequestBody(final String contentLengthHeader) throws IOException {
        // TODO: MultiValueMap
        final Map<String, String> body = new HashMap<>();
        int contentLength = Integer.parseInt(contentLengthHeader);
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);

        for (String temp : new String(buffer).split("&")) {
            String[] value = temp.split("=");
            body.put(value[0], URLDecoder.decode(value[1], "UTF-8"));
        }
        return body;
    }
}
