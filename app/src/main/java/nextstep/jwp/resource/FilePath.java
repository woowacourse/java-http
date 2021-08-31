package nextstep.jwp.resource;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

public class FilePath {

    public static final String DEFAULT_PREFIX = "static/";

    private static final String SLASH = "/";
    private static final int SLASH_SUBSTRING_INDEX = 1;
    private static final String DOT = ".";

    private final String path;
    private final String prefix;
    private final String suffix;

    public FilePath(String path, String suffix) {
        this(path, suffix, DEFAULT_PREFIX);
    }

    public FilePath(String path, String suffix, String prefix) {
        if (path.startsWith(SLASH)) {
            path = path.substring(SLASH_SUBSTRING_INDEX);
        }
        this.prefix = prefix;
        this.path = path;

        if (!"".equals(suffix)) {
            suffix = DOT + suffix;
        }

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
            throw new IllegalArgumentException("해당 file은 존재하지 않습니다.");
        }
    }

    private String relativePath() {
        if (path.startsWith(prefix)) {
            return path + suffix;
        }
        return prefix + path + suffix;
    }
}
