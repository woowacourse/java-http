package nextstep.jwp.http.response;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ResponseHeader {

    private final Map<String, String> headers = new HashMap<>();

    public void setContentType(ContentType contentType) {
        this.headers.put("Content-Type", contentType.getValue());
    }

    public void setContentLength(int length) {
        this.headers.put("Content-Length", String.valueOf(length));
    }

    public void setLocation(String url) {
        this.headers.put("Location", url);
    }

    @Override
    public String toString() {
        return headersToString();
    }

    private String headersToString() {
        StringJoiner joiner = new StringJoiner(" \r\n", "", " \r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            joiner.add(key + ": " + value);
        }
        return joiner.toString();
    }
}