package nextstep.jwp.model.web;

import nextstep.jwp.model.web.sessions.HttpCookie;
import nextstep.jwp.model.web.sessions.HttpSessions;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Headers {

    private final Map<String, List<String>> headers = new LinkedHashMap<>();
    private final HttpCookie httpCookie = new HttpCookie();

    public Headers() {
    }

    public Headers(Map<String, String> headers) {
        setHeadersAndCookie(headers);
    }

    public void setHeadersAndCookie(Map<String, String> headers) {
        for (Map.Entry<String, String> keyValue : headers.entrySet()) {
            if (keyValue.getKey().equals("Cookie")) {
                setHttpCookie(keyValue.getValue());
                continue;
            }
            this.headers.put(keyValue.getKey(), Arrays.asList(keyValue.getValue().split(", ")));
        }
    }

    public void addHeader(String key, List<String> value) {
        headers.put(key, value);
    }

    private void setHttpCookie(String value) {
        String[] cookies = value.split("; ");
        for (String cookie : cookies) {
            String[] kV = cookie.split("=");
            httpCookie.addCookie(kV[0], kV[1]);
        }
    }

    public int getContentLength() {
        if (headers.containsKey("Content-Length")) {
            return Integer.parseInt(headers.get("Content-Length").get(0));
        }
        return 0;
    }

    public String asString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, List<String>> kV : headers.entrySet()) {
            String key = kV.getKey();
            List<String> value = kV.getValue();
            builder.append(key)
                    .append(": ")
                    .append(String.join(", ", value))
                    .append("\r\n");
        }
        return builder.toString();
    }

    public boolean existsSessionIdCookie() {
        if (httpCookie.containsKey("JSESSIONID")) {
            return HttpSessions.existsSessionId(
                    httpCookie.getSessionValue()
            );
        }
        return false;
    }
}
