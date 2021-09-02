package nextstep.jwp.infrastructure.http;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;

public class FileResolver {

    private static final String EXCEPTION_MESSAGE_FORMAT = "Cannot read File.(%s)";
    private final String defaultPath;
    private final ClassLoader classLoader = getClass().getClassLoader();

    public FileResolver(final String defaultPath) {
        this.defaultPath = defaultPath;
    }

    public String read(final String filePath) {
        return Optional.ofNullable(classLoader.getResource(defaultPath + filePath))
            .map(url -> new File(url.getFile()).toPath())
            .map(path -> {
                try {
                    return Files.readString(path);
                } catch (IOException ioException) {
                    throw getIllegalArgumentException(filePath);
                }
            })
            .orElseThrow(() -> getIllegalArgumentException(filePath));
    }

    public String contentType(final String filePath) {
        return Optional.ofNullable(classLoader.getResource(defaultPath + filePath))
            .map(url -> new File(url.getFile()).toPath())
            .map(path -> {
                try {
                    return Files.probeContentType(path);
                } catch (IOException ioException) {
                    throw getIllegalArgumentException(filePath);
                }
            })
            .orElseThrow(() -> getIllegalArgumentException(filePath));
    }

    public boolean hasFile(final String filePath) {
        return Objects.nonNull(classLoader.getResource(defaultPath + filePath));
    }

    private IllegalArgumentException getIllegalArgumentException(final String filePath) {
        return new IllegalArgumentException(String.format(EXCEPTION_MESSAGE_FORMAT, filePath));
    }
}
