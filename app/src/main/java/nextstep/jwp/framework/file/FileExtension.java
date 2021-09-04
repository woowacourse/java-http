package nextstep.jwp.framework.file;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum FileExtension {
    HTML("html"),
    CSS("css"),
    JS("js"),
    ICO("ico");

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
        if (notExistsDot(path)) {
            throw new IllegalArgumentException(String.format("파일 이름에 점이 없어 확장자를 분리할 수 없습니다 (%s)", path));
        }
        int dotPos = path.lastIndexOf(DOT);
        return path.substring(dotPos + 1);
    }

    private static boolean notExistsDot(String path) {
        int dotPos = path.lastIndexOf(path);
        return dotPos == -1;
    }

    public static boolean supports(String path) {
        if (notExistsDot(path)) {
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
