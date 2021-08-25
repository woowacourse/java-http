package nextstep.jwp.framework.infrastructure.resolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import nextstep.jwp.framework.infrastructure.util.StaticFileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticFileResolver {

    private static final StaticFileResolver INSTANCE = new StaticFileResolver();
    private static final Logger log = LoggerFactory.getLogger(StaticFileResolver.class);
    private static final List<Path> STATIC_FILE_PATHS =
        StaticFileLoader.loadStaticFilePaths("static");
    private static final String RESPONSE_FORMAT =
        String.join("\r\n",
            "HTTP/1.1 %s %s ",
            "Content-Type: text/%s;charset=utf-8 ",
            "Content-Length: %d ", "", "%s"
        );

    private StaticFileResolver() {
    }

    public static StaticFileResolver getInstance() {
        return INSTANCE;
    }

    public HttpResponse render(HttpRequest httpRequest, HttpStatus httpStatus) {
        String staticFileUrl = "/static" + httpRequest.getUrl();
        return new HttpResponse(writeResponse(staticFileUrl, httpStatus));
    }

    public HttpResponse renderDefaultViewByStatus(HttpStatus httpStatus) {
        String defaultViewFileUrl = "/static/" + httpStatus.getCode() + ".html";
        return new HttpResponse(writeResponse(defaultViewFileUrl, httpStatus));
    }

    private String writeResponse(String url, HttpStatus httpStatus) {
        Path path = findResourcePath(url);
        httpStatus = adjustHttpStatus(path, httpStatus);
        try {
            String responseBody = String.join("\r\n", Files.readAllLines(path));
            return String.format(
                RESPONSE_FORMAT,
                httpStatus.getCode(),
                httpStatus.getMessage(),
                extractExtension(path),
                responseBody.getBytes().length,
                responseBody
            );
        } catch (IOException exception) {
            log.error("Exception IO", exception);
            throw new IllegalStateException(exception);
        }
    }

    private Path findResourcePath(String staticUrl) {
        return searchFileEndsWithSuffix(staticUrl)
            .findAny()
            .orElseGet(this::find404Path);
    }

    private Stream<Path> searchFileEndsWithSuffix(String staticUrl) {
        return STATIC_FILE_PATHS.stream()
            .filter(filePath -> filePath.toString().endsWith(staticUrl));
    }

    private Path find404Path() {
        return searchFileEndsWithSuffix("/static/404.html")
            .findAny()
            .orElseThrow(() -> new IllegalStateException("404 Page를 찾을 수 없습니다."));
    }

    private HttpStatus adjustHttpStatus(Path path, HttpStatus httpStatus) {
        if (path.toString().equals("/static/404.html")) {
            return HttpStatus.NOT_FOUND;
        }
        return httpStatus;
    }

    private String extractExtension(Path path) {
        String resourcePath = path.toString();
        int lastIndex = resourcePath.lastIndexOf(".");
        String extension = resourcePath.substring(lastIndex + 1);
        if (extension.equals("js")) {
            extension = "javascript";
        }
        return extension;
    }
}
