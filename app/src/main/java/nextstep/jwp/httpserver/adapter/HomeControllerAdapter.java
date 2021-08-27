package nextstep.jwp.httpserver.adapter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import nextstep.jwp.dashboard.HomeController;
import nextstep.jwp.httpserver.domain.View;

public class HomeControllerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof HomeController);
    }

    @Override
    public View handle(Object handler) throws URISyntaxException, IOException {
        final HomeController homeController = (HomeController) handler;

        final String viewName = homeController.handle();
        final String resourcePath = "static/" + viewName + ".html";

        final URL url = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        final Path path = Paths.get(url.toURI());
        final List<String> body = Files.readAllLines(path);

        final StringBuilder responseBody = new StringBuilder();
        for (String bodyLine : body) {
            responseBody.append(bodyLine).append("\r\n");
        }

        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.toString().getBytes().length + " ",
                "",
                responseBody.toString());

        return new View(resourcePath, response);
    }
}
