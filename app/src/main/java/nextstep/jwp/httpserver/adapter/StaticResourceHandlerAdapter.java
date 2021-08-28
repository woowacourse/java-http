package nextstep.jwp.httpserver.adapter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import nextstep.jwp.httpserver.controller.StaticResourceController;
import nextstep.jwp.httpserver.domain.View;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;

public class StaticResourceHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof StaticResourceController);
    }

    @Override
    public View handle(HttpRequest httpRequest, Object handler) throws URISyntaxException, IOException {
        final StaticResourceController staticResourceController = (StaticResourceController) handler;
        final HttpResponse httpResponse = staticResourceController.service(httpRequest, new HashMap<>());
        final String requestUri = httpRequest.getRequestUri();
        final List<String> body = readFile(requestUri);
        final String response = getResponse(requestUri, httpResponse, body);
        return new View(requestUri, response);
    }

    private List<String> readFile(String requestUri) throws URISyntaxException, IOException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource("static" + requestUri);
        final Path path = Paths.get(url.toURI());
        return Files.readAllLines(path);
    }

    private String getResponse(String requestUri, HttpResponse httpResponse, List<String> body) {
        final StringBuilder responseBody = new StringBuilder();
        for (String bodyLine : body) {
            responseBody.append(bodyLine).append("\r\n");
        }

        int index = requestUri.indexOf(".");

        return String.join("\r\n",
                httpResponse.statusLine(),
                "Content-Type: text/" + requestUri.substring(index + 1) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.toString().getBytes().length + " ",
                "",
                responseBody.toString());
    }
}
