package org.apache.coyote.http11;

public class HomeHttpRequestHandler implements HttpRequestHandler {

    @Override
    public boolean support(final RequestStartLine requestStartLine) {
        return requestStartLine.requestMethod() == RequestMethod.GET && requestStartLine.requestUrl().equals("/");
    }

    @Override
    public String response(final RequestStartLine requestStartLine) {
        String responseBody = "Hello world!";

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
