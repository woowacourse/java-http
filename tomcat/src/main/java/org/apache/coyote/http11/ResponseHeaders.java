package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeaders {

    private final Map<String, String> headers;

    private ResponseHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders from(final ResponseBody responseBody) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", responseBody.getHttpExtensionType().getContentType());
        headers.put("Content-Length", String.valueOf(responseBody.getLength()));
        return new ResponseHeaders(headers);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        headers.forEach((key, value) -> stringBuilder.append(key).append(": ").append(value).append(" \r\n"));
        return stringBuilder.toString();
    }
}
