package nextstep.jwp.http.response;

import nextstep.jwp.http.response.type.ContentType;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import static nextstep.jwp.http.session.HttpSession.SESSION_NAME;

public class ResponseHeaders {

    private static final String DELIMITER = ": ";
    private static final String ENTER_LINE = " \r\n";

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

    public byte[] getByte() {
        StringJoiner joiner = new StringJoiner(ENTER_LINE, "", ENTER_LINE);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = headers.get(key);
            joiner.add(key + DELIMITER + value);
        }
        return joiner.toString().getBytes(StandardCharsets.UTF_8);
    }

    public void setCookie(String cookie) {
        this.headers.put("Set-Cookie", SESSION_NAME + "=" + cookie);
    }
}
