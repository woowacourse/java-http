package org.apache.coyote.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http.HeaderName;

public class ResponseHeader {

    private final Map<String, String> header;

    public ResponseHeader() {
        this.header = new HashMap<>();
    }

    public void addHeader(HeaderName headerName, String value) {
        header.put(headerName.getValue(), value);
    }

    public String getResponse() {
        StringBuilder response = new StringBuilder();

        for (Entry<String, String> headerEntry : header.entrySet()) {
            response.append(headerEntry.getKey())
                    .append(": ")
                    .append(headerEntry.getValue())
                    .append("\r\n");
        }
        return String.valueOf(response);
    }
}
