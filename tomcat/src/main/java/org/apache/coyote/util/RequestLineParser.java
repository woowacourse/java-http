package org.apache.coyote.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.dto.RequestLine;

public class RequestLineParser {
    public static RequestLine parse(final String requestLine) {
        final String[] parts = requestLine.split(" ");
        final String method = parts[0].toUpperCase();
        final String fullPath = parts[1];
        final String version = parts[2];

        String path = fullPath;
        Map<String,String> queryParams = new HashMap<>();

        if (fullPath.contains("?")) {
            String[] pathParts = fullPath.split("\\?", 2);
            path = pathParts[0];
            queryParams = FormDataParser.parseFormData(pathParts[1]);
        }
        return new RequestLine(method, path, queryParams, version);
    }
}
