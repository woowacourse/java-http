package nextstep.jwp.framework.common;

import nextstep.jwp.framework.file.FileExtension;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum MediaType {
    TEXT_HTML_CHARSET_UTF8("text/html;charset=utf-8", FileExtension.HTML),
    TEXT_CSS_CHARSET_UTF8("text/css;charset=utf-8", FileExtension.CSS),
    TEXT_JS_CHARSET_UTF8("text/javascript;charset=utf-8", FileExtension.JS),
    IMAGE_ICO("image/x-icon", FileExtension.ICO);

    private final String value;
    private final FileExtension fileExtension;

    MediaType(String value, FileExtension fileExtension) {
        this.value = value;
        this.fileExtension = fileExtension;
    }

    public static MediaType from(String mediaTypeValue) {
        return Arrays.stream(values())
                .filter(mediaType -> mediaType.value.equals(mediaTypeValue))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("존재하지 않는 값의 미디어 타입입니다.(%s)", mediaTypeValue))
                );
    }

    public static MediaType from(FileExtension fileExtension) {
        return Arrays.stream(values())
                .filter(mediaType -> mediaType.fileExtension.equals(fileExtension))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("존재하지 않는 확장자의 미디어 타입입니다.(%s)", fileExtension))
                );
    }

    public String getValue() {
        return value;
    }
}
