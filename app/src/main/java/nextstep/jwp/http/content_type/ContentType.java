package nextstep.jwp.http.content_type;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public enum ContentType {
    TEXT_PLAIN("text/plain", "txt"),
    TEXT_HTML("text/html", "html", "htm"),
    TEXT_HTML_CHARSET_UTF_8("text/html;charset=utf-8", "html", "htm"),
    TEXT_CSS("text/css", "css"),
    APPLICATION_JS("application/js", "js");

    private final String value;
    private final List<String> supportExts;

    ContentType(String value, String... supportExts) {
        this.value = value;
        this.supportExts = List.of(supportExts);
    }

    public static String from(File file) {
        String[] splitFileName = file.getName().split("\\.");
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
