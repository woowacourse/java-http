package org.apache.coyote.domain.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHeader {

    private final Map<String, String> requestHeader;

    private RequestHeader(Map<String, String> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public static RequestHeader from(BufferedReader inputReader) throws IOException {
        Map<String, String> requestHeader = new HashMap<>();
        while (inputReader.ready()) {
            String line = inputReader.readLine();
            if (line.equals("")) {
                break;
            }
            String[] header = line.split(": ");
            requestHeader.put(header[0], header[1]);
        }
        return new RequestHeader(requestHeader);
    }

    public int getContentLength() {
        return Integer.parseInt(requestHeader.getOrDefault("Content-Length", "0"));
    }

    public String getCookies() {
        return requestHeader.getOrDefault("Cookie", "");
    }
}
