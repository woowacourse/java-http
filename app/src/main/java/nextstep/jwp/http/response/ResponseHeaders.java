package nextstep.jwp.http.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeaders {
    private Map<String, String> headers = new LinkedHashMap<>();

    public void setContentType(ContentType contentType) {
        headers.put("Content-Type", contentType.getValue());
    }

    public void setContentLength(int length) {
        headers.put("Content-Length", String.valueOf(length));
    }

    public void setLocation(String url) {
        headers.put("Location", url);
    }

    @Override
    public String toString() {
        return concatHeaders();
    }

    private String concatHeaders() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = headers.get(key);
            builder.append(key)
                    .append(": ")
                    .append(value)
                    .append(" \r\n");
        }
        return builder.toString();
    }
}
