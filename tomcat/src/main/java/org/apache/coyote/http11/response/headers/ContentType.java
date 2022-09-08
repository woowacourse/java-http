package org.apache.coyote.http11.response.headers;

import java.util.Arrays;
import org.apache.coyote.http11.response.PostProcessMeta;

public enum ContentType implements ResponseHeader {
    TEXT_HTML("text/html", ".html"),
    TEXT_CSS("text/css", ".css"),
    APPLICATION_JAVASCRIPT("application/javascript", ".js"),
    TEXT_PLAIN("text/plain", "");

    private final String type;
    private final String extension;

    ContentType(String type, String extension) {
        this.type = type;
        this.extension = extension;
    }

    public static ContentType findWithExtension(String path) {
        return Arrays.stream(values())
                .filter(value -> path.endsWith(value.extension))
                .findAny()
                .orElse(TEXT_PLAIN);
    }

    @Override
    public String getAsString() {
        return "Content-Type: " + String.join(";", type, "charset=utf-8");
    }


    @Override
    public ResponseHeader postProcess(PostProcessMeta meta) {
        return this;
    }
}
