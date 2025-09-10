package org.apache.coyote.http11;

public class HomeHttpRequestHandler implements HttpRequestHandler {

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.getRequestMethod() == RequestMethod.GET && httpRequest.getRequestUrl().equals("/");
    }

    @Override
    public String response(final HttpRequest httpRequest) {
        String responseBody = "Hello world!";

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
