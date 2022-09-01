package org.apache.coyote.http11;

public class StringResponseMaker implements ResponseMaker {

    @Override
    public String createResponse(final String requestUrl) {
        final var responseBody = "Hello world!";
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.length() + " ",
                "",
                responseBody);
    }
}
