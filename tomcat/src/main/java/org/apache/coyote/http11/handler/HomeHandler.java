package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.model.HttpRequest;

public class HomeHandler implements Handler {

    public HomeHandler(final HttpRequest httpRequest) {}

    @Override
    public String getResponse() {
        String responseBody = "Hello world!";
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
