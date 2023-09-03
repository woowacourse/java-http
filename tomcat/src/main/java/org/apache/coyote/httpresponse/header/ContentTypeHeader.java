package org.apache.coyote.httpresponse.header;

import java.nio.file.Path;
import java.util.Arrays;

public enum ContentTypeHeader implements ResponseHeader {
    TEXT_HTML("text/html;charset=utf-8", ".html"),
    TEXT_CSS("text/css;charset=utf-8", ".css"),
    TEXT_JS("text/js;charset=utf-8", ".js");

    private static final String DELIMITER = ": ";

    private final String mimeType;
    private final String fileExtension;

    ContentTypeHeader(final String mimeType, final String fileExtension) {
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
    }

    public static ContentTypeHeader from(final String path) {
        return Arrays.stream(values())
                .filter(contentTypeHeader -> path.endsWith(contentTypeHeader.fileExtension))
                .findFirst()
                .orElse(TEXT_HTML);
    }

    @Override
    public String getKeyAndValue(final ResponseHeaderType headerType) {
        return headerType.getHeaderName() + DELIMITER + mimeType;
    }
}
