package nextstep.jwp.web;

import nextstep.jwp.web.exception.UnsupportedContentType;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;

public enum ContentType {
    HTML("text/html", "html", "charset=" + StandardCharsets.UTF_8),
    CSS("text/css", "css", "charset=" + StandardCharsets.UTF_8),
    JS("text/javascript", "js", "charset=" + StandardCharsets.UTF_8),
    SVG("image/svg+xml", "svg", null);

    private final String httpNotation;
    private final Pattern fileExtensionPattern;
    private final String encodingType;

    ContentType(String httpNotation, String fileExtension, String encodingType) {
        this.httpNotation = httpNotation;
        this.fileExtensionPattern = Pattern.compile("." + fileExtension);
        this.encodingType = encodingType;
    }

    public static String toHttpNotationFromFileExtension(String fileName) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.fileExtensionPattern.matcher(fileName).find())
                .map(ContentType::toHttpNotation)
                .findFirst()
                .orElseThrow(UnsupportedContentType::new);
    }

    private String toHttpNotation() {
        return this.httpNotation + (this.encodingType != null ? ";" + this.encodingType.toLowerCase() : "");
    }
}
