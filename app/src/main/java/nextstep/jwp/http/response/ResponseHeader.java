package nextstep.jwp.http.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

public class ResponseHeader {

    private final Map<String, String> headers = new LinkedHashMap<>();

    public ResponseHeader() {

    }

    public void setContentType(ContentType contentType) {
        if (Objects.isNull(contentType)) {
            return;
        }
        this.headers.put("Content-Type", contentType.getValue());
    }

    public void setContentLength(int length) {
        if (length == 0) {
            return;
        }
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

    public void setCookie(String jSessionId) {
        this.headers.put("Set-Cookie", "JSESSIONID=" + jSessionId);
    }
}