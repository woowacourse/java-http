package nextstep.jwp.http;

import java.net.URL;
import java.util.Objects;
import nextstep.jwp.exception.NotFoundException;

public class ViewResolver {
    private static final String VIEW_PREFIX = "static/";
    private static final String VIEW_SUFFIX = ".html";

    private ViewResolver() {
    }

    public static View resolveView(String viewName) {
        String url = VIEW_PREFIX + viewName + VIEW_SUFFIX;

        final URL resource = ViewResolver.class.getClassLoader().getResource(url);

        if (Objects.isNull(resource)) {
            throw new NotFoundException("해당 View는 존재하지 않습니다.");
        }

        return new View(url);
    }
}
