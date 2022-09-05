package org.apache.coyote.response;

import static org.apache.coyote.response.StatusCode.*;

public class HttpResponse {

    private final StatusCode statusCode;
    private final ContentType contentType;
    private final String responseBody;
    private final String location;

    public HttpResponse(final StatusCode statusCode, final ContentType contentType, final String responseBody, final String location) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
        this.location = location;
    }

    public String getResponse() {
        if (statusCode.equals(FOUND)) {
            return String.join("\r\n",
                    "HTTP/1.1 " + statusCode + " ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "Location: " + location + " ",
                    "",
                    responseBody);
        }

        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
