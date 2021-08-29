package nextstep.jwp.http.response;

import nextstep.jwp.http.response.type.ContentType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ResponseHeaders {

    private final Map<String, String> headers = new LinkedHashMap<>();

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
        StringJoiner joiner = new StringJoiner(" \r\n", "", " \r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = headers.get(key);
            joiner.add(key + ": " + value);
        }
        return joiner.toString();
    }
}
