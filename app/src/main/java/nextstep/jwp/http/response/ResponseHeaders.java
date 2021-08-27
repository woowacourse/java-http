package nextstep.jwp.http.response;

import java.util.HashMap;
import java.util.Map;

public class ResponseHeaders {
    private Map<String, String> headers = new HashMap<>();

    public void setContentType(ContentType contentType) {
        headers.put("Content-Type", contentType.getValue());
    }

    public void setContentLength(int length) {
        headers.put("Content-Length", length + " ");
    }

    @Override
    public String toString() {
        return concatHeaders();
    }

    private String concatHeaders() {
        StringBuilder builder = new StringBuilder();
        for (String key : headers.keySet()) {
            String value = headers.get(key);
            builder.append(key)
                    .append(": ")
                    .append(value)
                    .append(" \r\n");
        }
        return builder.toString();
    }
}
