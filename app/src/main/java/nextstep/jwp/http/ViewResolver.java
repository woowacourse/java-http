package nextstep.jwp.http;

import static nextstep.jwp.http.HttpResponse.ok;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ViewResolver {
    private static final String VIEW_PREFIX = "static/";
    private final static String VIEW_SUFFIX = ".html";

    public static String resolveView(String viewName) throws IOException {
        final URL resource = ViewResolver.class.getClassLoader().getResource(VIEW_PREFIX + viewName + VIEW_SUFFIX);
        final Path path = new File(resource.getPath()).toPath();
        String html = Files.readString(path);

        return ok("text/html", html);
    }
}
