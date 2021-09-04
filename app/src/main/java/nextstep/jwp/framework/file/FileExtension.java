package nextstep.jwp.framework.file;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum FileExtension {
    HTML("html"),
    CSS("css"),
    JS("js"),
    ICO("ico"),
    SVG("svg");

    private static final String DOT = ".";

    private final String value;

    FileExtension(String value) {
        this.value = value;
    }

    public static FileExtension from(String extension) {
        return Arrays.stream(values())
                .filter(fileExtension -> fileExtension.value.equals(extension.toLowerCase()))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("지원하는 확장자가 아닙니다.(%s)", extension))
                );
    }

    public static FileExtension extractExtension(String path) {
        return from(parseExtension(path));
    }

    private static String parseExtension(String path) {
        if (isImpossible(path)) {
            throw new IllegalArgumentException(String.format("확장자를 분리할 수 없습니다 (%s)", path));
        }
        int dotPos = path.lastIndexOf(DOT);
        return path.substring(dotPos + 1);
    }

    private static boolean isImpossible(String path) {
        final int nonexistentIndex = -1;
        final int startIndex = 0;
        final int endIndex = path.length() - 1;
        int dotPos = path.lastIndexOf(DOT);

        return dotPos == nonexistentIndex ||
                dotPos == startIndex ||
                dotPos == endIndex;
    }

    public static boolean supports(String path) {
        if (isImpossible(path)) {
            return false;
        }
        String extension = parseExtension(path);
        return Arrays.stream(values())
                .anyMatch(fileExtension -> fileExtension.value.equals(extension.toLowerCase()));
    }

    public boolean match(FileExtension fileExtension) {
        return this == fileExtension;
    }
}
