package org.apache.coyote.http11.request;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.StringJoiner;

class TestMessage {

    static HttpMessage generateMessage() {
        return generateMessage("GET", "/path", "HTTP/1.1");
    }

    static HttpMessage generateMessage(final String method, final String uri) {
        return generate(method, uri, "HTTP/1.1", "");
    }

    static HttpMessage generateMessage(final String method, final String uri, final String version) {
        return generate(method, uri, version, "");
    }

    static HttpMessage generateMessageQueryString(final String query) {
        return generateMessage("GET", "/path?" + query);
    }

    static HttpMessage generateMessageWithMessageBody(final String messageBody) {
        return generate("GET", "/path", "HTTP/1.1", messageBody);
    }

    static HttpMessage generate(final String method, final String uri, final String version, final String messageBody) {
        final StringJoiner joiner = new StringJoiner(" ", "", " ");
        String requestLine = joiner.add(method)
            .add(uri)
            .add(version).toString();
        final String httpRequest = String.join("\r\n",
            requestLine,
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + messageBody.length(),
            "Content-Type: application/x-www-form-urlencoded",
            "",
            messageBody);
        final InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());

        return new HttpMessage(inputStream);
    }
}
