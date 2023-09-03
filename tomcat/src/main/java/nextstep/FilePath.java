package nextstep;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum FilePath {

    INDEX_HTML("/index.html");

    private final String requestPath;

    FilePath(final String requestPath) {
        this.requestPath = requestPath;
    }

    public static String getPath(final String path) {
        final List<FilePath> filePaths = Arrays.stream(FilePath.values())
                                               .filter(value -> value.requestPath.equals(path))
                                               .collect(Collectors.toList());
        final FilePath filePath = filePaths.get(0);
        return "static/" + filePath.requestPath;
    }
}
