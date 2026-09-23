package org.apache.coyote.http11;

import java.util.Arrays;

public record RequestHeader(
        HttpMethod method,
        String uri,
        String version,
        int contentLength,
        ContentType contentType
) {

    public static RequestHeader from(String requestLine, Headers headers) {
        String[] splitRequestLine = requestLine.split(" ");
        String method = splitRequestLine[0];
        HttpMethod httpMethod = HttpMethod.valueOf(method);
        String uri = splitRequestLine[1];
        String version = splitRequestLine[2];
        return RequestHeader.from(httpMethod, uri, version, headers);
    }

    private static RequestHeader from(HttpMethod method, String path, String version, Headers headers) {
        String acceptLine = headers.getValue("accept");
        ContentType contentType = Arrays.stream(ContentType.values())
                .filter(type -> acceptLine.contains(type.getName()))
                .findFirst()
                .orElse(ContentType.HTML);
        String contentLengthValue = headers.getValue("content-length");
        if (contentLengthValue.isEmpty()) {
            contentLengthValue = "0";
        }
        int contentLength = Integer.parseInt(contentLengthValue);
        return new RequestHeader(method, path, version, contentLength, contentType);
    }

    public String getPath() {
        int separatorIndex = uri.indexOf('?');
        if (separatorIndex != -1) {
            return uri.substring(0, separatorIndex);
        }
        return uri;
    }

    public String getQueryString() {
        int separatorIndex = uri.indexOf('?');
        if (separatorIndex != -1) {
            return uri.substring(separatorIndex + 1);
        }
        return "";
    }

    public String getContentTypeName() {
        return contentType.getName();
    }

    public String getMethodName() {
        return method.getName();
    }
}
