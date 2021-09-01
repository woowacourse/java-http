package nextstep.jwp.model.web;

import java.util.Map;

public class Headers {

    private final Map<String, String> headers;

    public Headers(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getContentLength() {
        if (headers.containsKey("Content-Length")) {
            return Integer.parseInt(headers.get("Content-Length"));
        }
        return 0;
    }

    public String asString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> kV : headers.entrySet()) {
            String key = kV.getKey();
            String value = kV.getValue();
            builder.append(key + ": " + value + " " + "\r\n");
        }
        return builder.toString();
    }
}
