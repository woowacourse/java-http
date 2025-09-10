package org.apache.coyote.util;

import org.apache.coyote.dto.RequestInfo;

import java.util.HashMap;
import java.util.Map;

public class RequestLineParser {
    public static RequestInfo parse(final String requestLine) {
        final String[] parts = requestLine.split(" ");
        final String method = parts[0].toUpperCase();
        final String fullPath = parts.length >= 2 ? parts[1] : "/";

        String path = fullPath;
        Map<String,String> queryParams = new HashMap<>();

        if (fullPath.contains("?")) {
            String[] pathParts = fullPath.split("\\?", 2);
            path = pathParts[0];
            queryParams = FormDataParser.parseFormData(pathParts[1]);
        }
        return new RequestInfo(method, path, queryParams);
    }

}
