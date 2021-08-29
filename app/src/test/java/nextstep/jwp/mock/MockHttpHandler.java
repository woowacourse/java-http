package nextstep.jwp.mock;

import nextstep.jwp.dispatcher.handler.HttpHandler;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.message.ContentType;
import nextstep.jwp.http.message.HttpHeaders;
import nextstep.jwp.http.message.HttpStatus;

public class MockHttpHandler extends HttpHandler {

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        super.doGet(httpRequest, httpResponse);
        String content = "hi~ binghe!";

        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setVersionOfProtocol("HTTP/1.1");
        httpResponse.setContent(content);

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type",
            ContentType.valueByFileExtension("html").getDescription());
        headers.addHeader("Content-Length",
            String.valueOf(httpResponse.getContentAsString().length()));
        httpResponse.setHeaders(headers);
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        super.doPost(httpRequest, httpResponse);

        String body = httpRequest.getBody();

        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setVersionOfProtocol("HTTP/1.1");
        httpResponse.setContent(body);

        HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type",
            ContentType.valueByFileExtension("html").getDescription());
        headers.addHeader("Content-Length",
            String.valueOf(httpResponse.getContentAsString().length()));
        httpResponse.setHeaders(headers);
    }
}
