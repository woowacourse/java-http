package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestManager {

    private final RequestHeaderManager requestHeaderManager;
    private RequestBodyManager requestBodyManager;

    public RequestManager(RequestHeaderManager requestHeaderManager, RequestBodyManager requestBodyManager) {
        this.requestHeaderManager = requestHeaderManager;
        this.requestBodyManager = requestBodyManager;
    }

    public void read(BufferedReader br) throws IOException {
        requestHeaderManager.read(br);
        if (hasBodyByHeader(requestHeaderManager)) {
            requestBodyManager.read(br);
        }
    }

    private static boolean hasBodyByHeader(RequestHeaderManager requestHeaderManager) {
        String te = requestHeaderManager.getHeader("Transfer-Encoding");
        if (te != null && te.equalsIgnoreCase("chunked")) {
            return true;
        }

        String cl = requestHeaderManager.getHeader("Content-Length");
        if (cl == null) {
            return false;
        }
        try {
            return Long.parseLong(cl) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean equalsPath(String path) {
        return requestHeaderManager.equalsPath(path);
    }
}
