package org.apache.coyote.http11;

import java.util.Arrays;
import javax.annotation.Nonnull;

public record RequestHeader(
        HttpMethod method,
        String uri,
        String version,
        HttpCookie cookie,
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
        String cookieLine = headers.getValue("cookie");
        String contentLengthValue = headers.getValue("content-length");
        HttpCookie httpCookie = HttpCookie.from(cookieLine);
        ContentType contentType = findContentType(acceptLine);
        int contentLength = checkContentLength(contentLengthValue);
        return new RequestHeader(method, path, version, httpCookie, contentLength, contentType);
    }

    private static int checkContentLength(String contentLengthValue) {
        if (contentLengthValue.isEmpty()) {
            contentLengthValue = "0";
        }
        return Integer.parseInt(contentLengthValue);
    }

    @Nonnull
    private static ContentType findContentType(String acceptLine) {
        return Arrays.stream(ContentType.values())
                .filter(type -> acceptLine.contains(type.getName()))
                .findFirst()
                .orElse(ContentType.HTML);
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

    public String getJSessionId() {
        return cookie.getJSessionId();
    }

    public boolean hasJSessionId() {
        return cookie.hasJSessionId();
    }
}
