package org.apache.coyote.http11.model;

import java.util.Arrays;
import org.apache.coyote.http11.utils.Files;

public enum Path {

    LOGIN("/login", View.LOGIN),
    REGISTER("/register", View.REGISTER);

    private final String path;
    private final View fileView;

    Path(final String path, final View fileView) {
        this.path = path;
        this.fileView = fileView;
    }

    public static String from(final String path) {
        if (Files.existsFile(path)) {
            return path;
        }

        return Arrays.stream(Path.values())
                .filter(it -> it.path.equals(path))
                .findFirst()
                .map(it -> it.fileView.getPath())
                .orElseThrow(() -> new IllegalArgumentException("path가 잘못되었습니다. [path : " + path + "]"));
    }
}
