package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.Http11Response;

public class DefaultController extends AbstractController {

    @Override
    public void service(HttpRequest httpRequest, Http11Response httpResponse) throws RuntimeException {
        final String requestUri = httpRequest.getRequestLine().getRequestURI();
        final String resourcePath = RESOURCE_PATH + requestUri;
        httpResponse.setResource(classLoader.getResource(resourcePath));
        httpResponse.setHttpStatusCode(200);
        httpResponse.setStatusMessage("OK");
    }

    @Override
    void doGet(HttpRequest request, Http11Response response) {
        service(request, response);
    }

    @Override
    void doPost(HttpRequest request, Http11Response response) {
        service(request, response);
    }
}
