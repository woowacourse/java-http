package org.apache.coyote.http11;

public record RequestLine(
        HttpMethod method,
        String uri,
        String protocolVersion
) {
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
}
