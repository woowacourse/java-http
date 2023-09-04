package org.apache.coyote.http11.response;

import org.apache.coyote.http11.StatusCode;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final StatusCode statusCode;
    private final Map<String, String> header;
    private final String responseBody;

    public HttpResponse(final StatusCode statusCode,
                        final ContentType contentType,
                        final String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;

        header = new HashMap<>();
        header.put("Content-Type: ", contentType.getContentType());
        header.put("Content-Length: ", String.valueOf(responseBody.getBytes().length));
    }

    public String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + header.get("Content-Type: ") + ";charset=utf-8 ",
                "Content-Length: " + header.get("Content-Length: ") + " ",
                "",
                responseBody);
    }

    public String getRedirectResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "HTTP/1.1 " + statusCode + "\r",
                "Location: " + header.get("Location: ") + "\r",
                "Content-Type: " + header.get("Content-Type: ") + "\r",
                "Content-Length: " + header.get("Content-Length: ") + "\r\n",
                responseBody);
    }
}
