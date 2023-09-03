package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeader {
    private final Map<String, String> responseHeader;

    private ResponseHeader(final Map<String, String> responseHeader) {
        this.responseHeader = responseHeader;
    }

    public static ResponseHeader basic(final ResponseBody responseBody) {
        final Map<String, String> result = new LinkedHashMap<>();
        result.put("Content-Type", responseBody.getContentType().getContentType() + ";charset=utf-8");
        result.put("Content-Length", String.valueOf(responseBody.getContent().getBytes().length));
        return new ResponseHeader(result);
    }

    public static ResponseHeader redirect(final ResponseBody responseBody, final String redirectPath) {
        return new ResponseHeader(Map.of("Location", redirectPath));
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : responseHeader.entrySet()) {
            result.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(" ");
        }
        return result.toString();
    }
}
