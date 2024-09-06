package org.apache.catalina;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> header;

    public HttpRequest(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        this.requestLine = new RequestLine(bufferedReader.readLine());
        this.header = mapHeader(bufferedReader);
    }

    private Map<String, String> mapHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> header = new HashMap<>();
        String rawLine;

        while ((rawLine = bufferedReader.readLine()) != null && !rawLine.isEmpty()) {
            String[] headerEntry = rawLine.split(": ", 2);
            header.put(headerEntry[0], headerEntry[1]);
        }
        return header;
    }
}
