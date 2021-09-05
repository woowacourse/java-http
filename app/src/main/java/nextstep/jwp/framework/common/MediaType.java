package nextstep.jwp.framework.common;

import nextstep.jwp.framework.file.FileExtension;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum MediaType {
    TEXT_HTML_CHARSET_UTF8("text/html;charset=utf-8", FileExtension.HTML),
    TEXT_CSS_CHARSET_UTF8("text/css;charset=utf-8", FileExtension.CSS),
    TEXT_JS_CHARSET_UTF8("text/javascript;charset=utf-8", FileExtension.JS),
    IMAGE_ICO("image/x-icon", FileExtension.ICO),
    IMAGE_SVG("image/svg+xml", FileExtension.SVG);

    private static final Map<String, MediaType> valueMappings = new HashMap<>();
    private static final Map<FileExtension, MediaType> fileExtensionMappings = new EnumMap<>(FileExtension.class);

    static {
        for (MediaType mediaType : values()) {
            valueMappings.put(mediaType.value, mediaType);
            fileExtensionMappings.put(mediaType.fileExtension, mediaType);
        }
    }

    private final String value;
    private final FileExtension fileExtension;

    MediaType(String value, FileExtension fileExtension) {
        this.value = value;
        this.fileExtension = fileExtension;
    }

    public static MediaType from(String mediaTypeValue) {
        return valueMappings.computeIfAbsent(
                mediaTypeValue,
                key -> {
                    throw new NoSuchElementException(String.format("존재하지 않는 값의 미디어 타입입니다.(%s)", mediaTypeValue));
                }
        );
    }

    public static MediaType from(FileExtension fileExtension) {
        return fileExtensionMappings.computeIfAbsent(
                fileExtension,
                key -> {
                    throw new NoSuchElementException(String.format("존재하지 않는 확장자의 미디어 타입입니다.(%s)", fileExtension));
                }
        );
    }

    public String getValue() {
        return value;
    }
}
