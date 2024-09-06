package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private final Map<String, String> requestLine;

    public RequestLine(String requestLine) {
        this.requestLine = mapRequestLine(requestLine);
    }

    private Map<String, String> mapRequestLine(String rawRequestLine) {
        Map<String, String> mappedRequestLine = new HashMap<>();

        String[] requestLineEntries = rawRequestLine.split(" ");
        requestLine.put("Version of the protocol", requestLineEntries[0]);
        requestLine.put("Status code", requestLineEntries[1]);
        requestLine.put("Status message", requestLineEntries[2]);

        return mappedRequestLine;
    }
}
