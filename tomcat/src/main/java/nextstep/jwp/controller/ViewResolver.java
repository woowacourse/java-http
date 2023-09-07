package nextstep.jwp.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ViewResolver {

    private ViewResolver() {
    }

    public static String findView(final String viewName) throws IOException {
        final String fileName = "static/" + viewName + ".html";
        final URL resource = ViewResolver.class.getClassLoader().getResource(fileName);
        final Path path = new File(resource.getPath()).toPath();
        return new String(Files.readAllBytes(path));
    }
}
