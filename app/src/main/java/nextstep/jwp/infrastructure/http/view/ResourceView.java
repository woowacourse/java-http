package nextstep.jwp.infrastructure.http.view;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import nextstep.jwp.infrastructure.http.HttpHeaders;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.HttpStatusCode;
import nextstep.jwp.infrastructure.http.response.HttpStatusLine;

public class ResourceView implements View {

    private final ClassLoader classLoader = getClass().getClassLoader();
    private final HttpStatusCode statusCode;
    private final String resourceName;

    public ResourceView(final HttpStatusCode statusCode, final String resourceName) {
        this.statusCode = statusCode;
        this.resourceName = resourceName;
    }

    public ResourceView(final String resourceName) {
        this(HttpStatusCode.OK, resourceName);
    }

    @Override
    public Optional<HttpResponse> httpResponse(final String defaultPath) {
        return Optional.ofNullable(classLoader.getResource(defaultPath + resourceName))
            .map(url -> new File(url.getFile()).toPath())
            .map(this::readFile);
    }

    private HttpResponse readFile(final Path path) {
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
            throw new IllegalArgumentException(String.format("Cannot read this file(%s).", path));
        }
    }
}

