package org.apache.coyote.http11.response;

import java.util.Map;

public class HttpResponse {
    private final String protocolVersion = "HTTP/1.1";
    private final String statusCode;
    private final String statusMessage;
    private final Map<String, String> headers;
    private final String body;

    private HttpResponse(final String statusCode, final String statusMessage,
                         final Map<String, String> headers, final String body) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse create200Response(final Map<String, String> headers, final String body) {
        return new HttpResponse("200", "OK", headers, body);
    }

    public static HttpResponse create302Response(final Map<String, String> headers, final String body) {
        return new HttpResponse("302", "Found", headers, body);
    }

    public byte[] toBytes() {
        return createResponse().getBytes();
    }

    private String createResponse() {
        if (headers.containsKey("Set-Cookie")) {
            return String.join("\r\n",
                    "HTTP/1.1 " + statusCode + " " + statusMessage + " ",
                    "Content-Type: " + headers.get("contentType") + ";charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " ",
                    "Set-Cookie: " + headers.get("Set-Cookie") + " ",
                    "",
                    body);
        }

        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " " + statusMessage + " ",
                "Content-Type: " + headers.get("contentType") + ";charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    public void setCookie() {
        headers.put("Set-Cookie", JSessionId.create());
    }
}

