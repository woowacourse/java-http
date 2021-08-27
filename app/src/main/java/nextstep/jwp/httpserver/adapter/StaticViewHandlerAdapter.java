package nextstep.jwp.httpserver.adapter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import nextstep.jwp.httpserver.controller.StaticViewController;
import nextstep.jwp.httpserver.domain.View;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;

public class StaticViewHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof StaticViewController);
    }

    @Override
    public View handle(HttpRequest httpRequest, Object handler) throws URISyntaxException, IOException {
        final StaticViewController staticViewController = (StaticViewController) handler;
        final HttpResponse httpResponse = staticViewController.handle();

        final String requestUri = httpRequest.getRequestUri();
        final String resourcePath = getResourcePath(requestUri);

        final List<String> body = readFile(resourcePath);

        final String response = getResponse(httpResponse, body);
        return new View(resourcePath, response);
    }

    private String getResourcePath(String requestUri) {
        String resourcePath = "static" + requestUri;

        if (requestUri.equals("/")) {
            resourcePath = "static/index.html";
        }
        return resourcePath;
    }

    private List<String> readFile(String resourcePath) throws URISyntaxException, IOException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        final Path path = Paths.get(url.toURI());
        return Files.readAllLines(path);
    }

    private String getResponse(HttpResponse httpResponse, List<String> body) {
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
