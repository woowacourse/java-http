package kokodak.handler;

import kokodak.http.HttpRequest;
import kokodak.http.HttpResponse;

public class BasicHandler implements Handler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String responseBody = "Hello world!";
        return HttpResponse.builder()
                           .header("Content-Type", "text/html;charset=utf-8")
                           .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                           .body(responseBody)
                           .build();
    }
}
