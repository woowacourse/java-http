package nextstep.jwp.httpserver.adapter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import nextstep.jwp.dashboard.controller.LoginController;
import nextstep.jwp.httpserver.domain.StatusCode;
import nextstep.jwp.httpserver.domain.View;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;
import nextstep.jwp.httpserver.exception.GlobalException;

public class LoginHandlerAdapter extends AbstractHandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof LoginController);
    }

    @Override
    public View handle(HttpRequest httpRequest, Object handler) throws URISyntaxException, IOException {
        final LoginController loginController = (LoginController) handler;
        final String requestUri = httpRequest.getRequestUri();

        final String path = getResourcePath(requestUri);

        try {
            final HttpResponse httpResponse = loginController.service(httpRequest, httpRequest.getBodyToMap());
            final List<String> body = readFile(path);
            final String response = getResponse(httpResponse, body);
            return new View(path, response);
        } catch (GlobalException e) {
            final StatusCode exceptionCode = e.getStatusCode();
            return exceptionResponse(exceptionCode);
        }
    }

    protected String getResourcePath(String requestUri) {
        if (requestUri.contains("?")) {
            final int index = requestUri.indexOf("?");
            return requestUri.substring(0, index);
        }
        return requestUri;
    }

    @Override
    protected String getResponse(HttpResponse httpResponse, List<String> body) {
        final StringBuilder responseBody = new StringBuilder();
        for (String bodyLine : body) {
            responseBody.append(bodyLine).append("\r\n");
        }

        return String.join("\r\n",
                httpResponse.statusLine(),
                "Location: " + httpResponse.getLocation() + " ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.toString().getBytes().length + " ",
                "",
                responseBody.toString());
    }
}
