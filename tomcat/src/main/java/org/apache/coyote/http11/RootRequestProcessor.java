package org.apache.coyote.http11;

public class RootRequestProcessor implements HttpRequestProcessor{
    @Override
    public HttpResponse process(HttpRequest request) {
        String responseBody = "Hello world!";
        return new HttpResponse(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8\r\nContent-Length: " + responseBody.getBytes().length,
                responseBody);
    }
}
