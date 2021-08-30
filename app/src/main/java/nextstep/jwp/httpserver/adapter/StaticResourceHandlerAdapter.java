package nextstep.jwp.httpserver.adapter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import nextstep.jwp.httpserver.controller.StaticResourceController;
import nextstep.jwp.httpserver.domain.StatusCode;
import nextstep.jwp.httpserver.domain.View;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;

public class StaticResourceHandlerAdapter extends AbstractHandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof StaticResourceController);
    }

    @Override
    public View handle(HttpRequest httpRequest, Object handler) throws URISyntaxException, IOException {
        final StaticResourceController staticResourceController = (StaticResourceController) handler;

        final String requestUri = httpRequest.getRequestUri();
        final String path = getResourcePath(requestUri);

        HttpResponse httpResponse = new HttpResponse();
        try {
            httpResponse = staticResourceController.service(httpRequest, new HashMap<>());
            final List<String> body = readFile(path);
            final String response = getResponse(path, httpResponse, body);
            return new View(path, response);
        } catch (RuntimeException e) {
            return exceptionResponse(httpResponse, StatusCode.NOT_FOUND);
        }
    }

    private String getResourcePath(String requestUri) {
        if (requestUri.equals("/")) {
            return "/index.html";
        }
        return requestUri;
    }

    private String getResponse(String requestUri, HttpResponse httpResponse, List<String> body) {
        final StringBuilder responseBody = new StringBuilder();
        for (String bodyLine : body) {
            responseBody.append(bodyLine).append("\r\n");
        }

        int index = requestUri.indexOf(".");
        httpResponse.addHeader("Content-Type", "text/" + requestUri.substring(index + 1) + ";charset=utf-8");
        httpResponse.addHeader("Content-Length", Integer.toString(responseBody.toString().getBytes().length));

        return httpResponse.responseToString(responseBody.toString());
    }
}
