package nextstep.jwp.constants;

import java.util.Arrays;
import nextstep.jwp.exception.HttpException;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "text/js"),
    SVG("svg", "image/svg+xml");

    private final String fileType;
    private final String contentType;

    ContentType(String fileType, String contentType) {
        this.fileType = fileType;
        this.contentType = contentType;
    }

    public static ContentType findContentType(String fileType) {
        return Arrays.stream(ContentType.values())
                .filter(type -> type.fileType.equalsIgnoreCase(fileType))
                .findFirst()
                .orElseThrow(() -> new HttpException("해당하는 파일 타입이 없어요"));
    }

    public String getContentType() {
        return this.contentType;
    }
}
