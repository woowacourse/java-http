package nextstep.jwp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import nextstep.jwp.exception.UncheckedServletException;

public class StaticResourceResolver {

    private static final int MAX_DEPTH = 3;
    private static final String PATH_DELIMITER = "/";
    private static final String RESOURCE_ROOT_PATH = "static";

    public static final String HOME_PAGE = "/index.html";
    public static final String LOGIN_PAGE = "/login.html";
    public static final String UNAUTHORIZED_PAGE = "/401.html";
    public static final String REGISTER_PAGE = "/register.html";

    public String findFileContentByPath(String requestPath) throws IOException {
        final var resourcePath = getClass().getClassLoader().getResource(RESOURCE_ROOT_PATH).getPath();
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
