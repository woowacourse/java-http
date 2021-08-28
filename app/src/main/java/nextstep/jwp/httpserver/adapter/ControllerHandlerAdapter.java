package nextstep.jwp.httpserver.adapter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import nextstep.jwp.httpserver.controller.Controller;
import nextstep.jwp.httpserver.controller.StaticResourceController;
import nextstep.jwp.httpserver.domain.StatusCode;
import nextstep.jwp.httpserver.domain.View;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;
import nextstep.jwp.httpserver.exception.GlobalException;

public class ControllerHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof Controller) && !(handler instanceof StaticResourceController);
    }

    @Override
    public View handle(HttpRequest httpRequest, Object handler) throws URISyntaxException, IOException {
        final Controller controller = (Controller) handler;

        final String requestUri = httpRequest.getRequestUri();
        final String path = getResourcePath(requestUri);

        try {
            final HttpResponse httpResponse = controller.service(httpRequest, httpRequest.getBodyToMap());
            final List<String> body = readFile(path);
            final String response = getResponse(httpResponse, body);
            return new View(path, response);
        } catch (GlobalException e) {
            final StatusCode exceptionCode = e.getStatusCode();
            return exceptionResponse(exceptionCode);
        }
    }

    private String getResourcePath(String requestUri) {
        if (requestUri.contains("?")) {
            final int index = requestUri.indexOf("?");
            return requestUri.substring(0, index);
        }
        return requestUri;
    }

    private List<String> readFile(String resourcePath) throws URISyntaxException, IOException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource("static" + resourcePath + ".html");
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
                "Location: " + httpResponse.getLocation() + " ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.toString().getBytes().length + " ",
                "",
                responseBody.toString());
    }

    private View exceptionResponse(StatusCode code) throws URISyntaxException, IOException {
        final List<String> body = readFile("/" + code.getCode());

        final StringBuilder responseBody = new StringBuilder();
        for (String bodyLine : body) {
            responseBody.append(bodyLine).append("\r\n");
        }

        final StatusCode redirectCode = StatusCode.FOUND;

        final String response = String.join("\r\n",
                "HTTP/1.1 " + redirectCode.getCode() + " " + redirectCode.getStatusText() + " ",
                "Location: /" + code.getCode() + ".html",
                responseBody.toString());

        return new View("/" + code.getCode(), response);
    }
}
