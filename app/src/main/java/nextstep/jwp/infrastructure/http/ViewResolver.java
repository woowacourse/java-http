package nextstep.jwp.infrastructure.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.HttpStatusCode;
import nextstep.jwp.infrastructure.http.response.HttpStatusLine;

public class ViewResolver {

    private static final String NOT_FOUND_FILE_NAME = "/404.html";

    private final ClassLoader classLoader;
    private final String defaultPath;
    private final Path notFoundResourcePath;

    public ViewResolver(final String defaultPath) {
        this.classLoader = getClass().getClassLoader();
        this.defaultPath = defaultPath;
        this.notFoundResourcePath = findNotFoundResourcePath(classLoader, defaultPath);
    }

    private static Path findNotFoundResourcePath(final ClassLoader classLoader, final String defaultPath) {
        final URL notFoundUrl = classLoader.getResource(defaultPath + NOT_FOUND_FILE_NAME);
        if (Objects.isNull(notFoundUrl) || Objects.isNull(notFoundUrl.getFile())) {
            throw new IllegalStateException(String.format("Cannot find notFoundResource(%s)", NOT_FOUND_FILE_NAME));
        }

        return new File(notFoundUrl.getFile()).toPath();
    }

    public HttpResponse resolve(final View view) {
        if (view.needsResource()) {
            return resolveByResource(view);
        }
        return view.getResponse();
    }

    private HttpResponse resolveByResource(final View view) {
        final Optional<URL> resourceUrl = Optional.ofNullable(classLoader.getResource(defaultPath + view.getResourceName()));

        final Path path = resourceUrl.map(url -> new File(url.getFile()).toPath())
            .orElse(notFoundResourcePath);
        final HttpStatusCode statusCode = resourceUrl.map(url -> view.getStatusCode())
            .orElse(HttpStatusCode.NOT_FOUND);

        try {
            final String responseBody = Files.readString(path);

            return new HttpResponse(
                new HttpStatusLine(statusCode),
                new HttpHeaders.Builder()
                    .header("Content-Type",
                        String.join(";", Files.probeContentType(path), "charset=" + Charset.defaultCharset().displayName().toLowerCase(Locale.ROOT)))
                    .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                    .build(),
                responseBody
            );
        } catch (IOException ioException) {
            throw new IllegalStateException(String.format("Cannot read this file(%s).", view));
        }
    }
}
