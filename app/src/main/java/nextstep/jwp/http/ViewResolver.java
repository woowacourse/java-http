package nextstep.jwp.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import nextstep.jwp.exception.NotFoundException;

public class ViewResolver {
    private static final String VIEW_PREFIX = "static/";
    private static final String VIEW_SUFFIX = ".html";

    private ViewResolver() {
    }

    public static String resolveView(String viewName) throws IOException {
        final URL resource = ViewResolver.class.getClassLoader().getResource(VIEW_PREFIX + viewName + VIEW_SUFFIX);

        if (Objects.isNull(resource)) {
            throw new NotFoundException("해당 View는 존재하지 않습니다.");
        }

        final Path path = new File(resource.getPath()).toPath();
        String html = Files.readString(path);

        return HttpResponse.ok("text/html", html);
    }
}
