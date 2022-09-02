package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.HttpRequest;

public interface Handler {

    String handle(final HttpRequest httpRequest);

    default String createResponseMessage(final ContentType contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
