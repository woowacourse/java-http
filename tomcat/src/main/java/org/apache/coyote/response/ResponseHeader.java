package org.apache.coyote.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.Protocol;

public class ResponseHeader {

    private static final String LINE_SPLIT_DELIMITER = "\r\n";

    private Protocol protocol;
    private HttpStatus httpStatus;
    private Map<String, String> headers;
    private HttpCookie httpCookie;

    public ResponseHeader() {
        this.protocol = Protocol.HTTP1_1;
        this.httpStatus = null;
        this.headers = new HashMap<>();
        this.httpCookie = HttpCookie.from("");
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void addHeaders(String key, String value) {
        this.headers.put(key, value);
    }

    public void addCookie(String key, String value) {
        httpCookie.addCookie(key, value);
    }

    public String getHeader() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(protocol.getValue() + " " + httpStatus.getStatusCode() + " " + httpStatus.getStatus()
                + LINE_SPLIT_DELIMITER);
        for (Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            stringBuilder.append(key + ": " + value + LINE_SPLIT_DELIMITER);
        }
        addCookieHeader(stringBuilder);
        return stringBuilder.toString();
    }

    private void addCookieHeader(StringBuilder stringBuilder) {
        if (httpCookie.isEmpty()) {
            return;
        }
        stringBuilder.append("Set-Cookie: " + httpCookie.getCookies());
    }

}
