package nextstep.jwp.web.http.response;

import static nextstep.jwp.resource.ExtensionExtractor.extract;

import java.util.Arrays;
import nextstep.jwp.resource.FileType;

public enum ContentType {
    HTML("text/html;charset=utf-8", FileType.HTML),
    PLAIN_TEXT("text/plain;charset=utf-8", FileType.PLAIN_TEXT),
    JS("text/js", FileType.JS),
    CSS("text/css", FileType.CSS),
    SVG("image/svg+xml", FileType.SVG),
    JSON("application/json", FileType.NONE),
    ALL("*/*", FileType.NONE),
    NONE("", FileType.NONE);

    private final String mimeType;
    private final FileType type;

    ContentType(String mimeType, FileType type) {
        this.mimeType = mimeType;
        this.type = type;
    }

    public static ContentType findByExtensionName(String name) {
        final FileType fileType = FileType.findByName(name);
        return Arrays.stream(values())
            .filter(contentType -> contentType.type == fileType)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("해당 확장자에 해당하는 ContentType이 없습니다."));
    }

    public static boolean isStaticResource(String url) {
        ContentType type = findByExtensionName(extract(url));
        return type != ALL && type != JSON;
    }

    public String getMimeType() {
        return mimeType;
    }

}
