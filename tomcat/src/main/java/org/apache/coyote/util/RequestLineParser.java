package org.apache.coyote.util;

import org.apache.coyote.dto.RequestInfo;

public class RequestLineParser {
    public static RequestInfo parse(String requestLine) {
        String[] parts = requestLine.split(" ");
        String method = parts.length >= 1 ? parts[0].toUpperCase() : "GET";
        String path = parts.length >= 2 ? parts[1] : "/";
        return new RequestInfo(method, path);
    }


}
