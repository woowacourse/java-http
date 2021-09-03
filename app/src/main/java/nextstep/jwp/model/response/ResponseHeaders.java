package nextstep.jwp.model.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeaders {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String LOCATION = "Location";

    private final Map<String, String> values = new LinkedHashMap<>();

    public void putContentType(String contentType) {
        values.put(CONTENT_TYPE, contentType);
    }

    public void putContentLength(int contentLength) {
        values.put(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void putLocation(String location) {
        values.put(LOCATION, location);
    }

    public void putSetCookie(String id) {
        values.put("Set-Cookie", "JSESSIONID=" + id);
    }

    public String toMessage() {
        if (values.isEmpty()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            stringBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append(" \r\n");
        }
        return stringBuilder.toString();
    }
}
