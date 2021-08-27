package nextstep.jwp.infrastructure.http.resourcersolver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import nextstep.jwp.infrastructure.http.HttpHeaders;
import nextstep.jwp.infrastructure.http.HttpHeaders.Builder;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.HttpStatusCode;
import nextstep.jwp.infrastructure.http.response.HttpStatusLine;

public class ResourceResolver {

    private static final String NOT_FOUND_FILE_NAME = "/404.html";

    private final ClassLoader classLoader;
    private final String defaultPath;
    private final Path notFoundResourcePath;

    public ResourceResolver(final String defaultPath) {
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

    public HttpResponse readResource(final HttpRequest request) {
        final String resourceName = request.getRequestLine().getPath();
        final Path path = findPathOrElse(defaultPath + resourceName, notFoundResourcePath);

        try {
            final String responseBody = Files.readString(path);

            return new HttpResponse(
                new HttpStatusLine(HttpStatusCode.OK),
                new Builder()
                    .header("Content-Type", String.join(";", Files.probeContentType(path), "charset=" + Charset.defaultCharset().displayName().toLowerCase(Locale.ROOT)))
                    .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                    .build(),
                responseBody
            );
        } catch (IOException ioException) {
            throw new IllegalStateException(String.format("Cannot read this file(%s).", resourceName));
        }
    }

    private Path findPathOrElse(final String resourcePath, final Path defaultPath) {
        final URL resourceUrl = classLoader.getResource(resourcePath);

        if (Objects.isNull(resourceUrl)) {
            return defaultPath;
        }
        return new File(resourceUrl.getFile()).toPath();
    }
}
