package nextstep.jwp.httpserver.adapter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import nextstep.jwp.httpserver.controller.StaticViewController;
import nextstep.jwp.httpserver.domain.View;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;

public class StaticViewHandlerAdapter extends AbstractHandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof StaticViewController);
    }

    @Override
    public View handle(HttpRequest httpRequest, Object handler) throws URISyntaxException, IOException {
        final StaticViewController staticViewController = (StaticViewController) handler;
        final HttpResponse httpResponse = staticViewController.service(httpRequest, new HashMap<>());

        final String requestUri = httpRequest.getRequestUri();
        final String resourcePath = getResourcePath(requestUri);

        final List<String> body = readFile(resourcePath);

        final String response = getResponse(httpResponse, body);
        return new View(resourcePath, response);
    }

    @Override
    protected String getResourcePath(String requestUri) {
        if (requestUri.equals("/")) {
            return "/index";
        }
        final int index = requestUri.indexOf(".html");
        return requestUri.substring(0, index);
    }

    @Override
    protected String getResponse(HttpResponse httpResponse, List<String> body) {
        final StringBuilder responseBody = new StringBuilder();
        for (String bodyLine : body) {
            responseBody.append(bodyLine).append("\r\n");
        }

        return String.join("\r\n",
                httpResponse.statusLine(),
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.toString().getBytes().length + " ",
                "",
                responseBody.toString());
    }
}
