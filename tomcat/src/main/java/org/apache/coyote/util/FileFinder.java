package org.apache.coyote.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http11.ResponseEntityFactory;

public class FileFinder {

    private static final int MAX_DEPTH = 3;
    private static final String PATH_DELIMITER = "/";
    private static final String RESOURCE_PATH = ResponseEntityFactory.class.getClassLoader().getResource("static")
        .getPath();

    public static String readFile(final String fileName) throws IOException {
        final Path absolutePath = findAbsolutePath(fileName);

        try (final var inputStream = Files.newInputStream(absolutePath)) {
            final byte[] indexContentBytes = inputStream.readAllBytes();
            return new String(indexContentBytes);
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    public static Path findAbsolutePath(final String filePath) throws IOException {
        final String fileName = filePath.substring(filePath.lastIndexOf(PATH_DELIMITER) + 1);

        return Files.find(
                Path.of(RESOURCE_PATH),
                MAX_DEPTH,
                (path, fileAttr) -> fileAttr.isRegularFile() && path.toString().contains(fileName)
            ).findFirst()
            .orElseThrow(() -> new NoSuchElementException("파일을 찾을 수 없습니다."));
    }
}
