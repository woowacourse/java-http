package org.apache.coyote.http;

import java.util.Map;

public class Header {

    private Map<String, String> headerMap;

    public Header(final Map<String, String> header) {
        this.headerMap = header;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public String getContentType() {
        return headerMap.getOrDefault("Content-Type", "");
    }
}
