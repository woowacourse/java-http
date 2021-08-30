package nextstep.jwp.httpserver.adapter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import nextstep.jwp.httpserver.controller.Controller;
import nextstep.jwp.httpserver.controller.StaticResourceController;
import nextstep.jwp.httpserver.domain.StatusCode;
import nextstep.jwp.httpserver.domain.View;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;
import nextstep.jwp.httpserver.exception.GlobalException;

public class ControllerHandlerAdapter extends AbstractHandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof Controller) && !(handler instanceof StaticResourceController);
    }

    @Override
    public View handle(HttpRequest httpRequest, Object handler) throws URISyntaxException, IOException {
        final Controller controller = (Controller) handler;

        final String requestUri = httpRequest.getRequestUri();
        final String path = getResourcePath(requestUri);

        HttpResponse httpResponse = new HttpResponse();
        try {
            httpResponse = controller.service(httpRequest, httpRequest.getBodyToMap());
            final List<String> body = readFile(path + ".html");
            final String response = getResponse(httpResponse, body);
            return new View(path, response);
        } catch (GlobalException e) {
            final StatusCode exceptionCode = e.getStatusCode();
            return exceptionResponse(httpResponse, exceptionCode);
        }
    }

    private String getResourcePath(String requestUri) {
        if (requestUri.contains("?")) {
            final int index = requestUri.indexOf("?");
            return requestUri.substring(0, index);
        }
        return requestUri;
    }

    private String getResponse(HttpResponse httpResponse, List<String> body) {
        final StringBuilder bodyBuilder = new StringBuilder();
        for (String bodyLine : body) {
            bodyBuilder.append(bodyLine).append("\r\n");
        }
        String responseBody = bodyBuilder.toString();

        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", Integer.toString(responseBody.getBytes().length));

        return httpResponse.responseToString(responseBody);
    }
}
