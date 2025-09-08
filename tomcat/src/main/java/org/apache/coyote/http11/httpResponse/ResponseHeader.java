package org.apache.coyote.http11.httpResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.httpRequest.HttpRequest;

public class ResponseHeader {

    private final HttpRequest httpRequest;

    public ResponseHeader(
            final HttpRequest httpRequest
    ) {
        this.httpRequest = httpRequest;
    }

    public String getHeader(
            final ResponseContent responseContent
    ) {
        final List<String> headers = new ArrayList<>();
        headers.add("HTTP/1.1 " + responseContent.httpStatus().toString());
        headers.add("Content-Type: " + getContentType() + ";charset=utf-8");
        headers.add("Content-Length: " + responseContent.body().getBytes(StandardCharsets.UTF_8).length);

        if (responseContent.location() != null) {
            headers.add("Location: " + responseContent.location());
        }

        if (responseContent.httpCookie() != null) {
            Map<String, String> cookies = responseContent.httpCookie().getCookies();
            for (String name : cookies.keySet()) {
                headers.add("Set-Cookie: " + name + "=" + cookies.get(name) + ";");
            }
        }

        headers.add("\r\n");
        return String.join(" \r\n", headers);
    }

    private String getContentType() {
        if (httpRequest.getPath().endsWith(".css")) {
            return "text/css";
        }
        if (httpRequest.getPath().equals(".js")) {
            return "text/javascript";
        }
        if (httpRequest.getPath().equals("svg")) {
            return "image/svg+xml";
        }
        return "text/html";
    }
}
