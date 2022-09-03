package org.apache.coyote.http;

import java.util.Map;

public class Header {

    private Map<String, String> values;

    public Header(Map<String, String> header) {
        this.values = header;
    }

    public Map<String, String> values() {
        return values;
    }

    public String getContentType(){
        return values.getOrDefault("Content-Type", "");
    }
}
