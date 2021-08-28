package nextstep.jwp.resource;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

public class FilePath {

    public static final String PREFIX = "static/";

    private static final String SLASH = "/";
    private static final int SLASH_SUBSTRING_INDEX = 1;

    private final String filePath;
    private final String prefix;

    public FilePath(String filePath) {
        this(filePath, PREFIX);
    }

    public FilePath(String filePath, String prefix) {
        if (filePath.startsWith(SLASH)) {
            filePath = filePath.substring(SLASH_SUBSTRING_INDEX);
        }

        this.prefix = prefix;
        this.filePath = filePath;
    }

    public Path path() {
        URL resource = getClass()
            .getClassLoader()
            .getResource(relativePath());

        validateResourcePath(resource);

        return new File(resource.getPath()).toPath();
    }

    private void validateResourcePath(URL resource) {
        if (Objects.isNull(resource)) {
            throw new RuntimeException("해당 file은 존재하지 않습니다.");
        }
    }

    private String relativePath() {
        if (filePath.startsWith(prefix)) {
            return filePath;
        }

        return prefix + filePath;
    }
}
