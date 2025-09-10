package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.session.HttpCookie;

public class HttpResponse {

    private int statusCode = 200;
    private String reason = "OK";
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];
    private boolean committed = false;
    private final List<ResponseCookie> cookies = new ArrayList<>();

    public static byte[] bytes(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public void setStatusCode(
            HttpStatus httpStatus
    ) {
        this.statusCode = httpStatus.getStatusCode();
        this.reason = httpStatus.getReasonPhrase();
    }

    public void addCookie(String name, String value) {
        cookies.add(new ResponseCookie(name, value));
        updateSetCookieHeaders();
    }

    public void addCookie(ResponseCookie cookie) {
        cookies.add(cookie);
        updateSetCookieHeaders();
    }

    private void updateSetCookieHeaders() {
        headers.entrySet().removeIf(entry ->
                entry.getKey().equalsIgnoreCase("Set-Cookie"));
        for (int i = 0; i < cookies.size(); i++) {
            String headerName = i == 0 ? "Set-Cookie" : "Set-Cookie-" + i;
            headers.put(headerName, cookies.get(i).toSetCookieHeader());
        }
    }

    public void setHeader(
            String name,
            String value
    ) {
        headers.put(name, value);
    }

    public void setBody(byte[] body) {
        this.body = body != null ? body : new byte[0];
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReason() {
        return reason;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public boolean isCommitted() {
        return committed;
    }

    public void markCommitted() {
        committed = true;
    }
}

