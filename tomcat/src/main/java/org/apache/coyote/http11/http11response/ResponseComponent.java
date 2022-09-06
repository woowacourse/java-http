package org.apache.coyote.http11.http11response;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import org.apache.coyote.http11.HeaderElement;
import org.apache.coyote.http11.StatusCode;

public class ResponseComponent {

    private static final String LINE = "\r\n";
    private static final String HEADER_NAME_VALUE_DELIMITER = ": ";
    private static final String LINE_POSTFIX = " ";

    private StatusCode statusCode;
    private Map<HeaderElement, String> headerElements;
    private String body;

    public ResponseComponent(StatusCode statusCode, String body) {
        this.statusCode = statusCode;
        headerElements = new HashMap<>();
        this.body = body;
    }

    public ResponseComponent(StatusCode statusCode) {
        this(statusCode, null);
    }

    public void setContentType(String value) {
        headerElements.put(HeaderElement.CONTENT_TYPE, value);
    }

    public void setContentLength(String value) {
        headerElements.put(HeaderElement.CONTENT_LENGTH, value);
    }

    public void setLocation(String value) {
        headerElements.put(HeaderElement.LOCATION, value);
    }

    public void setCookie(String value) {
        headerElements.put(HeaderElement.SET_COOKIE, value);
    }

    public String toString() {
        return String.join(LINE,
                "HTTP/1.1 " + statusCode.getCode() + LINE_POSTFIX,
                makeHead(),
                "",
                body
        );
    }

    private String makeHead() {
        StringJoiner stringJoiner = new StringJoiner(LINE);
        for (Entry<HeaderElement, String> entry : headerElements.entrySet()) {
            stringJoiner.add(entry.getKey().getValue() + HEADER_NAME_VALUE_DELIMITER + entry.getValue() + LINE_POSTFIX);
        }
        return stringJoiner.toString();
    }
}
