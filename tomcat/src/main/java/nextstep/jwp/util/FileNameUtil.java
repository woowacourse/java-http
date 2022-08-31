package nextstep.jwp.util;

import java.util.Objects;

public class FileNameUtil {

    private static final String EXTENTION_DOT = ".";

    private FileNameUtil() {
    }

    public static String getExtension(final String path) {
        Objects.requireNonNull(path);
        return path.substring(path.lastIndexOf(EXTENTION_DOT) + 1);
    }
}
