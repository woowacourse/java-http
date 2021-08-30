package nextstep.jwp.infrastructure.http;

import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.HttpStatusCode;
import nextstep.jwp.infrastructure.http.view.ResourceView;
import nextstep.jwp.infrastructure.http.view.View;

public class ViewResolver {

    private static final String NOT_FOUND_FILE_NAME = "/404.html";

    private final ClassLoader classLoader;
    private final String defaultPath;
    private final HttpResponse notFoundResponse;

    public ViewResolver(final String defaultPath) {
        this.classLoader = getClass().getClassLoader();
        this.defaultPath = defaultPath;
        this.notFoundResponse = notFoundResponse(classLoader, defaultPath);
    }

    private static HttpResponse notFoundResponse(final ClassLoader classLoader, final String defaultPath) {
        final ResourceView notFoundView = new ResourceView(HttpStatusCode.NOT_FOUND, NOT_FOUND_FILE_NAME);

        return notFoundView.httpResponse(defaultPath)
            .orElseThrow(() -> new IllegalStateException(String.format("Cannot find notFoundResource(%s)", NOT_FOUND_FILE_NAME)));
    }

    public HttpResponse resolve(final View view) {
        return view.httpResponse(defaultPath)
            .orElse(notFoundResponse);
    }
}
