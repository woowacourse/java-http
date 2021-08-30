package nextstep.jwp.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HttpResponse {

    private static final String HTTP_LINE_SEPERATOR = "\r\n";
    private static final int COOKIE_VALUE_INDEX = 1;
    private static final int COOKIE_KEY_INDEX = 0;
    private static final String HEADER_KEY_OF_JSESSIONID = "JSESSIONID";
    private static final String HEADER_KEY_OF_SET_COOKIE = "Set-Cookie";
    private HttpStatus status;
    private String body;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();

    public HttpResponse() {
        this.status = HttpStatus.OK;
    }

    public HttpResponse(HttpStatus status) {
        this(status, "");
    }

    public HttpResponse(HttpStatus status, String body) {
        this.status = status;
        this.body = body;
    }

    public void putHeader(String key, String value) {
        if (key.equals(HEADER_KEY_OF_SET_COOKIE)) {
            String[] split = value.split("=");
            putCookie(split[COOKIE_KEY_INDEX], split[COOKIE_VALUE_INDEX]);
            return;
        }
        headers.put(key, value);
    }

    public void putCookie(String key, String value) {
        cookies.put(key, value);
    }

    private String getCookieAsString() {
        List<String> cookiesAsString = new ArrayList<>();
        cookies.forEach((key, value) -> {
            cookiesAsString.add(key + "=" + value);
        });
        return String.join("; ", cookiesAsString);
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return String.join(HTTP_LINE_SEPERATOR,
            makeStartLine(),
            makeHeaderLines(),
            body);
    }

    private String makeStartLine() {
        return "HTTP/1.1 " + status.getCode() + " " + status.getMessage();
    }

    private String makeHeaderLines() {
        StringBuilder sb = new StringBuilder();
        headers.put(HEADER_KEY_OF_SET_COOKIE, getCookieAsString());
        headers.forEach((key, value) -> {
            sb.append(key);
            sb.append(": ");
            sb.append(value);
            sb.append(HTTP_LINE_SEPERATOR);
        });
        return sb.toString();
    }

    public void createJSessionId() {
        cookies.put(HEADER_KEY_OF_JSESSIONID, UUID.randomUUID().toString());
    }
}
