package nextstep.jwp.framework.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractController implements Controller {

    protected String readFile(String url) {
        String resourcePath = "static" + url;
        URL resource = Thread.currentThread().getContextClassLoader()
            .getResource(resourcePath);
        try {
            Path path = Paths.get(resource.toURI());
            return String.join("\r\n", Files.readAllLines(path));
        } catch (IOException | URISyntaxException exception) {
            exception.printStackTrace();
            throw new IllegalStateException("404");
        }
    }
}
