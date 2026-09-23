package org.apache.coyote.http11;

import java.util.Arrays;

public record RequestHeader(
        String method,
        String path,
        RequestParams requestParams,
        int contentLength,
        ContentType contentType
) {

    public static RequestHeader from(String requestLine, Headers headers) {
        String[] splitRequestLine = requestLine.split(" ");
        String method = splitRequestLine[0];
        String uri = splitRequestLine[1];
        int separatorIndex = uri.indexOf('?');
        if (separatorIndex != -1) {
            String path = uri.substring(0, separatorIndex);
            String queryString = uri.substring(separatorIndex + 1);
            RequestParams requestParams = RequestParams.of(queryString);
            return RequestHeader.from(method, path, requestParams, headers);
        }
        return RequestHeader.from(method, uri, RequestParams.empty(), headers);
    }

    private static RequestHeader from(String method, String path, RequestParams requestParams, Headers headers) {
        String acceptLine = headers.getValue("accept");
        ContentType contentType = Arrays.stream(ContentType.values())
                .filter(type -> acceptLine.contains(type.getType()))
                .findFirst()
                .orElse(ContentType.HTML);
        int contentLength = Integer.parseInt(headers.getValue("content-length"));
        return new RequestHeader(method, path, requestParams, contentLength, contentType);
    }
}
