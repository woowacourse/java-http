package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Optional;

public class FileReader {

    public String readFileContent(final String fileName) {
        try {
            final Optional<File> file = getStaticFile(fileName);
            return file.map(this::readFileContent)
                    .orElseThrow(() -> new NoSuchFileException("존재하지 않는 파일입니다. - " + fileName));
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("파일을 읽는 도중 문제가 발생하였습니다. - " + fileName);
        }
    }

    private Optional<File> getStaticFile(final String fileName) throws URISyntaxException {
        final URL resource = getClass().getResource("/static" + fileName.trim());
        if (resource == null) {
            return Optional.empty();
        }

        return Optional.of(Paths.get(resource.toURI()).toFile());
    }

    private String readFileContent(final File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new IllegalStateException("파일을 읽는 도중 문제가 발생하였습니다. - " + file.getName());
        }
    }
}
