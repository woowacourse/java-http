package nextstep.jwp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import nextstep.jwp.controller.rest.ResponseEntity;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceController implements Controller {

    private static final int MAX_DEPTH = 3;
    private static final String PATH_DELIMITER = "/";
    private static final String RESOURCE_ROOT_PATH = "static";

    public static final String HOME_PAGE = "/index.html";
    public static final String UNAUTHORIZED_PAGE = "/401.html";
    public static final String REGISTER_PAGE = "register.html";

    private final List<String> staticRequestPaths = List.of(
            "/login",
            "/register"
    );

    @Override
    public boolean canHandle(HttpRequest request) {
        final String path = request.getPath();
        return path.substring(path.lastIndexOf("/") + 1).contains(".") || (staticRequestPaths.contains(path) && !request.hasQueryStrings() && request.getMethod() == HttpMethod.GET);
    }

    @Override
    public ResponseEntity handle(HttpRequest request) throws IOException {
        return createStaticResponse(request);
    }

    private ResponseEntity createStaticResponse(HttpRequest request) throws IOException {
        var responseBody = "";
        if (request.getMethod() == HttpMethod.GET) {
            responseBody = findFileContentByPath(request.getPath());
        }

        return ResponseEntity.ok(responseBody);
    }

    private String findFileContentByPath(String requestPath) throws IOException {
        final var resourcePath = HttpResponse.class.getClassLoader().getResource(RESOURCE_ROOT_PATH).getPath();
        final var fileName = requestPath.substring(requestPath.lastIndexOf(PATH_DELIMITER) + 1);
        final var filePath = findAbsolutePath(resourcePath, fileName);

        try (final var inputStream = Files.newInputStream(filePath)) {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    private Path findAbsolutePath(String resourcePath, String fileName) throws IOException {
        final var pathStream = Files.find(
                Path.of(resourcePath),
                MAX_DEPTH,
                (path, basicFileAttributes) -> basicFileAttributes.isRegularFile() && path.toFile().getName().contains(fileName)
        );
        try (pathStream) {
            return pathStream.findFirst()
                             .orElseThrow(() -> new NoSuchFileException(fileName + "에 해당 하는 파일을 찾을 수 없습니다."));
        }
    }
}
