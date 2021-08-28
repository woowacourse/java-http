package nextstep.jwp.resource;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

public class FilePath {

    public static final String PREFIX = "static/";

    private static final String SLASH = "/";
    private static final int SLASH_SUBSTRING_INDEX = 1;
    private static final String DOT = ".";

    private final String filePath;
    private final String prefix;
    private final String suffix;

    public FilePath(String filePath, String suffix) {
        this(filePath, suffix, PREFIX);
    }

    public FilePath(String filePath, String suffix, String prefix) {
        if (filePath.startsWith(SLASH)) {
            filePath = filePath.substring(SLASH_SUBSTRING_INDEX);
        }
        this.prefix = prefix;
        this.filePath = filePath;
        this.suffix = suffix;
    }

    public Path path() {
        URL resource = getClass()
            .getClassLoader()
            .getResource(relativePath());

        validateResourcePath(resource);

        return new File(resource.getPath()).toPath();
    }

    public boolean isExist() {
        URL resource = getClass()
            .getClassLoader()
            .getResource(relativePath());

        return !Objects.isNull(resource);
    }

    private void validateResourcePath(URL resource) {
        if (Objects.isNull(resource)) {
            throw new RuntimeException("해당 file은 존재하지 않습니다.");
        }
    }

    private String relativePath() {
        if (filePath.startsWith(prefix)) {
            return filePath + DOT + suffix;
        }
        return prefix + filePath + DOT + suffix;
    }
}
