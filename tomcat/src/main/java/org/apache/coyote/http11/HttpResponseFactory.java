package org.apache.coyote.http11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import nextstep.jwp.exception.UncheckedServletException;

public class HttpResponseFactory {

    private static final String CSS_CONTENT_TYPE = "text/css";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final int MAX_DEPTH = 3;
    private static final String RESOURCE_PATH = "static";
    private static final String PATH_DELIMITER = "/";

    public HttpResponse createHttpResponse(final HttpRequest httpRequest) throws IOException {
        final String body = readFile(httpRequest.getPath());

        if (httpRequest.containsAccept(CSS_CONTENT_TYPE)) {
            return new HttpResponse(CSS_CONTENT_TYPE, body);
        }

        return new HttpResponse(HTML_CONTENT_TYPE, body);
    }

    private String readFile(final String fileName) throws IOException {
        final String resourcePath = getClass().getClassLoader().getResource(RESOURCE_PATH).getPath();
        final Path absolutePath = findAbsolutePath(fileName, resourcePath);
        System.out.println("absolutePath = " + absolutePath);

        try (final var inputStream = Files.newInputStream(absolutePath)) {
            final byte[] indexContentBytes = inputStream.readAllBytes();
            return new String(indexContentBytes);
        } catch (final IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    private Path findAbsolutePath(final String filePath, final String rootPath) throws IOException {
        final String fileName = filePath.substring(filePath.lastIndexOf(PATH_DELIMITER) + 1);
        System.out.println(filePath + " " + fileName);
        return Files.find(
                Path.of(rootPath),
                MAX_DEPTH,
                (path, fileAttr) -> fileAttr.isRegularFile() && path.endsWith(fileName)
            ).findFirst()
            .orElseThrow(() -> new NoSuchElementException("파일을 찾을 수 없습니다."));
    }
}
