package nextstep.jwp.util;

import java.util.Objects;

public class FileUtil {

    private static final String EXTENTION_DOT = ".";

    private FileUtil() {
    }

    public static String getExtension(final String path) {
        Objects.requireNonNull(path);
        if (!path.contains(EXTENTION_DOT)) {
            throw new IllegalArgumentException("path가 파일형식과 맞지 않아 확장자를 반환할 수 없습니다.");
        }
        return path.substring(path.lastIndexOf(EXTENTION_DOT) + 1);
    }
}
