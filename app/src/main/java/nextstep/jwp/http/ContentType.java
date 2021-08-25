package nextstep.jwp.http;

import java.util.Arrays;
import java.util.List;

public enum ContentType {
    TEXT_PLAIN("text/plain", "txt"),
    TEXT_HTML("text/html", "html", "htm"),
    TEXT_CSS("text/css", "css"),
    APPLICATION_JS("application/js", "js");

    private final String value;
    private final List<String> supportExts;

    ContentType(String value, String... supportExts) {
        this.value = value;
        this.supportExts = List.of(supportExts);
    }

    public static String from(String path) {
        String[] splitFileName = path.split("\\.");
        String ext = splitFileName[splitFileName.length-1];

        return Arrays.stream(values())
            .filter(contentType -> contentType.supportExts.contains(ext))
            .map(ContentType::asString)
            .findAny()
            .orElse(TEXT_PLAIN.asString());
    }

    public String asString() {
        return value;
    }
}
