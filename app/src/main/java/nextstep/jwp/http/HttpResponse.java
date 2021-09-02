package nextstep.jwp.http;

import java.util.HashMap;
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
    private Cookies cookies = new Cookies();

    public HttpResponse() {
        this.status = HttpStatus.OK;
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
        cookies.putCookie(key, value);
    }

    private String getCookieAsString() {
        return cookies.asString();
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

    public String createJSessionId() {
        String jSessionId = UUID.randomUUID().toString();
        cookies.putCookie(HEADER_KEY_OF_JSESSIONID, jSessionId);
        return jSessionId;
    }
}
